package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.ConcurrentMap;

public final class Signal extends Error {

    private static final long serialVersionUID = -221145131122459977L;
    private static final ConcurrentMap<String, Boolean> map = PlatformDependent.newConcurrentHashMap();
    private final UniqueName uname;

    public Signal(String s) {
        super(s);
        this.uname = new UniqueName(Signal.map, s, new Object[0]);
    }

    public void expect(Signal signal) {
        if (this != signal) {
            throw new IllegalStateException("unexpected signal: " + signal);
        }
    }

    public Throwable initCause(Throwable throwable) {
        return this;
    }

    public Throwable fillInStackTrace() {
        return this;
    }

    public String toString() {
        return this.uname.name();
    }
}
