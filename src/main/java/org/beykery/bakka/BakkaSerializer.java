/**
 * 自定义序列化方法
 */
package org.beykery.bakka;

import akka.serialization.JSerializer;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author beykery
 */
public class BakkaSerializer extends JSerializer
{

  private static final Logger LOG = LoggerFactory.getLogger(BakkaSerializer.class);

  @Override
  public Object fromBinaryJava(byte[] content, Class<?> claz)
  {
    try {
      Schema schema = RuntimeSchema.getSchema(claz);
      Object product = claz.newInstance();
      ProtostuffIOUtil.mergeFrom(content, product, schema);
      return product;
    } catch (Exception e) {
      LOG.error("无法反序列化" + claz);
    }
    return null;
  }

  @Override
  public int identifier()
  {
    return 456678923;
  }

  @Override
  public byte[] toBinary(Object o)
  {
    Schema schema = RuntimeSchema.getSchema(o.getClass());
    LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    byte[] protostuff = ProtostuffIOUtil.toByteArray(o, schema, buffer);
    return protostuff;
  }

  @Override
  public boolean includeManifest()
  {
    return true;
  }

}
