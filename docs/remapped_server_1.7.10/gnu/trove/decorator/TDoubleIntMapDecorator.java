package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleIntIterator;
import gnu.trove.map.TDoubleIntMap;
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

public class TDoubleIntMapDecorator extends AbstractMap<Double, Integer> implements Map<Double, Integer>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TDoubleIntMap _map;

    public TDoubleIntMapDecorator() {}

    public TDoubleIntMapDecorator(TDoubleIntMap tdoubleintmap) {
        this._map = tdoubleintmap;
    }

    public TDoubleIntMap getMap() {
        return this._map;
    }

    public Integer put(Double odouble, Integer integer) {
        double d0;

        if (odouble == null) {
            d0 = this._map.getNoEntryKey();
        } else {
            d0 = this.unwrapKey(odouble);
        }

        int i;

        if (integer == null) {
            i = this._map.getNoEntryValue();
        } else {
            i = this.unwrapValue(integer);
        }

        int j = this._map.put(d0, i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public Integer get(Object object) {
        double d0;

        if (object != null) {
            if (!(object instanceof Double)) {
                return null;
            }

            d0 = this.unwrapKey(object);
        } else {
            d0 = this._map.getNoEntryKey();
        }

        int i = this._map.get(d0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public void clear() {
        this._map.clear();
    }

    public Integer remove(Object object) {
        double d0;

        if (object != null) {
            if (!(object instanceof Double)) {
                return null;
            }

            d0 = this.unwrapKey(object);
        } else {
            d0 = this._map.getNoEntryKey();
        }

        int i = this._map.remove(d0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public Set<Entry<Double, Integer>> entrySet() {
        return new AbstractSet<Entry<Double, Integer>>() {
            public int size() {
                return TDoubleIntMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TDoubleIntMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TDoubleIntMapDecorator.this.containsKey(object1) && TDoubleIntMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Double, Integer>> iterator() {
                return new Iterator<Entry<Double, Integer>>() {
                    private final TDoubleIntIterator it;

                    {
                        this.it = TDoubleIntMapDecorator.this._map.iterator();
                    }

                    public Entry<Double, Integer> next() {
                        this.it.advance();
                        double d0 = this.it.key();
                        final Double odouble = d0 == TDoubleIntMapDecorator.this._map.getNoEntryKey() ? null : TDoubleIntMapDecorator.this.wrapKey(d0);
                        int i = this.it.value();
                        final Integer integer = i == TDoubleIntMapDecorator.this._map.getNoEntryValue() ? null : TDoubleIntMapDecorator.this.wrapValue(i);

                        return new Entry<Double, Integer>() {
                            private Integer val = integer;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(odouble) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Double getKey() {
                                return odouble;
                            }

                            public Integer getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return odouble.hashCode() + this.val.hashCode();
                            }

                            public Integer setValue(Integer integer1) {
                                this.val = integer1;
                                return TDoubleIntMapDecorator.this.put(odouble, integer1);
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

            public boolean add(Entry<Double, Integer> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Double odouble = (Double) ((Entry) object).getKey();

                    TDoubleIntMapDecorator.this._map.remove(TDoubleIntMapDecorator.this.unwrapKey(odouble));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Double, Integer>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TDoubleIntMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Integer && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Double, ? extends Integer> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Double) entry.getKey(), (Integer) entry.getValue());
        }

    }

    protected Double wrapKey(double d0) {
        return d0;
    }

    protected double unwrapKey(Object object) {
        return (Double) object;
    }

    protected Integer wrapValue(int i) {
        return i;
    }

    protected int unwrapValue(Object object) {
        return (Integer) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TDoubleIntMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
