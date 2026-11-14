package io.netty.util.concurrent;

import io.netty.util.Signal;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public class DefaultPromise<V> extends AbstractFuture<V> implements Promise<V> {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultPromise.class);
    private static final int MAX_LISTENER_STACK_DEPTH = 8;
    private static final ThreadLocal<Integer> LISTENER_STACK_DEPTH = new ThreadLocal<Integer>() {
        protected Integer initialValue() {
            return 0;
        }
    };
    private static final Signal SUCCESS = new Signal(DefaultPromise.class.getName() + ".SUCCESS");
    private static final Signal UNCANCELLABLE = new Signal(DefaultPromise.class.getName() + ".UNCANCELLABLE");
    private final EventExecutor executor;
    private volatile Object result;
    private Object listeners;
    private short waiters;

    public DefaultPromise(EventExecutor eventexecutor) {
        if (eventexecutor == null) {
            throw new NullPointerException("executor");
        } else {
            this.executor = eventexecutor;
        }
    }

    protected DefaultPromise() {
        this.executor = null;
    }

    protected EventExecutor executor() {
        return this.executor;
    }

    public boolean isCancelled() {
        return isCancelled0(this.result);
    }

    private static boolean isCancelled0(Object object) {
        return object instanceof DefaultPromise.CauseHolder && ((DefaultPromise.CauseHolder) object).cause instanceof CancellationException;
    }

    @Override
    public boolean isCancellable() {
        return this.result == null;
    }

    public boolean isDone() {
        return isDone0(this.result);
    }

    private static boolean isDone0(Object object) {
        return object != null && object != DefaultPromise.UNCANCELLABLE;
    }

    @Override
    public boolean isSuccess() {
        Object object = this.result;

        return object != null && object != DefaultPromise.UNCANCELLABLE ? !(object instanceof DefaultPromise.CauseHolder) : false;
    }

    @Override
    public Throwable cause() {
        Object object = this.result;

        return object instanceof DefaultPromise.CauseHolder ? ((DefaultPromise.CauseHolder) object).cause : null;
    }

    @Override
    public Promise<V> addListener(GenericFutureListener<? extends Future<? super V>> genericfuturelistener) {
        if (genericfuturelistener == null) {
            throw new NullPointerException("listener");
        } else if (this.isDone()) {
            notifyListener(this.executor(), this, genericfuturelistener);
            return this;
        } else {
            synchronized (this) {
                if (!this.isDone()) {
                    if (this.listeners == null) {
                        this.listeners = genericfuturelistener;
                    } else if (this.listeners instanceof DefaultFutureListeners) {
                        ((DefaultFutureListeners) this.listeners).add(genericfuturelistener);
                    } else {
                        GenericFutureListener genericfuturelistener1 = (GenericFutureListener) this.listeners;

                        this.listeners = new DefaultFutureListeners(genericfuturelistener1, genericfuturelistener);
                    }

                    return this;
                }
            }

            notifyListener(this.executor(), this, genericfuturelistener);
            return this;
        }
    }

    @Override
    public Promise<V> addListeners(GenericFutureListener<? extends Future<? super V>>... agenericfuturelistener) {
        if (agenericfuturelistener == null) {
            throw new NullPointerException("listeners");
        } else {
            GenericFutureListener[] agenericfuturelistener1 = agenericfuturelistener;
            int i = agenericfuturelistener.length;

            for (int j = 0; j < i; ++j) {
                GenericFutureListener genericfuturelistener = agenericfuturelistener1[j];

                if (genericfuturelistener == null) {
                    break;
                }

                this.addListener(genericfuturelistener);
            }

            return this;
        }
    }

    @Override
    public Promise<V> removeListener(GenericFutureListener<? extends Future<? super V>> genericfuturelistener) {
        if (genericfuturelistener == null) {
            throw new NullPointerException("listener");
        } else if (this.isDone()) {
            return this;
        } else {
            synchronized (this) {
                if (!this.isDone()) {
                    if (this.listeners instanceof DefaultFutureListeners) {
                        ((DefaultFutureListeners) this.listeners).remove(genericfuturelistener);
                    } else if (this.listeners == genericfuturelistener) {
                        this.listeners = null;
                    }
                }

                return this;
            }
        }
    }

    @Override
    public Promise<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... agenericfuturelistener) {
        if (agenericfuturelistener == null) {
            throw new NullPointerException("listeners");
        } else {
            GenericFutureListener[] agenericfuturelistener1 = agenericfuturelistener;
            int i = agenericfuturelistener.length;

            for (int j = 0; j < i; ++j) {
                GenericFutureListener genericfuturelistener = agenericfuturelistener1[j];

                if (genericfuturelistener == null) {
                    break;
                }

                this.removeListener(genericfuturelistener);
            }

            return this;
        }
    }

    @Override
    public Promise<V> sync() throws InterruptedException {
        this.await();
        this.rethrowIfFailed();
        return this;
    }

    @Override
    public Promise<V> syncUninterruptibly() {
        this.awaitUninterruptibly();
        this.rethrowIfFailed();
        return this;
    }

    private void rethrowIfFailed() {
        Throwable throwable = this.cause();

        if (throwable != null) {
            PlatformDependent.throwException(throwable);
        }
    }

    @Override
    public Promise<V> await() throws InterruptedException {
        if (this.isDone()) {
            return this;
        } else if (Thread.interrupted()) {
            throw new InterruptedException(this.toString());
        } else {
            synchronized (this) {
                while (!this.isDone()) {
                    this.checkDeadLock();
                    this.incWaiters();

                    try {
                        this.wait();
                    } finally {
                        this.decWaiters();
                    }
                }

                return this;
            }
        }
    }

    @Override
    public boolean await(long i, TimeUnit timeunit) throws InterruptedException {
        return this.await0(timeunit.toNanos(i), true);
    }

    @Override
    public boolean await(long i) throws InterruptedException {
        return this.await0(TimeUnit.MILLISECONDS.toNanos(i), true);
    }

    @Override
    public Promise<V> awaitUninterruptibly() {
        if (this.isDone()) {
            return this;
        } else {
            boolean flag = false;

            synchronized (this) {
                while (!this.isDone()) {
                    this.checkDeadLock();
                    this.incWaiters();

                    try {
                        this.wait();
                    } catch (InterruptedException interruptedexception) {
                        flag = true;
                    } finally {
                        this.decWaiters();
                    }
                }
            }

            if (flag) {
                Thread.currentThread().interrupt();
            }

            return this;
        }
    }

    @Override
    public boolean awaitUninterruptibly(long i, TimeUnit timeunit) {
        try {
            return this.await0(timeunit.toNanos(i), false);
        } catch (InterruptedException interruptedexception) {
            throw new InternalError();
        }
    }

    @Override
    public boolean awaitUninterruptibly(long i) {
        try {
            return this.await0(TimeUnit.MILLISECONDS.toNanos(i), false);
        } catch (InterruptedException interruptedexception) {
            throw new InternalError();
        }
    }

    private boolean await0(long i, boolean flag) throws InterruptedException {
        if (this.isDone()) {
            return true;
        } else if (i <= 0L) {
            return this.isDone();
        } else if (flag && Thread.interrupted()) {
            throw new InterruptedException(this.toString());
        } else {
            long j = i <= 0L ? 0L : System.nanoTime();
            long k = i;
            boolean flag1 = false;

            try {
                synchronized (this) {
                    boolean flag2;

                    if (this.isDone()) {
                        flag2 = true;
                        return flag2;
                    } else if (k <= 0L) {
                        flag2 = this.isDone();
                        return flag2;
                    } else {
                        this.checkDeadLock();
                        this.incWaiters();

                        while (true) {
                            try {
                                InterruptedException interruptedexception;

                                try {
                                    this.wait(k / 1000000L, (int) (k % 1000000L));
                                } catch (InterruptedException interruptedexception1) {
                                    interruptedexception = interruptedexception1;
                                    if (flag) {
                                        throw interruptedexception1;
                                    }

                                    flag1 = true;
                                }

                                if (!this.isDone()) {
                                    k = i - (System.nanoTime() - j);
                                    if (k <= 0L) {
                                        interruptedexception = this.isDone();
                                        return (boolean) interruptedexception;
                                    }
                                } else {
                                    interruptedexception = true;
                                    return (boolean) interruptedexception;
                                }
                            } finally {
                                this.decWaiters();
                            }
                        }
                    }
                }
            } finally {
                if (flag1) {
                    Thread.currentThread().interrupt();
                }

            }
        }
    }

    protected void checkDeadLock() {
        EventExecutor eventexecutor = this.executor();

        if (eventexecutor != null && eventexecutor.inEventLoop()) {
            throw new BlockingOperationException(this.toString());
        }
    }

    @Override
    public Promise<V> setSuccess(V v0) {
        if (this.setSuccess0(v0)) {
            this.notifyListeners();
            return this;
        } else {
            throw new IllegalStateException("complete already: " + this);
        }
    }

    @Override
    public boolean trySuccess(V v0) {
        if (this.setSuccess0(v0)) {
            this.notifyListeners();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Promise<V> setFailure(Throwable throwable) {
        if (this.setFailure0(throwable)) {
            this.notifyListeners();
            return this;
        } else {
            throw new IllegalStateException("complete already: " + this, throwable);
        }
    }

    @Override
    public boolean tryFailure(Throwable throwable) {
        if (this.setFailure0(throwable)) {
            this.notifyListeners();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean cancel(boolean flag) {
        Object object = this.result;

        if (!isDone0(object) && object != DefaultPromise.UNCANCELLABLE) {
            synchronized (this) {
                object = this.result;
                if (isDone0(object) || object == DefaultPromise.UNCANCELLABLE) {
                    return false;
                }

                this.result = new DefaultPromise.CauseHolder(new CancellationException());
                if (this.hasWaiters()) {
                    this.notifyAll();
                }
            }

            this.notifyListeners();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean setUncancellable() {
        Object object = this.result;

        if (isDone0(object)) {
            return false;
        } else {
            synchronized (this) {
                object = this.result;
                if (isDone0(object)) {
                    return false;
                } else {
                    this.result = DefaultPromise.UNCANCELLABLE;
                    return true;
                }
            }
        }
    }

    private boolean setFailure0(Throwable throwable) {
        if (this.isDone()) {
            return false;
        } else {
            synchronized (this) {
                if (this.isDone()) {
                    return false;
                } else {
                    this.result = new DefaultPromise.CauseHolder(throwable);
                    if (this.hasWaiters()) {
                        this.notifyAll();
                    }

                    return true;
                }
            }
        }
    }

    private boolean setSuccess0(V v0) {
        if (this.isDone()) {
            return false;
        } else {
            synchronized (this) {
                if (this.isDone()) {
                    return false;
                } else {
                    if (v0 == null) {
                        this.result = DefaultPromise.SUCCESS;
                    } else {
                        this.result = v0;
                    }

                    if (this.hasWaiters()) {
                        this.notifyAll();
                    }

                    return true;
                }
            }
        }
    }

    @Override
    public V getNow() {
        Object object = this.result;

        return !(object instanceof DefaultPromise.CauseHolder) && object != DefaultPromise.SUCCESS ? object : null;
    }

    private boolean hasWaiters() {
        return this.waiters > 0;
    }

    private void incWaiters() {
        if (this.waiters == 32767) {
            throw new IllegalStateException("too many waiters: " + this);
        } else {
            ++this.waiters;
        }
    }

    private void decWaiters() {
        --this.waiters;
    }

    private void notifyListeners() {
        Object object = this.listeners;

        if (object != null) {
            this.listeners = null;
            EventExecutor eventexecutor = this.executor();

            if (eventexecutor.inEventLoop()) {
                Integer integer = (Integer) DefaultPromise.LISTENER_STACK_DEPTH.get();

                if (integer < 8) {
                    DefaultPromise.LISTENER_STACK_DEPTH.set(integer + 1);

                    try {
                        if (object instanceof DefaultFutureListeners) {
                            notifyListeners0(this, (DefaultFutureListeners) object);
                        } else {
                            GenericFutureListener genericfuturelistener = (GenericFutureListener) object;

                            notifyListener0(this, genericfuturelistener);
                        }
                    } finally {
                        DefaultPromise.LISTENER_STACK_DEPTH.set(integer);
                    }

                    return;
                }
            }

            try {
                if (object instanceof DefaultFutureListeners) {
                    final DefaultFutureListeners defaultfuturelisteners = (DefaultFutureListeners) object;

                    eventexecutor.execute(new Runnable() {
                        public void run() {
                            DefaultPromise.notifyListeners0(DefaultPromise.this, defaultfuturelisteners);
                        }
                    });
                } else {
                    final GenericFutureListener genericfuturelistener1 = (GenericFutureListener) object;

                    eventexecutor.execute(new Runnable() {
                        public void run() {
                            DefaultPromise.notifyListener0(DefaultPromise.this, genericfuturelistener1);
                        }
                    });
                }
            } catch (Throwable throwable) {
                DefaultPromise.logger.error("Failed to notify listener(s). Event loop shut down?", throwable);
            }

        }
    }

    private static void notifyListeners0(Future<?> future, DefaultFutureListeners defaultfuturelisteners) {
        GenericFutureListener[] agenericfuturelistener = defaultfuturelisteners.listeners();
        int i = defaultfuturelisteners.size();

        for (int j = 0; j < i; ++j) {
            notifyListener0(future, agenericfuturelistener[j]);
        }

    }

    protected static void notifyListener(EventExecutor eventexecutor, final Future<?> future, final GenericFutureListener<?> genericfuturelistener) {
        if (eventexecutor.inEventLoop()) {
            Integer integer = (Integer) DefaultPromise.LISTENER_STACK_DEPTH.get();

            if (integer < 8) {
                DefaultPromise.LISTENER_STACK_DEPTH.set(integer + 1);

                try {
                    notifyListener0(future, genericfuturelistener);
                } finally {
                    DefaultPromise.LISTENER_STACK_DEPTH.set(integer);
                }

                return;
            }
        }

        try {
            eventexecutor.execute(new Runnable() {
                public void run() {
                    DefaultPromise.notifyListener0(future, genericfuturelistener);
                }
            });
        } catch (Throwable throwable) {
            DefaultPromise.logger.error("Failed to notify a listener. Event loop shut down?", throwable);
        }

    }

    static void notifyListener0(Future future, GenericFutureListener genericfuturelistener) {
        try {
            genericfuturelistener.operationComplete(future);
        } catch (Throwable throwable) {
            if (DefaultPromise.logger.isWarnEnabled()) {
                DefaultPromise.logger.warn("An exception was thrown by " + genericfuturelistener.getClass().getName() + ".operationComplete()", throwable);
            }
        }

    }

    private synchronized Object progressiveListeners() {
        Object object = this.listeners;

        if (object == null) {
            return null;
        } else if (object instanceof DefaultFutureListeners) {
            DefaultFutureListeners defaultfuturelisteners = (DefaultFutureListeners) object;
            int i = defaultfuturelisteners.progressiveSize();
            GenericFutureListener[] agenericfuturelistener;
            int j;

            switch (i) {
                case 0:
                    return null;
                case 1:
                    agenericfuturelistener = defaultfuturelisteners.listeners();
                    int k = agenericfuturelistener.length;

                    for (j = 0; j < k; ++j) {
                        GenericFutureListener genericfuturelistener = agenericfuturelistener[j];

                        if (genericfuturelistener instanceof GenericProgressiveFutureListener) {
                            return genericfuturelistener;
                        }
                    }

                    return null;
                default:
                    agenericfuturelistener = defaultfuturelisteners.listeners();
                    GenericProgressiveFutureListener[] agenericprogressivefuturelistener = new GenericProgressiveFutureListener[i];

                    j = 0;

                    for (int l = 0; l < i; ++j) {
                        GenericFutureListener genericfuturelistener1 = agenericfuturelistener[j];

                        if (genericfuturelistener1 instanceof GenericProgressiveFutureListener) {
                            agenericprogressivefuturelistener[l++] = (GenericProgressiveFutureListener) genericfuturelistener1;
                        }
                    }

                    return agenericprogressivefuturelistener;
            }
        } else {
            return object instanceof GenericProgressiveFutureListener ? object : null;
        }
    }

    void notifyProgressiveListeners(final long i, final long j) {
        Object object = this.progressiveListeners();

        if (object != null) {
            final ProgressiveFuture progressivefuture = (ProgressiveFuture) this;
            EventExecutor eventexecutor = this.executor();

            if (eventexecutor.inEventLoop()) {
                if (object instanceof GenericProgressiveFutureListener[]) {
                    notifyProgressiveListeners0(progressivefuture, (GenericProgressiveFutureListener[]) ((GenericProgressiveFutureListener[]) object), i, j);
                } else {
                    notifyProgressiveListener0(progressivefuture, (GenericProgressiveFutureListener) object, i, j);
                }
            } else {
                try {
                    if (object instanceof GenericProgressiveFutureListener[]) {
                        final GenericProgressiveFutureListener[] agenericprogressivefuturelistener = (GenericProgressiveFutureListener[]) ((GenericProgressiveFutureListener[]) object);

                        eventexecutor.execute(new Runnable() {
                            public void run() {
                                DefaultPromise.notifyProgressiveListeners0(progressivefuture, agenericprogressivefuturelistener, i, j);
                            }
                        });
                    } else {
                        final GenericProgressiveFutureListener genericprogressivefuturelistener = (GenericProgressiveFutureListener) object;

                        eventexecutor.execute(new Runnable() {
                            public void run() {
                                DefaultPromise.notifyProgressiveListener0(progressivefuture, genericprogressivefuturelistener, i, j);
                            }
                        });
                    }
                } catch (Throwable throwable) {
                    DefaultPromise.logger.error("Failed to notify listener(s). Event loop shut down?", throwable);
                }
            }

        }
    }

    private static void notifyProgressiveListeners0(ProgressiveFuture<?> progressivefuture, GenericProgressiveFutureListener<?>[] agenericprogressivefuturelistener, long i, long j) {
        GenericProgressiveFutureListener[] agenericprogressivefuturelistener1 = agenericprogressivefuturelistener;
        int k = agenericprogressivefuturelistener.length;

        for (int l = 0; l < k; ++l) {
            GenericProgressiveFutureListener genericprogressivefuturelistener = agenericprogressivefuturelistener1[l];

            if (genericprogressivefuturelistener == null) {
                break;
            }

            notifyProgressiveListener0(progressivefuture, genericprogressivefuturelistener, i, j);
        }

    }

    private static void notifyProgressiveListener0(ProgressiveFuture progressivefuture, GenericProgressiveFutureListener genericprogressivefuturelistener, long i, long j) {
        try {
            genericprogressivefuturelistener.operationProgressed(progressivefuture, i, j);
        } catch (Throwable throwable) {
            if (DefaultPromise.logger.isWarnEnabled()) {
                DefaultPromise.logger.warn("An exception was thrown by " + genericprogressivefuturelistener.getClass().getName() + ".operationProgressed()", throwable);
            }
        }

    }

    public String toString() {
        return this.toStringBuilder().toString();
    }

    protected StringBuilder toStringBuilder() {
        StringBuilder stringbuilder = new StringBuilder(64);

        stringbuilder.append(StringUtil.simpleClassName((Object) this));
        stringbuilder.append('@');
        stringbuilder.append(Integer.toHexString(this.hashCode()));
        Object object = this.result;

        if (object == DefaultPromise.SUCCESS) {
            stringbuilder.append("(success)");
        } else if (object == DefaultPromise.UNCANCELLABLE) {
            stringbuilder.append("(uncancellable)");
        } else if (object instanceof DefaultPromise.CauseHolder) {
            stringbuilder.append("(failure(");
            stringbuilder.append(((DefaultPromise.CauseHolder) object).cause);
            stringbuilder.append(')');
        } else {
            stringbuilder.append("(incomplete)");
        }

        return stringbuilder;
    }

    private static final class CauseHolder {

        final Throwable cause;

        private CauseHolder(Throwable throwable) {
            this.cause = throwable;
        }
    }
}
