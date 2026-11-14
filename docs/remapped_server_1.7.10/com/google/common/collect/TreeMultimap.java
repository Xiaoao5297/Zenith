package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
public class TreeMultimap<K, V> extends AbstractSortedKeySortedSetMultimap<K, V> {

    private transient Comparator<? super K> keyComparator;
    private transient Comparator<? super V> valueComparator;
    @GwtIncompatible("not needed in emulated source")
    private static final long serialVersionUID = 0L;

    public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> create() {
        return new TreeMultimap<>(Ordering.natural(), Ordering.natural());
    }

    public static <K, V> TreeMultimap<K, V> create(Comparator<? super K> comparator, Comparator<? super V> comparator1) {
        return new TreeMultimap<>((Comparator) Preconditions.checkNotNull(comparator), (Comparator) Preconditions.checkNotNull(comparator1));
    }

    public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap) {
        return new TreeMultimap<>(Ordering.natural(), Ordering.natural(), multimap);
    }

    TreeMultimap(Comparator<? super K> comparator, Comparator<? super V> comparator1) {
        super((SortedMap) (new TreeMap(comparator)));
        this.keyComparator = comparator;
        this.valueComparator = comparator1;
    }

    private TreeMultimap(Comparator<? super K> comparator, Comparator<? super V> comparator1, Multimap<? extends K, ? extends V> multimap) {
        this(comparator, comparator1);
        this.putAll(multimap);
    }

    @Override
    SortedSet<V> createCollection() {
        return new TreeSet(this.valueComparator);
    }

    @Override
    Collection<V> createCollection(@Nullable K k0) {
        if (k0 == null) {
            this.keyComparator().compare(k0, k0);
        }

        return super.createCollection(k0);
    }

    public Comparator<? super K> keyComparator() {
        return this.keyComparator;
    }

    @Override
    public Comparator<? super V> valueComparator() {
        return this.valueComparator;
    }

    @GwtIncompatible("NavigableMap")
    @Override
    NavigableMap<K, Collection<V>> backingMap() {
        return (NavigableMap) super.backingMap();
    }

    @GwtIncompatible("NavigableSet")
    @Override
    public NavigableSet<V> get(@Nullable K k0) {
        return (NavigableSet) super.get(k0);
    }

    @GwtIncompatible("NavigableSet")
    @Override
    Collection<V> unmodifiableCollectionSubclass(Collection<V> collection) {
        return Sets.unmodifiableNavigableSet((NavigableSet) collection);
    }

    @GwtIncompatible("NavigableSet")
    @Override
    Collection<V> wrapCollection(K k0, Collection<V> collection) {
        return new AbstractMapBasedMultimap.WrappedNavigableSet(k0, (NavigableSet) collection, (AbstractMapBasedMultimap.WrappedCollection) null);
    }

    @GwtIncompatible("NavigableSet")
    @Override
    public NavigableSet<K> keySet() {
        return (NavigableSet) super.keySet();
    }

    @GwtIncompatible("NavigableSet")
    @Override
    NavigableSet<K> createKeySet() {
        return new AbstractMapBasedMultimap.NavigableKeySet(this.backingMap());
    }

    @GwtIncompatible("NavigableMap")
    @Override
    public NavigableMap<K, Collection<V>> asMap() {
        return (NavigableMap) super.asMap();
    }

    @GwtIncompatible("NavigableMap")
    @Override
    NavigableMap<K, Collection<V>> createAsMap() {
        return new AbstractMapBasedMultimap.NavigableAsMap(this.backingMap());
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeObject(this.keyComparator());
        objectoutputstream.writeObject(this.valueComparator());
        Serialization.writeMultimap(this, objectoutputstream);
    }

    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        this.keyComparator = (Comparator) Preconditions.checkNotNull((Comparator) objectinputstream.readObject());
        this.valueComparator = (Comparator) Preconditions.checkNotNull((Comparator) objectinputstream.readObject());
        this.setMap(new TreeMap(this.keyComparator));
        Serialization.populateMultimap(this, objectinputstream);
    }
}
