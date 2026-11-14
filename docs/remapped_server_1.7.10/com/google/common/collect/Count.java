package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible
final class Count implements Serializable {

    private int value;

    Count(int i) {
        this.value = i;
    }

    public int get() {
        return this.value;
    }

    public int getAndAdd(int i) {
        int j = this.value;

        this.value = j + i;
        return j;
    }

    public int addAndGet(int i) {
        return this.value += i;
    }

    public void set(int i) {
        this.value = i;
    }

    public int getAndSet(int i) {
        int j = this.value;

        this.value = i;
        return j;
    }

    public int hashCode() {
        return this.value;
    }

    public boolean equals(@Nullable Object object) {
        return object instanceof Count && ((Count) object).value == this.value;
    }

    public String toString() {
        return Integer.toString(this.value);
    }
}
