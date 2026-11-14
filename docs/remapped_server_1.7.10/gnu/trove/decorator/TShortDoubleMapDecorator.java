package gnu.trove.decorator;

import gnu.trove.iterator.TShortDoubleIterator;
import gnu.trove.map.TShortDoubleMap;
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

public class TShortDoubleMapDecorator extends AbstractMap<Short, Double> implements Map<Short, Double>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TShortDoubleMap _map;

    public TShortDoubleMapDecorator() {}

    public TShortDoubleMapDecorator(TShortDoubleMap tshortdoublemap) {
        this._map = tshortdoublemap;
    }

    public TShortDoubleMap getMap() {
        return this._map;
    }

    public Double put(Short oshort, Double odouble) {
        short short0;

        if (oshort == null) {
            short0 = this._map.getNoEntryKey();
        } else {
            short0 = this.unwrapKey(oshort);
        }

        double d0;

        if (odouble == null) {
            d0 = this._map.getNoEntryValue();
        } else {
            d0 = this.unwrapValue(odouble);
        }

        double d1 = this._map.put(short0, d0);

        return d1 == this._map.getNoEntryValue() ? null : this.wrapValue(d1);
    }

    public Double get(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey(object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        double d0 = this._map.get(short0);

        return d0 == this._map.getNoEntryValue() ? null : this.wrapValue(d0);
    }

    public void clear() {
        this._map.clear();
    }

    public Double remove(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey(object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        double d0 = this._map.remove(short0);

        return d0 == this._map.getNoEntryValue() ? null : this.wrapValue(d0);
    }

    public Set<Entry<Short, Double>> entrySet() {
        return new AbstractSet<Entry<Short, Double>>() {
            public int size() {
                return TShortDoubleMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TShortDoubleMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TShortDoubleMapDecorator.this.containsKey(object1) && TShortDoubleMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Short, Double>> iterator() {
                return new Iterator<Entry<Short, Double>>() {
                    private final TShortDoubleIterator it;

                    {
                        this.it = TShortDoubleMapDecorator.this._map.iterator();
                    }

                    public Entry<Short, Double> next() {
                        this.it.advance();
                        short short0 = this.it.key();
                        final Short oshort = short0 == TShortDoubleMapDecorator.this._map.getNoEntryKey() ? null : TShortDoubleMapDecorator.this.wrapKey(short0);
                        double d0 = this.it.value();
                        final Double odouble = d0 == TShortDoubleMapDecorator.this._map.getNoEntryValue() ? null : TShortDoubleMapDecorator.this.wrapValue(d0);

                        return new Entry<Short, Double>() {
                            private Double val = odouble;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(oshort) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Short getKey() {
                                return oshort;
                            }

                            public Double getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return oshort.hashCode() + this.val.hashCode();
                            }

                            public Double setValue(Double odouble1) {
                                this.val = odouble1;
                                return TShortDoubleMapDecorator.this.put(oshort, odouble1);
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

            public boolean add(Entry<Short, Double> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Short oshort = (Short) ((Entry) object).getKey();

                    TShortDoubleMapDecorator.this._map.remove(TShortDoubleMapDecorator.this.unwrapKey(oshort));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Short, Double>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TShortDoubleMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Double && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Short && this._map.containsKey(this.unwrapKey(object));
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Short, ? extends Double> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Short) entry.getKey(), (Double) entry.getValue());
        }

    }

    protected Short wrapKey(short short0) {
        return short0;
    }

    protected short unwrapKey(Object object) {
        return (Short) object;
    }

    protected Double wrapValue(double d0) {
        return d0;
    }

    protected double unwrapValue(Object object) {
        return (Double) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TShortDoubleMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
