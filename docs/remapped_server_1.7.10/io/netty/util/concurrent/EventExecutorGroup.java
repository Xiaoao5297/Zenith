package io.netty.util.concurrent;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public interface EventExecutorGroup extends ScheduledExecutorService, Iterable<EventExecutor> {

    boolean isShuttingDown();

    Future<?> shutdownGracefully();

    Future<?> shutdownGracefully(long i, long j, TimeUnit timeunit);

    Future<?> terminationFuture();

    /** @deprecated */
    @Deprecated
    void shutdown();

    /** @deprecated */
    @Deprecated
    List<Runnable> shutdownNow();

    EventExecutor next();

    Iterator<EventExecutor> iterator();

    Future<?> submit(Runnable runnable);

    <T> Future<T> submit(Runnable runnable, T t0);

    <T> Future<T> submit(Callable<T> callable);

    ScheduledFuture<?> schedule(Runnable runnable, long i, TimeUnit timeunit);

    <V> ScheduledFuture<V> schedule(Callable<V> callable, long i, TimeUnit timeunit);

    ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long i, long j, TimeUnit timeunit);

    ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long i, long j, TimeUnit timeunit);
}
