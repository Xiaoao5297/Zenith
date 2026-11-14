package io.netty.handler.codec;

import io.netty.util.Signal;

public class DecoderResult {

    protected static final Signal SIGNAL_UNFINISHED = new Signal(DecoderResult.class.getName() + ".UNFINISHED");
    protected static final Signal SIGNAL_SUCCESS = new Signal(DecoderResult.class.getName() + ".SUCCESS");
    public static final DecoderResult UNFINISHED = new DecoderResult(DecoderResult.SIGNAL_UNFINISHED);
    public static final DecoderResult SUCCESS = new DecoderResult(DecoderResult.SIGNAL_SUCCESS);
    private final Throwable cause;

    public static DecoderResult failure(Throwable throwable) {
        if (throwable == null) {
            throw new NullPointerException("cause");
        } else {
            return new DecoderResult(throwable);
        }
    }

    protected DecoderResult(Throwable throwable) {
        if (throwable == null) {
            throw new NullPointerException("cause");
        } else {
            this.cause = throwable;
        }
    }

    public boolean isFinished() {
        return this.cause != DecoderResult.SIGNAL_UNFINISHED;
    }

    public boolean isSuccess() {
        return this.cause == DecoderResult.SIGNAL_SUCCESS;
    }

    public boolean isFailure() {
        return this.cause != DecoderResult.SIGNAL_SUCCESS && this.cause != DecoderResult.SIGNAL_UNFINISHED;
    }

    public Throwable cause() {
        return this.isFailure() ? this.cause : null;
    }

    public String toString() {
        if (this.isFinished()) {
            if (this.isSuccess()) {
                return "success";
            } else {
                String s = this.cause().toString();
                StringBuilder stringbuilder = new StringBuilder(s.length() + 17);

                stringbuilder.append("failure(");
                stringbuilder.append(s);
                stringbuilder.append(')');
                return stringbuilder.toString();
            }
        } else {
            return "unfinished";
        }
    }
}
