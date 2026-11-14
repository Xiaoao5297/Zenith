package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
final class ImmutableEnumMap<K extends Enum<K>, V> extends ImmutableMap<K, V> {

    private final transient EnumMap<K, V> delegate;

    static <K extends Enum<K>, V> ImmutableMap<K, V> asImmutable(EnumMap<K, V> enummap) {
        switch (enummap.size()) {
            case 0:
                return ImmutableMap.of();
            case 1:
                Entry entry = (Entry) Iterables.getOnlyElement(enummap.entrySet());

                return ImmutableMap.of(entry.getKey(), entry.getValue());
            default:
                return new ImmutableEnumMap<>(enummap);
        }
    }

    private ImmutableEnumMap(EnumMap<K, V> enummap) {
        this.delegate = enummap;
        Preconditions.checkArgument(!enummap.isEmpty());
    }

    @Override
    ImmutableSet<K> createKeySet() {
        return new ImmutableSet<K>() {
            @Override
            public boolean contains(Object object) {
                return ImmutableEnumMap.this.delegate.containsKey(object);
            }

            public int size() {
                return ImmutableEnumMap.this.size();
            }

            @Override
            public UnmodifiableIterator<K> iterator() {
                return Iterators.unmodifiableIterator(ImmutableEnumMap.this.delegate.keySet().iterator());
            }

            @Override
            boolean isPartialView() {
                return true;
            }
        };
    }

    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean containsKey(@Nullable Object object) {
        return this.delegate.containsKey(object);
    }

    @Override
    public V get(Object object) {
        return this.delegate.get(object);
    }

    @Override
    ImmutableSet<Entry<K, V>> createEntrySet() {
        return new ImmutableMapEntrySet<K, V>() {
            @Override
            ImmutableMap<K, V> map() {
                return ImmutableEnumMap.this;
            }

            @Override
            public UnmodifiableIterator<Entry<K, V>> iterator() {
                return new UnmodifiableIterator<Entry<K, V>>() {
                    private final Iterator<Entry<K, V>> backingIterator;

                    {
                        this.backingIterator = ImmutableEnumMap.this.delegate.entrySet().iterator();
                    }

                    public boolean hasNext() {
                        return this.backingIterator.hasNext();
                    }

                    public Entry<K, V> next() {
                        Entry entry = (Entry) this.backingIterator.next();

                        return Maps.immutableEntry(entry.getKey(), entry.getValue());
                    }
                };
            }
        };
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    Object writeReplace() {
        return new ImmutableEnumMap.EnumSerializedForm<>(this.delegate);
    }

    private static class EnumSerializedForm<K extends Enum<K>, V> implements Serializable {

        final EnumMap<K, V> delegate;
        private static final long serialVersionUID = 0L;

        EnumSerializedForm(EnumMap<K, V> enummap) {
            this.delegate = enummap;
        }

        Object readResolve() {
            return new ImmutableEnumMap<>(this.delegate);
        }
    }
}
