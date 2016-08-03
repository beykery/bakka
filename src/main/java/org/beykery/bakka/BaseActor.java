/**
 * 处理一些集群事件
 */
package org.beykery.bakka;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.remote.RemoteActorRefProvider;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author beykery
 */
public abstract class BaseActor extends UntypedActor
{

  protected Cluster cluster = Cluster.get(getContext().system());
  protected final Map<String, List<ActorRef>> services = new HashMap<>();
  protected String service;
  private List<String> slaves;
  protected LoggingAdapter log = Logging.getLogger(getContext().system(), this);
  private final Map<Class<? extends Serializable>, Method> ps = new HashMap<>();//参数类型

  /**
   * 所需的服务
   *
   */
  public BaseActor()
  {
    Bakka an = this.getClass().getAnnotation(Bakka.class);
    if (an != null) {
      this.service = an.service();
      this.slaves = Arrays.asList(an.slaves());
      Method[] ms = this.getClass().getMethods();
      for (Method m : ms) {
        BakkaRequest rm = m.getAnnotation(BakkaRequest.class);
        if (rm != null) {
          Class<?>[] pts = m.getParameterTypes();
          if (pts != null && pts.length == 1 && Serializable.class.isAssignableFrom(pts[0])) {
            Class<? extends Serializable> c = pts[0].asSubclass(Serializable.class);
            ps.put(c, m);
            continue;
          }
          throw new RuntimeException(m + " 方法签名异常");
        }
      }
    }
  }

  @Override
  public void preStart() throws Exception
  {
    cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), ClusterEvent.MemberUp.class, ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
  }

  @Override
  public void postStop() throws Exception
  {
    cluster.unsubscribe(getSelf());
  }

  @Override
  public void onReceive(Object message) throws Throwable
  {
    if (message instanceof ClusterEvent.MemberUp) {
      ClusterEvent.MemberUp event = (ClusterEvent.MemberUp) message;
      this.up(event.member());
    } else if (message instanceof ClusterEvent.CurrentClusterState) {
      ClusterEvent.CurrentClusterState state = (ClusterEvent.CurrentClusterState) message;
      Iterable<Member> members = state.getMembers();
      for (Member o : members) {
        if (o.status().equals(MemberStatus.up())) {
          this.up(o);
        }
      }
    } else if (message instanceof ClusterEvent.UnreachableMember) {
      ClusterEvent.UnreachableMember mUnreachable = (ClusterEvent.UnreachableMember) message;
      log.info("有个节点不可达: {}", mUnreachable.member());
    } else if (message instanceof ClusterEvent.MemberRemoved) {
      ClusterEvent.MemberRemoved mRemoved = (ClusterEvent.MemberRemoved) message;
      log.info("有节点被删掉: {}", mRemoved.member());
    } else if (message instanceof ClusterEvent.MemberEvent) {
      log.info("节点事件: {}", message);
    } else if (message instanceof Registration) {
      Registration r = (Registration) message;
      if (match(this.slaves, r.getService())) {
        getContext().watch(getSender());
        this.addService(getSender(), r.getService());
        log.info("register success! sender =" + getSender());
        log.info("register success! self =" + this.self());
      } else {
          log.info("register failed! r.service = " + r.getService());
          log.info("register failed! this.service=" + this.service);
      }
    } else if (message instanceof Terminated) {
      Terminated terminated = (Terminated) message;
      this.removeService(terminated.actor());
    } else {
      this.onMessage(message);
    }
  }

  /**
   * 向新加入节点注册自己
   *
   * @param member
   */
  private void up(Member member)
  {
    ActorSelection as = getContext().actorSelection(member.address() + "/user/*");
    if (as != null) {
      as.tell(new Registration(service), self());
    }
  }

  /**
   * 添加一个service
   *
   * @param sender
   */
  private void addService(ActorRef ref, String name)
  {
    List<ActorRef> list = this.services.get(name);
    if (list == null) {
      list = new ArrayList<>();
      this.services.put(name, list);
    }
    if (!list.contains(ref)) {
      list.add(ref);
    }
  }

  /**
   * 删掉一个slave
   *
   * @param ref
   */
  private void removeService(ActorRef ref)
  {
    for (List<ActorRef> item : this.services.values()) {
      if (item.remove(ref)) {
        break;
      }
    }
  }

  /**
   * 处理业务消息
   *
   * @param message
   */
  private void onMessage(Object message)
  {
    Method m = this.ps.get(message.getClass());
    if (m != null) {
      try {
        Object r = m.invoke(this, message);
        if (r != null && Serializable.class.isAssignableFrom(r.getClass())) {
          ActorRef ar = sender();
          if (ar != null && !(ar instanceof RemoteActorRefProvider.RemoteDeadLetterActorRef)) {
            ar.tell(r, self());
          }
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        log.error("调用失败" + ex);
      }
    }
  }

  /**
   * 是否匹配
   *
   * @param slaves
   * @param service
   * @return
   */
  private boolean match(List<String> slaves, String service)
  {
    for (String item : slaves) {
      if (service.matches(item)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 服务
   *
   * @param name
   * @return
   */
  public List<ActorRef> getServices(String name)
  {
    return this.services.get(name);
  }
  
  
}
