package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleShortIterator;
import gnu.trove.map.TDoubleShortMap;
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

public class TDoubleShortMapDecorator extends AbstractMap<Double, Short> implements Map<Double, Short>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TDoubleShortMap _map;

    public TDoubleShortMapDecorator() {}

    public TDoubleShortMapDecorator(TDoubleShortMap tdoubleshortmap) {
        this._map = tdoubleshortmap;
    }

    public TDoubleShortMap getMap() {
        return this._map;
    }

    public Short put(Double odouble, Short oshort) {
        double d0;

        if (odouble == null) {
            d0 = this._map.getNoEntryKey();
        } else {
            d0 = this.unwrapKey(odouble);
        }

        short short0;

        if (oshort == null) {
            short0 = this._map.getNoEntryValue();
        } else {
            short0 = this.unwrapValue(oshort);
        }

        short short1 = this._map.put(d0, short0);

        return short1 == this._map.getNoEntryValue() ? null : this.wrapValue(short1);
    }

    public Short get(Object object) {
        double d0;

        if (object != null) {
            if (!(object instanceof Double)) {
                return null;
            }

            d0 = this.unwrapKey(object);
        } else {
            d0 = this._map.getNoEntryKey();
        }

        short short0 = this._map.get(d0);

        return short0 == this._map.getNoEntryValue() ? null : this.wrapValue(short0);
    }

    public void clear() {
        this._map.clear();
    }

    public Short remove(Object object) {
        double d0;

        if (object != null) {
            if (!(object instanceof Double)) {
                return null;
            }

            d0 = this.unwrapKey(object);
        } else {
            d0 = this._map.getNoEntryKey();
        }

        short short0 = this._map.remove(d0);

        return short0 == this._map.getNoEntryValue() ? null : this.wrapValue(short0);
    }

    public Set<Entry<Double, Short>> entrySet() {
        return new AbstractSet<Entry<Double, Short>>() {
            public int size() {
                return TDoubleShortMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TDoubleShortMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TDoubleShortMapDecorator.this.containsKey(object1) && TDoubleShortMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Double, Short>> iterator() {
                return new Iterator<Entry<Double, Short>>() {
                    private final TDoubleShortIterator it;

                    {
                        this.it = TDoubleShortMapDecorator.this._map.iterator();
                    }

                    public Entry<Double, Short> next() {
                        this.it.advance();
                        double d0 = this.it.key();
                        final Double odouble = d0 == TDoubleShortMapDecorator.this._map.getNoEntryKey() ? null : TDoubleShortMapDecorator.this.wrapKey(d0);
                        short short0 = this.it.value();
                        final Short oshort = short0 == TDoubleShortMapDecorator.this._map.getNoEntryValue() ? null : TDoubleShortMapDecorator.this.wrapValue(short0);

                        return new Entry<Double, Short>() {
                            private Short val = oshort;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(odouble) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Double getKey() {
                                return odouble;
                            }

                            public Short getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return odouble.hashCode() + this.val.hashCode();
                            }

                            public Short setValue(Short oshort1) {
                                this.val = oshort1;
                                return TDoubleShortMapDecorator.this.put(odouble, oshort1);
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

            public boolean add(Entry<Double, Short> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Double odouble = (Double) ((Entry) object).getKey();

                    TDoubleShortMapDecorator.this._map.remove(TDoubleShortMapDecorator.this.unwrapKey(odouble));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Double, Short>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TDoubleShortMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Short && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Double, ? extends Short> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Double) entry.getKey(), (Short) entry.getValue());
        }

    }

    protected Double wrapKey(double d0) {
        return d0;
    }

    protected double unwrapKey(Object object) {
        return (Double) object;
    }

    protected Short wrapValue(short short0) {
        return short0;
    }

    protected short unwrapValue(Object object) {
        return (Short) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TDoubleShortMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
