package gnu.trove.decorator;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
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

public class TObjectIntMapDecorator<K> extends AbstractMap<K, Integer> implements Map<K, Integer>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TObjectIntMap<K> _map;

    public TObjectIntMapDecorator() {}

    public TObjectIntMapDecorator(TObjectIntMap<K> tobjectintmap) {
        this._map = tobjectintmap;
    }

    public TObjectIntMap<K> getMap() {
        return this._map;
    }

    public Integer put(K k0, Integer integer) {
        return integer == null ? this.wrapValue(this._map.put(k0, this._map.getNoEntryValue())) : this.wrapValue(this._map.put(k0, this.unwrapValue(integer)));
    }

    public Integer get(Object object) {
        int i = this._map.get(object);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public void clear() {
        this._map.clear();
    }

    public Integer remove(Object object) {
        int i = this._map.remove(object);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public Set<Entry<K, Integer>> entrySet() {
        return new AbstractSet<Entry<K, Integer>>() {
            public int size() {
                return TObjectIntMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TObjectIntMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TObjectIntMapDecorator.this.containsKey(object1) && TObjectIntMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<K, Integer>> iterator() {
                return new Iterator<Entry<K, Integer>>() {
                    private final TObjectIntIterator<K> it;

                    {
                        this.it = TObjectIntMapDecorator.this._map.iterator();
                    }

                    public Entry<K, Integer> next() {
                        this.it.advance();
                        final Object object = this.it.key();
                        final Integer integer = TObjectIntMapDecorator.this.wrapValue(this.it.value());

                        return new Entry<K, Integer>() {
                            private Integer val = integer;

                            public boolean equals(Object object1) {
                                return object1 instanceof Entry && ((Entry) object1).getKey().equals(object) && ((Entry) object1).getValue().equals(this.val);
                            }

                            public K getKey() {
                                return object;
                            }

                            public Integer getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return object.hashCode() + this.val.hashCode();
                            }

                            public Integer setValue(Integer integer1) {
                                this.val = integer1;
                                return TObjectIntMapDecorator.this.put(object, integer1);
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

            public boolean add(Entry<K, Integer> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Object object1 = ((Entry) object).getKey();

                    TObjectIntMapDecorator.this._map.remove(object1);
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<K, Integer>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TObjectIntMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Integer && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return this._map.containsKey(object);
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this._map.size() == 0;
    }

    public void putAll(Map<? extends K, ? extends Integer> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Integer) entry.getValue());
        }

    }

    protected Integer wrapValue(int i) {
        return i;
    }

    protected int unwrapValue(Object object) {
        return (Integer) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TObjectIntMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
