package io.netty.util.internal.chmv8;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Random;
import sun.misc.Unsafe;

abstract class Striped64 extends Number {

    static final Striped64.ThreadHashCode threadHashCode = new Striped64.ThreadHashCode();
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    transient volatile Striped64.Cell[] cells;
    transient volatile long base;
    transient volatile int busy;
    private static final Unsafe UNSAFE;
    private static final long baseOffset;
    private static final long busyOffset;

    Striped64() {}

    final boolean casBase(long i, long j) {
        return Striped64.UNSAFE.compareAndSwapLong(this, Striped64.baseOffset, i, j);
    }

    final boolean casBusy() {
        return Striped64.UNSAFE.compareAndSwapInt(this, Striped64.busyOffset, 0, 1);
    }

    abstract long fn(long i, long j);

    final void retryUpdate(long i, Striped64.HashCode striped64_hashcode, boolean flag) {
        int j = striped64_hashcode.code;
        boolean flag1 = false;

        while (true) {
            Striped64.Cell[] astriped64_cell = this.cells;
            long k;
            int l;

            if (this.cells != null && (l = astriped64_cell.length) > 0) {
                Striped64.Cell striped64_cell;

                if ((striped64_cell = astriped64_cell[l - 1 & j]) == null) {
                    if (this.busy == 0) {
                        Striped64.Cell striped64_cell1 = new Striped64.Cell(i);

                        if (this.busy == 0 && this.casBusy()) {
                            boolean flag2 = false;

                            try {
                                Striped64.Cell[] astriped64_cell1 = this.cells;
                                int i1;
                                int j1;

                                if (this.cells != null && (j1 = astriped64_cell1.length) > 0 && astriped64_cell1[i1 = j1 - 1 & j] == null) {
                                    astriped64_cell1[i1] = striped64_cell1;
                                    flag2 = true;
                                }
                            } finally {
                                this.busy = 0;
                            }

                            if (flag2) {
                                break;
                            }
                            continue;
                        }
                    }

                    flag1 = false;
                } else if (!flag) {
                    flag = true;
                } else {
                    if (striped64_cell.cas(k = striped64_cell.value, this.fn(k, i))) {
                        break;
                    }

                    if (l < Striped64.NCPU && this.cells == astriped64_cell) {
                        if (!flag1) {
                            flag1 = true;
                        } else if (this.busy == 0 && this.casBusy()) {
                            try {
                                if (this.cells == astriped64_cell) {
                                    Striped64.Cell[] astriped64_cell2 = new Striped64.Cell[l << 1];

                                    for (int k1 = 0; k1 < l; ++k1) {
                                        astriped64_cell2[k1] = astriped64_cell[k1];
                                    }

                                    this.cells = astriped64_cell2;
                                }
                            } finally {
                                this.busy = 0;
                            }

                            flag1 = false;
                            continue;
                        }
                    } else {
                        flag1 = false;
                    }
                }

                j ^= j << 13;
                j ^= j >>> 17;
                j ^= j << 5;
            } else if (this.busy == 0 && this.cells == astriped64_cell && this.casBusy()) {
                boolean flag3 = false;

                try {
                    if (this.cells == astriped64_cell) {
                        Striped64.Cell[] astriped64_cell3 = new Striped64.Cell[2];

                        astriped64_cell3[j & 1] = new Striped64.Cell(i);
                        this.cells = astriped64_cell3;
                        flag3 = true;
                    }
                } finally {
                    this.busy = 0;
                }

                if (flag3) {
                    break;
                }
            } else if (this.casBase(k = this.base, this.fn(k, i))) {
                break;
            }
        }

        striped64_hashcode.code = j;
    }

    final void internalReset(long i) {
        Striped64.Cell[] astriped64_cell = this.cells;

        this.base = i;
        if (astriped64_cell != null) {
            int j = astriped64_cell.length;

            for (int k = 0; k < j; ++k) {
                Striped64.Cell striped64_cell = astriped64_cell[k];

                if (striped64_cell != null) {
                    striped64_cell.value = i;
                }
            }
        }

    }

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
            UNSAFE = getUnsafe();
            Class oclass = Striped64.class;

            baseOffset = Striped64.UNSAFE.objectFieldOffset(oclass.getDeclaredField("base"));
            busyOffset = Striped64.UNSAFE.objectFieldOffset(oclass.getDeclaredField("busy"));
        } catch (Exception exception) {
            throw new Error(exception);
        }
    }

    static final class ThreadHashCode extends ThreadLocal<Striped64.HashCode> {

        ThreadHashCode() {}

        public Striped64.HashCode initialValue() {
            return new Striped64.HashCode();
        }
    }

    static final class HashCode {

        static final Random rng = new Random();
        int code;

        HashCode() {
            int i = Striped64.HashCode.rng.nextInt();

            this.code = i == 0 ? 1 : i;
        }
    }

    static final class Cell {

        volatile long p0;
        volatile long p1;
        volatile long p2;
        volatile long p3;
        volatile long p4;
        volatile long p5;
        volatile long p6;
        volatile long value;
        volatile long q0;
        volatile long q1;
        volatile long q2;
        volatile long q3;
        volatile long q4;
        volatile long q5;
        volatile long q6;
        private static final Unsafe UNSAFE;
        private static final long valueOffset;

        Cell(long i) {
            this.value = i;
        }

        final boolean cas(long i, long j) {
            return Striped64.Cell.UNSAFE.compareAndSwapLong(this, Striped64.Cell.valueOffset, i, j);
        }

        static {
            try {
                UNSAFE = Striped64.getUnsafe();
                Class oclass = Striped64.Cell.class;

                valueOffset = Striped64.Cell.UNSAFE.objectFieldOffset(oclass.getDeclaredField("value"));
            } catch (Exception exception) {
                throw new Error(exception);
            }
        }
    }
}
