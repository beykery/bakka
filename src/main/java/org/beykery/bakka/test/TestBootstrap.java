/**
 * 后端启动
 */
package org.beykery.bakka.test;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Inbox;
import akka.actor.PoisonPill;
import akka.util.Timeout;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.beykery.bakka.Bootstrap;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

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
    ActorSelection as = bs.getActorSystem().actorSelection("/user/Admin");
    as.tell(new HI("hi, I'm admin!"),ActorRef.noSender());
    
    
    Bootstrap bs2 = Bootstrap.newInstance("bakka_backend");
    bs2.start();
    Thread.sleep(6000);
    ActorSelection as2 = bs2.getActorSystem().actorSelection("/user/Backend");
    as2.tell(new HI("hi, I'm backend!"),ActorRef.noSender());
    //System.out.println("Backend".matches("\\w*"));

    Bootstrap bs1 = Bootstrap.newInstance("bakka_fronted");
    bs1.start();
    Thread.sleep(6000);
    ActorSelection as1 = bs1.getActorSystem().actorSelection("/user/Fronted");
//    as1.tell(new HI("hi, I'm fronted!"),ActorRef.noSender());
   

    HI hi = (HI) bs.locatActorSend(Admin.class, new HI("i am hi"));
      System.out.println("hi="+hi);
  }
}
