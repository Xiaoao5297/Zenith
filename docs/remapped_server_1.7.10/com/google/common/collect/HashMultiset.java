package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

@GwtCompatible(serializable = true, emulated = true)
public final class HashMultiset<E> extends AbstractMapBasedMultiset<E> {

    @GwtIncompatible("Not needed in emulated source.")
    private static final long serialVersionUID = 0L;

    public static <E> HashMultiset<E> create() {
        return new HashMultiset<>();
    }

    public static <E> HashMultiset<E> create(int i) {
        return new HashMultiset<>(i);
    }

    public static <E> HashMultiset<E> create(Iterable<? extends E> iterable) {
        HashMultiset hashmultiset = create(Multisets.inferDistinctElements(iterable));

        Iterables.addAll(hashmultiset, iterable);
        return hashmultiset;
    }

    private HashMultiset() {
        super(new HashMap());
    }

    private HashMultiset(int i) {
        super(Maps.newHashMapWithExpectedSize(i));
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        Serialization.writeMultiset(this, objectoutputstream);
    }

    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        int i = Serialization.readCount(objectinputstream);

        this.setBackingMap(Maps.newHashMapWithExpectedSize(i));
        Serialization.populateMultiset(this, objectinputstream, i);
    }
}
