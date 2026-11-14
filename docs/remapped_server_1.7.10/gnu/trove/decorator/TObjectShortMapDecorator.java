package gnu.trove.decorator;

import gnu.trove.iterator.TObjectShortIterator;
import gnu.trove.map.TObjectShortMap;
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

public class TObjectShortMapDecorator<K> extends AbstractMap<K, Short> implements Map<K, Short>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TObjectShortMap<K> _map;

    public TObjectShortMapDecorator() {}

    public TObjectShortMapDecorator(TObjectShortMap<K> tobjectshortmap) {
        this._map = tobjectshortmap;
    }

    public TObjectShortMap<K> getMap() {
        return this._map;
    }

    public Short put(K k0, Short oshort) {
        return oshort == null ? this.wrapValue(this._map.put(k0, this._map.getNoEntryValue())) : this.wrapValue(this._map.put(k0, this.unwrapValue(oshort)));
    }

    public Short get(Object object) {
        short short0 = this._map.get(object);

        return short0 == this._map.getNoEntryValue() ? null : this.wrapValue(short0);
    }

    public void clear() {
        this._map.clear();
    }

    public Short remove(Object object) {
        short short0 = this._map.remove(object);

        return short0 == this._map.getNoEntryValue() ? null : this.wrapValue(short0);
    }

    public Set<Entry<K, Short>> entrySet() {
        return new AbstractSet<Entry<K, Short>>() {
            public int size() {
                return TObjectShortMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TObjectShortMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TObjectShortMapDecorator.this.containsKey(object1) && TObjectShortMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<K, Short>> iterator() {
                return new Iterator<Entry<K, Short>>() {
                    private final TObjectShortIterator<K> it;

                    {
                        this.it = TObjectShortMapDecorator.this._map.iterator();
                    }

                    public Entry<K, Short> next() {
                        this.it.advance();
                        final Object object = this.it.key();
                        final Short oshort = TObjectShortMapDecorator.this.wrapValue(this.it.value());

                        return new Entry<K, Short>() {
                            private Short val = oshort;

                            public boolean equals(Object object1) {
                                return object1 instanceof Entry && ((Entry) object1).getKey().equals(object) && ((Entry) object1).getValue().equals(this.val);
                            }

                            public K getKey() {
                                return object;
                            }

                            public Short getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return object.hashCode() + this.val.hashCode();
                            }

                            public Short setValue(Short oshort1) {
                                this.val = oshort1;
                                return TObjectShortMapDecorator.this.put(object, oshort1);
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

            public boolean add(Entry<K, Short> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Object object1 = ((Entry) object).getKey();

                    TObjectShortMapDecorator.this._map.remove(object1);
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<K, Short>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TObjectShortMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Short && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends K, ? extends Short> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Short) entry.getValue());
        }

    }

    protected Short wrapValue(short short0) {
        return short0;
    }

    protected short unwrapValue(Object object) {
        return (Short) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TObjectShortMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
