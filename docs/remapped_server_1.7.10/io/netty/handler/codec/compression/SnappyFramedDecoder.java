package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.Arrays;
import java.util.List;

public class SnappyFramedDecoder extends ByteToMessageDecoder {

    private static final byte[] SNAPPY = new byte[]{115, 78, 97, 80, 112, 89};
    private final Snappy snappy;
    private final boolean validateChecksums;
    private boolean started;
    private boolean corrupted;

    public SnappyFramedDecoder() {
        this(false);
    }

    public SnappyFramedDecoder(boolean flag) {
        this.snappy = new Snappy();
        this.validateChecksums = flag;
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (this.corrupted) {
            bytebuf.skipBytes(bytebuf.readableBytes());
        } else {
            try {
                int i = bytebuf.readerIndex();
                int j = bytebuf.writerIndex() - i;

                if (j >= 4) {
                    short short0 = bytebuf.getUnsignedByte(i);
                    SnappyFramedDecoder.ChunkType snappyframeddecoder_chunktype = mapChunkType((byte) short0);
                    int k = ByteBufUtil.swapMedium(bytebuf.getUnsignedMedium(i + 1));
                    int l;

                    switch (snappyframeddecoder_chunktype) {
                        case STREAM_IDENTIFIER:
                            if (k != SnappyFramedDecoder.SNAPPY.length) {
                                throw new DecompressionException("Unexpected length of stream identifier: " + k);
                            }

                            if (j >= 4 + SnappyFramedDecoder.SNAPPY.length) {
                                byte[] abyte = new byte[k];

                                bytebuf.skipBytes(4).readBytes(abyte);
                                if (!Arrays.equals(abyte, SnappyFramedDecoder.SNAPPY)) {
                                    throw new DecompressionException("Unexpected stream identifier contents. Mismatched snappy protocol version?");
                                }

                                this.started = true;
                            }
                            break;
                        case RESERVED_SKIPPABLE:
                            if (!this.started) {
                                throw new DecompressionException("Received RESERVED_SKIPPABLE tag before STREAM_IDENTIFIER");
                            }

                            if (j < 4 + k) {
                                return;
                            }

                            bytebuf.skipBytes(4 + k);
                            break;
                        case RESERVED_UNSKIPPABLE:
                            throw new DecompressionException("Found reserved unskippable chunk type: 0x" + Integer.toHexString(short0));
                        case UNCOMPRESSED_DATA:
                            if (!this.started) {
                                throw new DecompressionException("Received UNCOMPRESSED_DATA tag before STREAM_IDENTIFIER");
                            }

                            if (k > 65540) {
                                throw new DecompressionException("Received UNCOMPRESSED_DATA larger than 65540 bytes");
                            }

                            if (j < 4 + k) {
                                return;
                            }

                            bytebuf.skipBytes(4);
                            if (this.validateChecksums) {
                                l = ByteBufUtil.swapInt(bytebuf.readInt());
                                Snappy.validateChecksum(l, bytebuf, bytebuf.readerIndex(), k - 4);
                            } else {
                                bytebuf.skipBytes(4);
                            }

                            list.add(bytebuf.readSlice(k - 4).retain());
                            break;
                        case COMPRESSED_DATA:
                            if (!this.started) {
                                throw new DecompressionException("Received COMPRESSED_DATA tag before STREAM_IDENTIFIER");
                            }

                            if (j < 4 + k) {
                                return;
                            }

                            bytebuf.skipBytes(4);
                            l = ByteBufUtil.swapInt(bytebuf.readInt());
                            ByteBuf bytebuf1 = channelhandlercontext.alloc().buffer(0);

                            if (this.validateChecksums) {
                                int i1 = bytebuf.writerIndex();

                                try {
                                    bytebuf.writerIndex(bytebuf.readerIndex() + k - 4);
                                    this.snappy.decode(bytebuf, bytebuf1);
                                } finally {
                                    bytebuf.writerIndex(i1);
                                }

                                Snappy.validateChecksum(l, bytebuf1, 0, bytebuf1.writerIndex());
                            } else {
                                this.snappy.decode(bytebuf.readSlice(k - 4), bytebuf1);
                            }

                            list.add(bytebuf1);
                            this.snappy.reset();
                    }

                }
            } catch (Exception exception) {
                this.corrupted = true;
                throw exception;
            }
        }
    }

    static SnappyFramedDecoder.ChunkType mapChunkType(byte b0) {
        return b0 == 0 ? SnappyFramedDecoder.ChunkType.COMPRESSED_DATA : (b0 == 1 ? SnappyFramedDecoder.ChunkType.UNCOMPRESSED_DATA : (b0 == Byte.MIN_VALUE ? SnappyFramedDecoder.ChunkType.STREAM_IDENTIFIER : ((b0 & 128) == 128 ? SnappyFramedDecoder.ChunkType.RESERVED_SKIPPABLE : SnappyFramedDecoder.ChunkType.RESERVED_UNSKIPPABLE)));
    }

    static enum ChunkType {

        STREAM_IDENTIFIER, COMPRESSED_DATA, UNCOMPRESSED_DATA, RESERVED_UNSKIPPABLE, RESERVED_SKIPPABLE;

        private ChunkType() {}
    }
}
