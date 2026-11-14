package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
public class ExecutionError extends Error {

    private static final long serialVersionUID = 0L;

    protected ExecutionError() {}

    protected ExecutionError(@Nullable String s) {
        super(s);
    }

    public ExecutionError(@Nullable String s, @Nullable Error error) {
        super(s, error);
    }

    public ExecutionError(@Nullable Error error) {
        super(error);
    }
}
