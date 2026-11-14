package io.netty.util;

import java.util.Arrays;

public class ResourceLeakException extends RuntimeException {

    private static final long serialVersionUID = 7186453858343358280L;
    private final StackTraceElement[] cachedStackTrace = this.getStackTrace();

    public ResourceLeakException() {}

    public ResourceLeakException(String s) {
        super(s);
    }

    public ResourceLeakException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ResourceLeakException(Throwable throwable) {
        super(throwable);
    }

    public int hashCode() {
        StackTraceElement[] astacktraceelement = this.cachedStackTrace;
        int i = 0;
        StackTraceElement[] astacktraceelement1 = astacktraceelement;
        int j = astacktraceelement.length;

        for (int k = 0; k < j; ++k) {
            StackTraceElement stacktraceelement = astacktraceelement1[k];

            i = i * 31 + stacktraceelement.hashCode();
        }

        return i;
    }

    public boolean equals(Object object) {
        return !(object instanceof ResourceLeakException) ? false : (object == this ? true : Arrays.equals(this.cachedStackTrace, ((ResourceLeakException) object).cachedStackTrace));
    }
}
