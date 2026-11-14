package io.netty.handler.codec.serialization;

class ClassLoaderClassResolver implements ClassResolver {

    private final ClassLoader classLoader;

    ClassLoaderClassResolver(ClassLoader classloader) {
        this.classLoader = classloader;
    }

    @Override
    public Class<?> resolve(String s) throws ClassNotFoundException {
        try {
            return this.classLoader.loadClass(s);
        } catch (ClassNotFoundException classnotfoundexception) {
            return Class.forName(s, false, this.classLoader);
        }
    }
}
