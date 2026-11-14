package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

@GwtCompatible(serializable = true, emulated = true)
abstract class ImmutableAsList<E> extends ImmutableList<E> {

    ImmutableAsList() {}

    abstract ImmutableCollection<E> delegateCollection();

    @Override
    public boolean contains(Object object) {
        return this.delegateCollection().contains(object);
    }

    public int size() {
        return this.delegateCollection().size();
    }

    public boolean isEmpty() {
        return this.delegateCollection().isEmpty();
    }

    @Override
    boolean isPartialView() {
        return this.delegateCollection().isPartialView();
    }

    @GwtIncompatible("serialization")
    private void readObject(ObjectInputStream objectinputstream) throws InvalidObjectException {
        throw new InvalidObjectException("Use SerializedForm");
    }

    @GwtIncompatible("serialization")
    @Override
    Object writeReplace() {
        return new ImmutableAsList.SerializedForm(this.delegateCollection());
    }

    @GwtIncompatible("serialization")
    static class SerializedForm implements Serializable {

        final ImmutableCollection<?> collection;
        private static final long serialVersionUID = 0L;

        SerializedForm(ImmutableCollection<?> immutablecollection) {
            this.collection = immutablecollection;
        }

        Object readResolve() {
            return this.collection.asList();
        }
    }
}
