package io.netty.util;

public interface Attribute<T> {

    AttributeKey<T> key();

    T get();

    void set(T t0);

    T getAndSet(T t0);

    T setIfAbsent(T t0);

    T getAndRemove();

    boolean compareAndSet(T t0, T t1);

    void remove();
}
