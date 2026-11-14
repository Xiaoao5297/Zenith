package gnu.trove.decorator;

import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.map.TLongDoubleMap;
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

public class TLongDoubleMapDecorator extends AbstractMap<Long, Double> implements Map<Long, Double>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TLongDoubleMap _map;

    public TLongDoubleMapDecorator() {}

    public TLongDoubleMapDecorator(TLongDoubleMap tlongdoublemap) {
        this._map = tlongdoublemap;
    }

    public TLongDoubleMap getMap() {
        return this._map;
    }

    public Double put(Long olong, Double odouble) {
        long i;

        if (olong == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(olong);
        }

        double d0;

        if (odouble == null) {
            d0 = this._map.getNoEntryValue();
        } else {
            d0 = this.unwrapValue(odouble);
        }

        double d1 = this._map.put(i, d0);

        return d1 == this._map.getNoEntryValue() ? null : this.wrapValue(d1);
    }

    public Double get(Object object) {
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        double d0 = this._map.get(i);

        return d0 == this._map.getNoEntryValue() ? null : this.wrapValue(d0);
    }

    public void clear() {
        this._map.clear();
    }

    public Double remove(Object object) {
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        double d0 = this._map.remove(i);

        return d0 == this._map.getNoEntryValue() ? null : this.wrapValue(d0);
    }

    public Set<Entry<Long, Double>> entrySet() {
        return new AbstractSet<Entry<Long, Double>>() {
            public int size() {
                return TLongDoubleMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TLongDoubleMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TLongDoubleMapDecorator.this.containsKey(object1) && TLongDoubleMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Long, Double>> iterator() {
                return new Iterator<Entry<Long, Double>>() {
                    private final TLongDoubleIterator it;

                    {
                        this.it = TLongDoubleMapDecorator.this._map.iterator();
                    }

                    public Entry<Long, Double> next() {
                        this.it.advance();
                        long i = this.it.key();
                        final Long olong = i == TLongDoubleMapDecorator.this._map.getNoEntryKey() ? null : TLongDoubleMapDecorator.this.wrapKey(i);
                        double d0 = this.it.value();
                        final Double odouble = d0 == TLongDoubleMapDecorator.this._map.getNoEntryValue() ? null : TLongDoubleMapDecorator.this.wrapValue(d0);

                        return new Entry<Long, Double>() {
                            private Double val = odouble;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(olong) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Long getKey() {
                                return olong;
                            }

                            public Double getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return olong.hashCode() + this.val.hashCode();
                            }

                            public Double setValue(Double odouble1) {
                                this.val = odouble1;
                                return TLongDoubleMapDecorator.this.put(olong, odouble1);
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

            public boolean add(Entry<Long, Double> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Long olong = (Long) ((Entry) object).getKey();

                    TLongDoubleMapDecorator.this._map.remove(TLongDoubleMapDecorator.this.unwrapKey(olong));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Long, Double>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TLongDoubleMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Double && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Long && this._map.containsKey(this.unwrapKey(object));
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Long, ? extends Double> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Long) entry.getKey(), (Double) entry.getValue());
        }

    }

    protected Long wrapKey(long i) {
        return i;
    }

    protected long unwrapKey(Object object) {
        return (Long) object;
    }

    protected Double wrapValue(double d0) {
        return d0;
    }

    protected double unwrapValue(Object object) {
        return (Double) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TLongDoubleMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
