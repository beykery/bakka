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

    public List<Tree> getChildren() {
        return children;
    }

    public void setChildren(List<Tree> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

  public void add(Tree child) {
        this.children.add(child);
  }

  

}
