package org.beykery.bakka;

/**
 * 子系统注册的消息
 */
public class Registration implements Serializable
{

  private String service;

  public Registration()
  {
  }

  public Registration(String service)
  {
    this.service = service;
  }

  public String getService()
  {
    return service;
  }

  public void setService(String name)
  {
    this.service = name;
  }

}
