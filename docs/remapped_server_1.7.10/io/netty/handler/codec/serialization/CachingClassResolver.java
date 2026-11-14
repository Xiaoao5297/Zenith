package io.netty.handler.codec.serialization;

import java.util.Map;

class CachingClassResolver implements ClassResolver {

    private final Map<String, Class<?>> classCache;
    private final ClassResolver delegate;

    CachingClassResolver(ClassResolver classresolver, Map<String, Class<?>> map) {
        this.delegate = classresolver;
        this.classCache = map;
    }

    @Override
    public Class<?> resolve(String s) throws ClassNotFoundException {
        Class oclass = (Class) this.classCache.get(s);

        if (oclass != null) {
            return oclass;
        } else {
            oclass = this.delegate.resolve(s);
            this.classCache.put(s, oclass);
            return oclass;
        }
    }
}
