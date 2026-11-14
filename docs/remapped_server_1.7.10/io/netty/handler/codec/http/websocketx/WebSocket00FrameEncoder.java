package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@ChannelHandler.Sharable
public class WebSocket00FrameEncoder extends MessageToMessageEncoder<WebSocketFrame> implements WebSocketFrameEncoder {

    private static final ByteBuf _0X00 = Unpooled.unreleasableBuffer(Unpooled.directBuffer(1, 1).writeByte(0));
    private static final ByteBuf _0XFF = Unpooled.unreleasableBuffer(Unpooled.directBuffer(1, 1).writeByte(-1));
    private static final ByteBuf _0XFF_0X00 = Unpooled.unreleasableBuffer(Unpooled.directBuffer(2, 2).writeByte(-1).writeByte(0));

    public WebSocket00FrameEncoder() {}

    protected void encode(ChannelHandlerContext channelhandlercontext, WebSocketFrame websocketframe, List<Object> list) throws Exception {
        ByteBuf bytebuf;

        if (websocketframe instanceof TextWebSocketFrame) {
            bytebuf = websocketframe.content();
            list.add(WebSocket00FrameEncoder._0X00.duplicate());
            list.add(bytebuf.retain());
            list.add(WebSocket00FrameEncoder._0XFF.duplicate());
        } else if (websocketframe instanceof CloseWebSocketFrame) {
            list.add(WebSocket00FrameEncoder._0XFF_0X00);
        } else {
            bytebuf = websocketframe.content();
            int i = bytebuf.readableBytes();
            ByteBuf bytebuf1 = channelhandlercontext.alloc().buffer(5);
            boolean flag = true;

            try {
                bytebuf1.writeByte(-128);
                int j = i >>> 28 & 127;
                int k = i >>> 14 & 127;
                int l = i >>> 7 & 127;
                int i1 = i & 127;

                if (j == 0) {
                    if (k == 0) {
                        if (l == 0) {
                            bytebuf1.writeByte(i1);
                        } else {
                            bytebuf1.writeByte(l | 128);
                            bytebuf1.writeByte(i1);
                        }
                    } else {
                        bytebuf1.writeByte(k | 128);
                        bytebuf1.writeByte(l | 128);
                        bytebuf1.writeByte(i1);
                    }
                } else {
                    bytebuf1.writeByte(j | 128);
                    bytebuf1.writeByte(k | 128);
                    bytebuf1.writeByte(l | 128);
                    bytebuf1.writeByte(i1);
                }

                list.add(bytebuf1);
                list.add(bytebuf.retain());
                flag = false;
            } finally {
                if (flag) {
                    bytebuf1.release();
                }

            }
        }

    }
}
