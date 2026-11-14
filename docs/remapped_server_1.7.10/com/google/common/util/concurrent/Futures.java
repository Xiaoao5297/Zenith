package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

@Beta
public final class Futures {

    private static final AsyncFunction<ListenableFuture<Object>, Object> DEREFERENCER = new AsyncFunction<ListenableFuture<Object>, Object>() {
        public ListenableFuture<Object> apply(ListenableFuture<Object> listenablefuture) {
            return listenablefuture;
        }
    };
    private static final Ordering<Constructor<?>> WITH_STRING_PARAM_FIRST = Ordering.natural().onResultOf(new Function<Constructor<?>, Boolean>() {
        public Boolean apply(Constructor<?> constructor) {
            return Arrays.asList(constructor.getParameterTypes()).contains(String.class);
        }
    }).reverse();

    private Futures() {}

    public static <V, X extends Exception> CheckedFuture<V, X> makeChecked(ListenableFuture<V> listenablefuture, Function<Exception, X> function) {
        return new Futures.MappingCheckedFuture<>((ListenableFuture) Preconditions.checkNotNull(listenablefuture), function);
    }

    public static <V> ListenableFuture<V> immediateFuture(@Nullable V v0) {
        return new Futures.ImmediateSuccessfulFuture<>(v0);
    }

    public static <V, X extends Exception> CheckedFuture<V, X> immediateCheckedFuture(@Nullable V v0) {
        return new Futures.ImmediateSuccessfulCheckedFuture<>(v0);
    }

    public static <V> ListenableFuture<V> immediateFailedFuture(Throwable throwable) {
        Preconditions.checkNotNull(throwable);
        return new Futures.ImmediateFailedFuture<>(throwable);
    }

    public static <V> ListenableFuture<V> immediateCancelledFuture() {
        return new Futures.ImmediateCancelledFuture<>();
    }

    public static <V, X extends Exception> CheckedFuture<V, X> immediateFailedCheckedFuture(X x0) {
        Preconditions.checkNotNull(x0);
        return new Futures.ImmediateFailedCheckedFuture<>(x0);
    }

    public static <V> ListenableFuture<V> withFallback(ListenableFuture<? extends V> listenablefuture, FutureFallback<? extends V> futurefallback) {
        return withFallback(listenablefuture, futurefallback, MoreExecutors.sameThreadExecutor());
    }

    public static <V> ListenableFuture<V> withFallback(ListenableFuture<? extends V> listenablefuture, FutureFallback<? extends V> futurefallback, Executor executor) {
        Preconditions.checkNotNull(futurefallback);
        return new Futures.FallbackFuture<>(listenablefuture, futurefallback, executor);
    }

    public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> listenablefuture, AsyncFunction<? super I, ? extends O> asyncfunction) {
        return transform(listenablefuture, asyncfunction, MoreExecutors.sameThreadExecutor());
    }

    public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> listenablefuture, AsyncFunction<? super I, ? extends O> asyncfunction, Executor executor) {
        Futures.ChainingListenableFuture futures_chaininglistenablefuture = new Futures.ChainingListenableFuture<>(asyncfunction, listenablefuture);

        listenablefuture.addListener(futures_chaininglistenablefuture, executor);
        return futures_chaininglistenablefuture;
    }

    public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> listenablefuture, Function<? super I, ? extends O> function) {
        return transform(listenablefuture, function, MoreExecutors.sameThreadExecutor());
    }

    public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> listenablefuture, final Function<? super I, ? extends O> function, Executor executor) {
        Preconditions.checkNotNull(function);
        AsyncFunction asyncfunction = new AsyncFunction<I, O>() {
            @Override
            public ListenableFuture<O> apply(I i0) {
                Object object = function.apply(i0);

                return Futures.immediateFuture(object);
            }
        };

        return transform(listenablefuture, asyncfunction, executor);
    }

    public static <I, O> Future<O> lazyTransform(final Future<I> future, final Function<? super I, ? extends O> function) {
        Preconditions.checkNotNull(future);
        Preconditions.checkNotNull(function);
        return new Future<O>() {
            public boolean cancel(boolean flag) {
                return future.cancel(flag);
            }

            public boolean isCancelled() {
                return future.isCancelled();
            }

            public boolean isDone() {
                return future.isDone();
            }

            public O get() throws InterruptedException, ExecutionException {
                return this.applyTransformation(future.get());
            }

            public O get(long i, TimeUnit timeunit) throws InterruptedException, ExecutionException, TimeoutException {
                return this.applyTransformation(future.get(i, timeunit));
            }

            private O applyTransformation(I i0) throws ExecutionException {
                try {
                    return function.apply(i0);
                } catch (Throwable throwable) {
                    throw new ExecutionException(throwable);
                }
            }
        };
    }

    public static <V> ListenableFuture<V> dereference(ListenableFuture<? extends ListenableFuture<? extends V>> listenablefuture) {
        return transform(listenablefuture, Futures.DEREFERENCER);
    }

    @Beta
    public static <V> ListenableFuture<List<V>> allAsList(ListenableFuture<? extends V>... alistenablefuture) {
        return listFuture(ImmutableList.copyOf((Object[]) alistenablefuture), true, MoreExecutors.sameThreadExecutor());
    }

    @Beta
    public static <V> ListenableFuture<List<V>> allAsList(Iterable<? extends ListenableFuture<? extends V>> iterable) {
        return listFuture(ImmutableList.copyOf(iterable), true, MoreExecutors.sameThreadExecutor());
    }

    public static <V> ListenableFuture<V> nonCancellationPropagating(ListenableFuture<V> listenablefuture) {
        return new Futures.NonCancellationPropagatingFuture<>(listenablefuture);
    }

    @Beta
    public static <V> ListenableFuture<List<V>> successfulAsList(ListenableFuture<? extends V>... alistenablefuture) {
        return listFuture(ImmutableList.copyOf((Object[]) alistenablefuture), false, MoreExecutors.sameThreadExecutor());
    }

    @Beta
    public static <V> ListenableFuture<List<V>> successfulAsList(Iterable<? extends ListenableFuture<? extends V>> iterable) {
        return listFuture(ImmutableList.copyOf(iterable), false, MoreExecutors.sameThreadExecutor());
    }

    public static <V> void addCallback(ListenableFuture<V> listenablefuture, FutureCallback<? super V> futurecallback) {
        addCallback(listenablefuture, futurecallback, MoreExecutors.sameThreadExecutor());
    }

    public static <V> void addCallback(final ListenableFuture<V> listenablefuture, final FutureCallback<? super V> futurecallback, Executor executor) {
        Preconditions.checkNotNull(futurecallback);
        Runnable runnable = new Runnable() {
            public void run() {
                Object object;

                try {
                    object = Uninterruptibles.getUninterruptibly(listenablefuture);
                } catch (ExecutionException executionexception) {
                    futurecallback.onFailure(executionexception.getCause());
                    return;
                } catch (RuntimeException runtimeexception) {
                    futurecallback.onFailure(runtimeexception);
                    return;
                } catch (Error error) {
                    futurecallback.onFailure(error);
                    return;
                }

                futurecallback.onSuccess(object);
            }
        };

        listenablefuture.addListener(runnable, executor);
    }

    public static <V, X extends Exception> V get(Future<V> future, Class<X> oclass) throws X {
        Preconditions.checkNotNull(future);
        Preconditions.checkArgument(!RuntimeException.class.isAssignableFrom(oclass), "Futures.get exception type (%s) must not be a RuntimeException", oclass);

        try {
            return future.get();
        } catch (InterruptedException interruptedexception) {
            Thread.currentThread().interrupt();
            throw newWithCause(oclass, interruptedexception);
        } catch (ExecutionException executionexception) {
            wrapAndThrowExceptionOrError(executionexception.getCause(), oclass);
            throw new AssertionError();
        }
    }

    public static <V, X extends Exception> V get(Future<V> future, long i, TimeUnit timeunit, Class<X> oclass) throws X {
        Preconditions.checkNotNull(future);
        Preconditions.checkNotNull(timeunit);
        Preconditions.checkArgument(!RuntimeException.class.isAssignableFrom(oclass), "Futures.get exception type (%s) must not be a RuntimeException", oclass);

        try {
            return future.get(i, timeunit);
        } catch (InterruptedException interruptedexception) {
            Thread.currentThread().interrupt();
            throw newWithCause(oclass, interruptedexception);
        } catch (TimeoutException timeoutexception) {
            throw newWithCause(oclass, timeoutexception);
        } catch (ExecutionException executionexception) {
            wrapAndThrowExceptionOrError(executionexception.getCause(), oclass);
            throw new AssertionError();
        }
    }

    private static <X extends Exception> void wrapAndThrowExceptionOrError(Throwable throwable, Class<X> oclass) throws X {
        if (throwable instanceof Error) {
            throw new ExecutionError((Error) throwable);
        } else if (throwable instanceof RuntimeException) {
            throw new UncheckedExecutionException(throwable);
        } else {
            throw newWithCause(oclass, throwable);
        }
    }

    public static <V> V getUnchecked(Future<V> future) {
        Preconditions.checkNotNull(future);

        try {
            return Uninterruptibles.getUninterruptibly(future);
        } catch (ExecutionException executionexception) {
            wrapAndThrowUnchecked(executionexception.getCause());
            throw new AssertionError();
        }
    }

    private static void wrapAndThrowUnchecked(Throwable throwable) {
        if (throwable instanceof Error) {
            throw new ExecutionError((Error) throwable);
        } else {
            throw new UncheckedExecutionException(throwable);
        }
    }

    private static <X extends Exception> X newWithCause(Class<X> oclass, Throwable throwable) {
        List list = Arrays.asList(oclass.getConstructors());
        Iterator iterator = preferringStrings(list).iterator();

        Exception exception;

        do {
            if (!iterator.hasNext()) {
                throw new IllegalArgumentException("No appropriate constructor for exception of type " + oclass + " in response to chained exception", throwable);
            }

            Constructor constructor = (Constructor) iterator.next();

            exception = (Exception) newFromConstructor(constructor, throwable);
        } while (exception == null);

        if (exception.getCause() == null) {
            exception.initCause(throwable);
        }

        return exception;
    }

    private static <X extends Exception> List<Constructor<X>> preferringStrings(List<Constructor<X>> list) {
        return Futures.WITH_STRING_PARAM_FIRST.sortedCopy(list);
    }

    @Nullable
    private static <X> X newFromConstructor(Constructor<X> constructor, Throwable throwable) {
        Class[] aclass = constructor.getParameterTypes();
        Object[] aobject = new Object[aclass.length];

        for (int i = 0; i < aclass.length; ++i) {
            Class oclass = aclass[i];

            if (oclass.equals(String.class)) {
                aobject[i] = throwable.toString();
            } else {
                if (!oclass.equals(Throwable.class)) {
                    return null;
                }

                aobject[i] = throwable;
            }
        }

        try {
            return constructor.newInstance(aobject);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        } catch (InstantiationException instantiationexception) {
            return null;
        } catch (IllegalAccessException illegalaccessexception) {
            return null;
        } catch (InvocationTargetException invocationtargetexception) {
            return null;
        }
    }

    private static <V> ListenableFuture<List<V>> listFuture(ImmutableList<ListenableFuture<? extends V>> immutablelist, boolean flag, Executor executor) {
        return new Futures.CombinedFuture<>(immutablelist, flag, executor, new Futures.FutureCombiner<V, List<V>>() {
            @Override
            public List<V> combine(List<Optional<V>> list) {
                ArrayList arraylist = Lists.newArrayList();
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    Optional optional = (Optional) iterator.next();

                    arraylist.add(optional != null ? optional.orNull() : null);
                }

                return Collections.unmodifiableList(arraylist);
            }
        });
    }

    private static class MappingCheckedFuture<V, X extends Exception> extends AbstractCheckedFuture<V, X> {

        final Function<Exception, X> mapper;

        MappingCheckedFuture(ListenableFuture<V> listenablefuture, Function<Exception, X> function) {
            super(listenablefuture);
            this.mapper = (Function) Preconditions.checkNotNull(function);
        }

        @Override
        protected X mapException(Exception exception) {
            return (Exception) this.mapper.apply(exception);
        }
    }

    private static class CombinedFuture<V, C> extends AbstractFuture<C> {

        private static final Logger logger = Logger.getLogger(Futures.CombinedFuture.class.getName());
        ImmutableCollection<? extends ListenableFuture<? extends V>> futures;
        final boolean allMustSucceed;
        final AtomicInteger remaining;
        Futures.FutureCombiner<V, C> combiner;
        List<Optional<V>> values;
        final Object seenExceptionsLock = new Object();
        Set<Throwable> seenExceptions;

        CombinedFuture(ImmutableCollection<? extends ListenableFuture<? extends V>> immutablecollection, boolean flag, Executor executor, Futures.FutureCombiner<V, C> futures_futurecombiner) {
            this.futures = immutablecollection;
            this.allMustSucceed = flag;
            this.remaining = new AtomicInteger(immutablecollection.size());
            this.combiner = futures_futurecombiner;
            this.values = Lists.newArrayListWithCapacity(immutablecollection.size());
            this.init(executor);
        }

        protected void init(Executor executor) {
            this.addListener(new Runnable() {
                public void run() {
                    if (CombinedFuture.this.isCancelled()) {
                        Iterator iterator = CombinedFuture.this.futures.iterator();

                        while (iterator.hasNext()) {
                            ListenableFuture listenablefuture = (ListenableFuture) iterator.next();

                            listenablefuture.cancel(CombinedFuture.this.wasInterrupted());
                        }
                    }

                    CombinedFuture.this.futures = null;
                    CombinedFuture.this.values = null;
                    CombinedFuture.this.combiner = null;
                }
            }, MoreExecutors.sameThreadExecutor());
            if (this.futures.isEmpty()) {
                this.set(this.combiner.combine(ImmutableList.of()));
            } else {
                int i;

                for (i = 0; i < this.futures.size(); ++i) {
                    this.values.add((Object) null);
                }

                i = 0;
                Iterator iterator = this.futures.iterator();

                while (iterator.hasNext()) {
                    final ListenableFuture listenablefuture = (ListenableFuture) iterator.next();
                    final int j = i++;

                    listenablefuture.addListener(new Runnable() {
                        public void run() {
                            CombinedFuture.this.setOneValue(j, listenablefuture);
                        }
                    }, executor);
                }

            }
        }

        private void setExceptionAndMaybeLog(Throwable throwable) {
            boolean flag = false;
            boolean flag1 = true;

            if (this.allMustSucceed) {
                flag = super.setException(throwable);
                Object object = this.seenExceptionsLock;

                synchronized (this.seenExceptionsLock) {
                    if (this.seenExceptions == null) {
                        this.seenExceptions = Sets.newHashSet();
                    }

                    flag1 = this.seenExceptions.add(throwable);
                }
            }

            if (throwable instanceof Error || this.allMustSucceed && !flag && flag1) {
                Futures.CombinedFuture.logger.log(Level.SEVERE, "input future failed.", throwable);
            }

        }

        private void setOneValue(int i, Future<? extends V> future) {
            List list = this.values;

            if (this.isDone() || list == null) {
                Preconditions.checkState(this.allMustSucceed || this.isCancelled(), "Future was done before all dependencies completed");
            }

            boolean flag = false;

            int j;
            Futures.FutureCombiner futures_futurecombiner;
            label303:
            {
                label304:
                {
                    label305:
                    {
                        try {
                            flag = true;
                            Preconditions.checkState(future.isDone(), "Tried to set value from future which is not done");
                            Object object = Uninterruptibles.getUninterruptibly(future);

                            if (list != null) {
                                list.set(i, Optional.fromNullable(object));
                                flag = false;
                            } else {
                                flag = false;
                            }
                            break label303;
                        } catch (CancellationException cancellationexception) {
                            if (this.allMustSucceed) {
                                this.cancel(false);
                                flag = false;
                            } else {
                                flag = false;
                            }
                            break label304;
                        } catch (ExecutionException executionexception) {
                            this.setExceptionAndMaybeLog(executionexception.getCause());
                            flag = false;
                            break label305;
                        } catch (Throwable throwable) {
                            this.setExceptionAndMaybeLog(throwable);
                            flag = false;
                        } finally {
                            if (flag) {
                                int k = this.remaining.decrementAndGet();

                                Preconditions.checkState(k >= 0, "Less than 0 remaining futures");
                                if (k == 0) {
                                    Futures.FutureCombiner futures_futurecombiner1 = this.combiner;

                                    if (futures_futurecombiner1 != null && list != null) {
                                        this.set(futures_futurecombiner1.combine(list));
                                    } else {
                                        Preconditions.checkState(this.isDone());
                                    }
                                }

                            }
                        }

                        j = this.remaining.decrementAndGet();
                        Preconditions.checkState(j >= 0, "Less than 0 remaining futures");
                        if (j == 0) {
                            futures_futurecombiner = this.combiner;
                            if (futures_futurecombiner != null && list != null) {
                                this.set(futures_futurecombiner.combine(list));
                            } else {
                                Preconditions.checkState(this.isDone());
                            }

                            return;
                        }

                        return;
                    }

                    j = this.remaining.decrementAndGet();
                    Preconditions.checkState(j >= 0, "Less than 0 remaining futures");
                    if (j == 0) {
                        futures_futurecombiner = this.combiner;
                        if (futures_futurecombiner != null && list != null) {
                            this.set(futures_futurecombiner.combine(list));
                        } else {
                            Preconditions.checkState(this.isDone());
                        }

                        return;
                    }

                    return;
                }

                j = this.remaining.decrementAndGet();
                Preconditions.checkState(j >= 0, "Less than 0 remaining futures");
                if (j == 0) {
                    futures_futurecombiner = this.combiner;
                    if (futures_futurecombiner != null && list != null) {
                        this.set(futures_futurecombiner.combine(list));
                    } else {
                        Preconditions.checkState(this.isDone());
                    }

                    return;
                }

                return;
            }

            j = this.remaining.decrementAndGet();
            Preconditions.checkState(j >= 0, "Less than 0 remaining futures");
            if (j == 0) {
                futures_futurecombiner = this.combiner;
                if (futures_futurecombiner != null && list != null) {
                    this.set(futures_futurecombiner.combine(list));
                } else {
                    Preconditions.checkState(this.isDone());
                }
            }

        }
    }

    private interface FutureCombiner<V, C> {

        C combine(List<Optional<V>> list);
    }

    private static class NonCancellationPropagatingFuture<V> extends AbstractFuture<V> {

        NonCancellationPropagatingFuture(final ListenableFuture<V> listenablefuture) {
            Preconditions.checkNotNull(listenablefuture);
            Futures.addCallback(listenablefuture, new FutureCallback<V>() {
                @Override
                public void onSuccess(V v0) {
                    NonCancellationPropagatingFuture.this.set(v0);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    if (listenablefuture.isCancelled()) {
                        NonCancellationPropagatingFuture.this.cancel(false);
                    } else {
                        NonCancellationPropagatingFuture.this.setException(throwable);
                    }

                }
            }, MoreExecutors.sameThreadExecutor());
        }
    }

    private static class ChainingListenableFuture<I, O> extends AbstractFuture<O> implements Runnable {

        private AsyncFunction<? super I, ? extends O> function;
        private ListenableFuture<? extends I> inputFuture;
        private volatile ListenableFuture<? extends O> outputFuture;
        private final CountDownLatch outputCreated;

        private ChainingListenableFuture(AsyncFunction<? super I, ? extends O> asyncfunction, ListenableFuture<? extends I> listenablefuture) {
            this.outputCreated = new CountDownLatch(1);
            this.function = (AsyncFunction) Preconditions.checkNotNull(asyncfunction);
            this.inputFuture = (ListenableFuture) Preconditions.checkNotNull(listenablefuture);
        }

        @Override
        public boolean cancel(boolean flag) {
            if (super.cancel(flag)) {
                this.cancel(this.inputFuture, flag);
                this.cancel(this.outputFuture, flag);
                return true;
            } else {
                return false;
            }
        }

        private void cancel(@Nullable Future<?> future, boolean flag) {
            if (future != null) {
                future.cancel(flag);
            }

        }

        public void run() {
            try {
                Object object;

                try {
                    object = Uninterruptibles.getUninterruptibly(this.inputFuture);
                } catch (CancellationException cancellationexception) {
                    this.cancel(false);
                    return;
                } catch (ExecutionException executionexception) {
                    this.setException(executionexception.getCause());
                    return;
                }

                final ListenableFuture listenablefuture = this.outputFuture = this.function.apply(object);

                if (!this.isCancelled()) {
                    listenablefuture.addListener(new Runnable() {
                        public void run() {
                            try {
                                ChainingListenableFuture.this.set(Uninterruptibles.getUninterruptibly(listenablefuture));
                                return;
                            } catch (CancellationException cancellationexception1) {
                                ChainingListenableFuture.this.cancel(false);
                            } catch (ExecutionException executionexception1) {
                                ChainingListenableFuture.this.setException(executionexception1.getCause());
                                return;
                            } finally {
                                ChainingListenableFuture.this.outputFuture = null;
                            }

                        }
                    }, MoreExecutors.sameThreadExecutor());
                } else {
                    listenablefuture.cancel(this.wasInterrupted());
                    this.outputFuture = null;
                }
            } catch (UndeclaredThrowableException undeclaredthrowableexception) {
                this.setException(undeclaredthrowableexception.getCause());
            } catch (Throwable throwable) {
                this.setException(throwable);
            } finally {
                this.function = null;
                this.inputFuture = null;
                this.outputCreated.countDown();
            }
        }
    }

    private static class FallbackFuture<V> extends AbstractFuture<V> {

        private volatile ListenableFuture<? extends V> running;

        FallbackFuture(ListenableFuture<? extends V> listenablefuture, final FutureFallback<? extends V> futurefallback, Executor executor) {
            this.running = listenablefuture;
            Futures.addCallback(this.running, new FutureCallback<V>() {
                @Override
                public void onSuccess(V v0) {
                    FallbackFuture.this.set(v0);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    if (!FallbackFuture.this.isCancelled()) {
                        try {
                            FallbackFuture.this.running = futurefallback.create(throwable);
                            if (FallbackFuture.this.isCancelled()) {
                                FallbackFuture.this.running.cancel(FallbackFuture.this.wasInterrupted());
                                return;
                            }

                            Futures.addCallback(FallbackFuture.this.running, new FutureCallback<V>() {
                                @Override
                                public void onSuccess(V v0) {
                                    FallbackFuture.this.set(v0);
                                }

                                @Override
                                public void onFailure(Throwable throwable1) {
                                    if (FallbackFuture.this.running.isCancelled()) {
                                        FallbackFuture.this.cancel(false);
                                    } else {
                                        FallbackFuture.this.setException(throwable1);
                                    }

                                }
                            }, MoreExecutors.sameThreadExecutor());
                        } catch (Throwable throwable1) {
                            FallbackFuture.this.setException(throwable1);
                        }

                    }
                }
            }, executor);
        }

        @Override
        public boolean cancel(boolean flag) {
            if (super.cancel(flag)) {
                this.running.cancel(flag);
                return true;
            } else {
                return false;
            }
        }
    }

    private static class ImmediateFailedCheckedFuture<V, X extends Exception> extends Futures.ImmediateFuture<V> implements CheckedFuture<V, X> {

        private final X thrown;

        ImmediateFailedCheckedFuture(X x0) {
            super(null);
            this.thrown = x0;
        }

        @Override
        public V get() throws ExecutionException {
            throw new ExecutionException(this.thrown);
        }

        @Override
        public V checkedGet() throws X {
            throw this.thrown;
        }

        @Override
        public V checkedGet(long i, TimeUnit timeunit) throws X {
            Preconditions.checkNotNull(timeunit);
            throw this.thrown;
        }
    }

    private static class ImmediateCancelledFuture<V> extends Futures.ImmediateFuture<V> {

        private final CancellationException thrown = new CancellationException("Immediate cancelled future.");

        ImmediateCancelledFuture() {
            super(null);
        }

        @Override
        public boolean isCancelled() {
            return true;
        }

        @Override
        public V get() {
            throw AbstractFuture.cancellationExceptionWithCause("Task was cancelled.", this.thrown);
        }
    }

    private static class ImmediateFailedFuture<V> extends Futures.ImmediateFuture<V> {

        private final Throwable thrown;

        ImmediateFailedFuture(Throwable throwable) {
            super(null);
            this.thrown = throwable;
        }

        @Override
        public V get() throws ExecutionException {
            throw new ExecutionException(this.thrown);
        }
    }

    private static class ImmediateSuccessfulCheckedFuture<V, X extends Exception> extends Futures.ImmediateFuture<V> implements CheckedFuture<V, X> {

        @Nullable
        private final V value;

        ImmediateSuccessfulCheckedFuture(@Nullable V v0) {
            super(null);
            this.value = v0;
        }

        @Override
        public V get() {
            return this.value;
        }

        @Override
        public V checkedGet() {
            return this.value;
        }

        @Override
        public V checkedGet(long i, TimeUnit timeunit) {
            Preconditions.checkNotNull(timeunit);
            return this.value;
        }
    }

    private static class ImmediateSuccessfulFuture<V> extends Futures.ImmediateFuture<V> {

        @Nullable
        private final V value;

        ImmediateSuccessfulFuture(@Nullable V v0) {
            super(null);
            this.value = v0;
        }

        @Override
        public V get() {
            return this.value;
        }
    }

    private abstract static class ImmediateFuture<V> implements ListenableFuture<V> {

        private static final Logger log = Logger.getLogger(Futures.ImmediateFuture.class.getName());

        private ImmediateFuture() {}

        @Override
        public void addListener(Runnable runnable, Executor executor) {
            Preconditions.checkNotNull(runnable, "Runnable was null.");
            Preconditions.checkNotNull(executor, "Executor was null.");

            try {
                executor.execute(runnable);
            } catch (RuntimeException runtimeexception) {
                Futures.ImmediateFuture.log.log(Level.SEVERE, "RuntimeException while executing runnable " + runnable + " with executor " + executor, runtimeexception);
            }

        }

        public boolean cancel(boolean flag) {
            return false;
        }

        public abstract V get() throws ExecutionException;

        public V get(long i, TimeUnit timeunit) throws ExecutionException {
            Preconditions.checkNotNull(timeunit);
            return this.get();
        }

        public boolean isCancelled() {
            return false;
        }

        public boolean isDone() {
            return true;
        }
    }
}
