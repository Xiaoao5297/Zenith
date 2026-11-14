package gnu.trove.decorator;

import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.TObjectFloatMap;
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

public class TObjectFloatMapDecorator<K> extends AbstractMap<K, Float> implements Map<K, Float>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TObjectFloatMap<K> _map;

    public TObjectFloatMapDecorator() {}

    public TObjectFloatMapDecorator(TObjectFloatMap<K> tobjectfloatmap) {
        this._map = tobjectfloatmap;
    }

    public TObjectFloatMap<K> getMap() {
        return this._map;
    }

    public Float put(K k0, Float ofloat) {
        return ofloat == null ? this.wrapValue(this._map.put(k0, this._map.getNoEntryValue())) : this.wrapValue(this._map.put(k0, this.unwrapValue(ofloat)));
    }

    public Float get(Object object) {
        float f = this._map.get(object);

        return f == this._map.getNoEntryValue() ? null : this.wrapValue(f);
    }

    public void clear() {
        this._map.clear();
    }

    public Float remove(Object object) {
        float f = this._map.remove(object);

        return f == this._map.getNoEntryValue() ? null : this.wrapValue(f);
    }

    public Set<Entry<K, Float>> entrySet() {
        return new AbstractSet<Entry<K, Float>>() {
            public int size() {
                return TObjectFloatMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TObjectFloatMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TObjectFloatMapDecorator.this.containsKey(object1) && TObjectFloatMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<K, Float>> iterator() {
                return new Iterator<Entry<K, Float>>() {
                    private final TObjectFloatIterator<K> it;

                    {
                        this.it = TObjectFloatMapDecorator.this._map.iterator();
                    }

                    public Entry<K, Float> next() {
                        this.it.advance();
                        final Object object = this.it.key();
                        final Float ofloat = TObjectFloatMapDecorator.this.wrapValue(this.it.value());

                        return new Entry<K, Float>() {
                            private Float val = ofloat;

                            public boolean equals(Object object1) {
                                return object1 instanceof Entry && ((Entry) object1).getKey().equals(object) && ((Entry) object1).getValue().equals(this.val);
                            }

                            public K getKey() {
                                return object;
                            }

                            public Float getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return object.hashCode() + this.val.hashCode();
                            }

                            public Float setValue(Float ofloat1) {
                                this.val = ofloat1;
                                return TObjectFloatMapDecorator.this.put(object, ofloat1);
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

            public boolean add(Entry<K, Float> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Object object1 = ((Entry) object).getKey();

                    TObjectFloatMapDecorator.this._map.remove(object1);
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<K, Float>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TObjectFloatMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Float && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends K, ? extends Float> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Float) entry.getValue());
        }

    }

    protected Float wrapValue(float f) {
        return f;
    }

    protected float unwrapValue(Object object) {
        return (Float) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TObjectFloatMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
