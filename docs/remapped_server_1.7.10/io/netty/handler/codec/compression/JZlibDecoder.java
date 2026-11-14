package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;

public class JZlibDecoder extends ZlibDecoder {

    private final Inflater z;
    private byte[] dictionary;
    private volatile boolean finished;

    public JZlibDecoder() {
        this(ZlibWrapper.ZLIB);
    }

    public JZlibDecoder(ZlibWrapper zlibwrapper) {
        this.z = new Inflater();
        if (zlibwrapper == null) {
            throw new NullPointerException("wrapper");
        } else {
            int i = this.z.init(ZlibUtil.convertWrapperType(zlibwrapper));

            if (i != 0) {
                ZlibUtil.fail(this.z, "initialization failure", i);
            }

        }
    }

    public JZlibDecoder(byte[] abyte) {
        this.z = new Inflater();
        if (abyte == null) {
            throw new NullPointerException("dictionary");
        } else {
            this.dictionary = abyte;
            int i = this.z.inflateInit(JZlib.W_ZLIB);

            if (i != 0) {
                ZlibUtil.fail(this.z, "initialization failure", i);
            }

        }
    }

    @Override
    public boolean isClosed() {
        return this.finished;
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (bytebuf.isReadable()) {
            try {
                int i = bytebuf.readableBytes();

                this.z.avail_in = i;
                if (bytebuf.hasArray()) {
                    this.z.next_in = bytebuf.array();
                    this.z.next_in_index = bytebuf.arrayOffset() + bytebuf.readerIndex();
                } else {
                    byte[] abyte = new byte[i];

                    bytebuf.getBytes(bytebuf.readerIndex(), abyte);
                    this.z.next_in = abyte;
                    this.z.next_in_index = 0;
                }

                int j = this.z.next_in_index;
                int k = i << 1;
                ByteBuf bytebuf1 = channelhandlercontext.alloc().heapBuffer(k);

                try {
                    while (true) {
                        this.z.avail_out = k;
                        bytebuf1.ensureWritable(k);
                        this.z.next_out = bytebuf1.array();
                        this.z.next_out_index = bytebuf1.arrayOffset() + bytebuf1.writerIndex();
                        int l = this.z.next_out_index;
                        int i1 = this.z.inflate(2);
                        int j1 = this.z.next_out_index - l;

                        if (j1 > 0) {
                            bytebuf1.writerIndex(bytebuf1.writerIndex() + j1);
                        }

                        switch (i1) {
                            case -5:
                                if (this.z.avail_in <= 0) {
                                    return;
                                }
                                break;
                            case -4:
                            case -3:
                            case -2:
                            case -1:
                            default:
                                ZlibUtil.fail(this.z, "decompression failure", i1);
                            case 0:
                                break;
                            case 1:
                                this.finished = true;
                                this.z.inflateEnd();
                                return;
                            case 2:
                                if (this.dictionary == null) {
                                    ZlibUtil.fail(this.z, "decompression failure", i1);
                                } else {
                                    i1 = this.z.inflateSetDictionary(this.dictionary, this.dictionary.length);
                                    if (i1 != 0) {
                                        ZlibUtil.fail(this.z, "failed to set the dictionary", i1);
                                    }
                                }
                        }
                    }
                } finally {
                    bytebuf.skipBytes(this.z.next_in_index - j);
                    if (bytebuf1.isReadable()) {
                        list.add(bytebuf1);
                    } else {
                        bytebuf1.release();
                    }

                }
            } finally {
                this.z.next_in = null;
                this.z.next_out = null;
            }
        }
    }
}
