/**
 * 管理
 */
package org.beykery.bakka.test;

import org.beykery.bakka.Bakka;
import org.beykery.bakka.BakkaRequest;
import org.beykery.bakka.BaseActor;

/**
 *
 * @author beykery
 */
@Bakka(service = "Admin",slaves = "[Fronted, Backend]")
public class Admin extends BaseActor
{
  @BakkaRequest
  public HI hi(HI hi)
  {
    System.out.println(hi+" Admin hi, ......");
    return hi;
  }
  @BakkaRequest
  public Tree tree(Tree tree)
  {
    System.out.println(tree+" Admin tree, ......");
    return tree;
  }
}
