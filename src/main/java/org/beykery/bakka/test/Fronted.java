/**
 * 测试用的actor
 */
package org.beykery.bakka.test;

import org.beykery.bakka.Bakka;
import org.beykery.bakka.BakkaRequest;
import org.beykery.bakka.BaseActor;

/**
 *
 * @author beykery
 */
@Bakka(service = "Fronted",slaves = {"Backend"})
public class Fronted extends BaseActor
{
  @BakkaRequest
  public void hi(HI hi)
  {
    System.out.println(hi+" fronted, ......");
    this.services.get("Backend").get(0).tell(hi, self());
  }

}
