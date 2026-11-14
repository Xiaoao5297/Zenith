package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
abstract class AbstractMapBasedMultiset<E> extends AbstractMultiset<E> implements Serializable {

    private transient Map<E, Count> backingMap;
    private transient long size;
    @GwtIncompatible("not needed in emulated source.")
    private static final long serialVersionUID = -2250766705698539974L;

    protected AbstractMapBasedMultiset(Map<E, Count> map) {
        this.backingMap = (Map) Preconditions.checkNotNull(map);
        this.size = (long) super.size();
    }

    void setBackingMap(Map<E, Count> map) {
        this.backingMap = map;
    }

    @Override
    public Set<Multiset.Entry<E>> entrySet() {
        return super.entrySet();
    }

    @Override
    Iterator<Multiset.Entry<E>> entryIterator() {
        final Iterator iterator = this.backingMap.entrySet().iterator();

        return new Iterator<Multiset.Entry<E>>() {
            java.util.Map.Entry<E, Count> toRemove;

            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Multiset.Entry<E> next() {
                final java.util.Map.Entry java_util_map_entry = (java.util.Map.Entry) iterator.next();

                this.toRemove = java_util_map_entry;
                return new Multisets.AbstractEntry<E>() {
                    @Override
                    public E getElement() {
                        return java_util_map_entry.getKey();
                    }

                    @Override
                    public int getCount() {
                        Count count = (Count) java_util_map_entry.getValue();

                        if (count == null || count.get() == 0) {
                            Count count1 = (Count) AbstractMapBasedMultiset.this.backingMap.get(this.getElement());

                            if (count1 != null) {
                                return count1.get();
                            }
                        }

                        return count == null ? 0 : count.get();
                    }
                };
            }

            public void remove() {
                CollectPreconditions.checkRemove(this.toRemove != null);
                AbstractMapBasedMultiset.this.size = (long) ((Count) this.toRemove.getValue()).getAndSet(0);
                iterator.remove();
                this.toRemove = null;
            }
        };
    }

    @Override
    public void clear() {
        Iterator iterator = this.backingMap.values().iterator();

        while (iterator.hasNext()) {
            Count count = (Count) iterator.next();

            count.set(0);
        }

        this.backingMap.clear();
        this.size = 0L;
    }

    @Override
    int distinctElements() {
        return this.backingMap.size();
    }

    @Override
    public int size() {
        return Ints.saturatedCast(this.size);
    }

    @Override
    public Iterator<E> iterator() {
        return new AbstractMapBasedMultiset.MapBasedMultisetIterator();
    }

    @Override
    public int count(@Nullable Object object) {
        Count count = (Count) Maps.safeGet(this.backingMap, object);

        return count == null ? 0 : count.get();
    }

    @Override
    public int add(@Nullable E e0, int i) {
        if (i == 0) {
            return this.count(e0);
        } else {
            Preconditions.checkArgument(i > 0, "occurrences cannot be negative: %s", i);
            Count count = (Count) this.backingMap.get(e0);
            int j;

            if (count == null) {
                j = 0;
                this.backingMap.put(e0, new Count(i));
            } else {
                j = count.get();
                long k = (long) j + (long) i;

                Preconditions.checkArgument(k <= 2147483647L, "too many occurrences: %s", k);
                count.getAndAdd(i);
            }

            this.size += (long) i;
            return j;
        }
    }

    @Override
    public int remove(@Nullable Object object, int i) {
        if (i == 0) {
            return this.count(object);
        } else {
            Preconditions.checkArgument(i > 0, "occurrences cannot be negative: %s", i);
            Count count = (Count) this.backingMap.get(object);

            if (count == null) {
                return 0;
            } else {
                int j = count.get();
                int k;

                if (j > i) {
                    k = i;
                } else {
                    k = j;
                    this.backingMap.remove(object);
                }

                count.addAndGet(-k);
                this.size -= (long) k;
                return j;
            }
        }
    }

    @Override
    public int setCount(@Nullable E e0, int i) {
        CollectPreconditions.checkNonnegative(i, "count");
        Count count;
        int j;

        if (i == 0) {
            count = (Count) this.backingMap.remove(e0);
            j = getAndSet(count, i);
        } else {
            count = (Count) this.backingMap.get(e0);
            j = getAndSet(count, i);
            if (count == null) {
                this.backingMap.put(e0, new Count(i));
            }
        }

        this.size += (long) (i - j);
        return j;
    }

    private static int getAndSet(Count count, int i) {
        return count == null ? 0 : count.getAndSet(i);
    }

    @GwtIncompatible("java.io.ObjectStreamException")
    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("Stream data required");
    }

    private class MapBasedMultisetIterator implements Iterator<E> {

        final Iterator<java.util.Map.Entry<E, Count>> entryIterator;
        java.util.Map.Entry<E, Count> currentEntry;
        int occurrencesLeft;
        boolean canRemove;

        MapBasedMultisetIterator() {
            this.entryIterator = AbstractMapBasedMultiset.this.backingMap.entrySet().iterator();
        }

        public boolean hasNext() {
            return this.occurrencesLeft > 0 || this.entryIterator.hasNext();
        }

        public E next() {
            if (this.occurrencesLeft == 0) {
                this.currentEntry = (java.util.Map.Entry) this.entryIterator.next();
                this.occurrencesLeft = ((Count) this.currentEntry.getValue()).get();
            }

            --this.occurrencesLeft;
            this.canRemove = true;
            return this.currentEntry.getKey();
        }

        public void remove() {
            CollectPreconditions.checkRemove(this.canRemove);
            int i = ((Count) this.currentEntry.getValue()).get();

            if (i <= 0) {
                throw new ConcurrentModificationException();
            } else {
                if (((Count) this.currentEntry.getValue()).addAndGet(-1) == 0) {
                    this.entryIterator.remove();
                }

                AbstractMapBasedMultiset.this.size--;
                this.canRemove = false;
            }
        }
    }
}
