package com.yd.netty.test.hello;

import com.yd.netty.test.codec.FixedLengthFrameDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

public class FixedLengthFrameDecoderTest {
 
	@Test
    public void testFramesDecoded() {
		ByteBuf buf = Unpooled.buffer();
		for (int i = 0; i < 9; i++) {
			buf.writeByte(i);
		}
		ByteBuf input = buf.duplicate();
		EmbeddedChannel channel = new EmbeddedChannel(
				new FixedLengthFrameDecoder(3));
		// write bytes
		Assert.assertTrue(channel.writeInbound(input));
		Assert.assertTrue(channel.finish());//将EmbeddedChannel标记为已完成
		// read message
		Assert.assertEquals(buf.readBytes(3), channel.readInbound());
		Assert.assertEquals(buf.readBytes(3), channel.readInbound());
		Assert.assertEquals(buf.readBytes(3), channel.readInbound());
		Assert.assertNull(channel.readInbound());
	}
 
	@Test
	public void testFramesDecoded2() {
		ByteBuf buf = Unpooled.buffer();
		for (int i = 0; i < 9; i++) {
			buf.writeByte(i);
		}
		ByteBuf input = buf.duplicate();
		EmbeddedChannel channel = new EmbeddedChannel(
				new FixedLengthFrameDecoder(3));
		Assert.assertFalse(channel.writeInbound(input.readBytes(2)));
		Assert.assertTrue(channel.writeInbound(input.readBytes(7)));
		Assert.assertTrue(channel.finish());
		Assert.assertEquals(buf.readBytes(3), channel.readInbound());
		Assert.assertEquals(buf.readBytes(3), channel.readInbound());
		Assert.assertEquals(buf.readBytes(3), channel.readInbound());
		Assert.assertNull(channel.readInbound());
	}
 
}