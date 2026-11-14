package io.netty.util.internal;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

public final class ConcurrentSet<E> extends AbstractSet<E> implements Serializable {

    private static final long serialVersionUID = -6761513279741915432L;
    private final ConcurrentMap<E, Boolean> map = PlatformDependent.newConcurrentHashMap();

    public ConcurrentSet() {}

    public int size() {
        return this.map.size();
    }

    public boolean contains(Object object) {
        return this.map.containsKey(object);
    }

    public boolean add(E e0) {
        return this.map.putIfAbsent(e0, Boolean.TRUE) == null;
    }

    public boolean remove(Object object) {
        return this.map.remove(object) != null;
    }

    public void clear() {
        this.map.clear();
    }

    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }
}
