/**
 * bakka的启动类
 */
package org.beykery.bakka;

import org.beykery.bakka.util.ClassUtil;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author beykery
 */
public class Bootstrap
{

  private Config config;
  private ActorSystem actorSystem;
  private boolean running;
  private final Map<Class<? extends BaseActor>, ActorRef> localActors;
  private final String confFile;

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
}
