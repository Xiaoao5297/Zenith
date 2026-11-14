package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTrafficShapingHandler extends ChannelDuplexHandler {

    public static final long DEFAULT_CHECK_INTERVAL = 1000L;
    private static final long MINIMAL_WAIT = 10L;
    protected TrafficCounter trafficCounter;
    private long writeLimit;
    private long readLimit;
    protected long checkInterval;
    private static final AttributeKey<Boolean> READ_SUSPENDED = new AttributeKey<>("readSuspended");
    private static final AttributeKey<Runnable> REOPEN_TASK = new AttributeKey<>("reopenTask");

    void setTrafficCounter(TrafficCounter trafficcounter) {
        this.trafficCounter = trafficcounter;
    }

    protected AbstractTrafficShapingHandler(long i, long j, long k) {
        this.checkInterval = 1000L;
        this.writeLimit = i;
        this.readLimit = j;
        this.checkInterval = k;
    }

    protected AbstractTrafficShapingHandler(long i, long j) {
        this(i, j, 1000L);
    }

    protected AbstractTrafficShapingHandler() {
        this(0L, 0L, 1000L);
    }

    protected AbstractTrafficShapingHandler(long i) {
        this(0L, 0L, i);
    }

    public void configure(long i, long j, long k) {
        this.configure(i, j);
        this.configure(k);
    }

    public void configure(long i, long j) {
        this.writeLimit = i;
        this.readLimit = j;
        if (this.trafficCounter != null) {
            this.trafficCounter.resetAccounting(System.currentTimeMillis() + 1L);
        }

    }

    public void configure(long i) {
        this.checkInterval = i;
        if (this.trafficCounter != null) {
            this.trafficCounter.configure(this.checkInterval);
        }

    }

    protected void doAccounting(TrafficCounter trafficcounter) {}

    private static long getTimeToWait(long i, long j, long k, long l) {
        long i1 = l - k;

        return i1 <= 0L ? 0L : (j * 1000L / i - i1) / 10L * 10L;
    }

    @Override
    public void channelRead(final ChannelHandlerContext channelhandlercontext, final Object object) throws Exception {
        long i = this.calculateSize(object);
        long j = System.currentTimeMillis();

        if (this.trafficCounter != null) {
            this.trafficCounter.bytesRecvFlowControl(i);
            if (this.readLimit == 0L) {
                channelhandlercontext.fireChannelRead(object);
                return;
            }

            long k = getTimeToWait(this.readLimit, this.trafficCounter.currentReadBytes(), this.trafficCounter.lastTime(), j);

            if (k >= 10L) {
                if (isSuspended(channelhandlercontext)) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            channelhandlercontext.fireChannelRead(object);
                        }
                    };

                    channelhandlercontext.executor().schedule(runnable, k, TimeUnit.MILLISECONDS);
                    return;
                }

                channelhandlercontext.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).set(true);
                Attribute attribute = channelhandlercontext.attr(AbstractTrafficShapingHandler.REOPEN_TASK);
                Object object1 = (Runnable) attribute.get();

                if (object1 == null) {
                    object1 = new AbstractTrafficShapingHandler.ReopenReadTimerTask(channelhandlercontext);
                    attribute.set(object1);
                }

                channelhandlercontext.executor().schedule((Runnable) object1, k, TimeUnit.MILLISECONDS);
            }
        }

        channelhandlercontext.fireChannelRead(object);
    }

    @Override
    public void read(ChannelHandlerContext channelhandlercontext) {
        if (!isSuspended(channelhandlercontext)) {
            channelhandlercontext.read();
        }

    }

    private static boolean isSuspended(ChannelHandlerContext channelhandlercontext) {
        Boolean obool = (Boolean) channelhandlercontext.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).get();

        return obool != null && !Boolean.FALSE.equals(obool);
    }

    @Override
    public void write(final ChannelHandlerContext channelhandlercontext, final Object object, final ChannelPromise channelpromise) throws Exception {
        long i = System.currentTimeMillis();
        long j = this.calculateSize(object);

        if (j > -1L && this.trafficCounter != null) {
            this.trafficCounter.bytesWriteFlowControl(j);
            if (this.writeLimit == 0L) {
                channelhandlercontext.write(object, channelpromise);
                return;
            }

            long k = getTimeToWait(this.writeLimit, this.trafficCounter.currentWrittenBytes(), this.trafficCounter.lastTime(), i);

            if (k >= 10L) {
                channelhandlercontext.executor().schedule(new Runnable() {
                    public void run() {
                        channelhandlercontext.write(object, channelpromise);
                    }
                }, k, TimeUnit.MILLISECONDS);
                return;
            }
        }

        channelhandlercontext.write(object, channelpromise);
    }

    public TrafficCounter trafficCounter() {
        return this.trafficCounter;
    }

    public String toString() {
        return "TrafficShaping with Write Limit: " + this.writeLimit + " Read Limit: " + this.readLimit + " and Counter: " + (this.trafficCounter != null ? this.trafficCounter.toString() : "none");
    }

    protected long calculateSize(Object object) {
        return object instanceof ByteBuf ? (long) ((ByteBuf) object).readableBytes() : (object instanceof ByteBufHolder ? (long) ((ByteBufHolder) object).content().readableBytes() : -1L);
    }

    private static final class ReopenReadTimerTask implements Runnable {

        final ChannelHandlerContext ctx;

        ReopenReadTimerTask(ChannelHandlerContext channelhandlercontext) {
            this.ctx = channelhandlercontext;
        }

        public void run() {
            this.ctx.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).set(false);
            this.ctx.read();
        }
    }
}
