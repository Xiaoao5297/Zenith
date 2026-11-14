package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

public final class Callables {

    private Callables() {}

    public static <T> Callable<T> returning(@Nullable final T t0) {
        return new Callable<T>() {
            public T call() {
                return t0;
            }
        };
    }

    static <T> Callable<T> threadRenaming(final Callable<T> callable, final Supplier<String> supplier) {
        Preconditions.checkNotNull(supplier);
        Preconditions.checkNotNull(callable);
        return new Callable<T>() {
            public T call() throws Exception {
                Thread thread = Thread.currentThread();
                String s = thread.getName();
                boolean flag = Callables.trySetName((String) supplier.get(), thread);

                Object object;

                try {
                    object = callable.call();
                } finally {
                    if (flag) {
                        Callables.trySetName(s, thread);
                    }

                }

                return object;
            }
        };
    }

    static Runnable threadRenaming(final Runnable runnable, final Supplier<String> supplier) {
        Preconditions.checkNotNull(supplier);
        Preconditions.checkNotNull(runnable);
        return new Runnable() {
            public void run() {
                Thread thread = Thread.currentThread();
                String s = thread.getName();
                boolean flag = Callables.trySetName((String) supplier.get(), thread);

                try {
                    runnable.run();
                } finally {
                    if (flag) {
                        Callables.trySetName(s, thread);
                    }

                }

            }
        };
    }

    private static boolean trySetName(String s, Thread thread) {
        try {
            thread.setName(s);
            return true;
        } catch (SecurityException securityexception) {
            return false;
        }
    }
}
