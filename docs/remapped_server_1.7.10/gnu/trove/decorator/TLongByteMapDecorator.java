package gnu.trove.decorator;

import gnu.trove.iterator.TLongByteIterator;
import gnu.trove.map.TLongByteMap;
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

public class TLongByteMapDecorator extends AbstractMap<Long, Byte> implements Map<Long, Byte>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TLongByteMap _map;

    public TLongByteMapDecorator() {}

    public TLongByteMapDecorator(TLongByteMap tlongbytemap) {
        this._map = tlongbytemap;
    }

    public TLongByteMap getMap() {
        return this._map;
    }

    public Byte put(Long olong, Byte obyte) {
        long i;

        if (olong == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(olong);
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
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
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
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        byte b0 = this._map.remove(i);

        return b0 == this._map.getNoEntryValue() ? null : this.wrapValue(b0);
    }

    public Set<Entry<Long, Byte>> entrySet() {
        return new AbstractSet<Entry<Long, Byte>>() {
            public int size() {
                return TLongByteMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TLongByteMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TLongByteMapDecorator.this.containsKey(object1) && TLongByteMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Long, Byte>> iterator() {
                return new Iterator<Entry<Long, Byte>>() {
                    private final TLongByteIterator it;

                    {
                        this.it = TLongByteMapDecorator.this._map.iterator();
                    }

                    public Entry<Long, Byte> next() {
                        this.it.advance();
                        long i = this.it.key();
                        final Long olong = i == TLongByteMapDecorator.this._map.getNoEntryKey() ? null : TLongByteMapDecorator.this.wrapKey(i);
                        byte b0 = this.it.value();
                        final Byte obyte = b0 == TLongByteMapDecorator.this._map.getNoEntryValue() ? null : TLongByteMapDecorator.this.wrapValue(b0);

                        return new Entry<Long, Byte>() {
                            private Byte val = obyte;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(olong) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Long getKey() {
                                return olong;
                            }

                            public Byte getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return olong.hashCode() + this.val.hashCode();
                            }

                            public Byte setValue(Byte obyte1) {
                                this.val = obyte1;
                                return TLongByteMapDecorator.this.put(olong, obyte1);
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

            public boolean add(Entry<Long, Byte> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Long olong = (Long) ((Entry) object).getKey();

                    TLongByteMapDecorator.this._map.remove(TLongByteMapDecorator.this.unwrapKey(olong));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Long, Byte>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TLongByteMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Byte && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Long && this._map.containsKey(this.unwrapKey(object));
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Long, ? extends Byte> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Long) entry.getKey(), (Byte) entry.getValue());
        }

    }

    protected Long wrapKey(long i) {
        return i;
    }

    protected long unwrapKey(Object object) {
        return (Long) object;
    }

    protected Byte wrapValue(byte b0) {
        return b0;
    }

    protected byte unwrapValue(Object object) {
        return (Byte) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TLongByteMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
