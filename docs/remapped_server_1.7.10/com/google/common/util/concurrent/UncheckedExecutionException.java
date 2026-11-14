package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
public class UncheckedExecutionException extends RuntimeException {

    private static final long serialVersionUID = 0L;

    protected UncheckedExecutionException() {}

    protected UncheckedExecutionException(@Nullable String s) {
        super(s);
    }

    public UncheckedExecutionException(@Nullable String s, @Nullable Throwable throwable) {
        super(s, throwable);
    }

    public UncheckedExecutionException(@Nullable Throwable throwable) {
        super(throwable);
    }
}
