package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
public class LinkedListMultimap<K, V> extends AbstractMultimap<K, V> implements ListMultimap<K, V>, Serializable {

    private transient LinkedListMultimap.Node<K, V> head;
    private transient LinkedListMultimap.Node<K, V> tail;
    private transient Map<K, LinkedListMultimap.KeyList<K, V>> keyToKeyList;
    private transient int size;
    private transient int modCount;
    @GwtIncompatible("java serialization not supported")
    private static final long serialVersionUID = 0L;

    public static <K, V> LinkedListMultimap<K, V> create() {
        return new LinkedListMultimap<>();
    }

    public static <K, V> LinkedListMultimap<K, V> create(int i) {
        return new LinkedListMultimap<>(i);
    }

    public static <K, V> LinkedListMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap) {
        return new LinkedListMultimap<>(multimap);
    }

    LinkedListMultimap() {
        this.keyToKeyList = Maps.newHashMap();
    }

    private LinkedListMultimap(int i) {
        this.keyToKeyList = new HashMap(i);
    }

    private LinkedListMultimap(Multimap<? extends K, ? extends V> multimap) {
        this(multimap.keySet().size());
        this.putAll(multimap);
    }

    private LinkedListMultimap.Node<K, V> addNode(@Nullable K k0, @Nullable V v0, @Nullable LinkedListMultimap.Node<K, V> linkedlistmultimap_node) {
        LinkedListMultimap.Node linkedlistmultimap_node1 = new LinkedListMultimap.Node<>(k0, v0);

        if (this.head == null) {
            this.head = this.tail = linkedlistmultimap_node1;
            this.keyToKeyList.put(k0, new LinkedListMultimap.KeyList<>(linkedlistmultimap_node1));
            ++this.modCount;
        } else {
            LinkedListMultimap.KeyList linkedlistmultimap_keylist;

            if (linkedlistmultimap_node == null) {
                this.tail.next = linkedlistmultimap_node1;
                linkedlistmultimap_node1.previous = this.tail;
                this.tail = linkedlistmultimap_node1;
                linkedlistmultimap_keylist = (LinkedListMultimap.KeyList) this.keyToKeyList.get(k0);
                if (linkedlistmultimap_keylist == null) {
                    this.keyToKeyList.put(k0, new LinkedListMultimap.KeyList<>(linkedlistmultimap_node1));
                    ++this.modCount;
                } else {
                    ++linkedlistmultimap_keylist.count;
                    LinkedListMultimap.Node linkedlistmultimap_node2 = linkedlistmultimap_keylist.tail;

                    linkedlistmultimap_node2.nextSibling = linkedlistmultimap_node1;
                    linkedlistmultimap_node1.previousSibling = linkedlistmultimap_node2;
                    linkedlistmultimap_keylist.tail = linkedlistmultimap_node1;
                }
            } else {
                linkedlistmultimap_keylist = (LinkedListMultimap.KeyList) this.keyToKeyList.get(k0);
                ++linkedlistmultimap_keylist.count;
                linkedlistmultimap_node1.previous = linkedlistmultimap_node.previous;
                linkedlistmultimap_node1.previousSibling = linkedlistmultimap_node.previousSibling;
                linkedlistmultimap_node1.next = linkedlistmultimap_node;
                linkedlistmultimap_node1.nextSibling = linkedlistmultimap_node;
                if (linkedlistmultimap_node.previousSibling == null) {
                    ((LinkedListMultimap.KeyList) this.keyToKeyList.get(k0)).head = linkedlistmultimap_node1;
                } else {
                    linkedlistmultimap_node.previousSibling.nextSibling = linkedlistmultimap_node1;
                }

                if (linkedlistmultimap_node.previous == null) {
                    this.head = linkedlistmultimap_node1;
                } else {
                    linkedlistmultimap_node.previous.next = linkedlistmultimap_node1;
                }

                linkedlistmultimap_node.previous = linkedlistmultimap_node1;
                linkedlistmultimap_node.previousSibling = linkedlistmultimap_node1;
            }
        }

        ++this.size;
        return linkedlistmultimap_node1;
    }

    private void removeNode(LinkedListMultimap.Node<K, V> linkedlistmultimap_node) {
        if (linkedlistmultimap_node.previous != null) {
            linkedlistmultimap_node.previous.next = linkedlistmultimap_node.next;
        } else {
            this.head = linkedlistmultimap_node.next;
        }

        if (linkedlistmultimap_node.next != null) {
            linkedlistmultimap_node.next.previous = linkedlistmultimap_node.previous;
        } else {
            this.tail = linkedlistmultimap_node.previous;
        }

        LinkedListMultimap.KeyList linkedlistmultimap_keylist;

        if (linkedlistmultimap_node.previousSibling == null && linkedlistmultimap_node.nextSibling == null) {
            linkedlistmultimap_keylist = (LinkedListMultimap.KeyList) this.keyToKeyList.remove(linkedlistmultimap_node.key);
            linkedlistmultimap_keylist.count = 0;
            ++this.modCount;
        } else {
            linkedlistmultimap_keylist = (LinkedListMultimap.KeyList) this.keyToKeyList.get(linkedlistmultimap_node.key);
            --linkedlistmultimap_keylist.count;
            if (linkedlistmultimap_node.previousSibling == null) {
                linkedlistmultimap_keylist.head = linkedlistmultimap_node.nextSibling;
            } else {
                linkedlistmultimap_node.previousSibling.nextSibling = linkedlistmultimap_node.nextSibling;
            }

            if (linkedlistmultimap_node.nextSibling == null) {
                linkedlistmultimap_keylist.tail = linkedlistmultimap_node.previousSibling;
            } else {
                linkedlistmultimap_node.nextSibling.previousSibling = linkedlistmultimap_node.previousSibling;
            }
        }

        --this.size;
    }

    private void removeAllNodes(@Nullable Object object) {
        Iterators.clear(new LinkedListMultimap.ValueForKeyIterator(object));
    }

    private static void checkElement(@Nullable Object object) {
        if (object == null) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.head == null;
    }

    @Override
    public boolean containsKey(@Nullable Object object) {
        return this.keyToKeyList.containsKey(object);
    }

    @Override
    public boolean containsValue(@Nullable Object object) {
        return this.values().contains(object);
    }

    @Override
    public boolean put(@Nullable K k0, @Nullable V v0) {
        this.addNode(k0, v0, (LinkedListMultimap.Node) null);
        return true;
    }

    @Override
    public List<V> replaceValues(@Nullable K k0, Iterable<? extends V> iterable) {
        List list = this.getCopy(k0);
        LinkedListMultimap.ValueForKeyIterator linkedlistmultimap_valueforkeyiterator = new LinkedListMultimap.ValueForKeyIterator(k0);
        Iterator iterator = iterable.iterator();

        while (linkedlistmultimap_valueforkeyiterator.hasNext() && iterator.hasNext()) {
            linkedlistmultimap_valueforkeyiterator.next();
            linkedlistmultimap_valueforkeyiterator.set(iterator.next());
        }

        while (linkedlistmultimap_valueforkeyiterator.hasNext()) {
            linkedlistmultimap_valueforkeyiterator.next();
            linkedlistmultimap_valueforkeyiterator.remove();
        }

        while (iterator.hasNext()) {
            linkedlistmultimap_valueforkeyiterator.add(iterator.next());
        }

        return list;
    }

    private List<V> getCopy(@Nullable Object object) {
        return Collections.unmodifiableList(Lists.newArrayList((Iterator) (new LinkedListMultimap.ValueForKeyIterator(object))));
    }

    @Override
    public List<V> removeAll(@Nullable Object object) {
        List list = this.getCopy(object);

        this.removeAllNodes(object);
        return list;
    }

    @Override
    public void clear() {
        this.head = null;
        this.tail = null;
        this.keyToKeyList.clear();
        this.size = 0;
        ++this.modCount;
    }

    @Override
    public List<V> get(@Nullable final K k0) {
        return new AbstractSequentialList<V>() {
            public int size() {
                LinkedListMultimap.KeyList linkedlistmultimap_keylist = (LinkedListMultimap.KeyList) LinkedListMultimap.this.keyToKeyList.get(k0);

                return linkedlistmultimap_keylist == null ? 0 : linkedlistmultimap_keylist.count;
            }

            public ListIterator<V> listIterator(int i) {
                return LinkedListMultimap.this.new ValueForKeyIterator(k0, i);
            }
        };
    }

    @Override
    Set<K> createKeySet() {
        return new Sets.ImprovedAbstractSet<K>() {
            public int size() {
                return LinkedListMultimap.this.keyToKeyList.size();
            }

            public Iterator<K> iterator() {
                return LinkedListMultimap.this.new DistinctKeyIterator();
            }

            public boolean contains(Object object) {
                return LinkedListMultimap.this.containsKey(object);
            }

            public boolean remove(Object object) {
                return !LinkedListMultimap.this.removeAll(object).isEmpty();
            }
        };
    }

    @Override
    public List<V> values() {
        return (List) super.values();
    }

    @Override
    List<V> createValues() {
        return new AbstractSequentialList<V>() {
            public int size() {
                return LinkedListMultimap.this.size;
            }

            public ListIterator<V> listIterator(int i) {
                final LinkedListMultimap.NodeIterator linkedlistmultimap_nodeiterator = LinkedListMultimap.this.new NodeIterator(i);

                return new TransformedListIterator<Entry<K, V>, V>(linkedlistmultimap_nodeiterator) {
                    V transform(Entry<K, V> entry) {
                        return entry.getValue();
                    }

                    @Override
                    public void set(V v0) {
                        linkedlistmultimap_nodeiterator.setValue(v0);
                    }
                };
            }
        };
    }

    @Override
    public List<Entry<K, V>> entries() {
        return (List) super.entries();
    }

    @Override
    List<Entry<K, V>> createEntries() {
        return new AbstractSequentialList<Entry<K, V>>() {
            public int size() {
                return LinkedListMultimap.this.size;
            }

            public ListIterator<Entry<K, V>> listIterator(int i) {
                return LinkedListMultimap.this.new NodeIterator(i);
            }
        };
    }

    @Override
    Iterator<Entry<K, V>> entryIterator() {
        throw new AssertionError("should never be called");
    }

    @Override
    Map<K, Collection<V>> createAsMap() {
        return new Multimaps.AsMap<>(this);
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeInt(this.size());
        Iterator iterator = this.entries().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            objectoutputstream.writeObject(entry.getKey());
            objectoutputstream.writeObject(entry.getValue());
        }

    }

    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        this.keyToKeyList = Maps.newLinkedHashMap();
        int i = objectinputstream.readInt();

        for (int j = 0; j < i; ++j) {
            Object object = objectinputstream.readObject();
            Object object1 = objectinputstream.readObject();

            this.put(object, object1);
        }

    }

    private class ValueForKeyIterator implements ListIterator<V> {

        final Object key;
        int nextIndex;
        LinkedListMultimap.Node<K, V> next;
        LinkedListMultimap.Node<K, V> current;
        LinkedListMultimap.Node<K, V> previous;

        ValueForKeyIterator(@Nullable Object object) {
            this.key = object;
            LinkedListMultimap.KeyList linkedlistmultimap_keylist = (LinkedListMultimap.KeyList) LinkedListMultimap.this.keyToKeyList.get(object);

            this.next = linkedlistmultimap_keylist == null ? null : linkedlistmultimap_keylist.head;
        }

        public ValueForKeyIterator(@Nullable Object object, int i) {
            LinkedListMultimap.KeyList linkedlistmultimap_keylist = (LinkedListMultimap.KeyList) LinkedListMultimap.this.keyToKeyList.get(object);
            int j = linkedlistmultimap_keylist == null ? 0 : linkedlistmultimap_keylist.count;

            Preconditions.checkPositionIndex(i, j);
            if (i >= j / 2) {
                this.previous = linkedlistmultimap_keylist == null ? null : linkedlistmultimap_keylist.tail;
                this.nextIndex = j;

                while (i++ < j) {
                    this.previous();
                }
            } else {
                this.next = linkedlistmultimap_keylist == null ? null : linkedlistmultimap_keylist.head;

                while (i-- > 0) {
                    this.next();
                }
            }

            this.key = object;
            this.current = null;
        }

        public boolean hasNext() {
            return this.next != null;
        }

        public V next() {
            LinkedListMultimap.checkElement(this.next);
            this.previous = this.current = this.next;
            this.next = this.next.nextSibling;
            ++this.nextIndex;
            return this.current.value;
        }

        public boolean hasPrevious() {
            return this.previous != null;
        }

        public V previous() {
            LinkedListMultimap.checkElement(this.previous);
            this.next = this.current = this.previous;
            this.previous = this.previous.previousSibling;
            --this.nextIndex;
            return this.current.value;
        }

        public int nextIndex() {
            return this.nextIndex;
        }

        public int previousIndex() {
            return this.nextIndex - 1;
        }

        public void remove() {
            CollectPreconditions.checkRemove(this.current != null);
            if (this.current != this.next) {
                this.previous = this.current.previousSibling;
                --this.nextIndex;
            } else {
                this.next = this.current.nextSibling;
            }

            LinkedListMultimap.this.removeNode(this.current);
            this.current = null;
        }

        public void set(V v0) {
            Preconditions.checkState(this.current != null);
            this.current.value = v0;
        }

        public void add(V v0) {
            this.previous = LinkedListMultimap.this.addNode(this.key, v0, this.next);
            ++this.nextIndex;
            this.current = null;
        }
    }

    private class DistinctKeyIterator implements Iterator<K> {

        final Set<K> seenKeys;
        LinkedListMultimap.Node<K, V> next;
        LinkedListMultimap.Node<K, V> current;
        int expectedModCount;

        private DistinctKeyIterator() {
            this.seenKeys = Sets.newHashSetWithExpectedSize(LinkedListMultimap.this.keySet().size());
            this.next = LinkedListMultimap.this.head;
            this.expectedModCount = LinkedListMultimap.this.modCount;
        }

        private void checkForConcurrentModification() {
            if (LinkedListMultimap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        public boolean hasNext() {
            this.checkForConcurrentModification();
            return this.next != null;
        }

        public K next() {
            this.checkForConcurrentModification();
            LinkedListMultimap.checkElement(this.next);
            this.current = this.next;
            this.seenKeys.add(this.current.key);

            do {
                this.next = this.next.next;
            } while (this.next != null && !this.seenKeys.add(this.next.key));

            return this.current.key;
        }

        public void remove() {
            this.checkForConcurrentModification();
            CollectPreconditions.checkRemove(this.current != null);
            LinkedListMultimap.this.removeAllNodes(this.current.key);
            this.current = null;
            this.expectedModCount = LinkedListMultimap.this.modCount;
        }
    }

    private class NodeIterator implements ListIterator<Entry<K, V>> {

        int nextIndex;
        LinkedListMultimap.Node<K, V> next;
        LinkedListMultimap.Node<K, V> current;
        LinkedListMultimap.Node<K, V> previous;
        int expectedModCount;

        NodeIterator(int i) {
            this.expectedModCount = LinkedListMultimap.this.modCount;
            int j = LinkedListMultimap.this.size();

            Preconditions.checkPositionIndex(i, j);
            if (i >= j / 2) {
                this.previous = LinkedListMultimap.this.tail;
                this.nextIndex = j;

                while (i++ < j) {
                    this.previous();
                }
            } else {
                this.next = LinkedListMultimap.this.head;

                while (i-- > 0) {
                    this.next();
                }
            }

            this.current = null;
        }

        private void checkForConcurrentModification() {
            if (LinkedListMultimap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        public boolean hasNext() {
            this.checkForConcurrentModification();
            return this.next != null;
        }

        public LinkedListMultimap.Node<K, V> next() {
            this.checkForConcurrentModification();
            LinkedListMultimap.checkElement(this.next);
            this.previous = this.current = this.next;
            this.next = this.next.next;
            ++this.nextIndex;
            return this.current;
        }

        public void remove() {
            this.checkForConcurrentModification();
            CollectPreconditions.checkRemove(this.current != null);
            if (this.current != this.next) {
                this.previous = this.current.previous;
                --this.nextIndex;
            } else {
                this.next = this.current.next;
            }

            LinkedListMultimap.this.removeNode(this.current);
            this.current = null;
            this.expectedModCount = LinkedListMultimap.this.modCount;
        }

        public boolean hasPrevious() {
            this.checkForConcurrentModification();
            return this.previous != null;
        }

        public LinkedListMultimap.Node<K, V> previous() {
            this.checkForConcurrentModification();
            LinkedListMultimap.checkElement(this.previous);
            this.next = this.current = this.previous;
            this.previous = this.previous.previous;
            --this.nextIndex;
            return this.current;
        }

        public int nextIndex() {
            return this.nextIndex;
        }

        public int previousIndex() {
            return this.nextIndex - 1;
        }

        public void set(Entry<K, V> entry) {
            throw new UnsupportedOperationException();
        }

        public void add(Entry<K, V> entry) {
            throw new UnsupportedOperationException();
        }

        void setValue(V v0) {
            Preconditions.checkState(this.current != null);
            this.current.value = v0;
        }
    }

    private static class KeyList<K, V> {

        LinkedListMultimap.Node<K, V> head;
        LinkedListMultimap.Node<K, V> tail;
        int count;

        KeyList(LinkedListMultimap.Node<K, V> linkedlistmultimap_node) {
            this.head = linkedlistmultimap_node;
            this.tail = linkedlistmultimap_node;
            linkedlistmultimap_node.previousSibling = null;
            linkedlistmultimap_node.nextSibling = null;
            this.count = 1;
        }
    }

    private static final class Node<K, V> extends AbstractMapEntry<K, V> {

        final K key;
        V value;
        LinkedListMultimap.Node<K, V> next;
        LinkedListMultimap.Node<K, V> previous;
        LinkedListMultimap.Node<K, V> nextSibling;
        LinkedListMultimap.Node<K, V> previousSibling;

        Node(@Nullable K k0, @Nullable V v0) {
            this.key = k0;
            this.value = v0;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(@Nullable V v0) {
            Object object = this.value;

            this.value = v0;
            return object;
        }
    }
}
