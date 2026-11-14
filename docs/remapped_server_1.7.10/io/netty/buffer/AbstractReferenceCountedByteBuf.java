package io.netty.buffer;

import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AbstractReferenceCountedByteBuf extends AbstractByteBuf {

    private static final AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> refCntUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCountedByteBuf.class, "refCnt");
    private static final long REFCNT_FIELD_OFFSET;
    private volatile int refCnt = 1;

    protected AbstractReferenceCountedByteBuf(int i) {
        super(i);
    }

    @Override
    public final int refCnt() {
        return AbstractReferenceCountedByteBuf.REFCNT_FIELD_OFFSET >= 0L ? PlatformDependent.getInt(this, AbstractReferenceCountedByteBuf.REFCNT_FIELD_OFFSET) : this.refCnt;
    }

    protected final void setRefCnt(int i) {
        this.refCnt = i;
    }

    @Override
    public ByteBuf retain() {
        int i;

        do {
            i = this.refCnt;
            if (i == 0) {
                throw new IllegalReferenceCountException(0, 1);
            }

            if (i == Integer.MAX_VALUE) {
                throw new IllegalReferenceCountException(Integer.MAX_VALUE, 1);
            }
        } while (!AbstractReferenceCountedByteBuf.refCntUpdater.compareAndSet(this, i, i + 1));

        return this;
    }

    @Override
    public ByteBuf retain(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("increment: " + i + " (expected: > 0)");
        } else {
            int j;

            do {
                j = this.refCnt;
                if (j == 0) {
                    throw new IllegalReferenceCountException(0, i);
                }

                if (j > Integer.MAX_VALUE - i) {
                    throw new IllegalReferenceCountException(j, i);
                }
            } while (!AbstractReferenceCountedByteBuf.refCntUpdater.compareAndSet(this, j, j + i));

            return this;
        }
    }

    @Override
    public final boolean release() {
        int i;

        do {
            i = this.refCnt;
            if (i == 0) {
                throw new IllegalReferenceCountException(0, -1);
            }
        } while (!AbstractReferenceCountedByteBuf.refCntUpdater.compareAndSet(this, i, i - 1));

        if (i == 1) {
            this.deallocate();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final boolean release(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("decrement: " + i + " (expected: > 0)");
        } else {
            int j;

            do {
                j = this.refCnt;
                if (j < i) {
                    throw new IllegalReferenceCountException(j, -i);
                }
            } while (!AbstractReferenceCountedByteBuf.refCntUpdater.compareAndSet(this, j, j - i));

            if (j == i) {
                this.deallocate();
                return true;
            } else {
                return false;
            }
        }
    }

    protected abstract void deallocate();

    static {
        long i = -1L;

        try {
            if (PlatformDependent.hasUnsafe()) {
                i = PlatformDependent.objectFieldOffset(AbstractReferenceCountedByteBuf.class.getDeclaredField("refCnt"));
            }
        } catch (Throwable throwable) {
            ;
        }

        REFCNT_FIELD_OFFSET = i;
    }
}
