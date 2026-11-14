package io.netty.handler.ssl;

import javax.net.ssl.SSLException;

public class NotSslRecordException extends SSLException {

    private static final long serialVersionUID = -4316784434770656841L;

    public NotSslRecordException() {
        super("");
    }

    public NotSslRecordException(String s) {
        super(s);
    }

    public NotSslRecordException(Throwable throwable) {
        super(throwable);
    }

    public NotSslRecordException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
