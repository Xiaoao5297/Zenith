package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;

public class LineBasedFrameDecoder extends ByteToMessageDecoder {

    private final int maxLength;
    private final boolean failFast;
    private final boolean stripDelimiter;
    private boolean discarding;
    private int discardedBytes;

    public LineBasedFrameDecoder(int i) {
        this(i, true, false);
    }

    public LineBasedFrameDecoder(int i, boolean flag, boolean flag1) {
        this.maxLength = i;
        this.failFast = flag1;
        this.stripDelimiter = flag;
    }

    @Override
    protected final void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        Object object = this.decode(channelhandlercontext, bytebuf);

        if (object != null) {
            list.add(object);
        }

    }

    protected Object decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf) throws Exception {
        int i = findEndOfLine(bytebuf);
        int j;
        int k;

        if (!this.discarding) {
            if (i >= 0) {
                j = i - bytebuf.readerIndex();
                int l = bytebuf.getByte(i) == 13 ? 2 : 1;

                if (j > this.maxLength) {
                    bytebuf.readerIndex(i + l);
                    this.fail(channelhandlercontext, j);
                    return null;
                } else {
                    ByteBuf bytebuf1;

                    if (this.stripDelimiter) {
                        bytebuf1 = bytebuf.readBytes(j);
                        bytebuf.skipBytes(l);
                    } else {
                        bytebuf1 = bytebuf.readBytes(j + l);
                    }

                    return bytebuf1;
                }
            } else {
                k = bytebuf.readableBytes();
                if (k > this.maxLength) {
                    this.discardedBytes = k;
                    bytebuf.readerIndex(bytebuf.writerIndex());
                    this.discarding = true;
                    if (this.failFast) {
                        this.fail(channelhandlercontext, "over " + this.discardedBytes);
                    }
                }

                return null;
            }
        } else {
            if (i >= 0) {
                k = this.discardedBytes + i - bytebuf.readerIndex();
                j = bytebuf.getByte(i) == 13 ? 2 : 1;
                bytebuf.readerIndex(i + j);
                this.discardedBytes = 0;
                this.discarding = false;
                if (!this.failFast) {
                    this.fail(channelhandlercontext, k);
                }
            } else {
                this.discardedBytes = bytebuf.readableBytes();
                bytebuf.readerIndex(bytebuf.writerIndex());
            }

            return null;
        }
    }

    private void fail(ChannelHandlerContext channelhandlercontext, int i) {
        this.fail(channelhandlercontext, String.valueOf(i));
    }

    private void fail(ChannelHandlerContext channelhandlercontext, String s) {
        channelhandlercontext.fireExceptionCaught(new TooLongFrameException("frame length (" + s + ") exceeds the allowed maximum (" + this.maxLength + ')'));
    }

    private static int findEndOfLine(ByteBuf bytebuf) {
        int i = bytebuf.writerIndex();

        for (int j = bytebuf.readerIndex(); j < i; ++j) {
            byte b0 = bytebuf.getByte(j);

            if (b0 == 10) {
                return j;
            }

            if (b0 == 13 && j < i - 1 && bytebuf.getByte(j + 1) == 10) {
                return j;
            }
        }

        return -1;
    }
}
