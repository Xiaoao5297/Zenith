package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class JdkZlibDecoder extends ZlibDecoder {

    private static final int FHCRC = 2;
    private static final int FEXTRA = 4;
    private static final int FNAME = 8;
    private static final int FCOMMENT = 16;
    private static final int FRESERVED = 224;
    private final Inflater inflater;
    private final byte[] dictionary;
    private final CRC32 crc;
    private JdkZlibDecoder.GzipState gzipState;
    private int flags;
    private int xlen;
    private volatile boolean finished;

    public JdkZlibDecoder() {
        this(ZlibWrapper.ZLIB, (byte[]) null);
    }

    public JdkZlibDecoder(byte[] abyte) {
        this(ZlibWrapper.ZLIB, abyte);
    }

    public JdkZlibDecoder(ZlibWrapper zlibwrapper) {
        this(zlibwrapper, (byte[]) null);
    }

    private JdkZlibDecoder(ZlibWrapper zlibwrapper, byte[] abyte) {
        this.gzipState = JdkZlibDecoder.GzipState.HEADER_START;
        this.flags = -1;
        this.xlen = -1;
        if (zlibwrapper == null) {
            throw new NullPointerException("wrapper");
        } else {
            switch (zlibwrapper) {
                case GZIP:
                    this.inflater = new Inflater(true);
                    this.crc = new CRC32();
                    break;
                case NONE:
                    this.inflater = new Inflater(true);
                    this.crc = null;
                    break;
                case ZLIB:
                    this.inflater = new Inflater();
                    this.crc = null;
                    break;
                default:
                    throw new IllegalArgumentException("Only GZIP or ZLIB is supported, but you used " + zlibwrapper);
            }

            this.dictionary = abyte;
        }
    }

    @Override
    public boolean isClosed() {
        return this.finished;
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (bytebuf.isReadable() || !this.finished) {
            if (this.crc != null) {
                switch (this.gzipState) {
                    case FOOTER_START:
                        if (this.readGZIPFooter(bytebuf)) {
                            this.finished = true;
                        }

                        return;
                    default:
                        if (this.gzipState != JdkZlibDecoder.GzipState.HEADER_END && !this.readGZIPHeader(bytebuf)) {
                            return;
                        }
                }
            }

            int i = bytebuf.readableBytes();

            if (bytebuf.hasArray()) {
                this.inflater.setInput(bytebuf.array(), bytebuf.arrayOffset() + bytebuf.readerIndex(), bytebuf.readableBytes());
            } else {
                byte[] abyte = new byte[bytebuf.readableBytes()];

                bytebuf.getBytes(bytebuf.readerIndex(), abyte);
                this.inflater.setInput(abyte);
            }

            int j = this.inflater.getRemaining() << 1;
            ByteBuf bytebuf1 = channelhandlercontext.alloc().heapBuffer(j);

            try {
                boolean flag = false;

                while (!this.inflater.needsInput()) {
                    byte[] abyte1 = bytebuf1.array();
                    int k = bytebuf1.arrayOffset() + bytebuf1.writerIndex();
                    int l = abyte1.length - k;
                    int i1 = this.inflater.inflate(abyte1, k, l);

                    if (i1 > 0) {
                        bytebuf1.writerIndex(bytebuf1.writerIndex() + i1);
                        if (this.crc != null) {
                            this.crc.update(abyte1, k, i1);
                        }
                    } else if (this.inflater.needsDictionary()) {
                        if (this.dictionary == null) {
                            throw new DecompressionException("decompression failure, unable to set dictionary as non was specified");
                        }

                        this.inflater.setDictionary(this.dictionary);
                    }

                    if (this.inflater.finished()) {
                        if (this.crc == null) {
                            this.finished = true;
                        } else {
                            flag = true;
                        }
                        break;
                    }
                }

                bytebuf.skipBytes(i - this.inflater.getRemaining());
                if (flag) {
                    this.gzipState = JdkZlibDecoder.GzipState.FOOTER_START;
                    if (this.readGZIPFooter(bytebuf)) {
                        this.finished = true;
                    }
                }
            } catch (DataFormatException dataformatexception) {
                throw new DecompressionException("decompression failure", dataformatexception);
            } finally {
                if (bytebuf1.isReadable()) {
                    list.add(bytebuf1);
                } else {
                    bytebuf1.release();
                }

            }

        }
    }

    @Override
    protected void handlerRemoved0(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.handlerRemoved0(channelhandlercontext);
        this.inflater.end();
    }

    private boolean readGZIPHeader(ByteBuf bytebuf) {
        short short0;

        switch (this.gzipState) {
            case HEADER_START:
                if (bytebuf.readableBytes() < 10) {
                    return false;
                } else {
                    byte b0 = bytebuf.readByte();
                    byte b1 = bytebuf.readByte();

                    if (b0 != 31) {
                        throw new CompressionException("Input is not in the GZIP format");
                    } else {
                        this.crc.update(b0);
                        this.crc.update(b1);
                        short short1 = bytebuf.readUnsignedByte();

                        if (short1 != 8) {
                            throw new CompressionException("Unsupported compression method " + short1 + " in the GZIP header");
                        } else {
                            this.crc.update(short1);
                            this.flags = bytebuf.readUnsignedByte();
                            this.crc.update(this.flags);
                            if ((this.flags & 224) != 0) {
                                throw new CompressionException("Reserved flags are set in the GZIP header");
                            } else {
                                this.crc.update(bytebuf.readByte());
                                this.crc.update(bytebuf.readByte());
                                this.crc.update(bytebuf.readByte());
                                this.crc.update(bytebuf.readByte());
                                this.crc.update(bytebuf.readUnsignedByte());
                                this.crc.update(bytebuf.readUnsignedByte());
                                this.gzipState = JdkZlibDecoder.GzipState.FLG_READ;
                            }
                        }
                    }
                }
            case FLG_READ:
                if ((this.flags & 4) != 0) {
                    if (bytebuf.readableBytes() < 2) {
                        return false;
                    }

                    short0 = bytebuf.readUnsignedByte();
                    short short2 = bytebuf.readUnsignedByte();

                    this.crc.update(short0);
                    this.crc.update(short2);
                    this.xlen |= short0 << 8 | short2;
                }

                this.gzipState = JdkZlibDecoder.GzipState.XLEN_READ;
            case XLEN_READ:
                if (this.xlen != -1) {
                    if (bytebuf.readableBytes() < this.xlen) {
                        return false;
                    }

                    byte[] abyte = new byte[this.xlen];

                    bytebuf.readBytes(abyte);
                    this.crc.update(abyte);
                }

                this.gzipState = JdkZlibDecoder.GzipState.SKIP_FNAME;
            case SKIP_FNAME:
                if ((this.flags & 8) != 0) {
                    if (!bytebuf.isReadable()) {
                        return false;
                    }

                    do {
                        short0 = bytebuf.readUnsignedByte();
                        this.crc.update(short0);
                    } while (short0 != 0 && bytebuf.isReadable());
                }

                this.gzipState = JdkZlibDecoder.GzipState.SKIP_COMMENT;
            case SKIP_COMMENT:
                if ((this.flags & 16) != 0) {
                    if (!bytebuf.isReadable()) {
                        return false;
                    }

                    do {
                        short0 = bytebuf.readUnsignedByte();
                        this.crc.update(short0);
                    } while (short0 != 0 && bytebuf.isReadable());
                }

                this.gzipState = JdkZlibDecoder.GzipState.PROCESS_FHCRC;
            case PROCESS_FHCRC:
                if ((this.flags & 2) != 0) {
                    if (bytebuf.readableBytes() < 4) {
                        return false;
                    }

                    this.verifyCrc(bytebuf);
                }

                this.crc.reset();
                this.gzipState = JdkZlibDecoder.GzipState.HEADER_END;
            case HEADER_END:
                return true;
            default:
                throw new IllegalStateException();
        }
    }

    private boolean readGZIPFooter(ByteBuf bytebuf) {
        if (bytebuf.readableBytes() < 8) {
            return false;
        } else {
            this.verifyCrc(bytebuf);
            int i = 0;

            int j;

            for (j = 0; j < 4; ++j) {
                i |= bytebuf.readUnsignedByte() << j * 8;
            }

            j = this.inflater.getTotalOut();
            if (i != j) {
                throw new CompressionException("Number of bytes missmatch. Expected: " + i + ", Got: " + j);
            } else {
                return true;
            }
        }
    }

    private void verifyCrc(ByteBuf bytebuf) {
        long i = 0L;

        for (int j = 0; j < 4; ++j) {
            i |= (long) bytebuf.readUnsignedByte() << j * 8;
        }

        long k = this.crc.getValue();

        if (i != k) {
            throw new CompressionException("CRC value missmatch. Expected: " + i + ", Got: " + k);
        }
    }

    private static enum GzipState {

        HEADER_START, HEADER_END, FLG_READ, XLEN_READ, SKIP_FNAME, SKIP_COMMENT, PROCESS_FHCRC, FOOTER_START;

        private GzipState() {}
    }
}
