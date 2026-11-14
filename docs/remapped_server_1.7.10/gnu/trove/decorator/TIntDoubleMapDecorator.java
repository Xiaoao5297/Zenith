package gnu.trove.decorator;

import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.map.TIntDoubleMap;
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

public class TIntDoubleMapDecorator extends AbstractMap<Integer, Double> implements Map<Integer, Double>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TIntDoubleMap _map;

    public TIntDoubleMapDecorator() {}

    public TIntDoubleMapDecorator(TIntDoubleMap tintdoublemap) {
        this._map = tintdoublemap;
    }

    public TIntDoubleMap getMap() {
        return this._map;
    }

    public Double put(Integer integer, Double odouble) {
        int i;

        if (integer == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(integer);
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
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
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
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        double d0 = this._map.remove(i);

        return d0 == this._map.getNoEntryValue() ? null : this.wrapValue(d0);
    }

    public Set<Entry<Integer, Double>> entrySet() {
        return new AbstractSet<Entry<Integer, Double>>() {
            public int size() {
                return TIntDoubleMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TIntDoubleMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TIntDoubleMapDecorator.this.containsKey(object1) && TIntDoubleMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Integer, Double>> iterator() {
                return new Iterator<Entry<Integer, Double>>() {
                    private final TIntDoubleIterator it;

                    {
                        this.it = TIntDoubleMapDecorator.this._map.iterator();
                    }

                    public Entry<Integer, Double> next() {
                        this.it.advance();
                        int i = this.it.key();
                        final Integer integer = i == TIntDoubleMapDecorator.this._map.getNoEntryKey() ? null : TIntDoubleMapDecorator.this.wrapKey(i);
                        double d0 = this.it.value();
                        final Double odouble = d0 == TIntDoubleMapDecorator.this._map.getNoEntryValue() ? null : TIntDoubleMapDecorator.this.wrapValue(d0);

                        return new Entry<Integer, Double>() {
                            private Double val = odouble;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(integer) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Integer getKey() {
                                return integer;
                            }

                            public Double getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return integer.hashCode() + this.val.hashCode();
                            }

                            public Double setValue(Double odouble1) {
                                this.val = odouble1;
                                return TIntDoubleMapDecorator.this.put(integer, odouble1);
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

            public boolean add(Entry<Integer, Double> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Integer integer = (Integer) ((Entry) object).getKey();

                    TIntDoubleMapDecorator.this._map.remove(TIntDoubleMapDecorator.this.unwrapKey(integer));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Integer, Double>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TIntDoubleMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Double && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Integer && this._map.containsKey(this.unwrapKey(object));
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Integer, ? extends Double> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Integer) entry.getKey(), (Double) entry.getValue());
        }

    }

    protected Integer wrapKey(int i) {
        return i;
    }

    protected int unwrapKey(Object object) {
        return (Integer) object;
    }

    protected Double wrapValue(double d0) {
        return d0;
    }

    protected double unwrapValue(Object object) {
        return (Double) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TIntDoubleMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
