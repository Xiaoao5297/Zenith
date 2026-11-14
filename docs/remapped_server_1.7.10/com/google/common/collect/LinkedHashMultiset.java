package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;

@GwtCompatible(serializable = true, emulated = true)
public final class LinkedHashMultiset<E> extends AbstractMapBasedMultiset<E> {

    @GwtIncompatible("not needed in emulated source")
    private static final long serialVersionUID = 0L;

    public static <E> LinkedHashMultiset<E> create() {
        return new LinkedHashMultiset<>();
    }

    public static <E> LinkedHashMultiset<E> create(int i) {
        return new LinkedHashMultiset<>(i);
    }

    public static <E> LinkedHashMultiset<E> create(Iterable<? extends E> iterable) {
        LinkedHashMultiset linkedhashmultiset = create(Multisets.inferDistinctElements(iterable));

        Iterables.addAll(linkedhashmultiset, iterable);
        return linkedhashmultiset;
    }

    private LinkedHashMultiset() {
        super(new LinkedHashMap());
    }

    private LinkedHashMultiset(int i) {
        super(new LinkedHashMap(Maps.capacity(i)));
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

        this.setBackingMap(new LinkedHashMap(Maps.capacity(i)));
        Serialization.populateMultiset(this, objectinputstream, i);
    }
}
