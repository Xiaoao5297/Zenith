package gnu.trove.decorator;

import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.map.TFloatObjectMap;
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

public class TFloatObjectMapDecorator<V> extends AbstractMap<Float, V> implements Map<Float, V>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TFloatObjectMap<V> _map;

    public TFloatObjectMapDecorator() {}

    public TFloatObjectMapDecorator(TFloatObjectMap<V> tfloatobjectmap) {
        this._map = tfloatobjectmap;
    }

    public TFloatObjectMap<V> getMap() {
        return this._map;
    }

    public V put(Float ofloat, V v0) {
        float f;

        if (ofloat == null) {
            f = this._map.getNoEntryKey();
        } else {
            f = this.unwrapKey(ofloat);
        }

        return this._map.put(f, v0);
    }

    public V get(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey((Float) object);
        } else {
            f = this._map.getNoEntryKey();
        }

        return this._map.get(f);
    }

    public void clear() {
        this._map.clear();
    }

    public V remove(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey((Float) object);
        } else {
            f = this._map.getNoEntryKey();
        }

        return this._map.remove(f);
    }

    public Set<Entry<Float, V>> entrySet() {
        return new AbstractSet<Entry<Float, V>>() {
            public int size() {
                return TFloatObjectMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TFloatObjectMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TFloatObjectMapDecorator.this.containsKey(object1) && TFloatObjectMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Float, V>> iterator() {
                return new Iterator<Entry<Float, V>>() {
                    private final TFloatObjectIterator<V> it;

                    {
                        this.it = TFloatObjectMapDecorator.this._map.iterator();
                    }

                    public Entry<Float, V> next() {
                        this.it.advance();
                        float f = this.it.key();
                        final Float ofloat = f == TFloatObjectMapDecorator.this._map.getNoEntryKey() ? null : TFloatObjectMapDecorator.this.wrapKey(f);
                        final Object object = this.it.value();

                        return new Entry<Float, V>() {
                            private V val = object;

                            public boolean equals(Object object1) {
                                return object1 instanceof Entry && ((Entry) object1).getKey().equals(ofloat) && ((Entry) object1).getValue().equals(this.val);
                            }

                            public Float getKey() {
                                return ofloat;
                            }

                            public V getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return ofloat.hashCode() + this.val.hashCode();
                            }

                            public V setValue(V v0) {
                                this.val = v0;
                                return TFloatObjectMapDecorator.this.put(ofloat, v0);
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

            public boolean add(Entry<Float, V> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Float ofloat = (Float) ((Entry) object).getKey();

                    TFloatObjectMapDecorator.this._map.remove(TFloatObjectMapDecorator.this.unwrapKey(ofloat));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Float, V>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TFloatObjectMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return this._map.containsValue(object);
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Float && this._map.containsKey((Float) object);
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Float, ? extends V> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Float) entry.getKey(), entry.getValue());
        }

    }

    protected Float wrapKey(float f) {
        return f;
    }

    protected float unwrapKey(Float ofloat) {
        return ofloat;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TFloatObjectMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
