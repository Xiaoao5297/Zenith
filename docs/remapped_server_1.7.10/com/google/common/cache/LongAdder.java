package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@GwtCompatible(emulated = true)
final class LongAdder extends Striped64 implements Serializable, LongAddable {

    private static final long serialVersionUID = 7249069246863182397L;

    @Override
    final long fn(long i, long j) {
        return i + j;
    }

    public LongAdder() {}

    @Override
    public void add(long i) {
        Striped64.Cell[] astriped64_cell = this.cells;

        if (this.cells == null) {
            long j = this.base;

            if (this.casBase(this.base, j + i)) {
                return;
            }
        }

        boolean flag = true;
        Striped64.HashCode striped64_hashcode;
        int k = (striped64_hashcode = (Striped64.HashCode) LongAdder.threadHashCode.get()).code;
        Striped64.Cell striped64_cell;
        int l;

        if (astriped64_cell != null && (l = astriped64_cell.length) >= 1 && (striped64_cell = astriped64_cell[l - 1 & k]) != null) {
            long i1 = striped64_cell.value;

            if (flag = striped64_cell.cas(striped64_cell.value, i1 + i)) {
                return;
            }
        }

        this.retryUpdate(i, striped64_hashcode, flag);
    }

    @Override
    public void increment() {
        this.add(1L);
    }

    public void decrement() {
        this.add(-1L);
    }

    @Override
    public long sum() {
        long i = this.base;
        Striped64.Cell[] astriped64_cell = this.cells;

        if (astriped64_cell != null) {
            int j = astriped64_cell.length;

            for (int k = 0; k < j; ++k) {
                Striped64.Cell striped64_cell = astriped64_cell[k];

                if (striped64_cell != null) {
                    i += striped64_cell.value;
                }
            }
        }

        return i;
    }

    public void reset() {
        this.internalReset(0L);
    }

    public long sumThenReset() {
        long i = this.base;
        Striped64.Cell[] astriped64_cell = this.cells;

        this.base = 0L;
        if (astriped64_cell != null) {
            int j = astriped64_cell.length;

            for (int k = 0; k < j; ++k) {
                Striped64.Cell striped64_cell = astriped64_cell[k];

                if (striped64_cell != null) {
                    i += striped64_cell.value;
                    striped64_cell.value = 0L;
                }
            }
        }

        return i;
    }

    public String toString() {
        return Long.toString(this.sum());
    }

    public long longValue() {
        return this.sum();
    }

    public int intValue() {
        return (int) this.sum();
    }

    public float floatValue() {
        return (float) this.sum();
    }

    public double doubleValue() {
        return (double) this.sum();
    }

    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeLong(this.sum());
    }

    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        this.busy = 0;
        this.cells = null;
        this.base = objectinputstream.readLong();
    }
}
