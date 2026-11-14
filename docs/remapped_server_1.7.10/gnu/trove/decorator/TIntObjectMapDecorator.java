package gnu.trove.decorator;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TIntObjectMapDecorator<V> extends AbstractMap<Integer, V> implements Map<Integer, V>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TIntObjectMap<V> _map;

    public TIntObjectMapDecorator() {}

    public TIntObjectMapDecorator(TIntObjectMap<V> tintobjectmap) {
        this._map = tintobjectmap;
    }

    public TIntObjectMap<V> getMap() {
        return this._map;
    }

    public V put(Integer integer, V v0) {
        int i;

        if (integer == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(integer);
        }

        return this._map.put(i, v0);
    }

    public V get(Object object) {
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
                return null;
            }

            i = this.unwrapKey((Integer) object);
        } else {
            i = this._map.getNoEntryKey();
        }

        return this._map.get(i);
    }

    public void clear() {
        this._map.clear();
    }

    public V remove(Object object) {
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
                return null;
            }

            i = this.unwrapKey((Integer) object);
        } else {
            i = this._map.getNoEntryKey();
        }

        return this._map.remove(i);
    }

    public Set<Entry<Integer, V>> entrySet() {
        return new AbstractSet<Entry<Integer, V>>() {
            public int size() {
                return TIntObjectMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TIntObjectMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TIntObjectMapDecorator.this.containsKey(object1) && TIntObjectMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Integer, V>> iterator() {
                return new Iterator<Entry<Integer, V>>() {
                    private final TIntObjectIterator<V> it;

                    {
                        this.it = TIntObjectMapDecorator.this._map.iterator();
                    }

                    public Entry<Integer, V> next() {
                        this.it.advance();
                        int i = this.it.key();
                        final Integer integer = i == TIntObjectMapDecorator.this._map.getNoEntryKey() ? null : TIntObjectMapDecorator.this.wrapKey(i);
                        final Object object = this.it.value();

                        return new Entry<Integer, V>() {
                            private V val = object;

                            public boolean equals(Object object1) {
                                return object1 instanceof Entry && ((Entry) object1).getKey().equals(integer) && ((Entry) object1).getValue().equals(this.val);
                            }

                            public Integer getKey() {
                                return integer;
                            }

                            public V getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return integer.hashCode() + this.val.hashCode();
                            }

                            public V setValue(V v0) {
                                this.val = v0;
                                return TIntObjectMapDecorator.this.put(integer, v0);
                            }
                        };
                    }

                    public boolean hasNext() {
                        return this.it.hasNext();
                    }

                    public void remove() {
                        this.it.remove();
                    }
                };
            }

            public boolean add(Entry<Integer, V> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Integer integer = (Integer) ((Entry) object).getKey();

                    TIntObjectMapDecorator.this._map.remove(TIntObjectMapDecorator.this.unwrapKey(integer));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Integer, V>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TIntObjectMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return this._map.containsValue(object);
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Integer && this._map.containsKey((Integer) object);
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Integer, ? extends V> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Integer) entry.getKey(), entry.getValue());
        }

    }

    protected Integer wrapKey(int i) {
        return i;
    }

    protected int unwrapKey(Integer integer) {
        return integer;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TIntObjectMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
