package io.netty.util;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueName implements Comparable<UniqueName> {

    private static final AtomicInteger nextId = new AtomicInteger();
    private final int id;
    private final String name;

    public UniqueName(ConcurrentMap<String, Boolean> concurrentmap, String s, Object... aobject) {
        if (concurrentmap == null) {
            throw new NullPointerException("map");
        } else if (s == null) {
            throw new NullPointerException("name");
        } else {
            if (aobject != null && aobject.length > 0) {
                this.validateArgs(aobject);
            }

            if (concurrentmap.putIfAbsent(s, Boolean.TRUE) != null) {
                throw new IllegalArgumentException(String.format("'%s' is already in use", s));
            } else {
                this.id = UniqueName.nextId.incrementAndGet();
                this.name = s;
            }
        }
    }

    protected void validateArgs(Object... aobject) {}

    public final String name() {
        return this.name;
    }

    public final int id() {
        return this.id;
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public final boolean equals(Object object) {
        return super.equals(object);
    }

    public int compareTo(UniqueName uniquename) {
        if (this == uniquename) {
            return 0;
        } else {
            int i = this.name.compareTo(uniquename.name);

            return i != 0 ? i : Integer.valueOf(this.id).compareTo(uniquename.id);
        }
    }

    public String toString() {
        return this.name();
    }
}
