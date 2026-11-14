package com.google.gson;

public class JsonParseException extends RuntimeException {

    static final long serialVersionUID = -4086729973971783390L;

    public JsonParseException(String s) {
        super(s);
    }

    public JsonParseException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public JsonParseException(Throwable throwable) {
        super(throwable);
    }
}
