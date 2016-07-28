/**
 * 后端服务
 */
package org.beykery.bakka.test;

import org.beykery.bakka.Bakka;
import org.beykery.bakka.BaseActor;

/**
 *
 * @author beykery
 */
@Bakka(service = "Backend")
public class Backend extends BaseActor
{

  @Override
  protected void onMessage(Object message)
  {
    System.out.println(message);
  }

}
