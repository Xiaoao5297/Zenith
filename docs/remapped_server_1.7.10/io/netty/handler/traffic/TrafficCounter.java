package io.netty.handler.traffic;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TrafficCounter {

    private final AtomicLong currentWrittenBytes = new AtomicLong();
    private final AtomicLong currentReadBytes = new AtomicLong();
    private final AtomicLong cumulativeWrittenBytes = new AtomicLong();
    private final AtomicLong cumulativeReadBytes = new AtomicLong();
    private long lastCumulativeTime;
    private long lastWriteThroughput;
    private long lastReadThroughput;
    private final AtomicLong lastTime = new AtomicLong();
    private long lastWrittenBytes;
    private long lastReadBytes;
    final AtomicLong checkInterval = new AtomicLong(1000L);
    final String name;
    private final AbstractTrafficShapingHandler trafficShapingHandler;
    private final ScheduledExecutorService executor;
    private Runnable monitor;
    private volatile ScheduledFuture<?> scheduledFuture;
    final AtomicBoolean monitorActive = new AtomicBoolean();

    public void start() {
        AtomicLong atomiclong = this.lastTime;

        synchronized (this.lastTime) {
            if (!this.monitorActive.get()) {
                this.lastTime.set(System.currentTimeMillis());
                if (this.checkInterval.get() > 0L) {
                    this.monitorActive.set(true);
                    this.monitor = new TrafficCounter.TrafficMonitoringTask(this.trafficShapingHandler, this);
                    this.scheduledFuture = this.executor.schedule(this.monitor, this.checkInterval.get(), TimeUnit.MILLISECONDS);
                }

            }
        }
    }

    public void stop() {
        AtomicLong atomiclong = this.lastTime;

        synchronized (this.lastTime) {
            if (this.monitorActive.get()) {
                this.monitorActive.set(false);
                this.resetAccounting(System.currentTimeMillis());
                if (this.trafficShapingHandler != null) {
                    this.trafficShapingHandler.doAccounting(this);
                }

                if (this.scheduledFuture != null) {
                    this.scheduledFuture.cancel(true);
                }

            }
        }
    }

    void resetAccounting(long i) {
        AtomicLong atomiclong = this.lastTime;

        synchronized (this.lastTime) {
            long j = i - this.lastTime.getAndSet(i);

            if (j != 0L) {
                this.lastReadBytes = this.currentReadBytes.getAndSet(0L);
                this.lastWrittenBytes = this.currentWrittenBytes.getAndSet(0L);
                this.lastReadThroughput = this.lastReadBytes / j * 1000L;
                this.lastWriteThroughput = this.lastWrittenBytes / j * 1000L;
            }
        }
    }

    public TrafficCounter(AbstractTrafficShapingHandler abstracttrafficshapinghandler, ScheduledExecutorService scheduledexecutorservice, String s, long i) {
        this.trafficShapingHandler = abstracttrafficshapinghandler;
        this.executor = scheduledexecutorservice;
        this.name = s;
        this.lastCumulativeTime = System.currentTimeMillis();
        this.configure(i);
    }

    public void configure(long i) {
        long j = i / 10L * 10L;

        if (this.checkInterval.get() != j) {
            this.checkInterval.set(j);
            if (j <= 0L) {
                this.stop();
                this.lastTime.set(System.currentTimeMillis());
            } else {
                this.start();
            }
        }

    }

    void bytesRecvFlowControl(long i) {
        this.currentReadBytes.addAndGet(i);
        this.cumulativeReadBytes.addAndGet(i);
    }

    void bytesWriteFlowControl(long i) {
        this.currentWrittenBytes.addAndGet(i);
        this.cumulativeWrittenBytes.addAndGet(i);
    }

    public long checkInterval() {
        return this.checkInterval.get();
    }

    public long lastReadThroughput() {
        return this.lastReadThroughput;
    }

    public long lastWriteThroughput() {
        return this.lastWriteThroughput;
    }

    public long lastReadBytes() {
        return this.lastReadBytes;
    }

    public long lastWrittenBytes() {
        return this.lastWrittenBytes;
    }

    public long currentReadBytes() {
        return this.currentReadBytes.get();
    }

    public long currentWrittenBytes() {
        return this.currentWrittenBytes.get();
    }

    public long lastTime() {
        return this.lastTime.get();
    }

    public long cumulativeWrittenBytes() {
        return this.cumulativeWrittenBytes.get();
    }

    public long cumulativeReadBytes() {
        return this.cumulativeReadBytes.get();
    }

    public long lastCumulativeTime() {
        return this.lastCumulativeTime;
    }

    public void resetCumulativeTime() {
        this.lastCumulativeTime = System.currentTimeMillis();
        this.cumulativeReadBytes.set(0L);
        this.cumulativeWrittenBytes.set(0L);
    }

    public String name() {
        return this.name;
    }

    public String toString() {
        return "Monitor " + this.name + " Current Speed Read: " + (this.lastReadThroughput >> 10) + " KB/s, Write: " + (this.lastWriteThroughput >> 10) + " KB/s Current Read: " + (this.currentReadBytes.get() >> 10) + " KB Current Write: " + (this.currentWrittenBytes.get() >> 10) + " KB";
    }

    private static class TrafficMonitoringTask implements Runnable {

        private final AbstractTrafficShapingHandler trafficShapingHandler1;
        private final TrafficCounter counter;

        protected TrafficMonitoringTask(AbstractTrafficShapingHandler abstracttrafficshapinghandler, TrafficCounter trafficcounter) {
            this.trafficShapingHandler1 = abstracttrafficshapinghandler;
            this.counter = trafficcounter;
        }

        public void run() {
            if (this.counter.monitorActive.get()) {
                long i = System.currentTimeMillis();

                this.counter.resetAccounting(i);
                if (this.trafficShapingHandler1 != null) {
                    this.trafficShapingHandler1.doAccounting(this.counter);
                }

                this.counter.scheduledFuture = this.counter.executor.schedule(this, this.counter.checkInterval.get(), TimeUnit.MILLISECONDS);
            }
        }
    }
}
