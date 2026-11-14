package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@Beta
public final class Reflection {

    public static String getPackageName(Class<?> oclass) {
        return getPackageName(oclass.getName());
    }

    public static String getPackageName(String s) {
        int i = s.lastIndexOf(46);

        return i < 0 ? "" : s.substring(0, i);
    }

    public static void initialize(Class<?>... aclass) {
        Class[] aclass1 = aclass;
        int i = aclass.length;

        for (int j = 0; j < i; ++j) {
            Class oclass = aclass1[j];

            try {
                Class.forName(oclass.getName(), true, oclass.getClassLoader());
            } catch (ClassNotFoundException classnotfoundexception) {
                throw new AssertionError(classnotfoundexception);
            }
        }

    }

    public static <T> T newProxy(Class<T> oclass, InvocationHandler invocationhandler) {
        Preconditions.checkNotNull(invocationhandler);
        Preconditions.checkArgument(oclass.isInterface(), "%s is not an interface", oclass);
        Object object = Proxy.newProxyInstance(oclass.getClassLoader(), new Class[]{oclass}, invocationhandler);

        return oclass.cast(object);
    }

    private Reflection() {}
}
