package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPromiseNotifier;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.EmptyArrays;
import java.util.concurrent.TimeUnit;

public class JZlibEncoder extends ZlibEncoder {

    private final Deflater z;
    private volatile boolean finished;
    private volatile ChannelHandlerContext ctx;

    public JZlibEncoder() {
        this(6);
    }

    public JZlibEncoder(int i) {
        this(ZlibWrapper.ZLIB, i);
    }

    public JZlibEncoder(ZlibWrapper zlibwrapper) {
        this(zlibwrapper, 6);
    }

    public JZlibEncoder(ZlibWrapper zlibwrapper, int i) {
        this(zlibwrapper, i, 15, 8);
    }

    public JZlibEncoder(ZlibWrapper zlibwrapper, int i, int j, int k) {
        this.z = new Deflater();
        if (i >= 0 && i <= 9) {
            if (j >= 9 && j <= 15) {
                if (k >= 1 && k <= 9) {
                    if (zlibwrapper == null) {
                        throw new NullPointerException("wrapper");
                    } else if (zlibwrapper == ZlibWrapper.ZLIB_OR_NONE) {
                        throw new IllegalArgumentException("wrapper '" + ZlibWrapper.ZLIB_OR_NONE + "' is not " + "allowed for compression.");
                    } else {
                        int l = this.z.init(i, j, k, ZlibUtil.convertWrapperType(zlibwrapper));

                        if (l != 0) {
                            ZlibUtil.fail(this.z, "initialization failure", l);
                        }

                    }
                } else {
                    throw new IllegalArgumentException("memLevel: " + k + " (expected: 1-9)");
                }
            } else {
                throw new IllegalArgumentException("windowBits: " + j + " (expected: 9-15)");
            }
        } else {
            throw new IllegalArgumentException("compressionLevel: " + i + " (expected: 0-9)");
        }
    }

    public JZlibEncoder(byte[] abyte) {
        this(6, abyte);
    }

    public JZlibEncoder(int i, byte[] abyte) {
        this(i, 15, 8, abyte);
    }

    public JZlibEncoder(int i, int j, int k, byte[] abyte) {
        this.z = new Deflater();
        if (i >= 0 && i <= 9) {
            if (j >= 9 && j <= 15) {
                if (k >= 1 && k <= 9) {
                    if (abyte == null) {
                        throw new NullPointerException("dictionary");
                    } else {
                        int l = this.z.deflateInit(i, j, k, JZlib.W_ZLIB);

                        if (l != 0) {
                            ZlibUtil.fail(this.z, "initialization failure", l);
                        } else {
                            l = this.z.deflateSetDictionary(abyte, abyte.length);
                            if (l != 0) {
                                ZlibUtil.fail(this.z, "failed to set the dictionary", l);
                            }
                        }

                    }
                } else {
                    throw new IllegalArgumentException("memLevel: " + k + " (expected: 1-9)");
                }
            } else {
                throw new IllegalArgumentException("windowBits: " + j + " (expected: 9-15)");
            }
        } else {
            throw new IllegalArgumentException("compressionLevel: " + i + " (expected: 0-9)");
        }
    }

    @Override
    public ChannelFuture close() {
        return this.close(this.ctx().channel().newPromise());
    }

    @Override
    public ChannelFuture close(final ChannelPromise channelpromise) {
        ChannelHandlerContext channelhandlercontext = this.ctx();
        EventExecutor eventexecutor = channelhandlercontext.executor();

        if (eventexecutor.inEventLoop()) {
            return this.finishEncode(channelhandlercontext, channelpromise);
        } else {
            final ChannelPromise channelpromise1 = channelhandlercontext.newPromise();

            eventexecutor.execute(new Runnable() {
                public void run() {
                    ChannelFuture channelfuture = JZlibEncoder.this.finishEncode(JZlibEncoder.this.ctx(), channelpromise1);

                    channelfuture.addListener(new ChannelPromiseNotifier(new ChannelPromise[]{channelpromise}));
                }
            });
            return channelpromise1;
        }
    }

    private ChannelHandlerContext ctx() {
        ChannelHandlerContext channelhandlercontext = this.ctx;

        if (channelhandlercontext == null) {
            throw new IllegalStateException("not added to a pipeline");
        } else {
            return channelhandlercontext;
        }
    }

    @Override
    public boolean isClosed() {
        return this.finished;
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, ByteBuf bytebuf1) throws Exception {
        if (!this.finished) {
            try {
                int i = bytebuf.readableBytes();
                boolean flag = bytebuf.hasArray();

                this.z.avail_in = i;
                if (flag) {
                    this.z.next_in = bytebuf.array();
                    this.z.next_in_index = bytebuf.arrayOffset() + bytebuf.readerIndex();
                } else {
                    byte[] abyte = new byte[i];

                    bytebuf.getBytes(bytebuf.readerIndex(), abyte);
                    this.z.next_in = abyte;
                    this.z.next_in_index = 0;
                }

                int j = this.z.next_in_index;
                int k = (int) Math.ceil((double) i * 1.001D) + 12;

                bytebuf1.ensureWritable(k);
                this.z.avail_out = k;
                this.z.next_out = bytebuf1.array();
                this.z.next_out_index = bytebuf1.arrayOffset() + bytebuf1.writerIndex();
                int l = this.z.next_out_index;

                int i1;

                try {
                    i1 = this.z.deflate(2);
                } finally {
                    bytebuf.skipBytes(this.z.next_in_index - j);
                }

                if (i1 != 0) {
                    ZlibUtil.fail(this.z, "compression failure", i1);
                }

                int j1 = this.z.next_out_index - l;

                if (j1 > 0) {
                    bytebuf1.writerIndex(bytebuf1.writerIndex() + j1);
                }
            } finally {
                this.z.next_in = null;
                this.z.next_out = null;
            }

        }
    }

    @Override
    public void close(final ChannelHandlerContext channelhandlercontext, final ChannelPromise channelpromise) {
        ChannelFuture channelfuture = this.finishEncode(channelhandlercontext, channelhandlercontext.newPromise());

        channelfuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelfuture1) throws Exception {
                channelhandlercontext.close(channelpromise);
            }
        });
        if (!channelfuture.isDone()) {
            channelhandlercontext.executor().schedule(new Runnable() {
                public void run() {
                    channelhandlercontext.close(channelpromise);
                }
            }, 10L, TimeUnit.SECONDS);
        }

    }

    private ChannelFuture finishEncode(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) {
        if (this.finished) {
            channelpromise.setSuccess();
            return channelpromise;
        } else {
            this.finished = true;

            ChannelPromise channelpromise1;

            try {
                this.z.next_in = EmptyArrays.EMPTY_BYTES;
                this.z.next_in_index = 0;
                this.z.avail_in = 0;
                byte[] abyte = new byte[32];

                this.z.next_out = abyte;
                this.z.next_out_index = 0;
                this.z.avail_out = abyte.length;
                int i = this.z.deflate(4);

                if (i == 0 || i == 1) {
                    ByteBuf bytebuf;

                    if (this.z.next_out_index != 0) {
                        bytebuf = Unpooled.wrappedBuffer(abyte, 0, this.z.next_out_index);
                    } else {
                        bytebuf = Unpooled.EMPTY_BUFFER;
                    }

                    return channelhandlercontext.writeAndFlush(bytebuf, channelpromise);
                }

                channelpromise.setFailure(ZlibUtil.deflaterException(this.z, "compression failure", i));
                channelpromise1 = channelpromise;
            } finally {
                this.z.deflateEnd();
                this.z.next_in = null;
                this.z.next_out = null;
            }

            return channelpromise1;
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.ctx = channelhandlercontext;
    }
}
