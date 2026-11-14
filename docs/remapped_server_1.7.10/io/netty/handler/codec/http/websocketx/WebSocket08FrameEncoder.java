package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.util.List;

public class WebSocket08FrameEncoder extends MessageToMessageEncoder<WebSocketFrame> implements WebSocketFrameEncoder {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameEncoder.class);
    private static final byte OPCODE_CONT = 0;
    private static final byte OPCODE_TEXT = 1;
    private static final byte OPCODE_BINARY = 2;
    private static final byte OPCODE_CLOSE = 8;
    private static final byte OPCODE_PING = 9;
    private static final byte OPCODE_PONG = 10;
    private final boolean maskPayload;

    public WebSocket08FrameEncoder(boolean flag) {
        this.maskPayload = flag;
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, WebSocketFrame websocketframe, List<Object> list) throws Exception {
        ByteBuf bytebuf = websocketframe.content();

        if (bytebuf == null) {
            bytebuf = Unpooled.EMPTY_BUFFER;
        }

        byte b0;

        if (websocketframe instanceof TextWebSocketFrame) {
            b0 = 1;
        } else if (websocketframe instanceof PingWebSocketFrame) {
            b0 = 9;
        } else if (websocketframe instanceof PongWebSocketFrame) {
            b0 = 10;
        } else if (websocketframe instanceof CloseWebSocketFrame) {
            b0 = 8;
        } else if (websocketframe instanceof BinaryWebSocketFrame) {
            b0 = 2;
        } else {
            if (!(websocketframe instanceof ContinuationWebSocketFrame)) {
                throw new UnsupportedOperationException("Cannot encode frame of type: " + websocketframe.getClass().getName());
            }

            b0 = 0;
        }

        int i = bytebuf.readableBytes();

        if (WebSocket08FrameEncoder.logger.isDebugEnabled()) {
            WebSocket08FrameEncoder.logger.debug("Encoding WebSocket Frame opCode=" + b0 + " length=" + i);
        }

        int j = 0;

        if (websocketframe.isFinalFragment()) {
            j |= 128;
        }

        j |= websocketframe.rsv() % 8 << 4;
        j |= b0 % 128;
        if (b0 == 9 && i > 125) {
            throw new TooLongFrameException("invalid payload for PING (payload length must be <= 125, was " + i);
        } else {
            boolean flag = true;
            ByteBuf bytebuf1 = null;

            try {
                int k = this.maskPayload ? 4 : 0;
                int l;
                int i1;

                if (i <= 125) {
                    l = 2 + k;
                    if (this.maskPayload) {
                        l += i;
                    }

                    bytebuf1 = channelhandlercontext.alloc().buffer(l);
                    bytebuf1.writeByte(j);
                    i1 = (byte) (this.maskPayload ? 128 | (byte) i : (byte) i);
                    bytebuf1.writeByte(i1);
                } else if (i <= 65535) {
                    l = 4 + k;
                    if (this.maskPayload) {
                        l += i;
                    }

                    bytebuf1 = channelhandlercontext.alloc().buffer(l);
                    bytebuf1.writeByte(j);
                    bytebuf1.writeByte(this.maskPayload ? 254 : 126);
                    bytebuf1.writeByte(i >>> 8 & 255);
                    bytebuf1.writeByte(i & 255);
                } else {
                    l = 10 + k;
                    if (this.maskPayload) {
                        l += i;
                    }

                    bytebuf1 = channelhandlercontext.alloc().buffer(l);
                    bytebuf1.writeByte(j);
                    bytebuf1.writeByte(this.maskPayload ? 255 : 127);
                    bytebuf1.writeLong((long) i);
                }

                if (this.maskPayload) {
                    l = (int) (Math.random() * 2.147483647E9D);
                    byte[] abyte = ByteBuffer.allocate(4).putInt(l).array();

                    bytebuf1.writeBytes(abyte);
                    i1 = 0;

                    for (int j1 = bytebuf.readerIndex(); j1 < bytebuf.writerIndex(); ++j1) {
                        byte b1 = bytebuf.getByte(j1);

                        bytebuf1.writeByte(b1 ^ abyte[i1++ % 4]);
                    }

                    list.add(bytebuf1);
                } else {
                    list.add(bytebuf1);
                    list.add(bytebuf.retain());
                }

                flag = false;
            } finally {
                if (flag && bytebuf1 != null) {
                    bytebuf1.release();
                }

            }

        }
    }
}
