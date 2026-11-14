package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public final class SimpleTimeLimiter implements TimeLimiter {

    private final ExecutorService executor;

    public SimpleTimeLimiter(ExecutorService executorservice) {
        this.executor = (ExecutorService) Preconditions.checkNotNull(executorservice);
    }

    public SimpleTimeLimiter() {
        this(Executors.newCachedThreadPool());
    }

    @Override
    public <T> T newProxy(final T t0, Class<T> oclass, final long i, final TimeUnit timeunit) {
        Preconditions.checkNotNull(t0);
        Preconditions.checkNotNull(oclass);
        Preconditions.checkNotNull(timeunit);
        Preconditions.checkArgument(i > 0L, "bad timeout: " + i);
        Preconditions.checkArgument(oclass.isInterface(), "interfaceType must be an interface type");
        final Set set = findInterruptibleMethods(oclass);
        InvocationHandler invocationhandler = new InvocationHandler() {
            public Object invoke(Object object, final Method method, final Object[] aobject) throws Throwable {
                Callable callable = new Callable<Object>() {
                    public Object call() throws Exception {
                        try {
                            return method.invoke(t0, aobject);
                        } catch (InvocationTargetException invocationtargetexception) {
                            SimpleTimeLimiter.throwCause(invocationtargetexception, false);
                            throw new AssertionError("can't get here");
                        }
                    }
                };

                return SimpleTimeLimiter.this.callWithTimeout(callable, i, timeunit, set.contains(method));
            }
        };

        return newProxy(oclass, invocationhandler);
    }

    @Override
    public <T> T callWithTimeout(Callable<T> callable, long i, TimeUnit timeunit, boolean flag) throws Exception {
        Preconditions.checkNotNull(callable);
        Preconditions.checkNotNull(timeunit);
        Preconditions.checkArgument(i > 0L, "timeout must be positive: %s", i);
        Future future = this.executor.submit(callable);

        try {
            if (flag) {
                try {
                    return future.get(i, timeunit);
                } catch (InterruptedException interruptedexception) {
                    future.cancel(true);
                    throw interruptedexception;
                }
            } else {
                return Uninterruptibles.getUninterruptibly(future, i, timeunit);
            }
        } catch (ExecutionException executionexception) {
            throw throwCause(executionexception, true);
        } catch (TimeoutException timeoutexception) {
            future.cancel(true);
            throw new UncheckedTimeoutException(timeoutexception);
        }
    }

    private static Exception throwCause(Exception exception, boolean flag) throws Exception {
        Throwable throwable = exception.getCause();

        if (throwable == null) {
            throw exception;
        } else {
            if (flag) {
                StackTraceElement[] astacktraceelement = (StackTraceElement[]) ObjectArrays.concat(throwable.getStackTrace(), exception.getStackTrace(), StackTraceElement.class);

                throwable.setStackTrace(astacktraceelement);
            }

            if (throwable instanceof Exception) {
                throw (Exception) throwable;
            } else if (throwable instanceof Error) {
                throw (Error) throwable;
            } else {
                throw exception;
            }
        }
    }

    private static Set<Method> findInterruptibleMethods(Class<?> oclass) {
        HashSet hashset = Sets.newHashSet();
        Method[] amethod = oclass.getMethods();
        int i = amethod.length;

        for (int j = 0; j < i; ++j) {
            Method method = amethod[j];

            if (declaresInterruptedEx(method)) {
                hashset.add(method);
            }
        }

        return hashset;
    }

    private static boolean declaresInterruptedEx(Method method) {
        Class[] aclass = method.getExceptionTypes();
        int i = aclass.length;

        for (int j = 0; j < i; ++j) {
            Class oclass = aclass[j];

            if (oclass == InterruptedException.class) {
                return true;
            }
        }

        return false;
    }

    private static <T> T newProxy(Class<T> oclass, InvocationHandler invocationhandler) {
        Object object = Proxy.newProxyInstance(oclass.getClassLoader(), new Class[]{oclass}, invocationhandler);

        return oclass.cast(object);
    }
}
