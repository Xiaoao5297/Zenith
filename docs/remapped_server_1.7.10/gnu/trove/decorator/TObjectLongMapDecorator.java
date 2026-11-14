package gnu.trove.decorator;

import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.TObjectLongMap;
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

public class TObjectLongMapDecorator<K> extends AbstractMap<K, Long> implements Map<K, Long>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TObjectLongMap<K> _map;

    public TObjectLongMapDecorator() {}

    public TObjectLongMapDecorator(TObjectLongMap<K> tobjectlongmap) {
        this._map = tobjectlongmap;
    }

    public TObjectLongMap<K> getMap() {
        return this._map;
    }

    public Long put(K k0, Long olong) {
        return olong == null ? this.wrapValue(this._map.put(k0, this._map.getNoEntryValue())) : this.wrapValue(this._map.put(k0, this.unwrapValue(olong)));
    }

    public Long get(Object object) {
        long i = this._map.get(object);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public void clear() {
        this._map.clear();
    }

    public Long remove(Object object) {
        long i = this._map.remove(object);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public Set<Entry<K, Long>> entrySet() {
        return new AbstractSet<Entry<K, Long>>() {
            public int size() {
                return TObjectLongMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TObjectLongMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TObjectLongMapDecorator.this.containsKey(object1) && TObjectLongMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<K, Long>> iterator() {
                return new Iterator<Entry<K, Long>>() {
                    private final TObjectLongIterator<K> it;

                    {
                        this.it = TObjectLongMapDecorator.this._map.iterator();
                    }

                    public Entry<K, Long> next() {
                        this.it.advance();
                        final Object object = this.it.key();
                        final Long olong = TObjectLongMapDecorator.this.wrapValue(this.it.value());

                        return new Entry<K, Long>() {
                            private Long val = olong;

                            public boolean equals(Object object1) {
                                return object1 instanceof Entry && ((Entry) object1).getKey().equals(object) && ((Entry) object1).getValue().equals(this.val);
                            }

                            public K getKey() {
                                return object;
                            }

                            public Long getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return object.hashCode() + this.val.hashCode();
                            }

                            public Long setValue(Long olong1) {
                                this.val = olong1;
                                return TObjectLongMapDecorator.this.put(object, olong1);
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

            public boolean add(Entry<K, Long> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Object object1 = ((Entry) object).getKey();

                    TObjectLongMapDecorator.this._map.remove(object1);
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<K, Long>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TObjectLongMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Long && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends K, ? extends Long> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Long) entry.getValue());
        }

    }

    protected Long wrapValue(long i) {
        return i;
    }

    protected long unwrapValue(Object object) {
        return (Long) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TObjectLongMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
