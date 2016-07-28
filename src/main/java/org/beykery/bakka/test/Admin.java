/**
 * 管理
 */
package org.beykery.bakka.test;

import org.beykery.bakka.Bakka;
import org.beykery.bakka.BaseActor;

/**
 *
 * @author beykery
 */
@Bakka(service = "Admin",slaves = "\\w*")
public class Admin extends BaseActor
{

  @Override
  protected void onMessage(Object message)
  {
   
  }
  
}
