package gnu.trove.decorator;

import gnu.trove.iterator.TShortByteIterator;
import gnu.trove.map.TShortByteMap;
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

public class TShortByteMapDecorator extends AbstractMap<Short, Byte> implements Map<Short, Byte>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TShortByteMap _map;

    public TShortByteMapDecorator() {}

    public TShortByteMapDecorator(TShortByteMap tshortbytemap) {
        this._map = tshortbytemap;
    }

    public TShortByteMap getMap() {
        return this._map;
    }

    public Byte put(Short oshort, Byte obyte) {
        short short0;

        if (oshort == null) {
            short0 = this._map.getNoEntryKey();
        } else {
            short0 = this.unwrapKey(oshort);
        }

        byte b0;

        if (obyte == null) {
            b0 = this._map.getNoEntryValue();
        } else {
            b0 = this.unwrapValue(obyte);
        }

        byte b1 = this._map.put(short0, b0);

        return b1 == this._map.getNoEntryValue() ? null : this.wrapValue(b1);
    }

    public Byte get(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey(object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        byte b0 = this._map.get(short0);

        return b0 == this._map.getNoEntryValue() ? null : this.wrapValue(b0);
    }

    public void clear() {
        this._map.clear();
    }

    public Byte remove(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey(object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        byte b0 = this._map.remove(short0);

        return b0 == this._map.getNoEntryValue() ? null : this.wrapValue(b0);
    }

    public Set<Entry<Short, Byte>> entrySet() {
        return new AbstractSet<Entry<Short, Byte>>() {
            public int size() {
                return TShortByteMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TShortByteMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TShortByteMapDecorator.this.containsKey(object1) && TShortByteMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Short, Byte>> iterator() {
                return new Iterator<Entry<Short, Byte>>() {
                    private final TShortByteIterator it;

                    {
                        this.it = TShortByteMapDecorator.this._map.iterator();
                    }

                    public Entry<Short, Byte> next() {
                        this.it.advance();
                        short short0 = this.it.key();
                        final Short oshort = short0 == TShortByteMapDecorator.this._map.getNoEntryKey() ? null : TShortByteMapDecorator.this.wrapKey(short0);
                        byte b0 = this.it.value();
                        final Byte obyte = b0 == TShortByteMapDecorator.this._map.getNoEntryValue() ? null : TShortByteMapDecorator.this.wrapValue(b0);

                        return new Entry<Short, Byte>() {
                            private Byte val = obyte;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(oshort) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Short getKey() {
                                return oshort;
                            }

                            public Byte getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return oshort.hashCode() + this.val.hashCode();
                            }

                            public Byte setValue(Byte obyte1) {
                                this.val = obyte1;
                                return TShortByteMapDecorator.this.put(oshort, obyte1);
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

            public boolean add(Entry<Short, Byte> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Short oshort = (Short) ((Entry) object).getKey();

                    TShortByteMapDecorator.this._map.remove(TShortByteMapDecorator.this.unwrapKey(oshort));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Short, Byte>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TShortByteMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Byte && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Short, ? extends Byte> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Short) entry.getKey(), (Byte) entry.getValue());
        }

    }

    protected Short wrapKey(short short0) {
        return short0;
    }

    protected short unwrapKey(Object object) {
        return (Short) object;
    }

    protected Byte wrapValue(byte b0) {
        return b0;
    }

    protected byte unwrapValue(Object object) {
        return (Byte) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TShortByteMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
