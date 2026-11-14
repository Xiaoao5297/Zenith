package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class TreeMultiset<E> extends AbstractSortedMultiset<E> implements Serializable {

    private final transient TreeMultiset.Reference<TreeMultiset.AvlNode<E>> rootReference;
    private final transient GeneralRange<E> range;
    private final transient TreeMultiset.AvlNode<E> header;
    @GwtIncompatible("not needed in emulated source")
    private static final long serialVersionUID = 1L;

    public static <E extends Comparable> TreeMultiset<E> create() {
        return new TreeMultiset<>(Ordering.natural());
    }

    public static <E> TreeMultiset<E> create(@Nullable Comparator<? super E> comparator) {
        return comparator == null ? new TreeMultiset<>(Ordering.natural()) : new TreeMultiset<>(comparator);
    }

    public static <E extends Comparable> TreeMultiset<E> create(Iterable<? extends E> iterable) {
        TreeMultiset treemultiset = create();

        Iterables.addAll(treemultiset, iterable);
        return treemultiset;
    }

    TreeMultiset(TreeMultiset.Reference<TreeMultiset.AvlNode<E>> treemultiset_reference, GeneralRange<E> generalrange, TreeMultiset.AvlNode<E> treemultiset_avlnode) {
        super(generalrange.comparator());
        this.rootReference = treemultiset_reference;
        this.range = generalrange;
        this.header = treemultiset_avlnode;
    }

    TreeMultiset(Comparator<? super E> comparator) {
        super(comparator);
        this.range = GeneralRange.all(comparator);
        this.header = new TreeMultiset.AvlNode<>((Object) null, 1);
        successor(this.header, this.header);
        this.rootReference = new TreeMultiset.Reference<>();
    }

    private long aggregateForEntries(TreeMultiset.Aggregate treemultiset_aggregate) {
        TreeMultiset.AvlNode treemultiset_avlnode = (TreeMultiset.AvlNode) this.rootReference.get();
        long i = treemultiset_aggregate.treeAggregate(treemultiset_avlnode);

        if (this.range.hasLowerBound()) {
            i -= this.aggregateBelowRange(treemultiset_aggregate, treemultiset_avlnode);
        }

        if (this.range.hasUpperBound()) {
            i -= this.aggregateAboveRange(treemultiset_aggregate, treemultiset_avlnode);
        }

        return i;
    }

    private long aggregateBelowRange(TreeMultiset.Aggregate treemultiset_aggregate, @Nullable TreeMultiset.AvlNode<E> treemultiset_avlnode) {
        if (treemultiset_avlnode == null) {
            return 0L;
        } else {
            int i = this.comparator().compare(this.range.getLowerEndpoint(), treemultiset_avlnode.elem);

            if (i < 0) {
                return this.aggregateBelowRange(treemultiset_aggregate, treemultiset_avlnode.left);
            } else if (i == 0) {
                switch (this.range.getLowerBoundType()) {
                    case OPEN:
                        return (long) treemultiset_aggregate.nodeAggregate(treemultiset_avlnode) + treemultiset_aggregate.treeAggregate(treemultiset_avlnode.left);
                    case CLOSED:
                        return treemultiset_aggregate.treeAggregate(treemultiset_avlnode.left);
                    default:
                        throw new AssertionError();
                }
            } else {
                return treemultiset_aggregate.treeAggregate(treemultiset_avlnode.left) + (long) treemultiset_aggregate.nodeAggregate(treemultiset_avlnode) + this.aggregateBelowRange(treemultiset_aggregate, treemultiset_avlnode.right);
            }
        }
    }

    private long aggregateAboveRange(TreeMultiset.Aggregate treemultiset_aggregate, @Nullable TreeMultiset.AvlNode<E> treemultiset_avlnode) {
        if (treemultiset_avlnode == null) {
            return 0L;
        } else {
            int i = this.comparator().compare(this.range.getUpperEndpoint(), treemultiset_avlnode.elem);

            if (i > 0) {
                return this.aggregateAboveRange(treemultiset_aggregate, treemultiset_avlnode.right);
            } else if (i == 0) {
                switch (this.range.getUpperBoundType()) {
                    case OPEN:
                        return (long) treemultiset_aggregate.nodeAggregate(treemultiset_avlnode) + treemultiset_aggregate.treeAggregate(treemultiset_avlnode.right);
                    case CLOSED:
                        return treemultiset_aggregate.treeAggregate(treemultiset_avlnode.right);
                    default:
                        throw new AssertionError();
                }
            } else {
                return treemultiset_aggregate.treeAggregate(treemultiset_avlnode.right) + (long) treemultiset_aggregate.nodeAggregate(treemultiset_avlnode) + this.aggregateAboveRange(treemultiset_aggregate, treemultiset_avlnode.left);
            }
        }
    }

    @Override
    public int size() {
        return Ints.saturatedCast(this.aggregateForEntries(TreeMultiset.Aggregate.SIZE));
    }

    @Override
    int distinctElements() {
        return Ints.saturatedCast(this.aggregateForEntries(TreeMultiset.Aggregate.DISTINCT));
    }

    @Override
    public int count(@Nullable Object object) {
        try {
            TreeMultiset.AvlNode treemultiset_avlnode = (TreeMultiset.AvlNode) this.rootReference.get();

            return this.range.contains(object) && treemultiset_avlnode != null ? treemultiset_avlnode.count(this.comparator(), object) : 0;
        } catch (ClassCastException classcastexception) {
            return 0;
        } catch (NullPointerException nullpointerexception) {
            return 0;
        }
    }

    @Override
    public int add(@Nullable E e0, int i) {
        CollectPreconditions.checkNonnegative(i, "occurrences");
        if (i == 0) {
            return this.count(e0);
        } else {
            Preconditions.checkArgument(this.range.contains(e0));
            TreeMultiset.AvlNode treemultiset_avlnode = (TreeMultiset.AvlNode) this.rootReference.get();

            if (treemultiset_avlnode == null) {
                this.comparator().compare(e0, e0);
                TreeMultiset.AvlNode treemultiset_avlnode1 = new TreeMultiset.AvlNode<>(e0, i);

                successor(this.header, treemultiset_avlnode1, this.header);
                this.rootReference.checkAndSet(treemultiset_avlnode, treemultiset_avlnode1);
                return 0;
            } else {
                int[] aint = new int[1];
                TreeMultiset.AvlNode treemultiset_avlnode2 = treemultiset_avlnode.add(this.comparator(), e0, i, aint);

                this.rootReference.checkAndSet(treemultiset_avlnode, treemultiset_avlnode2);
                return aint[0];
            }
        }
    }

    @Override
    public int remove(@Nullable Object object, int i) {
        CollectPreconditions.checkNonnegative(i, "occurrences");
        if (i == 0) {
            return this.count(object);
        } else {
            TreeMultiset.AvlNode treemultiset_avlnode = (TreeMultiset.AvlNode) this.rootReference.get();
            int[] aint = new int[1];

            TreeMultiset.AvlNode treemultiset_avlnode1;

            try {
                if (!this.range.contains(object) || treemultiset_avlnode == null) {
                    return 0;
                }

                treemultiset_avlnode1 = treemultiset_avlnode.remove(this.comparator(), object, i, aint);
            } catch (ClassCastException classcastexception) {
                return 0;
            } catch (NullPointerException nullpointerexception) {
                return 0;
            }

            this.rootReference.checkAndSet(treemultiset_avlnode, treemultiset_avlnode1);
            return aint[0];
        }
    }

    @Override
    public int setCount(@Nullable E e0, int i) {
        CollectPreconditions.checkNonnegative(i, "count");
        if (!this.range.contains(e0)) {
            Preconditions.checkArgument(i == 0);
            return 0;
        } else {
            TreeMultiset.AvlNode treemultiset_avlnode = (TreeMultiset.AvlNode) this.rootReference.get();

            if (treemultiset_avlnode == null) {
                if (i > 0) {
                    this.add(e0, i);
                }

                return 0;
            } else {
                int[] aint = new int[1];
                TreeMultiset.AvlNode treemultiset_avlnode1 = treemultiset_avlnode.setCount(this.comparator(), e0, i, aint);

                this.rootReference.checkAndSet(treemultiset_avlnode, treemultiset_avlnode1);
                return aint[0];
            }
        }
    }

    @Override
    public boolean setCount(@Nullable E e0, int i, int j) {
        CollectPreconditions.checkNonnegative(j, "newCount");
        CollectPreconditions.checkNonnegative(i, "oldCount");
        Preconditions.checkArgument(this.range.contains(e0));
        TreeMultiset.AvlNode treemultiset_avlnode = (TreeMultiset.AvlNode) this.rootReference.get();

        if (treemultiset_avlnode == null) {
            if (i == 0) {
                if (j > 0) {
                    this.add(e0, j);
                }

                return true;
            } else {
                return false;
            }
        } else {
            int[] aint = new int[1];
            TreeMultiset.AvlNode treemultiset_avlnode1 = treemultiset_avlnode.setCount(this.comparator(), e0, i, j, aint);

            this.rootReference.checkAndSet(treemultiset_avlnode, treemultiset_avlnode1);
            return aint[0] == i;
        }
    }

    private Multiset.Entry<E> wrapEntry(final TreeMultiset.AvlNode<E> treemultiset_avlnode) {
        return new Multisets.AbstractEntry<E>() {
            @Override
            public E getElement() {
                return treemultiset_avlnode.getElement();
            }

            @Override
            public int getCount() {
                int i = treemultiset_avlnode.getCount();

                return i == 0 ? TreeMultiset.this.count(this.getElement()) : i;
            }
        };
    }

    @Nullable
    private TreeMultiset.AvlNode<E> firstNode() {
        TreeMultiset.AvlNode treemultiset_avlnode = (TreeMultiset.AvlNode) this.rootReference.get();

        if (treemultiset_avlnode == null) {
            return null;
        } else {
            TreeMultiset.AvlNode treemultiset_avlnode1;

            if (this.range.hasLowerBound()) {
                Object object = this.range.getLowerEndpoint();

                treemultiset_avlnode1 = ((TreeMultiset.AvlNode) this.rootReference.get()).ceiling(this.comparator(), object);
                if (treemultiset_avlnode1 == null) {
                    return null;
                }

                if (this.range.getLowerBoundType() == BoundType.OPEN && this.comparator().compare(object, treemultiset_avlnode1.getElement()) == 0) {
                    treemultiset_avlnode1 = treemultiset_avlnode1.succ;
                }
            } else {
                treemultiset_avlnode1 = this.header.succ;
            }

            return treemultiset_avlnode1 != this.header && this.range.contains(treemultiset_avlnode1.getElement()) ? treemultiset_avlnode1 : null;
        }
    }

    @Nullable
    private TreeMultiset.AvlNode<E> lastNode() {
        TreeMultiset.AvlNode treemultiset_avlnode = (TreeMultiset.AvlNode) this.rootReference.get();

        if (treemultiset_avlnode == null) {
            return null;
        } else {
            TreeMultiset.AvlNode treemultiset_avlnode1;

            if (this.range.hasUpperBound()) {
                Object object = this.range.getUpperEndpoint();

                treemultiset_avlnode1 = ((TreeMultiset.AvlNode) this.rootReference.get()).floor(this.comparator(), object);
                if (treemultiset_avlnode1 == null) {
                    return null;
                }

                if (this.range.getUpperBoundType() == BoundType.OPEN && this.comparator().compare(object, treemultiset_avlnode1.getElement()) == 0) {
                    treemultiset_avlnode1 = treemultiset_avlnode1.pred;
                }
            } else {
                treemultiset_avlnode1 = this.header.pred;
            }

            return treemultiset_avlnode1 != this.header && this.range.contains(treemultiset_avlnode1.getElement()) ? treemultiset_avlnode1 : null;
        }
    }

    @Override
    Iterator<Multiset.Entry<E>> entryIterator() {
        return new Iterator<Multiset.Entry<E>>() {
            TreeMultiset.AvlNode<E> current = TreeMultiset.this.firstNode();
            Multiset.Entry<E> prevEntry;

            public boolean hasNext() {
                if (this.current == null) {
                    return false;
                } else if (TreeMultiset.this.range.tooHigh(this.current.getElement())) {
                    this.current = null;
                    return false;
                } else {
                    return true;
                }
            }

            public Multiset.Entry<E> next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    Multiset.Entry multiset_entry = TreeMultiset.this.wrapEntry(this.current);

                    this.prevEntry = multiset_entry;
                    if (this.current.succ == TreeMultiset.this.header) {
                        this.current = null;
                    } else {
                        this.current = this.current.succ;
                    }

                    return multiset_entry;
                }
            }

            public void remove() {
                CollectPreconditions.checkRemove(this.prevEntry != null);
                TreeMultiset.this.setCount(this.prevEntry.getElement(), 0);
                this.prevEntry = null;
            }
        };
    }

    @Override
    Iterator<Multiset.Entry<E>> descendingEntryIterator() {
        return new Iterator<Multiset.Entry<E>>() {
            TreeMultiset.AvlNode<E> current = TreeMultiset.this.lastNode();
            Multiset.Entry<E> prevEntry = null;

            public boolean hasNext() {
                if (this.current == null) {
                    return false;
                } else if (TreeMultiset.this.range.tooLow(this.current.getElement())) {
                    this.current = null;
                    return false;
                } else {
                    return true;
                }
            }

            public Multiset.Entry<E> next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    Multiset.Entry multiset_entry = TreeMultiset.this.wrapEntry(this.current);

                    this.prevEntry = multiset_entry;
                    if (this.current.pred == TreeMultiset.this.header) {
                        this.current = null;
                    } else {
                        this.current = this.current.pred;
                    }

                    return multiset_entry;
                }
            }

            public void remove() {
                CollectPreconditions.checkRemove(this.prevEntry != null);
                TreeMultiset.this.setCount(this.prevEntry.getElement(), 0);
                this.prevEntry = null;
            }
        };
    }

    @Override
    public SortedMultiset<E> headMultiset(@Nullable E e0, BoundType boundtype) {
        return new TreeMultiset<>(this.rootReference, this.range.intersect(GeneralRange.upTo(this.comparator(), e0, boundtype)), this.header);
    }

    @Override
    public SortedMultiset<E> tailMultiset(@Nullable E e0, BoundType boundtype) {
        return new TreeMultiset<>(this.rootReference, this.range.intersect(GeneralRange.downTo(this.comparator(), e0, boundtype)), this.header);
    }

    static int distinctElements(@Nullable TreeMultiset.AvlNode<?> treemultiset_avlnode) {
        return treemultiset_avlnode == null ? 0 : treemultiset_avlnode.distinctElements;
    }

    private static <T> void successor(TreeMultiset.AvlNode<T> treemultiset_avlnode, TreeMultiset.AvlNode<T> treemultiset_avlnode1) {
        treemultiset_avlnode.succ = treemultiset_avlnode1;
        treemultiset_avlnode1.pred = treemultiset_avlnode;
    }

    private static <T> void successor(TreeMultiset.AvlNode<T> treemultiset_avlnode, TreeMultiset.AvlNode<T> treemultiset_avlnode1, TreeMultiset.AvlNode<T> treemultiset_avlnode2) {
        successor(treemultiset_avlnode, treemultiset_avlnode1);
        successor(treemultiset_avlnode1, treemultiset_avlnode2);
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeObject(this.elementSet().comparator());
        Serialization.writeMultiset(this, objectoutputstream);
    }

    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        Comparator comparator = (Comparator) objectinputstream.readObject();

        Serialization.getFieldSetter(AbstractSortedMultiset.class, "comparator").set(this, comparator);
        Serialization.getFieldSetter(TreeMultiset.class, "range").set(this, GeneralRange.all(comparator));
        Serialization.getFieldSetter(TreeMultiset.class, "rootReference").set(this, new TreeMultiset.Reference<>());
        TreeMultiset.AvlNode treemultiset_avlnode = new TreeMultiset.AvlNode<>((Object) null, 1);

        Serialization.getFieldSetter(TreeMultiset.class, "header").set(this, treemultiset_avlnode);
        successor(treemultiset_avlnode, treemultiset_avlnode);
        Serialization.populateMultiset(this, objectinputstream);
    }

    private static final class AvlNode<E> extends Multisets.AbstractEntry<E> {

        @Nullable
        private final E elem;
        private int elemCount;
        private int distinctElements;
        private long totalCount;
        private int height;
        private TreeMultiset.AvlNode<E> left;
        private TreeMultiset.AvlNode<E> right;
        private TreeMultiset.AvlNode<E> pred;
        private TreeMultiset.AvlNode<E> succ;

        AvlNode(@Nullable E e0, int i) {
            Preconditions.checkArgument(i > 0);
            this.elem = e0;
            this.elemCount = i;
            this.totalCount = (long) i;
            this.distinctElements = 1;
            this.height = 1;
            this.left = null;
            this.right = null;
        }

        public int count(Comparator<? super E> comparator, E e0) {
            int i = comparator.compare(e0, this.elem);

            return i < 0 ? (this.left == null ? 0 : this.left.count(comparator, e0)) : (i > 0 ? (this.right == null ? 0 : this.right.count(comparator, e0)) : this.elemCount);
        }

        private TreeMultiset.AvlNode<E> addRightChild(E e0, int i) {
            this.right = new TreeMultiset.AvlNode<>(e0, i);
            TreeMultiset.successor(this, this.right, this.succ);
            this.height = Math.max(2, this.height);
            ++this.distinctElements;
            this.totalCount += (long) i;
            return this;
        }

        private TreeMultiset.AvlNode<E> addLeftChild(E e0, int i) {
            this.left = new TreeMultiset.AvlNode<>(e0, i);
            TreeMultiset.successor(this.pred, this.left, this);
            this.height = Math.max(2, this.height);
            ++this.distinctElements;
            this.totalCount += (long) i;
            return this;
        }

        TreeMultiset.AvlNode<E> add(Comparator<? super E> comparator, @Nullable E e0, int i, int[] aint) {
            int j = comparator.compare(e0, this.elem);
            TreeMultiset.AvlNode treemultiset_avlnode;
            int k;

            if (j < 0) {
                treemultiset_avlnode = this.left;
                if (treemultiset_avlnode == null) {
                    aint[0] = 0;
                    return this.addLeftChild(e0, i);
                } else {
                    k = treemultiset_avlnode.height;
                    this.left = treemultiset_avlnode.add(comparator, e0, i, aint);
                    if (aint[0] == 0) {
                        ++this.distinctElements;
                    }

                    this.totalCount += (long) i;
                    return this.left.height == k ? this : this.rebalance();
                }
            } else if (j > 0) {
                treemultiset_avlnode = this.right;
                if (treemultiset_avlnode == null) {
                    aint[0] = 0;
                    return this.addRightChild(e0, i);
                } else {
                    k = treemultiset_avlnode.height;
                    this.right = treemultiset_avlnode.add(comparator, e0, i, aint);
                    if (aint[0] == 0) {
                        ++this.distinctElements;
                    }

                    this.totalCount += (long) i;
                    return this.right.height == k ? this : this.rebalance();
                }
            } else {
                aint[0] = this.elemCount;
                long l = (long) this.elemCount + (long) i;

                Preconditions.checkArgument(l <= 2147483647L);
                this.elemCount += i;
                this.totalCount += (long) i;
                return this;
            }
        }

        TreeMultiset.AvlNode<E> remove(Comparator<? super E> comparator, @Nullable E e0, int i, int[] aint) {
            int j = comparator.compare(e0, this.elem);
            TreeMultiset.AvlNode treemultiset_avlnode;

            if (j < 0) {
                treemultiset_avlnode = this.left;
                if (treemultiset_avlnode == null) {
                    aint[0] = 0;
                    return this;
                } else {
                    this.left = treemultiset_avlnode.remove(comparator, e0, i, aint);
                    if (aint[0] > 0) {
                        if (i >= aint[0]) {
                            --this.distinctElements;
                            this.totalCount -= (long) aint[0];
                        } else {
                            this.totalCount -= (long) i;
                        }
                    }

                    return aint[0] == 0 ? this : this.rebalance();
                }
            } else if (j > 0) {
                treemultiset_avlnode = this.right;
                if (treemultiset_avlnode == null) {
                    aint[0] = 0;
                    return this;
                } else {
                    this.right = treemultiset_avlnode.remove(comparator, e0, i, aint);
                    if (aint[0] > 0) {
                        if (i >= aint[0]) {
                            --this.distinctElements;
                            this.totalCount -= (long) aint[0];
                        } else {
                            this.totalCount -= (long) i;
                        }
                    }

                    return this.rebalance();
                }
            } else {
                aint[0] = this.elemCount;
                if (i >= this.elemCount) {
                    return this.deleteMe();
                } else {
                    this.elemCount -= i;
                    this.totalCount -= (long) i;
                    return this;
                }
            }
        }

        TreeMultiset.AvlNode<E> setCount(Comparator<? super E> comparator, @Nullable E e0, int i, int[] aint) {
            int j = comparator.compare(e0, this.elem);
            TreeMultiset.AvlNode treemultiset_avlnode;

            if (j < 0) {
                treemultiset_avlnode = this.left;
                if (treemultiset_avlnode == null) {
                    aint[0] = 0;
                    return i > 0 ? this.addLeftChild(e0, i) : this;
                } else {
                    this.left = treemultiset_avlnode.setCount(comparator, e0, i, aint);
                    if (i == 0 && aint[0] != 0) {
                        --this.distinctElements;
                    } else if (i > 0 && aint[0] == 0) {
                        ++this.distinctElements;
                    }

                    this.totalCount += (long) (i - aint[0]);
                    return this.rebalance();
                }
            } else if (j > 0) {
                treemultiset_avlnode = this.right;
                if (treemultiset_avlnode == null) {
                    aint[0] = 0;
                    return i > 0 ? this.addRightChild(e0, i) : this;
                } else {
                    this.right = treemultiset_avlnode.setCount(comparator, e0, i, aint);
                    if (i == 0 && aint[0] != 0) {
                        --this.distinctElements;
                    } else if (i > 0 && aint[0] == 0) {
                        ++this.distinctElements;
                    }

                    this.totalCount += (long) (i - aint[0]);
                    return this.rebalance();
                }
            } else {
                aint[0] = this.elemCount;
                if (i == 0) {
                    return this.deleteMe();
                } else {
                    this.totalCount += (long) (i - this.elemCount);
                    this.elemCount = i;
                    return this;
                }
            }
        }

        TreeMultiset.AvlNode<E> setCount(Comparator<? super E> comparator, @Nullable E e0, int i, int j, int[] aint) {
            int k = comparator.compare(e0, this.elem);
            TreeMultiset.AvlNode treemultiset_avlnode;

            if (k < 0) {
                treemultiset_avlnode = this.left;
                if (treemultiset_avlnode == null) {
                    aint[0] = 0;
                    return i == 0 && j > 0 ? this.addLeftChild(e0, j) : this;
                } else {
                    this.left = treemultiset_avlnode.setCount(comparator, e0, i, j, aint);
                    if (aint[0] == i) {
                        if (j == 0 && aint[0] != 0) {
                            --this.distinctElements;
                        } else if (j > 0 && aint[0] == 0) {
                            ++this.distinctElements;
                        }

                        this.totalCount += (long) (j - aint[0]);
                    }

                    return this.rebalance();
                }
            } else if (k > 0) {
                treemultiset_avlnode = this.right;
                if (treemultiset_avlnode == null) {
                    aint[0] = 0;
                    return i == 0 && j > 0 ? this.addRightChild(e0, j) : this;
                } else {
                    this.right = treemultiset_avlnode.setCount(comparator, e0, i, j, aint);
                    if (aint[0] == i) {
                        if (j == 0 && aint[0] != 0) {
                            --this.distinctElements;
                        } else if (j > 0 && aint[0] == 0) {
                            ++this.distinctElements;
                        }

                        this.totalCount += (long) (j - aint[0]);
                    }

                    return this.rebalance();
                }
            } else {
                aint[0] = this.elemCount;
                if (i == this.elemCount) {
                    if (j == 0) {
                        return this.deleteMe();
                    }

                    this.totalCount += (long) (j - this.elemCount);
                    this.elemCount = j;
                }

                return this;
            }
        }

        private TreeMultiset.AvlNode<E> deleteMe() {
            int i = this.elemCount;

            this.elemCount = 0;
            TreeMultiset.successor(this.pred, this.succ);
            if (this.left == null) {
                return this.right;
            } else if (this.right == null) {
                return this.left;
            } else {
                TreeMultiset.AvlNode treemultiset_avlnode;

                if (this.left.height >= this.right.height) {
                    treemultiset_avlnode = this.pred;
                    treemultiset_avlnode.left = this.left.removeMax(treemultiset_avlnode);
                    treemultiset_avlnode.right = this.right;
                    treemultiset_avlnode.distinctElements = this.distinctElements - 1;
                    treemultiset_avlnode.totalCount = this.totalCount - (long) i;
                    return treemultiset_avlnode.rebalance();
                } else {
                    treemultiset_avlnode = this.succ;
                    treemultiset_avlnode.right = this.right.removeMin(treemultiset_avlnode);
                    treemultiset_avlnode.left = this.left;
                    treemultiset_avlnode.distinctElements = this.distinctElements - 1;
                    treemultiset_avlnode.totalCount = this.totalCount - (long) i;
                    return treemultiset_avlnode.rebalance();
                }
            }
        }

        private TreeMultiset.AvlNode<E> removeMin(TreeMultiset.AvlNode<E> treemultiset_avlnode) {
            if (this.left == null) {
                return this.right;
            } else {
                this.left = this.left.removeMin(treemultiset_avlnode);
                --this.distinctElements;
                this.totalCount -= (long) treemultiset_avlnode.elemCount;
                return this.rebalance();
            }
        }

        private TreeMultiset.AvlNode<E> removeMax(TreeMultiset.AvlNode<E> treemultiset_avlnode) {
            if (this.right == null) {
                return this.left;
            } else {
                this.right = this.right.removeMax(treemultiset_avlnode);
                --this.distinctElements;
                this.totalCount -= (long) treemultiset_avlnode.elemCount;
                return this.rebalance();
            }
        }

        private void recomputeMultiset() {
            this.distinctElements = 1 + TreeMultiset.distinctElements(this.left) + TreeMultiset.distinctElements(this.right);
            this.totalCount = (long) this.elemCount + totalCount(this.left) + totalCount(this.right);
        }

        private void recomputeHeight() {
            this.height = 1 + Math.max(height(this.left), height(this.right));
        }

        private void recompute() {
            this.recomputeMultiset();
            this.recomputeHeight();
        }

        private TreeMultiset.AvlNode<E> rebalance() {
            switch (this.balanceFactor()) {
                case -2:
                    if (this.right.balanceFactor() > 0) {
                        this.right = this.right.rotateRight();
                    }

                    return this.rotateLeft();
                case 2:
                    if (this.left.balanceFactor() < 0) {
                        this.left = this.left.rotateLeft();
                    }

                    return this.rotateRight();
                default:
                    this.recomputeHeight();
                    return this;
            }
        }

        private int balanceFactor() {
            return height(this.left) - height(this.right);
        }

        private TreeMultiset.AvlNode<E> rotateLeft() {
            Preconditions.checkState(this.right != null);
            TreeMultiset.AvlNode treemultiset_avlnode = this.right;

            this.right = treemultiset_avlnode.left;
            treemultiset_avlnode.left = this;
            treemultiset_avlnode.totalCount = this.totalCount;
            treemultiset_avlnode.distinctElements = this.distinctElements;
            this.recompute();
            treemultiset_avlnode.recomputeHeight();
            return treemultiset_avlnode;
        }

        private TreeMultiset.AvlNode<E> rotateRight() {
            Preconditions.checkState(this.left != null);
            TreeMultiset.AvlNode treemultiset_avlnode = this.left;

            this.left = treemultiset_avlnode.right;
            treemultiset_avlnode.right = this;
            treemultiset_avlnode.totalCount = this.totalCount;
            treemultiset_avlnode.distinctElements = this.distinctElements;
            this.recompute();
            treemultiset_avlnode.recomputeHeight();
            return treemultiset_avlnode;
        }

        private static long totalCount(@Nullable TreeMultiset.AvlNode<?> treemultiset_avlnode) {
            return treemultiset_avlnode == null ? 0L : treemultiset_avlnode.totalCount;
        }

        private static int height(@Nullable TreeMultiset.AvlNode<?> treemultiset_avlnode) {
            return treemultiset_avlnode == null ? 0 : treemultiset_avlnode.height;
        }

        @Nullable
        private TreeMultiset.AvlNode<E> ceiling(Comparator<? super E> comparator, E e0) {
            int i = comparator.compare(e0, this.elem);

            return i < 0 ? (this.left == null ? this : (TreeMultiset.AvlNode) Objects.firstNonNull(this.left.ceiling(comparator, e0), this)) : (i == 0 ? this : (this.right == null ? null : this.right.ceiling(comparator, e0)));
        }

        @Nullable
        private TreeMultiset.AvlNode<E> floor(Comparator<? super E> comparator, E e0) {
            int i = comparator.compare(e0, this.elem);

            return i > 0 ? (this.right == null ? this : (TreeMultiset.AvlNode) Objects.firstNonNull(this.right.floor(comparator, e0), this)) : (i == 0 ? this : (this.left == null ? null : this.left.floor(comparator, e0)));
        }

        @Override
        public E getElement() {
            return this.elem;
        }

        @Override
        public int getCount() {
            return this.elemCount;
        }

        @Override
        public String toString() {
            return Multisets.immutableEntry(this.getElement(), this.getCount()).toString();
        }
    }

    private static final class Reference<T> {

        @Nullable
        private T value;

        private Reference() {}

        @Nullable
        public T get() {
            return this.value;
        }

        public void checkAndSet(@Nullable T t0, T t1) {
            if (this.value != t0) {
                throw new ConcurrentModificationException();
            } else {
                this.value = t1;
            }
        }
    }

    private static enum Aggregate {

        SIZE {
            @Override
            int nodeAggregate(TreeMultiset.AvlNode<?> treemultiset_avlnode) {
                return treemultiset_avlnode.elemCount;
            }

            @Override
            long treeAggregate(@Nullable TreeMultiset.AvlNode<?> treemultiset_avlnode) {
                return treemultiset_avlnode == null ? 0L : treemultiset_avlnode.totalCount;
            }
        },
        DISTINCT {
            @Override
            int nodeAggregate(TreeMultiset.AvlNode<?> treemultiset_avlnode) {
                return 1;
            }

            @Override
            long treeAggregate(@Nullable TreeMultiset.AvlNode<?> treemultiset_avlnode) {
                return treemultiset_avlnode == null ? 0L : (long) treemultiset_avlnode.distinctElements;
            }
        };

        private Aggregate() {}

        abstract int nodeAggregate(TreeMultiset.AvlNode<?> treemultiset_avlnode);

        abstract long treeAggregate(@Nullable TreeMultiset.AvlNode<?> treemultiset_avlnode);
    }
}
