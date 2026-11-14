package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPromiseNotifier;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

public class JdkZlibEncoder extends ZlibEncoder {

    private final byte[] encodeBuf;
    private final Deflater deflater;
    private volatile boolean finished;
    private volatile ChannelHandlerContext ctx;
    private final boolean gzip;
    private final CRC32 crc;
    private static final byte[] gzipHeader = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, 0};
    private boolean writeHeader;

    public JdkZlibEncoder() {
        this(6);
    }

    public JdkZlibEncoder(int i) {
        this(ZlibWrapper.ZLIB, i);
    }

    public JdkZlibEncoder(ZlibWrapper zlibwrapper) {
        this(zlibwrapper, 6);
    }

    public JdkZlibEncoder(ZlibWrapper zlibwrapper, int i) {
        this.encodeBuf = new byte[8192];
        this.crc = new CRC32();
        this.writeHeader = true;
        if (i >= 0 && i <= 9) {
            if (zlibwrapper == null) {
                throw new NullPointerException("wrapper");
            } else if (zlibwrapper == ZlibWrapper.ZLIB_OR_NONE) {
                throw new IllegalArgumentException("wrapper '" + ZlibWrapper.ZLIB_OR_NONE + "' is not " + "allowed for compression.");
            } else {
                this.gzip = zlibwrapper == ZlibWrapper.GZIP;
                this.deflater = new Deflater(i, zlibwrapper != ZlibWrapper.ZLIB);
            }
        } else {
            throw new IllegalArgumentException("compressionLevel: " + i + " (expected: 0-9)");
        }
    }

    public JdkZlibEncoder(byte[] abyte) {
        this(6, abyte);
    }

    public JdkZlibEncoder(int i, byte[] abyte) {
        this.encodeBuf = new byte[8192];
        this.crc = new CRC32();
        this.writeHeader = true;
        if (i >= 0 && i <= 9) {
            if (abyte == null) {
                throw new NullPointerException("dictionary");
            } else {
                this.gzip = false;
                this.deflater = new Deflater(i);
                this.deflater.setDictionary(abyte);
            }
        } else {
            throw new IllegalArgumentException("compressionLevel: " + i + " (expected: 0-9)");
        }
    }

    @Override
    public ChannelFuture close() {
        return this.close(this.ctx().newPromise());
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
                    ChannelFuture channelfuture = JdkZlibEncoder.this.finishEncode(JdkZlibEncoder.this.ctx(), channelpromise1);

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
        if (this.finished) {
            bytebuf1.writeBytes(bytebuf);
        } else {
            byte[] abyte = new byte[bytebuf.readableBytes()];

            bytebuf.readBytes(abyte);
            int i = (int) Math.ceil((double) abyte.length * 1.001D) + 12;

            bytebuf1.ensureWritable(i);
            if (this.gzip) {
                this.crc.update(abyte);
                if (this.writeHeader) {
                    bytebuf1.writeBytes(JdkZlibEncoder.gzipHeader);
                    this.writeHeader = false;
                }
            }

            this.deflater.setInput(abyte);

            while (!this.deflater.needsInput()) {
                int j = this.deflater.deflate(this.encodeBuf, 0, this.encodeBuf.length, 2);

                bytebuf1.writeBytes(this.encodeBuf, 0, j);
            }

        }
    }

    @Override
    public void close(final ChannelHandlerContext channelhandlercontext, final ChannelPromise channelpromise) throws Exception {
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
            ByteBuf bytebuf = channelhandlercontext.alloc().buffer();

            this.deflater.finish();

            int i;

            while (!this.deflater.finished()) {
                i = this.deflater.deflate(this.encodeBuf, 0, this.encodeBuf.length);
                bytebuf.writeBytes(this.encodeBuf, 0, i);
            }

            if (this.gzip) {
                i = (int) this.crc.getValue();
                int j = this.deflater.getTotalIn();

                bytebuf.writeByte(i);
                bytebuf.writeByte(i >>> 8);
                bytebuf.writeByte(i >>> 16);
                bytebuf.writeByte(i >>> 24);
                bytebuf.writeByte(j);
                bytebuf.writeByte(j >>> 8);
                bytebuf.writeByte(j >>> 16);
                bytebuf.writeByte(j >>> 24);
            }

            this.deflater.end();
            return channelhandlercontext.writeAndFlush(bytebuf, channelpromise);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.ctx = channelhandlercontext;
    }
}
