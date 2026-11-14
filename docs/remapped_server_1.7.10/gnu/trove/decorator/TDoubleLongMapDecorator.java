package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleLongIterator;
import gnu.trove.map.TDoubleLongMap;
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

public class TDoubleLongMapDecorator extends AbstractMap<Double, Long> implements Map<Double, Long>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TDoubleLongMap _map;

    public TDoubleLongMapDecorator() {}

    public TDoubleLongMapDecorator(TDoubleLongMap tdoublelongmap) {
        this._map = tdoublelongmap;
    }

    public TDoubleLongMap getMap() {
        return this._map;
    }

    public Long put(Double odouble, Long olong) {
        double d0;

        if (odouble == null) {
            d0 = this._map.getNoEntryKey();
        } else {
            d0 = this.unwrapKey(odouble);
        }

        long i;

        if (olong == null) {
            i = this._map.getNoEntryValue();
        } else {
            i = this.unwrapValue(olong);
        }

        long j = this._map.put(d0, i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public Long get(Object object) {
        double d0;

        if (object != null) {
            if (!(object instanceof Double)) {
                return null;
            }

            d0 = this.unwrapKey(object);
        } else {
            d0 = this._map.getNoEntryKey();
        }

        long i = this._map.get(d0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public void clear() {
        this._map.clear();
    }

    public Long remove(Object object) {
        double d0;

        if (object != null) {
            if (!(object instanceof Double)) {
                return null;
            }

            d0 = this.unwrapKey(object);
        } else {
            d0 = this._map.getNoEntryKey();
        }

        long i = this._map.remove(d0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public Set<Entry<Double, Long>> entrySet() {
        return new AbstractSet<Entry<Double, Long>>() {
            public int size() {
                return TDoubleLongMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TDoubleLongMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TDoubleLongMapDecorator.this.containsKey(object1) && TDoubleLongMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Double, Long>> iterator() {
                return new Iterator<Entry<Double, Long>>() {
                    private final TDoubleLongIterator it;

                    {
                        this.it = TDoubleLongMapDecorator.this._map.iterator();
                    }

                    public Entry<Double, Long> next() {
                        this.it.advance();
                        double d0 = this.it.key();
                        final Double odouble = d0 == TDoubleLongMapDecorator.this._map.getNoEntryKey() ? null : TDoubleLongMapDecorator.this.wrapKey(d0);
                        long i = this.it.value();
                        final Long olong = i == TDoubleLongMapDecorator.this._map.getNoEntryValue() ? null : TDoubleLongMapDecorator.this.wrapValue(i);

                        return new Entry<Double, Long>() {
                            private Long val = olong;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(odouble) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Double getKey() {
                                return odouble;
                            }

                            public Long getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return odouble.hashCode() + this.val.hashCode();
                            }

                            public Long setValue(Long olong1) {
                                this.val = olong1;
                                return TDoubleLongMapDecorator.this.put(odouble, olong1);
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

            public boolean add(Entry<Double, Long> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Double odouble = (Double) ((Entry) object).getKey();

                    TDoubleLongMapDecorator.this._map.remove(TDoubleLongMapDecorator.this.unwrapKey(odouble));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Double, Long>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TDoubleLongMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Long && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Double, ? extends Long> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Double) entry.getKey(), (Long) entry.getValue());
        }

    }

    protected Double wrapKey(double d0) {
        return d0;
    }

    protected double unwrapKey(Object object) {
        return (Double) object;
    }

    protected Long wrapValue(long i) {
        return i;
    }

    protected long unwrapValue(Object object) {
        return (Long) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TDoubleLongMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
