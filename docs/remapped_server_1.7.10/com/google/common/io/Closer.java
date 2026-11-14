package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import javax.annotation.Nullable;

@Beta
public final class Closer implements Closeable {

    private static final Closer.Suppressor SUPPRESSOR = Closer.SuppressingSuppressor.isAvailable() ? Closer.SuppressingSuppressor.INSTANCE : Closer.LoggingSuppressor.INSTANCE;
    @VisibleForTesting
    final Closer.Suppressor suppressor;
    private final Deque<Closeable> stack = new ArrayDeque(4);
    private Throwable thrown;

    public static Closer create() {
        return new Closer(Closer.SUPPRESSOR);
    }

    @VisibleForTesting
    Closer(Closer.Suppressor closer_suppressor) {
        this.suppressor = (Closer.Suppressor) Preconditions.checkNotNull(closer_suppressor);
    }

    public <C extends Closeable> C register(@Nullable C c0) {
        if (c0 != null) {
            this.stack.push(c0);
        }

        return c0;
    }

    public RuntimeException rethrow(Throwable throwable) throws IOException {
        Preconditions.checkNotNull(throwable);
        this.thrown = throwable;
        Throwables.propagateIfPossible(throwable, IOException.class);
        throw new RuntimeException(throwable);
    }

    public <X extends Exception> RuntimeException rethrow(Throwable throwable, Class<X> oclass) throws IOException, X {
        Preconditions.checkNotNull(throwable);
        this.thrown = throwable;
        Throwables.propagateIfPossible(throwable, IOException.class);
        Throwables.propagateIfPossible(throwable, oclass);
        throw new RuntimeException(throwable);
    }

    public <X1 extends Exception, X2 extends Exception> RuntimeException rethrow(Throwable throwable, Class<X1> oclass, Class<X2> oclass1) throws IOException, X1, X2 {
        Preconditions.checkNotNull(throwable);
        this.thrown = throwable;
        Throwables.propagateIfPossible(throwable, IOException.class);
        Throwables.propagateIfPossible(throwable, oclass, oclass1);
        throw new RuntimeException(throwable);
    }

    public void close() throws IOException {
        Throwable throwable = this.thrown;

        while (!this.stack.isEmpty()) {
            Closeable closeable = (Closeable) this.stack.pop();

            try {
                closeable.close();
            } catch (Throwable throwable1) {
                if (throwable == null) {
                    throwable = throwable1;
                } else {
                    this.suppressor.suppress(closeable, throwable, throwable1);
                }
            }
        }

        if (this.thrown == null && throwable != null) {
            Throwables.propagateIfPossible(throwable, IOException.class);
            throw new AssertionError(throwable);
        }
    }

    @VisibleForTesting
    static final class SuppressingSuppressor implements Closer.Suppressor {

        static final Closer.SuppressingSuppressor INSTANCE = new Closer.SuppressingSuppressor();
        static final Method addSuppressed = getAddSuppressed();

        SuppressingSuppressor() {}

        static boolean isAvailable() {
            return Closer.SuppressingSuppressor.addSuppressed != null;
        }

        private static Method getAddSuppressed() {
            try {
                return Throwable.class.getMethod("addSuppressed", Throwable.class);
            } catch (Throwable throwable) {
                return null;
            }
        }

        @Override
        public void suppress(Closeable closeable, Throwable throwable, Throwable throwable1) {
            if (throwable != throwable1) {
                try {
                    Closer.SuppressingSuppressor.addSuppressed.invoke(throwable, throwable1);
                } catch (Throwable throwable2) {
                    Closer.LoggingSuppressor.INSTANCE.suppress(closeable, throwable, throwable1);
                }

            }
        }
    }

    @VisibleForTesting
    static final class LoggingSuppressor implements Closer.Suppressor {

        static final Closer.LoggingSuppressor INSTANCE = new Closer.LoggingSuppressor();

        LoggingSuppressor() {}

        @Override
        public void suppress(Closeable closeable, Throwable throwable, Throwable throwable1) {
            Closeables.logger.log(Level.WARNING, "Suppressing exception thrown when closing " + closeable, throwable1);
        }
    }

    @VisibleForTesting
    interface Suppressor {

        void suppress(Closeable closeable, Throwable throwable, Throwable throwable1);
    }
}
