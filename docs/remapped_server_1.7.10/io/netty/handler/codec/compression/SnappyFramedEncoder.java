package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class SnappyFramedEncoder extends MessageToByteEncoder<ByteBuf> {

    private static final int MIN_COMPRESSIBLE_LENGTH = 18;
    private static final byte[] STREAM_START = new byte[]{Byte.MIN_VALUE, 6, 0, 0, 115, 78, 97, 80, 112, 89};
    private final Snappy snappy = new Snappy();
    private boolean started;

    public SnappyFramedEncoder() {}

    protected void encode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, ByteBuf bytebuf1) throws Exception {
        if (bytebuf.isReadable()) {
            if (!this.started) {
                this.started = true;
                bytebuf1.writeBytes(SnappyFramedEncoder.STREAM_START);
            }

            int i = bytebuf.readableBytes();

            if (i > 18) {
                while (true) {
                    int j = bytebuf1.writerIndex() + 1;
                    ByteBuf bytebuf2;

                    if (i < 18) {
                        bytebuf2 = bytebuf.readSlice(i);
                        writeUnencodedChunk(bytebuf2, bytebuf1, i);
                        break;
                    }

                    bytebuf1.writeInt(0);
                    if (i <= 32767) {
                        bytebuf2 = bytebuf.readSlice(i);
                        calculateAndWriteChecksum(bytebuf2, bytebuf1);
                        this.snappy.encode(bytebuf2, bytebuf1, i);
                        setChunkLength(bytebuf1, j);
                        break;
                    }

                    bytebuf2 = bytebuf.readSlice(32767);
                    calculateAndWriteChecksum(bytebuf2, bytebuf1);
                    this.snappy.encode(bytebuf2, bytebuf1, 32767);
                    setChunkLength(bytebuf1, j);
                    i -= 32767;
                }
            } else {
                writeUnencodedChunk(bytebuf, bytebuf1, i);
            }

        }
    }

    private static void writeUnencodedChunk(ByteBuf bytebuf, ByteBuf bytebuf1, int i) {
        bytebuf1.writeByte(1);
        writeChunkLength(bytebuf1, i + 4);
        calculateAndWriteChecksum(bytebuf, bytebuf1);
        bytebuf1.writeBytes(bytebuf, i);
    }

    private static void setChunkLength(ByteBuf bytebuf, int i) {
        int j = bytebuf.writerIndex() - i - 3;

        if (j >>> 24 != 0) {
            throw new CompressionException("compressed data too large: " + j);
        } else {
            bytebuf.setMedium(i, ByteBufUtil.swapMedium(j));
        }
    }

    private static void writeChunkLength(ByteBuf bytebuf, int i) {
        bytebuf.writeMedium(ByteBufUtil.swapMedium(i));
    }

    private static void calculateAndWriteChecksum(ByteBuf bytebuf, ByteBuf bytebuf1) {
        bytebuf1.writeInt(ByteBufUtil.swapInt(Snappy.calculateChecksum(bytebuf)));
    }
}
