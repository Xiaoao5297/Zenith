package com.google.gson.stream;

import java.io.IOException;

public final class MalformedJsonException extends IOException {

    private static final long serialVersionUID = 1L;

    public MalformedJsonException(String s) {
        super(s);
    }

    public MalformedJsonException(String s, Throwable throwable) {
        super(s);
        this.initCause(throwable);
    }

    public MalformedJsonException(Throwable throwable) {
        this.initCause(throwable);
    }
}
