package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.ConcurrentMap;

public final class AttributeKey<T> extends UniqueName {

    private static final ConcurrentMap<String, Boolean> names = PlatformDependent.newConcurrentHashMap();

    public AttributeKey(String s) {
        super(AttributeKey.names, s);
    }
}
