package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public final class ConcurrentHashMultiset<E> extends AbstractMultiset<E> implements Serializable {

    private final transient ConcurrentMap<E, AtomicInteger> countMap;
    private transient ConcurrentHashMultiset<E>.EntrySet entrySet;
    private static final long serialVersionUID = 1L;

    public static <E> ConcurrentHashMultiset<E> create() {
        return new ConcurrentHashMultiset<>(new ConcurrentHashMap());
    }

    public static <E> ConcurrentHashMultiset<E> create(Iterable<? extends E> iterable) {
        ConcurrentHashMultiset concurrenthashmultiset = create();

        Iterables.addAll(concurrenthashmultiset, iterable);
        return concurrenthashmultiset;
    }

    @Beta
    public static <E> ConcurrentHashMultiset<E> create(MapMaker mapmaker) {
        return new ConcurrentHashMultiset<>(mapmaker.makeMap());
    }

    @VisibleForTesting
    ConcurrentHashMultiset(ConcurrentMap<E, AtomicInteger> concurrentmap) {
        Preconditions.checkArgument(concurrentmap.isEmpty());
        this.countMap = concurrentmap;
    }

    @Override
    public int count(@Nullable Object object) {
        AtomicInteger atomicinteger = (AtomicInteger) Maps.safeGet(this.countMap, object);

        return atomicinteger == null ? 0 : atomicinteger.get();
    }

    @Override
    public int size() {
        long i = 0L;

        AtomicInteger atomicinteger;

        for (Iterator iterator = this.countMap.values().iterator(); iterator.hasNext(); i += (long) atomicinteger.get()) {
            atomicinteger = (AtomicInteger) iterator.next();
        }

        return Ints.saturatedCast(i);
    }

    public Object[] toArray() {
        return this.snapshot().toArray();
    }

    public <T> T[] toArray(T[] at) {
        return this.snapshot().toArray(at);
    }

    private List<E> snapshot() {
        ArrayList arraylist = Lists.newArrayListWithExpectedSize(this.size());
        Iterator iterator = this.entrySet().iterator();

        while (iterator.hasNext()) {
            Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();
            Object object = multiset_entry.getElement();

            for (int i = multiset_entry.getCount(); i > 0; --i) {
                arraylist.add(object);
            }
        }

        return arraylist;
    }

    @Override
    public int add(E e0, int i) {
        Preconditions.checkNotNull(e0);
        if (i == 0) {
            return this.count(e0);
        } else {
            Preconditions.checkArgument(i > 0, "Invalid occurrences: %s", i);

            AtomicInteger atomicinteger;
            AtomicInteger atomicinteger1;

            do {
                atomicinteger = (AtomicInteger) Maps.safeGet(this.countMap, e0);
                if (atomicinteger == null) {
                    atomicinteger = (AtomicInteger) this.countMap.putIfAbsent(e0, new AtomicInteger(i));
                    if (atomicinteger == null) {
                        return 0;
                    }
                }

                while (true) {
                    int j = atomicinteger.get();

                    if (j == 0) {
                        atomicinteger1 = new AtomicInteger(i);
                        break;
                    }

                    try {
                        int k = IntMath.checkedAdd(j, i);

                        if (atomicinteger.compareAndSet(j, k)) {
                            return j;
                        }
                    } catch (ArithmeticException arithmeticexception) {
                        throw new IllegalArgumentException("Overflow adding " + i + " occurrences to a count of " + j);
                    }
                }
            } while (this.countMap.putIfAbsent(e0, atomicinteger1) != null && !this.countMap.replace(e0, atomicinteger, atomicinteger1));

            return 0;
        }
    }

    @Override
    public int remove(@Nullable Object object, int i) {
        if (i == 0) {
            return this.count(object);
        } else {
            Preconditions.checkArgument(i > 0, "Invalid occurrences: %s", i);
            AtomicInteger atomicinteger = (AtomicInteger) Maps.safeGet(this.countMap, object);

            if (atomicinteger == null) {
                return 0;
            } else {
                int j;
                int k;

                do {
                    j = atomicinteger.get();
                    if (j == 0) {
                        return 0;
                    }

                    k = Math.max(0, j - i);
                } while (!atomicinteger.compareAndSet(j, k));

                if (k == 0) {
                    this.countMap.remove(object, atomicinteger);
                }

                return j;
            }
        }
    }

    public boolean removeExactly(@Nullable Object object, int i) {
        if (i == 0) {
            return true;
        } else {
            Preconditions.checkArgument(i > 0, "Invalid occurrences: %s", i);
            AtomicInteger atomicinteger = (AtomicInteger) Maps.safeGet(this.countMap, object);

            if (atomicinteger == null) {
                return false;
            } else {
                int j;
                int k;

                do {
                    j = atomicinteger.get();
                    if (j < i) {
                        return false;
                    }

                    k = j - i;
                } while (!atomicinteger.compareAndSet(j, k));

                if (k == 0) {
                    this.countMap.remove(object, atomicinteger);
                }

                return true;
            }
        }
    }

    @Override
    public int setCount(E e0, int i) {
        Preconditions.checkNotNull(e0);
        CollectPreconditions.checkNonnegative(i, "count");

        AtomicInteger atomicinteger;
        AtomicInteger atomicinteger1;

        label40:
        do {
            atomicinteger = (AtomicInteger) Maps.safeGet(this.countMap, e0);
            if (atomicinteger == null) {
                if (i == 0) {
                    return 0;
                }

                atomicinteger = (AtomicInteger) this.countMap.putIfAbsent(e0, new AtomicInteger(i));
                if (atomicinteger == null) {
                    return 0;
                }
            }

            int j;

            do {
                j = atomicinteger.get();
                if (j == 0) {
                    if (i == 0) {
                        return 0;
                    }

                    atomicinteger1 = new AtomicInteger(i);
                    continue label40;
                }
            } while (!atomicinteger.compareAndSet(j, i));

            if (i == 0) {
                this.countMap.remove(e0, atomicinteger);
            }

            return j;
        } while (this.countMap.putIfAbsent(e0, atomicinteger1) != null && !this.countMap.replace(e0, atomicinteger, atomicinteger1));

        return 0;
    }

    @Override
    public boolean setCount(E e0, int i, int j) {
        Preconditions.checkNotNull(e0);
        CollectPreconditions.checkNonnegative(i, "oldCount");
        CollectPreconditions.checkNonnegative(j, "newCount");
        AtomicInteger atomicinteger = (AtomicInteger) Maps.safeGet(this.countMap, e0);

        if (atomicinteger == null) {
            return i != 0 ? false : (j == 0 ? true : this.countMap.putIfAbsent(e0, new AtomicInteger(j)) == null);
        } else {
            int k = atomicinteger.get();

            if (k == i) {
                if (k == 0) {
                    if (j == 0) {
                        this.countMap.remove(e0, atomicinteger);
                        return true;
                    }

                    AtomicInteger atomicinteger1 = new AtomicInteger(j);

                    return this.countMap.putIfAbsent(e0, atomicinteger1) == null || this.countMap.replace(e0, atomicinteger, atomicinteger1);
                }

                if (atomicinteger.compareAndSet(k, j)) {
                    if (j == 0) {
                        this.countMap.remove(e0, atomicinteger);
                    }

                    return true;
                }
            }

            return false;
        }
    }

    @Override
    Set<E> createElementSet() {
        final Set set = this.countMap.keySet();

        return new ForwardingSet<E>() {
            @Override
            protected Set<E> delegate() {
                return set;
            }

            @Override
            public boolean contains(@Nullable Object object) {
                return object != null && Collections2.safeContains(set, object);
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                return this.standardContainsAll(collection);
            }

            @Override
            public boolean remove(Object object) {
                return object != null && Collections2.safeRemove(set, object);
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return this.standardRemoveAll(collection);
            }
        };
    }

    @Override
    public Set<Multiset.Entry<E>> entrySet() {
        ConcurrentHashMultiset.EntrySet concurrenthashmultiset_entryset = this.entrySet;

        if (concurrenthashmultiset_entryset == null) {
            this.entrySet = concurrenthashmultiset_entryset = new ConcurrentHashMultiset.EntrySet();
        }

        return concurrenthashmultiset_entryset;
    }

    @Override
    int distinctElements() {
        return this.countMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.countMap.isEmpty();
    }

    @Override
    Iterator<Multiset.Entry<E>> entryIterator() {
        final AbstractIterator abstractiterator = new AbstractIterator<Multiset.Entry<E>>() {
            private Iterator<java.util.Map.Entry<E, AtomicInteger>> mapEntries;

            {
                this.mapEntries = ConcurrentHashMultiset.this.countMap.entrySet().iterator();
            }

            @Override
            protected Multiset.Entry<E> computeNext() {
                java.util.Map.Entry java_util_map_entry;
                int i;

                do {
                    if (!this.mapEntries.hasNext()) {
                        return (Multiset.Entry) this.endOfData();
                    }

                    java_util_map_entry = (java.util.Map.Entry) this.mapEntries.next();
                    i = ((AtomicInteger) java_util_map_entry.getValue()).get();
                } while (i == 0);

                return Multisets.immutableEntry(java_util_map_entry.getKey(), i);
            }
        };

        return new ForwardingIterator<Multiset.Entry<E>>() {
            private Multiset.Entry<E> last;

            @Override
            protected Iterator<Multiset.Entry<E>> delegate() {
                return abstractiterator;
            }

            @Override
            public Multiset.Entry<E> next() {
                this.last = (Multiset.Entry) super.next();
                return this.last;
            }

            @Override
            public void remove() {
                CollectPreconditions.checkRemove(this.last != null);
                ConcurrentHashMultiset.this.setCount(this.last.getElement(), 0);
                this.last = null;
            }
        };
    }

    @Override
    public void clear() {
        this.countMap.clear();
    }

    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeObject(this.countMap);
    }

    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        ConcurrentMap concurrentmap = (ConcurrentMap) objectinputstream.readObject();

        ConcurrentHashMultiset.FieldSettersHolder.COUNT_MAP_FIELD_SETTER.set(this, concurrentmap);
    }

    private class EntrySet extends AbstractMultiset<E>.EntrySet {

        private EntrySet() {
            super();
        }

        @Override
        ConcurrentHashMultiset<E> multiset() {
            return ConcurrentHashMultiset.this;
        }

        public Object[] toArray() {
            return this.snapshot().toArray();
        }

        public <T> T[] toArray(T[] at) {
            return this.snapshot().toArray(at);
        }

        private List<Multiset.Entry<E>> snapshot() {
            ArrayList arraylist = Lists.newArrayListWithExpectedSize(this.size());

            Iterators.addAll(arraylist, this.iterator());
            return arraylist;
        }
    }

    private static class FieldSettersHolder {

        static final Serialization.FieldSetter<ConcurrentHashMultiset> COUNT_MAP_FIELD_SETTER = Serialization.getFieldSetter(ConcurrentHashMultiset.class, "countMap");

        private FieldSettersHolder() {}
    }
}
