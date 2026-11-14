package com.google.common.util.concurrent;

import javax.annotation.Nullable;

public class UncheckedTimeoutException extends RuntimeException {

    private static final long serialVersionUID = 0L;

    public UncheckedTimeoutException() {}

    public UncheckedTimeoutException(@Nullable String s) {
        super(s);
    }

    public UncheckedTimeoutException(@Nullable Throwable throwable) {
        super(throwable);
    }

    public UncheckedTimeoutException(@Nullable String s, @Nullable Throwable throwable) {
        super(s, throwable);
    }
}
