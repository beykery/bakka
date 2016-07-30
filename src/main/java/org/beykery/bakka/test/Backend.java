/**
 * 后端服务
 */
package org.beykery.bakka.test;

import org.beykery.bakka.Bakka;
import org.beykery.bakka.BakkaRequest;
import org.beykery.bakka.BaseActor;

/**
 *
 * @author beykery
 */
@Bakka(service = "Backend")
public class Backend extends BaseActor
{

  @BakkaRequest
  public HI hi(HI hi)
  {
    System.out.println(hi+"......");
    return hi;
  }
}
