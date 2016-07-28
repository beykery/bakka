/**
 * 后端启动
 */
package org.beykery.bakka.test;

import org.beykery.bakka.test.HI;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.PoisonPill;
import org.beykery.bakka.Bootstrap;

/**
 *
 * @author beykery
 */
public class TestBootstrap
{

  public static void main(String[] args) throws InterruptedException
  {
    Bootstrap bs = Bootstrap.getInstance();
    bs.start();
    Thread.sleep(20000);
    ActorSelection as = bs.getActorSystem().actorSelection("/user/Backend");
    as.tell(new HI("hi"), ActorRef.noSender());
    //System.out.println("Backend".matches("\\w*"));
  }
}
