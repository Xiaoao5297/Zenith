package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

@Beta
public final class MinMaxPriorityQueue<E> extends AbstractQueue<E> {

    private final MinMaxPriorityQueue<E>.Heap minHeap;
    private final MinMaxPriorityQueue<E>.Heap maxHeap;
    @VisibleForTesting
    final int maximumSize;
    private Object[] queue;
    private int size;
    private int modCount;
    private static final int EVEN_POWERS_OF_TWO = 1431655765;
    private static final int ODD_POWERS_OF_TWO = -1431655766;
    private static final int DEFAULT_CAPACITY = 11;

    public static <E extends Comparable<E>> MinMaxPriorityQueue<E> create() {
        return (new MinMaxPriorityQueue.Builder<>(Ordering.natural())).create();
    }

    public static <E extends Comparable<E>> MinMaxPriorityQueue<E> create(Iterable<? extends E> iterable) {
        return (new MinMaxPriorityQueue.Builder<>(Ordering.natural())).create(iterable);
    }

    public static <B> MinMaxPriorityQueue.Builder<B> orderedBy(Comparator<B> comparator) {
        return new MinMaxPriorityQueue.Builder<>(comparator);
    }

    public static MinMaxPriorityQueue.Builder<Comparable> expectedSize(int i) {
        return (new MinMaxPriorityQueue.Builder<>(Ordering.natural())).expectedSize(i);
    }

    public static MinMaxPriorityQueue.Builder<Comparable> maximumSize(int i) {
        return (new MinMaxPriorityQueue.Builder<>(Ordering.natural())).maximumSize(i);
    }

    private MinMaxPriorityQueue(MinMaxPriorityQueue.Builder<? super E> minmaxpriorityqueue_builder, int i) {
        Ordering ordering = minmaxpriorityqueue_builder.ordering();

        this.minHeap = new MinMaxPriorityQueue.Heap(ordering);
        this.maxHeap = new MinMaxPriorityQueue.Heap(ordering.reverse());
        this.minHeap.otherHeap = this.maxHeap;
        this.maxHeap.otherHeap = this.minHeap;
        this.maximumSize = minmaxpriorityqueue_builder.maximumSize;
        this.queue = new Object[i];
    }

    public int size() {
        return this.size;
    }

    public boolean add(E e0) {
        this.offer(e0);
        return true;
    }

    public boolean addAll(Collection<? extends E> collection) {
        boolean flag = false;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); flag = true) {
            Object object = iterator.next();

            this.offer(object);
        }

        return flag;
    }

    public boolean offer(E e0) {
        Preconditions.checkNotNull(e0);
        ++this.modCount;
        int i = this.size++;

        this.growIfNeeded();
        this.heapForIndex(i).bubbleUp(i, e0);
        return this.size <= this.maximumSize || this.pollLast() != e0;
    }

    public E poll() {
        return this.isEmpty() ? null : this.removeAndGet(0);
    }

    E elementData(int i) {
        return this.queue[i];
    }

    public E peek() {
        return this.isEmpty() ? null : this.elementData(0);
    }

    private int getMaxElementIndex() {
        switch (this.size) {
            case 1:
                return 0;
            case 2:
                return 1;
            default:
                return this.maxHeap.compareElements(1, 2) <= 0 ? 1 : 2;
        }
    }

    public E pollFirst() {
        return this.poll();
    }

    public E removeFirst() {
        return this.remove();
    }

    public E peekFirst() {
        return this.peek();
    }

    public E pollLast() {
        return this.isEmpty() ? null : this.removeAndGet(this.getMaxElementIndex());
    }

    public E removeLast() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return this.removeAndGet(this.getMaxElementIndex());
        }
    }

    public E peekLast() {
        return this.isEmpty() ? null : this.elementData(this.getMaxElementIndex());
    }

    @VisibleForTesting
    MinMaxPriorityQueue.MoveDesc<E> removeAt(int i) {
        Preconditions.checkPositionIndex(i, this.size);
        ++this.modCount;
        --this.size;
        if (this.size == i) {
            this.queue[this.size] = null;
            return null;
        } else {
            Object object = this.elementData(this.size);
            int j = this.heapForIndex(this.size).getCorrectLastElement(object);
            Object object1 = this.elementData(this.size);

            this.queue[this.size] = null;
            MinMaxPriorityQueue.MoveDesc minmaxpriorityqueue_movedesc = this.fillHole(i, object1);

            return j < i ? (minmaxpriorityqueue_movedesc == null ? new MinMaxPriorityQueue.MoveDesc<>(object, object1) : new MinMaxPriorityQueue.MoveDesc<>(object, minmaxpriorityqueue_movedesc.replaced)) : minmaxpriorityqueue_movedesc;
        }
    }

    private MinMaxPriorityQueue.MoveDesc<E> fillHole(int i, E e0) {
        MinMaxPriorityQueue.Heap minmaxpriorityqueue_heap = this.heapForIndex(i);
        int j = minmaxpriorityqueue_heap.fillHoleAt(i);
        int k = minmaxpriorityqueue_heap.bubbleUpAlternatingLevels(j, e0);

        return k == j ? minmaxpriorityqueue_heap.tryCrossOverAndBubbleUp(i, j, e0) : (k < i ? new MinMaxPriorityQueue.MoveDesc<>(e0, this.elementData(i)) : null);
    }

    private E removeAndGet(int i) {
        Object object = this.elementData(i);

        this.removeAt(i);
        return object;
    }

    private MinMaxPriorityQueue<E>.Heap heapForIndex(int i) {
        return isEvenLevel(i) ? this.minHeap : this.maxHeap;
    }

    @VisibleForTesting
    static boolean isEvenLevel(int i) {
        int j = i + 1;

        Preconditions.checkState(j > 0, "negative index");
        return (j & 1431655765) > (j & -1431655766);
    }

    @VisibleForTesting
    boolean isIntact() {
        for (int i = 1; i < this.size; ++i) {
            if (!this.heapForIndex(i).verifyIndex(i)) {
                return false;
            }
        }

        return true;
    }

    public Iterator<E> iterator() {
        return new MinMaxPriorityQueue.QueueIterator();
    }

    public void clear() {
        for (int i = 0; i < this.size; ++i) {
            this.queue[i] = null;
        }

        this.size = 0;
    }

    public Object[] toArray() {
        Object[] aobject = new Object[this.size];

        System.arraycopy(this.queue, 0, aobject, 0, this.size);
        return aobject;
    }

    public Comparator<? super E> comparator() {
        return this.minHeap.ordering;
    }

    @VisibleForTesting
    int capacity() {
        return this.queue.length;
    }

    @VisibleForTesting
    static int initialQueueSize(int i, int j, Iterable<?> iterable) {
        int k = i == -1 ? 11 : i;

        if (iterable instanceof Collection) {
            int l = ((Collection) iterable).size();

            k = Math.max(k, l);
        }

        return capAtMaximumSize(k, j);
    }

    private void growIfNeeded() {
        if (this.size > this.queue.length) {
            int i = this.calculateNewCapacity();
            Object[] aobject = new Object[i];

            System.arraycopy(this.queue, 0, aobject, 0, this.queue.length);
            this.queue = aobject;
        }

    }

    private int calculateNewCapacity() {
        int i = this.queue.length;
        int j = i < 64 ? (i + 1) * 2 : IntMath.checkedMultiply(i / 2, 3);

        return capAtMaximumSize(j, this.maximumSize);
    }

    private static int capAtMaximumSize(int i, int j) {
        return Math.min(i - 1, j) + 1;
    }

    private class QueueIterator implements Iterator<E> {

        private int cursor;
        private int expectedModCount;
        private Queue<E> forgetMeNot;
        private List<E> skipMe;
        private E lastFromForgetMeNot;
        private boolean canRemove;

        private QueueIterator() {
            this.cursor = -1;
            this.expectedModCount = MinMaxPriorityQueue.this.modCount;
        }

        public boolean hasNext() {
            this.checkModCount();
            return this.nextNotInSkipMe(this.cursor + 1) < MinMaxPriorityQueue.this.size() || this.forgetMeNot != null && !this.forgetMeNot.isEmpty();
        }

        public E next() {
            this.checkModCount();
            int i = this.nextNotInSkipMe(this.cursor + 1);

            if (i < MinMaxPriorityQueue.this.size()) {
                this.cursor = i;
                this.canRemove = true;
                return MinMaxPriorityQueue.this.elementData(this.cursor);
            } else {
                if (this.forgetMeNot != null) {
                    this.cursor = MinMaxPriorityQueue.this.size();
                    this.lastFromForgetMeNot = this.forgetMeNot.poll();
                    if (this.lastFromForgetMeNot != null) {
                        this.canRemove = true;
                        return this.lastFromForgetMeNot;
                    }
                }

                throw new NoSuchElementException("iterator moved past last element in queue.");
            }
        }

        public void remove() {
            CollectPreconditions.checkRemove(this.canRemove);
            this.checkModCount();
            this.canRemove = false;
            ++this.expectedModCount;
            if (this.cursor < MinMaxPriorityQueue.this.size()) {
                MinMaxPriorityQueue.MoveDesc minmaxpriorityqueue_movedesc = MinMaxPriorityQueue.this.removeAt(this.cursor);

                if (minmaxpriorityqueue_movedesc != null) {
                    if (this.forgetMeNot == null) {
                        this.forgetMeNot = new ArrayDeque();
                        this.skipMe = new ArrayList(3);
                    }

                    this.forgetMeNot.add(minmaxpriorityqueue_movedesc.toTrickle);
                    this.skipMe.add(minmaxpriorityqueue_movedesc.replaced);
                }

                --this.cursor;
            } else {
                Preconditions.checkState(this.removeExact(this.lastFromForgetMeNot));
                this.lastFromForgetMeNot = null;
            }

        }

        private boolean containsExact(Iterable<E> iterable, E e0) {
            Iterator iterator = iterable.iterator();

            Object object;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                object = iterator.next();
            } while (object != e0);

            return true;
        }

        boolean removeExact(Object object) {
            for (int i = 0; i < MinMaxPriorityQueue.this.size; ++i) {
                if (MinMaxPriorityQueue.this.queue[i] == object) {
                    MinMaxPriorityQueue.this.removeAt(i);
                    return true;
                }
            }

            return false;
        }

        void checkModCount() {
            if (MinMaxPriorityQueue.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        private int nextNotInSkipMe(int i) {
            if (this.skipMe != null) {
                while (i < MinMaxPriorityQueue.this.size() && this.containsExact(this.skipMe, MinMaxPriorityQueue.this.elementData(i))) {
                    ++i;
                }
            }

            return i;
        }
    }

    private class Heap {

        final Ordering<E> ordering;
        MinMaxPriorityQueue<E>.Heap otherHeap;

        Heap(Ordering<E> ordering) {
            this.ordering = ordering;
        }

        int compareElements(int i, int j) {
            return this.ordering.compare(MinMaxPriorityQueue.this.elementData(i), MinMaxPriorityQueue.this.elementData(j));
        }

        MinMaxPriorityQueue.MoveDesc<E> tryCrossOverAndBubbleUp(int i, int j, E e0) {
            int k = this.crossOver(j, e0);

            if (k == j) {
                return null;
            } else {
                Object object;

                if (k < i) {
                    object = MinMaxPriorityQueue.this.elementData(i);
                } else {
                    object = MinMaxPriorityQueue.this.elementData(this.getParentIndex(i));
                }

                return this.otherHeap.bubbleUpAlternatingLevels(k, e0) < i ? new MinMaxPriorityQueue.MoveDesc<>(e0, object) : null;
            }
        }

        void bubbleUp(int i, E e0) {
            int j = this.crossOverUp(i, e0);
            MinMaxPriorityQueue.Heap minmaxpriorityqueue_heap;

            if (j == i) {
                minmaxpriorityqueue_heap = this;
            } else {
                i = j;
                minmaxpriorityqueue_heap = this.otherHeap;
            }

            minmaxpriorityqueue_heap.bubbleUpAlternatingLevels(i, e0);
        }

        int bubbleUpAlternatingLevels(int i, E e0) {
            while (true) {
                if (i > 2) {
                    int j = this.getGrandparentIndex(i);
                    Object object = MinMaxPriorityQueue.this.elementData(j);

                    if (this.ordering.compare(object, e0) > 0) {
                        MinMaxPriorityQueue.this.queue[i] = object;
                        i = j;
                        continue;
                    }
                }

                MinMaxPriorityQueue.this.queue[i] = e0;
                return i;
            }
        }

        int findMin(int i, int j) {
            if (i >= MinMaxPriorityQueue.this.size) {
                return -1;
            } else {
                Preconditions.checkState(i > 0);
                int k = Math.min(i, MinMaxPriorityQueue.this.size - j) + j;
                int l = i;

                for (int i1 = i + 1; i1 < k; ++i1) {
                    if (this.compareElements(i1, l) < 0) {
                        l = i1;
                    }
                }

                return l;
            }
        }

        int findMinChild(int i) {
            return this.findMin(this.getLeftChildIndex(i), 2);
        }

        int findMinGrandChild(int i) {
            int j = this.getLeftChildIndex(i);

            return j < 0 ? -1 : this.findMin(this.getLeftChildIndex(j), 4);
        }

        int crossOverUp(int i, E e0) {
            if (i == 0) {
                MinMaxPriorityQueue.this.queue[0] = e0;
                return 0;
            } else {
                int j = this.getParentIndex(i);
                Object object = MinMaxPriorityQueue.this.elementData(j);

                if (j != 0) {
                    int k = this.getParentIndex(j);
                    int l = this.getRightChildIndex(k);

                    if (l != j && this.getLeftChildIndex(l) >= MinMaxPriorityQueue.this.size) {
                        Object object1 = MinMaxPriorityQueue.this.elementData(l);

                        if (this.ordering.compare(object1, object) < 0) {
                            j = l;
                            object = object1;
                        }
                    }
                }

                if (this.ordering.compare(object, e0) < 0) {
                    MinMaxPriorityQueue.this.queue[i] = object;
                    MinMaxPriorityQueue.this.queue[j] = e0;
                    return j;
                } else {
                    MinMaxPriorityQueue.this.queue[i] = e0;
                    return i;
                }
            }
        }

        int getCorrectLastElement(E e0) {
            int i = this.getParentIndex(MinMaxPriorityQueue.this.size);

            if (i != 0) {
                int j = this.getParentIndex(i);
                int k = this.getRightChildIndex(j);

                if (k != i && this.getLeftChildIndex(k) >= MinMaxPriorityQueue.this.size) {
                    Object object = MinMaxPriorityQueue.this.elementData(k);

                    if (this.ordering.compare(object, e0) < 0) {
                        MinMaxPriorityQueue.this.queue[k] = e0;
                        MinMaxPriorityQueue.this.queue[MinMaxPriorityQueue.this.size] = object;
                        return k;
                    }
                }
            }

            return MinMaxPriorityQueue.this.size;
        }

        int crossOver(int i, E e0) {
            int j = this.findMinChild(i);

            if (j > 0 && this.ordering.compare(MinMaxPriorityQueue.this.elementData(j), e0) < 0) {
                MinMaxPriorityQueue.this.queue[i] = MinMaxPriorityQueue.this.elementData(j);
                MinMaxPriorityQueue.this.queue[j] = e0;
                return j;
            } else {
                return this.crossOverUp(i, e0);
            }
        }

        int fillHoleAt(int i) {
            int j;

            while ((j = this.findMinGrandChild(i)) > 0) {
                MinMaxPriorityQueue.this.queue[i] = MinMaxPriorityQueue.this.elementData(j);
                i = j;
            }

            return i;
        }

        private boolean verifyIndex(int i) {
            return this.getLeftChildIndex(i) < MinMaxPriorityQueue.this.size && this.compareElements(i, this.getLeftChildIndex(i)) > 0 ? false : (this.getRightChildIndex(i) < MinMaxPriorityQueue.this.size && this.compareElements(i, this.getRightChildIndex(i)) > 0 ? false : (i > 0 && this.compareElements(i, this.getParentIndex(i)) > 0 ? false : i <= 2 || this.compareElements(this.getGrandparentIndex(i), i) <= 0));
        }

        private int getLeftChildIndex(int i) {
            return i * 2 + 1;
        }

        private int getRightChildIndex(int i) {
            return i * 2 + 2;
        }

        private int getParentIndex(int i) {
            return (i - 1) / 2;
        }

        private int getGrandparentIndex(int i) {
            return this.getParentIndex(this.getParentIndex(i));
        }
    }

    static class MoveDesc<E> {

        final E toTrickle;
        final E replaced;

        MoveDesc(E e0, E e1) {
            this.toTrickle = e0;
            this.replaced = e1;
        }
    }

    @Beta
    public static final class Builder<B> {

        private static final int UNSET_EXPECTED_SIZE = -1;
        private final Comparator<B> comparator;
        private int expectedSize;
        private int maximumSize;

        private Builder(Comparator<B> comparator) {
            this.expectedSize = -1;
            this.maximumSize = Integer.MAX_VALUE;
            this.comparator = (Comparator) Preconditions.checkNotNull(comparator);
        }

        public MinMaxPriorityQueue.Builder<B> expectedSize(int i) {
            Preconditions.checkArgument(i >= 0);
            this.expectedSize = i;
            return this;
        }

        public MinMaxPriorityQueue.Builder<B> maximumSize(int i) {
            Preconditions.checkArgument(i > 0);
            this.maximumSize = i;
            return this;
        }

        public <T extends B> MinMaxPriorityQueue<T> create() {
            return this.create(Collections.emptySet());
        }

        public <T extends B> MinMaxPriorityQueue<T> create(Iterable<? extends T> iterable) {
            MinMaxPriorityQueue minmaxpriorityqueue = new MinMaxPriorityQueue<>(this, MinMaxPriorityQueue.initialQueueSize(this.expectedSize, this.maximumSize, iterable));
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();

                minmaxpriorityqueue.offer(object);
            }

            return minmaxpriorityqueue;
        }

        private <T extends B> Ordering<T> ordering() {
            return Ordering.from(this.comparator);
        }
    }
}
