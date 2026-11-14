package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Set;

@GwtCompatible(serializable = true, emulated = true)
public final class HashMultimap<K, V> extends AbstractSetMultimap<K, V> {

    private static final int DEFAULT_VALUES_PER_KEY = 2;
    @VisibleForTesting
    transient int expectedValuesPerKey = 2;
    @GwtIncompatible("Not needed in emulated source")
    private static final long serialVersionUID = 0L;

    public static <K, V> HashMultimap<K, V> create() {
        return new HashMultimap<>();
    }

    public static <K, V> HashMultimap<K, V> create(int i, int j) {
        return new HashMultimap<>(i, j);
    }

    public static <K, V> HashMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap) {
        return new HashMultimap<>(multimap);
    }

    private HashMultimap() {
        super(new HashMap());
    }

    private HashMultimap(int i, int j) {
        super(Maps.newHashMapWithExpectedSize(i));
        Preconditions.checkArgument(j >= 0);
        this.expectedValuesPerKey = j;
    }

    private HashMultimap(Multimap<? extends K, ? extends V> multimap) {
        super(Maps.newHashMapWithExpectedSize(multimap.keySet().size()));
        this.putAll(multimap);
    }

    @Override
    Set<V> createCollection() {
        return Sets.newHashSetWithExpectedSize(this.expectedValuesPerKey);
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeInt(this.expectedValuesPerKey);
        Serialization.writeMultimap(this, objectoutputstream);
    }

    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        this.expectedValuesPerKey = objectinputstream.readInt();
        int i = Serialization.readCount(objectinputstream);
        HashMap hashmap = Maps.newHashMapWithExpectedSize(i);

        this.setMap(hashmap);
        Serialization.populateMultimap(this, objectinputstream, i);
    }
}
