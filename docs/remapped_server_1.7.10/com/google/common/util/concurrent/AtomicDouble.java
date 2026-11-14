package com.google.common.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class AtomicDouble extends Number implements Serializable {

    private static final long serialVersionUID = 0L;
    private transient volatile long value;
    private static final AtomicLongFieldUpdater<AtomicDouble> updater = AtomicLongFieldUpdater.newUpdater(AtomicDouble.class, "value");

    public AtomicDouble(double d0) {
        this.value = Double.doubleToRawLongBits(d0);
    }

    public AtomicDouble() {}

    public final double get() {
        return Double.longBitsToDouble(this.value);
    }

    public final void set(double d0) {
        long i = Double.doubleToRawLongBits(d0);

        this.value = i;
    }

    public final void lazySet(double d0) {
        this.set(d0);
    }

    public final double getAndSet(double d0) {
        long i = Double.doubleToRawLongBits(d0);

        return Double.longBitsToDouble(AtomicDouble.updater.getAndSet(this, i));
    }

    public final boolean compareAndSet(double d0, double d1) {
        return AtomicDouble.updater.compareAndSet(this, Double.doubleToRawLongBits(d0), Double.doubleToRawLongBits(d1));
    }

    public final boolean weakCompareAndSet(double d0, double d1) {
        return AtomicDouble.updater.weakCompareAndSet(this, Double.doubleToRawLongBits(d0), Double.doubleToRawLongBits(d1));
    }

    public final double getAndAdd(double d0) {
        long i;
        double d1;
        long j;

        do {
            i = this.value;
            d1 = Double.longBitsToDouble(i);
            double d2 = d1 + d0;

            j = Double.doubleToRawLongBits(d2);
        } while (!AtomicDouble.updater.compareAndSet(this, i, j));

        return d1;
    }

    public final double addAndGet(double d0) {
        long i;
        double d1;
        long j;

        do {
            i = this.value;
            double d2 = Double.longBitsToDouble(i);

            d1 = d2 + d0;
            j = Double.doubleToRawLongBits(d1);
        } while (!AtomicDouble.updater.compareAndSet(this, i, j));

        return d1;
    }

    public String toString() {
        return Double.toString(this.get());
    }

    public int intValue() {
        return (int) this.get();
    }

    public long longValue() {
        return (long) this.get();
    }

    public float floatValue() {
        return (float) this.get();
    }

    public double doubleValue() {
        return this.get();
    }

    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeDouble(this.get());
    }

    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        this.set(objectinputstream.readDouble());
    }
}
