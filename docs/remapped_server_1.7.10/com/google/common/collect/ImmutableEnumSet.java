package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;

@GwtCompatible(serializable = true, emulated = true)
final class ImmutableEnumSet<E extends Enum<E>> extends ImmutableSet<E> {

    private final transient EnumSet<E> delegate;
    private transient int hashCode;

    static <E extends Enum<E>> ImmutableSet<E> asImmutable(EnumSet<E> enumset) {
        switch (enumset.size()) {
            case 0:
                return ImmutableSet.of();
            case 1:
                return ImmutableSet.of(Iterables.getOnlyElement(enumset));
            default:
                return new ImmutableEnumSet<>(enumset);
        }
    }

    private ImmutableEnumSet(EnumSet<E> enumset) {
        this.delegate = enumset;
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return Iterators.unmodifiableIterator(this.delegate.iterator());
    }

    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean contains(Object object) {
        return this.delegate.contains(object);
    }

    public boolean containsAll(Collection<?> collection) {
        return this.delegate.containsAll(collection);
    }

    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean equals(Object object) {
        return object == this || this.delegate.equals(object);
    }

    @Override
    public int hashCode() {
        int i = this.hashCode;

        return i == 0 ? (this.hashCode = this.delegate.hashCode()) : i;
    }

    public String toString() {
        return this.delegate.toString();
    }

    @Override
    Object writeReplace() {
        return new ImmutableEnumSet.EnumSerializedForm<>(this.delegate);
    }

    private static class EnumSerializedForm<E extends Enum<E>> implements Serializable {

        final EnumSet<E> delegate;
        private static final long serialVersionUID = 0L;

        EnumSerializedForm(EnumSet<E> enumset) {
            this.delegate = enumset;
        }

        Object readResolve() {
            return new ImmutableEnumSet<>(this.delegate.clone());
        }
    }
}
