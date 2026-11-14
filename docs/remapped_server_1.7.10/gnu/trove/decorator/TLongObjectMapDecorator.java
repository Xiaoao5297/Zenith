package gnu.trove.decorator;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
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

public class TLongObjectMapDecorator<V> extends AbstractMap<Long, V> implements Map<Long, V>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TLongObjectMap<V> _map;

    public TLongObjectMapDecorator() {}

    public TLongObjectMapDecorator(TLongObjectMap<V> tlongobjectmap) {
        this._map = tlongobjectmap;
    }

    public TLongObjectMap<V> getMap() {
        return this._map;
    }

    public V put(Long olong, V v0) {
        long i;

        if (olong == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(olong);
        }

        return this._map.put(i, v0);
    }

    public V get(Object object) {
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
                return null;
            }

            i = this.unwrapKey((Long) object);
        } else {
            i = this._map.getNoEntryKey();
        }

        return this._map.get(i);
    }

    public void clear() {
        this._map.clear();
    }

    public V remove(Object object) {
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
                return null;
            }

            i = this.unwrapKey((Long) object);
        } else {
            i = this._map.getNoEntryKey();
        }

        return this._map.remove(i);
    }

    public Set<Entry<Long, V>> entrySet() {
        return new AbstractSet<Entry<Long, V>>() {
            public int size() {
                return TLongObjectMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TLongObjectMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TLongObjectMapDecorator.this.containsKey(object1) && TLongObjectMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Long, V>> iterator() {
                return new Iterator<Entry<Long, V>>() {
                    private final TLongObjectIterator<V> it;

                    {
                        this.it = TLongObjectMapDecorator.this._map.iterator();
                    }

                    public Entry<Long, V> next() {
                        this.it.advance();
                        long i = this.it.key();
                        final Long olong = i == TLongObjectMapDecorator.this._map.getNoEntryKey() ? null : TLongObjectMapDecorator.this.wrapKey(i);
                        final Object object = this.it.value();

                        return new Entry<Long, V>() {
                            private V val = object;

                            public boolean equals(Object object1) {
                                return object1 instanceof Entry && ((Entry) object1).getKey().equals(olong) && ((Entry) object1).getValue().equals(this.val);
                            }

                            public Long getKey() {
                                return olong;
                            }

                            public V getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return olong.hashCode() + this.val.hashCode();
                            }

                            public V setValue(V v0) {
                                this.val = v0;
                                return TLongObjectMapDecorator.this.put(olong, v0);
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

            public boolean add(Entry<Long, V> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Long olong = (Long) ((Entry) object).getKey();

                    TLongObjectMapDecorator.this._map.remove(TLongObjectMapDecorator.this.unwrapKey(olong));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Long, V>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TLongObjectMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return this._map.containsValue(object);
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Long && this._map.containsKey((Long) object);
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Long, ? extends V> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Long) entry.getKey(), entry.getValue());
        }

    }

    protected Long wrapKey(long i) {
        return i;
    }

    protected long unwrapKey(Long olong) {
        return olong;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TLongObjectMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
