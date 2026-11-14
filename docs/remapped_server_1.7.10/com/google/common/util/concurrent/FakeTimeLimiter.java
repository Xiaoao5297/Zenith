package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Beta
public final class FakeTimeLimiter implements TimeLimiter {

    public FakeTimeLimiter() {}

    @Override
    public <T> T newProxy(T t0, Class<T> oclass, long i, TimeUnit timeunit) {
        Preconditions.checkNotNull(t0);
        Preconditions.checkNotNull(oclass);
        Preconditions.checkNotNull(timeunit);
        return t0;
    }

    @Override
    public <T> T callWithTimeout(Callable<T> callable, long i, TimeUnit timeunit, boolean flag) throws Exception {
        Preconditions.checkNotNull(timeunit);
        return callable.call();
    }
}
