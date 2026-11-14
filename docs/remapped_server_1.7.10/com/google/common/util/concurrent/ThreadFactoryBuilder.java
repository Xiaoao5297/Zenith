package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public final class ThreadFactoryBuilder {

    private String nameFormat = null;
    private Boolean daemon = null;
    private Integer priority = null;
    private UncaughtExceptionHandler uncaughtExceptionHandler = null;
    private ThreadFactory backingThreadFactory = null;

    public ThreadFactoryBuilder() {}

    public ThreadFactoryBuilder setNameFormat(String s) {
        String.format(s, 0);
        this.nameFormat = s;
        return this;
    }

    public ThreadFactoryBuilder setDaemon(boolean flag) {
        this.daemon = flag;
        return this;
    }

    public ThreadFactoryBuilder setPriority(int i) {
        Preconditions.checkArgument(i >= 1, "Thread priority (%s) must be >= %s", i, 1);
        Preconditions.checkArgument(i <= 10, "Thread priority (%s) must be <= %s", i, 10);
        this.priority = i;
        return this;
    }

    public ThreadFactoryBuilder setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtexceptionhandler) {
        this.uncaughtExceptionHandler = (UncaughtExceptionHandler) Preconditions.checkNotNull(uncaughtexceptionhandler);
        return this;
    }

    public ThreadFactoryBuilder setThreadFactory(ThreadFactory threadfactory) {
        this.backingThreadFactory = (ThreadFactory) Preconditions.checkNotNull(threadfactory);
        return this;
    }

    public ThreadFactory build() {
        return build(this);
    }

    private static ThreadFactory build(ThreadFactoryBuilder threadfactorybuilder) {
        final String s = threadfactorybuilder.nameFormat;
        final Boolean obool = threadfactorybuilder.daemon;
        final Integer integer = threadfactorybuilder.priority;
        final UncaughtExceptionHandler uncaughtexceptionhandler = threadfactorybuilder.uncaughtExceptionHandler;
        final ThreadFactory threadfactory = threadfactorybuilder.backingThreadFactory != null ? threadfactorybuilder.backingThreadFactory : Executors.defaultThreadFactory();
        final AtomicLong atomiclong = s != null ? new AtomicLong(0L) : null;

        return new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread thread = threadfactory.newThread(runnable);

                if (s != null) {
                    thread.setName(String.format(s, atomiclong.getAndIncrement()));
                }

                if (obool != null) {
                    thread.setDaemon(obool);
                }

                if (integer != null) {
                    thread.setPriority(integer);
                }

                if (uncaughtexceptionhandler != null) {
                    thread.setUncaughtExceptionHandler(uncaughtexceptionhandler);
                }

                return thread;
            }
        };
    }
}
