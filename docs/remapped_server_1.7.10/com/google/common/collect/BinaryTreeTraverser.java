package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;
import java.util.Iterator;

@Beta
@GwtCompatible(emulated = true)
public abstract class BinaryTreeTraverser<T> extends TreeTraverser<T> {

    public BinaryTreeTraverser() {}

    public abstract Optional<T> leftChild(T t0);

    public abstract Optional<T> rightChild(T t0);

    @Override
    public final Iterable<T> children(final T t0) {
        Preconditions.checkNotNull(t0);
        return new FluentIterable<T>() {
            public Iterator<T> iterator() {
                return new AbstractIterator<T>() {
                    boolean doneLeft;
                    boolean doneRight;

                    @Override
                    protected T computeNext() {
                        Optional optional;

                        if (!this.doneLeft) {
                            this.doneLeft = true;
                            optional = BinaryTreeTraverser.this.leftChild(t0);
                            if (optional.isPresent()) {
                                return optional.get();
                            }
                        }

                        if (!this.doneRight) {
                            this.doneRight = true;
                            optional = BinaryTreeTraverser.this.rightChild(t0);
                            if (optional.isPresent()) {
                                return optional.get();
                            }
                        }

                        return this.endOfData();
                    }
                };
            }
        };
    }

    @Override
    UnmodifiableIterator<T> preOrderIterator(T t0) {
        return new BinaryTreeTraverser.PreOrderIterator(t0);
    }

    @Override
    UnmodifiableIterator<T> postOrderIterator(T t0) {
        return new BinaryTreeTraverser.PostOrderIterator(t0);
    }

    public final FluentIterable<T> inOrderTraversal(final T t0) {
        Preconditions.checkNotNull(t0);
        return new FluentIterable<T>() {
            public UnmodifiableIterator<T> iterator() {
                return BinaryTreeTraverser.this.new InOrderIterator(t0);
            }
        };
    }

    private static <T> void pushIfPresent(Deque<T> deque, Optional<T> optional) {
        if (optional.isPresent()) {
            deque.addLast(optional.get());
        }

    }

    private final class InOrderIterator extends AbstractIterator<T> {

        private final Deque<T> stack = new ArrayDeque();
        private final BitSet hasExpandedLeft = new BitSet();

        InOrderIterator(T t0) {
            this.stack.addLast(t0);
        }

        @Override
        protected T computeNext() {
            while (!this.stack.isEmpty()) {
                Object object = this.stack.getLast();

                if (this.hasExpandedLeft.get(this.stack.size() - 1)) {
                    this.stack.removeLast();
                    this.hasExpandedLeft.clear(this.stack.size());
                    BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.rightChild(object));
                    return object;
                }

                this.hasExpandedLeft.set(this.stack.size() - 1);
                BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.leftChild(object));
            }

            return this.endOfData();
        }
    }

    private final class PostOrderIterator extends UnmodifiableIterator<T> {

        private final Deque<T> stack = new ArrayDeque();
        private final BitSet hasExpanded;

        PostOrderIterator(T t0) {
            this.stack.addLast(t0);
            this.hasExpanded = new BitSet();
        }

        public boolean hasNext() {
            return !this.stack.isEmpty();
        }

        public T next() {
            while (true) {
                Object object = this.stack.getLast();
                boolean flag = this.hasExpanded.get(this.stack.size() - 1);

                if (flag) {
                    this.stack.removeLast();
                    this.hasExpanded.clear(this.stack.size());
                    return object;
                }

                this.hasExpanded.set(this.stack.size() - 1);
                BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.rightChild(object));
                BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.leftChild(object));
            }
        }
    }

    private final class PreOrderIterator extends UnmodifiableIterator<T> implements PeekingIterator<T> {

        private final Deque<T> stack = new ArrayDeque();

        PreOrderIterator(T t0) {
            this.stack.addLast(t0);
        }

        public boolean hasNext() {
            return !this.stack.isEmpty();
        }

        @Override
        public T next() {
            Object object = this.stack.removeLast();

            BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.rightChild(object));
            BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.leftChild(object));
            return object;
        }

        @Override
        public T peek() {
            return this.stack.getLast();
        }
    }
}
