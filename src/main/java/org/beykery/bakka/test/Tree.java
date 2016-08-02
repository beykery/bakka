/**
 * 打招呼
 */
package org.beykery.bakka.test;

import org.beykery.bakka.Serializable;

/**
 *
 * @author beykery
 */
public class Tree implements Serializable
{

  private String path;
  private String child;
  

  public Tree(String path)
  {
    this.path = path;
  }

  public Tree()
  {
  }

  @Override
  public String toString()
  {
    return this.path;
  }

    /**
     * @return the child
     */
    public String getChild() {
        return child;
    }

    /**
     * @param child the child to set
     */
    public void setChild(String child) {
        this.child = child;
    }

}
