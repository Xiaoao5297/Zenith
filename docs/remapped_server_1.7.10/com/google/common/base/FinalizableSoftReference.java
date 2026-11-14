package com.google.common.base;

import java.lang.ref.SoftReference;

public abstract class FinalizableSoftReference<T> extends SoftReference<T> implements FinalizableReference {

    protected FinalizableSoftReference(T t0, FinalizableReferenceQueue finalizablereferencequeue) {
        super(t0, finalizablereferencequeue.queue);
        finalizablereferencequeue.cleanUp();
    }
}
