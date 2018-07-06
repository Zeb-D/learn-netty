package com.yd.netty.test.hello;

import com.yd.netty.test.codec.AbsIntegerEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
 
import org.junit.Assert;
import org.junit.Test;
 
public class AbsIntegerEncoderTest {
 
	@Test
    public void testEncoded() {
		//创建一个能容纳10个int的ByteBuf
		ByteBuf buf = Unpooled.buffer();
		for (int i = 1; i < 10; i++) {
			buf.writeInt(i * -1);
		}
		//创建EmbeddedChannel对象
		EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
		//将buf数据写入出站EmbeddedChannel
		Assert.assertTrue(channel.writeOutbound(buf));

		//标示EmbeddedChannel完成
		Assert.assertTrue(channel.finish());

		//读取出站数据
		for (int i = 1; i < 10; i++) {
			Assert.assertEquals(i, (int)channel.readOutbound());
		}
		Assert.assertNull(channel.readOutbound());
	}
 
}