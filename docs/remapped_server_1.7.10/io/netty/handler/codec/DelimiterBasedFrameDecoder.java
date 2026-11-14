package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;

public class DelimiterBasedFrameDecoder extends ByteToMessageDecoder {

    private final ByteBuf[] delimiters;
    private final int maxFrameLength;
    private final boolean stripDelimiter;
    private final boolean failFast;
    private boolean discardingTooLongFrame;
    private int tooLongFrameLength;
    private final LineBasedFrameDecoder lineBasedDecoder;

    public DelimiterBasedFrameDecoder(int i, ByteBuf bytebuf) {
        this(i, true, bytebuf);
    }

    public DelimiterBasedFrameDecoder(int i, boolean flag, ByteBuf bytebuf) {
        this(i, flag, true, bytebuf);
    }

    public DelimiterBasedFrameDecoder(int i, boolean flag, boolean flag1, ByteBuf bytebuf) {
        this(i, flag, flag1, bytebuf.slice(bytebuf.readerIndex(), bytebuf.readableBytes()));
    }

    public DelimiterBasedFrameDecoder(int i, ByteBuf... abytebuf) {
        this(i, true, abytebuf);
    }

    public DelimiterBasedFrameDecoder(int i, boolean flag, ByteBuf... abytebuf) {
        this(i, flag, true, abytebuf);
    }

    public DelimiterBasedFrameDecoder(int i, boolean flag, boolean flag1, ByteBuf... abytebuf) {
        validateMaxFrameLength(i);
        if (abytebuf == null) {
            throw new NullPointerException("delimiters");
        } else if (abytebuf.length == 0) {
            throw new IllegalArgumentException("empty delimiters");
        } else {
            if (isLineBased(abytebuf) && !this.isSubclass()) {
                this.lineBasedDecoder = new LineBasedFrameDecoder(i, flag, flag1);
                this.delimiters = null;
            } else {
                this.delimiters = new ByteBuf[abytebuf.length];

                for (int j = 0; j < abytebuf.length; ++j) {
                    ByteBuf bytebuf = abytebuf[j];

                    validateDelimiter(bytebuf);
                    this.delimiters[j] = bytebuf.slice(bytebuf.readerIndex(), bytebuf.readableBytes());
                }

                this.lineBasedDecoder = null;
            }

            this.maxFrameLength = i;
            this.stripDelimiter = flag;
            this.failFast = flag1;
        }
    }

    private static boolean isLineBased(ByteBuf[] abytebuf) {
        if (abytebuf.length != 2) {
            return false;
        } else {
            ByteBuf bytebuf = abytebuf[0];
            ByteBuf bytebuf1 = abytebuf[1];

            if (bytebuf.capacity() < bytebuf1.capacity()) {
                bytebuf = abytebuf[1];
                bytebuf1 = abytebuf[0];
            }

            return bytebuf.capacity() == 2 && bytebuf1.capacity() == 1 && bytebuf.getByte(0) == 13 && bytebuf.getByte(1) == 10 && bytebuf1.getByte(0) == 10;
        }
    }

    private boolean isSubclass() {
        return this.getClass() != DelimiterBasedFrameDecoder.class;
    }

    @Override
    protected final void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        Object object = this.decode(channelhandlercontext, bytebuf);

        if (object != null) {
            list.add(object);
        }

    }

    protected Object decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf) throws Exception {
        if (this.lineBasedDecoder != null) {
            return this.lineBasedDecoder.decode(channelhandlercontext, bytebuf);
        } else {
            int i = Integer.MAX_VALUE;
            ByteBuf bytebuf1 = null;
            ByteBuf[] abytebuf = this.delimiters;
            int j = abytebuf.length;

            int k;

            for (k = 0; k < j; ++k) {
                ByteBuf bytebuf2 = abytebuf[k];
                int l = indexOf(bytebuf, bytebuf2);

                if (l >= 0 && l < i) {
                    i = l;
                    bytebuf1 = bytebuf2;
                }
            }

            if (bytebuf1 != null) {
                int i1 = bytebuf1.capacity();

                if (this.discardingTooLongFrame) {
                    this.discardingTooLongFrame = false;
                    bytebuf.skipBytes(i + i1);
                    k = this.tooLongFrameLength;
                    this.tooLongFrameLength = 0;
                    if (!this.failFast) {
                        this.fail(channelhandlercontext, (long) k);
                    }

                    return null;
                } else if (i > this.maxFrameLength) {
                    bytebuf.skipBytes(i + i1);
                    this.fail(channelhandlercontext, (long) i);
                    return null;
                } else {
                    ByteBuf bytebuf3;

                    if (this.stripDelimiter) {
                        bytebuf3 = bytebuf.readBytes(i);
                        bytebuf.skipBytes(i1);
                    } else {
                        bytebuf3 = bytebuf.readBytes(i + i1);
                    }

                    return bytebuf3;
                }
            } else {
                if (!this.discardingTooLongFrame) {
                    if (bytebuf.readableBytes() > this.maxFrameLength) {
                        this.tooLongFrameLength = bytebuf.readableBytes();
                        bytebuf.skipBytes(bytebuf.readableBytes());
                        this.discardingTooLongFrame = true;
                        if (this.failFast) {
                            this.fail(channelhandlercontext, (long) this.tooLongFrameLength);
                        }
                    }
                } else {
                    this.tooLongFrameLength += bytebuf.readableBytes();
                    bytebuf.skipBytes(bytebuf.readableBytes());
                }

                return null;
            }
        }
    }

    private void fail(ChannelHandlerContext channelhandlercontext, long i) {
        if (i > 0L) {
            channelhandlercontext.fireExceptionCaught(new TooLongFrameException("frame length exceeds " + this.maxFrameLength + ": " + i + " - discarded"));
        } else {
            channelhandlercontext.fireExceptionCaught(new TooLongFrameException("frame length exceeds " + this.maxFrameLength + " - discarding"));
        }

    }

    private static int indexOf(ByteBuf bytebuf, ByteBuf bytebuf1) {
        for (int i = bytebuf.readerIndex(); i < bytebuf.writerIndex(); ++i) {
            int j = i;

            int k;

            for (k = 0; k < bytebuf1.capacity() && bytebuf.getByte(j) == bytebuf1.getByte(k); ++k) {
                ++j;
                if (j == bytebuf.writerIndex() && k != bytebuf1.capacity() - 1) {
                    return -1;
                }
            }

            if (k == bytebuf1.capacity()) {
                return i - bytebuf.readerIndex();
            }
        }

        return -1;
    }

    private static void validateDelimiter(ByteBuf bytebuf) {
        if (bytebuf == null) {
            throw new NullPointerException("delimiter");
        } else if (!bytebuf.isReadable()) {
            throw new IllegalArgumentException("empty delimiter");
        }
    }

    private static void validateMaxFrameLength(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("maxFrameLength must be a positive integer: " + i);
        }
    }
}
