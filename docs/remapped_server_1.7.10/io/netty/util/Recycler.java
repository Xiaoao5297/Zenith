package io.netty.util;

import java.util.IdentityHashMap;
import java.util.Map;

public abstract class Recycler<T> {

    private final ThreadLocal<Recycler.Stack<T>> threadLocal = new ThreadLocal<Recycler.Stack<T>>() {
        protected Recycler.Stack<T> initialValue() {
            return new Recycler.Stack<>(Recycler.this, Thread.currentThread());
        }
    };

    public Recycler() {}

    public final T get() {
        Recycler.Stack recycler_stack = (Recycler.Stack) this.threadLocal.get();
        Object object = recycler_stack.pop();

        if (object == null) {
            object = this.newObject(recycler_stack);
        }

        return object;
    }

    public final boolean recycle(T t0, Recycler.Handle recycler_handle) {
        Recycler.Stack recycler_stack = (Recycler.Stack) recycler_handle;

        if (recycler_stack.parent != this) {
            return false;
        } else if (Thread.currentThread() != recycler_stack.thread) {
            return false;
        } else {
            recycler_stack.push(t0);
            return true;
        }
    }

    protected abstract T newObject(Recycler.Handle recycler_handle);

    static final class Stack<T> implements Recycler.Handle {

        private static final int INITIAL_CAPACITY = 256;
        final Recycler<T> parent;
        final Thread thread;
        private T[] elements;
        private int size;
        private final Map<T, Boolean> map = new IdentityHashMap(256);

        Stack(Recycler<T> recycler, Thread thread) {
            this.parent = recycler;
            this.thread = thread;
            this.elements = newArray(256);
        }

        T pop() {
            int i = this.size;

            if (i == 0) {
                return null;
            } else {
                --i;
                Object object = this.elements[i];

                this.elements[i] = null;
                this.map.remove(object);
                this.size = i;
                return object;
            }
        }

        void push(T t0) {
            if (this.map.put(t0, Boolean.TRUE) != null) {
                throw new IllegalStateException("recycled already");
            } else {
                int i = this.size;

                if (i == this.elements.length) {
                    Object[] aobject = newArray(i << 1);

                    System.arraycopy(this.elements, 0, aobject, 0, i);
                    this.elements = aobject;
                }

                this.elements[i] = t0;
                this.size = i + 1;
            }
        }

        private static <T> T[] newArray(int i) {
            return (Object[]) (new Object[i]);
        }
    }

    public interface Handle {}
}
