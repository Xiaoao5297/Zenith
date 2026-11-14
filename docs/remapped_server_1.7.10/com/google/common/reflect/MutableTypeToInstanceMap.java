package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ForwardingMapEntry;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public final class MutableTypeToInstanceMap<B> extends ForwardingMap<TypeToken<? extends B>, B> implements TypeToInstanceMap<B> {

    private final Map<TypeToken<? extends B>, B> backingMap = Maps.newHashMap();

    public MutableTypeToInstanceMap() {}

    @Nullable
    @Override
    public <T extends B> T getInstance(Class<T> oclass) {
        return this.trustedGet(TypeToken.of(oclass));
    }

    @Nullable
    @Override
    public <T extends B> T putInstance(Class<T> oclass, @Nullable T t0) {
        return this.trustedPut(TypeToken.of(oclass), t0);
    }

    @Nullable
    @Override
    public <T extends B> T getInstance(TypeToken<T> typetoken) {
        return this.trustedGet(typetoken.rejectTypeVariables());
    }

    @Nullable
    @Override
    public <T extends B> T putInstance(TypeToken<T> typetoken, @Nullable T t0) {
        return this.trustedPut(typetoken.rejectTypeVariables(), t0);
    }

    public B put(TypeToken<? extends B> typetoken, B b0) {
        throw new UnsupportedOperationException("Please use putInstance() instead.");
    }

    @Override
    public void putAll(Map<? extends TypeToken<? extends B>, ? extends B> map) {
        throw new UnsupportedOperationException("Please use putInstance() instead.");
    }

    @Override
    public Set<Entry<TypeToken<? extends B>, B>> entrySet() {
        return MutableTypeToInstanceMap.UnmodifiableEntry.transformEntries(super.entrySet());
    }

    @Override
    protected Map<TypeToken<? extends B>, B> delegate() {
        return this.backingMap;
    }

    @Nullable
    private <T extends B> T trustedPut(TypeToken<T> typetoken, @Nullable T t0) {
        return this.backingMap.put(typetoken, t0);
    }

    @Nullable
    private <T extends B> T trustedGet(TypeToken<T> typetoken) {
        return this.backingMap.get(typetoken);
    }

    private static final class UnmodifiableEntry<K, V> extends ForwardingMapEntry<K, V> {

        private final Entry<K, V> delegate;

        static <K, V> Set<Entry<K, V>> transformEntries(final Set<Entry<K, V>> set) {
            return new ForwardingSet<Entry<K, V>>() {
                @Override
                protected Set<Entry<K, V>> delegate() {
                    return set;
                }

                @Override
                public Iterator<Entry<K, V>> iterator() {
                    return MutableTypeToInstanceMap.UnmodifiableEntry.transformEntries(super.iterator());
                }

                @Override
                public Object[] toArray() {
                    return this.standardToArray();
                }

                @Override
                public <T> T[] toArray(T[] at) {
                    return this.standardToArray(at);
                }
            };
        }

        private static <K, V> Iterator<Entry<K, V>> transformEntries(Iterator<Entry<K, V>> iterator) {
            return Iterators.transform(iterator, new Function<Entry<K, V>, Entry<K, V>>() {
                public Entry<K, V> apply(Entry<K, V> entry) {
                    return new MutableTypeToInstanceMap.UnmodifiableEntry<>(entry);
                }
            });
        }

        private UnmodifiableEntry(Entry<K, V> entry) {
            this.delegate = (Entry) Preconditions.checkNotNull(entry);
        }

        @Override
        protected Entry<K, V> delegate() {
            return this.delegate;
        }

        @Override
        public V setValue(V v0) {
            throw new UnsupportedOperationException();
        }
    }
}
