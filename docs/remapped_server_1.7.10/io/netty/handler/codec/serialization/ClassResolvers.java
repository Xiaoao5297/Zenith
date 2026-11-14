package io.netty.handler.codec.serialization;

import io.netty.util.internal.PlatformDependent;
import java.util.HashMap;

public final class ClassResolvers {

    public static ClassResolver cacheDisabled(ClassLoader classloader) {
        return new ClassLoaderClassResolver(defaultClassLoader(classloader));
    }

    public static ClassResolver weakCachingResolver(ClassLoader classloader) {
        return new CachingClassResolver(new ClassLoaderClassResolver(defaultClassLoader(classloader)), new WeakReferenceMap<>(new HashMap()));
    }

    public static ClassResolver softCachingResolver(ClassLoader classloader) {
        return new CachingClassResolver(new ClassLoaderClassResolver(defaultClassLoader(classloader)), new SoftReferenceMap<>(new HashMap()));
    }

    public static ClassResolver weakCachingConcurrentResolver(ClassLoader classloader) {
        return new CachingClassResolver(new ClassLoaderClassResolver(defaultClassLoader(classloader)), new WeakReferenceMap<>(PlatformDependent.newConcurrentHashMap()));
    }

    public static ClassResolver softCachingConcurrentResolver(ClassLoader classloader) {
        return new CachingClassResolver(new ClassLoaderClassResolver(defaultClassLoader(classloader)), new SoftReferenceMap<>(PlatformDependent.newConcurrentHashMap()));
    }

    static ClassLoader defaultClassLoader(ClassLoader classloader) {
        if (classloader != null) {
            return classloader;
        } else {
            ClassLoader classloader1 = Thread.currentThread().getContextClassLoader();

            return classloader1 != null ? classloader1 : ClassResolvers.class.getClassLoader();
        }
    }

    private ClassResolvers() {}
}
