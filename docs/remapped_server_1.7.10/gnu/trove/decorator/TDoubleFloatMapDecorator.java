package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleFloatIterator;
import gnu.trove.map.TDoubleFloatMap;
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

public class TDoubleFloatMapDecorator extends AbstractMap<Double, Float> implements Map<Double, Float>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TDoubleFloatMap _map;

    public TDoubleFloatMapDecorator() {}

    public TDoubleFloatMapDecorator(TDoubleFloatMap tdoublefloatmap) {
        this._map = tdoublefloatmap;
    }

    public TDoubleFloatMap getMap() {
        return this._map;
    }

    public Float put(Double odouble, Float ofloat) {
        double d0;

        if (odouble == null) {
            d0 = this._map.getNoEntryKey();
        } else {
            d0 = this.unwrapKey(odouble);
        }

        float f;

        if (ofloat == null) {
            f = this._map.getNoEntryValue();
        } else {
            f = this.unwrapValue(ofloat);
        }

        float f1 = this._map.put(d0, f);

        return f1 == this._map.getNoEntryValue() ? null : this.wrapValue(f1);
    }

    public Float get(Object object) {
        double d0;

        if (object != null) {
            if (!(object instanceof Double)) {
                return null;
            }

            d0 = this.unwrapKey(object);
        } else {
            d0 = this._map.getNoEntryKey();
        }

        float f = this._map.get(d0);

        return f == this._map.getNoEntryValue() ? null : this.wrapValue(f);
    }

    public void clear() {
        this._map.clear();
    }

    public Float remove(Object object) {
        double d0;

        if (object != null) {
            if (!(object instanceof Double)) {
                return null;
            }

            d0 = this.unwrapKey(object);
        } else {
            d0 = this._map.getNoEntryKey();
        }

        float f = this._map.remove(d0);

        return f == this._map.getNoEntryValue() ? null : this.wrapValue(f);
    }

    public Set<Entry<Double, Float>> entrySet() {
        return new AbstractSet<Entry<Double, Float>>() {
            public int size() {
                return TDoubleFloatMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TDoubleFloatMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TDoubleFloatMapDecorator.this.containsKey(object1) && TDoubleFloatMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Double, Float>> iterator() {
                return new Iterator<Entry<Double, Float>>() {
                    private final TDoubleFloatIterator it;

                    {
                        this.it = TDoubleFloatMapDecorator.this._map.iterator();
                    }

                    public Entry<Double, Float> next() {
                        this.it.advance();
                        double d0 = this.it.key();
                        final Double odouble = d0 == TDoubleFloatMapDecorator.this._map.getNoEntryKey() ? null : TDoubleFloatMapDecorator.this.wrapKey(d0);
                        float f = this.it.value();
                        final Float ofloat = f == TDoubleFloatMapDecorator.this._map.getNoEntryValue() ? null : TDoubleFloatMapDecorator.this.wrapValue(f);

                        return new Entry<Double, Float>() {
                            private Float val = ofloat;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(odouble) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Double getKey() {
                                return odouble;
                            }

                            public Float getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return odouble.hashCode() + this.val.hashCode();
                            }

                            public Float setValue(Float ofloat1) {
                                this.val = ofloat1;
                                return TDoubleFloatMapDecorator.this.put(odouble, ofloat1);
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

            public boolean add(Entry<Double, Float> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Double odouble = (Double) ((Entry) object).getKey();

                    TDoubleFloatMapDecorator.this._map.remove(TDoubleFloatMapDecorator.this.unwrapKey(odouble));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Double, Float>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TDoubleFloatMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Float && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Double, ? extends Float> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Double) entry.getKey(), (Float) entry.getValue());
        }

    }

    protected Double wrapKey(double d0) {
        return d0;
    }

    protected double unwrapKey(Object object) {
        return (Double) object;
    }

    protected Float wrapValue(float f) {
        return f;
    }

    protected float unwrapValue(Object object) {
        return (Float) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TDoubleFloatMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
