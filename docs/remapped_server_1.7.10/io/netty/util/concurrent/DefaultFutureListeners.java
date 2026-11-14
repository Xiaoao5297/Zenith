package io.netty.util.concurrent;

import java.util.Arrays;

final class DefaultFutureListeners {

    private GenericFutureListener<? extends Future<?>>[] listeners = new GenericFutureListener[2];
    private int size;
    private int progressiveSize;

    public DefaultFutureListeners(GenericFutureListener<? extends Future<?>> genericfuturelistener, GenericFutureListener<? extends Future<?>> genericfuturelistener1) {
        this.listeners[0] = genericfuturelistener;
        this.listeners[1] = genericfuturelistener1;
        this.size = 2;
        if (genericfuturelistener instanceof GenericProgressiveFutureListener) {
            ++this.progressiveSize;
        }

        if (genericfuturelistener1 instanceof GenericProgressiveFutureListener) {
            ++this.progressiveSize;
        }

    }

    public void add(GenericFutureListener<? extends Future<?>> genericfuturelistener) {
        GenericFutureListener[] agenericfuturelistener = this.listeners;
        int i = this.size;

        if (i == agenericfuturelistener.length) {
            this.listeners = agenericfuturelistener = (GenericFutureListener[]) Arrays.copyOf(agenericfuturelistener, i << 1);
        }

        agenericfuturelistener[i] = genericfuturelistener;
        this.size = i + 1;
        if (genericfuturelistener instanceof GenericProgressiveFutureListener) {
            ++this.progressiveSize;
        }

    }

    public void remove(GenericFutureListener<? extends Future<?>> genericfuturelistener) {
        GenericFutureListener[] agenericfuturelistener = this.listeners;
        int i = this.size;

        for (int j = 0; j < i; ++j) {
            if (agenericfuturelistener[j] == genericfuturelistener) {
                int k = i - j - 1;

                if (k > 0) {
                    System.arraycopy(agenericfuturelistener, j + 1, agenericfuturelistener, j, k);
                }

                --i;
                agenericfuturelistener[i] = null;
                this.size = i;
                if (genericfuturelistener instanceof GenericProgressiveFutureListener) {
                    --this.progressiveSize;
                }

                return;
            }
        }

    }

    public GenericFutureListener<? extends Future<?>>[] listeners() {
        return this.listeners;
    }

    public int size() {
        return this.size;
    }

    public int progressiveSize() {
        return this.progressiveSize;
    }
}
