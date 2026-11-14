package gnu.trove.decorator;

import gnu.trove.iterator.TFloatByteIterator;
import gnu.trove.map.TFloatByteMap;
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

public class TFloatByteMapDecorator extends AbstractMap<Float, Byte> implements Map<Float, Byte>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TFloatByteMap _map;

    public TFloatByteMapDecorator() {}

    public TFloatByteMapDecorator(TFloatByteMap tfloatbytemap) {
        this._map = tfloatbytemap;
    }

    public TFloatByteMap getMap() {
        return this._map;
    }

    public Byte put(Float ofloat, Byte obyte) {
        float f;

        if (ofloat == null) {
            f = this._map.getNoEntryKey();
        } else {
            f = this.unwrapKey(ofloat);
        }

        byte b0;

        if (obyte == null) {
            b0 = this._map.getNoEntryValue();
        } else {
            b0 = this.unwrapValue(obyte);
        }

        byte b1 = this._map.put(f, b0);

        return b1 == this._map.getNoEntryValue() ? null : this.wrapValue(b1);
    }

    public Byte get(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey(object);
        } else {
            f = this._map.getNoEntryKey();
        }

        byte b0 = this._map.get(f);

        return b0 == this._map.getNoEntryValue() ? null : this.wrapValue(b0);
    }

    public void clear() {
        this._map.clear();
    }

    public Byte remove(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey(object);
        } else {
            f = this._map.getNoEntryKey();
        }

        byte b0 = this._map.remove(f);

        return b0 == this._map.getNoEntryValue() ? null : this.wrapValue(b0);
    }

    public Set<Entry<Float, Byte>> entrySet() {
        return new AbstractSet<Entry<Float, Byte>>() {
            public int size() {
                return TFloatByteMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TFloatByteMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TFloatByteMapDecorator.this.containsKey(object1) && TFloatByteMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Float, Byte>> iterator() {
                return new Iterator<Entry<Float, Byte>>() {
                    private final TFloatByteIterator it;

                    {
                        this.it = TFloatByteMapDecorator.this._map.iterator();
                    }

                    public Entry<Float, Byte> next() {
                        this.it.advance();
                        float f = this.it.key();
                        final Float ofloat = f == TFloatByteMapDecorator.this._map.getNoEntryKey() ? null : TFloatByteMapDecorator.this.wrapKey(f);
                        byte b0 = this.it.value();
                        final Byte obyte = b0 == TFloatByteMapDecorator.this._map.getNoEntryValue() ? null : TFloatByteMapDecorator.this.wrapValue(b0);

                        return new Entry<Float, Byte>() {
                            private Byte val = obyte;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(ofloat) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Float getKey() {
                                return ofloat;
                            }

                            public Byte getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return ofloat.hashCode() + this.val.hashCode();
                            }

                            public Byte setValue(Byte obyte1) {
                                this.val = obyte1;
                                return TFloatByteMapDecorator.this.put(ofloat, obyte1);
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

            public boolean add(Entry<Float, Byte> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Float ofloat = (Float) ((Entry) object).getKey();

                    TFloatByteMapDecorator.this._map.remove(TFloatByteMapDecorator.this.unwrapKey(ofloat));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Float, Byte>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TFloatByteMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Byte && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Float, ? extends Byte> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Float) entry.getKey(), (Byte) entry.getValue());
        }

    }

    protected Float wrapKey(float f) {
        return f;
    }

    protected float unwrapKey(Object object) {
        return (Float) object;
    }

    protected Byte wrapValue(byte b0) {
        return b0;
    }

    protected byte unwrapValue(Object object) {
        return (Byte) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TFloatByteMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
