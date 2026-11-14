package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

@ChannelHandler.Sharable
public class LengthFieldPrepender extends MessageToByteEncoder<ByteBuf> {

    private final int lengthFieldLength;
    private final boolean lengthIncludesLengthFieldLength;
    private final int lengthAdjustment;

    public LengthFieldPrepender(int i) {
        this(i, false);
    }

    public LengthFieldPrepender(int i, boolean flag) {
        this(i, 0, flag);
    }

    public LengthFieldPrepender(int i, int j) {
        this(i, j, false);
    }

    public LengthFieldPrepender(int i, int j, boolean flag) {
        if (i != 1 && i != 2 && i != 3 && i != 4 && i != 8) {
            throw new IllegalArgumentException("lengthFieldLength must be either 1, 2, 3, 4, or 8: " + i);
        } else {
            this.lengthFieldLength = i;
            this.lengthIncludesLengthFieldLength = flag;
            this.lengthAdjustment = j;
        }
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, ByteBuf bytebuf1) throws Exception {
        int i = bytebuf.readableBytes() + this.lengthAdjustment;

        if (this.lengthIncludesLengthFieldLength) {
            i += this.lengthFieldLength;
        }

        if (i < 0) {
            throw new IllegalArgumentException("Adjusted frame length (" + i + ") is less than zero");
        } else {
            switch (this.lengthFieldLength) {
                case 1:
                    if (i >= 256) {
                        throw new IllegalArgumentException("length does not fit into a byte: " + i);
                    }

                    bytebuf1.writeByte((byte) i);
                    break;
                case 2:
                    if (i >= 65536) {
                        throw new IllegalArgumentException("length does not fit into a short integer: " + i);
                    }

                    bytebuf1.writeShort((short) i);
                    break;
                case 3:
                    if (i >= 16777216) {
                        throw new IllegalArgumentException("length does not fit into a medium integer: " + i);
                    }

                    bytebuf1.writeMedium(i);
                    break;
                case 4:
                    bytebuf1.writeInt(i);
                    break;
                case 5:
                case 6:
                case 7:
                default:
                    throw new Error("should not reach here");
                case 8:
                    bytebuf1.writeLong((long) i);
            }

            bytebuf1.writeBytes(bytebuf, bytebuf.readerIndex(), bytebuf.readableBytes());
        }
    }
}
