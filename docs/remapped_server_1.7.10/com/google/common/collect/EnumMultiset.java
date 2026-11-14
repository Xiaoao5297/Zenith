package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.Iterator;

@GwtCompatible(emulated = true)
public final class EnumMultiset<E extends Enum<E>> extends AbstractMapBasedMultiset<E> {

    private transient Class<E> type;
    @GwtIncompatible("Not needed in emulated source")
    private static final long serialVersionUID = 0L;

    public static <E extends Enum<E>> EnumMultiset<E> create(Class<E> oclass) {
        return new EnumMultiset<>(oclass);
    }

    public static <E extends Enum<E>> EnumMultiset<E> create(Iterable<E> iterable) {
        Iterator iterator = iterable.iterator();

        Preconditions.checkArgument(iterator.hasNext(), "EnumMultiset constructor passed empty Iterable");
        EnumMultiset enummultiset = new EnumMultiset<>(((Enum) iterator.next()).getDeclaringClass());

        Iterables.addAll(enummultiset, iterable);
        return enummultiset;
    }

    public static <E extends Enum<E>> EnumMultiset<E> create(Iterable<E> iterable, Class<E> oclass) {
        EnumMultiset enummultiset = create(oclass);

        Iterables.addAll(enummultiset, iterable);
        return enummultiset;
    }

    private EnumMultiset(Class<E> oclass) {
        super(WellBehavedMap.wrap(new EnumMap(oclass)));
        this.type = oclass;
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeObject(this.type);
        Serialization.writeMultiset(this, objectoutputstream);
    }

    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        Class oclass = (Class) objectinputstream.readObject();

        this.type = oclass;
        this.setBackingMap(WellBehavedMap.wrap(new EnumMap(this.type)));
        Serialization.populateMultiset(this, objectinputstream);
    }
}
