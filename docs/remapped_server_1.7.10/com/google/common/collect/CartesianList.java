package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import javax.annotation.Nullable;

@GwtCompatible
final class CartesianList<E> extends AbstractList<List<E>> implements RandomAccess {

    private final transient ImmutableList<List<E>> axes;
    private final transient int[] axesSizeProduct;

    static <E> List<List<E>> create(List<? extends List<? extends E>> list) {
        ImmutableList.Builder immutablelist_builder = new ImmutableList.Builder<>(list.size());
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            List list1 = (List) iterator.next();
            ImmutableList immutablelist = ImmutableList.copyOf((Collection) list1);

            if (immutablelist.isEmpty()) {
                return ImmutableList.of();
            }

            immutablelist_builder.add((Object) immutablelist);
        }

        return new CartesianList<>(immutablelist_builder.build());
    }

    CartesianList(ImmutableList<List<E>> immutablelist) {
        this.axes = immutablelist;
        int[] aint = new int[immutablelist.size() + 1];

        aint[immutablelist.size()] = 1;

        try {
            for (int i = immutablelist.size() - 1; i >= 0; --i) {
                aint[i] = IntMath.checkedMultiply(aint[i + 1], ((List) immutablelist.get(i)).size());
            }
        } catch (ArithmeticException arithmeticexception) {
            throw new IllegalArgumentException("Cartesian product too large; must have size at most Integer.MAX_VALUE");
        }

        this.axesSizeProduct = aint;
    }

    private int getAxisIndexForProductIndex(int i, int j) {
        return i / this.axesSizeProduct[j + 1] % ((List) this.axes.get(j)).size();
    }

    public ImmutableList<E> get(final int i) {
        Preconditions.checkElementIndex(i, this.size());
        return new ImmutableList<E>() {
            public int size() {
                return CartesianList.this.axes.size();
            }

            public E get(int j) {
                Preconditions.checkElementIndex(j, this.size());
                int k = CartesianList.this.getAxisIndexForProductIndex(i, j);

                return ((List) CartesianList.this.axes.get(j)).get(k);
            }

            @Override
            boolean isPartialView() {
                return true;
            }
        };
    }

    public int size() {
        return this.axesSizeProduct[0];
    }

    public boolean contains(@Nullable Object object) {
        if (!(object instanceof List)) {
            return false;
        } else {
            List list = (List) object;

            if (list.size() != this.axes.size()) {
                return false;
            } else {
                ListIterator listiterator = list.listIterator();

                int i;

                do {
                    if (!listiterator.hasNext()) {
                        return true;
                    }

                    i = listiterator.nextIndex();
                } while (((List) this.axes.get(i)).contains(listiterator.next()));

                return false;
            }
        }
    }
}
