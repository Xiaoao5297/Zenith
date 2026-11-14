package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleObjectIterator;
import gnu.trove.map.TDoubleObjectMap;
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

public class TDoubleObjectMapDecorator<V> extends AbstractMap<Double, V> implements Map<Double, V>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TDoubleObjectMap<V> _map;

    public TDoubleObjectMapDecorator() {}

    public TDoubleObjectMapDecorator(TDoubleObjectMap<V> tdoubleobjectmap) {
        this._map = tdoubleobjectmap;
    }

    public TDoubleObjectMap<V> getMap() {
        return this._map;
    }

    public V put(Double odouble, V v0) {
        double d0;

        if (odouble == null) {
            d0 = this._map.getNoEntryKey();
        } else {
            d0 = this.unwrapKey(odouble);
        }

        return this._map.put(d0, v0);
    }

    public V get(Object object) {
        double d0;

        if (object != null) {
            if (!(object instanceof Double)) {
                return null;
            }

            d0 = this.unwrapKey((Double) object);
        } else {
            d0 = this._map.getNoEntryKey();
        }

        return this._map.get(d0);
    }

    public void clear() {
        this._map.clear();
    }

    public V remove(Object object) {
        double d0;

        if (object != null) {
            if (!(object instanceof Double)) {
                return null;
            }

            d0 = this.unwrapKey((Double) object);
        } else {
            d0 = this._map.getNoEntryKey();
        }

        return this._map.remove(d0);
    }

    public Set<Entry<Double, V>> entrySet() {
        return new AbstractSet<Entry<Double, V>>() {
            public int size() {
                return TDoubleObjectMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TDoubleObjectMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TDoubleObjectMapDecorator.this.containsKey(object1) && TDoubleObjectMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Double, V>> iterator() {
                return new Iterator<Entry<Double, V>>() {
                    private final TDoubleObjectIterator<V> it;

                    {
                        this.it = TDoubleObjectMapDecorator.this._map.iterator();
                    }

                    public Entry<Double, V> next() {
                        this.it.advance();
                        double d0 = this.it.key();
                        final Double odouble = d0 == TDoubleObjectMapDecorator.this._map.getNoEntryKey() ? null : TDoubleObjectMapDecorator.this.wrapKey(d0);
                        final Object object = this.it.value();

                        return new Entry<Double, V>() {
                            private V val = object;

                            public boolean equals(Object object1) {
                                return object1 instanceof Entry && ((Entry) object1).getKey().equals(odouble) && ((Entry) object1).getValue().equals(this.val);
                            }

                            public Double getKey() {
                                return odouble;
                            }

                            public V getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return odouble.hashCode() + this.val.hashCode();
                            }

                            public V setValue(V v0) {
                                this.val = v0;
                                return TDoubleObjectMapDecorator.this.put(odouble, v0);
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

            public boolean add(Entry<Double, V> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Double odouble = (Double) ((Entry) object).getKey();

                    TDoubleObjectMapDecorator.this._map.remove(TDoubleObjectMapDecorator.this.unwrapKey(odouble));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Double, V>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TDoubleObjectMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return this._map.containsValue(object);
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Double && this._map.containsKey((Double) object);
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Double, ? extends V> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Double) entry.getKey(), entry.getValue());
        }

    }

    protected Double wrapKey(double d0) {
        return d0;
    }

    protected double unwrapKey(Double odouble) {
        return odouble;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TDoubleObjectMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
