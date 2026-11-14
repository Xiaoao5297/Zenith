package io.netty.handler.timeout;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ReadTimeoutHandler extends ChannelInboundHandlerAdapter {

    private final long timeoutMillis;
    private volatile ScheduledFuture<?> timeout;
    private volatile long lastReadTime;
    private volatile int state;
    private boolean closed;

    public ReadTimeoutHandler(int i) {
        this((long) i, TimeUnit.SECONDS);
    }

    public ReadTimeoutHandler(long i, TimeUnit timeunit) {
        if (timeunit == null) {
            throw new NullPointerException("unit");
        } else {
            if (i <= 0L) {
                this.timeoutMillis = 0L;
            } else {
                this.timeoutMillis = Math.max(timeunit.toMillis(i), 1L);
            }

        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (channelhandlercontext.channel().isActive() && channelhandlercontext.channel().isRegistered()) {
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
        channelhandlercontext.fireChannelRead(object);
    }

    private void initialize(ChannelHandlerContext channelhandlercontext) {
        switch (this.state) {
            case 1:
            case 2:
                return;
            default:
                this.state = 1;
                this.lastReadTime = System.currentTimeMillis();
                if (this.timeoutMillis > 0L) {
                    this.timeout = channelhandlercontext.executor().schedule((Runnable) (new ReadTimeoutHandler.ReadTimeoutTask(channelhandlercontext)), this.timeoutMillis, TimeUnit.MILLISECONDS);
                }

        }
    }

    private void destroy() {
        this.state = 2;
        if (this.timeout != null) {
            this.timeout.cancel(false);
            this.timeout = null;
        }

    }

    protected void readTimedOut(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (!this.closed) {
            channelhandlercontext.fireExceptionCaught(ReadTimeoutException.INSTANCE);
            channelhandlercontext.close();
            this.closed = true;
        }

    }

    private final class ReadTimeoutTask implements Runnable {

        private final ChannelHandlerContext ctx;

        ReadTimeoutTask(ChannelHandlerContext channelhandlercontext) {
            this.ctx = channelhandlercontext;
        }

        public void run() {
            if (this.ctx.channel().isOpen()) {
                long i = System.currentTimeMillis();
                long j = ReadTimeoutHandler.this.timeoutMillis - (i - ReadTimeoutHandler.this.lastReadTime);

                if (j <= 0L) {
                    ReadTimeoutHandler.this.timeout = this.ctx.executor().schedule((Runnable) this, ReadTimeoutHandler.this.timeoutMillis, TimeUnit.MILLISECONDS);

                    try {
                        ReadTimeoutHandler.this.readTimedOut(this.ctx);
                    } catch (Throwable throwable) {
                        this.ctx.fireExceptionCaught(throwable);
                    }
                } else {
                    ReadTimeoutHandler.this.timeout = this.ctx.executor().schedule((Runnable) this, j, TimeUnit.MILLISECONDS);
                }

            }
        }
    }
}
