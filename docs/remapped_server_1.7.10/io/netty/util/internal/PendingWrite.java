package io.netty.util.internal;

import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;

public final class PendingWrite {

    private static final Recycler<PendingWrite> RECYCLER = new Recycler<PendingWrite>() {
        @Override
        protected PendingWrite newObject(Recycler.Handle recycler_handle) {
            return new PendingWrite(recycler_handle);
        }
    };
    private final Recycler.Handle handle;
    private Object msg;
    private Promise<Void> promise;

    public static PendingWrite newInstance(Object object, Promise<Void> promise) {
        PendingWrite pendingwrite = (PendingWrite) PendingWrite.RECYCLER.get();

        pendingwrite.msg = object;
        pendingwrite.promise = promise;
        return pendingwrite;
    }

    private PendingWrite(Recycler.Handle recycler_handle) {
        this.handle = recycler_handle;
    }

    public boolean recycle() {
        this.msg = null;
        this.promise = null;
        return PendingWrite.RECYCLER.recycle(this, this.handle);
    }

    public boolean failAndRecycle(Throwable throwable) {
        ReferenceCountUtil.release(this.msg);
        if (this.promise != null) {
            this.promise.setFailure(throwable);
        }

        return this.recycle();
    }

    public boolean successAndRecycle() {
        if (this.promise != null) {
            this.promise.setSuccess((Object) null);
        }

        return this.recycle();
    }

    public Object msg() {
        return this.msg;
    }

    public Promise<Void> promise() {
        return this.promise;
    }

    public Promise<Void> recycleAndGet() {
        Promise promise = this.promise;

        this.recycle();
        return promise;
    }
}
