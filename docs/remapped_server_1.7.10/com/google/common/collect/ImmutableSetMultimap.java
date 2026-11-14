package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
public class ImmutableSetMultimap<K, V> extends ImmutableMultimap<K, V> implements SetMultimap<K, V> {

    private final transient ImmutableSet<V> emptySet;
    private transient ImmutableSetMultimap<V, K> inverse;
    private transient ImmutableSet<Entry<K, V>> entries;
    @GwtIncompatible("not needed in emulated source.")
    private static final long serialVersionUID = 0L;

    public static <K, V> ImmutableSetMultimap<K, V> of() {
        return EmptyImmutableSetMultimap.INSTANCE;
    }

    public static <K, V> ImmutableSetMultimap<K, V> of(K k0, V v0) {
        ImmutableSetMultimap.Builder immutablesetmultimap_builder = builder();

        immutablesetmultimap_builder.put(k0, v0);
        return immutablesetmultimap_builder.build();
    }

    public static <K, V> ImmutableSetMultimap<K, V> of(K k0, V v0, K k1, V v1) {
        ImmutableSetMultimap.Builder immutablesetmultimap_builder = builder();

        immutablesetmultimap_builder.put(k0, v0);
        immutablesetmultimap_builder.put(k1, v1);
        return immutablesetmultimap_builder.build();
    }

    public static <K, V> ImmutableSetMultimap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2) {
        ImmutableSetMultimap.Builder immutablesetmultimap_builder = builder();

        immutablesetmultimap_builder.put(k0, v0);
        immutablesetmultimap_builder.put(k1, v1);
        immutablesetmultimap_builder.put(k2, v2);
        return immutablesetmultimap_builder.build();
    }

    public static <K, V> ImmutableSetMultimap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3) {
        ImmutableSetMultimap.Builder immutablesetmultimap_builder = builder();

        immutablesetmultimap_builder.put(k0, v0);
        immutablesetmultimap_builder.put(k1, v1);
        immutablesetmultimap_builder.put(k2, v2);
        immutablesetmultimap_builder.put(k3, v3);
        return immutablesetmultimap_builder.build();
    }

    public static <K, V> ImmutableSetMultimap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        ImmutableSetMultimap.Builder immutablesetmultimap_builder = builder();

        immutablesetmultimap_builder.put(k0, v0);
        immutablesetmultimap_builder.put(k1, v1);
        immutablesetmultimap_builder.put(k2, v2);
        immutablesetmultimap_builder.put(k3, v3);
        immutablesetmultimap_builder.put(k4, v4);
        return immutablesetmultimap_builder.build();
    }

    public static <K, V> ImmutableSetMultimap.Builder<K, V> builder() {
        return new ImmutableSetMultimap.Builder<>();
    }

    public static <K, V> ImmutableSetMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap) {
        return copyOf(multimap, (Comparator) null);
    }

    private static <K, V> ImmutableSetMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap, Comparator<? super V> comparator) {
        Preconditions.checkNotNull(multimap);
        if (multimap.isEmpty() && comparator == null) {
            return of();
        } else {
            if (multimap instanceof ImmutableSetMultimap) {
                ImmutableSetMultimap immutablesetmultimap = (ImmutableSetMultimap) multimap;

                if (!immutablesetmultimap.isPartialView()) {
                    return immutablesetmultimap;
                }
            }

            ImmutableMap.Builder immutablemap_builder = ImmutableMap.builder();
            int i = 0;
            Iterator iterator = multimap.asMap().entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                Object object = entry.getKey();
                Collection collection = (Collection) entry.getValue();
                ImmutableSet immutableset = valueSet(comparator, collection);

                if (!immutableset.isEmpty()) {
                    immutablemap_builder.put(object, immutableset);
                    i += immutableset.size();
                }
            }

            return new ImmutableSetMultimap<>(immutablemap_builder.build(), i, comparator);
        }
    }

    ImmutableSetMultimap(ImmutableMap<K, ImmutableSet<V>> immutablemap, int i, @Nullable Comparator<? super V> comparator) {
        super(immutablemap, i);
        this.emptySet = emptySet(comparator);
    }

    @Override
    public ImmutableSet<V> get(@Nullable K k0) {
        ImmutableSet immutableset = (ImmutableSet) this.map.get(k0);

        return (ImmutableSet) Objects.firstNonNull(immutableset, this.emptySet);
    }

    @Override
    public ImmutableSetMultimap<V, K> inverse() {
        ImmutableSetMultimap immutablesetmultimap = this.inverse;

        return immutablesetmultimap == null ? (this.inverse = this.invert()) : immutablesetmultimap;
    }

    private ImmutableSetMultimap<V, K> invert() {
        ImmutableSetMultimap.Builder immutablesetmultimap_builder = builder();
        Iterator iterator = this.entries().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            immutablesetmultimap_builder.put(entry.getValue(), entry.getKey());
        }

        ImmutableSetMultimap immutablesetmultimap = immutablesetmultimap_builder.build();

        immutablesetmultimap.inverse = this;
        return immutablesetmultimap;
    }

    /** @deprecated */
    @Deprecated
    @Override
    public ImmutableSet<V> removeAll(Object object) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public ImmutableSet<V> replaceValues(K k0, Iterable<? extends V> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableSet<Entry<K, V>> entries() {
        ImmutableSet immutableset = this.entries;

        return immutableset == null ? (this.entries = new ImmutableSetMultimap.EntrySet<>(this)) : immutableset;
    }

    private static <V> ImmutableSet<V> valueSet(@Nullable Comparator<? super V> comparator, Collection<? extends V> collection) {
        return (ImmutableSet) (comparator == null ? ImmutableSet.copyOf(collection) : ImmutableSortedSet.copyOf(comparator, collection));
    }

    private static <V> ImmutableSet<V> emptySet(@Nullable Comparator<? super V> comparator) {
        return (ImmutableSet) (comparator == null ? ImmutableSet.of() : ImmutableSortedSet.emptySet(comparator));
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeObject(this.valueComparator());
        Serialization.writeMultimap(this, objectoutputstream);
    }

    @Nullable
    Comparator<? super V> valueComparator() {
        return this.emptySet instanceof ImmutableSortedSet ? ((ImmutableSortedSet) this.emptySet).comparator() : null;
    }

    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        Comparator comparator = (Comparator) objectinputstream.readObject();
        int i = objectinputstream.readInt();

        if (i < 0) {
            throw new InvalidObjectException("Invalid key count " + i);
        } else {
            ImmutableMap.Builder immutablemap_builder = ImmutableMap.builder();
            int j = 0;

            for (int k = 0; k < i; ++k) {
                Object object = objectinputstream.readObject();
                int l = objectinputstream.readInt();

                if (l <= 0) {
                    throw new InvalidObjectException("Invalid value count " + l);
                }

                Object[] aobject = new Object[l];

                for (int i1 = 0; i1 < l; ++i1) {
                    aobject[i1] = objectinputstream.readObject();
                }

                ImmutableSet immutableset = valueSet(comparator, Arrays.asList(aobject));

                if (immutableset.size() != aobject.length) {
                    throw new InvalidObjectException("Duplicate key-value pairs exist for key " + object);
                }

                immutablemap_builder.put(object, immutableset);
                j += l;
            }

            ImmutableMap immutablemap;

            try {
                immutablemap = immutablemap_builder.build();
            } catch (IllegalArgumentException illegalargumentexception) {
                throw (InvalidObjectException) (new InvalidObjectException(illegalargumentexception.getMessage())).initCause(illegalargumentexception);
            }

            ImmutableMultimap.FieldSettersHolder.MAP_FIELD_SETTER.set(this, immutablemap);
            ImmutableMultimap.FieldSettersHolder.SIZE_FIELD_SETTER.set(this, j);
            ImmutableMultimap.FieldSettersHolder.EMPTY_SET_FIELD_SETTER.set(this, emptySet(comparator));
        }
    }

    private static final class EntrySet<K, V> extends ImmutableSet<Entry<K, V>> {

        private final transient ImmutableSetMultimap<K, V> multimap;

        EntrySet(ImmutableSetMultimap<K, V> immutablesetmultimap) {
            this.multimap = immutablesetmultimap;
        }

        @Override
        public boolean contains(@Nullable Object object) {
            if (object instanceof Entry) {
                Entry entry = (Entry) object;

                return this.multimap.containsEntry(entry.getKey(), entry.getValue());
            } else {
                return false;
            }
        }

        public int size() {
            return this.multimap.size();
        }

        @Override
        public UnmodifiableIterator<Entry<K, V>> iterator() {
            return this.multimap.entryIterator();
        }

        @Override
        boolean isPartialView() {
            return false;
        }
    }

    public static final class Builder<K, V> extends ImmutableMultimap.Builder<K, V> {

        public Builder() {
            this.builderMultimap = new ImmutableSetMultimap.BuilderMultimap<>();
        }

        @Override
        public ImmutableSetMultimap.Builder<K, V> put(K k0, V v0) {
            this.builderMultimap.put(Preconditions.checkNotNull(k0), Preconditions.checkNotNull(v0));
            return this;
        }

        @Override
        public ImmutableSetMultimap.Builder<K, V> put(Entry<? extends K, ? extends V> entry) {
            this.builderMultimap.put(Preconditions.checkNotNull(entry.getKey()), Preconditions.checkNotNull(entry.getValue()));
            return this;
        }

        @Override
        public ImmutableSetMultimap.Builder<K, V> putAll(K k0, Iterable<? extends V> iterable) {
            Collection collection = this.builderMultimap.get(Preconditions.checkNotNull(k0));
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();

                collection.add(Preconditions.checkNotNull(object));
            }

            return this;
        }

        @Override
        public ImmutableSetMultimap.Builder<K, V> putAll(K k0, V... av) {
            return this.putAll(k0, (Iterable) Arrays.asList(av));
        }

        @Override
        public ImmutableSetMultimap.Builder<K, V> putAll(Multimap<? extends K, ? extends V> multimap) {
            Iterator iterator = multimap.asMap().entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                this.putAll(entry.getKey(), (Iterable) entry.getValue());
            }

            return this;
        }

        @Override
        public ImmutableSetMultimap.Builder<K, V> orderKeysBy(Comparator<? super K> comparator) {
            this.keyComparator = (Comparator) Preconditions.checkNotNull(comparator);
            return this;
        }

        @Override
        public ImmutableSetMultimap.Builder<K, V> orderValuesBy(Comparator<? super V> comparator) {
            super.orderValuesBy(comparator);
            return this;
        }

        @Override
        public ImmutableSetMultimap<K, V> build() {
            if (this.keyComparator != null) {
                ImmutableSetMultimap.BuilderMultimap immutablesetmultimap_buildermultimap = new ImmutableSetMultimap.BuilderMultimap<>();
                ArrayList arraylist = Lists.newArrayList((Iterable) this.builderMultimap.asMap().entrySet());

                Collections.sort(arraylist, Ordering.from(this.keyComparator).onKeys());
                Iterator iterator = arraylist.iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();

                    immutablesetmultimap_buildermultimap.putAll(entry.getKey(), (Iterable) entry.getValue());
                }

                this.builderMultimap = immutablesetmultimap_buildermultimap;
            }

            return ImmutableSetMultimap.copyOf(this.builderMultimap, this.valueComparator);
        }
    }

    private static class BuilderMultimap<K, V> extends AbstractMapBasedMultimap<K, V> {

        private static final long serialVersionUID = 0L;

        BuilderMultimap() {
            super(new LinkedHashMap());
        }

        @Override
        Collection<V> createCollection() {
            return Sets.newLinkedHashSet();
        }
    }
}
