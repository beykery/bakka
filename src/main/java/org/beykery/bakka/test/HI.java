/**
 * 打招呼
 */
package org.beykery.bakka.test;

import org.beykery.bakka.Serializable;

/**
 *
 * @author beykery
 */
public class HI implements Serializable
{

  private String content;

  public HI(String content)
  {
    this.content = content;
  }

  public HI()
  {
  }

  @Override
  public String toString()
  {
    return this.content;
  }

}
