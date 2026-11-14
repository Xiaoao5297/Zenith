package gnu.trove.decorator;

import gnu.trove.iterator.TByteDoubleIterator;
import gnu.trove.map.TByteDoubleMap;
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

public class TByteDoubleMapDecorator extends AbstractMap<Byte, Double> implements Map<Byte, Double>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TByteDoubleMap _map;

    public TByteDoubleMapDecorator() {}

    public TByteDoubleMapDecorator(TByteDoubleMap tbytedoublemap) {
        this._map = tbytedoublemap;
    }

    public TByteDoubleMap getMap() {
        return this._map;
    }

    public Double put(Byte obyte, Double odouble) {
        byte b0;

        if (obyte == null) {
            b0 = this._map.getNoEntryKey();
        } else {
            b0 = this.unwrapKey(obyte);
        }

        double d0;

        if (odouble == null) {
            d0 = this._map.getNoEntryValue();
        } else {
            d0 = this.unwrapValue(odouble);
        }

        double d1 = this._map.put(b0, d0);

        return d1 == this._map.getNoEntryValue() ? null : this.wrapValue(d1);
    }

    public Double get(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey(object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        double d0 = this._map.get(b0);

        return d0 == this._map.getNoEntryValue() ? null : this.wrapValue(d0);
    }

    public void clear() {
        this._map.clear();
    }

    public Double remove(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey(object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        double d0 = this._map.remove(b0);

        return d0 == this._map.getNoEntryValue() ? null : this.wrapValue(d0);
    }

    public Set<Entry<Byte, Double>> entrySet() {
        return new AbstractSet<Entry<Byte, Double>>() {
            public int size() {
                return TByteDoubleMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TByteDoubleMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TByteDoubleMapDecorator.this.containsKey(object1) && TByteDoubleMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Byte, Double>> iterator() {
                return new Iterator<Entry<Byte, Double>>() {
                    private final TByteDoubleIterator it;

                    {
                        this.it = TByteDoubleMapDecorator.this._map.iterator();
                    }

                    public Entry<Byte, Double> next() {
                        this.it.advance();
                        byte b0 = this.it.key();
                        final Byte obyte = b0 == TByteDoubleMapDecorator.this._map.getNoEntryKey() ? null : TByteDoubleMapDecorator.this.wrapKey(b0);
                        double d0 = this.it.value();
                        final Double odouble = d0 == TByteDoubleMapDecorator.this._map.getNoEntryValue() ? null : TByteDoubleMapDecorator.this.wrapValue(d0);

                        return new Entry<Byte, Double>() {
                            private Double val = odouble;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(obyte) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Byte getKey() {
                                return obyte;
                            }

                            public Double getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return obyte.hashCode() + this.val.hashCode();
                            }

                            public Double setValue(Double odouble1) {
                                this.val = odouble1;
                                return TByteDoubleMapDecorator.this.put(obyte, odouble1);
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

            public boolean add(Entry<Byte, Double> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Byte obyte = (Byte) ((Entry) object).getKey();

                    TByteDoubleMapDecorator.this._map.remove(TByteDoubleMapDecorator.this.unwrapKey(obyte));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Byte, Double>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TByteDoubleMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Double && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Byte && this._map.containsKey(this.unwrapKey(object));
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Byte, ? extends Double> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), (Double) entry.getValue());
        }

    }

    protected Byte wrapKey(byte b0) {
        return b0;
    }

    protected byte unwrapKey(Object object) {
        return (Byte) object;
    }

    protected Double wrapValue(double d0) {
        return d0;
    }

    protected double unwrapValue(Object object) {
        return (Double) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TByteDoubleMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
