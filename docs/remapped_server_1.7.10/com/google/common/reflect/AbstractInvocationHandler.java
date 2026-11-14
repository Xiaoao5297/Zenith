package com.google.common.reflect;

import com.google.common.annotations.Beta;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import javax.annotation.Nullable;

@Beta
public abstract class AbstractInvocationHandler implements InvocationHandler {

    private static final Object[] NO_ARGS = new Object[0];

    public AbstractInvocationHandler() {}

    public final Object invoke(Object object, Method method, @Nullable Object[] aobject) throws Throwable {
        if (aobject == null) {
            aobject = AbstractInvocationHandler.NO_ARGS;
        }

        if (aobject.length == 0 && method.getName().equals("hashCode")) {
            return this.hashCode();
        } else if (aobject.length == 1 && method.getName().equals("equals") && method.getParameterTypes()[0] == Object.class) {
            Object object1 = aobject[0];

            return object1 == null ? false : (object == object1 ? true : isProxyOfSameInterfaces(object1, object.getClass()) && this.equals(Proxy.getInvocationHandler(object1)));
        } else {
            return aobject.length == 0 && method.getName().equals("toString") ? this.toString() : this.handleInvocation(object, method, aobject);
        }
    }

    protected abstract Object handleInvocation(Object object, Method method, Object[] aobject) throws Throwable;

    public boolean equals(Object object) {
        return super.equals(object);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        return super.toString();
    }

    private static boolean isProxyOfSameInterfaces(Object object, Class<?> oclass) {
        return oclass.isInstance(object) || Proxy.isProxyClass(object.getClass()) && Arrays.equals(object.getClass().getInterfaces(), oclass.getInterfaces());
    }
}
