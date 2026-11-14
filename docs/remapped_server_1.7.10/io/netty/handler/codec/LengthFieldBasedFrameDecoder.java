package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.nio.ByteOrder;
import java.util.List;

public class LengthFieldBasedFrameDecoder extends ByteToMessageDecoder {

    private final ByteOrder byteOrder;
    private final int maxFrameLength;
    private final int lengthFieldOffset;
    private final int lengthFieldLength;
    private final int lengthFieldEndOffset;
    private final int lengthAdjustment;
    private final int initialBytesToStrip;
    private final boolean failFast;
    private boolean discardingTooLongFrame;
    private long tooLongFrameLength;
    private long bytesToDiscard;

    public LengthFieldBasedFrameDecoder(int i, int j, int k) {
        this(i, j, k, 0, 0);
    }

    public LengthFieldBasedFrameDecoder(int i, int j, int k, int l, int i1) {
        this(i, j, k, l, i1, true);
    }

    public LengthFieldBasedFrameDecoder(int i, int j, int k, int l, int i1, boolean flag) {
        this(ByteOrder.BIG_ENDIAN, i, j, k, l, i1, flag);
    }

    public LengthFieldBasedFrameDecoder(ByteOrder byteorder, int i, int j, int k, int l, int i1, boolean flag) {
        if (byteorder == null) {
            throw new NullPointerException("byteOrder");
        } else if (i <= 0) {
            throw new IllegalArgumentException("maxFrameLength must be a positive integer: " + i);
        } else if (j < 0) {
            throw new IllegalArgumentException("lengthFieldOffset must be a non-negative integer: " + j);
        } else if (i1 < 0) {
            throw new IllegalArgumentException("initialBytesToStrip must be a non-negative integer: " + i1);
        } else if (k != 1 && k != 2 && k != 3 && k != 4 && k != 8) {
            throw new IllegalArgumentException("lengthFieldLength must be either 1, 2, 3, 4, or 8: " + k);
        } else if (j > i - k) {
            throw new IllegalArgumentException("maxFrameLength (" + i + ") " + "must be equal to or greater than " + "lengthFieldOffset (" + j + ") + " + "lengthFieldLength (" + k + ").");
        } else {
            this.byteOrder = byteorder;
            this.maxFrameLength = i;
            this.lengthFieldOffset = j;
            this.lengthFieldLength = k;
            this.lengthAdjustment = l;
            this.lengthFieldEndOffset = j + k;
            this.initialBytesToStrip = i1;
            this.failFast = flag;
        }
    }

    @Override
    protected final void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        Object object = this.decode(channelhandlercontext, bytebuf);

        if (object != null) {
            list.add(object);
        }

    }

    protected Object decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf) throws Exception {
        if (this.discardingTooLongFrame) {
            long i = this.bytesToDiscard;
            int j = (int) Math.min(i, (long) bytebuf.readableBytes());

            bytebuf.skipBytes(j);
            i -= (long) j;
            this.bytesToDiscard = i;
            this.failIfNecessary(false);
        }

        if (bytebuf.readableBytes() < this.lengthFieldEndOffset) {
            return null;
        } else {
            int k = bytebuf.readerIndex() + this.lengthFieldOffset;
            long l = this.getFrameLength(bytebuf, k);

            if (l < 0L) {
                bytebuf.skipBytes(this.lengthFieldEndOffset);
                throw new CorruptedFrameException("negative pre-adjustment length field: " + l);
            } else {
                l += (long) (this.lengthAdjustment + this.lengthFieldEndOffset);
                if (l < (long) this.lengthFieldEndOffset) {
                    bytebuf.skipBytes(this.lengthFieldEndOffset);
                    throw new CorruptedFrameException("Adjusted frame length (" + l + ") is less " + "than lengthFieldEndOffset: " + this.lengthFieldEndOffset);
                } else if (l > (long) this.maxFrameLength) {
                    long i1 = l - (long) bytebuf.readableBytes();

                    this.tooLongFrameLength = l;
                    if (i1 < 0L) {
                        bytebuf.skipBytes((int) l);
                    } else {
                        this.discardingTooLongFrame = true;
                        this.bytesToDiscard = i1;
                        bytebuf.skipBytes(bytebuf.readableBytes());
                    }

                    this.failIfNecessary(true);
                    return null;
                } else {
                    int j1 = (int) l;

                    if (bytebuf.readableBytes() < j1) {
                        return null;
                    } else if (this.initialBytesToStrip > j1) {
                        bytebuf.skipBytes(j1);
                        throw new CorruptedFrameException("Adjusted frame length (" + l + ") is less " + "than initialBytesToStrip: " + this.initialBytesToStrip);
                    } else {
                        bytebuf.skipBytes(this.initialBytesToStrip);
                        int k1 = bytebuf.readerIndex();
                        int l1 = j1 - this.initialBytesToStrip;
                        ByteBuf bytebuf1 = this.extractFrame(channelhandlercontext, bytebuf, k1, l1);

                        bytebuf.readerIndex(k1 + l1);
                        return bytebuf1;
                    }
                }
            }
        }
    }

    private long getFrameLength(ByteBuf bytebuf, int i) {
        bytebuf = bytebuf.order(this.byteOrder);
        long j;

        switch (this.lengthFieldLength) {
            case 1:
                j = (long) bytebuf.getUnsignedByte(i);
                break;
            case 2:
                j = (long) bytebuf.getUnsignedShort(i);
                break;
            case 3:
                j = (long) bytebuf.getUnsignedMedium(i);
                break;
            case 4:
                j = bytebuf.getUnsignedInt(i);
                break;
            case 5:
            case 6:
            case 7:
            default:
                throw new Error("should not reach here");
            case 8:
                j = bytebuf.getLong(i);
        }

        return j;
    }

    private void failIfNecessary(boolean flag) {
        if (this.bytesToDiscard == 0L) {
            long i = this.tooLongFrameLength;

            this.tooLongFrameLength = 0L;
            this.discardingTooLongFrame = false;
            if (!this.failFast || this.failFast && flag) {
                this.fail(i);
            }
        } else if (this.failFast && flag) {
            this.fail(this.tooLongFrameLength);
        }

    }

    protected ByteBuf extractFrame(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, int i, int j) {
        ByteBuf bytebuf1 = channelhandlercontext.alloc().buffer(j);

        bytebuf1.writeBytes(bytebuf, i, j);
        return bytebuf1;
    }

    private void fail(long i) {
        if (i > 0L) {
            throw new TooLongFrameException("Adjusted frame length exceeds " + this.maxFrameLength + ": " + i + " - discarded");
        } else {
            throw new TooLongFrameException("Adjusted frame length exceeds " + this.maxFrameLength + " - discarding");
        }
    }
}
