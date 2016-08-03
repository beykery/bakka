/**
 * 管理
 */
package org.beykery.bakka.test;

import akka.actor.ActorRef;
import java.util.List;
import java.util.Map;
import org.beykery.bakka.Bakka;
import org.beykery.bakka.BakkaRequest;
import org.beykery.bakka.BaseActor;
import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author beykery
 */
@Bakka(service = "Admin",slaves = "\\w*")
public class Admin extends BaseActor
{
  @BakkaRequest
  public HI hi(HI hi)
  {
    System.out.println(hi+" Admin hi, ......");
    return hi;
  }
  @BakkaRequest
  public Tree tree(Tree root)
  {
    for(Map.Entry<String, List<ActorRef>> en : this.services.entrySet()) {
        Tree child = new Tree(en.getKey());
        root.add(child);
        for(ActorRef ar : en.getValue()) {
            child.add(new Tree(ar.path().name()));
        }
    }
    
    System.out.print("tree="+JSONObject.toJSON(root));
    return root;
  }
}
