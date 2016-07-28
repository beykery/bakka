/**
 * bakka的注解
 */
package org.beykery.bakka;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value
  = {
    ElementType.TYPE
  })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Bakka
{

  /*名字*/
  public String service();

  /*所需服务*/
  public String[] slaves() default {};
}
