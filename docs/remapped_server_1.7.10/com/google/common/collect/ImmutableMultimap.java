package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public abstract class ImmutableMultimap<K, V> extends AbstractMultimap<K, V> implements Serializable {

    final transient ImmutableMap<K, ? extends ImmutableCollection<V>> map;
    final transient int size;
    private static final long serialVersionUID = 0L;

    public static <K, V> ImmutableMultimap<K, V> of() {
        return ImmutableListMultimap.of();
    }

    public static <K, V> ImmutableMultimap<K, V> of(K k0, V v0) {
        return ImmutableListMultimap.of(k0, v0);
    }

    public static <K, V> ImmutableMultimap<K, V> of(K k0, V v0, K k1, V v1) {
        return ImmutableListMultimap.of(k0, v0, k1, v1);
    }

    public static <K, V> ImmutableMultimap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2) {
        return ImmutableListMultimap.of(k0, v0, k1, v1, k2, v2);
    }

    public static <K, V> ImmutableMultimap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3) {
        return ImmutableListMultimap.of(k0, v0, k1, v1, k2, v2, k3, v3);
    }

    public static <K, V> ImmutableMultimap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return ImmutableListMultimap.of(k0, v0, k1, v1, k2, v2, k3, v3, k4, v4);
    }

    public static <K, V> ImmutableMultimap.Builder<K, V> builder() {
        return new ImmutableMultimap.Builder<>();
    }

    public static <K, V> ImmutableMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap) {
        if (multimap instanceof ImmutableMultimap) {
            ImmutableMultimap immutablemultimap = (ImmutableMultimap) multimap;

            if (!immutablemultimap.isPartialView()) {
                return immutablemultimap;
            }
        }

        return ImmutableListMultimap.copyOf(multimap);
    }

    ImmutableMultimap(ImmutableMap<K, ? extends ImmutableCollection<V>> immutablemap, int i) {
        this.map = immutablemap;
        this.size = i;
    }

    /** @deprecated */
    @Deprecated
    @Override
    public ImmutableCollection<V> removeAll(Object object) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public ImmutableCollection<V> replaceValues(K k0, Iterable<? extends V> iterable) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public abstract ImmutableCollection<V> get(K k0);

    public abstract ImmutableMultimap<V, K> inverse();

    /** @deprecated */
    @Deprecated
    @Override
    public boolean put(K k0, V v0) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public boolean putAll(K k0, Iterable<? extends V> iterable) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public boolean remove(Object object, Object object1) {
        throw new UnsupportedOperationException();
    }

    boolean isPartialView() {
        return this.map.isPartialView();
    }

    @Override
    public boolean containsKey(@Nullable Object object) {
        return this.map.containsKey(object);
    }

    @Override
    public boolean containsValue(@Nullable Object object) {
        return object != null && super.containsValue(object);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ImmutableSet<K> keySet() {
        return this.map.keySet();
    }

    @Override
    public ImmutableMap<K, Collection<V>> asMap() {
        return this.map;
    }

    @Override
    Map<K, Collection<V>> createAsMap() {
        throw new AssertionError("should never be called");
    }

    @Override
    public ImmutableCollection<Entry<K, V>> entries() {
        return (ImmutableCollection) super.entries();
    }

    @Override
    ImmutableCollection<Entry<K, V>> createEntries() {
        return new ImmutableMultimap.EntryCollection<>(this);
    }

    @Override
    UnmodifiableIterator<Entry<K, V>> entryIterator() {
        return new ImmutableMultimap<K, V>.Itr<Entry<K, V>>() {
            @Override
            Entry<K, V> output(K k0, V v0) {
                return Maps.immutableEntry(k0, v0);
            }
        };
    }

    @Override
    public ImmutableMultiset<K> keys() {
        return (ImmutableMultiset) super.keys();
    }

    @Override
    ImmutableMultiset<K> createKeys() {
        return new ImmutableMultimap.Keys();
    }

    @Override
    public ImmutableCollection<V> values() {
        return (ImmutableCollection) super.values();
    }

    @Override
    ImmutableCollection<V> createValues() {
        return new ImmutableMultimap.Values<>(this);
    }

    @Override
    UnmodifiableIterator<V> valueIterator() {
        return new ImmutableMultimap<K, V>.Itr<V>() {
            @Override
            V output(K k0, V v0) {
                return v0;
            }
        };
    }

    private static final class Values<K, V> extends ImmutableCollection<V> {

        private final transient ImmutableMultimap<K, V> multimap;
        private static final long serialVersionUID = 0L;

        Values(ImmutableMultimap<K, V> immutablemultimap) {
            this.multimap = immutablemultimap;
        }

        @Override
        public boolean contains(@Nullable Object object) {
            return this.multimap.containsValue(object);
        }

        @Override
        public UnmodifiableIterator<V> iterator() {
            return this.multimap.valueIterator();
        }

        @GwtIncompatible("not present in emulated superclass")
        @Override
        int copyIntoArray(Object[] aobject, int i) {
            ImmutableCollection immutablecollection;

            for (Iterator iterator = this.multimap.map.values().iterator(); iterator.hasNext(); i = immutablecollection.copyIntoArray(aobject, i)) {
                immutablecollection = (ImmutableCollection) iterator.next();
            }

            return i;
        }

        public int size() {
            return this.multimap.size();
        }

        @Override
        boolean isPartialView() {
            return true;
        }
    }

    class Keys extends ImmutableMultiset<K> {

        Keys() {}

        @Override
        public boolean contains(@Nullable Object object) {
            return ImmutableMultimap.this.containsKey(object);
        }

        @Override
        public int count(@Nullable Object object) {
            Collection collection = (Collection) ImmutableMultimap.this.map.get(object);

            return collection == null ? 0 : collection.size();
        }

        @Override
        public Set<K> elementSet() {
            return ImmutableMultimap.this.keySet();
        }

        public int size() {
            return ImmutableMultimap.this.size();
        }

        @Override
        Multiset.Entry<K> getEntry(int i) {
            Entry entry = (Entry) ImmutableMultimap.this.map.entrySet().asList().get(i);

            return Multisets.immutableEntry(entry.getKey(), ((Collection) entry.getValue()).size());
        }

        @Override
        boolean isPartialView() {
            return true;
        }
    }

    private abstract class Itr<T> extends UnmodifiableIterator<T> {

        final Iterator<Entry<K, Collection<V>>> mapIterator;
        K key;
        Iterator<V> valueIterator;

        private Itr() {
            this.mapIterator = ImmutableMultimap.this.asMap().entrySet().iterator();
            this.key = null;
            this.valueIterator = Iterators.emptyIterator();
        }

        abstract T output(K k0, V v0);

        public boolean hasNext() {
            return this.mapIterator.hasNext() || this.valueIterator.hasNext();
        }

        public T next() {
            if (!this.valueIterator.hasNext()) {
                Entry entry = (Entry) this.mapIterator.next();

                this.key = entry.getKey();
                this.valueIterator = ((Collection) entry.getValue()).iterator();
            }

            return this.output(this.key, this.valueIterator.next());
        }
    }

    private static class EntryCollection<K, V> extends ImmutableCollection<Entry<K, V>> {

        final ImmutableMultimap<K, V> multimap;
        private static final long serialVersionUID = 0L;

        EntryCollection(ImmutableMultimap<K, V> immutablemultimap) {
            this.multimap = immutablemultimap;
        }

        @Override
        public UnmodifiableIterator<Entry<K, V>> iterator() {
            return this.multimap.entryIterator();
        }

        @Override
        boolean isPartialView() {
            return this.multimap.isPartialView();
        }

        public int size() {
            return this.multimap.size();
        }

        @Override
        public boolean contains(Object object) {
            if (object instanceof Entry) {
                Entry entry = (Entry) object;

                return this.multimap.containsEntry(entry.getKey(), entry.getValue());
            } else {
                return false;
            }
        }
    }

    @GwtIncompatible("java serialization is not supported")
    static class FieldSettersHolder {

        static final Serialization.FieldSetter<ImmutableMultimap> MAP_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "map");
        static final Serialization.FieldSetter<ImmutableMultimap> SIZE_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "size");
        static final Serialization.FieldSetter<ImmutableSetMultimap> EMPTY_SET_FIELD_SETTER = Serialization.getFieldSetter(ImmutableSetMultimap.class, "emptySet");

        FieldSettersHolder() {}
    }

    public static class Builder<K, V> {

        Multimap<K, V> builderMultimap = new ImmutableMultimap.BuilderMultimap<>();
        Comparator<? super K> keyComparator;
        Comparator<? super V> valueComparator;

        public Builder() {}

        public ImmutableMultimap.Builder<K, V> put(K k0, V v0) {
            CollectPreconditions.checkEntryNotNull(k0, v0);
            this.builderMultimap.put(k0, v0);
            return this;
        }

        public ImmutableMultimap.Builder<K, V> put(Entry<? extends K, ? extends V> entry) {
            return this.put(entry.getKey(), entry.getValue());
        }

        public ImmutableMultimap.Builder<K, V> putAll(K k0, Iterable<? extends V> iterable) {
            if (k0 == null) {
                throw new NullPointerException("null key in entry: null=" + Iterables.toString(iterable));
            } else {
                Collection collection = this.builderMultimap.get(k0);
                Iterator iterator = iterable.iterator();

                while (iterator.hasNext()) {
                    Object object = iterator.next();

                    CollectPreconditions.checkEntryNotNull(k0, object);
                    collection.add(object);
                }

                return this;
            }
        }

        public ImmutableMultimap.Builder<K, V> putAll(K k0, V... av) {
            return this.putAll(k0, (Iterable) Arrays.asList(av));
        }

        public ImmutableMultimap.Builder<K, V> putAll(Multimap<? extends K, ? extends V> multimap) {
            Iterator iterator = multimap.asMap().entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                this.putAll(entry.getKey(), (Iterable) entry.getValue());
            }

            return this;
        }

        public ImmutableMultimap.Builder<K, V> orderKeysBy(Comparator<? super K> comparator) {
            this.keyComparator = (Comparator) Preconditions.checkNotNull(comparator);
            return this;
        }

        public ImmutableMultimap.Builder<K, V> orderValuesBy(Comparator<? super V> comparator) {
            this.valueComparator = (Comparator) Preconditions.checkNotNull(comparator);
            return this;
        }

        public ImmutableMultimap<K, V> build() {
            if (this.valueComparator != null) {
                Iterator iterator = this.builderMultimap.asMap().values().iterator();

                while (iterator.hasNext()) {
                    Collection collection = (Collection) iterator.next();
                    List list = (List) collection;

                    Collections.sort(list, this.valueComparator);
                }
            }

            if (this.keyComparator != null) {
                ImmutableMultimap.BuilderMultimap immutablemultimap_buildermultimap = new ImmutableMultimap.BuilderMultimap<>();
                ArrayList arraylist = Lists.newArrayList((Iterable) this.builderMultimap.asMap().entrySet());

                Collections.sort(arraylist, Ordering.from(this.keyComparator).onKeys());
                Iterator iterator1 = arraylist.iterator();

                while (iterator1.hasNext()) {
                    Entry entry = (Entry) iterator1.next();

                    immutablemultimap_buildermultimap.putAll(entry.getKey(), (Iterable) entry.getValue());
                }

                this.builderMultimap = immutablemultimap_buildermultimap;
            }

            return ImmutableMultimap.copyOf(this.builderMultimap);
        }
    }

    private static class BuilderMultimap<K, V> extends AbstractMapBasedMultimap<K, V> {

        private static final long serialVersionUID = 0L;

        BuilderMultimap() {
            super(new LinkedHashMap());
        }

        @Override
        Collection<V> createCollection() {
            return Lists.newArrayList();
        }
    }
}
