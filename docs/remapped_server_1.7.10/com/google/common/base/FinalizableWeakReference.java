package com.google.common.base;

import java.lang.ref.WeakReference;

public abstract class FinalizableWeakReference<T> extends WeakReference<T> implements FinalizableReference {

    protected FinalizableWeakReference(T t0, FinalizableReferenceQueue finalizablereferencequeue) {
        super(t0, finalizablereferencequeue.queue);
        finalizablereferencequeue.cleanUp();
    }
}
