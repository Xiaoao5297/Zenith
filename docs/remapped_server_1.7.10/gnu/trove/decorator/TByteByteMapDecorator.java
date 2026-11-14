package gnu.trove.decorator;

import gnu.trove.iterator.TByteByteIterator;
import gnu.trove.map.TByteByteMap;
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

public class TByteByteMapDecorator extends AbstractMap<Byte, Byte> implements Map<Byte, Byte>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TByteByteMap _map;

    public TByteByteMapDecorator() {}

    public TByteByteMapDecorator(TByteByteMap tbytebytemap) {
        this._map = tbytebytemap;
    }

    public TByteByteMap getMap() {
        return this._map;
    }

    public Byte put(Byte obyte, Byte obyte1) {
        byte b0;

        if (obyte == null) {
            b0 = this._map.getNoEntryKey();
        } else {
            b0 = this.unwrapKey(obyte);
        }

        byte b1;

        if (obyte1 == null) {
            b1 = this._map.getNoEntryValue();
        } else {
            b1 = this.unwrapValue(obyte1);
        }

        byte b2 = this._map.put(b0, b1);

        return b2 == this._map.getNoEntryValue() ? null : this.wrapValue(b2);
    }

    public Byte get(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey(object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        byte b1 = this._map.get(b0);

        return b1 == this._map.getNoEntryValue() ? null : this.wrapValue(b1);
    }

    public void clear() {
        this._map.clear();
    }

    public Byte remove(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey(object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        byte b1 = this._map.remove(b0);

        return b1 == this._map.getNoEntryValue() ? null : this.wrapValue(b1);
    }

    public Set<Entry<Byte, Byte>> entrySet() {
        return new AbstractSet<Entry<Byte, Byte>>() {
            public int size() {
                return TByteByteMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TByteByteMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TByteByteMapDecorator.this.containsKey(object1) && TByteByteMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Byte, Byte>> iterator() {
                return new Iterator<Entry<Byte, Byte>>() {
                    private final TByteByteIterator it;

                    {
                        this.it = TByteByteMapDecorator.this._map.iterator();
                    }

                    public Entry<Byte, Byte> next() {
                        this.it.advance();
                        byte b0 = this.it.key();
                        final Byte obyte = b0 == TByteByteMapDecorator.this._map.getNoEntryKey() ? null : TByteByteMapDecorator.this.wrapKey(b0);
                        byte b1 = this.it.value();
                        final Byte obyte1 = b1 == TByteByteMapDecorator.this._map.getNoEntryValue() ? null : TByteByteMapDecorator.this.wrapValue(b1);

                        return new Entry<Byte, Byte>() {
                            private Byte val = obyte1;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(obyte) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Byte getKey() {
                                return obyte;
                            }

                            public Byte getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return obyte.hashCode() + this.val.hashCode();
                            }

                            public Byte setValue(Byte obyte2) {
                                this.val = obyte2;
                                return TByteByteMapDecorator.this.put(obyte, obyte2);
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

            public boolean add(Entry<Byte, Byte> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Byte obyte = (Byte) ((Entry) object).getKey();

                    TByteByteMapDecorator.this._map.remove(TByteByteMapDecorator.this.unwrapKey(obyte));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Byte, Byte>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TByteByteMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Byte && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Byte, ? extends Byte> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), (Byte) entry.getValue());
        }

    }

    protected Byte wrapKey(byte b0) {
        return b0;
    }

    protected byte unwrapKey(Object object) {
        return (Byte) object;
    }

    protected Byte wrapValue(byte b0) {
        return b0;
    }

    protected byte unwrapValue(Object object) {
        return (Byte) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TByteByteMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
