package com.google.gson.internal;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public final class LinkedTreeMap<K, V> extends AbstractMap<K, V> implements Serializable {

    private static final Comparator<Comparable> NATURAL_ORDER = new Comparator<Comparable>() {
        public int compare(Comparable comparable, Comparable comparable1) {
            return comparable.compareTo(comparable1);
        }
    };
    Comparator<? super K> comparator;
    LinkedTreeMap.Node<K, V> root;
    int size;
    int modCount;
    final LinkedTreeMap.Node<K, V> header;
    private LinkedTreeMap<K, V>.EntrySet entrySet;
    private LinkedTreeMap<K, V>.KeySet keySet;

    public LinkedTreeMap() {
        this(LinkedTreeMap.NATURAL_ORDER);
    }

    public LinkedTreeMap(Comparator<? super K> comparator) {
        this.size = 0;
        this.modCount = 0;
        this.header = new LinkedTreeMap.Node<>();
        this.comparator = comparator != null ? comparator : LinkedTreeMap.NATURAL_ORDER;
    }

    public int size() {
        return this.size;
    }

    public V get(Object object) {
        LinkedTreeMap.Node linkedtreemap_node = this.findByObject(object);

        return linkedtreemap_node != null ? linkedtreemap_node.value : null;
    }

    public boolean containsKey(Object object) {
        return this.findByObject(object) != null;
    }

    public V put(K k0, V v0) {
        if (k0 == null) {
            throw new NullPointerException("key == null");
        } else {
            LinkedTreeMap.Node linkedtreemap_node = this.find(k0, true);
            Object object = linkedtreemap_node.value;

            linkedtreemap_node.value = v0;
            return object;
        }
    }

    public void clear() {
        this.root = null;
        this.size = 0;
        ++this.modCount;
        LinkedTreeMap.Node linkedtreemap_node = this.header;

        linkedtreemap_node.next = linkedtreemap_node.prev = linkedtreemap_node;
    }

    public V remove(Object object) {
        LinkedTreeMap.Node linkedtreemap_node = this.removeInternalByKey(object);

        return linkedtreemap_node != null ? linkedtreemap_node.value : null;
    }

    LinkedTreeMap.Node<K, V> find(K k0, boolean flag) {
        Comparator comparator = this.comparator;
        LinkedTreeMap.Node linkedtreemap_node = this.root;
        int i = 0;
        LinkedTreeMap.Node linkedtreemap_node1;

        if (linkedtreemap_node != null) {
            Comparable comparable = comparator == LinkedTreeMap.NATURAL_ORDER ? (Comparable) k0 : null;

            while (true) {
                i = comparable != null ? comparable.compareTo(linkedtreemap_node.key) : comparator.compare(k0, linkedtreemap_node.key);
                if (i == 0) {
                    return linkedtreemap_node;
                }

                linkedtreemap_node1 = i < 0 ? linkedtreemap_node.left : linkedtreemap_node.right;
                if (linkedtreemap_node1 == null) {
                    break;
                }

                linkedtreemap_node = linkedtreemap_node1;
            }
        }

        if (!flag) {
            return null;
        } else {
            LinkedTreeMap.Node linkedtreemap_node2 = this.header;

            if (linkedtreemap_node == null) {
                if (comparator == LinkedTreeMap.NATURAL_ORDER && !(k0 instanceof Comparable)) {
                    throw new ClassCastException(k0.getClass().getName() + " is not Comparable");
                }

                linkedtreemap_node1 = new LinkedTreeMap.Node<>(linkedtreemap_node, k0, linkedtreemap_node2, linkedtreemap_node2.prev);
                this.root = linkedtreemap_node1;
            } else {
                linkedtreemap_node1 = new LinkedTreeMap.Node<>(linkedtreemap_node, k0, linkedtreemap_node2, linkedtreemap_node2.prev);
                if (i < 0) {
                    linkedtreemap_node.left = linkedtreemap_node1;
                } else {
                    linkedtreemap_node.right = linkedtreemap_node1;
                }

                this.rebalance(linkedtreemap_node, true);
            }

            ++this.size;
            ++this.modCount;
            return linkedtreemap_node1;
        }
    }

    LinkedTreeMap.Node<K, V> findByObject(Object object) {
        try {
            return object != null ? this.find(object, false) : null;
        } catch (ClassCastException classcastexception) {
            return null;
        }
    }

    LinkedTreeMap.Node<K, V> findByEntry(Entry<?, ?> entry) {
        LinkedTreeMap.Node linkedtreemap_node = this.findByObject(entry.getKey());
        boolean flag = linkedtreemap_node != null && this.equal(linkedtreemap_node.value, entry.getValue());

        return flag ? linkedtreemap_node : null;
    }

    private boolean equal(Object object, Object object1) {
        return object == object1 || object != null && object.equals(object1);
    }

    void removeInternal(LinkedTreeMap.Node<K, V> linkedtreemap_node, boolean flag) {
        if (flag) {
            linkedtreemap_node.prev.next = linkedtreemap_node.next;
            linkedtreemap_node.next.prev = linkedtreemap_node.prev;
        }

        LinkedTreeMap.Node linkedtreemap_node1 = linkedtreemap_node.left;
        LinkedTreeMap.Node linkedtreemap_node2 = linkedtreemap_node.right;
        LinkedTreeMap.Node linkedtreemap_node3 = linkedtreemap_node.parent;

        if (linkedtreemap_node1 != null && linkedtreemap_node2 != null) {
            LinkedTreeMap.Node linkedtreemap_node4 = linkedtreemap_node1.height > linkedtreemap_node2.height ? linkedtreemap_node1.last() : linkedtreemap_node2.first();

            this.removeInternal(linkedtreemap_node4, false);
            int i = 0;

            linkedtreemap_node1 = linkedtreemap_node.left;
            if (linkedtreemap_node1 != null) {
                i = linkedtreemap_node1.height;
                linkedtreemap_node4.left = linkedtreemap_node1;
                linkedtreemap_node1.parent = linkedtreemap_node4;
                linkedtreemap_node.left = null;
            }

            int j = 0;

            linkedtreemap_node2 = linkedtreemap_node.right;
            if (linkedtreemap_node2 != null) {
                j = linkedtreemap_node2.height;
                linkedtreemap_node4.right = linkedtreemap_node2;
                linkedtreemap_node2.parent = linkedtreemap_node4;
                linkedtreemap_node.right = null;
            }

            linkedtreemap_node4.height = Math.max(i, j) + 1;
            this.replaceInParent(linkedtreemap_node, linkedtreemap_node4);
        } else {
            if (linkedtreemap_node1 != null) {
                this.replaceInParent(linkedtreemap_node, linkedtreemap_node1);
                linkedtreemap_node.left = null;
            } else if (linkedtreemap_node2 != null) {
                this.replaceInParent(linkedtreemap_node, linkedtreemap_node2);
                linkedtreemap_node.right = null;
            } else {
                this.replaceInParent(linkedtreemap_node, (LinkedTreeMap.Node) null);
            }

            this.rebalance(linkedtreemap_node3, false);
            --this.size;
            ++this.modCount;
        }
    }

    LinkedTreeMap.Node<K, V> removeInternalByKey(Object object) {
        LinkedTreeMap.Node linkedtreemap_node = this.findByObject(object);

        if (linkedtreemap_node != null) {
            this.removeInternal(linkedtreemap_node, true);
        }

        return linkedtreemap_node;
    }

    private void replaceInParent(LinkedTreeMap.Node<K, V> linkedtreemap_node, LinkedTreeMap.Node<K, V> linkedtreemap_node1) {
        LinkedTreeMap.Node linkedtreemap_node2 = linkedtreemap_node.parent;

        linkedtreemap_node.parent = null;
        if (linkedtreemap_node1 != null) {
            linkedtreemap_node1.parent = linkedtreemap_node2;
        }

        if (linkedtreemap_node2 != null) {
            if (linkedtreemap_node2.left == linkedtreemap_node) {
                linkedtreemap_node2.left = linkedtreemap_node1;
            } else {
                assert linkedtreemap_node2.right == linkedtreemap_node;

                linkedtreemap_node2.right = linkedtreemap_node1;
            }
        } else {
            this.root = linkedtreemap_node1;
        }

    }

    private void rebalance(LinkedTreeMap.Node<K, V> linkedtreemap_node, boolean flag) {
        for (LinkedTreeMap.Node linkedtreemap_node1 = linkedtreemap_node; linkedtreemap_node1 != null; linkedtreemap_node1 = linkedtreemap_node1.parent) {
            LinkedTreeMap.Node linkedtreemap_node2 = linkedtreemap_node1.left;
            LinkedTreeMap.Node linkedtreemap_node3 = linkedtreemap_node1.right;
            int i = linkedtreemap_node2 != null ? linkedtreemap_node2.height : 0;
            int j = linkedtreemap_node3 != null ? linkedtreemap_node3.height : 0;
            int k = i - j;
            LinkedTreeMap.Node linkedtreemap_node4;
            LinkedTreeMap.Node linkedtreemap_node5;
            int l;
            int i1;
            int j1;

            if (k == -2) {
                linkedtreemap_node4 = linkedtreemap_node3.left;
                linkedtreemap_node5 = linkedtreemap_node3.right;
                l = linkedtreemap_node5 != null ? linkedtreemap_node5.height : 0;
                i1 = linkedtreemap_node4 != null ? linkedtreemap_node4.height : 0;
                j1 = i1 - l;
                if (j1 != -1 && (j1 != 0 || flag)) {
                    assert j1 == 1;

                    this.rotateRight(linkedtreemap_node3);
                    this.rotateLeft(linkedtreemap_node1);
                } else {
                    this.rotateLeft(linkedtreemap_node1);
                }

                if (flag) {
                    break;
                }
            } else if (k == 2) {
                linkedtreemap_node4 = linkedtreemap_node2.left;
                linkedtreemap_node5 = linkedtreemap_node2.right;
                l = linkedtreemap_node5 != null ? linkedtreemap_node5.height : 0;
                i1 = linkedtreemap_node4 != null ? linkedtreemap_node4.height : 0;
                j1 = i1 - l;
                if (j1 != 1 && (j1 != 0 || flag)) {
                    assert j1 == -1;

                    this.rotateLeft(linkedtreemap_node2);
                    this.rotateRight(linkedtreemap_node1);
                } else {
                    this.rotateRight(linkedtreemap_node1);
                }

                if (flag) {
                    break;
                }
            } else if (k == 0) {
                linkedtreemap_node1.height = i + 1;
                if (flag) {
                    break;
                }
            } else {
                assert k == -1 || k == 1;

                linkedtreemap_node1.height = Math.max(i, j) + 1;
                if (!flag) {
                    break;
                }
            }
        }

    }

    private void rotateLeft(LinkedTreeMap.Node<K, V> linkedtreemap_node) {
        LinkedTreeMap.Node linkedtreemap_node1 = linkedtreemap_node.left;
        LinkedTreeMap.Node linkedtreemap_node2 = linkedtreemap_node.right;
        LinkedTreeMap.Node linkedtreemap_node3 = linkedtreemap_node2.left;
        LinkedTreeMap.Node linkedtreemap_node4 = linkedtreemap_node2.right;

        linkedtreemap_node.right = linkedtreemap_node3;
        if (linkedtreemap_node3 != null) {
            linkedtreemap_node3.parent = linkedtreemap_node;
        }

        this.replaceInParent(linkedtreemap_node, linkedtreemap_node2);
        linkedtreemap_node2.left = linkedtreemap_node;
        linkedtreemap_node.parent = linkedtreemap_node2;
        linkedtreemap_node.height = Math.max(linkedtreemap_node1 != null ? linkedtreemap_node1.height : 0, linkedtreemap_node3 != null ? linkedtreemap_node3.height : 0) + 1;
        linkedtreemap_node2.height = Math.max(linkedtreemap_node.height, linkedtreemap_node4 != null ? linkedtreemap_node4.height : 0) + 1;
    }

    private void rotateRight(LinkedTreeMap.Node<K, V> linkedtreemap_node) {
        LinkedTreeMap.Node linkedtreemap_node1 = linkedtreemap_node.left;
        LinkedTreeMap.Node linkedtreemap_node2 = linkedtreemap_node.right;
        LinkedTreeMap.Node linkedtreemap_node3 = linkedtreemap_node1.left;
        LinkedTreeMap.Node linkedtreemap_node4 = linkedtreemap_node1.right;

        linkedtreemap_node.left = linkedtreemap_node4;
        if (linkedtreemap_node4 != null) {
            linkedtreemap_node4.parent = linkedtreemap_node;
        }

        this.replaceInParent(linkedtreemap_node, linkedtreemap_node1);
        linkedtreemap_node1.right = linkedtreemap_node;
        linkedtreemap_node.parent = linkedtreemap_node1;
        linkedtreemap_node.height = Math.max(linkedtreemap_node2 != null ? linkedtreemap_node2.height : 0, linkedtreemap_node4 != null ? linkedtreemap_node4.height : 0) + 1;
        linkedtreemap_node1.height = Math.max(linkedtreemap_node.height, linkedtreemap_node3 != null ? linkedtreemap_node3.height : 0) + 1;
    }

    public Set<Entry<K, V>> entrySet() {
        LinkedTreeMap.EntrySet linkedtreemap_entryset = this.entrySet;

        return linkedtreemap_entryset != null ? linkedtreemap_entryset : (this.entrySet = new LinkedTreeMap.EntrySet());
    }

    public Set<K> keySet() {
        LinkedTreeMap.KeySet linkedtreemap_keyset = this.keySet;

        return linkedtreemap_keyset != null ? linkedtreemap_keyset : (this.keySet = new LinkedTreeMap.KeySet());
    }

    private Object writeReplace() throws ObjectStreamException {
        return new LinkedHashMap(this);
    }

    class KeySet extends AbstractSet<K> {

        KeySet() {}

        public int size() {
            return LinkedTreeMap.this.size;
        }

        public Iterator<K> iterator() {
            return new LinkedTreeMap<K, V>.LinkedTreeMapIterator<K>() {
                public K next() {
                    return this.nextNode().key;
                }
            };
        }

        public boolean contains(Object object) {
            return LinkedTreeMap.this.containsKey(object);
        }

        public boolean remove(Object object) {
            return LinkedTreeMap.this.removeInternalByKey(object) != null;
        }

        public void clear() {
            LinkedTreeMap.this.clear();
        }
    }

    class EntrySet extends AbstractSet<Entry<K, V>> {

        EntrySet() {}

        public int size() {
            return LinkedTreeMap.this.size;
        }

        public Iterator<Entry<K, V>> iterator() {
            return new LinkedTreeMap<K, V>.LinkedTreeMapIterator<Entry<K, V>>() {
                public Entry<K, V> next() {
                    return this.nextNode();
                }
            };
        }

        public boolean contains(Object object) {
            return object instanceof Entry && LinkedTreeMap.this.findByEntry((Entry) object) != null;
        }

        public boolean remove(Object object) {
            if (!(object instanceof Entry)) {
                return false;
            } else {
                LinkedTreeMap.Node linkedtreemap_node = LinkedTreeMap.this.findByEntry((Entry) object);

                if (linkedtreemap_node == null) {
                    return false;
                } else {
                    LinkedTreeMap.this.removeInternal(linkedtreemap_node, true);
                    return true;
                }
            }
        }

        public void clear() {
            LinkedTreeMap.this.clear();
        }
    }

    private abstract class LinkedTreeMapIterator<T> implements Iterator<T> {

        LinkedTreeMap.Node<K, V> next;
        LinkedTreeMap.Node<K, V> lastReturned;
        int expectedModCount;

        private LinkedTreeMapIterator() {
            this.next = LinkedTreeMap.this.header.next;
            this.lastReturned = null;
            this.expectedModCount = LinkedTreeMap.this.modCount;
        }

        public final boolean hasNext() {
            return this.next != LinkedTreeMap.this.header;
        }

        final LinkedTreeMap.Node<K, V> nextNode() {
            LinkedTreeMap.Node linkedtreemap_node = this.next;

            if (linkedtreemap_node == LinkedTreeMap.this.header) {
                throw new NoSuchElementException();
            } else if (LinkedTreeMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else {
                this.next = linkedtreemap_node.next;
                return this.lastReturned = linkedtreemap_node;
            }
        }

        public final void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            } else {
                LinkedTreeMap.this.removeInternal(this.lastReturned, true);
                this.lastReturned = null;
                this.expectedModCount = LinkedTreeMap.this.modCount;
            }
        }
    }

    static final class Node<K, V> implements Entry<K, V> {

        LinkedTreeMap.Node<K, V> parent;
        LinkedTreeMap.Node<K, V> left;
        LinkedTreeMap.Node<K, V> right;
        LinkedTreeMap.Node<K, V> next;
        LinkedTreeMap.Node<K, V> prev;
        final K key;
        V value;
        int height;

        Node() {
            this.key = null;
            this.next = this.prev = this;
        }

        Node(LinkedTreeMap.Node<K, V> linkedtreemap_node, K k0, LinkedTreeMap.Node<K, V> linkedtreemap_node1, LinkedTreeMap.Node<K, V> linkedtreemap_node2) {
            this.parent = linkedtreemap_node;
            this.key = k0;
            this.height = 1;
            this.next = linkedtreemap_node1;
            this.prev = linkedtreemap_node2;
            linkedtreemap_node2.next = this;
            linkedtreemap_node1.prev = this;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public V setValue(V v0) {
            Object object = this.value;

            this.value = v0;
            return object;
        }

        public boolean equals(Object object) {
            if (!(object instanceof Entry)) {
                return false;
            } else {
                boolean flag;
                label39:
                {
                    Entry entry = (Entry) object;

                    if (this.key == null) {
                        if (entry.getKey() != null) {
                            break label39;
                        }
                    } else if (!this.key.equals(entry.getKey())) {
                        break label39;
                    }

                    if (this.value == null) {
                        if (entry.getValue() != null) {
                            break label39;
                        }
                    } else if (!this.value.equals(entry.getValue())) {
                        break label39;
                    }

                    flag = true;
                    return flag;
                }

                flag = false;
                return flag;
            }
        }

        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.key + "=" + this.value;
        }

        public LinkedTreeMap.Node<K, V> first() {
            LinkedTreeMap.Node linkedtreemap_node = this;

            for (LinkedTreeMap.Node linkedtreemap_node1 = this.left; linkedtreemap_node1 != null; linkedtreemap_node1 = linkedtreemap_node1.left) {
                linkedtreemap_node = linkedtreemap_node1;
            }

            return linkedtreemap_node;
        }

        public LinkedTreeMap.Node<K, V> last() {
            LinkedTreeMap.Node linkedtreemap_node = this;

            for (LinkedTreeMap.Node linkedtreemap_node1 = this.right; linkedtreemap_node1 != null; linkedtreemap_node1 = linkedtreemap_node1.right) {
                linkedtreemap_node = linkedtreemap_node1;
            }

            return linkedtreemap_node;
        }
    }
}
