package io.netty.util.internal.chmv8;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import sun.misc.Unsafe;

public abstract class CountedCompleter<T> extends ForkJoinTask<T> {

    private static final long serialVersionUID = 5232453752276485070L;
    final CountedCompleter<?> completer;
    volatile int pending;
    private static final Unsafe U;
    private static final long PENDING;

    protected CountedCompleter(CountedCompleter<?> countedcompleter, int i) {
        this.completer = countedcompleter;
        this.pending = i;
    }

    protected CountedCompleter(CountedCompleter<?> countedcompleter) {
        this.completer = countedcompleter;
    }

    protected CountedCompleter() {
        this.completer = null;
    }

    public abstract void compute();

    public void onCompletion(CountedCompleter<?> countedcompleter) {}

    public boolean onExceptionalCompletion(Throwable throwable, CountedCompleter<?> countedcompleter) {
        return true;
    }

    public final CountedCompleter<?> getCompleter() {
        return this.completer;
    }

    public final int getPendingCount() {
        return this.pending;
    }

    public final void setPendingCount(int i) {
        this.pending = i;
    }

    public final void addToPendingCount(int i) {
        int j;

        do {
            j = this.pending;
        } while (!CountedCompleter.U.compareAndSwapInt(this, CountedCompleter.PENDING, this.pending, j + i));

    }

    public final boolean compareAndSetPendingCount(int i, int j) {
        return CountedCompleter.U.compareAndSwapInt(this, CountedCompleter.PENDING, i, j);
    }

    public final int decrementPendingCountUnlessZero() {
        int i;

        do {
            i = this.pending;
        } while (this.pending != 0 && !CountedCompleter.U.compareAndSwapInt(this, CountedCompleter.PENDING, i, i - 1));

        return i;
    }

    public final CountedCompleter<?> getRoot() {
        CountedCompleter countedcompleter = this;

        while (true) {
            CountedCompleter countedcompleter1 = countedcompleter.completer;

            if (countedcompleter.completer == null) {
                return countedcompleter;
            }

            countedcompleter = countedcompleter1;
        }
    }

    public final void tryComplete() {
        CountedCompleter countedcompleter = this;
        CountedCompleter countedcompleter1 = this;

        label17:
        do {
            int i;

            do {
                i = countedcompleter.pending;
                if (countedcompleter.pending == 0) {
                    countedcompleter.onCompletion(countedcompleter1);
                    countedcompleter1 = countedcompleter;
                    continue label17;
                }
            } while (!CountedCompleter.U.compareAndSwapInt(countedcompleter, CountedCompleter.PENDING, i, i - 1));

            return;
        } while ((countedcompleter = countedcompleter.completer) != null);

        countedcompleter1.quietlyComplete();
    }

    public final void propagateCompletion() {
        CountedCompleter countedcompleter = this;

        CountedCompleter countedcompleter1;

        label17:
        do {
            int i;

            do {
                i = countedcompleter.pending;
                if (countedcompleter.pending == 0) {
                    countedcompleter1 = countedcompleter;
                    continue label17;
                }
            } while (!CountedCompleter.U.compareAndSwapInt(countedcompleter, CountedCompleter.PENDING, i, i - 1));

            return;
        } while ((countedcompleter = countedcompleter.completer) != null);

        countedcompleter1.quietlyComplete();
    }

    @Override
    public void complete(T t0) {
        this.setRawResult(t0);
        this.onCompletion(this);
        this.quietlyComplete();
        CountedCompleter countedcompleter = this.completer;

        if (this.completer != null) {
            countedcompleter.tryComplete();
        }

    }

    public final CountedCompleter<?> firstComplete() {
        int i;

        do {
            i = this.pending;
            if (this.pending == 0) {
                return this;
            }
        } while (!CountedCompleter.U.compareAndSwapInt(this, CountedCompleter.PENDING, i, i - 1));

        return null;
    }

    public final CountedCompleter<?> nextComplete() {
        CountedCompleter countedcompleter = this.completer;

        if (this.completer != null) {
            return countedcompleter.firstComplete();
        } else {
            this.quietlyComplete();
            return null;
        }
    }

    public final void quietlyCompleteRoot() {
        CountedCompleter countedcompleter = this;

        while (true) {
            CountedCompleter countedcompleter1 = countedcompleter.completer;

            if (countedcompleter.completer == null) {
                countedcompleter.quietlyComplete();
                return;
            }

            countedcompleter = countedcompleter1;
        }
    }

    @Override
    void internalPropagateException(Throwable throwable) {
        CountedCompleter countedcompleter = this;
        CountedCompleter countedcompleter1 = this;

        while (countedcompleter.onExceptionalCompletion(throwable, countedcompleter1)) {
            countedcompleter1 = countedcompleter;
            if ((countedcompleter = countedcompleter.completer) == null || countedcompleter.status < 0 || countedcompleter.recordExceptionalCompletion(throwable) != Integer.MIN_VALUE) {
                break;
            }
        }

    }

    @Override
    protected final boolean exec() {
        this.compute();
        return false;
    }

    @Override
    public T getRawResult() {
        return null;
    }

    @Override
    protected void setRawResult(T t0) {}

    private static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        } catch (SecurityException securityexception) {
            try {
                return (Unsafe) AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
                    public Unsafe run() throws Exception {
                        Class oclass = Unsafe.class;
                        Field[] afield = oclass.getDeclaredFields();
                        int i = afield.length;

                        for (int j = 0; j < i; ++j) {
                            Field field = afield[j];

                            field.setAccessible(true);
                            Object object = field.get((Object) null);

                            if (oclass.isInstance(object)) {
                                return (Unsafe) oclass.cast(object);
                            }
                        }

                        throw new NoSuchFieldError("the Unsafe");
                    }
                });
            } catch (PrivilegedActionException privilegedactionexception) {
                throw new RuntimeException("Could not initialize intrinsics", privilegedactionexception.getCause());
            }
        }
    }

    static {
        try {
            U = getUnsafe();
            PENDING = CountedCompleter.U.objectFieldOffset(CountedCompleter.class.getDeclaredField("pending"));
        } catch (Exception exception) {
            throw new Error(exception);
        }
    }
}
