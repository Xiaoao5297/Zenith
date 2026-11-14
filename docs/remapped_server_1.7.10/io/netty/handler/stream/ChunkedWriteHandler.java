package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

public class ChunkedWriteHandler extends ChannelDuplexHandler {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChunkedWriteHandler.class);
    private final Queue<ChunkedWriteHandler.PendingWrite> queue = new ArrayDeque();
    private volatile ChannelHandlerContext ctx;
    private ChunkedWriteHandler.PendingWrite currentWrite;

    public ChunkedWriteHandler() {}

    /** @deprecated */
    @Deprecated
    public ChunkedWriteHandler(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("maxPendingWrites: " + i + " (expected: > 0)");
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.ctx = channelhandlercontext;
    }

    public void resumeTransfer() {
        final ChannelHandlerContext channelhandlercontext = this.ctx;

        if (channelhandlercontext != null) {
            if (channelhandlercontext.executor().inEventLoop()) {
                try {
                    this.doFlush(channelhandlercontext);
                } catch (Exception exception) {
                    if (ChunkedWriteHandler.logger.isWarnEnabled()) {
                        ChunkedWriteHandler.logger.warn("Unexpected exception while sending chunks.", (Throwable) exception);
                    }
                }
            } else {
                channelhandlercontext.executor().execute(new Runnable() {
                    public void run() {
                        try {
                            ChunkedWriteHandler.this.doFlush(channelhandlercontext);
                        } catch (Exception exception1) {
                            if (ChunkedWriteHandler.logger.isWarnEnabled()) {
                                ChunkedWriteHandler.logger.warn("Unexpected exception while sending chunks.", (Throwable) exception1);
                            }
                        }

                    }
                });
            }

        }
    }

    @Override
    public void write(ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception {
        this.queue.add(new ChunkedWriteHandler.PendingWrite(object, channelpromise));
    }

    @Override
    public void flush(ChannelHandlerContext channelhandlercontext) throws Exception {
        Channel channel = channelhandlercontext.channel();

        if (channel.isWritable() || !channel.isActive()) {
            this.doFlush(channelhandlercontext);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.doFlush(channelhandlercontext);
        super.channelInactive(channelhandlercontext);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (channelhandlercontext.channel().isWritable()) {
            this.doFlush(channelhandlercontext);
        }

        channelhandlercontext.fireChannelWritabilityChanged();
    }

    private void discard(Throwable throwable) {
        while (true) {
            ChunkedWriteHandler.PendingWrite chunkedwritehandler_pendingwrite = this.currentWrite;

            if (this.currentWrite == null) {
                chunkedwritehandler_pendingwrite = (ChunkedWriteHandler.PendingWrite) this.queue.poll();
            } else {
                this.currentWrite = null;
            }

            if (chunkedwritehandler_pendingwrite == null) {
                return;
            }

            Object object = chunkedwritehandler_pendingwrite.msg;

            if (object instanceof ChunkedInput) {
                ChunkedInput chunkedinput = (ChunkedInput) object;

                try {
                    if (!chunkedinput.isEndOfInput()) {
                        if (throwable == null) {
                            throwable = new ClosedChannelException();
                        }

                        chunkedwritehandler_pendingwrite.fail((Throwable) throwable);
                    } else {
                        chunkedwritehandler_pendingwrite.success();
                    }

                    closeInput(chunkedinput);
                } catch (Exception exception) {
                    chunkedwritehandler_pendingwrite.fail(exception);
                    ChunkedWriteHandler.logger.warn(ChunkedInput.class.getSimpleName() + ".isEndOfInput() failed", (Throwable) exception);
                    closeInput(chunkedinput);
                }
            } else {
                if (throwable == null) {
                    throwable = new ClosedChannelException();
                }

                chunkedwritehandler_pendingwrite.fail((Throwable) throwable);
            }
        }
    }

    private void doFlush(final ChannelHandlerContext channelhandlercontext) throws Exception {
        final Channel channel = channelhandlercontext.channel();

        if (!channel.isActive()) {
            this.discard((Throwable) null);
        } else {
            while (channel.isWritable()) {
                if (this.currentWrite == null) {
                    this.currentWrite = (ChunkedWriteHandler.PendingWrite) this.queue.poll();
                }

                if (this.currentWrite == null) {
                    break;
                }

                boolean flag = true;
                final ChunkedWriteHandler.PendingWrite chunkedwritehandler_pendingwrite = this.currentWrite;
                final Object object = chunkedwritehandler_pendingwrite.msg;

                if (object instanceof ChunkedInput) {
                    final ChunkedInput chunkedinput = (ChunkedInput) object;
                    Object object1 = null;

                    boolean flag1;
                    boolean flag2;

                    try {
                        object1 = chunkedinput.readChunk(channelhandlercontext);
                        flag1 = chunkedinput.isEndOfInput();
                        if (object1 == null) {
                            flag2 = !flag1;
                        } else {
                            flag2 = false;
                        }
                    } catch (final Throwable throwable) {
                        this.currentWrite = null;
                        if (object1 != null) {
                            ReferenceCountUtil.release(object1);
                        }

                        chunkedwritehandler_pendingwrite.fail(throwable);
                        if (channelhandlercontext.executor().inEventLoop()) {
                            channelhandlercontext.fireExceptionCaught(throwable);
                        } else {
                            channelhandlercontext.executor().execute(new Runnable() {
                                public void run() {
                                    channelhandlercontext.fireExceptionCaught(throwable);
                                }
                            });
                        }

                        closeInput(chunkedinput);
                        break;
                    }

                    if (flag2) {
                        break;
                    }

                    if (object1 == null) {
                        object1 = Unpooled.EMPTY_BUFFER;
                    }

                    final int i = amount(object1);
                    ChannelFuture channelfuture = channelhandlercontext.write(object1);

                    if (flag1) {
                        this.currentWrite = null;
                        channelfuture.addListener(new ChannelFutureListener() {
                            public void operationComplete(ChannelFuture channelfuture1) throws Exception {
                                chunkedwritehandler_pendingwrite.progress(i);
                                chunkedwritehandler_pendingwrite.success();
                                ChunkedWriteHandler.closeInput(chunkedinput);
                            }
                        });
                    } else if (channel.isWritable()) {
                        channelfuture.addListener(new ChannelFutureListener() {
                            public void operationComplete(ChannelFuture channelfuture1) throws Exception {
                                if (!channelfuture1.isSuccess()) {
                                    ChunkedWriteHandler.closeInput((ChunkedInput) object);
                                    chunkedwritehandler_pendingwrite.fail(channelfuture1.cause());
                                } else {
                                    chunkedwritehandler_pendingwrite.progress(i);
                                }

                            }
                        });
                    } else {
                        channelfuture.addListener(new ChannelFutureListener() {
                            public void operationComplete(ChannelFuture channelfuture1) throws Exception {
                                if (!channelfuture1.isSuccess()) {
                                    ChunkedWriteHandler.closeInput((ChunkedInput) object);
                                    chunkedwritehandler_pendingwrite.fail(channelfuture1.cause());
                                } else {
                                    chunkedwritehandler_pendingwrite.progress(i);
                                    if (channel.isWritable()) {
                                        ChunkedWriteHandler.this.resumeTransfer();
                                    }
                                }

                            }
                        });
                    }
                } else {
                    channelhandlercontext.write(object, chunkedwritehandler_pendingwrite.promise);
                    this.currentWrite = null;
                }

                if (flag) {
                    channelhandlercontext.flush();
                }

                if (!channel.isActive()) {
                    this.discard(new ClosedChannelException());
                    return;
                }
            }

        }
    }

    static void closeInput(ChunkedInput<?> chunkedinput) {
        try {
            chunkedinput.close();
        } catch (Throwable throwable) {
            if (ChunkedWriteHandler.logger.isWarnEnabled()) {
                ChunkedWriteHandler.logger.warn("Failed to close a chunked input.", throwable);
            }
        }

    }

    private static int amount(Object object) {
        return object instanceof ByteBuf ? ((ByteBuf) object).readableBytes() : (object instanceof ByteBufHolder ? ((ByteBufHolder) object).content().readableBytes() : 1);
    }

    private static final class PendingWrite {

        final Object msg;
        final ChannelPromise promise;
        private long progress;

        PendingWrite(Object object, ChannelPromise channelpromise) {
            this.msg = object;
            this.promise = channelpromise;
        }

        void fail(Throwable throwable) {
            ReferenceCountUtil.release(this.msg);
            if (this.promise != null) {
                this.promise.setFailure(throwable);
            }

        }

        void success() {
            if (this.promise instanceof ChannelProgressivePromise) {
                ((ChannelProgressivePromise) this.promise).tryProgress(this.progress, this.progress);
            }

            this.promise.setSuccess();
        }

        void progress(int i) {
            this.progress += (long) i;
            if (this.promise instanceof ChannelProgressivePromise) {
                ((ChannelProgressivePromise) this.promise).tryProgress(this.progress, -1L);
            }

        }
    }
}
