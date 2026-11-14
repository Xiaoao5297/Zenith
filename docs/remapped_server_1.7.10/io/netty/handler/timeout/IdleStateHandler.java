package io.netty.handler.timeout;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class IdleStateHandler extends ChannelDuplexHandler {

    private final long readerIdleTimeMillis;
    private final long writerIdleTimeMillis;
    private final long allIdleTimeMillis;
    volatile ScheduledFuture<?> readerIdleTimeout;
    volatile long lastReadTime;
    private boolean firstReaderIdleEvent;
    volatile ScheduledFuture<?> writerIdleTimeout;
    volatile long lastWriteTime;
    private boolean firstWriterIdleEvent;
    volatile ScheduledFuture<?> allIdleTimeout;
    private boolean firstAllIdleEvent;
    private volatile int state;

    public IdleStateHandler(int i, int j, int k) {
        this((long) i, (long) j, (long) k, TimeUnit.SECONDS);
    }

    public IdleStateHandler(long i, long j, long k, TimeUnit timeunit) {
        this.firstReaderIdleEvent = true;
        this.firstWriterIdleEvent = true;
        this.firstAllIdleEvent = true;
        if (timeunit == null) {
            throw new NullPointerException("unit");
        } else {
            if (i <= 0L) {
                this.readerIdleTimeMillis = 0L;
            } else {
                this.readerIdleTimeMillis = Math.max(timeunit.toMillis(i), 1L);
            }

            if (j <= 0L) {
                this.writerIdleTimeMillis = 0L;
            } else {
                this.writerIdleTimeMillis = Math.max(timeunit.toMillis(j), 1L);
            }

            if (k <= 0L) {
                this.allIdleTimeMillis = 0L;
            } else {
                this.allIdleTimeMillis = Math.max(timeunit.toMillis(k), 1L);
            }

        }
    }

    public long getReaderIdleTimeInMillis() {
        return this.readerIdleTimeMillis;
    }

    public long getWriterIdleTimeInMillis() {
        return this.writerIdleTimeMillis;
    }

    public long getAllIdleTimeInMillis() {
        return this.allIdleTimeMillis;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (channelhandlercontext.channel().isActive() & channelhandlercontext.channel().isRegistered()) {
            this.initialize(channelhandlercontext);
        }

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.destroy();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (channelhandlercontext.channel().isActive()) {
            this.initialize(channelhandlercontext);
        }

        super.channelRegistered(channelhandlercontext);
    }

    @Override
    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.initialize(channelhandlercontext);
        super.channelActive(channelhandlercontext);
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.destroy();
        super.channelInactive(channelhandlercontext);
    }

    @Override
    public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        this.lastReadTime = System.currentTimeMillis();
        this.firstReaderIdleEvent = this.firstAllIdleEvent = true;
        channelhandlercontext.fireChannelRead(object);
    }

    @Override
    public void write(ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception {
        channelpromise.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelfuture) throws Exception {
                IdleStateHandler.this.lastWriteTime = System.currentTimeMillis();
                IdleStateHandler.this.firstWriterIdleEvent = IdleStateHandler.this.firstAllIdleEvent = true;
            }
        });
        channelhandlercontext.write(object, channelpromise);
    }

    private void initialize(ChannelHandlerContext channelhandlercontext) {
        switch (this.state) {
            case 1:
            case 2:
                return;
            default:
                this.state = 1;
                EventExecutor eventexecutor = channelhandlercontext.executor();

                this.lastReadTime = this.lastWriteTime = System.currentTimeMillis();
                if (this.readerIdleTimeMillis > 0L) {
                    this.readerIdleTimeout = eventexecutor.schedule((Runnable) (new IdleStateHandler.ReaderIdleTimeoutTask(channelhandlercontext)), this.readerIdleTimeMillis, TimeUnit.MILLISECONDS);
                }

                if (this.writerIdleTimeMillis > 0L) {
                    this.writerIdleTimeout = eventexecutor.schedule((Runnable) (new IdleStateHandler.WriterIdleTimeoutTask(channelhandlercontext)), this.writerIdleTimeMillis, TimeUnit.MILLISECONDS);
                }

                if (this.allIdleTimeMillis > 0L) {
                    this.allIdleTimeout = eventexecutor.schedule((Runnable) (new IdleStateHandler.AllIdleTimeoutTask(channelhandlercontext)), this.allIdleTimeMillis, TimeUnit.MILLISECONDS);
                }

        }
    }

    private void destroy() {
        this.state = 2;
        if (this.readerIdleTimeout != null) {
            this.readerIdleTimeout.cancel(false);
            this.readerIdleTimeout = null;
        }

        if (this.writerIdleTimeout != null) {
            this.writerIdleTimeout.cancel(false);
            this.writerIdleTimeout = null;
        }

        if (this.allIdleTimeout != null) {
            this.allIdleTimeout.cancel(false);
            this.allIdleTimeout = null;
        }

    }

    protected void channelIdle(ChannelHandlerContext channelhandlercontext, IdleStateEvent idlestateevent) throws Exception {
        channelhandlercontext.fireUserEventTriggered(idlestateevent);
    }

    private final class AllIdleTimeoutTask implements Runnable {

        private final ChannelHandlerContext ctx;

        AllIdleTimeoutTask(ChannelHandlerContext channelhandlercontext) {
            this.ctx = channelhandlercontext;
        }

        public void run() {
            if (this.ctx.channel().isOpen()) {
                long i = System.currentTimeMillis();
                long j = Math.max(IdleStateHandler.this.lastReadTime, IdleStateHandler.this.lastWriteTime);
                long k = IdleStateHandler.this.allIdleTimeMillis - (i - j);

                if (k <= 0L) {
                    IdleStateHandler.this.allIdleTimeout = this.ctx.executor().schedule((Runnable) this, IdleStateHandler.this.allIdleTimeMillis, TimeUnit.MILLISECONDS);

                    try {
                        IdleStateEvent idlestateevent;

                        if (IdleStateHandler.this.firstAllIdleEvent) {
                            IdleStateHandler.this.firstAllIdleEvent = false;
                            idlestateevent = IdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT;
                        } else {
                            idlestateevent = IdleStateEvent.ALL_IDLE_STATE_EVENT;
                        }

                        IdleStateHandler.this.channelIdle(this.ctx, idlestateevent);
                    } catch (Throwable throwable) {
                        this.ctx.fireExceptionCaught(throwable);
                    }
                } else {
                    IdleStateHandler.this.allIdleTimeout = this.ctx.executor().schedule((Runnable) this, k, TimeUnit.MILLISECONDS);
                }

            }
        }
    }

    private final class WriterIdleTimeoutTask implements Runnable {

        private final ChannelHandlerContext ctx;

        WriterIdleTimeoutTask(ChannelHandlerContext channelhandlercontext) {
            this.ctx = channelhandlercontext;
        }

        public void run() {
            if (this.ctx.channel().isOpen()) {
                long i = System.currentTimeMillis();
                long j = IdleStateHandler.this.lastWriteTime;
                long k = IdleStateHandler.this.writerIdleTimeMillis - (i - j);

                if (k <= 0L) {
                    IdleStateHandler.this.writerIdleTimeout = this.ctx.executor().schedule((Runnable) this, IdleStateHandler.this.writerIdleTimeMillis, TimeUnit.MILLISECONDS);

                    try {
                        IdleStateEvent idlestateevent;

                        if (IdleStateHandler.this.firstWriterIdleEvent) {
                            IdleStateHandler.this.firstWriterIdleEvent = false;
                            idlestateevent = IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT;
                        } else {
                            idlestateevent = IdleStateEvent.WRITER_IDLE_STATE_EVENT;
                        }

                        IdleStateHandler.this.channelIdle(this.ctx, idlestateevent);
                    } catch (Throwable throwable) {
                        this.ctx.fireExceptionCaught(throwable);
                    }
                } else {
                    IdleStateHandler.this.writerIdleTimeout = this.ctx.executor().schedule((Runnable) this, k, TimeUnit.MILLISECONDS);
                }

            }
        }
    }

    private final class ReaderIdleTimeoutTask implements Runnable {

        private final ChannelHandlerContext ctx;

        ReaderIdleTimeoutTask(ChannelHandlerContext channelhandlercontext) {
            this.ctx = channelhandlercontext;
        }

        public void run() {
            if (this.ctx.channel().isOpen()) {
                long i = System.currentTimeMillis();
                long j = IdleStateHandler.this.lastReadTime;
                long k = IdleStateHandler.this.readerIdleTimeMillis - (i - j);

                if (k <= 0L) {
                    IdleStateHandler.this.readerIdleTimeout = this.ctx.executor().schedule((Runnable) this, IdleStateHandler.this.readerIdleTimeMillis, TimeUnit.MILLISECONDS);

                    try {
                        IdleStateEvent idlestateevent;

                        if (IdleStateHandler.this.firstReaderIdleEvent) {
                            IdleStateHandler.this.firstReaderIdleEvent = false;
                            idlestateevent = IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT;
                        } else {
                            idlestateevent = IdleStateEvent.READER_IDLE_STATE_EVENT;
                        }

                        IdleStateHandler.this.channelIdle(this.ctx, idlestateevent);
                    } catch (Throwable throwable) {
                        this.ctx.fireExceptionCaught(throwable);
                    }
                } else {
                    IdleStateHandler.this.readerIdleTimeout = this.ctx.executor().schedule((Runnable) this, k, TimeUnit.MILLISECONDS);
                }

            }
        }
    }
}
