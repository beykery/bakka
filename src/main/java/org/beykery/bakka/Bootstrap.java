/**
 * bakka的启动类
 */
package org.beykery.bakka;

import org.beykery.bakka.util.ClassUtil;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

/**
 *
 * @author beykery
 */
public class Bootstrap
{

  private Logger logger = LoggerFactory.getLogger(Bootstrap.class);
  private Config config;
  private ActorSystem actorSystem;
  private boolean running;
  private final Map<Class<? extends BaseActor>, ActorRef> localActors;
  private final String confFile;
  private Inbox inbox;

  /**
   * 構造
   */
  private Bootstrap(String conf)
  {
    confFile = conf;
    localActors = new HashMap<>();
  }

  /**
   * 启动
   *
   * @param port
   */
  public void start(int port)
  {
    if (!running)
    {
      running = true;
      config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
              withFallback(ConfigFactory.load(this.confFile));
      this.start(config);
    }
  }

  /**
   * 启动
   *
   */
  public void start()
  {
    if (!running)
    {
      running = true;
      config = ConfigFactory.load(this.confFile);
      this.start(config);
    }
  }

  /**
   * 启动
   */
  private void start(Config config)
  {
    String systemName = config.getString("bakka.system.name");
    String path = config.getString("bakka.system.classpath");
    List<String> services = config.getStringList("akka.cluster.roles");
    this.actorSystem = ActorSystem.create(systemName, config);
    this.inbox = Inbox.create(actorSystem);
    Set<Class<?>> classes = ClassUtil.scan(path);
    for (Class<?> c : classes)
    {
      if (c.isAnnotationPresent(Bakka.class))
      {
        Bakka an = c.getAnnotation(Bakka.class);
        if (services.contains(an.service()))
        {
          Props prop = Props.create(c).withDispatcher("dispatcher");
          ActorRef ar = actorSystem.actorOf(prop, an.service());
          localActors.put((Class<? extends BaseActor>) c, ar);
        }
      }
    }
  }

  /**
   * actorSystem
   *
   * @return
   */
  public ActorSystem getActorSystem()
  {
    return actorSystem;
  }

  /**
   * 实例
   *
   * @param conf
   * @return
   */
  public static Bootstrap newInstance(String conf)
  {
    return new Bootstrap(conf);
  }

  /**
   * 本地的actorRef
   *
   * @param c
   * @return
   */
  public ActorRef getLocalActorRef(Class<? extends BaseActor> c)
  {
    return this.localActors.get(c);
  }
  
  /**
   * 本地actorRef发送消息
   * @param c
   * @param obj
   * @return 
   */
  public Serializable locatActorSend(Class<? extends BaseActor> c, Serializable obj) {
      ActorRef ar = this.getLocalActorRef(c);
      if(ar != null) {
          inbox.send(ar, obj);
          try {
              return (Serializable) inbox.receive(Duration.create(5, TimeUnit.MINUTES));
          } catch (TimeoutException ex) {
              logger.error("directSend error!"+obj);
          }
      }
      return null;
  }
  
  
}
