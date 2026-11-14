package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
public class ImmutableListMultimap<K, V> extends ImmutableMultimap<K, V> implements ListMultimap<K, V> {

    private transient ImmutableListMultimap<V, K> inverse;
    @GwtIncompatible("Not needed in emulated source")
    private static final long serialVersionUID = 0L;

    public static <K, V> ImmutableListMultimap<K, V> of() {
        return EmptyImmutableListMultimap.INSTANCE;
    }

    public static <K, V> ImmutableListMultimap<K, V> of(K k0, V v0) {
        ImmutableListMultimap.Builder immutablelistmultimap_builder = builder();

        immutablelistmultimap_builder.put(k0, v0);
        return immutablelistmultimap_builder.build();
    }

    public static <K, V> ImmutableListMultimap<K, V> of(K k0, V v0, K k1, V v1) {
        ImmutableListMultimap.Builder immutablelistmultimap_builder = builder();

        immutablelistmultimap_builder.put(k0, v0);
        immutablelistmultimap_builder.put(k1, v1);
        return immutablelistmultimap_builder.build();
    }

    public static <K, V> ImmutableListMultimap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2) {
        ImmutableListMultimap.Builder immutablelistmultimap_builder = builder();

        immutablelistmultimap_builder.put(k0, v0);
        immutablelistmultimap_builder.put(k1, v1);
        immutablelistmultimap_builder.put(k2, v2);
        return immutablelistmultimap_builder.build();
    }

    public static <K, V> ImmutableListMultimap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3) {
        ImmutableListMultimap.Builder immutablelistmultimap_builder = builder();

        immutablelistmultimap_builder.put(k0, v0);
        immutablelistmultimap_builder.put(k1, v1);
        immutablelistmultimap_builder.put(k2, v2);
        immutablelistmultimap_builder.put(k3, v3);
        return immutablelistmultimap_builder.build();
    }

    public static <K, V> ImmutableListMultimap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        ImmutableListMultimap.Builder immutablelistmultimap_builder = builder();

        immutablelistmultimap_builder.put(k0, v0);
        immutablelistmultimap_builder.put(k1, v1);
        immutablelistmultimap_builder.put(k2, v2);
        immutablelistmultimap_builder.put(k3, v3);
        immutablelistmultimap_builder.put(k4, v4);
        return immutablelistmultimap_builder.build();
    }

    public static <K, V> ImmutableListMultimap.Builder<K, V> builder() {
        return new ImmutableListMultimap.Builder<>();
    }

    public static <K, V> ImmutableListMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap) {
        if (multimap.isEmpty()) {
            return of();
        } else {
            if (multimap instanceof ImmutableListMultimap) {
                ImmutableListMultimap immutablelistmultimap = (ImmutableListMultimap) multimap;

                if (!immutablelistmultimap.isPartialView()) {
                    return immutablelistmultimap;
                }
            }

            ImmutableMap.Builder immutablemap_builder = ImmutableMap.builder();
            int i = 0;
            Iterator iterator = multimap.asMap().entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                ImmutableList immutablelist = ImmutableList.copyOf((Collection) entry.getValue());

                if (!immutablelist.isEmpty()) {
                    immutablemap_builder.put(entry.getKey(), immutablelist);
                    i += immutablelist.size();
                }
            }

            return new ImmutableListMultimap<>(immutablemap_builder.build(), i);
        }
    }

    ImmutableListMultimap(ImmutableMap<K, ImmutableList<V>> immutablemap, int i) {
        super(immutablemap, i);
    }

    @Override
    public ImmutableList<V> get(@Nullable K k0) {
        ImmutableList immutablelist = (ImmutableList) this.map.get(k0);

        return immutablelist == null ? ImmutableList.of() : immutablelist;
    }

    @Override
    public ImmutableListMultimap<V, K> inverse() {
        ImmutableListMultimap immutablelistmultimap = this.inverse;

        return immutablelistmultimap == null ? (this.inverse = this.invert()) : immutablelistmultimap;
    }

    private ImmutableListMultimap<V, K> invert() {
        ImmutableListMultimap.Builder immutablelistmultimap_builder = builder();
        Iterator iterator = this.entries().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            immutablelistmultimap_builder.put(entry.getValue(), entry.getKey());
        }

        ImmutableListMultimap immutablelistmultimap = immutablelistmultimap_builder.build();

        immutablelistmultimap.inverse = this;
        return immutablelistmultimap;
    }

    /** @deprecated */
    @Deprecated
    @Override
    public ImmutableList<V> removeAll(Object object) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public ImmutableList<V> replaceValues(K k0, Iterable<? extends V> iterable) {
        throw new UnsupportedOperationException();
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        Serialization.writeMultimap(this, objectoutputstream);
    }

    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
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

                immutablemap_builder.put(object, ImmutableList.copyOf(aobject));
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
        }
    }

    public static final class Builder<K, V> extends ImmutableMultimap.Builder<K, V> {

        public Builder() {}

        @Override
        public ImmutableListMultimap.Builder<K, V> put(K k0, V v0) {
            super.put(k0, v0);
            return this;
        }

        @Override
        public ImmutableListMultimap.Builder<K, V> put(Entry<? extends K, ? extends V> entry) {
            super.put(entry);
            return this;
        }

        @Override
        public ImmutableListMultimap.Builder<K, V> putAll(K k0, Iterable<? extends V> iterable) {
            super.putAll(k0, iterable);
            return this;
        }

        @Override
        public ImmutableListMultimap.Builder<K, V> putAll(K k0, V... av) {
            super.putAll(k0, av);
            return this;
        }

        @Override
        public ImmutableListMultimap.Builder<K, V> putAll(Multimap<? extends K, ? extends V> multimap) {
            super.putAll(multimap);
            return this;
        }

        @Override
        public ImmutableListMultimap.Builder<K, V> orderKeysBy(Comparator<? super K> comparator) {
            super.orderKeysBy(comparator);
            return this;
        }

        @Override
        public ImmutableListMultimap.Builder<K, V> orderValuesBy(Comparator<? super V> comparator) {
            super.orderValuesBy(comparator);
            return this;
        }

        @Override
        public ImmutableListMultimap<K, V> build() {
            return (ImmutableListMultimap) super.build();
        }
    }
}
