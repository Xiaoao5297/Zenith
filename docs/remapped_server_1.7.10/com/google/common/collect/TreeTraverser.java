package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;

@Beta
@GwtCompatible(emulated = true)
public abstract class TreeTraverser<T> {

    public TreeTraverser() {}

    public abstract Iterable<T> children(T t0);

    public final FluentIterable<T> preOrderTraversal(final T t0) {
        Preconditions.checkNotNull(t0);
        return new FluentIterable<T>() {
            public UnmodifiableIterator<T> iterator() {
                return TreeTraverser.this.preOrderIterator(t0);
            }
        };
    }

    UnmodifiableIterator<T> preOrderIterator(T t0) {
        return new TreeTraverser.PreOrderIterator(t0);
    }

    public final FluentIterable<T> postOrderTraversal(final T t0) {
        Preconditions.checkNotNull(t0);
        return new FluentIterable<T>() {
            public UnmodifiableIterator<T> iterator() {
                return TreeTraverser.this.postOrderIterator(t0);
            }
        };
    }

    UnmodifiableIterator<T> postOrderIterator(T t0) {
        return new TreeTraverser.PostOrderIterator(t0);
    }

    public final FluentIterable<T> breadthFirstTraversal(final T t0) {
        Preconditions.checkNotNull(t0);
        return new FluentIterable<T>() {
            public UnmodifiableIterator<T> iterator() {
                return TreeTraverser.this.new BreadthFirstIterator(t0);
            }
        };
    }

    private final class BreadthFirstIterator extends UnmodifiableIterator<T> implements PeekingIterator<T> {

        private final Queue<T> queue = new ArrayDeque();

        BreadthFirstIterator(T t0) {
            this.queue.add(t0);
        }

        public boolean hasNext() {
            return !this.queue.isEmpty();
        }

        @Override
        public T peek() {
            return this.queue.element();
        }

        @Override
        public T next() {
            Object object = this.queue.remove();

            Iterables.addAll(this.queue, TreeTraverser.this.children(object));
            return object;
        }
    }

    private final class PostOrderIterator extends AbstractIterator<T> {

        private final ArrayDeque<TreeTraverser.PostOrderNode<T>> stack = new ArrayDeque();

        PostOrderIterator(T t0) {
            this.stack.addLast(this.expand(t0));
        }

        @Override
        protected T computeNext() {
            while (true) {
                if (!this.stack.isEmpty()) {
                    TreeTraverser.PostOrderNode treetraverser_postordernode = (TreeTraverser.PostOrderNode) this.stack.getLast();

                    if (treetraverser_postordernode.childIterator.hasNext()) {
                        Object object = treetraverser_postordernode.childIterator.next();

                        this.stack.addLast(this.expand(object));
                        continue;
                    }

                    this.stack.removeLast();
                    return treetraverser_postordernode.root;
                }

                return this.endOfData();
            }
        }

        private TreeTraverser.PostOrderNode<T> expand(T t0) {
            return new TreeTraverser.PostOrderNode<>(t0, TreeTraverser.this.children(t0).iterator());
        }
    }

    private static final class PostOrderNode<T> {

        final T root;
        final Iterator<T> childIterator;

        PostOrderNode(T t0, Iterator<T> iterator) {
            this.root = Preconditions.checkNotNull(t0);
            this.childIterator = (Iterator) Preconditions.checkNotNull(iterator);
        }
    }

    private final class PreOrderIterator extends UnmodifiableIterator<T> {

        private final Deque<Iterator<T>> stack = new ArrayDeque();

        PreOrderIterator(T t0) {
            this.stack.addLast(Iterators.singletonIterator(Preconditions.checkNotNull(t0)));
        }

        public boolean hasNext() {
            return !this.stack.isEmpty();
        }

        public T next() {
            Iterator iterator = (Iterator) this.stack.getLast();
            Object object = Preconditions.checkNotNull(iterator.next());

            if (!iterator.hasNext()) {
                this.stack.removeLast();
            }

            Iterator iterator1 = TreeTraverser.this.children(object).iterator();

            if (iterator1.hasNext()) {
                this.stack.addLast(iterator1);
            }

            return object;
        }
    }
}
