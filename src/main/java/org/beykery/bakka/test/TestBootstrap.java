/**
 * 后端启动
 */
package org.beykery.bakka.test;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.PoisonPill;
import akka.util.Timeout;
import org.beykery.bakka.Bootstrap;
import scala.concurrent.duration.Duration;

/**
 *
 * @author beykery
 */
public class TestBootstrap
{

  public static void main(String[] args) throws InterruptedException, Exception
  {
    Bootstrap bs = Bootstrap.newInstance("bakka");
    bs.start();
    Thread.sleep(6000);
    ActorSelection as = bs.getActorSystem().actorSelection("/user/Backend");
    as.tell(new HI("hi"),ActorRef.noSender());
    //System.out.println("Backend".matches("\\w*"));
  }
}
