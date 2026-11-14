package com.google.common.base;

import java.lang.ref.PhantomReference;

public abstract class FinalizablePhantomReference<T> extends PhantomReference<T> implements FinalizableReference {

    protected FinalizablePhantomReference(T t0, FinalizableReferenceQueue finalizablereferencequeue) {
        super(t0, finalizablereferencequeue.queue);
        finalizablereferencequeue.cleanUp();
    }
}
