package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

@Beta
@GwtCompatible
public abstract class MultimapBuilder<K0, V0> {

    private static final int DEFAULT_EXPECTED_KEYS = 8;

    private MultimapBuilder() {}

    public static MultimapBuilder.MultimapBuilderWithKeys<Object> hashKeys() {
        return hashKeys(8);
    }

    public static MultimapBuilder.MultimapBuilderWithKeys<Object> hashKeys(final int i) {
        CollectPreconditions.checkNonnegative(i, "expectedKeys");
        return new MultimapBuilder.MultimapBuilderWithKeys<Object>() {
            @Override
            <K, V> Map<K, Collection<V>> createMap() {
                return new HashMap(i);
            }
        };
    }

    public static MultimapBuilder.MultimapBuilderWithKeys<Object> linkedHashKeys() {
        return linkedHashKeys(8);
    }

    public static MultimapBuilder.MultimapBuilderWithKeys<Object> linkedHashKeys(final int i) {
        CollectPreconditions.checkNonnegative(i, "expectedKeys");
        return new MultimapBuilder.MultimapBuilderWithKeys<Object>() {
            @Override
            <K, V> Map<K, Collection<V>> createMap() {
                return new LinkedHashMap(i);
            }
        };
    }

    public static MultimapBuilder.MultimapBuilderWithKeys<Comparable> treeKeys() {
        return treeKeys(Ordering.natural());
    }

    public static <K0> MultimapBuilder.MultimapBuilderWithKeys<K0> treeKeys(final Comparator<K0> comparator) {
        Preconditions.checkNotNull(comparator);
        return new MultimapBuilder.MultimapBuilderWithKeys<K0>() {
            @Override
            <K extends K0, V> Map<K, Collection<V>> createMap() {
                return new TreeMap(comparator);
            }
        };
    }

    public static <K0 extends Enum<K0>> MultimapBuilder.MultimapBuilderWithKeys<K0> enumKeys(final Class<K0> oclass) {
        Preconditions.checkNotNull(oclass);
        return new MultimapBuilder.MultimapBuilderWithKeys<K0>() {
            @Override
            <K extends K0, V> Map<K, Collection<V>> createMap() {
                return new EnumMap(oclass);
            }
        };
    }

    public abstract <K extends K0, V extends V0> Multimap<K, V> build();

    public <K extends K0, V extends V0> Multimap<K, V> build(Multimap<? extends K, ? extends V> multimap) {
        Multimap multimap1 = this.build();

        multimap1.putAll(multimap);
        return multimap1;
    }

    public abstract static class SortedSetMultimapBuilder<K0, V0> extends MultimapBuilder.SetMultimapBuilder<K0, V0> {

        SortedSetMultimapBuilder() {}

        @Override
        public abstract <K extends K0, V extends V0> SortedSetMultimap<K, V> build();

        @Override
        public <K extends K0, V extends V0> SortedSetMultimap<K, V> build(Multimap<? extends K, ? extends V> multimap) {
            return (SortedSetMultimap) super.build(multimap);
        }
    }

    public abstract static class SetMultimapBuilder<K0, V0> extends MultimapBuilder<K0, V0> {

        SetMultimapBuilder() {
            super(null);
        }

        @Override
        public abstract <K extends K0, V extends V0> SetMultimap<K, V> build();

        @Override
        public <K extends K0, V extends V0> SetMultimap<K, V> build(Multimap<? extends K, ? extends V> multimap) {
            return (SetMultimap) super.build(multimap);
        }
    }

    public abstract static class ListMultimapBuilder<K0, V0> extends MultimapBuilder<K0, V0> {

        ListMultimapBuilder() {
            super(null);
        }

        @Override
        public abstract <K extends K0, V extends V0> ListMultimap<K, V> build();

        @Override
        public <K extends K0, V extends V0> ListMultimap<K, V> build(Multimap<? extends K, ? extends V> multimap) {
            return (ListMultimap) super.build(multimap);
        }
    }

    public abstract static class MultimapBuilderWithKeys<K0> {

        private static final int DEFAULT_EXPECTED_VALUES_PER_KEY = 2;

        MultimapBuilderWithKeys() {}

        abstract <K extends K0, V> Map<K, Collection<V>> createMap();

        public MultimapBuilder.ListMultimapBuilder<K0, Object> arrayListValues() {
            return this.arrayListValues(2);
        }

        public MultimapBuilder.ListMultimapBuilder<K0, Object> arrayListValues(final int i) {
            CollectPreconditions.checkNonnegative(i, "expectedValuesPerKey");
            return new MultimapBuilder.ListMultimapBuilder<K0, Object>() {
                @Override
                public <K extends K0, V> ListMultimap<K, V> build() {
                    return Multimaps.newListMultimap(MultimapBuilderWithKeys.this.createMap(), new MultimapBuilder.ArrayListSupplier<>(i));
                }
            };
        }

        public MultimapBuilder.ListMultimapBuilder<K0, Object> linkedListValues() {
            return new MultimapBuilder.ListMultimapBuilder<K0, Object>() {
                @Override
                public <K extends K0, V> ListMultimap<K, V> build() {
                    return Multimaps.newListMultimap(MultimapBuilderWithKeys.this.createMap(), MultimapBuilder.LinkedListSupplier.instance());
                }
            };
        }

        public MultimapBuilder.SetMultimapBuilder<K0, Object> hashSetValues() {
            return this.hashSetValues(2);
        }

        public MultimapBuilder.SetMultimapBuilder<K0, Object> hashSetValues(final int i) {
            CollectPreconditions.checkNonnegative(i, "expectedValuesPerKey");
            return new MultimapBuilder.SetMultimapBuilder<K0, Object>() {
                @Override
                public <K extends K0, V> SetMultimap<K, V> build() {
                    return Multimaps.newSetMultimap(MultimapBuilderWithKeys.this.createMap(), new MultimapBuilder.HashSetSupplier<>(i));
                }
            };
        }

        public MultimapBuilder.SetMultimapBuilder<K0, Object> linkedHashSetValues() {
            return this.linkedHashSetValues(2);
        }

        public MultimapBuilder.SetMultimapBuilder<K0, Object> linkedHashSetValues(final int i) {
            CollectPreconditions.checkNonnegative(i, "expectedValuesPerKey");
            return new MultimapBuilder.SetMultimapBuilder<K0, Object>() {
                @Override
                public <K extends K0, V> SetMultimap<K, V> build() {
                    return Multimaps.newSetMultimap(MultimapBuilderWithKeys.this.createMap(), new MultimapBuilder.LinkedHashSetSupplier<>(i));
                }
            };
        }

        public MultimapBuilder.SortedSetMultimapBuilder<K0, Comparable> treeSetValues() {
            return this.treeSetValues(Ordering.natural());
        }

        public <V0> MultimapBuilder.SortedSetMultimapBuilder<K0, V0> treeSetValues(final Comparator<V0> comparator) {
            Preconditions.checkNotNull(comparator, "comparator");
            return new MultimapBuilder.SortedSetMultimapBuilder<K0, V0>() {
                @Override
                public <K extends K0, V extends V0> SortedSetMultimap<K, V> build() {
                    return Multimaps.newSortedSetMultimap(MultimapBuilderWithKeys.this.createMap(), new MultimapBuilder.TreeSetSupplier<>(comparator));
                }
            };
        }

        public <V0 extends Enum<V0>> MultimapBuilder.SetMultimapBuilder<K0, V0> enumSetValues(final Class<V0> oclass) {
            Preconditions.checkNotNull(oclass, "valueClass");
            return new MultimapBuilder.SetMultimapBuilder<K0, V0>() {
                @Override
                public <K extends K0, V extends V0> SetMultimap<K, V> build() {
                    MultimapBuilder.EnumSetSupplier multimapbuilder_enumsetsupplier = new MultimapBuilder.EnumSetSupplier<>(oclass);

                    return Multimaps.newSetMultimap(MultimapBuilderWithKeys.this.createMap(), multimapbuilder_enumsetsupplier);
                }
            };
        }
    }

    private static final class EnumSetSupplier<V extends Enum<V>> implements Supplier<Set<V>>, Serializable {

        private final Class<V> clazz;

        EnumSetSupplier(Class<V> oclass) {
            this.clazz = (Class) Preconditions.checkNotNull(oclass);
        }

        @Override
        public Set<V> get() {
            return EnumSet.noneOf(this.clazz);
        }
    }

    private static final class TreeSetSupplier<V> implements Supplier<SortedSet<V>>, Serializable {

        private final Comparator<? super V> comparator;

        TreeSetSupplier(Comparator<? super V> comparator) {
            this.comparator = (Comparator) Preconditions.checkNotNull(comparator);
        }

        @Override
        public SortedSet<V> get() {
            return new TreeSet(this.comparator);
        }
    }

    private static final class LinkedHashSetSupplier<V> implements Supplier<Set<V>>, Serializable {

        private final int expectedValuesPerKey;

        LinkedHashSetSupplier(int i) {
            this.expectedValuesPerKey = CollectPreconditions.checkNonnegative(i, "expectedValuesPerKey");
        }

        @Override
        public Set<V> get() {
            return new LinkedHashSet(this.expectedValuesPerKey);
        }
    }

    private static final class HashSetSupplier<V> implements Supplier<Set<V>>, Serializable {

        private final int expectedValuesPerKey;

        HashSetSupplier(int i) {
            this.expectedValuesPerKey = CollectPreconditions.checkNonnegative(i, "expectedValuesPerKey");
        }

        @Override
        public Set<V> get() {
            return new HashSet(this.expectedValuesPerKey);
        }
    }

    private static enum LinkedListSupplier implements Supplier<List<Object>> {

        INSTANCE;

        private LinkedListSupplier() {}

        public static <V> Supplier<List<V>> instance() {
            MultimapBuilder.LinkedListSupplier multimapbuilder_linkedlistsupplier = MultimapBuilder.LinkedListSupplier.INSTANCE;

            return multimapbuilder_linkedlistsupplier;
        }

        @Override
        public List<Object> get() {
            return new LinkedList();
        }
    }

    private static final class ArrayListSupplier<V> implements Supplier<List<V>>, Serializable {

        private final int expectedValuesPerKey;

        ArrayListSupplier(int i) {
            this.expectedValuesPerKey = CollectPreconditions.checkNonnegative(i, "expectedValuesPerKey");
        }

        @Override
        public List<V> get() {
            return new ArrayList(this.expectedValuesPerKey);
        }
    }
}
