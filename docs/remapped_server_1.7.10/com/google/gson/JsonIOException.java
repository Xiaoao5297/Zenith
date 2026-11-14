package com.google.gson;

public final class JsonIOException extends JsonParseException {

    private static final long serialVersionUID = 1L;

    public JsonIOException(String s) {
        super(s);
    }

    public JsonIOException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public JsonIOException(Throwable throwable) {
        super(throwable);
    }
}
