/**
 * 测试用的actor
 */
package org.beykery.bakka.test;

import org.beykery.bakka.Bakka;
import org.beykery.bakka.BaseActor;

/**
 *
 * @author beykery
 */
@Bakka(service = "Fronted",slaves = {"Backend"})
public class Fronted extends BaseActor
{


}
