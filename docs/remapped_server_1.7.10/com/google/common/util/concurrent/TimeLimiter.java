package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Beta
public interface TimeLimiter {

    <T> T newProxy(T t0, Class<T> oclass, long i, TimeUnit timeunit);

    <T> T callWithTimeout(Callable<T> callable, long i, TimeUnit timeunit, boolean flag) throws Exception;
}
