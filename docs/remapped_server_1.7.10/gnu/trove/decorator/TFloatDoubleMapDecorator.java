package gnu.trove.decorator;

import gnu.trove.iterator.TFloatDoubleIterator;
import gnu.trove.map.TFloatDoubleMap;
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

public class TFloatDoubleMapDecorator extends AbstractMap<Float, Double> implements Map<Float, Double>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TFloatDoubleMap _map;

    public TFloatDoubleMapDecorator() {}

    public TFloatDoubleMapDecorator(TFloatDoubleMap tfloatdoublemap) {
        this._map = tfloatdoublemap;
    }

    public TFloatDoubleMap getMap() {
        return this._map;
    }

    public Double put(Float ofloat, Double odouble) {
        float f;

        if (ofloat == null) {
            f = this._map.getNoEntryKey();
        } else {
            f = this.unwrapKey(ofloat);
        }

        double d0;

        if (odouble == null) {
            d0 = this._map.getNoEntryValue();
        } else {
            d0 = this.unwrapValue(odouble);
        }

        double d1 = this._map.put(f, d0);

        return d1 == this._map.getNoEntryValue() ? null : this.wrapValue(d1);
    }

    public Double get(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey(object);
        } else {
            f = this._map.getNoEntryKey();
        }

        double d0 = this._map.get(f);

        return d0 == this._map.getNoEntryValue() ? null : this.wrapValue(d0);
    }

    public void clear() {
        this._map.clear();
    }

    public Double remove(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey(object);
        } else {
            f = this._map.getNoEntryKey();
        }

        double d0 = this._map.remove(f);

        return d0 == this._map.getNoEntryValue() ? null : this.wrapValue(d0);
    }

    public Set<Entry<Float, Double>> entrySet() {
        return new AbstractSet<Entry<Float, Double>>() {
            public int size() {
                return TFloatDoubleMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TFloatDoubleMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TFloatDoubleMapDecorator.this.containsKey(object1) && TFloatDoubleMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Float, Double>> iterator() {
                return new Iterator<Entry<Float, Double>>() {
                    private final TFloatDoubleIterator it;

                    {
                        this.it = TFloatDoubleMapDecorator.this._map.iterator();
                    }

                    public Entry<Float, Double> next() {
                        this.it.advance();
                        float f = this.it.key();
                        final Float ofloat = f == TFloatDoubleMapDecorator.this._map.getNoEntryKey() ? null : TFloatDoubleMapDecorator.this.wrapKey(f);
                        double d0 = this.it.value();
                        final Double odouble = d0 == TFloatDoubleMapDecorator.this._map.getNoEntryValue() ? null : TFloatDoubleMapDecorator.this.wrapValue(d0);

                        return new Entry<Float, Double>() {
                            private Double val = odouble;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(ofloat) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Float getKey() {
                                return ofloat;
                            }

                            public Double getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return ofloat.hashCode() + this.val.hashCode();
                            }

                            public Double setValue(Double odouble1) {
                                this.val = odouble1;
                                return TFloatDoubleMapDecorator.this.put(ofloat, odouble1);
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

            public boolean add(Entry<Float, Double> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Float ofloat = (Float) ((Entry) object).getKey();

                    TFloatDoubleMapDecorator.this._map.remove(TFloatDoubleMapDecorator.this.unwrapKey(ofloat));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Float, Double>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TFloatDoubleMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Double && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Float && this._map.containsKey(this.unwrapKey(object));
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Float, ? extends Double> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Float) entry.getKey(), (Double) entry.getValue());
        }

    }

    protected Float wrapKey(float f) {
        return f;
    }

    protected float unwrapKey(Object object) {
        return (Float) object;
    }

    protected Double wrapValue(double d0) {
        return d0;
    }

    protected double unwrapValue(Object object) {
        return (Double) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TFloatDoubleMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
