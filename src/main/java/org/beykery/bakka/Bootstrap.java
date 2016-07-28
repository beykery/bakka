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
import java.util.List;
import java.util.Set;

/**
 *
 * @author beykery
 */
public class Bootstrap
{

  private static final Bootstrap instance = new Bootstrap();
  private final Config config;
  private ActorSystem actorSystem;
  private boolean running;

  /**
   * 单例
   */
  private Bootstrap()
  {
    config = ConfigFactory.load("bkka");
  }

  /**
   * 启动
   */
  public void start()
  {
    if (!running)
    {
      running = true;
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
          }
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
   * @return
   */
  public static Bootstrap getInstance()
  {
    return instance;
  }

}
