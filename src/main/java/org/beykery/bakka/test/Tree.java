/**
 * 打招呼
 */
package org.beykery.bakka.test;

import java.util.ArrayList;
import java.util.List;
import org.beykery.bakka.Serializable;

/**
 *
 * @author beykery
 */
public class Tree implements Serializable
{

private List<Tree> children=new ArrayList<>();
    private String name;
  

  public Tree(String name)
  {
   this.name=name;
  }

  public Tree()
  {
  }

  @Override
  public String toString()
  {
    return this.name;
  }

  public void add(Tree child) {
        this.children.add(child);
  }

  

}
