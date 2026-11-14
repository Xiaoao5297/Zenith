package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@GwtCompatible(serializable = true, emulated = true)
public final class ArrayListMultimap<K, V> extends AbstractListMultimap<K, V> {

    private static final int DEFAULT_VALUES_PER_KEY = 3;
    @VisibleForTesting
    transient int expectedValuesPerKey;
    @GwtIncompatible("Not needed in emulated source.")
    private static final long serialVersionUID = 0L;

    public static <K, V> ArrayListMultimap<K, V> create() {
        return new ArrayListMultimap<>();
    }

    public static <K, V> ArrayListMultimap<K, V> create(int i, int j) {
        return new ArrayListMultimap<>(i, j);
    }

    public static <K, V> ArrayListMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap) {
        return new ArrayListMultimap<>(multimap);
    }

    private ArrayListMultimap() {
        super(new HashMap());
        this.expectedValuesPerKey = 3;
    }

    private ArrayListMultimap(int i, int j) {
        super(Maps.newHashMapWithExpectedSize(i));
        CollectPreconditions.checkNonnegative(j, "expectedValuesPerKey");
        this.expectedValuesPerKey = j;
    }

    private ArrayListMultimap(Multimap<? extends K, ? extends V> multimap) {
        this(multimap.keySet().size(), multimap instanceof ArrayListMultimap ? ((ArrayListMultimap) multimap).expectedValuesPerKey : 3);
        this.putAll(multimap);
    }

    @Override
    List<V> createCollection() {
        return new ArrayList(this.expectedValuesPerKey);
    }

    public void trimToSize() {
        Iterator iterator = this.backingMap().values().iterator();

        while (iterator.hasNext()) {
            Collection collection = (Collection) iterator.next();
            ArrayList arraylist = (ArrayList) collection;

            arraylist.trimToSize();
        }

    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeInt(this.expectedValuesPerKey);
        Serialization.writeMultimap(this, objectoutputstream);
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        this.expectedValuesPerKey = objectinputstream.readInt();
        int i = Serialization.readCount(objectinputstream);
        HashMap hashmap = Maps.newHashMapWithExpectedSize(i);

        this.setMap(hashmap);
        Serialization.populateMultimap(this, objectinputstream, i);
    }
}
