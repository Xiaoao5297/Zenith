package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
class FilteredEntryMultimap<K, V> extends AbstractMultimap<K, V> implements FilteredMultimap<K, V> {

    final Multimap<K, V> unfiltered;
    final Predicate<? super Entry<K, V>> predicate;

    FilteredEntryMultimap(Multimap<K, V> multimap, Predicate<? super Entry<K, V>> predicate) {
        this.unfiltered = (Multimap) Preconditions.checkNotNull(multimap);
        this.predicate = (Predicate) Preconditions.checkNotNull(predicate);
    }

    @Override
    public Multimap<K, V> unfiltered() {
        return this.unfiltered;
    }

    @Override
    public Predicate<? super Entry<K, V>> entryPredicate() {
        return this.predicate;
    }

    @Override
    public int size() {
        return this.entries().size();
    }

    private boolean satisfies(K k0, V v0) {
        return this.predicate.apply(Maps.immutableEntry(k0, v0));
    }

    static <E> Collection<E> filterCollection(Collection<E> collection, Predicate<? super E> predicate) {
        return (Collection) (collection instanceof Set ? Sets.filter((Set) collection, predicate) : Collections2.filter(collection, predicate));
    }

    @Override
    public boolean containsKey(@Nullable Object object) {
        return this.asMap().get(object) != null;
    }

    @Override
    public Collection<V> removeAll(@Nullable Object object) {
        return (Collection) Objects.firstNonNull(this.asMap().remove(object), this.unmodifiableEmptyCollection());
    }

    Collection<V> unmodifiableEmptyCollection() {
        return (Collection) (this.unfiltered instanceof SetMultimap ? Collections.emptySet() : Collections.emptyList());
    }

    @Override
    public void clear() {
        this.entries().clear();
    }

    @Override
    public Collection<V> get(K k0) {
        return filterCollection(this.unfiltered.get(k0), new FilteredEntryMultimap.ValuePredicate(k0));
    }

    @Override
    Collection<Entry<K, V>> createEntries() {
        return filterCollection(this.unfiltered.entries(), this.predicate);
    }

    @Override
    Collection<V> createValues() {
        return new FilteredMultimapValues<>(this);
    }

    @Override
    Iterator<Entry<K, V>> entryIterator() {
        throw new AssertionError("should never be called");
    }

    @Override
    Map<K, Collection<V>> createAsMap() {
        return new FilteredEntryMultimap.AsMap();
    }

    @Override
    public Set<K> keySet() {
        return this.asMap().keySet();
    }

    boolean removeEntriesIf(Predicate<? super Entry<K, Collection<V>>> predicate) {
        Iterator iterator = this.unfiltered.asMap().entrySet().iterator();
        boolean flag = false;

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Object object = entry.getKey();
            Collection collection = filterCollection((Collection) entry.getValue(), new FilteredEntryMultimap.ValuePredicate(object));

            if (!collection.isEmpty() && predicate.apply(Maps.immutableEntry(object, collection))) {
                if (collection.size() == ((Collection) entry.getValue()).size()) {
                    iterator.remove();
                } else {
                    collection.clear();
                }

                flag = true;
            }
        }

        return flag;
    }

    @Override
    Multiset<K> createKeys() {
        return new FilteredEntryMultimap.Keys();
    }

    class Keys extends Multimaps.Keys<K, V> {

        Keys() {
            super(FilteredEntryMultimap.this);
        }

        @Override
        public int remove(@Nullable Object object, int i) {
            CollectPreconditions.checkNonnegative(i, "occurrences");
            if (i == 0) {
                return this.count(object);
            } else {
                Collection collection = (Collection) FilteredEntryMultimap.this.unfiltered.asMap().get(object);

                if (collection == null) {
                    return 0;
                } else {
                    Object object1 = object;
                    int j = 0;
                    Iterator iterator = collection.iterator();

                    while (iterator.hasNext()) {
                        Object object2 = iterator.next();

                        if (FilteredEntryMultimap.this.satisfies(object1, object2)) {
                            ++j;
                            if (j <= i) {
                                iterator.remove();
                            }
                        }
                    }

                    return j;
                }
            }
        }

        @Override
        public Set<Multiset.Entry<K>> entrySet() {
            return new Multisets.EntrySet<K>() {
                @Override
                Multiset<K> multiset() {
                    return Keys.this;
                }

                public Iterator<Multiset.Entry<K>> iterator() {
                    return Keys.this.entryIterator();
                }

                public int size() {
                    return FilteredEntryMultimap.this.keySet().size();
                }

                private boolean removeEntriesIf(final Predicate<? super Multiset.Entry<K>> predicate) {
                    return FilteredEntryMultimap.this.removeEntriesIf(new Predicate<Entry<K, Collection<V>>>() {
                        public boolean apply(Entry<K, Collection<V>> entry) {
                            return predicate.apply(Multisets.immutableEntry(entry.getKey(), ((Collection) entry.getValue()).size()));
                        }
                    });
                }

                @Override
                public boolean removeAll(Collection<?> collection) {
                    return this.removeEntriesIf(Predicates.in(collection));
                }

                @Override
                public boolean retainAll(Collection<?> collection) {
                    return this.removeEntriesIf(Predicates.not(Predicates.in(collection)));
                }
            };
        }
    }

    class AsMap extends Maps.ImprovedAbstractMap<K, Collection<V>> {

        AsMap() {}

        public boolean containsKey(@Nullable Object object) {
            return this.get(object) != null;
        }

        public void clear() {
            FilteredEntryMultimap.this.clear();
        }

        public Collection<V> get(@Nullable Object object) {
            Collection collection = (Collection) FilteredEntryMultimap.this.unfiltered.asMap().get(object);

            if (collection == null) {
                return null;
            } else {
                collection = FilteredEntryMultimap.filterCollection(collection, FilteredEntryMultimap.this.new ValuePredicate(object));
                return collection.isEmpty() ? null : collection;
            }
        }

        public Collection<V> remove(@Nullable Object object) {
            Collection collection = (Collection) FilteredEntryMultimap.this.unfiltered.asMap().get(object);

            if (collection == null) {
                return null;
            } else {
                Object object1 = object;
                ArrayList arraylist = Lists.newArrayList();
                Iterator iterator = collection.iterator();

                while (iterator.hasNext()) {
                    Object object2 = iterator.next();

                    if (FilteredEntryMultimap.this.satisfies(object1, object2)) {
                        iterator.remove();
                        arraylist.add(object2);
                    }
                }

                if (arraylist.isEmpty()) {
                    return null;
                } else if (FilteredEntryMultimap.this.unfiltered instanceof SetMultimap) {
                    return Collections.unmodifiableSet(Sets.newLinkedHashSet(arraylist));
                } else {
                    return Collections.unmodifiableList(arraylist);
                }
            }
        }

        @Override
        Set<K> createKeySet() {
            return new Maps.KeySet<K, Collection<V>>(this) {
                @Override
                public boolean removeAll(Collection<?> collection) {
                    return FilteredEntryMultimap.this.removeEntriesIf(Maps.keyPredicateOnEntries(Predicates.in(collection)));
                }

                @Override
                public boolean retainAll(Collection<?> collection) {
                    return FilteredEntryMultimap.this.removeEntriesIf(Maps.keyPredicateOnEntries(Predicates.not(Predicates.in(collection))));
                }

                @Override
                public boolean remove(@Nullable Object object) {
                    return AsMap.this.remove(object) != null;
                }
            };
        }

        @Override
        Set<Entry<K, Collection<V>>> createEntrySet() {
            return new Maps.EntrySet<K, Collection<V>>() {
                @Override
                Map<K, Collection<V>> map() {
                    return AsMap.this;
                }

                public Iterator<Entry<K, Collection<V>>> iterator() {
                    return new AbstractIterator<Entry<K, Collection<V>>>() {
                        final Iterator<Entry<K, Collection<V>>> backingIterator;

                        {
                            this.backingIterator = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();
                        }

                        @Override
                        protected Entry<K, Collection<V>> computeNext() {
                            while (true) {
                                if (this.backingIterator.hasNext()) {
                                    Entry entry = (Entry) this.backingIterator.next();
                                    Object object = entry.getKey();
                                    Collection collection = FilteredEntryMultimap.filterCollection((Collection) entry.getValue(), FilteredEntryMultimap.this.new ValuePredicate(object));

                                    if (collection.isEmpty()) {
                                        continue;
                                    }

                                    return Maps.immutableEntry(object, collection);
                                }

                                return (Entry) this.endOfData();
                            }
                        }
                    };
                }

                @Override
                public boolean removeAll(Collection<?> collection) {
                    return FilteredEntryMultimap.this.removeEntriesIf(Predicates.in(collection));
                }

                @Override
                public boolean retainAll(Collection<?> collection) {
                    return FilteredEntryMultimap.this.removeEntriesIf(Predicates.not(Predicates.in(collection)));
                }

                @Override
                public int size() {
                    return Iterators.size(this.iterator());
                }
            };
        }

        @Override
        Collection<Collection<V>> createValues() {
            return new Maps.Values<K, Collection<V>>(this) {
                @Override
                public boolean remove(@Nullable Object object) {
                    if (object instanceof Collection) {
                        Collection collection = (Collection) object;
                        Iterator iterator = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();

                        while (iterator.hasNext()) {
                            Entry entry = (Entry) iterator.next();
                            Object object1 = entry.getKey();
                            Collection collection1 = FilteredEntryMultimap.filterCollection((Collection) entry.getValue(), FilteredEntryMultimap.this.new ValuePredicate(object1));

                            if (!collection1.isEmpty() && collection.equals(collection1)) {
                                if (collection1.size() == ((Collection) entry.getValue()).size()) {
                                    iterator.remove();
                                } else {
                                    collection1.clear();
                                }

                                return true;
                            }
                        }
                    }

                    return false;
                }

                @Override
                public boolean removeAll(Collection<?> collection) {
                    return FilteredEntryMultimap.this.removeEntriesIf(Maps.valuePredicateOnEntries(Predicates.in(collection)));
                }

                @Override
                public boolean retainAll(Collection<?> collection) {
                    return FilteredEntryMultimap.this.removeEntriesIf(Maps.valuePredicateOnEntries(Predicates.not(Predicates.in(collection))));
                }
            };
        }
    }

    final class ValuePredicate implements Predicate<V> {

        private final K key;

        ValuePredicate(K k0) {
            this.key = k0;
        }

        @Override
        public boolean apply(@Nullable V v0) {
            return FilteredEntryMultimap.this.satisfies(this.key, v0);
        }
    }
}
