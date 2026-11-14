package io.netty.util.internal.chmv8;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

public class ConcurrentHashMapV8<K, V> implements ConcurrentMap<K, V>, Serializable {

    private static final long serialVersionUID = 7249069246763182397L;
    private static final int MAXIMUM_CAPACITY = 1073741824;
    private static final int DEFAULT_CAPACITY = 16;
    static final int MAX_ARRAY_SIZE = 2147483639;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    private static final float LOAD_FACTOR = 0.75F;
    static final int TREEIFY_THRESHOLD = 8;
    static final int UNTREEIFY_THRESHOLD = 6;
    static final int MIN_TREEIFY_CAPACITY = 64;
    private static final int MIN_TRANSFER_STRIDE = 16;
    static final int MOVED = -1;
    static final int TREEBIN = -2;
    static final int RESERVED = -3;
    static final int HASH_BITS = Integer.MAX_VALUE;
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("segments", ConcurrentHashMapV8.Segment[].class), new ObjectStreamField("segmentMask", Integer.TYPE), new ObjectStreamField("segmentShift", Integer.TYPE)};
    transient volatile ConcurrentHashMapV8.Node<K, V>[] table;
    private transient volatile ConcurrentHashMapV8.Node<K, V>[] nextTable;
    private transient volatile long baseCount;
    private transient volatile int sizeCtl;
    private transient volatile int transferIndex;
    private transient volatile int transferOrigin;
    private transient volatile int cellsBusy;
    private transient volatile ConcurrentHashMapV8.CounterCell[] counterCells;
    private transient ConcurrentHashMapV8.KeySetView<K, V> keySet;
    private transient ConcurrentHashMapV8.ValuesView<K, V> values;
    private transient ConcurrentHashMapV8.EntrySetView<K, V> entrySet;
    static final AtomicInteger counterHashCodeGenerator = new AtomicInteger();
    static final int SEED_INCREMENT = 1640531527;
    static final ThreadLocal<ConcurrentHashMapV8.CounterHashCode> threadCounterHashCode = new ThreadLocal();
    private static final Unsafe U;
    private static final long SIZECTL;
    private static final long TRANSFERINDEX;
    private static final long TRANSFERORIGIN;
    private static final long BASECOUNT;
    private static final long CELLSBUSY;
    private static final long CELLVALUE;
    private static final long ABASE;
    private static final int ASHIFT;

    static final int spread(int i) {
        return (i ^ i >>> 16) & Integer.MAX_VALUE;
    }

    private static final int tableSizeFor(int i) {
        int j = i - 1;

        j |= j >>> 1;
        j |= j >>> 2;
        j |= j >>> 4;
        j |= j >>> 8;
        j |= j >>> 16;
        return j < 0 ? 1 : (j >= 1073741824 ? 1073741824 : j + 1);
    }

    static Class<?> comparableClassFor(Object object) {
        if (object instanceof Comparable) {
            Class oclass;

            if ((oclass = object.getClass()) == String.class) {
                return oclass;
            }

            Type[] atype;

            if ((atype = oclass.getGenericInterfaces()) != null) {
                for (int i = 0; i < atype.length; ++i) {
                    Type[] atype1;
                    ParameterizedType parameterizedtype;
                    Type type;

                    if ((type = atype[i]) instanceof ParameterizedType && (parameterizedtype = (ParameterizedType) type).getRawType() == Comparable.class && (atype1 = parameterizedtype.getActualTypeArguments()) != null && atype1.length == 1 && atype1[0] == oclass) {
                        return oclass;
                    }
                }
            }
        }

        return null;
    }

    static int compareComparables(Class<?> oclass, Object object, Object object1) {
        return object1 != null && object1.getClass() == oclass ? ((Comparable) object).compareTo(object1) : 0;
    }

    static final <K, V> ConcurrentHashMapV8.Node<K, V> tabAt(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, int i) {
        return (ConcurrentHashMapV8.Node) ConcurrentHashMapV8.U.getObjectVolatile(aconcurrenthashmapv8_node, ((long) i << ConcurrentHashMapV8.ASHIFT) + ConcurrentHashMapV8.ABASE);
    }

    static final <K, V> boolean casTabAt(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, int i, ConcurrentHashMapV8.Node<K, V> concurrenthashmapv8_node, ConcurrentHashMapV8.Node<K, V> concurrenthashmapv8_node1) {
        return ConcurrentHashMapV8.U.compareAndSwapObject(aconcurrenthashmapv8_node, ((long) i << ConcurrentHashMapV8.ASHIFT) + ConcurrentHashMapV8.ABASE, concurrenthashmapv8_node, concurrenthashmapv8_node1);
    }

    static final <K, V> void setTabAt(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, int i, ConcurrentHashMapV8.Node<K, V> concurrenthashmapv8_node) {
        ConcurrentHashMapV8.U.putObjectVolatile(aconcurrenthashmapv8_node, ((long) i << ConcurrentHashMapV8.ASHIFT) + ConcurrentHashMapV8.ABASE, concurrenthashmapv8_node);
    }

    public ConcurrentHashMapV8() {}

    public ConcurrentHashMapV8(int i) {
        if (i < 0) {
            throw new IllegalArgumentException();
        } else {
            int j = i >= 536870912 ? 1073741824 : tableSizeFor(i + (i >>> 1) + 1);

            this.sizeCtl = j;
        }
    }

    public ConcurrentHashMapV8(Map<? extends K, ? extends V> map) {
        this.sizeCtl = 16;
        this.putAll(map);
    }

    public ConcurrentHashMapV8(int i, float f) {
        this(i, f, 1);
    }

    public ConcurrentHashMapV8(int i, float f, int j) {
        if (f > 0.0F && i >= 0 && j > 0) {
            if (i < j) {
                i = j;
            }

            long k = (long) (1.0D + (double) ((float) ((long) i) / f));
            int l = k >= 1073741824L ? 1073741824 : tableSizeFor((int) k);

            this.sizeCtl = l;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int size() {
        long i = this.sumCount();

        return i < 0L ? 0 : (i > 2147483647L ? Integer.MAX_VALUE : (int) i);
    }

    public boolean isEmpty() {
        return this.sumCount() <= 0L;
    }

    public V get(Object object) {
        int i = spread(object.hashCode());
        ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;
        ConcurrentHashMapV8.Node concurrenthashmapv8_node;
        int j;

        if (this.table != null && (j = aconcurrenthashmapv8_node.length) > 0 && (concurrenthashmapv8_node = tabAt(aconcurrenthashmapv8_node, j - 1 & i)) != null) {
            int k = concurrenthashmapv8_node.hash;
            Object object1;

            if (concurrenthashmapv8_node.hash == i) {
                object1 = concurrenthashmapv8_node.key;
                if (concurrenthashmapv8_node.key == object || object1 != null && object.equals(object1)) {
                    return concurrenthashmapv8_node.val;
                }
            } else if (k < 0) {
                ConcurrentHashMapV8.Node concurrenthashmapv8_node1;

                return (concurrenthashmapv8_node1 = concurrenthashmapv8_node.find(i, object)) != null ? concurrenthashmapv8_node1.val : null;
            }

            while ((concurrenthashmapv8_node = concurrenthashmapv8_node.next) != null) {
                if (concurrenthashmapv8_node.hash == i) {
                    object1 = concurrenthashmapv8_node.key;
                    if (concurrenthashmapv8_node.key == object || object1 != null && object.equals(object1)) {
                        return concurrenthashmapv8_node.val;
                    }
                }
            }
        }

        return null;
    }

    public boolean containsKey(Object object) {
        return this.get(object) != null;
    }

    public boolean containsValue(Object object) {
        if (object == null) {
            throw new NullPointerException();
        } else {
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;

            if (this.table != null) {
                ConcurrentHashMapV8.Traverser concurrenthashmapv8_traverser = new ConcurrentHashMapV8.Traverser<>(aconcurrenthashmapv8_node, aconcurrenthashmapv8_node.length, 0, aconcurrenthashmapv8_node.length);

                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                while ((concurrenthashmapv8_node = concurrenthashmapv8_traverser.advance()) != null) {
                    Object object1 = concurrenthashmapv8_node.val;

                    if (concurrenthashmapv8_node.val == object || object1 != null && object.equals(object1)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public V put(K k0, V v0) {
        return this.putVal(k0, v0, false);
    }

    final V putVal(K k0, V v0, boolean flag) {
        if (k0 != null && v0 != null) {
            int i = spread(k0.hashCode());
            int j = 0;
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;

            while (true) {
                int k;

                while (aconcurrenthashmapv8_node == null || (k = aconcurrenthashmapv8_node.length) == 0) {
                    aconcurrenthashmapv8_node = this.initTable();
                }

                int l;
                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                if ((concurrenthashmapv8_node = tabAt(aconcurrenthashmapv8_node, l = k - 1 & i)) == null) {
                    if (casTabAt(aconcurrenthashmapv8_node, l, (ConcurrentHashMapV8.Node) null, new ConcurrentHashMapV8.Node<>(i, k0, v0, (ConcurrentHashMapV8.Node) null))) {
                        break;
                    }
                } else {
                    int i1 = concurrenthashmapv8_node.hash;

                    if (concurrenthashmapv8_node.hash == -1) {
                        aconcurrenthashmapv8_node = this.helpTransfer(aconcurrenthashmapv8_node, concurrenthashmapv8_node);
                    } else {
                        Object object = null;

                        synchronized (concurrenthashmapv8_node) {
                            if (tabAt(aconcurrenthashmapv8_node, l) == concurrenthashmapv8_node) {
                                if (i1 >= 0) {
                                    j = 1;
                                    ConcurrentHashMapV8.Node concurrenthashmapv8_node1 = concurrenthashmapv8_node;

                                    while (true) {
                                        if (concurrenthashmapv8_node1.hash == i) {
                                            Object object1 = concurrenthashmapv8_node1.key;

                                            if (concurrenthashmapv8_node1.key == k0 || object1 != null && k0.equals(object1)) {
                                                object = concurrenthashmapv8_node1.val;
                                                if (!flag) {
                                                    concurrenthashmapv8_node1.val = v0;
                                                }
                                                break;
                                            }
                                        }

                                        ConcurrentHashMapV8.Node concurrenthashmapv8_node2 = concurrenthashmapv8_node1;

                                        if ((concurrenthashmapv8_node1 = concurrenthashmapv8_node1.next) == null) {
                                            concurrenthashmapv8_node2.next = new ConcurrentHashMapV8.Node<>(i, k0, v0, (ConcurrentHashMapV8.Node) null);
                                            break;
                                        }

                                        ++j;
                                    }
                                } else if (concurrenthashmapv8_node instanceof ConcurrentHashMapV8.TreeBin) {
                                    j = 2;
                                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode;

                                    if ((concurrenthashmapv8_treenode = ((ConcurrentHashMapV8.TreeBin) concurrenthashmapv8_node).putTreeVal(i, k0, v0)) != null) {
                                        object = concurrenthashmapv8_treenode.val;
                                        if (!flag) {
                                            concurrenthashmapv8_treenode.val = v0;
                                        }
                                    }
                                }
                            }
                        }

                        if (j != 0) {
                            if (j >= 8) {
                                this.treeifyBin(aconcurrenthashmapv8_node, l);
                            }

                            if (object != null) {
                                return object;
                            }
                            break;
                        }
                    }
                }
            }

            this.addCount(1L, j);
            return null;
        } else {
            throw new NullPointerException();
        }
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        this.tryPresize(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.putVal(entry.getKey(), entry.getValue(), false);
        }

    }

    public V remove(Object object) {
        return this.replaceNode(object, (Object) null, (Object) null);
    }

    final V replaceNode(Object object, V v0, Object object1) {
        int i = spread(object.hashCode());
        ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;

        ConcurrentHashMapV8.Node concurrenthashmapv8_node;
        int j;
        int k;

        while (aconcurrenthashmapv8_node != null && (k = aconcurrenthashmapv8_node.length) != 0 && (concurrenthashmapv8_node = tabAt(aconcurrenthashmapv8_node, j = k - 1 & i)) != null) {
            int l = concurrenthashmapv8_node.hash;

            if (concurrenthashmapv8_node.hash == -1) {
                aconcurrenthashmapv8_node = this.helpTransfer(aconcurrenthashmapv8_node, concurrenthashmapv8_node);
            } else {
                Object object2 = null;
                boolean flag = false;

                synchronized (concurrenthashmapv8_node) {
                    if (tabAt(aconcurrenthashmapv8_node, j) == concurrenthashmapv8_node) {
                        Object object3;

                        if (l >= 0) {
                            flag = true;
                            ConcurrentHashMapV8.Node concurrenthashmapv8_node1 = concurrenthashmapv8_node;
                            ConcurrentHashMapV8.Node concurrenthashmapv8_node2 = null;

                            do {
                                if (concurrenthashmapv8_node1.hash == i) {
                                    Object object4 = concurrenthashmapv8_node1.key;

                                    if (concurrenthashmapv8_node1.key == object || object4 != null && object.equals(object4)) {
                                        object3 = concurrenthashmapv8_node1.val;
                                        if (object1 == null || object1 == object3 || object3 != null && object1.equals(object3)) {
                                            object2 = object3;
                                            if (v0 != null) {
                                                concurrenthashmapv8_node1.val = v0;
                                            } else if (concurrenthashmapv8_node2 != null) {
                                                concurrenthashmapv8_node2.next = concurrenthashmapv8_node1.next;
                                            } else {
                                                setTabAt(aconcurrenthashmapv8_node, j, concurrenthashmapv8_node1.next);
                                            }
                                        }
                                        break;
                                    }
                                }

                                concurrenthashmapv8_node2 = concurrenthashmapv8_node1;
                            } while ((concurrenthashmapv8_node1 = concurrenthashmapv8_node1.next) != null);
                        } else if (concurrenthashmapv8_node instanceof ConcurrentHashMapV8.TreeBin) {
                            flag = true;
                            ConcurrentHashMapV8.TreeBin concurrenthashmapv8_treebin = (ConcurrentHashMapV8.TreeBin) concurrenthashmapv8_node;
                            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode = concurrenthashmapv8_treebin.root;
                            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode1;

                            if (concurrenthashmapv8_treebin.root != null && (concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode.findTreeNode(i, object, (Class) null)) != null) {
                                object3 = concurrenthashmapv8_treenode1.val;
                                if (object1 == null || object1 == object3 || object3 != null && object1.equals(object3)) {
                                    object2 = object3;
                                    if (v0 != null) {
                                        concurrenthashmapv8_treenode1.val = v0;
                                    } else if (concurrenthashmapv8_treebin.removeTreeNode(concurrenthashmapv8_treenode1)) {
                                        setTabAt(aconcurrenthashmapv8_node, j, untreeify(concurrenthashmapv8_treebin.first));
                                    }
                                }
                            }
                        }
                    }
                }

                if (flag) {
                    if (object2 != null) {
                        if (v0 == null) {
                            this.addCount(-1L, -1);
                        }

                        return object2;
                    }
                    break;
                }
            }
        }

        return null;
    }

    public void clear() {
        long i = 0L;
        int j = 0;
        ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;

        while (aconcurrenthashmapv8_node != null && j < aconcurrenthashmapv8_node.length) {
            ConcurrentHashMapV8.Node concurrenthashmapv8_node = tabAt(aconcurrenthashmapv8_node, j);

            if (concurrenthashmapv8_node == null) {
                ++j;
            } else {
                int k = concurrenthashmapv8_node.hash;

                if (concurrenthashmapv8_node.hash == -1) {
                    aconcurrenthashmapv8_node = this.helpTransfer(aconcurrenthashmapv8_node, concurrenthashmapv8_node);
                    j = 0;
                } else {
                    synchronized (concurrenthashmapv8_node) {
                        if (tabAt(aconcurrenthashmapv8_node, j) == concurrenthashmapv8_node) {
                            for (Object object = k >= 0 ? concurrenthashmapv8_node : (concurrenthashmapv8_node instanceof ConcurrentHashMapV8.TreeBin ? ((ConcurrentHashMapV8.TreeBin) concurrenthashmapv8_node).first : null); object != null; object = ((ConcurrentHashMapV8.Node) object).next) {
                                --i;
                            }

                            setTabAt(aconcurrenthashmapv8_node, j++, (ConcurrentHashMapV8.Node) null);
                        }
                    }
                }
            }
        }

        if (i != 0L) {
            this.addCount(i, -1);
        }

    }

    public ConcurrentHashMapV8.KeySetView<K, V> keySet() {
        ConcurrentHashMapV8.KeySetView concurrenthashmapv8_keysetview = this.keySet;

        return this.keySet != null ? concurrenthashmapv8_keysetview : (this.keySet = new ConcurrentHashMapV8.KeySetView<>(this, (Object) null));
    }

    public Collection<V> values() {
        ConcurrentHashMapV8.ValuesView concurrenthashmapv8_valuesview = this.values;

        return this.values != null ? concurrenthashmapv8_valuesview : (this.values = new ConcurrentHashMapV8.ValuesView<>(this));
    }

    public Set<Entry<K, V>> entrySet() {
        ConcurrentHashMapV8.EntrySetView concurrenthashmapv8_entrysetview = this.entrySet;

        return this.entrySet != null ? concurrenthashmapv8_entrysetview : (this.entrySet = new ConcurrentHashMapV8.EntrySetView<>(this));
    }

    public int hashCode() {
        int i = 0;
        ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;
        ConcurrentHashMapV8.Node concurrenthashmapv8_node;

        if (this.table != null) {
            for (ConcurrentHashMapV8.Traverser concurrenthashmapv8_traverser = new ConcurrentHashMapV8.Traverser<>(aconcurrenthashmapv8_node, aconcurrenthashmapv8_node.length, 0, aconcurrenthashmapv8_node.length); (concurrenthashmapv8_node = concurrenthashmapv8_traverser.advance()) != null; i += concurrenthashmapv8_node.key.hashCode() ^ concurrenthashmapv8_node.val.hashCode()) {
                ;
            }
        }

        return i;
    }

    public String toString() {
        ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;
        int i = this.table == null ? 0 : aconcurrenthashmapv8_node.length;
        ConcurrentHashMapV8.Traverser concurrenthashmapv8_traverser = new ConcurrentHashMapV8.Traverser<>(aconcurrenthashmapv8_node, i, 0, i);
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append('{');
        ConcurrentHashMapV8.Node concurrenthashmapv8_node;

        if ((concurrenthashmapv8_node = concurrenthashmapv8_traverser.advance()) != null) {
            while (true) {
                Object object = concurrenthashmapv8_node.key;
                Object object1 = concurrenthashmapv8_node.val;

                stringbuilder.append(object == this ? "(this Map)" : object);
                stringbuilder.append('=');
                stringbuilder.append(object1 == this ? "(this Map)" : object1);
                if ((concurrenthashmapv8_node = concurrenthashmapv8_traverser.advance()) == null) {
                    break;
                }

                stringbuilder.append(',').append(' ');
            }
        }

        return stringbuilder.append('}').toString();
    }

    public boolean equals(Object object) {
        if (object != this) {
            if (!(object instanceof Map)) {
                return false;
            } else {
                Map map = (Map) object;
                ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;
                int i = this.table == null ? 0 : aconcurrenthashmapv8_node.length;
                ConcurrentHashMapV8.Traverser concurrenthashmapv8_traverser = new ConcurrentHashMapV8.Traverser<>(aconcurrenthashmapv8_node, i, 0, i);

                while (true) {
                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;
                    Object object1;

                    if ((concurrenthashmapv8_node = concurrenthashmapv8_traverser.advance()) != null) {
                        Object object2 = concurrenthashmapv8_node.val;

                        object1 = map.get(concurrenthashmapv8_node.key);
                        if (object1 != null && (object1 == object2 || object1.equals(object2))) {
                            continue;
                        }

                        return false;
                    }

                    Iterator iterator = map.entrySet().iterator();

                    Entry entry;
                    Object object3;
                    Object object4;

                    do {
                        if (!iterator.hasNext()) {
                            return true;
                        }

                        entry = (Entry) iterator.next();
                    } while ((object1 = entry.getKey()) != null && (object3 = entry.getValue()) != null && (object4 = this.get(object1)) != null && (object3 == object4 || object3.equals(object4)));

                    return false;
                }
            }
        } else {
            return true;
        }
    }

    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        int i = 0;

        int j;

        for (j = 1; j < 16; j <<= 1) {
            ++i;
        }

        int k = 32 - i;
        int l = j - 1;
        ConcurrentHashMapV8.Segment[] aconcurrenthashmapv8_segment = (ConcurrentHashMapV8.Segment[]) (new ConcurrentHashMapV8.Segment[16]);

        for (int i1 = 0; i1 < aconcurrenthashmapv8_segment.length; ++i1) {
            aconcurrenthashmapv8_segment[i1] = new ConcurrentHashMapV8.Segment<>(0.75F);
        }

        objectoutputstream.putFields().put("segments", aconcurrenthashmapv8_segment);
        objectoutputstream.putFields().put("segmentShift", k);
        objectoutputstream.putFields().put("segmentMask", l);
        objectoutputstream.writeFields();
        ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;

        if (this.table != null) {
            ConcurrentHashMapV8.Traverser concurrenthashmapv8_traverser = new ConcurrentHashMapV8.Traverser<>(aconcurrenthashmapv8_node, aconcurrenthashmapv8_node.length, 0, aconcurrenthashmapv8_node.length);

            ConcurrentHashMapV8.Node concurrenthashmapv8_node;

            while ((concurrenthashmapv8_node = concurrenthashmapv8_traverser.advance()) != null) {
                objectoutputstream.writeObject(concurrenthashmapv8_node.key);
                objectoutputstream.writeObject(concurrenthashmapv8_node.val);
            }
        }

        objectoutputstream.writeObject((Object) null);
        objectoutputstream.writeObject((Object) null);
        aconcurrenthashmapv8_segment = null;
    }

    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        this.sizeCtl = -1;
        objectinputstream.defaultReadObject();
        long i = 0L;
        ConcurrentHashMapV8.Node concurrenthashmapv8_node = null;

        while (true) {
            Object object = objectinputstream.readObject();
            Object object1 = objectinputstream.readObject();

            if (object == null || object1 == null) {
                if (i == 0L) {
                    this.sizeCtl = 0;
                } else {
                    int j;

                    if (i >= 536870912L) {
                        j = 1073741824;
                    } else {
                        int k = (int) i;

                        j = tableSizeFor(k + (k >>> 1) + 1);
                    }

                    ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = (ConcurrentHashMapV8.Node[]) (new ConcurrentHashMapV8.Node[j]);
                    int l = j - 1;

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node1;
                    long i1;

                    for (i1 = 0L; concurrenthashmapv8_node != null; concurrenthashmapv8_node = concurrenthashmapv8_node1) {
                        concurrenthashmapv8_node1 = concurrenthashmapv8_node.next;
                        int j1 = concurrenthashmapv8_node.hash;
                        int k1 = j1 & l;
                        boolean flag;
                        ConcurrentHashMapV8.Node concurrenthashmapv8_node2;

                        if ((concurrenthashmapv8_node2 = tabAt(aconcurrenthashmapv8_node, k1)) == null) {
                            flag = true;
                        } else {
                            Object object2 = concurrenthashmapv8_node.key;

                            if (concurrenthashmapv8_node2.hash < 0) {
                                ConcurrentHashMapV8.TreeBin concurrenthashmapv8_treebin = (ConcurrentHashMapV8.TreeBin) concurrenthashmapv8_node2;

                                if (concurrenthashmapv8_treebin.putTreeVal(j1, object2, concurrenthashmapv8_node.val) == null) {
                                    ++i1;
                                }

                                flag = false;
                            } else {
                                int l1 = 0;

                                flag = true;

                                ConcurrentHashMapV8.Node concurrenthashmapv8_node3;

                                for (concurrenthashmapv8_node3 = concurrenthashmapv8_node2; concurrenthashmapv8_node3 != null; concurrenthashmapv8_node3 = concurrenthashmapv8_node3.next) {
                                    if (concurrenthashmapv8_node3.hash == j1) {
                                        Object object3 = concurrenthashmapv8_node3.key;

                                        if (concurrenthashmapv8_node3.key == object2 || object3 != null && object2.equals(object3)) {
                                            flag = false;
                                            break;
                                        }
                                    }

                                    ++l1;
                                }

                                if (flag && l1 >= 8) {
                                    flag = false;
                                    ++i1;
                                    concurrenthashmapv8_node.next = concurrenthashmapv8_node2;
                                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode = null;
                                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode1 = null;

                                    for (concurrenthashmapv8_node3 = concurrenthashmapv8_node; concurrenthashmapv8_node3 != null; concurrenthashmapv8_node3 = concurrenthashmapv8_node3.next) {
                                        ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode2 = new ConcurrentHashMapV8.TreeNode<>(concurrenthashmapv8_node3.hash, concurrenthashmapv8_node3.key, concurrenthashmapv8_node3.val, (ConcurrentHashMapV8.Node) null, (ConcurrentHashMapV8.TreeNode) null);

                                        if ((concurrenthashmapv8_treenode2.prev = concurrenthashmapv8_treenode1) == null) {
                                            concurrenthashmapv8_treenode = concurrenthashmapv8_treenode2;
                                        } else {
                                            concurrenthashmapv8_treenode1.next = concurrenthashmapv8_treenode2;
                                        }

                                        concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode2;
                                    }

                                    setTabAt(aconcurrenthashmapv8_node, k1, new ConcurrentHashMapV8.TreeBin<>(concurrenthashmapv8_treenode));
                                }
                            }
                        }

                        if (flag) {
                            ++i1;
                            concurrenthashmapv8_node.next = concurrenthashmapv8_node2;
                            setTabAt(aconcurrenthashmapv8_node, k1, concurrenthashmapv8_node);
                        }
                    }

                    this.table = aconcurrenthashmapv8_node;
                    this.sizeCtl = j - (j >>> 2);
                    this.baseCount = i1;
                }

                return;
            }

            concurrenthashmapv8_node = new ConcurrentHashMapV8.Node<>(spread(object.hashCode()), object, object1, concurrenthashmapv8_node);
            ++i;
        }
    }

    public V putIfAbsent(K k0, V v0) {
        return this.putVal(k0, v0, true);
    }

    public boolean remove(Object object, Object object1) {
        if (object == null) {
            throw new NullPointerException();
        } else {
            return object1 != null && this.replaceNode(object, (Object) null, object1) != null;
        }
    }

    public boolean replace(K k0, V v0, V v1) {
        if (k0 != null && v0 != null && v1 != null) {
            return this.replaceNode(k0, v1, v0) != null;
        } else {
            throw new NullPointerException();
        }
    }

    public V replace(K k0, V v0) {
        if (k0 != null && v0 != null) {
            return this.replaceNode(k0, v0, (Object) null);
        } else {
            throw new NullPointerException();
        }
    }

    public V getOrDefault(Object object, V v0) {
        Object object1;

        return (object1 = this.get(object)) == null ? v0 : object1;
    }

    public void forEach(ConcurrentHashMapV8.BiAction<? super K, ? super V> concurrenthashmapv8_biaction) {
        if (concurrenthashmapv8_biaction == null) {
            throw new NullPointerException();
        } else {
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;

            if (this.table != null) {
                ConcurrentHashMapV8.Traverser concurrenthashmapv8_traverser = new ConcurrentHashMapV8.Traverser<>(aconcurrenthashmapv8_node, aconcurrenthashmapv8_node.length, 0, aconcurrenthashmapv8_node.length);

                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                while ((concurrenthashmapv8_node = concurrenthashmapv8_traverser.advance()) != null) {
                    concurrenthashmapv8_biaction.apply(concurrenthashmapv8_node.key, concurrenthashmapv8_node.val);
                }
            }

        }
    }

    public void replaceAll(ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends V> concurrenthashmapv8_bifun) {
        if (concurrenthashmapv8_bifun == null) {
            throw new NullPointerException();
        } else {
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;

            if (this.table != null) {
                ConcurrentHashMapV8.Traverser concurrenthashmapv8_traverser = new ConcurrentHashMapV8.Traverser<>(aconcurrenthashmapv8_node, aconcurrenthashmapv8_node.length, 0, aconcurrenthashmapv8_node.length);

                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                while ((concurrenthashmapv8_node = concurrenthashmapv8_traverser.advance()) != null) {
                    Object object = concurrenthashmapv8_node.val;
                    Object object1 = concurrenthashmapv8_node.key;

                    while (true) {
                        Object object2 = concurrenthashmapv8_bifun.apply(object1, object);

                        if (object2 == null) {
                            throw new NullPointerException();
                        }

                        if (this.replaceNode(object1, object2, object) != null || (object = this.get(object1)) == null) {
                            break;
                        }
                    }
                }
            }

        }
    }

    public V computeIfAbsent(K k0, ConcurrentHashMapV8.Fun<? super K, ? extends V> concurrenthashmapv8_fun) {
        if (k0 != null && concurrenthashmapv8_fun != null) {
            int i = spread(k0.hashCode());
            Object object = null;
            int j = 0;
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;

            while (true) {
                int k;

                while (aconcurrenthashmapv8_node == null || (k = aconcurrenthashmapv8_node.length) == 0) {
                    aconcurrenthashmapv8_node = this.initTable();
                }

                ConcurrentHashMapV8.Node concurrenthashmapv8_node;
                int l;
                ConcurrentHashMapV8.Node concurrenthashmapv8_node1;

                if ((concurrenthashmapv8_node1 = tabAt(aconcurrenthashmapv8_node, l = k - 1 & i)) == null) {
                    ConcurrentHashMapV8.ReservationNode concurrenthashmapv8_reservationnode = new ConcurrentHashMapV8.ReservationNode<>();

                    synchronized (concurrenthashmapv8_reservationnode) {
                        if (casTabAt(aconcurrenthashmapv8_node, l, (ConcurrentHashMapV8.Node) null, concurrenthashmapv8_reservationnode)) {
                            j = 1;
                            concurrenthashmapv8_node = null;

                            try {
                                if ((object = concurrenthashmapv8_fun.apply(k0)) != null) {
                                    concurrenthashmapv8_node = new ConcurrentHashMapV8.Node<>(i, k0, object, (ConcurrentHashMapV8.Node) null);
                                }
                            } finally {
                                setTabAt(aconcurrenthashmapv8_node, l, concurrenthashmapv8_node);
                            }
                        }
                    }

                    if (j != 0) {
                        break;
                    }
                } else {
                    int i1 = concurrenthashmapv8_node1.hash;

                    if (concurrenthashmapv8_node1.hash == -1) {
                        aconcurrenthashmapv8_node = this.helpTransfer(aconcurrenthashmapv8_node, concurrenthashmapv8_node1);
                    } else {
                        boolean flag = false;

                        synchronized (concurrenthashmapv8_node1) {
                            if (tabAt(aconcurrenthashmapv8_node, l) == concurrenthashmapv8_node1) {
                                if (i1 >= 0) {
                                    j = 1;
                                    concurrenthashmapv8_node = concurrenthashmapv8_node1;

                                    while (true) {
                                        if (concurrenthashmapv8_node.hash == i) {
                                            Object object1 = concurrenthashmapv8_node.key;

                                            if (concurrenthashmapv8_node.key == k0 || object1 != null && k0.equals(object1)) {
                                                object = concurrenthashmapv8_node.val;
                                                break;
                                            }
                                        }

                                        ConcurrentHashMapV8.Node concurrenthashmapv8_node2 = concurrenthashmapv8_node;

                                        if ((concurrenthashmapv8_node = concurrenthashmapv8_node.next) == null) {
                                            if ((object = concurrenthashmapv8_fun.apply(k0)) != null) {
                                                flag = true;
                                                concurrenthashmapv8_node2.next = new ConcurrentHashMapV8.Node<>(i, k0, object, (ConcurrentHashMapV8.Node) null);
                                            }
                                            break;
                                        }

                                        ++j;
                                    }
                                } else if (concurrenthashmapv8_node1 instanceof ConcurrentHashMapV8.TreeBin) {
                                    j = 2;
                                    ConcurrentHashMapV8.TreeBin concurrenthashmapv8_treebin = (ConcurrentHashMapV8.TreeBin) concurrenthashmapv8_node1;
                                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode = concurrenthashmapv8_treebin.root;
                                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode1;

                                    if (concurrenthashmapv8_treebin.root != null && (concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode.findTreeNode(i, k0, (Class) null)) != null) {
                                        object = concurrenthashmapv8_treenode1.val;
                                    } else if ((object = concurrenthashmapv8_fun.apply(k0)) != null) {
                                        flag = true;
                                        concurrenthashmapv8_treebin.putTreeVal(i, k0, object);
                                    }
                                }
                            }
                        }

                        if (j != 0) {
                            if (j >= 8) {
                                this.treeifyBin(aconcurrenthashmapv8_node, l);
                            }

                            if (!flag) {
                                return object;
                            }
                            break;
                        }
                    }
                }
            }

            if (object != null) {
                this.addCount(1L, j);
            }

            return object;
        } else {
            throw new NullPointerException();
        }
    }

    public V computeIfPresent(K k0, ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends V> concurrenthashmapv8_bifun) {
        if (k0 != null && concurrenthashmapv8_bifun != null) {
            int i = spread(k0.hashCode());
            Object object = null;
            byte b0 = 0;
            int j = 0;
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;

            while (true) {
                int k;

                while (aconcurrenthashmapv8_node == null || (k = aconcurrenthashmapv8_node.length) == 0) {
                    aconcurrenthashmapv8_node = this.initTable();
                }

                ConcurrentHashMapV8.Node concurrenthashmapv8_node;
                int l;

                if ((concurrenthashmapv8_node = tabAt(aconcurrenthashmapv8_node, l = k - 1 & i)) == null) {
                    break;
                }

                int i1 = concurrenthashmapv8_node.hash;

                if (concurrenthashmapv8_node.hash == -1) {
                    aconcurrenthashmapv8_node = this.helpTransfer(aconcurrenthashmapv8_node, concurrenthashmapv8_node);
                } else {
                    synchronized (concurrenthashmapv8_node) {
                        if (tabAt(aconcurrenthashmapv8_node, l) == concurrenthashmapv8_node) {
                            if (i1 >= 0) {
                                j = 1;
                                ConcurrentHashMapV8.Node concurrenthashmapv8_node1 = concurrenthashmapv8_node;
                                ConcurrentHashMapV8.Node concurrenthashmapv8_node2 = null;

                                while (true) {
                                    if (concurrenthashmapv8_node1.hash == i) {
                                        Object object1 = concurrenthashmapv8_node1.key;

                                        if (concurrenthashmapv8_node1.key == k0 || object1 != null && k0.equals(object1)) {
                                            object = concurrenthashmapv8_bifun.apply(k0, concurrenthashmapv8_node1.val);
                                            if (object != null) {
                                                concurrenthashmapv8_node1.val = object;
                                            } else {
                                                b0 = -1;
                                                ConcurrentHashMapV8.Node concurrenthashmapv8_node3 = concurrenthashmapv8_node1.next;

                                                if (concurrenthashmapv8_node2 != null) {
                                                    concurrenthashmapv8_node2.next = concurrenthashmapv8_node3;
                                                } else {
                                                    setTabAt(aconcurrenthashmapv8_node, l, concurrenthashmapv8_node3);
                                                }
                                            }
                                            break;
                                        }
                                    }

                                    concurrenthashmapv8_node2 = concurrenthashmapv8_node1;
                                    if ((concurrenthashmapv8_node1 = concurrenthashmapv8_node1.next) == null) {
                                        break;
                                    }

                                    ++j;
                                }
                            } else if (concurrenthashmapv8_node instanceof ConcurrentHashMapV8.TreeBin) {
                                j = 2;
                                ConcurrentHashMapV8.TreeBin concurrenthashmapv8_treebin = (ConcurrentHashMapV8.TreeBin) concurrenthashmapv8_node;
                                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode = concurrenthashmapv8_treebin.root;
                                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode1;

                                if (concurrenthashmapv8_treebin.root != null && (concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode.findTreeNode(i, k0, (Class) null)) != null) {
                                    object = concurrenthashmapv8_bifun.apply(k0, concurrenthashmapv8_treenode1.val);
                                    if (object != null) {
                                        concurrenthashmapv8_treenode1.val = object;
                                    } else {
                                        b0 = -1;
                                        if (concurrenthashmapv8_treebin.removeTreeNode(concurrenthashmapv8_treenode1)) {
                                            setTabAt(aconcurrenthashmapv8_node, l, untreeify(concurrenthashmapv8_treebin.first));
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (j != 0) {
                        break;
                    }
                }
            }

            if (b0 != 0) {
                this.addCount((long) b0, j);
            }

            return object;
        } else {
            throw new NullPointerException();
        }
    }

    public V compute(K k0, ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends V> concurrenthashmapv8_bifun) {
        if (k0 != null && concurrenthashmapv8_bifun != null) {
            int i = spread(k0.hashCode());
            Object object = null;
            byte b0 = 0;
            int j = 0;
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;

            while (true) {
                int k;

                while (aconcurrenthashmapv8_node == null || (k = aconcurrenthashmapv8_node.length) == 0) {
                    aconcurrenthashmapv8_node = this.initTable();
                }

                ConcurrentHashMapV8.Node concurrenthashmapv8_node;
                int l;
                ConcurrentHashMapV8.Node concurrenthashmapv8_node1;

                if ((concurrenthashmapv8_node1 = tabAt(aconcurrenthashmapv8_node, l = k - 1 & i)) == null) {
                    ConcurrentHashMapV8.ReservationNode concurrenthashmapv8_reservationnode = new ConcurrentHashMapV8.ReservationNode<>();

                    synchronized (concurrenthashmapv8_reservationnode) {
                        if (casTabAt(aconcurrenthashmapv8_node, l, (ConcurrentHashMapV8.Node) null, concurrenthashmapv8_reservationnode)) {
                            j = 1;
                            concurrenthashmapv8_node = null;

                            try {
                                if ((object = concurrenthashmapv8_bifun.apply(k0, (Object) null)) != null) {
                                    b0 = 1;
                                    concurrenthashmapv8_node = new ConcurrentHashMapV8.Node<>(i, k0, object, (ConcurrentHashMapV8.Node) null);
                                }
                            } finally {
                                setTabAt(aconcurrenthashmapv8_node, l, concurrenthashmapv8_node);
                            }
                        }
                    }

                    if (j != 0) {
                        break;
                    }
                } else {
                    int i1 = concurrenthashmapv8_node1.hash;

                    if (concurrenthashmapv8_node1.hash == -1) {
                        aconcurrenthashmapv8_node = this.helpTransfer(aconcurrenthashmapv8_node, concurrenthashmapv8_node1);
                    } else {
                        synchronized (concurrenthashmapv8_node1) {
                            if (tabAt(aconcurrenthashmapv8_node, l) == concurrenthashmapv8_node1) {
                                if (i1 >= 0) {
                                    j = 1;
                                    ConcurrentHashMapV8.Node concurrenthashmapv8_node2 = concurrenthashmapv8_node1;

                                    concurrenthashmapv8_node = null;

                                    while (true) {
                                        if (concurrenthashmapv8_node2.hash == i) {
                                            Object object1 = concurrenthashmapv8_node2.key;

                                            if (concurrenthashmapv8_node2.key == k0 || object1 != null && k0.equals(object1)) {
                                                object = concurrenthashmapv8_bifun.apply(k0, concurrenthashmapv8_node2.val);
                                                if (object != null) {
                                                    concurrenthashmapv8_node2.val = object;
                                                } else {
                                                    b0 = -1;
                                                    ConcurrentHashMapV8.Node concurrenthashmapv8_node3 = concurrenthashmapv8_node2.next;

                                                    if (concurrenthashmapv8_node != null) {
                                                        concurrenthashmapv8_node.next = concurrenthashmapv8_node3;
                                                    } else {
                                                        setTabAt(aconcurrenthashmapv8_node, l, concurrenthashmapv8_node3);
                                                    }
                                                }
                                                break;
                                            }
                                        }

                                        concurrenthashmapv8_node = concurrenthashmapv8_node2;
                                        if ((concurrenthashmapv8_node2 = concurrenthashmapv8_node2.next) == null) {
                                            object = concurrenthashmapv8_bifun.apply(k0, (Object) null);
                                            if (object != null) {
                                                b0 = 1;
                                                concurrenthashmapv8_node.next = new ConcurrentHashMapV8.Node<>(i, k0, object, (ConcurrentHashMapV8.Node) null);
                                            }
                                            break;
                                        }

                                        ++j;
                                    }
                                } else if (concurrenthashmapv8_node1 instanceof ConcurrentHashMapV8.TreeBin) {
                                    j = 1;
                                    ConcurrentHashMapV8.TreeBin concurrenthashmapv8_treebin = (ConcurrentHashMapV8.TreeBin) concurrenthashmapv8_node1;
                                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode = concurrenthashmapv8_treebin.root;
                                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode1;

                                    if (concurrenthashmapv8_treebin.root != null) {
                                        concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode.findTreeNode(i, k0, (Class) null);
                                    } else {
                                        concurrenthashmapv8_treenode1 = null;
                                    }

                                    Object object2 = concurrenthashmapv8_treenode1 == null ? null : concurrenthashmapv8_treenode1.val;

                                    object = concurrenthashmapv8_bifun.apply(k0, object2);
                                    if (object != null) {
                                        if (concurrenthashmapv8_treenode1 != null) {
                                            concurrenthashmapv8_treenode1.val = object;
                                        } else {
                                            b0 = 1;
                                            concurrenthashmapv8_treebin.putTreeVal(i, k0, object);
                                        }
                                    } else if (concurrenthashmapv8_treenode1 != null) {
                                        b0 = -1;
                                        if (concurrenthashmapv8_treebin.removeTreeNode(concurrenthashmapv8_treenode1)) {
                                            setTabAt(aconcurrenthashmapv8_node, l, untreeify(concurrenthashmapv8_treebin.first));
                                        }
                                    }
                                }
                            }
                        }

                        if (j != 0) {
                            if (j >= 8) {
                                this.treeifyBin(aconcurrenthashmapv8_node, l);
                            }
                            break;
                        }
                    }
                }
            }

            if (b0 != 0) {
                this.addCount((long) b0, j);
            }

            return object;
        } else {
            throw new NullPointerException();
        }
    }

    public V merge(K k0, V v0, ConcurrentHashMapV8.BiFun<? super V, ? super V, ? extends V> concurrenthashmapv8_bifun) {
        if (k0 != null && v0 != null && concurrenthashmapv8_bifun != null) {
            int i = spread(k0.hashCode());
            Object object = null;
            byte b0 = 0;
            int j = 0;
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;

            while (true) {
                int k;

                while (aconcurrenthashmapv8_node == null || (k = aconcurrenthashmapv8_node.length) == 0) {
                    aconcurrenthashmapv8_node = this.initTable();
                }

                int l;
                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                if ((concurrenthashmapv8_node = tabAt(aconcurrenthashmapv8_node, l = k - 1 & i)) == null) {
                    if (casTabAt(aconcurrenthashmapv8_node, l, (ConcurrentHashMapV8.Node) null, new ConcurrentHashMapV8.Node<>(i, k0, v0, (ConcurrentHashMapV8.Node) null))) {
                        b0 = 1;
                        object = v0;
                        break;
                    }
                } else {
                    int i1 = concurrenthashmapv8_node.hash;

                    if (concurrenthashmapv8_node.hash == -1) {
                        aconcurrenthashmapv8_node = this.helpTransfer(aconcurrenthashmapv8_node, concurrenthashmapv8_node);
                    } else {
                        synchronized (concurrenthashmapv8_node) {
                            if (tabAt(aconcurrenthashmapv8_node, l) == concurrenthashmapv8_node) {
                                if (i1 >= 0) {
                                    j = 1;
                                    ConcurrentHashMapV8.Node concurrenthashmapv8_node1 = concurrenthashmapv8_node;
                                    ConcurrentHashMapV8.Node concurrenthashmapv8_node2 = null;

                                    while (true) {
                                        if (concurrenthashmapv8_node1.hash == i) {
                                            Object object1 = concurrenthashmapv8_node1.key;

                                            if (concurrenthashmapv8_node1.key == k0 || object1 != null && k0.equals(object1)) {
                                                object = concurrenthashmapv8_bifun.apply(concurrenthashmapv8_node1.val, v0);
                                                if (object != null) {
                                                    concurrenthashmapv8_node1.val = object;
                                                } else {
                                                    b0 = -1;
                                                    ConcurrentHashMapV8.Node concurrenthashmapv8_node3 = concurrenthashmapv8_node1.next;

                                                    if (concurrenthashmapv8_node2 != null) {
                                                        concurrenthashmapv8_node2.next = concurrenthashmapv8_node3;
                                                    } else {
                                                        setTabAt(aconcurrenthashmapv8_node, l, concurrenthashmapv8_node3);
                                                    }
                                                }
                                                break;
                                            }
                                        }

                                        concurrenthashmapv8_node2 = concurrenthashmapv8_node1;
                                        if ((concurrenthashmapv8_node1 = concurrenthashmapv8_node1.next) == null) {
                                            b0 = 1;
                                            object = v0;
                                            concurrenthashmapv8_node2.next = new ConcurrentHashMapV8.Node<>(i, k0, v0, (ConcurrentHashMapV8.Node) null);
                                            break;
                                        }

                                        ++j;
                                    }
                                } else if (concurrenthashmapv8_node instanceof ConcurrentHashMapV8.TreeBin) {
                                    j = 2;
                                    ConcurrentHashMapV8.TreeBin concurrenthashmapv8_treebin = (ConcurrentHashMapV8.TreeBin) concurrenthashmapv8_node;
                                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode = concurrenthashmapv8_treebin.root;
                                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode == null ? null : concurrenthashmapv8_treenode.findTreeNode(i, k0, (Class) null);

                                    object = concurrenthashmapv8_treenode1 == null ? v0 : concurrenthashmapv8_bifun.apply(concurrenthashmapv8_treenode1.val, v0);
                                    if (object != null) {
                                        if (concurrenthashmapv8_treenode1 != null) {
                                            concurrenthashmapv8_treenode1.val = object;
                                        } else {
                                            b0 = 1;
                                            concurrenthashmapv8_treebin.putTreeVal(i, k0, object);
                                        }
                                    } else if (concurrenthashmapv8_treenode1 != null) {
                                        b0 = -1;
                                        if (concurrenthashmapv8_treebin.removeTreeNode(concurrenthashmapv8_treenode1)) {
                                            setTabAt(aconcurrenthashmapv8_node, l, untreeify(concurrenthashmapv8_treebin.first));
                                        }
                                    }
                                }
                            }
                        }

                        if (j != 0) {
                            if (j >= 8) {
                                this.treeifyBin(aconcurrenthashmapv8_node, l);
                            }
                            break;
                        }
                    }
                }
            }

            if (b0 != 0) {
                this.addCount((long) b0, j);
            }

            return object;
        } else {
            throw new NullPointerException();
        }
    }

    /** @deprecated */
    @Deprecated
    public boolean contains(Object object) {
        return this.containsValue(object);
    }

    public Enumeration<K> keys() {
        ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;
        int i = this.table == null ? 0 : aconcurrenthashmapv8_node.length;

        return new ConcurrentHashMapV8.KeyIterator<>(aconcurrenthashmapv8_node, i, 0, i, this);
    }

    public Enumeration<V> elements() {
        ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;
        int i = this.table == null ? 0 : aconcurrenthashmapv8_node.length;

        return new ConcurrentHashMapV8.ValueIterator<>(aconcurrenthashmapv8_node, i, 0, i, this);
    }

    public long mappingCount() {
        long i = this.sumCount();

        return i < 0L ? 0L : i;
    }

    public static <K> ConcurrentHashMapV8.KeySetView<K, Boolean> newKeySet() {
        return new ConcurrentHashMapV8.KeySetView<>(new ConcurrentHashMapV8<>(), Boolean.TRUE);
    }

    public static <K> ConcurrentHashMapV8.KeySetView<K, Boolean> newKeySet(int i) {
        return new ConcurrentHashMapV8.KeySetView<>(new ConcurrentHashMapV8<>(i), Boolean.TRUE);
    }

    public ConcurrentHashMapV8.KeySetView<K, V> keySet(V v0) {
        if (v0 == null) {
            throw new NullPointerException();
        } else {
            return new ConcurrentHashMapV8.KeySetView<>(this, v0);
        }
    }

    private final ConcurrentHashMapV8.Node<K, V>[] initTable() {
        ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node;

        while (true) {
            aconcurrenthashmapv8_node = this.table;
            if (this.table != null && aconcurrenthashmapv8_node.length != 0) {
                break;
            }

            int i = this.sizeCtl;

            if (this.sizeCtl < 0) {
                Thread.yield();
            } else if (ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, i, -1)) {
                try {
                    aconcurrenthashmapv8_node = this.table;
                    if (this.table == null || aconcurrenthashmapv8_node.length == 0) {
                        int j = i > 0 ? i : 16;
                        ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node1 = (ConcurrentHashMapV8.Node[]) (new ConcurrentHashMapV8.Node[j]);

                        aconcurrenthashmapv8_node = aconcurrenthashmapv8_node1;
                        this.table = aconcurrenthashmapv8_node1;
                        i = j - (j >>> 2);
                    }
                    break;
                } finally {
                    this.sizeCtl = i;
                }
            }
        }

        return aconcurrenthashmapv8_node;
    }

    private final void addCount(long i, int j) {
        long k;
        label70:
        {
            ConcurrentHashMapV8.CounterCell[] aconcurrenthashmapv8_countercell = this.counterCells;

            if (this.counterCells == null) {
                long l = this.baseCount;

                if (ConcurrentHashMapV8.U.compareAndSwapLong(this, ConcurrentHashMapV8.BASECOUNT, this.baseCount, k = l + i)) {
                    break label70;
                }
            }

            boolean flag = true;
            ConcurrentHashMapV8.CounterCell concurrenthashmapv8_countercell;
            int i1;
            ConcurrentHashMapV8.CounterHashCode concurrenthashmapv8_counterhashcode;

            if ((concurrenthashmapv8_counterhashcode = (ConcurrentHashMapV8.CounterHashCode) ConcurrentHashMapV8.threadCounterHashCode.get()) != null && aconcurrenthashmapv8_countercell != null && (i1 = aconcurrenthashmapv8_countercell.length - 1) >= 0 && (concurrenthashmapv8_countercell = aconcurrenthashmapv8_countercell[i1 & concurrenthashmapv8_counterhashcode.code]) != null) {
                long j1 = concurrenthashmapv8_countercell.value;

                if (flag = ConcurrentHashMapV8.U.compareAndSwapLong(concurrenthashmapv8_countercell, ConcurrentHashMapV8.CELLVALUE, concurrenthashmapv8_countercell.value, j1 + i)) {
                    if (j <= 1) {
                        return;
                    }

                    k = this.sumCount();
                    break label70;
                }
            }

            this.fullAddCount(i, concurrenthashmapv8_counterhashcode, flag);
            return;
        }

        if (j >= 0) {
            while (true) {
                int k1 = this.sizeCtl;

                if (k < (long) this.sizeCtl) {
                    break;
                }

                ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;

                if (this.table == null || aconcurrenthashmapv8_node.length >= 1073741824) {
                    break;
                }

                if (k1 < 0) {
                    if (k1 == -1 || this.transferIndex <= this.transferOrigin) {
                        break;
                    }

                    ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node1 = this.nextTable;

                    if (this.nextTable == null) {
                        break;
                    }

                    if (ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, k1, k1 - 1)) {
                        this.transfer(aconcurrenthashmapv8_node, aconcurrenthashmapv8_node1);
                    }
                } else if (ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, k1, -2)) {
                    this.transfer(aconcurrenthashmapv8_node, (ConcurrentHashMapV8.Node[]) null);
                }

                k = this.sumCount();
            }
        }

    }

    final ConcurrentHashMapV8.Node<K, V>[] helpTransfer(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.Node<K, V> concurrenthashmapv8_node) {
        if (concurrenthashmapv8_node instanceof ConcurrentHashMapV8.ForwardingNode) {
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node1 = ((ConcurrentHashMapV8.ForwardingNode) concurrenthashmapv8_node).nextTable;

            if (((ConcurrentHashMapV8.ForwardingNode) concurrenthashmapv8_node).nextTable != null) {
                if (aconcurrenthashmapv8_node1 == this.nextTable && aconcurrenthashmapv8_node == this.table && this.transferIndex > this.transferOrigin) {
                    int i = this.sizeCtl;

                    if (this.sizeCtl < -1 && ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, i, i - 1)) {
                        this.transfer(aconcurrenthashmapv8_node, aconcurrenthashmapv8_node1);
                    }
                }

                return aconcurrenthashmapv8_node1;
            }
        }

        return this.table;
    }

    private final void tryPresize(int i) {
        int j = i >= 536870912 ? 1073741824 : tableSizeFor(i + (i >>> 1) + 1);

        while (true) {
            int k = this.sizeCtl;

            if (this.sizeCtl < 0) {
                break;
            }

            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.table;
            int l;

            if (aconcurrenthashmapv8_node != null && (l = aconcurrenthashmapv8_node.length) != 0) {
                if (j <= k || l >= 1073741824) {
                    break;
                }

                if (aconcurrenthashmapv8_node == this.table && ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, k, -2)) {
                    this.transfer(aconcurrenthashmapv8_node, (ConcurrentHashMapV8.Node[]) null);
                }
            } else {
                l = k > j ? k : j;
                if (ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, k, -1)) {
                    try {
                        if (this.table == aconcurrenthashmapv8_node) {
                            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node1 = (ConcurrentHashMapV8.Node[]) (new ConcurrentHashMapV8.Node[l]);

                            this.table = aconcurrenthashmapv8_node1;
                            k = l - (l >>> 2);
                        }
                    } finally {
                        this.sizeCtl = k;
                    }
                }
            }
        }

    }

    private final void transfer(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node1) {
        int i = aconcurrenthashmapv8_node.length;
        int j;

        if ((j = ConcurrentHashMapV8.NCPU > 1 ? (i >>> 3) / ConcurrentHashMapV8.NCPU : i) < 16) {
            j = 16;
        }

        if (aconcurrenthashmapv8_node1 == null) {
            try {
                ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node2 = (ConcurrentHashMapV8.Node[]) (new ConcurrentHashMapV8.Node[i << 1]);

                aconcurrenthashmapv8_node1 = aconcurrenthashmapv8_node2;
            } catch (Throwable throwable) {
                this.sizeCtl = Integer.MAX_VALUE;
                return;
            }

            this.nextTable = aconcurrenthashmapv8_node1;
            this.transferOrigin = i;
            this.transferIndex = i;
            ConcurrentHashMapV8.ForwardingNode concurrenthashmapv8_forwardingnode = new ConcurrentHashMapV8.ForwardingNode<>(aconcurrenthashmapv8_node);
            int k = i;

            while (k > 0) {
                int l = k > j ? k - j : 0;

                int i1;

                for (i1 = l; i1 < k; ++i1) {
                    aconcurrenthashmapv8_node1[i1] = concurrenthashmapv8_forwardingnode;
                }

                for (i1 = i + l; i1 < i + k; ++i1) {
                    aconcurrenthashmapv8_node1[i1] = concurrenthashmapv8_forwardingnode;
                }

                k = l;
                ConcurrentHashMapV8.U.putOrderedInt(this, ConcurrentHashMapV8.TRANSFERORIGIN, l);
            }
        }

        int j1 = aconcurrenthashmapv8_node1.length;
        ConcurrentHashMapV8.ForwardingNode concurrenthashmapv8_forwardingnode1 = new ConcurrentHashMapV8.ForwardingNode<>(aconcurrenthashmapv8_node1);
        boolean flag = true;
        boolean flag1 = false;
        int k1 = 0;
        int l1 = 0;

        while (true) {
            while (!flag) {
                if (k1 >= 0 && k1 < i && k1 + i < j1) {
                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    if ((concurrenthashmapv8_node = tabAt(aconcurrenthashmapv8_node, k1)) == null) {
                        if (casTabAt(aconcurrenthashmapv8_node, k1, (ConcurrentHashMapV8.Node) null, concurrenthashmapv8_forwardingnode1)) {
                            setTabAt(aconcurrenthashmapv8_node1, k1, (ConcurrentHashMapV8.Node) null);
                            setTabAt(aconcurrenthashmapv8_node1, k1 + i, (ConcurrentHashMapV8.Node) null);
                            flag = true;
                        }
                    } else {
                        int i2 = concurrenthashmapv8_node.hash;

                        if (concurrenthashmapv8_node.hash == -1) {
                            flag = true;
                        } else {
                            synchronized (concurrenthashmapv8_node) {
                                if (tabAt(aconcurrenthashmapv8_node, k1) == concurrenthashmapv8_node) {
                                    if (i2 >= 0) {
                                        int j2 = i2 & i;
                                        ConcurrentHashMapV8.Node concurrenthashmapv8_node1 = concurrenthashmapv8_node;

                                        int k2;
                                        ConcurrentHashMapV8.Node concurrenthashmapv8_node2;

                                        for (concurrenthashmapv8_node2 = concurrenthashmapv8_node.next; concurrenthashmapv8_node2 != null; concurrenthashmapv8_node2 = concurrenthashmapv8_node2.next) {
                                            k2 = concurrenthashmapv8_node2.hash & i;
                                            if (k2 != j2) {
                                                j2 = k2;
                                                concurrenthashmapv8_node1 = concurrenthashmapv8_node2;
                                            }
                                        }

                                        ConcurrentHashMapV8.Node concurrenthashmapv8_node3;
                                        ConcurrentHashMapV8.Node concurrenthashmapv8_node4;

                                        if (j2 == 0) {
                                            concurrenthashmapv8_node3 = concurrenthashmapv8_node1;
                                            concurrenthashmapv8_node4 = null;
                                        } else {
                                            concurrenthashmapv8_node4 = concurrenthashmapv8_node1;
                                            concurrenthashmapv8_node3 = null;
                                        }

                                        for (concurrenthashmapv8_node2 = concurrenthashmapv8_node; concurrenthashmapv8_node2 != concurrenthashmapv8_node1; concurrenthashmapv8_node2 = concurrenthashmapv8_node2.next) {
                                            k2 = concurrenthashmapv8_node2.hash;
                                            Object object = concurrenthashmapv8_node2.key;
                                            Object object1 = concurrenthashmapv8_node2.val;

                                            if ((k2 & i) == 0) {
                                                concurrenthashmapv8_node3 = new ConcurrentHashMapV8.Node<>(k2, object, object1, concurrenthashmapv8_node3);
                                            } else {
                                                concurrenthashmapv8_node4 = new ConcurrentHashMapV8.Node<>(k2, object, object1, concurrenthashmapv8_node4);
                                            }
                                        }

                                        setTabAt(aconcurrenthashmapv8_node1, k1, concurrenthashmapv8_node3);
                                        setTabAt(aconcurrenthashmapv8_node1, k1 + i, concurrenthashmapv8_node4);
                                        setTabAt(aconcurrenthashmapv8_node, k1, concurrenthashmapv8_forwardingnode1);
                                        flag = true;
                                    } else if (concurrenthashmapv8_node instanceof ConcurrentHashMapV8.TreeBin) {
                                        ConcurrentHashMapV8.TreeBin concurrenthashmapv8_treebin = (ConcurrentHashMapV8.TreeBin) concurrenthashmapv8_node;
                                        ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode = null;
                                        ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode1 = null;
                                        ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode2 = null;
                                        ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode3 = null;
                                        int l2 = 0;
                                        int i3 = 0;

                                        for (Object object2 = concurrenthashmapv8_treebin.first; object2 != null; object2 = ((ConcurrentHashMapV8.Node) object2).next) {
                                            int j3 = ((ConcurrentHashMapV8.Node) object2).hash;
                                            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode4 = new ConcurrentHashMapV8.TreeNode<>(j3, ((ConcurrentHashMapV8.Node) object2).key, ((ConcurrentHashMapV8.Node) object2).val, (ConcurrentHashMapV8.Node) null, (ConcurrentHashMapV8.TreeNode) null);

                                            if ((j3 & i) == 0) {
                                                if ((concurrenthashmapv8_treenode4.prev = concurrenthashmapv8_treenode1) == null) {
                                                    concurrenthashmapv8_treenode = concurrenthashmapv8_treenode4;
                                                } else {
                                                    concurrenthashmapv8_treenode1.next = concurrenthashmapv8_treenode4;
                                                }

                                                concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode4;
                                                ++l2;
                                            } else {
                                                if ((concurrenthashmapv8_treenode4.prev = concurrenthashmapv8_treenode3) == null) {
                                                    concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode4;
                                                } else {
                                                    concurrenthashmapv8_treenode3.next = concurrenthashmapv8_treenode4;
                                                }

                                                concurrenthashmapv8_treenode3 = concurrenthashmapv8_treenode4;
                                                ++i3;
                                            }
                                        }

                                        Object object3 = l2 <= 6 ? untreeify(concurrenthashmapv8_treenode) : (i3 != 0 ? new ConcurrentHashMapV8.TreeBin<>(concurrenthashmapv8_treenode) : concurrenthashmapv8_treebin);
                                        Object object4 = i3 <= 6 ? untreeify(concurrenthashmapv8_treenode2) : (l2 != 0 ? new ConcurrentHashMapV8.TreeBin<>(concurrenthashmapv8_treenode2) : concurrenthashmapv8_treebin);

                                        setTabAt(aconcurrenthashmapv8_node1, k1, (ConcurrentHashMapV8.Node) object3);
                                        setTabAt(aconcurrenthashmapv8_node1, k1 + i, (ConcurrentHashMapV8.Node) object4);
                                        setTabAt(aconcurrenthashmapv8_node, k1, concurrenthashmapv8_forwardingnode1);
                                        flag = true;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (flag1) {
                        this.nextTable = null;
                        this.table = aconcurrenthashmapv8_node1;
                        this.sizeCtl = (i << 1) - (i >>> 1);
                        return;
                    }

                    int k3;

                    do {
                        k3 = this.sizeCtl;
                        ++k3;
                    } while (!ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, this.sizeCtl, k3));

                    if (k3 != -1) {
                        return;
                    }

                    flag = true;
                    flag1 = true;
                    k1 = i;
                }
            }

            --k1;
            if (k1 < l1 && !flag1) {
                int l3 = this.transferIndex;

                if (this.transferIndex <= this.transferOrigin) {
                    k1 = -1;
                    flag = false;
                } else {
                    int i4;

                    if (ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.TRANSFERINDEX, l3, i4 = l3 > j ? l3 - j : 0)) {
                        l1 = i4;
                        k1 = l3 - 1;
                        flag = false;
                    }
                }
            } else {
                flag = false;
            }
        }
    }

    private final void treeifyBin(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, int i) {
        if (aconcurrenthashmapv8_node != null) {
            int j;

            if ((j = aconcurrenthashmapv8_node.length) < 64) {
                if (aconcurrenthashmapv8_node == this.table) {
                    int k = this.sizeCtl;

                    if (this.sizeCtl >= 0 && ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, k, -2)) {
                        this.transfer(aconcurrenthashmapv8_node, (ConcurrentHashMapV8.Node[]) null);
                    }
                }
            } else {
                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                if ((concurrenthashmapv8_node = tabAt(aconcurrenthashmapv8_node, i)) != null && concurrenthashmapv8_node.hash >= 0) {
                    synchronized (concurrenthashmapv8_node) {
                        if (tabAt(aconcurrenthashmapv8_node, i) == concurrenthashmapv8_node) {
                            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode = null;
                            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode1 = null;

                            for (ConcurrentHashMapV8.Node concurrenthashmapv8_node1 = concurrenthashmapv8_node; concurrenthashmapv8_node1 != null; concurrenthashmapv8_node1 = concurrenthashmapv8_node1.next) {
                                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode2 = new ConcurrentHashMapV8.TreeNode<>(concurrenthashmapv8_node1.hash, concurrenthashmapv8_node1.key, concurrenthashmapv8_node1.val, (ConcurrentHashMapV8.Node) null, (ConcurrentHashMapV8.TreeNode) null);

                                if ((concurrenthashmapv8_treenode2.prev = concurrenthashmapv8_treenode1) == null) {
                                    concurrenthashmapv8_treenode = concurrenthashmapv8_treenode2;
                                } else {
                                    concurrenthashmapv8_treenode1.next = concurrenthashmapv8_treenode2;
                                }

                                concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode2;
                            }

                            setTabAt(aconcurrenthashmapv8_node, i, new ConcurrentHashMapV8.TreeBin<>(concurrenthashmapv8_treenode));
                        }
                    }
                }
            }
        }

    }

    static <K, V> ConcurrentHashMapV8.Node<K, V> untreeify(ConcurrentHashMapV8.Node<K, V> concurrenthashmapv8_node) {
        ConcurrentHashMapV8.Node concurrenthashmapv8_node1 = null;
        ConcurrentHashMapV8.Node concurrenthashmapv8_node2 = null;

        for (ConcurrentHashMapV8.Node concurrenthashmapv8_node3 = concurrenthashmapv8_node; concurrenthashmapv8_node3 != null; concurrenthashmapv8_node3 = concurrenthashmapv8_node3.next) {
            ConcurrentHashMapV8.Node concurrenthashmapv8_node4 = new ConcurrentHashMapV8.Node<>(concurrenthashmapv8_node3.hash, concurrenthashmapv8_node3.key, concurrenthashmapv8_node3.val, (ConcurrentHashMapV8.Node) null);

            if (concurrenthashmapv8_node2 == null) {
                concurrenthashmapv8_node1 = concurrenthashmapv8_node4;
            } else {
                concurrenthashmapv8_node2.next = concurrenthashmapv8_node4;
            }

            concurrenthashmapv8_node2 = concurrenthashmapv8_node4;
        }

        return concurrenthashmapv8_node1;
    }

    final int batchFor(long i) {
        long j;

        if (i != Long.MAX_VALUE && (j = this.sumCount()) > 1L && j >= i) {
            int k = ForkJoinPool.getCommonPoolParallelism() << 2;

            return i > 0L && (j /= i) < (long) k ? (int) j : k;
        } else {
            return 0;
        }
    }

    public void forEach(long i, ConcurrentHashMapV8.BiAction<? super K, ? super V> concurrenthashmapv8_biaction) {
        if (concurrenthashmapv8_biaction == null) {
            throw new NullPointerException();
        } else {
            (new ConcurrentHashMapV8.ForEachMappingTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, concurrenthashmapv8_biaction)).invoke();
        }
    }

    public <U> void forEach(long i, ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends U> concurrenthashmapv8_bifun, ConcurrentHashMapV8.Action<? super U> concurrenthashmapv8_action) {
        if (concurrenthashmapv8_bifun != null && concurrenthashmapv8_action != null) {
            (new ConcurrentHashMapV8.ForEachTransformedMappingTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, concurrenthashmapv8_bifun, concurrenthashmapv8_action)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public <U> U search(long i, ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends U> concurrenthashmapv8_bifun) {
        if (concurrenthashmapv8_bifun == null) {
            throw new NullPointerException();
        } else {
            return (new ConcurrentHashMapV8.SearchMappingsTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, concurrenthashmapv8_bifun, new AtomicReference())).invoke();
        }
    }

    public <U> U reduce(long i, ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends U> concurrenthashmapv8_bifun, ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> concurrenthashmapv8_bifun1) {
        if (concurrenthashmapv8_bifun != null && concurrenthashmapv8_bifun1 != null) {
            return (new ConcurrentHashMapV8.MapReduceMappingsTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceMappingsTask) null, concurrenthashmapv8_bifun, concurrenthashmapv8_bifun1)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public double reduceToDouble(long i, ConcurrentHashMapV8.ObjectByObjectToDouble<? super K, ? super V> concurrenthashmapv8_objectbyobjecttodouble, double d0, ConcurrentHashMapV8.DoubleByDoubleToDouble concurrenthashmapv8_doublebydoubletodouble) {
        if (concurrenthashmapv8_objectbyobjecttodouble != null && concurrenthashmapv8_doublebydoubletodouble != null) {
            return (Double) (new ConcurrentHashMapV8.MapReduceMappingsToDoubleTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceMappingsToDoubleTask) null, concurrenthashmapv8_objectbyobjecttodouble, d0, concurrenthashmapv8_doublebydoubletodouble)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public long reduceToLong(long i, ConcurrentHashMapV8.ObjectByObjectToLong<? super K, ? super V> concurrenthashmapv8_objectbyobjecttolong, long j, ConcurrentHashMapV8.LongByLongToLong concurrenthashmapv8_longbylongtolong) {
        if (concurrenthashmapv8_objectbyobjecttolong != null && concurrenthashmapv8_longbylongtolong != null) {
            return (Long) (new ConcurrentHashMapV8.MapReduceMappingsToLongTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceMappingsToLongTask) null, concurrenthashmapv8_objectbyobjecttolong, j, concurrenthashmapv8_longbylongtolong)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public int reduceToInt(long i, ConcurrentHashMapV8.ObjectByObjectToInt<? super K, ? super V> concurrenthashmapv8_objectbyobjecttoint, int j, ConcurrentHashMapV8.IntByIntToInt concurrenthashmapv8_intbyinttoint) {
        if (concurrenthashmapv8_objectbyobjecttoint != null && concurrenthashmapv8_intbyinttoint != null) {
            return (Integer) (new ConcurrentHashMapV8.MapReduceMappingsToIntTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceMappingsToIntTask) null, concurrenthashmapv8_objectbyobjecttoint, j, concurrenthashmapv8_intbyinttoint)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public void forEachKey(long i, ConcurrentHashMapV8.Action<? super K> concurrenthashmapv8_action) {
        if (concurrenthashmapv8_action == null) {
            throw new NullPointerException();
        } else {
            (new ConcurrentHashMapV8.ForEachKeyTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, concurrenthashmapv8_action)).invoke();
        }
    }

    public <U> void forEachKey(long i, ConcurrentHashMapV8.Fun<? super K, ? extends U> concurrenthashmapv8_fun, ConcurrentHashMapV8.Action<? super U> concurrenthashmapv8_action) {
        if (concurrenthashmapv8_fun != null && concurrenthashmapv8_action != null) {
            (new ConcurrentHashMapV8.ForEachTransformedKeyTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, concurrenthashmapv8_fun, concurrenthashmapv8_action)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public <U> U searchKeys(long i, ConcurrentHashMapV8.Fun<? super K, ? extends U> concurrenthashmapv8_fun) {
        if (concurrenthashmapv8_fun == null) {
            throw new NullPointerException();
        } else {
            return (new ConcurrentHashMapV8.SearchKeysTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, concurrenthashmapv8_fun, new AtomicReference())).invoke();
        }
    }

    public K reduceKeys(long i, ConcurrentHashMapV8.BiFun<? super K, ? super K, ? extends K> concurrenthashmapv8_bifun) {
        if (concurrenthashmapv8_bifun == null) {
            throw new NullPointerException();
        } else {
            return (new ConcurrentHashMapV8.ReduceKeysTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.ReduceKeysTask) null, concurrenthashmapv8_bifun)).invoke();
        }
    }

    public <U> U reduceKeys(long i, ConcurrentHashMapV8.Fun<? super K, ? extends U> concurrenthashmapv8_fun, ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> concurrenthashmapv8_bifun) {
        if (concurrenthashmapv8_fun != null && concurrenthashmapv8_bifun != null) {
            return (new ConcurrentHashMapV8.MapReduceKeysTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceKeysTask) null, concurrenthashmapv8_fun, concurrenthashmapv8_bifun)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public double reduceKeysToDouble(long i, ConcurrentHashMapV8.ObjectToDouble<? super K> concurrenthashmapv8_objecttodouble, double d0, ConcurrentHashMapV8.DoubleByDoubleToDouble concurrenthashmapv8_doublebydoubletodouble) {
        if (concurrenthashmapv8_objecttodouble != null && concurrenthashmapv8_doublebydoubletodouble != null) {
            return (Double) (new ConcurrentHashMapV8.MapReduceKeysToDoubleTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceKeysToDoubleTask) null, concurrenthashmapv8_objecttodouble, d0, concurrenthashmapv8_doublebydoubletodouble)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public long reduceKeysToLong(long i, ConcurrentHashMapV8.ObjectToLong<? super K> concurrenthashmapv8_objecttolong, long j, ConcurrentHashMapV8.LongByLongToLong concurrenthashmapv8_longbylongtolong) {
        if (concurrenthashmapv8_objecttolong != null && concurrenthashmapv8_longbylongtolong != null) {
            return (Long) (new ConcurrentHashMapV8.MapReduceKeysToLongTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceKeysToLongTask) null, concurrenthashmapv8_objecttolong, j, concurrenthashmapv8_longbylongtolong)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public int reduceKeysToInt(long i, ConcurrentHashMapV8.ObjectToInt<? super K> concurrenthashmapv8_objecttoint, int j, ConcurrentHashMapV8.IntByIntToInt concurrenthashmapv8_intbyinttoint) {
        if (concurrenthashmapv8_objecttoint != null && concurrenthashmapv8_intbyinttoint != null) {
            return (Integer) (new ConcurrentHashMapV8.MapReduceKeysToIntTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceKeysToIntTask) null, concurrenthashmapv8_objecttoint, j, concurrenthashmapv8_intbyinttoint)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public void forEachValue(long i, ConcurrentHashMapV8.Action<? super V> concurrenthashmapv8_action) {
        if (concurrenthashmapv8_action == null) {
            throw new NullPointerException();
        } else {
            (new ConcurrentHashMapV8.ForEachValueTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, concurrenthashmapv8_action)).invoke();
        }
    }

    public <U> void forEachValue(long i, ConcurrentHashMapV8.Fun<? super V, ? extends U> concurrenthashmapv8_fun, ConcurrentHashMapV8.Action<? super U> concurrenthashmapv8_action) {
        if (concurrenthashmapv8_fun != null && concurrenthashmapv8_action != null) {
            (new ConcurrentHashMapV8.ForEachTransformedValueTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, concurrenthashmapv8_fun, concurrenthashmapv8_action)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public <U> U searchValues(long i, ConcurrentHashMapV8.Fun<? super V, ? extends U> concurrenthashmapv8_fun) {
        if (concurrenthashmapv8_fun == null) {
            throw new NullPointerException();
        } else {
            return (new ConcurrentHashMapV8.SearchValuesTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, concurrenthashmapv8_fun, new AtomicReference())).invoke();
        }
    }

    public V reduceValues(long i, ConcurrentHashMapV8.BiFun<? super V, ? super V, ? extends V> concurrenthashmapv8_bifun) {
        if (concurrenthashmapv8_bifun == null) {
            throw new NullPointerException();
        } else {
            return (new ConcurrentHashMapV8.ReduceValuesTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.ReduceValuesTask) null, concurrenthashmapv8_bifun)).invoke();
        }
    }

    public <U> U reduceValues(long i, ConcurrentHashMapV8.Fun<? super V, ? extends U> concurrenthashmapv8_fun, ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> concurrenthashmapv8_bifun) {
        if (concurrenthashmapv8_fun != null && concurrenthashmapv8_bifun != null) {
            return (new ConcurrentHashMapV8.MapReduceValuesTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceValuesTask) null, concurrenthashmapv8_fun, concurrenthashmapv8_bifun)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public double reduceValuesToDouble(long i, ConcurrentHashMapV8.ObjectToDouble<? super V> concurrenthashmapv8_objecttodouble, double d0, ConcurrentHashMapV8.DoubleByDoubleToDouble concurrenthashmapv8_doublebydoubletodouble) {
        if (concurrenthashmapv8_objecttodouble != null && concurrenthashmapv8_doublebydoubletodouble != null) {
            return (Double) (new ConcurrentHashMapV8.MapReduceValuesToDoubleTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceValuesToDoubleTask) null, concurrenthashmapv8_objecttodouble, d0, concurrenthashmapv8_doublebydoubletodouble)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public long reduceValuesToLong(long i, ConcurrentHashMapV8.ObjectToLong<? super V> concurrenthashmapv8_objecttolong, long j, ConcurrentHashMapV8.LongByLongToLong concurrenthashmapv8_longbylongtolong) {
        if (concurrenthashmapv8_objecttolong != null && concurrenthashmapv8_longbylongtolong != null) {
            return (Long) (new ConcurrentHashMapV8.MapReduceValuesToLongTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceValuesToLongTask) null, concurrenthashmapv8_objecttolong, j, concurrenthashmapv8_longbylongtolong)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public int reduceValuesToInt(long i, ConcurrentHashMapV8.ObjectToInt<? super V> concurrenthashmapv8_objecttoint, int j, ConcurrentHashMapV8.IntByIntToInt concurrenthashmapv8_intbyinttoint) {
        if (concurrenthashmapv8_objecttoint != null && concurrenthashmapv8_intbyinttoint != null) {
            return (Integer) (new ConcurrentHashMapV8.MapReduceValuesToIntTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceValuesToIntTask) null, concurrenthashmapv8_objecttoint, j, concurrenthashmapv8_intbyinttoint)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public void forEachEntry(long i, ConcurrentHashMapV8.Action<? super Entry<K, V>> concurrenthashmapv8_action) {
        if (concurrenthashmapv8_action == null) {
            throw new NullPointerException();
        } else {
            (new ConcurrentHashMapV8.ForEachEntryTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, concurrenthashmapv8_action)).invoke();
        }
    }

    public <U> void forEachEntry(long i, ConcurrentHashMapV8.Fun<Entry<K, V>, ? extends U> concurrenthashmapv8_fun, ConcurrentHashMapV8.Action<? super U> concurrenthashmapv8_action) {
        if (concurrenthashmapv8_fun != null && concurrenthashmapv8_action != null) {
            (new ConcurrentHashMapV8.ForEachTransformedEntryTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, concurrenthashmapv8_fun, concurrenthashmapv8_action)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public <U> U searchEntries(long i, ConcurrentHashMapV8.Fun<Entry<K, V>, ? extends U> concurrenthashmapv8_fun) {
        if (concurrenthashmapv8_fun == null) {
            throw new NullPointerException();
        } else {
            return (new ConcurrentHashMapV8.SearchEntriesTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, concurrenthashmapv8_fun, new AtomicReference())).invoke();
        }
    }

    public Entry<K, V> reduceEntries(long i, ConcurrentHashMapV8.BiFun<Entry<K, V>, Entry<K, V>, ? extends Entry<K, V>> concurrenthashmapv8_bifun) {
        if (concurrenthashmapv8_bifun == null) {
            throw new NullPointerException();
        } else {
            return (Entry) (new ConcurrentHashMapV8.ReduceEntriesTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.ReduceEntriesTask) null, concurrenthashmapv8_bifun)).invoke();
        }
    }

    public <U> U reduceEntries(long i, ConcurrentHashMapV8.Fun<Entry<K, V>, ? extends U> concurrenthashmapv8_fun, ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> concurrenthashmapv8_bifun) {
        if (concurrenthashmapv8_fun != null && concurrenthashmapv8_bifun != null) {
            return (new ConcurrentHashMapV8.MapReduceEntriesTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceEntriesTask) null, concurrenthashmapv8_fun, concurrenthashmapv8_bifun)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public double reduceEntriesToDouble(long i, ConcurrentHashMapV8.ObjectToDouble<Entry<K, V>> concurrenthashmapv8_objecttodouble, double d0, ConcurrentHashMapV8.DoubleByDoubleToDouble concurrenthashmapv8_doublebydoubletodouble) {
        if (concurrenthashmapv8_objecttodouble != null && concurrenthashmapv8_doublebydoubletodouble != null) {
            return (Double) (new ConcurrentHashMapV8.MapReduceEntriesToDoubleTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceEntriesToDoubleTask) null, concurrenthashmapv8_objecttodouble, d0, concurrenthashmapv8_doublebydoubletodouble)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public long reduceEntriesToLong(long i, ConcurrentHashMapV8.ObjectToLong<Entry<K, V>> concurrenthashmapv8_objecttolong, long j, ConcurrentHashMapV8.LongByLongToLong concurrenthashmapv8_longbylongtolong) {
        if (concurrenthashmapv8_objecttolong != null && concurrenthashmapv8_longbylongtolong != null) {
            return (Long) (new ConcurrentHashMapV8.MapReduceEntriesToLongTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceEntriesToLongTask) null, concurrenthashmapv8_objecttolong, j, concurrenthashmapv8_longbylongtolong)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    public int reduceEntriesToInt(long i, ConcurrentHashMapV8.ObjectToInt<Entry<K, V>> concurrenthashmapv8_objecttoint, int j, ConcurrentHashMapV8.IntByIntToInt concurrenthashmapv8_intbyinttoint) {
        if (concurrenthashmapv8_objecttoint != null && concurrenthashmapv8_intbyinttoint != null) {
            return (Integer) (new ConcurrentHashMapV8.MapReduceEntriesToIntTask<>((ConcurrentHashMapV8.BulkTask) null, this.batchFor(i), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceEntriesToIntTask) null, concurrenthashmapv8_objecttoint, j, concurrenthashmapv8_intbyinttoint)).invoke();
        } else {
            throw new NullPointerException();
        }
    }

    final long sumCount() {
        ConcurrentHashMapV8.CounterCell[] aconcurrenthashmapv8_countercell = this.counterCells;
        long i = this.baseCount;

        if (aconcurrenthashmapv8_countercell != null) {
            for (int j = 0; j < aconcurrenthashmapv8_countercell.length; ++j) {
                ConcurrentHashMapV8.CounterCell concurrenthashmapv8_countercell;

                if ((concurrenthashmapv8_countercell = aconcurrenthashmapv8_countercell[j]) != null) {
                    i += concurrenthashmapv8_countercell.value;
                }
            }
        }

        return i;
    }

    private final void fullAddCount(long i, ConcurrentHashMapV8.CounterHashCode concurrenthashmapv8_counterhashcode, boolean flag) {
        int j;

        if (concurrenthashmapv8_counterhashcode == null) {
            concurrenthashmapv8_counterhashcode = new ConcurrentHashMapV8.CounterHashCode();
            int k = ConcurrentHashMapV8.counterHashCodeGenerator.addAndGet(1640531527);

            j = concurrenthashmapv8_counterhashcode.code = k == 0 ? 1 : k;
            ConcurrentHashMapV8.threadCounterHashCode.set(concurrenthashmapv8_counterhashcode);
        } else {
            j = concurrenthashmapv8_counterhashcode.code;
        }

        boolean flag1 = false;

        while (true) {
            ConcurrentHashMapV8.CounterCell[] aconcurrenthashmapv8_countercell = this.counterCells;
            long l;
            int i1;

            if (this.counterCells != null && (i1 = aconcurrenthashmapv8_countercell.length) > 0) {
                ConcurrentHashMapV8.CounterCell concurrenthashmapv8_countercell;

                if ((concurrenthashmapv8_countercell = aconcurrenthashmapv8_countercell[i1 - 1 & j]) == null) {
                    if (this.cellsBusy == 0) {
                        ConcurrentHashMapV8.CounterCell concurrenthashmapv8_countercell1 = new ConcurrentHashMapV8.CounterCell(i);

                        if (this.cellsBusy == 0 && ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.CELLSBUSY, 0, 1)) {
                            boolean flag2 = false;

                            try {
                                ConcurrentHashMapV8.CounterCell[] aconcurrenthashmapv8_countercell1 = this.counterCells;
                                int j1;
                                int k1;

                                if (this.counterCells != null && (k1 = aconcurrenthashmapv8_countercell1.length) > 0 && aconcurrenthashmapv8_countercell1[j1 = k1 - 1 & j] == null) {
                                    aconcurrenthashmapv8_countercell1[j1] = concurrenthashmapv8_countercell1;
                                    flag2 = true;
                                }
                            } finally {
                                this.cellsBusy = 0;
                            }

                            if (flag2) {
                                break;
                            }
                            continue;
                        }
                    }

                    flag1 = false;
                } else if (!flag) {
                    flag = true;
                } else {
                    l = concurrenthashmapv8_countercell.value;
                    if (ConcurrentHashMapV8.U.compareAndSwapLong(concurrenthashmapv8_countercell, ConcurrentHashMapV8.CELLVALUE, concurrenthashmapv8_countercell.value, l + i)) {
                        break;
                    }

                    if (this.counterCells == aconcurrenthashmapv8_countercell && i1 < ConcurrentHashMapV8.NCPU) {
                        if (!flag1) {
                            flag1 = true;
                        } else if (this.cellsBusy == 0 && ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.CELLSBUSY, 0, 1)) {
                            try {
                                if (this.counterCells == aconcurrenthashmapv8_countercell) {
                                    ConcurrentHashMapV8.CounterCell[] aconcurrenthashmapv8_countercell2 = new ConcurrentHashMapV8.CounterCell[i1 << 1];

                                    for (int l1 = 0; l1 < i1; ++l1) {
                                        aconcurrenthashmapv8_countercell2[l1] = aconcurrenthashmapv8_countercell[l1];
                                    }

                                    this.counterCells = aconcurrenthashmapv8_countercell2;
                                }
                            } finally {
                                this.cellsBusy = 0;
                            }

                            flag1 = false;
                            continue;
                        }
                    } else {
                        flag1 = false;
                    }
                }

                j ^= j << 13;
                j ^= j >>> 17;
                j ^= j << 5;
            } else if (this.cellsBusy == 0 && this.counterCells == aconcurrenthashmapv8_countercell && ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.CELLSBUSY, 0, 1)) {
                boolean flag3 = false;

                try {
                    if (this.counterCells == aconcurrenthashmapv8_countercell) {
                        ConcurrentHashMapV8.CounterCell[] aconcurrenthashmapv8_countercell3 = new ConcurrentHashMapV8.CounterCell[2];

                        aconcurrenthashmapv8_countercell3[j & 1] = new ConcurrentHashMapV8.CounterCell(i);
                        this.counterCells = aconcurrenthashmapv8_countercell3;
                        flag3 = true;
                    }
                } finally {
                    this.cellsBusy = 0;
                }

                if (flag3) {
                    break;
                }
            } else {
                l = this.baseCount;
                if (ConcurrentHashMapV8.U.compareAndSwapLong(this, ConcurrentHashMapV8.BASECOUNT, this.baseCount, l + i)) {
                    break;
                }
            }
        }

        concurrenthashmapv8_counterhashcode.code = j;
    }

    private static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        } catch (SecurityException securityexception) {
            try {
                return (Unsafe) AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
                    public Unsafe run() throws Exception {
                        Class oclass = Unsafe.class;
                        Field[] afield = oclass.getDeclaredFields();
                        int i = afield.length;

                        for (int j = 0; j < i; ++j) {
                            Field field = afield[j];

                            field.setAccessible(true);
                            Object object = field.get((Object) null);

                            if (oclass.isInstance(object)) {
                                return (Unsafe) oclass.cast(object);
                            }
                        }

                        throw new NoSuchFieldError("the Unsafe");
                    }
                });
            } catch (PrivilegedActionException privilegedactionexception) {
                throw new RuntimeException("Could not initialize intrinsics", privilegedactionexception.getCause());
            }
        }
    }

    static {
        try {
            U = getUnsafe();
            Class oclass = ConcurrentHashMapV8.class;

            SIZECTL = ConcurrentHashMapV8.U.objectFieldOffset(oclass.getDeclaredField("sizeCtl"));
            TRANSFERINDEX = ConcurrentHashMapV8.U.objectFieldOffset(oclass.getDeclaredField("transferIndex"));
            TRANSFERORIGIN = ConcurrentHashMapV8.U.objectFieldOffset(oclass.getDeclaredField("transferOrigin"));
            BASECOUNT = ConcurrentHashMapV8.U.objectFieldOffset(oclass.getDeclaredField("baseCount"));
            CELLSBUSY = ConcurrentHashMapV8.U.objectFieldOffset(oclass.getDeclaredField("cellsBusy"));
            Class oclass1 = ConcurrentHashMapV8.CounterCell.class;

            CELLVALUE = ConcurrentHashMapV8.U.objectFieldOffset(oclass1.getDeclaredField("value"));
            Class oclass2 = ConcurrentHashMapV8.Node[].class;

            ABASE = (long) ConcurrentHashMapV8.U.arrayBaseOffset(oclass2);
            int i = ConcurrentHashMapV8.U.arrayIndexScale(oclass2);

            if ((i & i - 1) != 0) {
                throw new Error("data type scale not a power of two");
            } else {
                ASHIFT = 31 - Integer.numberOfLeadingZeros(i);
            }
        } catch (Exception exception) {
            throw new Error(exception);
        }
    }

    static final class CounterHashCode {

        int code;

        CounterHashCode() {}
    }

    static final class CounterCell {

        volatile long p0;
        volatile long p1;
        volatile long p2;
        volatile long p3;
        volatile long p4;
        volatile long p5;
        volatile long p6;
        volatile long value;
        volatile long q0;
        volatile long q1;
        volatile long q2;
        volatile long q3;
        volatile long q4;
        volatile long q5;
        volatile long q6;

        CounterCell(long i) {
            this.value = i;
        }
    }

    static final class MapReduceMappingsToIntTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Integer> {

        final ConcurrentHashMapV8.ObjectByObjectToInt<? super K, ? super V> transformer;
        final ConcurrentHashMapV8.IntByIntToInt reducer;
        final int basis;
        int result;
        ConcurrentHashMapV8.MapReduceMappingsToIntTask<K, V> rights;
        ConcurrentHashMapV8.MapReduceMappingsToIntTask<K, V> nextRight;

        MapReduceMappingsToIntTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceMappingsToIntTask<K, V> concurrenthashmapv8_mapreducemappingstointtask, ConcurrentHashMapV8.ObjectByObjectToInt<? super K, ? super V> concurrenthashmapv8_objectbyobjecttoint, int l, ConcurrentHashMapV8.IntByIntToInt concurrenthashmapv8_intbyinttoint) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreducemappingstointtask;
            this.transformer = concurrenthashmapv8_objectbyobjecttoint;
            this.basis = l;
            this.reducer = concurrenthashmapv8_intbyinttoint;
        }

        @Override
        public final Integer getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.ObjectByObjectToInt concurrenthashmapv8_objectbyobjecttoint = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.IntByIntToInt concurrenthashmapv8_intbyinttoint = this.reducer;

                if (this.reducer != null) {
                    int i = this.basis;
                    int j = this.baseIndex;

                    while (this.batch > 0) {
                        int k = this.baseLimit;
                        int l;

                        if ((l = this.baseLimit + j >>> 1) <= j) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceMappingsToIntTask<>(this, this.batch >>>= 1, this.baseLimit = l, k, this.tab, this.rights, concurrenthashmapv8_objectbyobjecttoint, i, concurrenthashmapv8_intbyinttoint)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        i = concurrenthashmapv8_intbyinttoint.apply(i, concurrenthashmapv8_objectbyobjecttoint.apply(concurrenthashmapv8_node.key, concurrenthashmapv8_node.val));
                    }

                    this.result = i;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceMappingsToIntTask concurrenthashmapv8_mapreducemappingstointtask = (ConcurrentHashMapV8.MapReduceMappingsToIntTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceMappingsToIntTask concurrenthashmapv8_mapreducemappingstointtask1 = concurrenthashmapv8_mapreducemappingstointtask.rights; concurrenthashmapv8_mapreducemappingstointtask1 != null; concurrenthashmapv8_mapreducemappingstointtask1 = concurrenthashmapv8_mapreducemappingstointtask.rights = concurrenthashmapv8_mapreducemappingstointtask1.nextRight) {
                            concurrenthashmapv8_mapreducemappingstointtask.result = concurrenthashmapv8_intbyinttoint.apply(concurrenthashmapv8_mapreducemappingstointtask.result, concurrenthashmapv8_mapreducemappingstointtask1.result);
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceEntriesToIntTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Integer> {

        final ConcurrentHashMapV8.ObjectToInt<Entry<K, V>> transformer;
        final ConcurrentHashMapV8.IntByIntToInt reducer;
        final int basis;
        int result;
        ConcurrentHashMapV8.MapReduceEntriesToIntTask<K, V> rights;
        ConcurrentHashMapV8.MapReduceEntriesToIntTask<K, V> nextRight;

        MapReduceEntriesToIntTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceEntriesToIntTask<K, V> concurrenthashmapv8_mapreduceentriestointtask, ConcurrentHashMapV8.ObjectToInt<Entry<K, V>> concurrenthashmapv8_objecttoint, int l, ConcurrentHashMapV8.IntByIntToInt concurrenthashmapv8_intbyinttoint) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreduceentriestointtask;
            this.transformer = concurrenthashmapv8_objecttoint;
            this.basis = l;
            this.reducer = concurrenthashmapv8_intbyinttoint;
        }

        @Override
        public final Integer getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.ObjectToInt concurrenthashmapv8_objecttoint = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.IntByIntToInt concurrenthashmapv8_intbyinttoint = this.reducer;

                if (this.reducer != null) {
                    int i = this.basis;
                    int j = this.baseIndex;

                    while (this.batch > 0) {
                        int k = this.baseLimit;
                        int l;

                        if ((l = this.baseLimit + j >>> 1) <= j) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceEntriesToIntTask<>(this, this.batch >>>= 1, this.baseLimit = l, k, this.tab, this.rights, concurrenthashmapv8_objecttoint, i, concurrenthashmapv8_intbyinttoint)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        i = concurrenthashmapv8_intbyinttoint.apply(i, concurrenthashmapv8_objecttoint.apply(concurrenthashmapv8_node));
                    }

                    this.result = i;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceEntriesToIntTask concurrenthashmapv8_mapreduceentriestointtask = (ConcurrentHashMapV8.MapReduceEntriesToIntTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceEntriesToIntTask concurrenthashmapv8_mapreduceentriestointtask1 = concurrenthashmapv8_mapreduceentriestointtask.rights; concurrenthashmapv8_mapreduceentriestointtask1 != null; concurrenthashmapv8_mapreduceentriestointtask1 = concurrenthashmapv8_mapreduceentriestointtask.rights = concurrenthashmapv8_mapreduceentriestointtask1.nextRight) {
                            concurrenthashmapv8_mapreduceentriestointtask.result = concurrenthashmapv8_intbyinttoint.apply(concurrenthashmapv8_mapreduceentriestointtask.result, concurrenthashmapv8_mapreduceentriestointtask1.result);
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceValuesToIntTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Integer> {

        final ConcurrentHashMapV8.ObjectToInt<? super V> transformer;
        final ConcurrentHashMapV8.IntByIntToInt reducer;
        final int basis;
        int result;
        ConcurrentHashMapV8.MapReduceValuesToIntTask<K, V> rights;
        ConcurrentHashMapV8.MapReduceValuesToIntTask<K, V> nextRight;

        MapReduceValuesToIntTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceValuesToIntTask<K, V> concurrenthashmapv8_mapreducevaluestointtask, ConcurrentHashMapV8.ObjectToInt<? super V> concurrenthashmapv8_objecttoint, int l, ConcurrentHashMapV8.IntByIntToInt concurrenthashmapv8_intbyinttoint) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreducevaluestointtask;
            this.transformer = concurrenthashmapv8_objecttoint;
            this.basis = l;
            this.reducer = concurrenthashmapv8_intbyinttoint;
        }

        @Override
        public final Integer getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.ObjectToInt concurrenthashmapv8_objecttoint = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.IntByIntToInt concurrenthashmapv8_intbyinttoint = this.reducer;

                if (this.reducer != null) {
                    int i = this.basis;
                    int j = this.baseIndex;

                    while (this.batch > 0) {
                        int k = this.baseLimit;
                        int l;

                        if ((l = this.baseLimit + j >>> 1) <= j) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceValuesToIntTask<>(this, this.batch >>>= 1, this.baseLimit = l, k, this.tab, this.rights, concurrenthashmapv8_objecttoint, i, concurrenthashmapv8_intbyinttoint)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        i = concurrenthashmapv8_intbyinttoint.apply(i, concurrenthashmapv8_objecttoint.apply(concurrenthashmapv8_node.val));
                    }

                    this.result = i;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceValuesToIntTask concurrenthashmapv8_mapreducevaluestointtask = (ConcurrentHashMapV8.MapReduceValuesToIntTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceValuesToIntTask concurrenthashmapv8_mapreducevaluestointtask1 = concurrenthashmapv8_mapreducevaluestointtask.rights; concurrenthashmapv8_mapreducevaluestointtask1 != null; concurrenthashmapv8_mapreducevaluestointtask1 = concurrenthashmapv8_mapreducevaluestointtask.rights = concurrenthashmapv8_mapreducevaluestointtask1.nextRight) {
                            concurrenthashmapv8_mapreducevaluestointtask.result = concurrenthashmapv8_intbyinttoint.apply(concurrenthashmapv8_mapreducevaluestointtask.result, concurrenthashmapv8_mapreducevaluestointtask1.result);
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceKeysToIntTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Integer> {

        final ConcurrentHashMapV8.ObjectToInt<? super K> transformer;
        final ConcurrentHashMapV8.IntByIntToInt reducer;
        final int basis;
        int result;
        ConcurrentHashMapV8.MapReduceKeysToIntTask<K, V> rights;
        ConcurrentHashMapV8.MapReduceKeysToIntTask<K, V> nextRight;

        MapReduceKeysToIntTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceKeysToIntTask<K, V> concurrenthashmapv8_mapreducekeystointtask, ConcurrentHashMapV8.ObjectToInt<? super K> concurrenthashmapv8_objecttoint, int l, ConcurrentHashMapV8.IntByIntToInt concurrenthashmapv8_intbyinttoint) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreducekeystointtask;
            this.transformer = concurrenthashmapv8_objecttoint;
            this.basis = l;
            this.reducer = concurrenthashmapv8_intbyinttoint;
        }

        @Override
        public final Integer getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.ObjectToInt concurrenthashmapv8_objecttoint = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.IntByIntToInt concurrenthashmapv8_intbyinttoint = this.reducer;

                if (this.reducer != null) {
                    int i = this.basis;
                    int j = this.baseIndex;

                    while (this.batch > 0) {
                        int k = this.baseLimit;
                        int l;

                        if ((l = this.baseLimit + j >>> 1) <= j) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceKeysToIntTask<>(this, this.batch >>>= 1, this.baseLimit = l, k, this.tab, this.rights, concurrenthashmapv8_objecttoint, i, concurrenthashmapv8_intbyinttoint)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        i = concurrenthashmapv8_intbyinttoint.apply(i, concurrenthashmapv8_objecttoint.apply(concurrenthashmapv8_node.key));
                    }

                    this.result = i;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceKeysToIntTask concurrenthashmapv8_mapreducekeystointtask = (ConcurrentHashMapV8.MapReduceKeysToIntTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceKeysToIntTask concurrenthashmapv8_mapreducekeystointtask1 = concurrenthashmapv8_mapreducekeystointtask.rights; concurrenthashmapv8_mapreducekeystointtask1 != null; concurrenthashmapv8_mapreducekeystointtask1 = concurrenthashmapv8_mapreducekeystointtask.rights = concurrenthashmapv8_mapreducekeystointtask1.nextRight) {
                            concurrenthashmapv8_mapreducekeystointtask.result = concurrenthashmapv8_intbyinttoint.apply(concurrenthashmapv8_mapreducekeystointtask.result, concurrenthashmapv8_mapreducekeystointtask1.result);
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceMappingsToLongTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Long> {

        final ConcurrentHashMapV8.ObjectByObjectToLong<? super K, ? super V> transformer;
        final ConcurrentHashMapV8.LongByLongToLong reducer;
        final long basis;
        long result;
        ConcurrentHashMapV8.MapReduceMappingsToLongTask<K, V> rights;
        ConcurrentHashMapV8.MapReduceMappingsToLongTask<K, V> nextRight;

        MapReduceMappingsToLongTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceMappingsToLongTask<K, V> concurrenthashmapv8_mapreducemappingstolongtask, ConcurrentHashMapV8.ObjectByObjectToLong<? super K, ? super V> concurrenthashmapv8_objectbyobjecttolong, long l, ConcurrentHashMapV8.LongByLongToLong concurrenthashmapv8_longbylongtolong) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreducemappingstolongtask;
            this.transformer = concurrenthashmapv8_objectbyobjecttolong;
            this.basis = l;
            this.reducer = concurrenthashmapv8_longbylongtolong;
        }

        @Override
        public final Long getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.ObjectByObjectToLong concurrenthashmapv8_objectbyobjecttolong = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.LongByLongToLong concurrenthashmapv8_longbylongtolong = this.reducer;

                if (this.reducer != null) {
                    long i = this.basis;
                    int j = this.baseIndex;

                    while (this.batch > 0) {
                        int k = this.baseLimit;
                        int l;

                        if ((l = this.baseLimit + j >>> 1) <= j) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceMappingsToLongTask<>(this, this.batch >>>= 1, this.baseLimit = l, k, this.tab, this.rights, concurrenthashmapv8_objectbyobjecttolong, i, concurrenthashmapv8_longbylongtolong)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        i = concurrenthashmapv8_longbylongtolong.apply(i, concurrenthashmapv8_objectbyobjecttolong.apply(concurrenthashmapv8_node.key, concurrenthashmapv8_node.val));
                    }

                    this.result = i;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceMappingsToLongTask concurrenthashmapv8_mapreducemappingstolongtask = (ConcurrentHashMapV8.MapReduceMappingsToLongTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceMappingsToLongTask concurrenthashmapv8_mapreducemappingstolongtask1 = concurrenthashmapv8_mapreducemappingstolongtask.rights; concurrenthashmapv8_mapreducemappingstolongtask1 != null; concurrenthashmapv8_mapreducemappingstolongtask1 = concurrenthashmapv8_mapreducemappingstolongtask.rights = concurrenthashmapv8_mapreducemappingstolongtask1.nextRight) {
                            concurrenthashmapv8_mapreducemappingstolongtask.result = concurrenthashmapv8_longbylongtolong.apply(concurrenthashmapv8_mapreducemappingstolongtask.result, concurrenthashmapv8_mapreducemappingstolongtask1.result);
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceEntriesToLongTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Long> {

        final ConcurrentHashMapV8.ObjectToLong<Entry<K, V>> transformer;
        final ConcurrentHashMapV8.LongByLongToLong reducer;
        final long basis;
        long result;
        ConcurrentHashMapV8.MapReduceEntriesToLongTask<K, V> rights;
        ConcurrentHashMapV8.MapReduceEntriesToLongTask<K, V> nextRight;

        MapReduceEntriesToLongTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceEntriesToLongTask<K, V> concurrenthashmapv8_mapreduceentriestolongtask, ConcurrentHashMapV8.ObjectToLong<Entry<K, V>> concurrenthashmapv8_objecttolong, long l, ConcurrentHashMapV8.LongByLongToLong concurrenthashmapv8_longbylongtolong) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreduceentriestolongtask;
            this.transformer = concurrenthashmapv8_objecttolong;
            this.basis = l;
            this.reducer = concurrenthashmapv8_longbylongtolong;
        }

        @Override
        public final Long getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.ObjectToLong concurrenthashmapv8_objecttolong = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.LongByLongToLong concurrenthashmapv8_longbylongtolong = this.reducer;

                if (this.reducer != null) {
                    long i = this.basis;
                    int j = this.baseIndex;

                    while (this.batch > 0) {
                        int k = this.baseLimit;
                        int l;

                        if ((l = this.baseLimit + j >>> 1) <= j) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceEntriesToLongTask<>(this, this.batch >>>= 1, this.baseLimit = l, k, this.tab, this.rights, concurrenthashmapv8_objecttolong, i, concurrenthashmapv8_longbylongtolong)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        i = concurrenthashmapv8_longbylongtolong.apply(i, concurrenthashmapv8_objecttolong.apply(concurrenthashmapv8_node));
                    }

                    this.result = i;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceEntriesToLongTask concurrenthashmapv8_mapreduceentriestolongtask = (ConcurrentHashMapV8.MapReduceEntriesToLongTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceEntriesToLongTask concurrenthashmapv8_mapreduceentriestolongtask1 = concurrenthashmapv8_mapreduceentriestolongtask.rights; concurrenthashmapv8_mapreduceentriestolongtask1 != null; concurrenthashmapv8_mapreduceentriestolongtask1 = concurrenthashmapv8_mapreduceentriestolongtask.rights = concurrenthashmapv8_mapreduceentriestolongtask1.nextRight) {
                            concurrenthashmapv8_mapreduceentriestolongtask.result = concurrenthashmapv8_longbylongtolong.apply(concurrenthashmapv8_mapreduceentriestolongtask.result, concurrenthashmapv8_mapreduceentriestolongtask1.result);
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceValuesToLongTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Long> {

        final ConcurrentHashMapV8.ObjectToLong<? super V> transformer;
        final ConcurrentHashMapV8.LongByLongToLong reducer;
        final long basis;
        long result;
        ConcurrentHashMapV8.MapReduceValuesToLongTask<K, V> rights;
        ConcurrentHashMapV8.MapReduceValuesToLongTask<K, V> nextRight;

        MapReduceValuesToLongTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceValuesToLongTask<K, V> concurrenthashmapv8_mapreducevaluestolongtask, ConcurrentHashMapV8.ObjectToLong<? super V> concurrenthashmapv8_objecttolong, long l, ConcurrentHashMapV8.LongByLongToLong concurrenthashmapv8_longbylongtolong) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreducevaluestolongtask;
            this.transformer = concurrenthashmapv8_objecttolong;
            this.basis = l;
            this.reducer = concurrenthashmapv8_longbylongtolong;
        }

        @Override
        public final Long getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.ObjectToLong concurrenthashmapv8_objecttolong = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.LongByLongToLong concurrenthashmapv8_longbylongtolong = this.reducer;

                if (this.reducer != null) {
                    long i = this.basis;
                    int j = this.baseIndex;

                    while (this.batch > 0) {
                        int k = this.baseLimit;
                        int l;

                        if ((l = this.baseLimit + j >>> 1) <= j) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceValuesToLongTask<>(this, this.batch >>>= 1, this.baseLimit = l, k, this.tab, this.rights, concurrenthashmapv8_objecttolong, i, concurrenthashmapv8_longbylongtolong)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        i = concurrenthashmapv8_longbylongtolong.apply(i, concurrenthashmapv8_objecttolong.apply(concurrenthashmapv8_node.val));
                    }

                    this.result = i;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceValuesToLongTask concurrenthashmapv8_mapreducevaluestolongtask = (ConcurrentHashMapV8.MapReduceValuesToLongTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceValuesToLongTask concurrenthashmapv8_mapreducevaluestolongtask1 = concurrenthashmapv8_mapreducevaluestolongtask.rights; concurrenthashmapv8_mapreducevaluestolongtask1 != null; concurrenthashmapv8_mapreducevaluestolongtask1 = concurrenthashmapv8_mapreducevaluestolongtask.rights = concurrenthashmapv8_mapreducevaluestolongtask1.nextRight) {
                            concurrenthashmapv8_mapreducevaluestolongtask.result = concurrenthashmapv8_longbylongtolong.apply(concurrenthashmapv8_mapreducevaluestolongtask.result, concurrenthashmapv8_mapreducevaluestolongtask1.result);
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceKeysToLongTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Long> {

        final ConcurrentHashMapV8.ObjectToLong<? super K> transformer;
        final ConcurrentHashMapV8.LongByLongToLong reducer;
        final long basis;
        long result;
        ConcurrentHashMapV8.MapReduceKeysToLongTask<K, V> rights;
        ConcurrentHashMapV8.MapReduceKeysToLongTask<K, V> nextRight;

        MapReduceKeysToLongTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceKeysToLongTask<K, V> concurrenthashmapv8_mapreducekeystolongtask, ConcurrentHashMapV8.ObjectToLong<? super K> concurrenthashmapv8_objecttolong, long l, ConcurrentHashMapV8.LongByLongToLong concurrenthashmapv8_longbylongtolong) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreducekeystolongtask;
            this.transformer = concurrenthashmapv8_objecttolong;
            this.basis = l;
            this.reducer = concurrenthashmapv8_longbylongtolong;
        }

        @Override
        public final Long getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.ObjectToLong concurrenthashmapv8_objecttolong = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.LongByLongToLong concurrenthashmapv8_longbylongtolong = this.reducer;

                if (this.reducer != null) {
                    long i = this.basis;
                    int j = this.baseIndex;

                    while (this.batch > 0) {
                        int k = this.baseLimit;
                        int l;

                        if ((l = this.baseLimit + j >>> 1) <= j) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceKeysToLongTask<>(this, this.batch >>>= 1, this.baseLimit = l, k, this.tab, this.rights, concurrenthashmapv8_objecttolong, i, concurrenthashmapv8_longbylongtolong)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        i = concurrenthashmapv8_longbylongtolong.apply(i, concurrenthashmapv8_objecttolong.apply(concurrenthashmapv8_node.key));
                    }

                    this.result = i;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceKeysToLongTask concurrenthashmapv8_mapreducekeystolongtask = (ConcurrentHashMapV8.MapReduceKeysToLongTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceKeysToLongTask concurrenthashmapv8_mapreducekeystolongtask1 = concurrenthashmapv8_mapreducekeystolongtask.rights; concurrenthashmapv8_mapreducekeystolongtask1 != null; concurrenthashmapv8_mapreducekeystolongtask1 = concurrenthashmapv8_mapreducekeystolongtask.rights = concurrenthashmapv8_mapreducekeystolongtask1.nextRight) {
                            concurrenthashmapv8_mapreducekeystolongtask.result = concurrenthashmapv8_longbylongtolong.apply(concurrenthashmapv8_mapreducekeystolongtask.result, concurrenthashmapv8_mapreducekeystolongtask1.result);
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceMappingsToDoubleTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Double> {

        final ConcurrentHashMapV8.ObjectByObjectToDouble<? super K, ? super V> transformer;
        final ConcurrentHashMapV8.DoubleByDoubleToDouble reducer;
        final double basis;
        double result;
        ConcurrentHashMapV8.MapReduceMappingsToDoubleTask<K, V> rights;
        ConcurrentHashMapV8.MapReduceMappingsToDoubleTask<K, V> nextRight;

        MapReduceMappingsToDoubleTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceMappingsToDoubleTask<K, V> concurrenthashmapv8_mapreducemappingstodoubletask, ConcurrentHashMapV8.ObjectByObjectToDouble<? super K, ? super V> concurrenthashmapv8_objectbyobjecttodouble, double d0, ConcurrentHashMapV8.DoubleByDoubleToDouble concurrenthashmapv8_doublebydoubletodouble) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreducemappingstodoubletask;
            this.transformer = concurrenthashmapv8_objectbyobjecttodouble;
            this.basis = d0;
            this.reducer = concurrenthashmapv8_doublebydoubletodouble;
        }

        @Override
        public final Double getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.ObjectByObjectToDouble concurrenthashmapv8_objectbyobjecttodouble = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.DoubleByDoubleToDouble concurrenthashmapv8_doublebydoubletodouble = this.reducer;

                if (this.reducer != null) {
                    double d0 = this.basis;
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceMappingsToDoubleTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, concurrenthashmapv8_objectbyobjecttodouble, d0, concurrenthashmapv8_doublebydoubletodouble)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        d0 = concurrenthashmapv8_doublebydoubletodouble.apply(d0, concurrenthashmapv8_objectbyobjecttodouble.apply(concurrenthashmapv8_node.key, concurrenthashmapv8_node.val));
                    }

                    this.result = d0;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceMappingsToDoubleTask concurrenthashmapv8_mapreducemappingstodoubletask = (ConcurrentHashMapV8.MapReduceMappingsToDoubleTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceMappingsToDoubleTask concurrenthashmapv8_mapreducemappingstodoubletask1 = concurrenthashmapv8_mapreducemappingstodoubletask.rights; concurrenthashmapv8_mapreducemappingstodoubletask1 != null; concurrenthashmapv8_mapreducemappingstodoubletask1 = concurrenthashmapv8_mapreducemappingstodoubletask.rights = concurrenthashmapv8_mapreducemappingstodoubletask1.nextRight) {
                            concurrenthashmapv8_mapreducemappingstodoubletask.result = concurrenthashmapv8_doublebydoubletodouble.apply(concurrenthashmapv8_mapreducemappingstodoubletask.result, concurrenthashmapv8_mapreducemappingstodoubletask1.result);
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceEntriesToDoubleTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Double> {

        final ConcurrentHashMapV8.ObjectToDouble<Entry<K, V>> transformer;
        final ConcurrentHashMapV8.DoubleByDoubleToDouble reducer;
        final double basis;
        double result;
        ConcurrentHashMapV8.MapReduceEntriesToDoubleTask<K, V> rights;
        ConcurrentHashMapV8.MapReduceEntriesToDoubleTask<K, V> nextRight;

        MapReduceEntriesToDoubleTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceEntriesToDoubleTask<K, V> concurrenthashmapv8_mapreduceentriestodoubletask, ConcurrentHashMapV8.ObjectToDouble<Entry<K, V>> concurrenthashmapv8_objecttodouble, double d0, ConcurrentHashMapV8.DoubleByDoubleToDouble concurrenthashmapv8_doublebydoubletodouble) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreduceentriestodoubletask;
            this.transformer = concurrenthashmapv8_objecttodouble;
            this.basis = d0;
            this.reducer = concurrenthashmapv8_doublebydoubletodouble;
        }

        @Override
        public final Double getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.ObjectToDouble concurrenthashmapv8_objecttodouble = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.DoubleByDoubleToDouble concurrenthashmapv8_doublebydoubletodouble = this.reducer;

                if (this.reducer != null) {
                    double d0 = this.basis;
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceEntriesToDoubleTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, concurrenthashmapv8_objecttodouble, d0, concurrenthashmapv8_doublebydoubletodouble)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        d0 = concurrenthashmapv8_doublebydoubletodouble.apply(d0, concurrenthashmapv8_objecttodouble.apply(concurrenthashmapv8_node));
                    }

                    this.result = d0;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceEntriesToDoubleTask concurrenthashmapv8_mapreduceentriestodoubletask = (ConcurrentHashMapV8.MapReduceEntriesToDoubleTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceEntriesToDoubleTask concurrenthashmapv8_mapreduceentriestodoubletask1 = concurrenthashmapv8_mapreduceentriestodoubletask.rights; concurrenthashmapv8_mapreduceentriestodoubletask1 != null; concurrenthashmapv8_mapreduceentriestodoubletask1 = concurrenthashmapv8_mapreduceentriestodoubletask.rights = concurrenthashmapv8_mapreduceentriestodoubletask1.nextRight) {
                            concurrenthashmapv8_mapreduceentriestodoubletask.result = concurrenthashmapv8_doublebydoubletodouble.apply(concurrenthashmapv8_mapreduceentriestodoubletask.result, concurrenthashmapv8_mapreduceentriestodoubletask1.result);
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceValuesToDoubleTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Double> {

        final ConcurrentHashMapV8.ObjectToDouble<? super V> transformer;
        final ConcurrentHashMapV8.DoubleByDoubleToDouble reducer;
        final double basis;
        double result;
        ConcurrentHashMapV8.MapReduceValuesToDoubleTask<K, V> rights;
        ConcurrentHashMapV8.MapReduceValuesToDoubleTask<K, V> nextRight;

        MapReduceValuesToDoubleTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceValuesToDoubleTask<K, V> concurrenthashmapv8_mapreducevaluestodoubletask, ConcurrentHashMapV8.ObjectToDouble<? super V> concurrenthashmapv8_objecttodouble, double d0, ConcurrentHashMapV8.DoubleByDoubleToDouble concurrenthashmapv8_doublebydoubletodouble) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreducevaluestodoubletask;
            this.transformer = concurrenthashmapv8_objecttodouble;
            this.basis = d0;
            this.reducer = concurrenthashmapv8_doublebydoubletodouble;
        }

        @Override
        public final Double getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.ObjectToDouble concurrenthashmapv8_objecttodouble = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.DoubleByDoubleToDouble concurrenthashmapv8_doublebydoubletodouble = this.reducer;

                if (this.reducer != null) {
                    double d0 = this.basis;
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceValuesToDoubleTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, concurrenthashmapv8_objecttodouble, d0, concurrenthashmapv8_doublebydoubletodouble)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        d0 = concurrenthashmapv8_doublebydoubletodouble.apply(d0, concurrenthashmapv8_objecttodouble.apply(concurrenthashmapv8_node.val));
                    }

                    this.result = d0;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceValuesToDoubleTask concurrenthashmapv8_mapreducevaluestodoubletask = (ConcurrentHashMapV8.MapReduceValuesToDoubleTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceValuesToDoubleTask concurrenthashmapv8_mapreducevaluestodoubletask1 = concurrenthashmapv8_mapreducevaluestodoubletask.rights; concurrenthashmapv8_mapreducevaluestodoubletask1 != null; concurrenthashmapv8_mapreducevaluestodoubletask1 = concurrenthashmapv8_mapreducevaluestodoubletask.rights = concurrenthashmapv8_mapreducevaluestodoubletask1.nextRight) {
                            concurrenthashmapv8_mapreducevaluestodoubletask.result = concurrenthashmapv8_doublebydoubletodouble.apply(concurrenthashmapv8_mapreducevaluestodoubletask.result, concurrenthashmapv8_mapreducevaluestodoubletask1.result);
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceKeysToDoubleTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Double> {

        final ConcurrentHashMapV8.ObjectToDouble<? super K> transformer;
        final ConcurrentHashMapV8.DoubleByDoubleToDouble reducer;
        final double basis;
        double result;
        ConcurrentHashMapV8.MapReduceKeysToDoubleTask<K, V> rights;
        ConcurrentHashMapV8.MapReduceKeysToDoubleTask<K, V> nextRight;

        MapReduceKeysToDoubleTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceKeysToDoubleTask<K, V> concurrenthashmapv8_mapreducekeystodoubletask, ConcurrentHashMapV8.ObjectToDouble<? super K> concurrenthashmapv8_objecttodouble, double d0, ConcurrentHashMapV8.DoubleByDoubleToDouble concurrenthashmapv8_doublebydoubletodouble) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreducekeystodoubletask;
            this.transformer = concurrenthashmapv8_objecttodouble;
            this.basis = d0;
            this.reducer = concurrenthashmapv8_doublebydoubletodouble;
        }

        @Override
        public final Double getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.ObjectToDouble concurrenthashmapv8_objecttodouble = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.DoubleByDoubleToDouble concurrenthashmapv8_doublebydoubletodouble = this.reducer;

                if (this.reducer != null) {
                    double d0 = this.basis;
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceKeysToDoubleTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, concurrenthashmapv8_objecttodouble, d0, concurrenthashmapv8_doublebydoubletodouble)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        d0 = concurrenthashmapv8_doublebydoubletodouble.apply(d0, concurrenthashmapv8_objecttodouble.apply(concurrenthashmapv8_node.key));
                    }

                    this.result = d0;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceKeysToDoubleTask concurrenthashmapv8_mapreducekeystodoubletask = (ConcurrentHashMapV8.MapReduceKeysToDoubleTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceKeysToDoubleTask concurrenthashmapv8_mapreducekeystodoubletask1 = concurrenthashmapv8_mapreducekeystodoubletask.rights; concurrenthashmapv8_mapreducekeystodoubletask1 != null; concurrenthashmapv8_mapreducekeystodoubletask1 = concurrenthashmapv8_mapreducekeystodoubletask.rights = concurrenthashmapv8_mapreducekeystodoubletask1.nextRight) {
                            concurrenthashmapv8_mapreducekeystodoubletask.result = concurrenthashmapv8_doublebydoubletodouble.apply(concurrenthashmapv8_mapreducekeystodoubletask.result, concurrenthashmapv8_mapreducekeystodoubletask1.result);
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceMappingsTask<K, V, U> extends ConcurrentHashMapV8.BulkTask<K, V, U> {

        final ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends U> transformer;
        final ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> reducer;
        U result;
        ConcurrentHashMapV8.MapReduceMappingsTask<K, V, U> rights;
        ConcurrentHashMapV8.MapReduceMappingsTask<K, V, U> nextRight;

        MapReduceMappingsTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceMappingsTask<K, V, U> concurrenthashmapv8_mapreducemappingstask, ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends U> concurrenthashmapv8_bifun, ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> concurrenthashmapv8_bifun1) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreducemappingstask;
            this.transformer = concurrenthashmapv8_bifun;
            this.reducer = concurrenthashmapv8_bifun1;
        }

        @Override
        public final U getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.BiFun concurrenthashmapv8_bifun = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.BiFun concurrenthashmapv8_bifun1 = this.reducer;

                if (this.reducer != null) {
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceMappingsTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, concurrenthashmapv8_bifun, concurrenthashmapv8_bifun1)).fork();
                    }

                    Object object = null;

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        Object object1;

                        if ((object1 = concurrenthashmapv8_bifun.apply(concurrenthashmapv8_node.key, concurrenthashmapv8_node.val)) != null) {
                            object = object == null ? object1 : concurrenthashmapv8_bifun1.apply(object, object1);
                        }
                    }

                    this.result = object;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceMappingsTask concurrenthashmapv8_mapreducemappingstask = (ConcurrentHashMapV8.MapReduceMappingsTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceMappingsTask concurrenthashmapv8_mapreducemappingstask1 = concurrenthashmapv8_mapreducemappingstask.rights; concurrenthashmapv8_mapreducemappingstask1 != null; concurrenthashmapv8_mapreducemappingstask1 = concurrenthashmapv8_mapreducemappingstask.rights = concurrenthashmapv8_mapreducemappingstask1.nextRight) {
                            Object object2 = concurrenthashmapv8_mapreducemappingstask1.result;

                            if (concurrenthashmapv8_mapreducemappingstask1.result != null) {
                                Object object3 = concurrenthashmapv8_mapreducemappingstask.result;

                                concurrenthashmapv8_mapreducemappingstask.result = concurrenthashmapv8_mapreducemappingstask.result == null ? object2 : concurrenthashmapv8_bifun1.apply(object3, object2);
                            }
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceEntriesTask<K, V, U> extends ConcurrentHashMapV8.BulkTask<K, V, U> {

        final ConcurrentHashMapV8.Fun<Entry<K, V>, ? extends U> transformer;
        final ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> reducer;
        U result;
        ConcurrentHashMapV8.MapReduceEntriesTask<K, V, U> rights;
        ConcurrentHashMapV8.MapReduceEntriesTask<K, V, U> nextRight;

        MapReduceEntriesTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceEntriesTask<K, V, U> concurrenthashmapv8_mapreduceentriestask, ConcurrentHashMapV8.Fun<Entry<K, V>, ? extends U> concurrenthashmapv8_fun, ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> concurrenthashmapv8_bifun) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreduceentriestask;
            this.transformer = concurrenthashmapv8_fun;
            this.reducer = concurrenthashmapv8_bifun;
        }

        @Override
        public final U getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.Fun concurrenthashmapv8_fun = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.BiFun concurrenthashmapv8_bifun = this.reducer;

                if (this.reducer != null) {
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceEntriesTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, concurrenthashmapv8_fun, concurrenthashmapv8_bifun)).fork();
                    }

                    Object object = null;

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        Object object1;

                        if ((object1 = concurrenthashmapv8_fun.apply(concurrenthashmapv8_node)) != null) {
                            object = object == null ? object1 : concurrenthashmapv8_bifun.apply(object, object1);
                        }
                    }

                    this.result = object;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceEntriesTask concurrenthashmapv8_mapreduceentriestask = (ConcurrentHashMapV8.MapReduceEntriesTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceEntriesTask concurrenthashmapv8_mapreduceentriestask1 = concurrenthashmapv8_mapreduceentriestask.rights; concurrenthashmapv8_mapreduceentriestask1 != null; concurrenthashmapv8_mapreduceentriestask1 = concurrenthashmapv8_mapreduceentriestask.rights = concurrenthashmapv8_mapreduceentriestask1.nextRight) {
                            Object object2 = concurrenthashmapv8_mapreduceentriestask1.result;

                            if (concurrenthashmapv8_mapreduceentriestask1.result != null) {
                                Object object3 = concurrenthashmapv8_mapreduceentriestask.result;

                                concurrenthashmapv8_mapreduceentriestask.result = concurrenthashmapv8_mapreduceentriestask.result == null ? object2 : concurrenthashmapv8_bifun.apply(object3, object2);
                            }
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceValuesTask<K, V, U> extends ConcurrentHashMapV8.BulkTask<K, V, U> {

        final ConcurrentHashMapV8.Fun<? super V, ? extends U> transformer;
        final ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> reducer;
        U result;
        ConcurrentHashMapV8.MapReduceValuesTask<K, V, U> rights;
        ConcurrentHashMapV8.MapReduceValuesTask<K, V, U> nextRight;

        MapReduceValuesTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceValuesTask<K, V, U> concurrenthashmapv8_mapreducevaluestask, ConcurrentHashMapV8.Fun<? super V, ? extends U> concurrenthashmapv8_fun, ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> concurrenthashmapv8_bifun) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreducevaluestask;
            this.transformer = concurrenthashmapv8_fun;
            this.reducer = concurrenthashmapv8_bifun;
        }

        @Override
        public final U getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.Fun concurrenthashmapv8_fun = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.BiFun concurrenthashmapv8_bifun = this.reducer;

                if (this.reducer != null) {
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceValuesTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, concurrenthashmapv8_fun, concurrenthashmapv8_bifun)).fork();
                    }

                    Object object = null;

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        Object object1;

                        if ((object1 = concurrenthashmapv8_fun.apply(concurrenthashmapv8_node.val)) != null) {
                            object = object == null ? object1 : concurrenthashmapv8_bifun.apply(object, object1);
                        }
                    }

                    this.result = object;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceValuesTask concurrenthashmapv8_mapreducevaluestask = (ConcurrentHashMapV8.MapReduceValuesTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceValuesTask concurrenthashmapv8_mapreducevaluestask1 = concurrenthashmapv8_mapreducevaluestask.rights; concurrenthashmapv8_mapreducevaluestask1 != null; concurrenthashmapv8_mapreducevaluestask1 = concurrenthashmapv8_mapreducevaluestask.rights = concurrenthashmapv8_mapreducevaluestask1.nextRight) {
                            Object object2 = concurrenthashmapv8_mapreducevaluestask1.result;

                            if (concurrenthashmapv8_mapreducevaluestask1.result != null) {
                                Object object3 = concurrenthashmapv8_mapreducevaluestask.result;

                                concurrenthashmapv8_mapreducevaluestask.result = concurrenthashmapv8_mapreducevaluestask.result == null ? object2 : concurrenthashmapv8_bifun.apply(object3, object2);
                            }
                        }
                    }
                }
            }

        }
    }

    static final class MapReduceKeysTask<K, V, U> extends ConcurrentHashMapV8.BulkTask<K, V, U> {

        final ConcurrentHashMapV8.Fun<? super K, ? extends U> transformer;
        final ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> reducer;
        U result;
        ConcurrentHashMapV8.MapReduceKeysTask<K, V, U> rights;
        ConcurrentHashMapV8.MapReduceKeysTask<K, V, U> nextRight;

        MapReduceKeysTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.MapReduceKeysTask<K, V, U> concurrenthashmapv8_mapreducekeystask, ConcurrentHashMapV8.Fun<? super K, ? extends U> concurrenthashmapv8_fun, ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> concurrenthashmapv8_bifun) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_mapreducekeystask;
            this.transformer = concurrenthashmapv8_fun;
            this.reducer = concurrenthashmapv8_bifun;
        }

        @Override
        public final U getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.Fun concurrenthashmapv8_fun = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.BiFun concurrenthashmapv8_bifun = this.reducer;

                if (this.reducer != null) {
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (this.rights = new ConcurrentHashMapV8.MapReduceKeysTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, concurrenthashmapv8_fun, concurrenthashmapv8_bifun)).fork();
                    }

                    Object object = null;

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        Object object1;

                        if ((object1 = concurrenthashmapv8_fun.apply(concurrenthashmapv8_node.key)) != null) {
                            object = object == null ? object1 : concurrenthashmapv8_bifun.apply(object, object1);
                        }
                    }

                    this.result = object;

                    for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                        ConcurrentHashMapV8.MapReduceKeysTask concurrenthashmapv8_mapreducekeystask = (ConcurrentHashMapV8.MapReduceKeysTask) countedcompleter;

                        for (ConcurrentHashMapV8.MapReduceKeysTask concurrenthashmapv8_mapreducekeystask1 = concurrenthashmapv8_mapreducekeystask.rights; concurrenthashmapv8_mapreducekeystask1 != null; concurrenthashmapv8_mapreducekeystask1 = concurrenthashmapv8_mapreducekeystask.rights = concurrenthashmapv8_mapreducekeystask1.nextRight) {
                            Object object2 = concurrenthashmapv8_mapreducekeystask1.result;

                            if (concurrenthashmapv8_mapreducekeystask1.result != null) {
                                Object object3 = concurrenthashmapv8_mapreducekeystask.result;

                                concurrenthashmapv8_mapreducekeystask.result = concurrenthashmapv8_mapreducekeystask.result == null ? object2 : concurrenthashmapv8_bifun.apply(object3, object2);
                            }
                        }
                    }
                }
            }

        }
    }

    static final class ReduceEntriesTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Entry<K, V>> {

        final ConcurrentHashMapV8.BiFun<Entry<K, V>, Entry<K, V>, ? extends Entry<K, V>> reducer;
        Entry<K, V> result;
        ConcurrentHashMapV8.ReduceEntriesTask<K, V> rights;
        ConcurrentHashMapV8.ReduceEntriesTask<K, V> nextRight;

        ReduceEntriesTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.ReduceEntriesTask<K, V> concurrenthashmapv8_reduceentriestask, ConcurrentHashMapV8.BiFun<Entry<K, V>, Entry<K, V>, ? extends Entry<K, V>> concurrenthashmapv8_bifun) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_reduceentriestask;
            this.reducer = concurrenthashmapv8_bifun;
        }

        @Override
        public final Entry<K, V> getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.BiFun concurrenthashmapv8_bifun = this.reducer;

            if (this.reducer != null) {
                int i = this.baseIndex;

                while (this.batch > 0) {
                    int j = this.baseLimit;
                    int k;

                    if ((k = this.baseLimit + i >>> 1) <= i) {
                        break;
                    }

                    this.addToPendingCount(1);
                    (this.rights = new ConcurrentHashMapV8.ReduceEntriesTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, concurrenthashmapv8_bifun)).fork();
                }

                Object object;
                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                for (object = null; (concurrenthashmapv8_node = this.advance()) != null; object = object == null ? concurrenthashmapv8_node : (Entry) concurrenthashmapv8_bifun.apply(object, concurrenthashmapv8_node)) {
                    ;
                }

                this.result = (Entry) object;

                for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                    ConcurrentHashMapV8.ReduceEntriesTask concurrenthashmapv8_reduceentriestask = (ConcurrentHashMapV8.ReduceEntriesTask) countedcompleter;

                    for (ConcurrentHashMapV8.ReduceEntriesTask concurrenthashmapv8_reduceentriestask1 = concurrenthashmapv8_reduceentriestask.rights; concurrenthashmapv8_reduceentriestask1 != null; concurrenthashmapv8_reduceentriestask1 = concurrenthashmapv8_reduceentriestask.rights = concurrenthashmapv8_reduceentriestask1.nextRight) {
                        Entry entry = concurrenthashmapv8_reduceentriestask1.result;

                        if (concurrenthashmapv8_reduceentriestask1.result != null) {
                            Entry entry1 = concurrenthashmapv8_reduceentriestask.result;

                            concurrenthashmapv8_reduceentriestask.result = concurrenthashmapv8_reduceentriestask.result == null ? entry : (Entry) concurrenthashmapv8_bifun.apply(entry1, entry);
                        }
                    }
                }
            }

        }
    }

    static final class ReduceValuesTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, V> {

        final ConcurrentHashMapV8.BiFun<? super V, ? super V, ? extends V> reducer;
        V result;
        ConcurrentHashMapV8.ReduceValuesTask<K, V> rights;
        ConcurrentHashMapV8.ReduceValuesTask<K, V> nextRight;

        ReduceValuesTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.ReduceValuesTask<K, V> concurrenthashmapv8_reducevaluestask, ConcurrentHashMapV8.BiFun<? super V, ? super V, ? extends V> concurrenthashmapv8_bifun) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_reducevaluestask;
            this.reducer = concurrenthashmapv8_bifun;
        }

        @Override
        public final V getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.BiFun concurrenthashmapv8_bifun = this.reducer;

            if (this.reducer != null) {
                int i = this.baseIndex;

                while (this.batch > 0) {
                    int j = this.baseLimit;
                    int k;

                    if ((k = this.baseLimit + i >>> 1) <= i) {
                        break;
                    }

                    this.addToPendingCount(1);
                    (this.rights = new ConcurrentHashMapV8.ReduceValuesTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, concurrenthashmapv8_bifun)).fork();
                }

                Object object;
                ConcurrentHashMapV8.Node concurrenthashmapv8_node;
                Object object1;

                for (object1 = null; (concurrenthashmapv8_node = this.advance()) != null; object1 = object1 == null ? object : concurrenthashmapv8_bifun.apply(object1, object)) {
                    object = concurrenthashmapv8_node.val;
                }

                this.result = object1;

                for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                    ConcurrentHashMapV8.ReduceValuesTask concurrenthashmapv8_reducevaluestask = (ConcurrentHashMapV8.ReduceValuesTask) countedcompleter;

                    for (ConcurrentHashMapV8.ReduceValuesTask concurrenthashmapv8_reducevaluestask1 = concurrenthashmapv8_reducevaluestask.rights; concurrenthashmapv8_reducevaluestask1 != null; concurrenthashmapv8_reducevaluestask1 = concurrenthashmapv8_reducevaluestask.rights = concurrenthashmapv8_reducevaluestask1.nextRight) {
                        Object object2 = concurrenthashmapv8_reducevaluestask1.result;

                        if (concurrenthashmapv8_reducevaluestask1.result != null) {
                            Object object3 = concurrenthashmapv8_reducevaluestask.result;

                            concurrenthashmapv8_reducevaluestask.result = concurrenthashmapv8_reducevaluestask.result == null ? object2 : concurrenthashmapv8_bifun.apply(object3, object2);
                        }
                    }
                }
            }

        }
    }

    static final class ReduceKeysTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, K> {

        final ConcurrentHashMapV8.BiFun<? super K, ? super K, ? extends K> reducer;
        K result;
        ConcurrentHashMapV8.ReduceKeysTask<K, V> rights;
        ConcurrentHashMapV8.ReduceKeysTask<K, V> nextRight;

        ReduceKeysTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.ReduceKeysTask<K, V> concurrenthashmapv8_reducekeystask, ConcurrentHashMapV8.BiFun<? super K, ? super K, ? extends K> concurrenthashmapv8_bifun) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.nextRight = concurrenthashmapv8_reducekeystask;
            this.reducer = concurrenthashmapv8_bifun;
        }

        @Override
        public final K getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.BiFun concurrenthashmapv8_bifun = this.reducer;

            if (this.reducer != null) {
                int i = this.baseIndex;

                while (this.batch > 0) {
                    int j = this.baseLimit;
                    int k;

                    if ((k = this.baseLimit + i >>> 1) <= i) {
                        break;
                    }

                    this.addToPendingCount(1);
                    (this.rights = new ConcurrentHashMapV8.ReduceKeysTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, concurrenthashmapv8_bifun)).fork();
                }

                Object object;
                ConcurrentHashMapV8.Node concurrenthashmapv8_node;
                Object object1;

                for (object1 = null; (concurrenthashmapv8_node = this.advance()) != null; object1 = object1 == null ? object : (object == null ? object1 : concurrenthashmapv8_bifun.apply(object1, object))) {
                    object = concurrenthashmapv8_node.key;
                }

                this.result = object1;

                for (CountedCompleter countedcompleter = this.firstComplete(); countedcompleter != null; countedcompleter = countedcompleter.nextComplete()) {
                    ConcurrentHashMapV8.ReduceKeysTask concurrenthashmapv8_reducekeystask = (ConcurrentHashMapV8.ReduceKeysTask) countedcompleter;

                    for (ConcurrentHashMapV8.ReduceKeysTask concurrenthashmapv8_reducekeystask1 = concurrenthashmapv8_reducekeystask.rights; concurrenthashmapv8_reducekeystask1 != null; concurrenthashmapv8_reducekeystask1 = concurrenthashmapv8_reducekeystask.rights = concurrenthashmapv8_reducekeystask1.nextRight) {
                        Object object2 = concurrenthashmapv8_reducekeystask1.result;

                        if (concurrenthashmapv8_reducekeystask1.result != null) {
                            Object object3 = concurrenthashmapv8_reducekeystask.result;

                            concurrenthashmapv8_reducekeystask.result = concurrenthashmapv8_reducekeystask.result == null ? object2 : concurrenthashmapv8_bifun.apply(object3, object2);
                        }
                    }
                }
            }

        }
    }

    static final class SearchMappingsTask<K, V, U> extends ConcurrentHashMapV8.BulkTask<K, V, U> {

        final ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchMappingsTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends U> concurrenthashmapv8_bifun, AtomicReference<U> atomicreference) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.searchFunction = concurrenthashmapv8_bifun;
            this.result = atomicreference;
        }

        @Override
        public final U getRawResult() {
            return this.result.get();
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.BiFun concurrenthashmapv8_bifun = this.searchFunction;

            if (this.searchFunction != null) {
                AtomicReference atomicreference = this.result;

                if (this.result != null) {
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        if (atomicreference.get() != null) {
                            return;
                        }

                        this.addToPendingCount(1);
                        (new ConcurrentHashMapV8.SearchMappingsTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, concurrenthashmapv8_bifun, atomicreference)).fork();
                    }

                    while (atomicreference.get() == null) {
                        ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                        if ((concurrenthashmapv8_node = this.advance()) == null) {
                            this.propagateCompletion();
                            break;
                        }

                        Object object;

                        if ((object = concurrenthashmapv8_bifun.apply(concurrenthashmapv8_node.key, concurrenthashmapv8_node.val)) != null) {
                            if (atomicreference.compareAndSet((Object) null, object)) {
                                this.quietlyCompleteRoot();
                            }
                            break;
                        }
                    }
                }
            }

        }
    }

    static final class SearchEntriesTask<K, V, U> extends ConcurrentHashMapV8.BulkTask<K, V, U> {

        final ConcurrentHashMapV8.Fun<Entry<K, V>, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchEntriesTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.Fun<Entry<K, V>, ? extends U> concurrenthashmapv8_fun, AtomicReference<U> atomicreference) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.searchFunction = concurrenthashmapv8_fun;
            this.result = atomicreference;
        }

        @Override
        public final U getRawResult() {
            return this.result.get();
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.Fun concurrenthashmapv8_fun = this.searchFunction;

            if (this.searchFunction != null) {
                AtomicReference atomicreference = this.result;

                if (this.result != null) {
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        if (atomicreference.get() != null) {
                            return;
                        }

                        this.addToPendingCount(1);
                        (new ConcurrentHashMapV8.SearchEntriesTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, concurrenthashmapv8_fun, atomicreference)).fork();
                    }

                    while (atomicreference.get() == null) {
                        ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                        if ((concurrenthashmapv8_node = this.advance()) == null) {
                            this.propagateCompletion();
                            break;
                        }

                        Object object;

                        if ((object = concurrenthashmapv8_fun.apply(concurrenthashmapv8_node)) != null) {
                            if (atomicreference.compareAndSet((Object) null, object)) {
                                this.quietlyCompleteRoot();
                            }

                            return;
                        }
                    }
                }
            }

        }
    }

    static final class SearchValuesTask<K, V, U> extends ConcurrentHashMapV8.BulkTask<K, V, U> {

        final ConcurrentHashMapV8.Fun<? super V, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchValuesTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.Fun<? super V, ? extends U> concurrenthashmapv8_fun, AtomicReference<U> atomicreference) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.searchFunction = concurrenthashmapv8_fun;
            this.result = atomicreference;
        }

        @Override
        public final U getRawResult() {
            return this.result.get();
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.Fun concurrenthashmapv8_fun = this.searchFunction;

            if (this.searchFunction != null) {
                AtomicReference atomicreference = this.result;

                if (this.result != null) {
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        if (atomicreference.get() != null) {
                            return;
                        }

                        this.addToPendingCount(1);
                        (new ConcurrentHashMapV8.SearchValuesTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, concurrenthashmapv8_fun, atomicreference)).fork();
                    }

                    while (atomicreference.get() == null) {
                        ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                        if ((concurrenthashmapv8_node = this.advance()) == null) {
                            this.propagateCompletion();
                            break;
                        }

                        Object object;

                        if ((object = concurrenthashmapv8_fun.apply(concurrenthashmapv8_node.val)) != null) {
                            if (atomicreference.compareAndSet((Object) null, object)) {
                                this.quietlyCompleteRoot();
                            }
                            break;
                        }
                    }
                }
            }

        }
    }

    static final class SearchKeysTask<K, V, U> extends ConcurrentHashMapV8.BulkTask<K, V, U> {

        final ConcurrentHashMapV8.Fun<? super K, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchKeysTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.Fun<? super K, ? extends U> concurrenthashmapv8_fun, AtomicReference<U> atomicreference) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.searchFunction = concurrenthashmapv8_fun;
            this.result = atomicreference;
        }

        @Override
        public final U getRawResult() {
            return this.result.get();
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.Fun concurrenthashmapv8_fun = this.searchFunction;

            if (this.searchFunction != null) {
                AtomicReference atomicreference = this.result;

                if (this.result != null) {
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        if (atomicreference.get() != null) {
                            return;
                        }

                        this.addToPendingCount(1);
                        (new ConcurrentHashMapV8.SearchKeysTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, concurrenthashmapv8_fun, atomicreference)).fork();
                    }

                    while (atomicreference.get() == null) {
                        ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                        if ((concurrenthashmapv8_node = this.advance()) == null) {
                            this.propagateCompletion();
                            break;
                        }

                        Object object;

                        if ((object = concurrenthashmapv8_fun.apply(concurrenthashmapv8_node.key)) != null) {
                            if (atomicreference.compareAndSet((Object) null, object)) {
                                this.quietlyCompleteRoot();
                            }
                            break;
                        }
                    }
                }
            }

        }
    }

    static final class ForEachTransformedMappingTask<K, V, U> extends ConcurrentHashMapV8.BulkTask<K, V, Void> {

        final ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends U> transformer;
        final ConcurrentHashMapV8.Action<? super U> action;

        ForEachTransformedMappingTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends U> concurrenthashmapv8_bifun, ConcurrentHashMapV8.Action<? super U> concurrenthashmapv8_action) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.transformer = concurrenthashmapv8_bifun;
            this.action = concurrenthashmapv8_action;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.BiFun concurrenthashmapv8_bifun = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.Action concurrenthashmapv8_action = this.action;

                if (this.action != null) {
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (new ConcurrentHashMapV8.ForEachTransformedMappingTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, concurrenthashmapv8_bifun, concurrenthashmapv8_action)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        Object object;

                        if ((object = concurrenthashmapv8_bifun.apply(concurrenthashmapv8_node.key, concurrenthashmapv8_node.val)) != null) {
                            concurrenthashmapv8_action.apply(object);
                        }
                    }

                    this.propagateCompletion();
                }
            }

        }
    }

    static final class ForEachTransformedEntryTask<K, V, U> extends ConcurrentHashMapV8.BulkTask<K, V, Void> {

        final ConcurrentHashMapV8.Fun<Entry<K, V>, ? extends U> transformer;
        final ConcurrentHashMapV8.Action<? super U> action;

        ForEachTransformedEntryTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.Fun<Entry<K, V>, ? extends U> concurrenthashmapv8_fun, ConcurrentHashMapV8.Action<? super U> concurrenthashmapv8_action) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.transformer = concurrenthashmapv8_fun;
            this.action = concurrenthashmapv8_action;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.Fun concurrenthashmapv8_fun = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.Action concurrenthashmapv8_action = this.action;

                if (this.action != null) {
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (new ConcurrentHashMapV8.ForEachTransformedEntryTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, concurrenthashmapv8_fun, concurrenthashmapv8_action)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        Object object;

                        if ((object = concurrenthashmapv8_fun.apply(concurrenthashmapv8_node)) != null) {
                            concurrenthashmapv8_action.apply(object);
                        }
                    }

                    this.propagateCompletion();
                }
            }

        }
    }

    static final class ForEachTransformedValueTask<K, V, U> extends ConcurrentHashMapV8.BulkTask<K, V, Void> {

        final ConcurrentHashMapV8.Fun<? super V, ? extends U> transformer;
        final ConcurrentHashMapV8.Action<? super U> action;

        ForEachTransformedValueTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.Fun<? super V, ? extends U> concurrenthashmapv8_fun, ConcurrentHashMapV8.Action<? super U> concurrenthashmapv8_action) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.transformer = concurrenthashmapv8_fun;
            this.action = concurrenthashmapv8_action;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.Fun concurrenthashmapv8_fun = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.Action concurrenthashmapv8_action = this.action;

                if (this.action != null) {
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (new ConcurrentHashMapV8.ForEachTransformedValueTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, concurrenthashmapv8_fun, concurrenthashmapv8_action)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        Object object;

                        if ((object = concurrenthashmapv8_fun.apply(concurrenthashmapv8_node.val)) != null) {
                            concurrenthashmapv8_action.apply(object);
                        }
                    }

                    this.propagateCompletion();
                }
            }

        }
    }

    static final class ForEachTransformedKeyTask<K, V, U> extends ConcurrentHashMapV8.BulkTask<K, V, Void> {

        final ConcurrentHashMapV8.Fun<? super K, ? extends U> transformer;
        final ConcurrentHashMapV8.Action<? super U> action;

        ForEachTransformedKeyTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.Fun<? super K, ? extends U> concurrenthashmapv8_fun, ConcurrentHashMapV8.Action<? super U> concurrenthashmapv8_action) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.transformer = concurrenthashmapv8_fun;
            this.action = concurrenthashmapv8_action;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.Fun concurrenthashmapv8_fun = this.transformer;

            if (this.transformer != null) {
                ConcurrentHashMapV8.Action concurrenthashmapv8_action = this.action;

                if (this.action != null) {
                    int i = this.baseIndex;

                    while (this.batch > 0) {
                        int j = this.baseLimit;
                        int k;

                        if ((k = this.baseLimit + i >>> 1) <= i) {
                            break;
                        }

                        this.addToPendingCount(1);
                        (new ConcurrentHashMapV8.ForEachTransformedKeyTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, concurrenthashmapv8_fun, concurrenthashmapv8_action)).fork();
                    }

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = this.advance()) != null) {
                        Object object;

                        if ((object = concurrenthashmapv8_fun.apply(concurrenthashmapv8_node.key)) != null) {
                            concurrenthashmapv8_action.apply(object);
                        }
                    }

                    this.propagateCompletion();
                }
            }

        }
    }

    static final class ForEachMappingTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Void> {

        final ConcurrentHashMapV8.BiAction<? super K, ? super V> action;

        ForEachMappingTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.BiAction<? super K, ? super V> concurrenthashmapv8_biaction) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.action = concurrenthashmapv8_biaction;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.BiAction concurrenthashmapv8_biaction = this.action;

            if (this.action != null) {
                int i = this.baseIndex;

                while (this.batch > 0) {
                    int j = this.baseLimit;
                    int k;

                    if ((k = this.baseLimit + i >>> 1) <= i) {
                        break;
                    }

                    this.addToPendingCount(1);
                    (new ConcurrentHashMapV8.ForEachMappingTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, concurrenthashmapv8_biaction)).fork();
                }

                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                while ((concurrenthashmapv8_node = this.advance()) != null) {
                    concurrenthashmapv8_biaction.apply(concurrenthashmapv8_node.key, concurrenthashmapv8_node.val);
                }

                this.propagateCompletion();
            }

        }
    }

    static final class ForEachEntryTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Void> {

        final ConcurrentHashMapV8.Action<? super Entry<K, V>> action;

        ForEachEntryTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.Action<? super Entry<K, V>> concurrenthashmapv8_action) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.action = concurrenthashmapv8_action;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.Action concurrenthashmapv8_action = this.action;

            if (this.action != null) {
                int i = this.baseIndex;

                while (this.batch > 0) {
                    int j = this.baseLimit;
                    int k;

                    if ((k = this.baseLimit + i >>> 1) <= i) {
                        break;
                    }

                    this.addToPendingCount(1);
                    (new ConcurrentHashMapV8.ForEachEntryTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, concurrenthashmapv8_action)).fork();
                }

                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                while ((concurrenthashmapv8_node = this.advance()) != null) {
                    concurrenthashmapv8_action.apply(concurrenthashmapv8_node);
                }

                this.propagateCompletion();
            }

        }
    }

    static final class ForEachValueTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Void> {

        final ConcurrentHashMapV8.Action<? super V> action;

        ForEachValueTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.Action<? super V> concurrenthashmapv8_action) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.action = concurrenthashmapv8_action;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.Action concurrenthashmapv8_action = this.action;

            if (this.action != null) {
                int i = this.baseIndex;

                while (this.batch > 0) {
                    int j = this.baseLimit;
                    int k;

                    if ((k = this.baseLimit + i >>> 1) <= i) {
                        break;
                    }

                    this.addToPendingCount(1);
                    (new ConcurrentHashMapV8.ForEachValueTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, concurrenthashmapv8_action)).fork();
                }

                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                while ((concurrenthashmapv8_node = this.advance()) != null) {
                    concurrenthashmapv8_action.apply(concurrenthashmapv8_node.val);
                }

                this.propagateCompletion();
            }

        }
    }

    static final class ForEachKeyTask<K, V> extends ConcurrentHashMapV8.BulkTask<K, V, Void> {

        final ConcurrentHashMapV8.Action<? super K> action;

        ForEachKeyTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, ConcurrentHashMapV8.Action<? super K> concurrenthashmapv8_action) {
            super(concurrenthashmapv8_bulktask, i, j, k, aconcurrenthashmapv8_node);
            this.action = concurrenthashmapv8_action;
        }

        @Override
        public final void compute() {
            ConcurrentHashMapV8.Action concurrenthashmapv8_action = this.action;

            if (this.action != null) {
                int i = this.baseIndex;

                while (this.batch > 0) {
                    int j = this.baseLimit;
                    int k;

                    if ((k = this.baseLimit + i >>> 1) <= i) {
                        break;
                    }

                    this.addToPendingCount(1);
                    (new ConcurrentHashMapV8.ForEachKeyTask<>(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, concurrenthashmapv8_action)).fork();
                }

                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                while ((concurrenthashmapv8_node = this.advance()) != null) {
                    concurrenthashmapv8_action.apply(concurrenthashmapv8_node.key);
                }

                this.propagateCompletion();
            }

        }
    }

    abstract static class BulkTask<K, V, R> extends CountedCompleter<R> {

        ConcurrentHashMapV8.Node<K, V>[] tab;
        ConcurrentHashMapV8.Node<K, V> next;
        int index;
        int baseIndex;
        int baseLimit;
        final int baseSize;
        int batch;

        BulkTask(ConcurrentHashMapV8.BulkTask<K, V, ?> concurrenthashmapv8_bulktask, int i, int j, int k, ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node) {
            super(concurrenthashmapv8_bulktask);
            this.batch = i;
            this.index = this.baseIndex = j;
            if ((this.tab = aconcurrenthashmapv8_node) == null) {
                this.baseSize = this.baseLimit = 0;
            } else if (concurrenthashmapv8_bulktask == null) {
                this.baseSize = this.baseLimit = aconcurrenthashmapv8_node.length;
            } else {
                this.baseLimit = k;
                this.baseSize = concurrenthashmapv8_bulktask.baseSize;
            }

        }

        final ConcurrentHashMapV8.Node<K, V> advance() {
            Object object = this.next;

            if (this.next != null) {
                object = ((ConcurrentHashMapV8.Node) object).next;
            }

            while (true) {
                if (object != null) {
                    return this.next = (ConcurrentHashMapV8.Node) object;
                }

                if (this.baseIndex >= this.baseLimit) {
                    break;
                }

                ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.tab;

                if (this.tab == null) {
                    break;
                }

                int i;
                int j = i = aconcurrenthashmapv8_node.length;
                int k = this.index;

                if (j <= this.index || k < 0) {
                    break;
                }

                if ((object = ConcurrentHashMapV8.tabAt(aconcurrenthashmapv8_node, this.index)) != null && ((ConcurrentHashMapV8.Node) object).hash < 0) {
                    if (object instanceof ConcurrentHashMapV8.ForwardingNode) {
                        this.tab = ((ConcurrentHashMapV8.ForwardingNode) object).nextTable;
                        object = null;
                        continue;
                    }

                    if (object instanceof ConcurrentHashMapV8.TreeBin) {
                        object = ((ConcurrentHashMapV8.TreeBin) object).first;
                    } else {
                        object = null;
                    }
                }

                if ((this.index += this.baseSize) >= i) {
                    this.index = ++this.baseIndex;
                }
            }

            return this.next = null;
        }
    }

    static final class EntrySetView<K, V> extends ConcurrentHashMapV8.CollectionView<K, V, Entry<K, V>> implements Set<Entry<K, V>>, Serializable {

        private static final long serialVersionUID = 2249069246763182397L;

        EntrySetView(ConcurrentHashMapV8<K, V> concurrenthashmapv8) {
            super(concurrenthashmapv8);
        }

        @Override
        public boolean contains(Object object) {
            Object object1;
            Object object2;
            Entry entry;
            Object object3;

            return object instanceof Entry && (object3 = (entry = (Entry) object).getKey()) != null && (object2 = this.map.get(object3)) != null && (object1 = entry.getValue()) != null && (object1 == object2 || object1.equals(object2));
        }

        @Override
        public boolean remove(Object object) {
            Object object1;
            Object object2;
            Entry entry;

            return object instanceof Entry && (object1 = (entry = (Entry) object).getKey()) != null && (object2 = entry.getValue()) != null && this.map.remove(object1, object2);
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            ConcurrentHashMapV8 concurrenthashmapv8 = this.map;
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = concurrenthashmapv8.table;
            int i = concurrenthashmapv8.table == null ? 0 : aconcurrenthashmapv8_node.length;

            return new ConcurrentHashMapV8.EntryIterator<>(aconcurrenthashmapv8_node, i, 0, i, concurrenthashmapv8);
        }

        public boolean add(Entry<K, V> entry) {
            return this.map.putVal(entry.getKey(), entry.getValue(), false) == null;
        }

        public boolean addAll(Collection<? extends Entry<K, V>> collection) {
            boolean flag = false;
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                if (this.add(entry)) {
                    flag = true;
                }
            }

            return flag;
        }

        public final int hashCode() {
            int i = 0;
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.map.table;
            ConcurrentHashMapV8.Node concurrenthashmapv8_node;

            if (this.map.table != null) {
                for (ConcurrentHashMapV8.Traverser concurrenthashmapv8_traverser = new ConcurrentHashMapV8.Traverser<>(aconcurrenthashmapv8_node, aconcurrenthashmapv8_node.length, 0, aconcurrenthashmapv8_node.length); (concurrenthashmapv8_node = concurrenthashmapv8_traverser.advance()) != null; i += concurrenthashmapv8_node.hashCode()) {
                    ;
                }
            }

            return i;
        }

        public final boolean equals(Object object) {
            Set set;

            return object instanceof Set && ((set = (Set) object) == this || this.containsAll(set) && set.containsAll(this));
        }

        public ConcurrentHashMapV8.ConcurrentHashMapSpliterator<Entry<K, V>> spliterator() {
            ConcurrentHashMapV8 concurrenthashmapv8 = this.map;
            long i = concurrenthashmapv8.sumCount();
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = concurrenthashmapv8.table;
            int j = concurrenthashmapv8.table == null ? 0 : aconcurrenthashmapv8_node.length;

            return new ConcurrentHashMapV8.EntrySpliterator<>(aconcurrenthashmapv8_node, j, 0, j, i < 0L ? 0L : i, concurrenthashmapv8);
        }

        public void forEach(ConcurrentHashMapV8.Action<? super Entry<K, V>> concurrenthashmapv8_action) {
            if (concurrenthashmapv8_action == null) {
                throw new NullPointerException();
            } else {
                ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.map.table;

                if (this.map.table != null) {
                    ConcurrentHashMapV8.Traverser concurrenthashmapv8_traverser = new ConcurrentHashMapV8.Traverser<>(aconcurrenthashmapv8_node, aconcurrenthashmapv8_node.length, 0, aconcurrenthashmapv8_node.length);

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = concurrenthashmapv8_traverser.advance()) != null) {
                        concurrenthashmapv8_action.apply(new ConcurrentHashMapV8.MapEntry<>(concurrenthashmapv8_node.key, concurrenthashmapv8_node.val, this.map));
                    }
                }

            }
        }
    }

    static final class ValuesView<K, V> extends ConcurrentHashMapV8.CollectionView<K, V, V> implements Collection<V>, Serializable {

        private static final long serialVersionUID = 2249069246763182397L;

        ValuesView(ConcurrentHashMapV8<K, V> concurrenthashmapv8) {
            super(concurrenthashmapv8);
        }

        @Override
        public final boolean contains(Object object) {
            return this.map.containsValue(object);
        }

        @Override
        public final boolean remove(Object object) {
            if (object != null) {
                Iterator iterator = this.iterator();

                while (iterator.hasNext()) {
                    if (object.equals(iterator.next())) {
                        iterator.remove();
                        return true;
                    }
                }
            }

            return false;
        }

        @Override
        public final Iterator<V> iterator() {
            ConcurrentHashMapV8 concurrenthashmapv8 = this.map;
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = concurrenthashmapv8.table;
            int i = concurrenthashmapv8.table == null ? 0 : aconcurrenthashmapv8_node.length;

            return new ConcurrentHashMapV8.ValueIterator<>(aconcurrenthashmapv8_node, i, 0, i, concurrenthashmapv8);
        }

        public final boolean add(V v0) {
            throw new UnsupportedOperationException();
        }

        public final boolean addAll(Collection<? extends V> collection) {
            throw new UnsupportedOperationException();
        }

        public ConcurrentHashMapV8.ConcurrentHashMapSpliterator<V> spliterator() {
            ConcurrentHashMapV8 concurrenthashmapv8 = this.map;
            long i = concurrenthashmapv8.sumCount();
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = concurrenthashmapv8.table;
            int j = concurrenthashmapv8.table == null ? 0 : aconcurrenthashmapv8_node.length;

            return new ConcurrentHashMapV8.ValueSpliterator<>(aconcurrenthashmapv8_node, j, 0, j, i < 0L ? 0L : i);
        }

        public void forEach(ConcurrentHashMapV8.Action<? super V> concurrenthashmapv8_action) {
            if (concurrenthashmapv8_action == null) {
                throw new NullPointerException();
            } else {
                ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.map.table;

                if (this.map.table != null) {
                    ConcurrentHashMapV8.Traverser concurrenthashmapv8_traverser = new ConcurrentHashMapV8.Traverser<>(aconcurrenthashmapv8_node, aconcurrenthashmapv8_node.length, 0, aconcurrenthashmapv8_node.length);

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = concurrenthashmapv8_traverser.advance()) != null) {
                        concurrenthashmapv8_action.apply(concurrenthashmapv8_node.val);
                    }
                }

            }
        }
    }

    public static class KeySetView<K, V> extends ConcurrentHashMapV8.CollectionView<K, V, K> implements Set<K>, Serializable {

        private static final long serialVersionUID = 7249069246763182397L;
        private final V value;

        KeySetView(ConcurrentHashMapV8<K, V> concurrenthashmapv8, V v0) {
            super(concurrenthashmapv8);
            this.value = v0;
        }

        public V getMappedValue() {
            return this.value;
        }

        @Override
        public boolean contains(Object object) {
            return this.map.containsKey(object);
        }

        @Override
        public boolean remove(Object object) {
            return this.map.remove(object) != null;
        }

        @Override
        public Iterator<K> iterator() {
            ConcurrentHashMapV8 concurrenthashmapv8 = this.map;
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = concurrenthashmapv8.table;
            int i = concurrenthashmapv8.table == null ? 0 : aconcurrenthashmapv8_node.length;

            return new ConcurrentHashMapV8.KeyIterator<>(aconcurrenthashmapv8_node, i, 0, i, concurrenthashmapv8);
        }

        public boolean add(K k0) {
            Object object = this.value;

            if (this.value == null) {
                throw new UnsupportedOperationException();
            } else {
                return this.map.putVal(k0, object, true) == null;
            }
        }

        public boolean addAll(Collection<? extends K> collection) {
            boolean flag = false;
            Object object = this.value;

            if (this.value == null) {
                throw new UnsupportedOperationException();
            } else {
                Iterator iterator = collection.iterator();

                while (iterator.hasNext()) {
                    Object object1 = iterator.next();

                    if (this.map.putVal(object1, object, true) == null) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        public int hashCode() {
            int i = 0;

            Object object;

            for (Iterator iterator = this.iterator(); iterator.hasNext(); i += object.hashCode()) {
                object = iterator.next();
            }

            return i;
        }

        public boolean equals(Object object) {
            Set set;

            return object instanceof Set && ((set = (Set) object) == this || this.containsAll(set) && set.containsAll(this));
        }

        public ConcurrentHashMapV8.ConcurrentHashMapSpliterator<K> spliterator() {
            ConcurrentHashMapV8 concurrenthashmapv8 = this.map;
            long i = concurrenthashmapv8.sumCount();
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = concurrenthashmapv8.table;
            int j = concurrenthashmapv8.table == null ? 0 : aconcurrenthashmapv8_node.length;

            return new ConcurrentHashMapV8.KeySpliterator<>(aconcurrenthashmapv8_node, j, 0, j, i < 0L ? 0L : i);
        }

        public void forEach(ConcurrentHashMapV8.Action<? super K> concurrenthashmapv8_action) {
            if (concurrenthashmapv8_action == null) {
                throw new NullPointerException();
            } else {
                ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.map.table;

                if (this.map.table != null) {
                    ConcurrentHashMapV8.Traverser concurrenthashmapv8_traverser = new ConcurrentHashMapV8.Traverser<>(aconcurrenthashmapv8_node, aconcurrenthashmapv8_node.length, 0, aconcurrenthashmapv8_node.length);

                    ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                    while ((concurrenthashmapv8_node = concurrenthashmapv8_traverser.advance()) != null) {
                        concurrenthashmapv8_action.apply(concurrenthashmapv8_node.key);
                    }
                }

            }
        }
    }

    abstract static class CollectionView<K, V, E> implements Collection<E>, Serializable {

        private static final long serialVersionUID = 7249069246763182397L;
        final ConcurrentHashMapV8<K, V> map;
        private static final String oomeMsg = "Required array size too large";

        CollectionView(ConcurrentHashMapV8<K, V> concurrenthashmapv8) {
            this.map = concurrenthashmapv8;
        }

        public ConcurrentHashMapV8<K, V> getMap() {
            return this.map;
        }

        public final void clear() {
            this.map.clear();
        }

        public final int size() {
            return this.map.size();
        }

        public final boolean isEmpty() {
            return this.map.isEmpty();
        }

        public abstract Iterator<E> iterator();

        public abstract boolean contains(Object object);

        public abstract boolean remove(Object object);

        public final Object[] toArray() {
            long i = this.map.mappingCount();

            if (i > 2147483639L) {
                throw new OutOfMemoryError("Required array size too large");
            } else {
                int j = (int) i;
                Object[] aobject = new Object[j];
                int k = 0;

                Object object;

                for (Iterator iterator = this.iterator(); iterator.hasNext(); aobject[k++] = object) {
                    object = iterator.next();
                    if (k == j) {
                        if (j >= 2147483639) {
                            throw new OutOfMemoryError("Required array size too large");
                        }

                        if (j >= 1073741819) {
                            j = 2147483639;
                        } else {
                            j += (j >>> 1) + 1;
                        }

                        aobject = Arrays.copyOf(aobject, j);
                    }
                }

                return k == j ? aobject : Arrays.copyOf(aobject, k);
            }
        }

        public final <T> T[] toArray(T[] at) {
            long i = this.map.mappingCount();

            if (i > 2147483639L) {
                throw new OutOfMemoryError("Required array size too large");
            } else {
                int j = (int) i;
                Object[] aobject = at.length >= j ? at : (Object[]) ((Object[]) Array.newInstance(at.getClass().getComponentType(), j));
                int k = aobject.length;
                int l = 0;

                Object object;

                for (Iterator iterator = this.iterator(); iterator.hasNext(); aobject[l++] = object) {
                    object = iterator.next();
                    if (l == k) {
                        if (k >= 2147483639) {
                            throw new OutOfMemoryError("Required array size too large");
                        }

                        if (k >= 1073741819) {
                            k = 2147483639;
                        } else {
                            k += (k >>> 1) + 1;
                        }

                        aobject = Arrays.copyOf(aobject, k);
                    }
                }

                if (at == aobject && l < k) {
                    aobject[l] = null;
                    return aobject;
                } else {
                    return l == k ? aobject : Arrays.copyOf(aobject, l);
                }
            }
        }

        public final String toString() {
            StringBuilder stringbuilder = new StringBuilder();

            stringbuilder.append('[');
            Iterator iterator = this.iterator();

            if (iterator.hasNext()) {
                while (true) {
                    Object object = iterator.next();

                    stringbuilder.append(object == this ? "(this Collection)" : object);
                    if (!iterator.hasNext()) {
                        break;
                    }

                    stringbuilder.append(',').append(' ');
                }
            }

            return stringbuilder.append(']').toString();
        }

        public final boolean containsAll(Collection<?> collection) {
            if (collection != this) {
                Iterator iterator = collection.iterator();

                while (iterator.hasNext()) {
                    Object object = iterator.next();

                    if (object == null || !this.contains(object)) {
                        return false;
                    }
                }
            }

            return true;
        }

        public final boolean removeAll(Collection<?> collection) {
            boolean flag = false;
            Iterator iterator = this.iterator();

            while (iterator.hasNext()) {
                if (collection.contains(iterator.next())) {
                    iterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        public final boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            Iterator iterator = this.iterator();

            while (iterator.hasNext()) {
                if (!collection.contains(iterator.next())) {
                    iterator.remove();
                    flag = true;
                }
            }

            return flag;
        }
    }

    static final class EntrySpliterator<K, V> extends ConcurrentHashMapV8.Traverser<K, V> implements ConcurrentHashMapV8.ConcurrentHashMapSpliterator<Entry<K, V>> {

        final ConcurrentHashMapV8<K, V> map;
        long est;

        EntrySpliterator(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, int i, int j, int k, long l, ConcurrentHashMapV8<K, V> concurrenthashmapv8) {
            super(aconcurrenthashmapv8_node, i, j, k);
            this.map = concurrenthashmapv8;
            this.est = l;
        }

        @Override
        public ConcurrentHashMapV8.ConcurrentHashMapSpliterator<Entry<K, V>> trySplit() {
            int i = this.baseIndex;
            int j = this.baseLimit;
            int k;

            return (k = this.baseIndex + this.baseLimit >>> 1) <= i ? null : new ConcurrentHashMapV8.EntrySpliterator<>(this.tab, this.baseSize, this.baseLimit = k, j, this.est >>>= 1, this.map);
        }

        @Override
        public void forEachRemaining(ConcurrentHashMapV8.Action<? super Entry<K, V>> concurrenthashmapv8_action) {
            if (concurrenthashmapv8_action == null) {
                throw new NullPointerException();
            } else {
                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                while ((concurrenthashmapv8_node = this.advance()) != null) {
                    concurrenthashmapv8_action.apply(new ConcurrentHashMapV8.MapEntry<>(concurrenthashmapv8_node.key, concurrenthashmapv8_node.val, this.map));
                }

            }
        }

        @Override
        public boolean tryAdvance(ConcurrentHashMapV8.Action<? super Entry<K, V>> concurrenthashmapv8_action) {
            if (concurrenthashmapv8_action == null) {
                throw new NullPointerException();
            } else {
                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                if ((concurrenthashmapv8_node = this.advance()) == null) {
                    return false;
                } else {
                    concurrenthashmapv8_action.apply(new ConcurrentHashMapV8.MapEntry<>(concurrenthashmapv8_node.key, concurrenthashmapv8_node.val, this.map));
                    return true;
                }
            }
        }

        @Override
        public long estimateSize() {
            return this.est;
        }
    }

    static final class ValueSpliterator<K, V> extends ConcurrentHashMapV8.Traverser<K, V> implements ConcurrentHashMapV8.ConcurrentHashMapSpliterator<V> {

        long est;

        ValueSpliterator(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, int i, int j, int k, long l) {
            super(aconcurrenthashmapv8_node, i, j, k);
            this.est = l;
        }

        @Override
        public ConcurrentHashMapV8.ConcurrentHashMapSpliterator<V> trySplit() {
            int i = this.baseIndex;
            int j = this.baseLimit;
            int k;

            return (k = this.baseIndex + this.baseLimit >>> 1) <= i ? null : new ConcurrentHashMapV8.ValueSpliterator<>(this.tab, this.baseSize, this.baseLimit = k, j, this.est >>>= 1);
        }

        @Override
        public void forEachRemaining(ConcurrentHashMapV8.Action<? super V> concurrenthashmapv8_action) {
            if (concurrenthashmapv8_action == null) {
                throw new NullPointerException();
            } else {
                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                while ((concurrenthashmapv8_node = this.advance()) != null) {
                    concurrenthashmapv8_action.apply(concurrenthashmapv8_node.val);
                }

            }
        }

        @Override
        public boolean tryAdvance(ConcurrentHashMapV8.Action<? super V> concurrenthashmapv8_action) {
            if (concurrenthashmapv8_action == null) {
                throw new NullPointerException();
            } else {
                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                if ((concurrenthashmapv8_node = this.advance()) == null) {
                    return false;
                } else {
                    concurrenthashmapv8_action.apply(concurrenthashmapv8_node.val);
                    return true;
                }
            }
        }

        @Override
        public long estimateSize() {
            return this.est;
        }
    }

    static final class KeySpliterator<K, V> extends ConcurrentHashMapV8.Traverser<K, V> implements ConcurrentHashMapV8.ConcurrentHashMapSpliterator<K> {

        long est;

        KeySpliterator(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, int i, int j, int k, long l) {
            super(aconcurrenthashmapv8_node, i, j, k);
            this.est = l;
        }

        @Override
        public ConcurrentHashMapV8.ConcurrentHashMapSpliterator<K> trySplit() {
            int i = this.baseIndex;
            int j = this.baseLimit;
            int k;

            return (k = this.baseIndex + this.baseLimit >>> 1) <= i ? null : new ConcurrentHashMapV8.KeySpliterator<>(this.tab, this.baseSize, this.baseLimit = k, j, this.est >>>= 1);
        }

        @Override
        public void forEachRemaining(ConcurrentHashMapV8.Action<? super K> concurrenthashmapv8_action) {
            if (concurrenthashmapv8_action == null) {
                throw new NullPointerException();
            } else {
                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                while ((concurrenthashmapv8_node = this.advance()) != null) {
                    concurrenthashmapv8_action.apply(concurrenthashmapv8_node.key);
                }

            }
        }

        @Override
        public boolean tryAdvance(ConcurrentHashMapV8.Action<? super K> concurrenthashmapv8_action) {
            if (concurrenthashmapv8_action == null) {
                throw new NullPointerException();
            } else {
                ConcurrentHashMapV8.Node concurrenthashmapv8_node;

                if ((concurrenthashmapv8_node = this.advance()) == null) {
                    return false;
                } else {
                    concurrenthashmapv8_action.apply(concurrenthashmapv8_node.key);
                    return true;
                }
            }
        }

        @Override
        public long estimateSize() {
            return this.est;
        }
    }

    static final class MapEntry<K, V> implements Entry<K, V> {

        final K key;
        V val;
        final ConcurrentHashMapV8<K, V> map;

        MapEntry(K k0, V v0, ConcurrentHashMapV8<K, V> concurrenthashmapv8) {
            this.key = k0;
            this.val = v0;
            this.map = concurrenthashmapv8;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.val;
        }

        public int hashCode() {
            return this.key.hashCode() ^ this.val.hashCode();
        }

        public String toString() {
            return this.key + "=" + this.val;
        }

        public boolean equals(Object object) {
            Object object1;
            Object object2;
            Entry entry;

            return object instanceof Entry && (object2 = (entry = (Entry) object).getKey()) != null && (object1 = entry.getValue()) != null && (object2 == this.key || object2.equals(this.key)) && (object1 == this.val || object1.equals(this.val));
        }

        public V setValue(V v0) {
            if (v0 == null) {
                throw new NullPointerException();
            } else {
                Object object = this.val;

                this.val = v0;
                this.map.put(this.key, v0);
                return object;
            }
        }
    }

    static final class EntryIterator<K, V> extends ConcurrentHashMapV8.BaseIterator<K, V> implements Iterator<Entry<K, V>> {

        EntryIterator(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, int i, int j, int k, ConcurrentHashMapV8<K, V> concurrenthashmapv8) {
            super(aconcurrenthashmapv8_node, i, j, k, concurrenthashmapv8);
        }

        public final Entry<K, V> next() {
            ConcurrentHashMapV8.Node concurrenthashmapv8_node = this.next;

            if (this.next == null) {
                throw new NoSuchElementException();
            } else {
                Object object = concurrenthashmapv8_node.key;
                Object object1 = concurrenthashmapv8_node.val;

                this.lastReturned = concurrenthashmapv8_node;
                this.advance();
                return new ConcurrentHashMapV8.MapEntry<>(object, object1, this.map);
            }
        }
    }

    static final class ValueIterator<K, V> extends ConcurrentHashMapV8.BaseIterator<K, V> implements Iterator<V>, Enumeration<V> {

        ValueIterator(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, int i, int j, int k, ConcurrentHashMapV8<K, V> concurrenthashmapv8) {
            super(aconcurrenthashmapv8_node, i, j, k, concurrenthashmapv8);
        }

        public final V next() {
            ConcurrentHashMapV8.Node concurrenthashmapv8_node = this.next;

            if (this.next == null) {
                throw new NoSuchElementException();
            } else {
                Object object = concurrenthashmapv8_node.val;

                this.lastReturned = concurrenthashmapv8_node;
                this.advance();
                return object;
            }
        }

        public final V nextElement() {
            return this.next();
        }
    }

    static final class KeyIterator<K, V> extends ConcurrentHashMapV8.BaseIterator<K, V> implements Iterator<K>, Enumeration<K> {

        KeyIterator(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, int i, int j, int k, ConcurrentHashMapV8<K, V> concurrenthashmapv8) {
            super(aconcurrenthashmapv8_node, i, j, k, concurrenthashmapv8);
        }

        public final K next() {
            ConcurrentHashMapV8.Node concurrenthashmapv8_node = this.next;

            if (this.next == null) {
                throw new NoSuchElementException();
            } else {
                Object object = concurrenthashmapv8_node.key;

                this.lastReturned = concurrenthashmapv8_node;
                this.advance();
                return object;
            }
        }

        public final K nextElement() {
            return this.next();
        }
    }

    static class BaseIterator<K, V> extends ConcurrentHashMapV8.Traverser<K, V> {

        final ConcurrentHashMapV8<K, V> map;
        ConcurrentHashMapV8.Node<K, V> lastReturned;

        BaseIterator(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, int i, int j, int k, ConcurrentHashMapV8<K, V> concurrenthashmapv8) {
            super(aconcurrenthashmapv8_node, i, j, k);
            this.map = concurrenthashmapv8;
            this.advance();
        }

        public final boolean hasNext() {
            return this.next != null;
        }

        public final boolean hasMoreElements() {
            return this.next != null;
        }

        public final void remove() {
            ConcurrentHashMapV8.Node concurrenthashmapv8_node = this.lastReturned;

            if (this.lastReturned == null) {
                throw new IllegalStateException();
            } else {
                this.lastReturned = null;
                this.map.replaceNode(concurrenthashmapv8_node.key, (Object) null, (Object) null);
            }
        }
    }

    static class Traverser<K, V> {

        ConcurrentHashMapV8.Node<K, V>[] tab;
        ConcurrentHashMapV8.Node<K, V> next;
        int index;
        int baseIndex;
        int baseLimit;
        final int baseSize;

        Traverser(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node, int i, int j, int k) {
            this.tab = aconcurrenthashmapv8_node;
            this.baseSize = i;
            this.baseIndex = this.index = j;
            this.baseLimit = k;
            this.next = null;
        }

        final ConcurrentHashMapV8.Node<K, V> advance() {
            Object object = this.next;

            if (this.next != null) {
                object = ((ConcurrentHashMapV8.Node) object).next;
            }

            while (true) {
                if (object != null) {
                    return this.next = (ConcurrentHashMapV8.Node) object;
                }

                if (this.baseIndex >= this.baseLimit) {
                    break;
                }

                ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.tab;

                if (this.tab == null) {
                    break;
                }

                int i;
                int j = i = aconcurrenthashmapv8_node.length;
                int k = this.index;

                if (j <= this.index || k < 0) {
                    break;
                }

                if ((object = ConcurrentHashMapV8.tabAt(aconcurrenthashmapv8_node, this.index)) != null && ((ConcurrentHashMapV8.Node) object).hash < 0) {
                    if (object instanceof ConcurrentHashMapV8.ForwardingNode) {
                        this.tab = ((ConcurrentHashMapV8.ForwardingNode) object).nextTable;
                        object = null;
                        continue;
                    }

                    if (object instanceof ConcurrentHashMapV8.TreeBin) {
                        object = ((ConcurrentHashMapV8.TreeBin) object).first;
                    } else {
                        object = null;
                    }
                }

                if ((this.index += this.baseSize) >= i) {
                    this.index = ++this.baseIndex;
                }
            }

            return this.next = null;
        }
    }

    static final class TreeBin<K, V> extends ConcurrentHashMapV8.Node<K, V> {

        ConcurrentHashMapV8.TreeNode<K, V> root;
        volatile ConcurrentHashMapV8.TreeNode<K, V> first;
        volatile Thread waiter;
        volatile int lockState;
        static final int WRITER = 1;
        static final int WAITER = 2;
        static final int READER = 4;
        private static final Unsafe U;
        private static final long LOCKSTATE;

        TreeBin(ConcurrentHashMapV8.TreeNode<K, V> concurrenthashmapv8_treenode) {
            super(-2, (Object) null, (Object) null, (ConcurrentHashMapV8.Node) null);
            this.first = concurrenthashmapv8_treenode;
            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode1 = null;

            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode2;

            for (ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode3 = concurrenthashmapv8_treenode; concurrenthashmapv8_treenode3 != null; concurrenthashmapv8_treenode3 = concurrenthashmapv8_treenode2) {
                concurrenthashmapv8_treenode2 = (ConcurrentHashMapV8.TreeNode) concurrenthashmapv8_treenode3.next;
                concurrenthashmapv8_treenode3.left = concurrenthashmapv8_treenode3.right = null;
                if (concurrenthashmapv8_treenode1 == null) {
                    concurrenthashmapv8_treenode3.parent = null;
                    concurrenthashmapv8_treenode3.red = false;
                    concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode3;
                } else {
                    Object object = concurrenthashmapv8_treenode3.key;
                    int i = concurrenthashmapv8_treenode3.hash;
                    Class oclass = null;
                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode4 = concurrenthashmapv8_treenode1;

                    int j;
                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode5;

                    do {
                        int k = concurrenthashmapv8_treenode4.hash;

                        if (concurrenthashmapv8_treenode4.hash > i) {
                            j = -1;
                        } else if (k < i) {
                            j = 1;
                        } else if (oclass == null && (oclass = ConcurrentHashMapV8.comparableClassFor(object)) == null) {
                            j = 0;
                        } else {
                            j = ConcurrentHashMapV8.compareComparables(oclass, object, concurrenthashmapv8_treenode4.key);
                        }

                        concurrenthashmapv8_treenode5 = concurrenthashmapv8_treenode4;
                    } while ((concurrenthashmapv8_treenode4 = j <= 0 ? concurrenthashmapv8_treenode4.left : concurrenthashmapv8_treenode4.right) != null);

                    concurrenthashmapv8_treenode3.parent = concurrenthashmapv8_treenode5;
                    if (j <= 0) {
                        concurrenthashmapv8_treenode5.left = concurrenthashmapv8_treenode3;
                    } else {
                        concurrenthashmapv8_treenode5.right = concurrenthashmapv8_treenode3;
                    }

                    concurrenthashmapv8_treenode1 = balanceInsertion(concurrenthashmapv8_treenode1, concurrenthashmapv8_treenode3);
                }
            }

            this.root = concurrenthashmapv8_treenode1;
        }

        private final void lockRoot() {
            if (!ConcurrentHashMapV8.TreeBin.U.compareAndSwapInt(this, ConcurrentHashMapV8.TreeBin.LOCKSTATE, 0, 1)) {
                this.contendedLock();
            }

        }

        private final void unlockRoot() {
            this.lockState = 0;
        }

        private final void contendedLock() {
            boolean flag = false;

            int i;

            do {
                while (true) {
                    i = this.lockState;
                    if ((this.lockState & 1) == 0) {
                        break;
                    }

                    if ((i | 2) == 0) {
                        if (ConcurrentHashMapV8.TreeBin.U.compareAndSwapInt(this, ConcurrentHashMapV8.TreeBin.LOCKSTATE, i, i | 2)) {
                            flag = true;
                            this.waiter = Thread.currentThread();
                        }
                    } else if (flag) {
                        LockSupport.park(this);
                    }
                }
            } while (!ConcurrentHashMapV8.TreeBin.U.compareAndSwapInt(this, ConcurrentHashMapV8.TreeBin.LOCKSTATE, i, 1));

            if (flag) {
                this.waiter = null;
            }

        }

        @Override
        final ConcurrentHashMapV8.Node<K, V> find(int i, Object object) {
            if (object != null) {
                for (Object object1 = this.first; object1 != null; object1 = ((ConcurrentHashMapV8.Node) object1).next) {
                    int j = this.lockState;

                    if ((this.lockState & 3) != 0) {
                        if (((ConcurrentHashMapV8.Node) object1).hash == i) {
                            Object object2 = ((ConcurrentHashMapV8.Node) object1).key;

                            if (((ConcurrentHashMapV8.Node) object1).key == object || object2 != null && object.equals(object2)) {
                                return (ConcurrentHashMapV8.Node) object1;
                            }
                        }
                    } else if (ConcurrentHashMapV8.TreeBin.U.compareAndSwapInt(this, ConcurrentHashMapV8.TreeBin.LOCKSTATE, j, j + 4)) {
                        boolean flag = false;

                        ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode;

                        try {
                            flag = true;
                            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode1 = this.root;

                            concurrenthashmapv8_treenode = this.root == null ? null : concurrenthashmapv8_treenode1.findTreeNode(i, object, (Class) null);
                            flag = false;
                        } finally {
                            if (flag) {
                                int k;

                                do {
                                    k = this.lockState;
                                } while (!ConcurrentHashMapV8.TreeBin.U.compareAndSwapInt(this, ConcurrentHashMapV8.TreeBin.LOCKSTATE, this.lockState, k - 4));

                                if (k == 6) {
                                    Thread thread = this.waiter;

                                    if (this.waiter != null) {
                                        LockSupport.unpark(thread);
                                    }
                                }

                            }
                        }

                        int l;

                        do {
                            l = this.lockState;
                        } while (!ConcurrentHashMapV8.TreeBin.U.compareAndSwapInt(this, ConcurrentHashMapV8.TreeBin.LOCKSTATE, this.lockState, l - 4));

                        if (l == 6) {
                            Thread thread1 = this.waiter;

                            if (this.waiter != null) {
                                LockSupport.unpark(thread1);
                            }
                        }

                        return concurrenthashmapv8_treenode;
                    }
                }
            }

            return null;
        }

        final ConcurrentHashMapV8.TreeNode<K, V> putTreeVal(int i, K k0, V v0) {
            Class oclass = null;
            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode = this.root;

            while (true) {
                if (concurrenthashmapv8_treenode == null) {
                    this.first = this.root = new ConcurrentHashMapV8.TreeNode<>(i, k0, v0, (ConcurrentHashMapV8.Node) null, (ConcurrentHashMapV8.TreeNode) null);
                } else {
                    int j = concurrenthashmapv8_treenode.hash;
                    int k;

                    if (concurrenthashmapv8_treenode.hash > i) {
                        k = -1;
                    } else if (j < i) {
                        k = 1;
                    } else {
                        Object object = concurrenthashmapv8_treenode.key;

                        if (concurrenthashmapv8_treenode.key == k0 || object != null && k0.equals(object)) {
                            return concurrenthashmapv8_treenode;
                        }

                        if (oclass == null && (oclass = ConcurrentHashMapV8.comparableClassFor(k0)) == null || (k = ConcurrentHashMapV8.compareComparables(oclass, k0, object)) == 0) {
                            if (concurrenthashmapv8_treenode.left == null) {
                                k = 1;
                            } else {
                                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode.right;
                                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode2;

                                if (concurrenthashmapv8_treenode.right != null && (concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode1.findTreeNode(i, k0, oclass)) != null) {
                                    return concurrenthashmapv8_treenode2;
                                }

                                k = -1;
                            }
                        }
                    }

                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode3 = concurrenthashmapv8_treenode;

                    if ((concurrenthashmapv8_treenode = k < 0 ? concurrenthashmapv8_treenode.left : concurrenthashmapv8_treenode.right) != null) {
                        continue;
                    }

                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode4 = this.first;
                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode5;

                    this.first = concurrenthashmapv8_treenode5 = new ConcurrentHashMapV8.TreeNode<>(i, k0, v0, concurrenthashmapv8_treenode4, concurrenthashmapv8_treenode3);
                    if (concurrenthashmapv8_treenode4 != null) {
                        concurrenthashmapv8_treenode4.prev = concurrenthashmapv8_treenode5;
                    }

                    if (k < 0) {
                        concurrenthashmapv8_treenode3.left = concurrenthashmapv8_treenode5;
                    } else {
                        concurrenthashmapv8_treenode3.right = concurrenthashmapv8_treenode5;
                    }

                    if (!concurrenthashmapv8_treenode3.red) {
                        concurrenthashmapv8_treenode5.red = true;
                    } else {
                        this.lockRoot();

                        try {
                            this.root = balanceInsertion(this.root, concurrenthashmapv8_treenode5);
                        } finally {
                            this.unlockRoot();
                        }
                    }
                }

                assert checkInvariants(this.root);

                return null;
            }
        }

        final boolean removeTreeNode(ConcurrentHashMapV8.TreeNode<K, V> concurrenthashmapv8_treenode) {
            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode1 = (ConcurrentHashMapV8.TreeNode) concurrenthashmapv8_treenode.next;
            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode.prev;

            if (concurrenthashmapv8_treenode2 == null) {
                this.first = concurrenthashmapv8_treenode1;
            } else {
                concurrenthashmapv8_treenode2.next = concurrenthashmapv8_treenode1;
            }

            if (concurrenthashmapv8_treenode1 != null) {
                concurrenthashmapv8_treenode1.prev = concurrenthashmapv8_treenode2;
            }

            if (this.first == null) {
                this.root = null;
                return true;
            } else {
                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode3 = this.root;

                if (this.root != null && concurrenthashmapv8_treenode3.right != null) {
                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode4 = concurrenthashmapv8_treenode3.left;

                    if (concurrenthashmapv8_treenode3.left != null && concurrenthashmapv8_treenode4.left != null) {
                        this.lockRoot();

                        try {
                            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode5 = concurrenthashmapv8_treenode.left;
                            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode6 = concurrenthashmapv8_treenode.right;
                            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode7;
                            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode8;

                            if (concurrenthashmapv8_treenode5 != null && concurrenthashmapv8_treenode6 != null) {
                                concurrenthashmapv8_treenode7 = concurrenthashmapv8_treenode6;

                                while (true) {
                                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode9 = concurrenthashmapv8_treenode7.left;

                                    if (concurrenthashmapv8_treenode7.left == null) {
                                        boolean flag = concurrenthashmapv8_treenode7.red;

                                        concurrenthashmapv8_treenode7.red = concurrenthashmapv8_treenode.red;
                                        concurrenthashmapv8_treenode.red = flag;
                                        ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode10 = concurrenthashmapv8_treenode7.right;
                                        ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode11 = concurrenthashmapv8_treenode.parent;

                                        if (concurrenthashmapv8_treenode7 == concurrenthashmapv8_treenode6) {
                                            concurrenthashmapv8_treenode.parent = concurrenthashmapv8_treenode7;
                                            concurrenthashmapv8_treenode7.right = concurrenthashmapv8_treenode;
                                        } else {
                                            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode12 = concurrenthashmapv8_treenode7.parent;

                                            if ((concurrenthashmapv8_treenode.parent = concurrenthashmapv8_treenode12) != null) {
                                                if (concurrenthashmapv8_treenode7 == concurrenthashmapv8_treenode12.left) {
                                                    concurrenthashmapv8_treenode12.left = concurrenthashmapv8_treenode;
                                                } else {
                                                    concurrenthashmapv8_treenode12.right = concurrenthashmapv8_treenode;
                                                }
                                            }

                                            if ((concurrenthashmapv8_treenode7.right = concurrenthashmapv8_treenode6) != null) {
                                                concurrenthashmapv8_treenode6.parent = concurrenthashmapv8_treenode7;
                                            }
                                        }

                                        concurrenthashmapv8_treenode.left = null;
                                        if ((concurrenthashmapv8_treenode.right = concurrenthashmapv8_treenode10) != null) {
                                            concurrenthashmapv8_treenode10.parent = concurrenthashmapv8_treenode;
                                        }

                                        if ((concurrenthashmapv8_treenode7.left = concurrenthashmapv8_treenode5) != null) {
                                            concurrenthashmapv8_treenode5.parent = concurrenthashmapv8_treenode7;
                                        }

                                        if ((concurrenthashmapv8_treenode7.parent = concurrenthashmapv8_treenode11) == null) {
                                            concurrenthashmapv8_treenode3 = concurrenthashmapv8_treenode7;
                                        } else if (concurrenthashmapv8_treenode == concurrenthashmapv8_treenode11.left) {
                                            concurrenthashmapv8_treenode11.left = concurrenthashmapv8_treenode7;
                                        } else {
                                            concurrenthashmapv8_treenode11.right = concurrenthashmapv8_treenode7;
                                        }

                                        if (concurrenthashmapv8_treenode10 != null) {
                                            concurrenthashmapv8_treenode8 = concurrenthashmapv8_treenode10;
                                        } else {
                                            concurrenthashmapv8_treenode8 = concurrenthashmapv8_treenode;
                                        }
                                        break;
                                    }

                                    concurrenthashmapv8_treenode7 = concurrenthashmapv8_treenode9;
                                }
                            } else if (concurrenthashmapv8_treenode5 != null) {
                                concurrenthashmapv8_treenode8 = concurrenthashmapv8_treenode5;
                            } else if (concurrenthashmapv8_treenode6 != null) {
                                concurrenthashmapv8_treenode8 = concurrenthashmapv8_treenode6;
                            } else {
                                concurrenthashmapv8_treenode8 = concurrenthashmapv8_treenode;
                            }

                            if (concurrenthashmapv8_treenode8 != concurrenthashmapv8_treenode) {
                                concurrenthashmapv8_treenode7 = concurrenthashmapv8_treenode8.parent = concurrenthashmapv8_treenode.parent;
                                if (concurrenthashmapv8_treenode7 == null) {
                                    concurrenthashmapv8_treenode3 = concurrenthashmapv8_treenode8;
                                } else if (concurrenthashmapv8_treenode == concurrenthashmapv8_treenode7.left) {
                                    concurrenthashmapv8_treenode7.left = concurrenthashmapv8_treenode8;
                                } else {
                                    concurrenthashmapv8_treenode7.right = concurrenthashmapv8_treenode8;
                                }

                                concurrenthashmapv8_treenode.left = concurrenthashmapv8_treenode.right = concurrenthashmapv8_treenode.parent = null;
                            }

                            this.root = concurrenthashmapv8_treenode.red ? concurrenthashmapv8_treenode3 : balanceDeletion(concurrenthashmapv8_treenode3, concurrenthashmapv8_treenode8);
                            if (concurrenthashmapv8_treenode == concurrenthashmapv8_treenode8) {
                                concurrenthashmapv8_treenode7 = concurrenthashmapv8_treenode.parent;
                                if (concurrenthashmapv8_treenode.parent != null) {
                                    if (concurrenthashmapv8_treenode == concurrenthashmapv8_treenode7.left) {
                                        concurrenthashmapv8_treenode7.left = null;
                                    } else if (concurrenthashmapv8_treenode == concurrenthashmapv8_treenode7.right) {
                                        concurrenthashmapv8_treenode7.right = null;
                                    }

                                    concurrenthashmapv8_treenode.parent = null;
                                }
                            }
                        } finally {
                            this.unlockRoot();
                        }

                        assert checkInvariants(this.root);

                        return false;
                    }
                }

                return true;
            }
        }

        static <K, V> ConcurrentHashMapV8.TreeNode<K, V> rotateLeft(ConcurrentHashMapV8.TreeNode<K, V> concurrenthashmapv8_treenode, ConcurrentHashMapV8.TreeNode<K, V> concurrenthashmapv8_treenode1) {
            if (concurrenthashmapv8_treenode1 != null) {
                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode1.right;

                if (concurrenthashmapv8_treenode1.right != null) {
                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode3;

                    if ((concurrenthashmapv8_treenode3 = concurrenthashmapv8_treenode1.right = concurrenthashmapv8_treenode2.left) != null) {
                        concurrenthashmapv8_treenode3.parent = concurrenthashmapv8_treenode1;
                    }

                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode4;

                    if ((concurrenthashmapv8_treenode4 = concurrenthashmapv8_treenode2.parent = concurrenthashmapv8_treenode1.parent) == null) {
                        concurrenthashmapv8_treenode = concurrenthashmapv8_treenode2;
                        concurrenthashmapv8_treenode2.red = false;
                    } else if (concurrenthashmapv8_treenode4.left == concurrenthashmapv8_treenode1) {
                        concurrenthashmapv8_treenode4.left = concurrenthashmapv8_treenode2;
                    } else {
                        concurrenthashmapv8_treenode4.right = concurrenthashmapv8_treenode2;
                    }

                    concurrenthashmapv8_treenode2.left = concurrenthashmapv8_treenode1;
                    concurrenthashmapv8_treenode1.parent = concurrenthashmapv8_treenode2;
                }
            }

            return concurrenthashmapv8_treenode;
        }

        static <K, V> ConcurrentHashMapV8.TreeNode<K, V> rotateRight(ConcurrentHashMapV8.TreeNode<K, V> concurrenthashmapv8_treenode, ConcurrentHashMapV8.TreeNode<K, V> concurrenthashmapv8_treenode1) {
            if (concurrenthashmapv8_treenode1 != null) {
                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode1.left;

                if (concurrenthashmapv8_treenode1.left != null) {
                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode3;

                    if ((concurrenthashmapv8_treenode3 = concurrenthashmapv8_treenode1.left = concurrenthashmapv8_treenode2.right) != null) {
                        concurrenthashmapv8_treenode3.parent = concurrenthashmapv8_treenode1;
                    }

                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode4;

                    if ((concurrenthashmapv8_treenode4 = concurrenthashmapv8_treenode2.parent = concurrenthashmapv8_treenode1.parent) == null) {
                        concurrenthashmapv8_treenode = concurrenthashmapv8_treenode2;
                        concurrenthashmapv8_treenode2.red = false;
                    } else if (concurrenthashmapv8_treenode4.right == concurrenthashmapv8_treenode1) {
                        concurrenthashmapv8_treenode4.right = concurrenthashmapv8_treenode2;
                    } else {
                        concurrenthashmapv8_treenode4.left = concurrenthashmapv8_treenode2;
                    }

                    concurrenthashmapv8_treenode2.right = concurrenthashmapv8_treenode1;
                    concurrenthashmapv8_treenode1.parent = concurrenthashmapv8_treenode2;
                }
            }

            return concurrenthashmapv8_treenode;
        }

        static <K, V> ConcurrentHashMapV8.TreeNode<K, V> balanceInsertion(ConcurrentHashMapV8.TreeNode<K, V> concurrenthashmapv8_treenode, ConcurrentHashMapV8.TreeNode<K, V> concurrenthashmapv8_treenode1) {
            concurrenthashmapv8_treenode1.red = true;

            while (true) {
                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode1.parent;

                if (concurrenthashmapv8_treenode1.parent == null) {
                    concurrenthashmapv8_treenode1.red = false;
                    return concurrenthashmapv8_treenode1;
                }

                if (!concurrenthashmapv8_treenode2.red) {
                    break;
                }

                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode3 = concurrenthashmapv8_treenode2.parent;

                if (concurrenthashmapv8_treenode2.parent == null) {
                    break;
                }

                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode4 = concurrenthashmapv8_treenode3.left;

                if (concurrenthashmapv8_treenode2 == concurrenthashmapv8_treenode3.left) {
                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode5 = concurrenthashmapv8_treenode3.right;

                    if (concurrenthashmapv8_treenode3.right != null && concurrenthashmapv8_treenode5.red) {
                        concurrenthashmapv8_treenode5.red = false;
                        concurrenthashmapv8_treenode2.red = false;
                        concurrenthashmapv8_treenode3.red = true;
                        concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode3;
                    } else {
                        if (concurrenthashmapv8_treenode1 == concurrenthashmapv8_treenode2.right) {
                            concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode2;
                            concurrenthashmapv8_treenode = rotateLeft(concurrenthashmapv8_treenode, concurrenthashmapv8_treenode2);
                            concurrenthashmapv8_treenode3 = (concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode2.parent) == null ? null : concurrenthashmapv8_treenode2.parent;
                        }

                        if (concurrenthashmapv8_treenode2 != null) {
                            concurrenthashmapv8_treenode2.red = false;
                            if (concurrenthashmapv8_treenode3 != null) {
                                concurrenthashmapv8_treenode3.red = true;
                                concurrenthashmapv8_treenode = rotateRight(concurrenthashmapv8_treenode, concurrenthashmapv8_treenode3);
                            }
                        }
                    }
                } else if (concurrenthashmapv8_treenode4 != null && concurrenthashmapv8_treenode4.red) {
                    concurrenthashmapv8_treenode4.red = false;
                    concurrenthashmapv8_treenode2.red = false;
                    concurrenthashmapv8_treenode3.red = true;
                    concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode3;
                } else {
                    if (concurrenthashmapv8_treenode1 == concurrenthashmapv8_treenode2.left) {
                        concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode2;
                        concurrenthashmapv8_treenode = rotateRight(concurrenthashmapv8_treenode, concurrenthashmapv8_treenode2);
                        concurrenthashmapv8_treenode3 = (concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode2.parent) == null ? null : concurrenthashmapv8_treenode2.parent;
                    }

                    if (concurrenthashmapv8_treenode2 != null) {
                        concurrenthashmapv8_treenode2.red = false;
                        if (concurrenthashmapv8_treenode3 != null) {
                            concurrenthashmapv8_treenode3.red = true;
                            concurrenthashmapv8_treenode = rotateLeft(concurrenthashmapv8_treenode, concurrenthashmapv8_treenode3);
                        }
                    }
                }
            }

            return concurrenthashmapv8_treenode;
        }

        static <K, V> ConcurrentHashMapV8.TreeNode<K, V> balanceDeletion(ConcurrentHashMapV8.TreeNode<K, V> concurrenthashmapv8_treenode, ConcurrentHashMapV8.TreeNode<K, V> concurrenthashmapv8_treenode1) {
            while (concurrenthashmapv8_treenode1 != null && concurrenthashmapv8_treenode1 != concurrenthashmapv8_treenode) {
                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode1.parent;

                if (concurrenthashmapv8_treenode1.parent == null) {
                    concurrenthashmapv8_treenode1.red = false;
                    return concurrenthashmapv8_treenode1;
                }

                if (concurrenthashmapv8_treenode1.red) {
                    concurrenthashmapv8_treenode1.red = false;
                    return concurrenthashmapv8_treenode;
                }

                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode3 = concurrenthashmapv8_treenode2.left;
                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode4;
                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode5;

                if (concurrenthashmapv8_treenode2.left == concurrenthashmapv8_treenode1) {
                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode6 = concurrenthashmapv8_treenode2.right;

                    if (concurrenthashmapv8_treenode2.right != null && concurrenthashmapv8_treenode6.red) {
                        concurrenthashmapv8_treenode6.red = false;
                        concurrenthashmapv8_treenode2.red = true;
                        concurrenthashmapv8_treenode = rotateLeft(concurrenthashmapv8_treenode, concurrenthashmapv8_treenode2);
                        concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode1.parent;
                        concurrenthashmapv8_treenode6 = concurrenthashmapv8_treenode1.parent == null ? null : concurrenthashmapv8_treenode2.right;
                    }

                    if (concurrenthashmapv8_treenode6 == null) {
                        concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode2;
                    } else {
                        concurrenthashmapv8_treenode4 = concurrenthashmapv8_treenode6.left;
                        concurrenthashmapv8_treenode5 = concurrenthashmapv8_treenode6.right;
                        if ((concurrenthashmapv8_treenode5 == null || !concurrenthashmapv8_treenode5.red) && (concurrenthashmapv8_treenode4 == null || !concurrenthashmapv8_treenode4.red)) {
                            concurrenthashmapv8_treenode6.red = true;
                            concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode2;
                        } else {
                            if (concurrenthashmapv8_treenode5 == null || !concurrenthashmapv8_treenode5.red) {
                                if (concurrenthashmapv8_treenode4 != null) {
                                    concurrenthashmapv8_treenode4.red = false;
                                }

                                concurrenthashmapv8_treenode6.red = true;
                                concurrenthashmapv8_treenode = rotateRight(concurrenthashmapv8_treenode, concurrenthashmapv8_treenode6);
                                concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode1.parent;
                                concurrenthashmapv8_treenode6 = concurrenthashmapv8_treenode1.parent == null ? null : concurrenthashmapv8_treenode2.right;
                            }

                            if (concurrenthashmapv8_treenode6 != null) {
                                concurrenthashmapv8_treenode6.red = concurrenthashmapv8_treenode2 == null ? false : concurrenthashmapv8_treenode2.red;
                                concurrenthashmapv8_treenode5 = concurrenthashmapv8_treenode6.right;
                                if (concurrenthashmapv8_treenode6.right != null) {
                                    concurrenthashmapv8_treenode5.red = false;
                                }
                            }

                            if (concurrenthashmapv8_treenode2 != null) {
                                concurrenthashmapv8_treenode2.red = false;
                                concurrenthashmapv8_treenode = rotateLeft(concurrenthashmapv8_treenode, concurrenthashmapv8_treenode2);
                            }

                            concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode;
                        }
                    }
                } else {
                    if (concurrenthashmapv8_treenode3 != null && concurrenthashmapv8_treenode3.red) {
                        concurrenthashmapv8_treenode3.red = false;
                        concurrenthashmapv8_treenode2.red = true;
                        concurrenthashmapv8_treenode = rotateRight(concurrenthashmapv8_treenode, concurrenthashmapv8_treenode2);
                        concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode1.parent;
                        concurrenthashmapv8_treenode3 = concurrenthashmapv8_treenode1.parent == null ? null : concurrenthashmapv8_treenode2.left;
                    }

                    if (concurrenthashmapv8_treenode3 == null) {
                        concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode2;
                    } else {
                        concurrenthashmapv8_treenode4 = concurrenthashmapv8_treenode3.left;
                        concurrenthashmapv8_treenode5 = concurrenthashmapv8_treenode3.right;
                        if ((concurrenthashmapv8_treenode4 == null || !concurrenthashmapv8_treenode4.red) && (concurrenthashmapv8_treenode5 == null || !concurrenthashmapv8_treenode5.red)) {
                            concurrenthashmapv8_treenode3.red = true;
                            concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode2;
                        } else {
                            if (concurrenthashmapv8_treenode4 == null || !concurrenthashmapv8_treenode4.red) {
                                if (concurrenthashmapv8_treenode5 != null) {
                                    concurrenthashmapv8_treenode5.red = false;
                                }

                                concurrenthashmapv8_treenode3.red = true;
                                concurrenthashmapv8_treenode = rotateLeft(concurrenthashmapv8_treenode, concurrenthashmapv8_treenode3);
                                concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode1.parent;
                                concurrenthashmapv8_treenode3 = concurrenthashmapv8_treenode1.parent == null ? null : concurrenthashmapv8_treenode2.left;
                            }

                            if (concurrenthashmapv8_treenode3 != null) {
                                concurrenthashmapv8_treenode3.red = concurrenthashmapv8_treenode2 == null ? false : concurrenthashmapv8_treenode2.red;
                                concurrenthashmapv8_treenode4 = concurrenthashmapv8_treenode3.left;
                                if (concurrenthashmapv8_treenode3.left != null) {
                                    concurrenthashmapv8_treenode4.red = false;
                                }
                            }

                            if (concurrenthashmapv8_treenode2 != null) {
                                concurrenthashmapv8_treenode2.red = false;
                                concurrenthashmapv8_treenode = rotateRight(concurrenthashmapv8_treenode, concurrenthashmapv8_treenode2);
                            }

                            concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode;
                        }
                    }
                }
            }

            return concurrenthashmapv8_treenode;
        }

        static <K, V> boolean checkInvariants(ConcurrentHashMapV8.TreeNode<K, V> concurrenthashmapv8_treenode) {
            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode.parent;
            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode.left;
            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode3 = concurrenthashmapv8_treenode.right;
            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode4 = concurrenthashmapv8_treenode.prev;
            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode5 = (ConcurrentHashMapV8.TreeNode) concurrenthashmapv8_treenode.next;

            return concurrenthashmapv8_treenode4 != null && concurrenthashmapv8_treenode4.next != concurrenthashmapv8_treenode ? false : (concurrenthashmapv8_treenode5 != null && concurrenthashmapv8_treenode5.prev != concurrenthashmapv8_treenode ? false : (concurrenthashmapv8_treenode1 != null && concurrenthashmapv8_treenode != concurrenthashmapv8_treenode1.left && concurrenthashmapv8_treenode != concurrenthashmapv8_treenode1.right ? false : (concurrenthashmapv8_treenode2 != null && (concurrenthashmapv8_treenode2.parent != concurrenthashmapv8_treenode || concurrenthashmapv8_treenode2.hash > concurrenthashmapv8_treenode.hash) ? false : (concurrenthashmapv8_treenode3 != null && (concurrenthashmapv8_treenode3.parent != concurrenthashmapv8_treenode || concurrenthashmapv8_treenode3.hash < concurrenthashmapv8_treenode.hash) ? false : (concurrenthashmapv8_treenode.red && concurrenthashmapv8_treenode2 != null && concurrenthashmapv8_treenode2.red && concurrenthashmapv8_treenode3 != null && concurrenthashmapv8_treenode3.red ? false : (concurrenthashmapv8_treenode2 != null && !checkInvariants(concurrenthashmapv8_treenode2) ? false : concurrenthashmapv8_treenode3 == null || checkInvariants(concurrenthashmapv8_treenode3)))))));
        }

        static {
            try {
                U = ConcurrentHashMapV8.getUnsafe();
                Class oclass = ConcurrentHashMapV8.TreeBin.class;

                LOCKSTATE = ConcurrentHashMapV8.TreeBin.U.objectFieldOffset(oclass.getDeclaredField("lockState"));
            } catch (Exception exception) {
                throw new Error(exception);
            }
        }
    }

    static final class TreeNode<K, V> extends ConcurrentHashMapV8.Node<K, V> {

        ConcurrentHashMapV8.TreeNode<K, V> parent;
        ConcurrentHashMapV8.TreeNode<K, V> left;
        ConcurrentHashMapV8.TreeNode<K, V> right;
        ConcurrentHashMapV8.TreeNode<K, V> prev;
        boolean red;

        TreeNode(int i, K k0, V v0, ConcurrentHashMapV8.Node<K, V> concurrenthashmapv8_node, ConcurrentHashMapV8.TreeNode<K, V> concurrenthashmapv8_treenode) {
            super(i, k0, v0, concurrenthashmapv8_node);
            this.parent = concurrenthashmapv8_treenode;
        }

        @Override
        ConcurrentHashMapV8.Node<K, V> find(int i, Object object) {
            return this.findTreeNode(i, object, (Class) null);
        }

        final ConcurrentHashMapV8.TreeNode<K, V> findTreeNode(int i, Object object, Class<?> oclass) {
            if (object != null) {
                ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode = this;

                do {
                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode1 = concurrenthashmapv8_treenode.left;
                    ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode2 = concurrenthashmapv8_treenode.right;
                    int j = concurrenthashmapv8_treenode.hash;

                    if (concurrenthashmapv8_treenode.hash > i) {
                        concurrenthashmapv8_treenode = concurrenthashmapv8_treenode1;
                    } else if (j < i) {
                        concurrenthashmapv8_treenode = concurrenthashmapv8_treenode2;
                    } else {
                        Object object1 = concurrenthashmapv8_treenode.key;

                        if (concurrenthashmapv8_treenode.key == object || object1 != null && object.equals(object1)) {
                            return concurrenthashmapv8_treenode;
                        }

                        if (concurrenthashmapv8_treenode1 == null && concurrenthashmapv8_treenode2 == null) {
                            break;
                        }

                        int k;

                        if ((oclass != null || (oclass = ConcurrentHashMapV8.comparableClassFor(object)) != null) && (k = ConcurrentHashMapV8.compareComparables(oclass, object, object1)) != 0) {
                            concurrenthashmapv8_treenode = k < 0 ? concurrenthashmapv8_treenode1 : concurrenthashmapv8_treenode2;
                        } else if (concurrenthashmapv8_treenode1 == null) {
                            concurrenthashmapv8_treenode = concurrenthashmapv8_treenode2;
                        } else {
                            ConcurrentHashMapV8.TreeNode concurrenthashmapv8_treenode3;

                            if (concurrenthashmapv8_treenode2 != null && (concurrenthashmapv8_treenode3 = concurrenthashmapv8_treenode2.findTreeNode(i, object, oclass)) != null) {
                                return concurrenthashmapv8_treenode3;
                            }

                            concurrenthashmapv8_treenode = concurrenthashmapv8_treenode1;
                        }
                    }
                } while (concurrenthashmapv8_treenode != null);
            }

            return null;
        }
    }

    static final class ReservationNode<K, V> extends ConcurrentHashMapV8.Node<K, V> {

        ReservationNode() {
            super(-3, (Object) null, (Object) null, (ConcurrentHashMapV8.Node) null);
        }

        @Override
        ConcurrentHashMapV8.Node<K, V> find(int i, Object object) {
            return null;
        }
    }

    static final class ForwardingNode<K, V> extends ConcurrentHashMapV8.Node<K, V> {

        final ConcurrentHashMapV8.Node<K, V>[] nextTable;

        ForwardingNode(ConcurrentHashMapV8.Node<K, V>[] aconcurrenthashmapv8_node) {
            super(-1, (Object) null, (Object) null, (ConcurrentHashMapV8.Node) null);
            this.nextTable = aconcurrenthashmapv8_node;
        }

        @Override
        ConcurrentHashMapV8.Node<K, V> find(int i, Object object) {
            ConcurrentHashMapV8.Node[] aconcurrenthashmapv8_node = this.nextTable;

            label41:
            while (true) {
                ConcurrentHashMapV8.Node concurrenthashmapv8_node;
                int j;

                if (object != null && aconcurrenthashmapv8_node != null && (j = aconcurrenthashmapv8_node.length) != 0 && (concurrenthashmapv8_node = ConcurrentHashMapV8.tabAt(aconcurrenthashmapv8_node, j - 1 & i)) != null) {
                    do {
                        int k = concurrenthashmapv8_node.hash;

                        if (concurrenthashmapv8_node.hash == i) {
                            Object object1 = concurrenthashmapv8_node.key;

                            if (concurrenthashmapv8_node.key == object || object1 != null && object.equals(object1)) {
                                return concurrenthashmapv8_node;
                            }
                        }

                        if (k < 0) {
                            if (!(concurrenthashmapv8_node instanceof ConcurrentHashMapV8.ForwardingNode)) {
                                return concurrenthashmapv8_node.find(i, object);
                            }

                            aconcurrenthashmapv8_node = ((ConcurrentHashMapV8.ForwardingNode) concurrenthashmapv8_node).nextTable;
                            continue label41;
                        }
                    } while ((concurrenthashmapv8_node = concurrenthashmapv8_node.next) != null);

                    return null;
                }

                return null;
            }
        }
    }

    static class Segment<K, V> extends ReentrantLock implements Serializable {

        private static final long serialVersionUID = 2249069246763182397L;
        final float loadFactor;

        Segment(float f) {
            this.loadFactor = f;
        }
    }

    static class Node<K, V> implements Entry<K, V> {

        final int hash;
        final K key;
        volatile V val;
        volatile ConcurrentHashMapV8.Node<K, V> next;

        Node(int i, K k0, V v0, ConcurrentHashMapV8.Node<K, V> concurrenthashmapv8_node) {
            this.hash = i;
            this.key = k0;
            this.val = v0;
            this.next = concurrenthashmapv8_node;
        }

        public final K getKey() {
            return this.key;
        }

        public final V getValue() {
            return this.val;
        }

        public final int hashCode() {
            return this.key.hashCode() ^ this.val.hashCode();
        }

        public final String toString() {
            return this.key + "=" + this.val;
        }

        public final V setValue(V v0) {
            throw new UnsupportedOperationException();
        }

        public final boolean equals(Object object) {
            boolean flag;
            Object object1;
            Object object2;
            Entry entry;

            if (object instanceof Entry && (object2 = (entry = (Entry) object).getKey()) != null && (object1 = entry.getValue()) != null && (object2 == this.key || object2.equals(this.key))) {
                Object object3 = this.val;

                if (object1 == this.val || object1.equals(object3)) {
                    flag = true;
                    return flag;
                }
            }

            flag = false;
            return flag;
        }

        ConcurrentHashMapV8.Node<K, V> find(int i, Object object) {
            ConcurrentHashMapV8.Node concurrenthashmapv8_node = this;

            if (object != null) {
                do {
                    if (concurrenthashmapv8_node.hash == i) {
                        Object object1 = concurrenthashmapv8_node.key;

                        if (concurrenthashmapv8_node.key == object || object1 != null && object.equals(object1)) {
                            return concurrenthashmapv8_node;
                        }
                    }
                } while ((concurrenthashmapv8_node = concurrenthashmapv8_node.next) != null);
            }

            return null;
        }
    }

    public interface IntByIntToInt {

        int apply(int i, int j);
    }

    public interface LongByLongToLong {

        long apply(long i, long j);
    }

    public interface DoubleByDoubleToDouble {

        double apply(double d0, double d1);
    }

    public interface ObjectByObjectToInt<A, B> {

        int apply(A a0, B b0);
    }

    public interface ObjectByObjectToLong<A, B> {

        long apply(A a0, B b0);
    }

    public interface ObjectByObjectToDouble<A, B> {

        double apply(A a0, B b0);
    }

    public interface ObjectToInt<A> {

        int apply(A a0);
    }

    public interface ObjectToLong<A> {

        long apply(A a0);
    }

    public interface ObjectToDouble<A> {

        double apply(A a0);
    }

    public interface BiFun<A, B, T> {

        T apply(A a0, B b0);
    }

    public interface Fun<A, T> {

        T apply(A a0);
    }

    public interface BiAction<A, B> {

        void apply(A a0, B b0);
    }

    public interface Action<A> {

        void apply(A a0);
    }

    public interface ConcurrentHashMapSpliterator<T> {

        ConcurrentHashMapV8.ConcurrentHashMapSpliterator<T> trySplit();

        long estimateSize();

        void forEachRemaining(ConcurrentHashMapV8.Action<? super T> concurrenthashmapv8_action);

        boolean tryAdvance(ConcurrentHashMapV8.Action<? super T> concurrenthashmapv8_action);
    }
}
