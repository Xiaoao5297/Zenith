package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Beta
public interface ListeningScheduledExecutorService extends ScheduledExecutorService, ListeningExecutorService {

    ListenableScheduledFuture<?> schedule(Runnable runnable, long i, TimeUnit timeunit);

    <V> ListenableScheduledFuture<V> schedule(Callable<V> callable, long i, TimeUnit timeunit);

    ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long i, long j, TimeUnit timeunit);

    ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long i, long j, TimeUnit timeunit);
}
