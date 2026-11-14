package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import java.util.List;

public class WebSocket00FrameDecoder extends ReplayingDecoder<Void> implements WebSocketFrameDecoder {

    static final int DEFAULT_MAX_FRAME_SIZE = 16384;
    private final long maxFrameSize;
    private boolean receivedClosingHandshake;

    public WebSocket00FrameDecoder() {
        this(16384);
    }

    public WebSocket00FrameDecoder(int i) {
        this.maxFrameSize = (long) i;
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (this.receivedClosingHandshake) {
            bytebuf.skipBytes(this.actualReadableBytes());
        } else {
            byte b0 = bytebuf.readByte();

            if ((b0 & 128) == 128) {
                list.add(this.decodeBinaryFrame(channelhandlercontext, b0, bytebuf));
            } else {
                list.add(this.decodeTextFrame(channelhandlercontext, bytebuf));
            }

        }
    }

    private WebSocketFrame decodeBinaryFrame(ChannelHandlerContext channelhandlercontext, byte b0, ByteBuf bytebuf) {
        long i = 0L;
        int j = 0;

        byte b1;

        do {
            b1 = bytebuf.readByte();
            i <<= 7;
            i |= (long) (b1 & 127);
            if (i > this.maxFrameSize) {
                throw new TooLongFrameException();
            }

            ++j;
            if (j > 8) {
                throw new TooLongFrameException();
            }
        } while ((b1 & 128) == 128);

        if (b0 == -1 && i == 0L) {
            this.receivedClosingHandshake = true;
            return new CloseWebSocketFrame();
        } else {
            ByteBuf bytebuf1 = channelhandlercontext.alloc().buffer((int) i);

            bytebuf.readBytes(bytebuf1);
            return new BinaryWebSocketFrame(bytebuf1);
        }
    }

    private WebSocketFrame decodeTextFrame(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf) {
        int i = bytebuf.readerIndex();
        int j = this.actualReadableBytes();
        int k = bytebuf.indexOf(i, i + j, (byte) -1);

        if (k == -1) {
            if ((long) j > this.maxFrameSize) {
                throw new TooLongFrameException();
            } else {
                return null;
            }
        } else {
            int l = k - i;

            if ((long) l > this.maxFrameSize) {
                throw new TooLongFrameException();
            } else {
                ByteBuf bytebuf1 = channelhandlercontext.alloc().buffer(l);

                bytebuf.readBytes(bytebuf1);
                bytebuf.skipBytes(1);
                int i1 = bytebuf1.indexOf(bytebuf1.readerIndex(), bytebuf1.writerIndex(), (byte) -1);

                if (i1 >= 0) {
                    throw new IllegalArgumentException("a text frame should not contain 0xFF.");
                } else {
                    return new TextWebSocketFrame(bytebuf1);
                }
            }
        }
    }
}
