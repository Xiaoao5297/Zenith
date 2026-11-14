package com.google.common.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

abstract class WrappingScheduledExecutorService extends WrappingExecutorService implements ScheduledExecutorService {

    final ScheduledExecutorService delegate;

    protected WrappingScheduledExecutorService(ScheduledExecutorService scheduledexecutorservice) {
        super(scheduledexecutorservice);
        this.delegate = scheduledexecutorservice;
    }

    public final ScheduledFuture<?> schedule(Runnable runnable, long i, TimeUnit timeunit) {
        return this.delegate.schedule(this.wrapTask(runnable), i, timeunit);
    }

    public final <V> ScheduledFuture<V> schedule(Callable<V> callable, long i, TimeUnit timeunit) {
        return this.delegate.schedule(this.wrapTask(callable), i, timeunit);
    }

    public final ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long i, long j, TimeUnit timeunit) {
        return this.delegate.scheduleAtFixedRate(this.wrapTask(runnable), i, j, timeunit);
    }

    public final ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long i, long j, TimeUnit timeunit) {
        return this.delegate.scheduleWithFixedDelay(this.wrapTask(runnable), i, j, timeunit);
    }
}
