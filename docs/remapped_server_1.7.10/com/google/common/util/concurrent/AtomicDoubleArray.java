package com.google.common.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLongArray;

public class AtomicDoubleArray implements Serializable {

    private static final long serialVersionUID = 0L;
    private transient AtomicLongArray longs;

    public AtomicDoubleArray(int i) {
        this.longs = new AtomicLongArray(i);
    }

    public AtomicDoubleArray(double[] adouble) {
        int i = adouble.length;
        long[] along = new long[i];

        for (int j = 0; j < i; ++j) {
            along[j] = Double.doubleToRawLongBits(adouble[j]);
        }

        this.longs = new AtomicLongArray(along);
    }

    public final int length() {
        return this.longs.length();
    }

    public final double get(int i) {
        return Double.longBitsToDouble(this.longs.get(i));
    }

    public final void set(int i, double d0) {
        long j = Double.doubleToRawLongBits(d0);

        this.longs.set(i, j);
    }

    public final void lazySet(int i, double d0) {
        this.set(i, d0);
    }

    public final double getAndSet(int i, double d0) {
        long j = Double.doubleToRawLongBits(d0);

        return Double.longBitsToDouble(this.longs.getAndSet(i, j));
    }

    public final boolean compareAndSet(int i, double d0, double d1) {
        return this.longs.compareAndSet(i, Double.doubleToRawLongBits(d0), Double.doubleToRawLongBits(d1));
    }

    public final boolean weakCompareAndSet(int i, double d0, double d1) {
        return this.longs.weakCompareAndSet(i, Double.doubleToRawLongBits(d0), Double.doubleToRawLongBits(d1));
    }

    public final double getAndAdd(int i, double d0) {
        long j;
        double d1;
        long k;

        do {
            j = this.longs.get(i);
            d1 = Double.longBitsToDouble(j);
            double d2 = d1 + d0;

            k = Double.doubleToRawLongBits(d2);
        } while (!this.longs.compareAndSet(i, j, k));

        return d1;
    }

    public double addAndGet(int i, double d0) {
        long j;
        double d1;
        long k;

        do {
            j = this.longs.get(i);
            double d2 = Double.longBitsToDouble(j);

            d1 = d2 + d0;
            k = Double.doubleToRawLongBits(d1);
        } while (!this.longs.compareAndSet(i, j, k));

        return d1;
    }

    public String toString() {
        int i = this.length() - 1;

        if (i == -1) {
            return "[]";
        } else {
            StringBuilder stringbuilder = new StringBuilder(19 * (i + 1));

            stringbuilder.append('[');
            int j = 0;

            while (true) {
                stringbuilder.append(Double.longBitsToDouble(this.longs.get(j)));
                if (j == i) {
                    return stringbuilder.append(']').toString();
                }

                stringbuilder.append(',').append(' ');
                ++j;
            }
        }
    }

    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        int i = this.length();

        objectoutputstream.writeInt(i);

        for (int j = 0; j < i; ++j) {
            objectoutputstream.writeDouble(this.get(j));
        }

    }

    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        int i = objectinputstream.readInt();

        this.longs = new AtomicLongArray(i);

        for (int j = 0; j < i; ++j) {
            this.set(j, objectinputstream.readDouble());
        }

    }
}
