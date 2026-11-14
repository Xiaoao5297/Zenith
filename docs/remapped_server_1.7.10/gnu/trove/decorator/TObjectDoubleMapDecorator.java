package gnu.trove.decorator;

import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.map.TObjectDoubleMap;
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

public class TObjectDoubleMapDecorator<K> extends AbstractMap<K, Double> implements Map<K, Double>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TObjectDoubleMap<K> _map;

    public TObjectDoubleMapDecorator() {}

    public TObjectDoubleMapDecorator(TObjectDoubleMap<K> tobjectdoublemap) {
        this._map = tobjectdoublemap;
    }

    public TObjectDoubleMap<K> getMap() {
        return this._map;
    }

    public Double put(K k0, Double odouble) {
        return odouble == null ? this.wrapValue(this._map.put(k0, this._map.getNoEntryValue())) : this.wrapValue(this._map.put(k0, this.unwrapValue(odouble)));
    }

    public Double get(Object object) {
        double d0 = this._map.get(object);

        return d0 == this._map.getNoEntryValue() ? null : this.wrapValue(d0);
    }

    public void clear() {
        this._map.clear();
    }

    public Double remove(Object object) {
        double d0 = this._map.remove(object);

        return d0 == this._map.getNoEntryValue() ? null : this.wrapValue(d0);
    }

    public Set<Entry<K, Double>> entrySet() {
        return new AbstractSet<Entry<K, Double>>() {
            public int size() {
                return TObjectDoubleMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TObjectDoubleMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TObjectDoubleMapDecorator.this.containsKey(object1) && TObjectDoubleMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<K, Double>> iterator() {
                return new Iterator<Entry<K, Double>>() {
                    private final TObjectDoubleIterator<K> it;

                    {
                        this.it = TObjectDoubleMapDecorator.this._map.iterator();
                    }

                    public Entry<K, Double> next() {
                        this.it.advance();
                        final Object object = this.it.key();
                        final Double odouble = TObjectDoubleMapDecorator.this.wrapValue(this.it.value());

                        return new Entry<K, Double>() {
                            private Double val = odouble;

                            public boolean equals(Object object1) {
                                return object1 instanceof Entry && ((Entry) object1).getKey().equals(object) && ((Entry) object1).getValue().equals(this.val);
                            }

                            public K getKey() {
                                return object;
                            }

                            public Double getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return object.hashCode() + this.val.hashCode();
                            }

                            public Double setValue(Double odouble1) {
                                this.val = odouble1;
                                return TObjectDoubleMapDecorator.this.put(object, odouble1);
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

            public boolean add(Entry<K, Double> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Object object1 = ((Entry) object).getKey();

                    TObjectDoubleMapDecorator.this._map.remove(object1);
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<K, Double>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TObjectDoubleMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Double && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends K, ? extends Double> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Double) entry.getValue());
        }

    }

    protected Double wrapValue(double d0) {
        return d0;
    }

    protected double unwrapValue(Object object) {
        return (Double) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TObjectDoubleMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
