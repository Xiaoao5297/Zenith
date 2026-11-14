package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import javax.annotation.Nullable;

@GwtCompatible
@Beta
final class SortedLists {

    private SortedLists() {}

    public static <E extends Comparable> int binarySearch(List<? extends E> list, E e0, SortedLists.KeyPresentBehavior sortedlists_keypresentbehavior, SortedLists.KeyAbsentBehavior sortedlists_keyabsentbehavior) {
        Preconditions.checkNotNull(e0);
        return binarySearch(list, Preconditions.checkNotNull(e0), (Comparator) Ordering.natural(), sortedlists_keypresentbehavior, sortedlists_keyabsentbehavior);
    }

    public static <E, K extends Comparable> int binarySearch(List<E> list, Function<? super E, K> function, @Nullable K k0, SortedLists.KeyPresentBehavior sortedlists_keypresentbehavior, SortedLists.KeyAbsentBehavior sortedlists_keyabsentbehavior) {
        return binarySearch(list, function, k0, Ordering.natural(), sortedlists_keypresentbehavior, sortedlists_keyabsentbehavior);
    }

    public static <E, K> int binarySearch(List<E> list, Function<? super E, K> function, @Nullable K k0, Comparator<? super K> comparator, SortedLists.KeyPresentBehavior sortedlists_keypresentbehavior, SortedLists.KeyAbsentBehavior sortedlists_keyabsentbehavior) {
        return binarySearch(Lists.transform(list, function), k0, comparator, sortedlists_keypresentbehavior, sortedlists_keyabsentbehavior);
    }

    public static <E> int binarySearch(List<? extends E> list, @Nullable E e0, Comparator<? super E> comparator, SortedLists.KeyPresentBehavior sortedlists_keypresentbehavior, SortedLists.KeyAbsentBehavior sortedlists_keyabsentbehavior) {
        Preconditions.checkNotNull(comparator);
        Preconditions.checkNotNull(list);
        Preconditions.checkNotNull(sortedlists_keypresentbehavior);
        Preconditions.checkNotNull(sortedlists_keyabsentbehavior);
        if (!(list instanceof RandomAccess)) {
            list = Lists.newArrayList((Iterable) list);
        }

        int i = 0;
        int j = ((List) list).size() - 1;

        while (i <= j) {
            int k = i + j >>> 1;
            int l = comparator.compare(e0, ((List) list).get(k));

            if (l < 0) {
                j = k - 1;
            } else {
                if (l <= 0) {
                    return i + sortedlists_keypresentbehavior.resultIndex(comparator, e0, ((List) list).subList(i, j + 1), k - i);
                }

                i = k + 1;
            }
        }

        return sortedlists_keyabsentbehavior.resultIndex(i);
    }

    public static enum KeyAbsentBehavior {

        NEXT_LOWER {
            @Override
            int resultIndex(int i) {
                return i - 1;
            }
        },
        NEXT_HIGHER {
            @Override
            public int resultIndex(int i) {
                return i;
            }
        },
        INVERTED_INSERTION_INDEX {
            @Override
            public int resultIndex(int i) {
                return ~i;
            }
        };

        private KeyAbsentBehavior() {}

        abstract int resultIndex(int i);
    }

    public static enum KeyPresentBehavior {

        ANY_PRESENT {
            @Override
            <E> int resultIndex(Comparator<? super E> comparator, E e0, List<? extends E> list, int i) {
                return i;
            }
        },
        LAST_PRESENT {
            @Override
            <E> int resultIndex(Comparator<? super E> comparator, E e0, List<? extends E> list, int i) {
                int j = i;
                int k = list.size() - 1;

                while (j < k) {
                    int l = j + k + 1 >>> 1;
                    int i1 = comparator.compare(list.get(l), e0);

                    if (i1 > 0) {
                        k = l - 1;
                    } else {
                        j = l;
                    }
                }

                return j;
            }
        },
        FIRST_PRESENT {
            @Override
            <E> int resultIndex(Comparator<? super E> comparator, E e0, List<? extends E> list, int i) {
                int j = 0;
                int k = i;

                while (j < k) {
                    int l = j + k >>> 1;
                    int i1 = comparator.compare(list.get(l), e0);

                    if (i1 < 0) {
                        j = l + 1;
                    } else {
                        k = l;
                    }
                }

                return j;
            }
        },
        FIRST_AFTER {
            @Override
            public <E> int resultIndex(Comparator<? super E> comparator, E e0, List<? extends E> list, int i) {
                return null.LAST_PRESENT.resultIndex(comparator, e0, list, i) + 1;
            }
        },
        LAST_BEFORE {
            @Override
            public <E> int resultIndex(Comparator<? super E> comparator, E e0, List<? extends E> list, int i) {
                return null.FIRST_PRESENT.resultIndex(comparator, e0, list, i) - 1;
            }
        };

        private KeyPresentBehavior() {}

        abstract <E> int resultIndex(Comparator<? super E> comparator, E e0, List<? extends E> list, int i);
    }
}
