package gnu.trove.decorator;

import gnu.trove.iterator.TIntByteIterator;
import gnu.trove.map.TIntByteMap;
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

public class TIntByteMapDecorator extends AbstractMap<Integer, Byte> implements Map<Integer, Byte>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TIntByteMap _map;

    public TIntByteMapDecorator() {}

    public TIntByteMapDecorator(TIntByteMap tintbytemap) {
        this._map = tintbytemap;
    }

    public TIntByteMap getMap() {
        return this._map;
    }

    public Byte put(Integer integer, Byte obyte) {
        int i;

        if (integer == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(integer);
        }

        byte b0;

        if (obyte == null) {
            b0 = this._map.getNoEntryValue();
        } else {
            b0 = this.unwrapValue(obyte);
        }

        byte b1 = this._map.put(i, b0);

        return b1 == this._map.getNoEntryValue() ? null : this.wrapValue(b1);
    }

    public Byte get(Object object) {
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        byte b0 = this._map.get(i);

        return b0 == this._map.getNoEntryValue() ? null : this.wrapValue(b0);
    }

    public void clear() {
        this._map.clear();
    }

    public Byte remove(Object object) {
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        byte b0 = this._map.remove(i);

        return b0 == this._map.getNoEntryValue() ? null : this.wrapValue(b0);
    }

    public Set<Entry<Integer, Byte>> entrySet() {
        return new AbstractSet<Entry<Integer, Byte>>() {
            public int size() {
                return TIntByteMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TIntByteMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TIntByteMapDecorator.this.containsKey(object1) && TIntByteMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Integer, Byte>> iterator() {
                return new Iterator<Entry<Integer, Byte>>() {
                    private final TIntByteIterator it;

                    {
                        this.it = TIntByteMapDecorator.this._map.iterator();
                    }

                    public Entry<Integer, Byte> next() {
                        this.it.advance();
                        int i = this.it.key();
                        final Integer integer = i == TIntByteMapDecorator.this._map.getNoEntryKey() ? null : TIntByteMapDecorator.this.wrapKey(i);
                        byte b0 = this.it.value();
                        final Byte obyte = b0 == TIntByteMapDecorator.this._map.getNoEntryValue() ? null : TIntByteMapDecorator.this.wrapValue(b0);

                        return new Entry<Integer, Byte>() {
                            private Byte val = obyte;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(integer) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Integer getKey() {
                                return integer;
                            }

                            public Byte getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return integer.hashCode() + this.val.hashCode();
                            }

                            public Byte setValue(Byte obyte1) {
                                this.val = obyte1;
                                return TIntByteMapDecorator.this.put(integer, obyte1);
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

            public boolean add(Entry<Integer, Byte> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Integer integer = (Integer) ((Entry) object).getKey();

                    TIntByteMapDecorator.this._map.remove(TIntByteMapDecorator.this.unwrapKey(integer));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Integer, Byte>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TIntByteMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Byte && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Integer, ? extends Byte> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Integer) entry.getKey(), (Byte) entry.getValue());
        }

    }

    protected Integer wrapKey(int i) {
        return i;
    }

    protected int unwrapKey(Object object) {
        return (Integer) object;
    }

    protected Byte wrapValue(byte b0) {
        return b0;
    }

    protected byte unwrapValue(Object object) {
        return (Byte) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TIntByteMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
