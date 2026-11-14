package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
@Beta
public abstract class RateLimiter {

    private final RateLimiter.SleepingTicker ticker;
    private final long offsetNanos;
    double storedPermits;
    double maxPermits;
    volatile double stableIntervalMicros;
    private final Object mutex;
    private long nextFreeTicketMicros;

    public static RateLimiter create(double d0) {
        return create(RateLimiter.SleepingTicker.SYSTEM_TICKER, d0);
    }

    @VisibleForTesting
    static RateLimiter create(RateLimiter.SleepingTicker ratelimiter_sleepingticker, double d0) {
        RateLimiter.Bursty ratelimiter_bursty = new RateLimiter.Bursty(ratelimiter_sleepingticker, 1.0D);

        ratelimiter_bursty.setRate(d0);
        return ratelimiter_bursty;
    }

    public static RateLimiter create(double d0, long i, TimeUnit timeunit) {
        return create(RateLimiter.SleepingTicker.SYSTEM_TICKER, d0, i, timeunit);
    }

    @VisibleForTesting
    static RateLimiter create(RateLimiter.SleepingTicker ratelimiter_sleepingticker, double d0, long i, TimeUnit timeunit) {
        RateLimiter.WarmingUp ratelimiter_warmingup = new RateLimiter.WarmingUp(ratelimiter_sleepingticker, i, timeunit);

        ratelimiter_warmingup.setRate(d0);
        return ratelimiter_warmingup;
    }

    @VisibleForTesting
    static RateLimiter createWithCapacity(RateLimiter.SleepingTicker ratelimiter_sleepingticker, double d0, long i, TimeUnit timeunit) {
        double d1 = (double) timeunit.toNanos(i) / 1.0E9D;
        RateLimiter.Bursty ratelimiter_bursty = new RateLimiter.Bursty(ratelimiter_sleepingticker, d1);

        ratelimiter_bursty.setRate(d0);
        return ratelimiter_bursty;
    }

    private RateLimiter(RateLimiter.SleepingTicker ratelimiter_sleepingticker) {
        this.mutex = new Object();
        this.nextFreeTicketMicros = 0L;
        this.ticker = ratelimiter_sleepingticker;
        this.offsetNanos = ratelimiter_sleepingticker.read();
    }

    public final void setRate(double d0) {
        Preconditions.checkArgument(d0 > 0.0D && !Double.isNaN(d0), "rate must be positive");
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.resync(this.readSafeMicros());
            double d1 = (double) TimeUnit.SECONDS.toMicros(1L) / d0;

            this.stableIntervalMicros = d1;
            this.doSetRate(d0, d1);
        }
    }

    abstract void doSetRate(double d0, double d1);

    public final double getRate() {
        return (double) TimeUnit.SECONDS.toMicros(1L) / this.stableIntervalMicros;
    }

    public double acquire() {
        return this.acquire(1);
    }

    public double acquire(int i) {
        checkPermits(i);
        Object object = this.mutex;
        long j;

        synchronized (this.mutex) {
            j = this.reserveNextTicket((double) i, this.readSafeMicros());
        }

        this.ticker.sleepMicrosUninterruptibly(j);
        return 1.0D * (double) j / (double) TimeUnit.SECONDS.toMicros(1L);
    }

    public boolean tryAcquire(long i, TimeUnit timeunit) {
        return this.tryAcquire(1, i, timeunit);
    }

    public boolean tryAcquire(int i) {
        return this.tryAcquire(i, 0L, TimeUnit.MICROSECONDS);
    }

    public boolean tryAcquire() {
        return this.tryAcquire(1, 0L, TimeUnit.MICROSECONDS);
    }

    public boolean tryAcquire(int i, long j, TimeUnit timeunit) {
        long k = timeunit.toMicros(j);

        checkPermits(i);
        Object object = this.mutex;
        long l;

        synchronized (this.mutex) {
            long i1 = this.readSafeMicros();

            if (this.nextFreeTicketMicros > i1 + k) {
                return false;
            }

            l = this.reserveNextTicket((double) i, i1);
        }

        this.ticker.sleepMicrosUninterruptibly(l);
        return true;
    }

    private static void checkPermits(int i) {
        Preconditions.checkArgument(i > 0, "Requested permits must be positive");
    }

    private long reserveNextTicket(double d0, long i) {
        this.resync(i);
        long j = this.nextFreeTicketMicros - i;
        double d1 = Math.min(d0, this.storedPermits);
        double d2 = d0 - d1;
        long k = this.storedPermitsToWaitTime(this.storedPermits, d1) + (long) (d2 * this.stableIntervalMicros);

        this.nextFreeTicketMicros += k;
        this.storedPermits -= d1;
        return j;
    }

    abstract long storedPermitsToWaitTime(double d0, double d1);

    private void resync(long i) {
        if (i > this.nextFreeTicketMicros) {
            this.storedPermits = Math.min(this.maxPermits, this.storedPermits + (double) (i - this.nextFreeTicketMicros) / this.stableIntervalMicros);
            this.nextFreeTicketMicros = i;
        }

    }

    private long readSafeMicros() {
        return TimeUnit.NANOSECONDS.toMicros(this.ticker.read() - this.offsetNanos);
    }

    public String toString() {
        return String.format("RateLimiter[stableRate=%3.1fqps]", 1000000.0D / this.stableIntervalMicros);
    }

    @VisibleForTesting
    abstract static class SleepingTicker extends Ticker {

        static final RateLimiter.SleepingTicker SYSTEM_TICKER = new RateLimiter.SleepingTicker() {
            @Override
            public long read() {
                return systemTicker().read();
            }

            @Override
            public void sleepMicrosUninterruptibly(long i) {
                if (i > 0L) {
                    Uninterruptibles.sleepUninterruptibly(i, TimeUnit.MICROSECONDS);
                }

            }
        };

        SleepingTicker() {}

        abstract void sleepMicrosUninterruptibly(long i);
    }

    private static class Bursty extends RateLimiter {

        final double maxBurstSeconds;

        Bursty(RateLimiter.SleepingTicker ratelimiter_sleepingticker, double d0) {
            super(ratelimiter_sleepingticker, null);
            this.maxBurstSeconds = d0;
        }

        @Override
        void doSetRate(double d0, double d1) {
            double d2 = this.maxPermits;

            this.maxPermits = this.maxBurstSeconds * d0;
            this.storedPermits = d2 == 0.0D ? 0.0D : this.storedPermits * this.maxPermits / d2;
        }

        @Override
        long storedPermitsToWaitTime(double d0, double d1) {
            return 0L;
        }
    }

    private static class WarmingUp extends RateLimiter {

        final long warmupPeriodMicros;
        private double slope;
        private double halfPermits;

        WarmingUp(RateLimiter.SleepingTicker ratelimiter_sleepingticker, long i, TimeUnit timeunit) {
            super(ratelimiter_sleepingticker, null);
            this.warmupPeriodMicros = timeunit.toMicros(i);
        }

        @Override
        void doSetRate(double d0, double d1) {
            double d2 = this.maxPermits;

            this.maxPermits = (double) this.warmupPeriodMicros / d1;
            this.halfPermits = this.maxPermits / 2.0D;
            double d3 = d1 * 3.0D;

            this.slope = (d3 - d1) / this.halfPermits;
            if (d2 == Double.POSITIVE_INFINITY) {
                this.storedPermits = 0.0D;
            } else {
                this.storedPermits = d2 == 0.0D ? this.maxPermits : this.storedPermits * this.maxPermits / d2;
            }

        }

        @Override
        long storedPermitsToWaitTime(double d0, double d1) {
            double d2 = d0 - this.halfPermits;
            long i = 0L;

            if (d2 > 0.0D) {
                double d3 = Math.min(d2, d1);

                i = (long) (d3 * (this.permitsToTime(d2) + this.permitsToTime(d2 - d3)) / 2.0D);
                d1 -= d3;
            }

            i = (long) ((double) i + this.stableIntervalMicros * d1);
            return i;
        }

        private double permitsToTime(double d0) {
            return this.stableIntervalMicros + d0 * this.slope;
        }
    }
}
