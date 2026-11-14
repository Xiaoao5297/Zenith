package io.netty.handler.timeout;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WriteTimeoutHandler extends ChannelOutboundHandlerAdapter {

    private final long timeoutMillis;
    private boolean closed;

    public WriteTimeoutHandler(int i) {
        this((long) i, TimeUnit.SECONDS);
    }

    public WriteTimeoutHandler(long i, TimeUnit timeunit) {
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
    public void write(ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception {
        this.scheduleTimeout(channelhandlercontext, channelpromise);
        channelhandlercontext.write(object, channelpromise);
    }

    private void scheduleTimeout(final ChannelHandlerContext channelhandlercontext, final ChannelPromise channelpromise) {
        if (this.timeoutMillis > 0L) {
            final ScheduledFuture scheduledfuture = channelhandlercontext.executor().schedule(new Runnable() {
                public void run() {
                    if (channelpromise.tryFailure(WriteTimeoutException.INSTANCE)) {
                        try {
                            WriteTimeoutHandler.this.writeTimedOut(channelhandlercontext);
                        } catch (Throwable throwable) {
                            channelhandlercontext.fireExceptionCaught(throwable);
                        }
                    }

                }
            }, this.timeoutMillis, TimeUnit.MILLISECONDS);

            channelpromise.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelfuture) throws Exception {
                    scheduledfuture.cancel(false);
                }
            });
        }

    }

    protected void writeTimedOut(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (!this.closed) {
            channelhandlercontext.fireExceptionCaught(WriteTimeoutException.INSTANCE);
            channelhandlercontext.close();
            this.closed = true;
        }

    }
}
