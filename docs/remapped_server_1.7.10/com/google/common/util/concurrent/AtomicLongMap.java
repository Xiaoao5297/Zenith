package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@GwtCompatible
public final class AtomicLongMap<K> {

    private final ConcurrentHashMap<K, AtomicLong> map;
    private transient Map<K, Long> asMap;

    private AtomicLongMap(ConcurrentHashMap<K, AtomicLong> concurrenthashmap) {
        this.map = (ConcurrentHashMap) Preconditions.checkNotNull(concurrenthashmap);
    }

    public static <K> AtomicLongMap<K> create() {
        return new AtomicLongMap<>(new ConcurrentHashMap());
    }

    public static <K> AtomicLongMap<K> create(Map<? extends K, ? extends Long> map) {
        AtomicLongMap atomiclongmap = create();

        atomiclongmap.putAll(map);
        return atomiclongmap;
    }

    public long get(K k0) {
        AtomicLong atomiclong = (AtomicLong) this.map.get(k0);

        return atomiclong == null ? 0L : atomiclong.get();
    }

    public long incrementAndGet(K k0) {
        return this.addAndGet(k0, 1L);
    }

    public long decrementAndGet(K k0) {
        return this.addAndGet(k0, -1L);
    }

    public long addAndGet(K k0, long i) {
        AtomicLong atomiclong;

        label23:
        do {
            atomiclong = (AtomicLong) this.map.get(k0);
            if (atomiclong == null) {
                atomiclong = (AtomicLong) this.map.putIfAbsent(k0, new AtomicLong(i));
                if (atomiclong == null) {
                    return i;
                }
            }

            long j;
            long k;

            do {
                j = atomiclong.get();
                if (j == 0L) {
                    continue label23;
                }

                k = j + i;
            } while (!atomiclong.compareAndSet(j, k));

            return k;
        } while (!this.map.replace(k0, atomiclong, new AtomicLong(i)));

        return i;
    }

    public long getAndIncrement(K k0) {
        return this.getAndAdd(k0, 1L);
    }

    public long getAndDecrement(K k0) {
        return this.getAndAdd(k0, -1L);
    }

    public long getAndAdd(K k0, long i) {
        AtomicLong atomiclong;

        label23:
        do {
            atomiclong = (AtomicLong) this.map.get(k0);
            if (atomiclong == null) {
                atomiclong = (AtomicLong) this.map.putIfAbsent(k0, new AtomicLong(i));
                if (atomiclong == null) {
                    return 0L;
                }
            }

            long j;
            long k;

            do {
                j = atomiclong.get();
                if (j == 0L) {
                    continue label23;
                }

                k = j + i;
            } while (!atomiclong.compareAndSet(j, k));

            return j;
        } while (!this.map.replace(k0, atomiclong, new AtomicLong(i)));

        return 0L;
    }

    public long put(K k0, long i) {
        AtomicLong atomiclong;

        label23:
        do {
            atomiclong = (AtomicLong) this.map.get(k0);
            if (atomiclong == null) {
                atomiclong = (AtomicLong) this.map.putIfAbsent(k0, new AtomicLong(i));
                if (atomiclong == null) {
                    return 0L;
                }
            }

            long j;

            do {
                j = atomiclong.get();
                if (j == 0L) {
                    continue label23;
                }
            } while (!atomiclong.compareAndSet(j, i));

            return j;
        } while (!this.map.replace(k0, atomiclong, new AtomicLong(i)));

        return 0L;
    }

    public void putAll(Map<? extends K, ? extends Long> map) {
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Long) entry.getValue());
        }

    }

    public long remove(K k0) {
        AtomicLong atomiclong = (AtomicLong) this.map.get(k0);

        if (atomiclong == null) {
            return 0L;
        } else {
            long i;

            do {
                i = atomiclong.get();
            } while (i != 0L && !atomiclong.compareAndSet(i, 0L));

            this.map.remove(k0, atomiclong);
            return i;
        }
    }

    public void removeAllZeros() {
        Iterator iterator = this.map.keySet().iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();
            AtomicLong atomiclong = (AtomicLong) this.map.get(object);

            if (atomiclong != null && atomiclong.get() == 0L) {
                this.map.remove(object, atomiclong);
            }
        }

    }

    public long sum() {
        long i = 0L;

        AtomicLong atomiclong;

        for (Iterator iterator = this.map.values().iterator(); iterator.hasNext(); i += atomiclong.get()) {
            atomiclong = (AtomicLong) iterator.next();
        }

        return i;
    }

    public Map<K, Long> asMap() {
        Map map = this.asMap;

        return map == null ? (this.asMap = this.createAsMap()) : map;
    }

    private Map<K, Long> createAsMap() {
        return Collections.unmodifiableMap(Maps.transformValues((Map) this.map, new Function<AtomicLong, Long>() {
            public Long apply(AtomicLong atomiclong) {
                return atomiclong.get();
            }
        }));
    }

    public boolean containsKey(Object object) {
        return this.map.containsKey(object);
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public void clear() {
        this.map.clear();
    }

    public String toString() {
        return this.map.toString();
    }

    long putIfAbsent(K k0, long i) {
        while (true) {
            AtomicLong atomiclong = (AtomicLong) this.map.get(k0);

            if (atomiclong == null) {
                atomiclong = (AtomicLong) this.map.putIfAbsent(k0, new AtomicLong(i));
                if (atomiclong == null) {
                    return 0L;
                }
            }

            long j = atomiclong.get();

            if (j == 0L) {
                if (!this.map.replace(k0, atomiclong, new AtomicLong(i))) {
                    continue;
                }

                return 0L;
            }

            return j;
        }
    }

    boolean replace(K k0, long i, long j) {
        if (i == 0L) {
            return this.putIfAbsent(k0, j) == 0L;
        } else {
            AtomicLong atomiclong = (AtomicLong) this.map.get(k0);

            return atomiclong == null ? false : atomiclong.compareAndSet(i, j);
        }
    }

    boolean remove(K k0, long i) {
        AtomicLong atomiclong = (AtomicLong) this.map.get(k0);

        if (atomiclong == null) {
            return false;
        } else {
            long j = atomiclong.get();

            if (j != i) {
                return false;
            } else if (j != 0L && !atomiclong.compareAndSet(j, 0L)) {
                return false;
            } else {
                this.map.remove(k0, atomiclong);
                return true;
            }
        }
    }
}
