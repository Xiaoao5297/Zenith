package gnu.trove.decorator;

import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.map.TShortObjectMap;
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

public class TShortObjectMapDecorator<V> extends AbstractMap<Short, V> implements Map<Short, V>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TShortObjectMap<V> _map;

    public TShortObjectMapDecorator() {}

    public TShortObjectMapDecorator(TShortObjectMap<V> tshortobjectmap) {
        this._map = tshortobjectmap;
    }

    public TShortObjectMap<V> getMap() {
        return this._map;
    }

    public V put(Short oshort, V v0) {
        short short0;

        if (oshort == null) {
            short0 = this._map.getNoEntryKey();
        } else {
            short0 = this.unwrapKey(oshort);
        }

        return this._map.put(short0, v0);
    }

    public V get(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey((Short) object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        return this._map.get(short0);
    }

    public void clear() {
        this._map.clear();
    }

    public V remove(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey((Short) object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        return this._map.remove(short0);
    }

    public Set<Entry<Short, V>> entrySet() {
        return new AbstractSet<Entry<Short, V>>() {
            public int size() {
                return TShortObjectMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TShortObjectMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TShortObjectMapDecorator.this.containsKey(object1) && TShortObjectMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Short, V>> iterator() {
                return new Iterator<Entry<Short, V>>() {
                    private final TShortObjectIterator<V> it;

                    {
                        this.it = TShortObjectMapDecorator.this._map.iterator();
                    }

                    public Entry<Short, V> next() {
                        this.it.advance();
                        short short0 = this.it.key();
                        final Short oshort = short0 == TShortObjectMapDecorator.this._map.getNoEntryKey() ? null : TShortObjectMapDecorator.this.wrapKey(short0);
                        final Object object = this.it.value();

                        return new Entry<Short, V>() {
                            private V val = object;

                            public boolean equals(Object object1) {
                                return object1 instanceof Entry && ((Entry) object1).getKey().equals(oshort) && ((Entry) object1).getValue().equals(this.val);
                            }

                            public Short getKey() {
                                return oshort;
                            }

                            public V getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return oshort.hashCode() + this.val.hashCode();
                            }

                            public V setValue(V v0) {
                                this.val = v0;
                                return TShortObjectMapDecorator.this.put(oshort, v0);
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

            public boolean add(Entry<Short, V> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Short oshort = (Short) ((Entry) object).getKey();

                    TShortObjectMapDecorator.this._map.remove(TShortObjectMapDecorator.this.unwrapKey(oshort));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Short, V>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TShortObjectMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return this._map.containsValue(object);
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Short && this._map.containsKey((Short) object);
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Short, ? extends V> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Short) entry.getKey(), entry.getValue());
        }

    }

    protected Short wrapKey(short short0) {
        return short0;
    }

    protected short unwrapKey(Short oshort) {
        return oshort;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TShortObjectMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
