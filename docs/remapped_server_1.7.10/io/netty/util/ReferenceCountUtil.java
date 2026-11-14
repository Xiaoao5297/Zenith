package io.netty.util;

public final class ReferenceCountUtil {

    public static <T> T retain(T t0) {
        return t0 instanceof ReferenceCounted ? ((ReferenceCounted) t0).retain() : t0;
    }

    public static <T> T retain(T t0, int i) {
        return t0 instanceof ReferenceCounted ? ((ReferenceCounted) t0).retain(i) : t0;
    }

    public static boolean release(Object object) {
        return object instanceof ReferenceCounted ? ((ReferenceCounted) object).release() : false;
    }

    public static boolean release(Object object, int i) {
        return object instanceof ReferenceCounted ? ((ReferenceCounted) object).release(i) : false;
    }

    private ReferenceCountUtil() {}
}
