package gnu.trove.decorator;

import gnu.trove.iterator.TByteFloatIterator;
import gnu.trove.map.TByteFloatMap;
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

public class TByteFloatMapDecorator extends AbstractMap<Byte, Float> implements Map<Byte, Float>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TByteFloatMap _map;

    public TByteFloatMapDecorator() {}

    public TByteFloatMapDecorator(TByteFloatMap tbytefloatmap) {
        this._map = tbytefloatmap;
    }

    public TByteFloatMap getMap() {
        return this._map;
    }

    public Float put(Byte obyte, Float ofloat) {
        byte b0;

        if (obyte == null) {
            b0 = this._map.getNoEntryKey();
        } else {
            b0 = this.unwrapKey(obyte);
        }

        float f;

        if (ofloat == null) {
            f = this._map.getNoEntryValue();
        } else {
            f = this.unwrapValue(ofloat);
        }

        float f1 = this._map.put(b0, f);

        return f1 == this._map.getNoEntryValue() ? null : this.wrapValue(f1);
    }

    public Float get(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey(object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        float f = this._map.get(b0);

        return f == this._map.getNoEntryValue() ? null : this.wrapValue(f);
    }

    public void clear() {
        this._map.clear();
    }

    public Float remove(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey(object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        float f = this._map.remove(b0);

        return f == this._map.getNoEntryValue() ? null : this.wrapValue(f);
    }

    public Set<Entry<Byte, Float>> entrySet() {
        return new AbstractSet<Entry<Byte, Float>>() {
            public int size() {
                return TByteFloatMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TByteFloatMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TByteFloatMapDecorator.this.containsKey(object1) && TByteFloatMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Byte, Float>> iterator() {
                return new Iterator<Entry<Byte, Float>>() {
                    private final TByteFloatIterator it;

                    {
                        this.it = TByteFloatMapDecorator.this._map.iterator();
                    }

                    public Entry<Byte, Float> next() {
                        this.it.advance();
                        byte b0 = this.it.key();
                        final Byte obyte = b0 == TByteFloatMapDecorator.this._map.getNoEntryKey() ? null : TByteFloatMapDecorator.this.wrapKey(b0);
                        float f = this.it.value();
                        final Float ofloat = f == TByteFloatMapDecorator.this._map.getNoEntryValue() ? null : TByteFloatMapDecorator.this.wrapValue(f);

                        return new Entry<Byte, Float>() {
                            private Float val = ofloat;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(obyte) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Byte getKey() {
                                return obyte;
                            }

                            public Float getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return obyte.hashCode() + this.val.hashCode();
                            }

                            public Float setValue(Float ofloat1) {
                                this.val = ofloat1;
                                return TByteFloatMapDecorator.this.put(obyte, ofloat1);
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

            public boolean add(Entry<Byte, Float> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Byte obyte = (Byte) ((Entry) object).getKey();

                    TByteFloatMapDecorator.this._map.remove(TByteFloatMapDecorator.this.unwrapKey(obyte));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Byte, Float>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TByteFloatMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Float && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Byte, ? extends Float> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), (Float) entry.getValue());
        }

    }

    protected Byte wrapKey(byte b0) {
        return b0;
    }

    protected byte unwrapKey(Object object) {
        return (Byte) object;
    }

    protected Float wrapValue(float f) {
        return f;
    }

    protected float unwrapValue(Object object) {
        return (Float) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TByteFloatMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
