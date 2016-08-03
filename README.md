#bakka

一种使用akka的方式，适用于分布式系统的构建

坐标

```xml
  <dependency>
      <groupId>org.beykery</groupId>
      <artifactId>bakka</artifactId>
      <version>1.0.6</version>
  </dependency>
```

## 问题

使用akka构建分布式应用，需要做一些配置上的修改，书写各种actor，确定路由并向指定的actor发送消息
，期间要监听各种集群事件和actor的生命周期等。烦。

## 解决方案

bakka的思路是帮助开发者在一开始就尽力化简这些问题，只需要根据业务逻辑在指定actor里面书写处理业务
消息即可，当需要调用别的服务时，在已经找到的actor集合里面按照自己的路由方法发送消息。

## 使用

首先是要把bakka.conf放到resource下，bakka.system.name属性修改成你想要的名字，bakka.system.classpath修改成
你的应用程序里的actor包名，系统启动的时候，这个路径下的所有actor都将会被递归扫描并启动。roles也要修改为你
想启动的actor集合，比如有个actor叫Backend，要启动它，就把它填写到roles里面即可

```java
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
```

上面这个actor（必须继承BaseActor）,在@Bakka里面指定自己的服务名为Backend，如果bakka.conf里面roles集合里面有它，
则系统启动的时候，就会被自动启动；当它收到一个类型为HI的消息的时候，hi方法（带有@BakkaRequest注解）将会被调用
，hi方法的返回（HI），将会自动被送往当前的sender（如果有）。

bakka使用protostuff来序列化消息。所有的消息都应该实现org.beykery.bakka.Serializable接口，否则将不会被protostuff
序列化。

处理消息的时候（比如hi方法里面），如果要传递一个消息给另一个actor则访问它的services属性获取一个actor并发送即可：

```java
@Bakka(service = "Fronted",slaves = {"Backend"})
public class Fronted extends BaseActor
{
  @BakkaRequest
  public void hi(HI hi)
  {
    System.out.println(hi+"......");
    this.services.get("Backend").get(0).tell(hi, self());
  }

}
```

Fronted类的注解@Bakka声明了slaves（所依赖的其他actor），所以它就可以在services里面查找这些服务（Backend），这些服务
可能是本地的也可能是系统其他节点提供的actor，剩下的就是按照逻辑要求选择其一或更多actor并tell消息

当系统一些节点挂掉的时候，bakka会处理这些事情并把失效的actor从services里面清除掉；当一些节点up的时候，bakka会处理相关
actor(服务)的注册和监听；确保services会被正确更改。

## 结语



enjoy it.


