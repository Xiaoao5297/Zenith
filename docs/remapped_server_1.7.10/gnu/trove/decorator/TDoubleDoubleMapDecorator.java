package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.map.TDoubleDoubleMap;
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

public class TDoubleDoubleMapDecorator extends AbstractMap<Double, Double> implements Map<Double, Double>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TDoubleDoubleMap _map;

    public TDoubleDoubleMapDecorator() {}

    public TDoubleDoubleMapDecorator(TDoubleDoubleMap tdoubledoublemap) {
        this._map = tdoubledoublemap;
    }

    public TDoubleDoubleMap getMap() {
        return this._map;
    }

    public Double put(Double odouble, Double odouble1) {
        double d0;

        if (odouble == null) {
            d0 = this._map.getNoEntryKey();
        } else {
            d0 = this.unwrapKey(odouble);
        }

        double d1;

        if (odouble1 == null) {
            d1 = this._map.getNoEntryValue();
        } else {
            d1 = this.unwrapValue(odouble1);
        }

        double d2 = this._map.put(d0, d1);

        return d2 == this._map.getNoEntryValue() ? null : this.wrapValue(d2);
    }

    public Double get(Object object) {
        double d0;

        if (object != null) {
            if (!(object instanceof Double)) {
                return null;
            }

            d0 = this.unwrapKey(object);
        } else {
            d0 = this._map.getNoEntryKey();
        }

        double d1 = this._map.get(d0);

        return d1 == this._map.getNoEntryValue() ? null : this.wrapValue(d1);
    }

    public void clear() {
        this._map.clear();
    }

    public Double remove(Object object) {
        double d0;

        if (object != null) {
            if (!(object instanceof Double)) {
                return null;
            }

            d0 = this.unwrapKey(object);
        } else {
            d0 = this._map.getNoEntryKey();
        }

        double d1 = this._map.remove(d0);

        return d1 == this._map.getNoEntryValue() ? null : this.wrapValue(d1);
    }

    public Set<Entry<Double, Double>> entrySet() {
        return new AbstractSet<Entry<Double, Double>>() {
            public int size() {
                return TDoubleDoubleMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TDoubleDoubleMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TDoubleDoubleMapDecorator.this.containsKey(object1) && TDoubleDoubleMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Double, Double>> iterator() {
                return new Iterator<Entry<Double, Double>>() {
                    private final TDoubleDoubleIterator it;

                    {
                        this.it = TDoubleDoubleMapDecorator.this._map.iterator();
                    }

                    public Entry<Double, Double> next() {
                        this.it.advance();
                        double d0 = this.it.key();
                        final Double odouble = d0 == TDoubleDoubleMapDecorator.this._map.getNoEntryKey() ? null : TDoubleDoubleMapDecorator.this.wrapKey(d0);
                        double d1 = this.it.value();
                        final Double odouble1 = d1 == TDoubleDoubleMapDecorator.this._map.getNoEntryValue() ? null : TDoubleDoubleMapDecorator.this.wrapValue(d1);

                        return new Entry<Double, Double>() {
                            private Double val = odouble1;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(odouble) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Double getKey() {
                                return odouble;
                            }

                            public Double getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return odouble.hashCode() + this.val.hashCode();
                            }

                            public Double setValue(Double odouble2) {
                                this.val = odouble2;
                                return TDoubleDoubleMapDecorator.this.put(odouble, odouble2);
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

            public boolean add(Entry<Double, Double> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Double odouble = (Double) ((Entry) object).getKey();

                    TDoubleDoubleMapDecorator.this._map.remove(TDoubleDoubleMapDecorator.this.unwrapKey(odouble));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Double, Double>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TDoubleDoubleMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Double && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Double && this._map.containsKey(this.unwrapKey(object));
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Double, ? extends Double> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Double) entry.getKey(), (Double) entry.getValue());
        }

    }

    protected Double wrapKey(double d0) {
        return d0;
    }

    protected double unwrapKey(Object object) {
        return (Double) object;
    }

    protected Double wrapValue(double d0) {
        return d0;
    }

    protected double unwrapValue(Object object) {
        return (Double) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TDoubleDoubleMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
