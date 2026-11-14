package gnu.trove.decorator;

import gnu.trove.iterator.TByteLongIterator;
import gnu.trove.map.TByteLongMap;
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

public class TByteLongMapDecorator extends AbstractMap<Byte, Long> implements Map<Byte, Long>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TByteLongMap _map;

    public TByteLongMapDecorator() {}

    public TByteLongMapDecorator(TByteLongMap tbytelongmap) {
        this._map = tbytelongmap;
    }

    public TByteLongMap getMap() {
        return this._map;
    }

    public Long put(Byte obyte, Long olong) {
        byte b0;

        if (obyte == null) {
            b0 = this._map.getNoEntryKey();
        } else {
            b0 = this.unwrapKey(obyte);
        }

        long i;

        if (olong == null) {
            i = this._map.getNoEntryValue();
        } else {
            i = this.unwrapValue(olong);
        }

        long j = this._map.put(b0, i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public Long get(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey(object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        long i = this._map.get(b0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public void clear() {
        this._map.clear();
    }

    public Long remove(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey(object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        long i = this._map.remove(b0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public Set<Entry<Byte, Long>> entrySet() {
        return new AbstractSet<Entry<Byte, Long>>() {
            public int size() {
                return TByteLongMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TByteLongMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TByteLongMapDecorator.this.containsKey(object1) && TByteLongMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Byte, Long>> iterator() {
                return new Iterator<Entry<Byte, Long>>() {
                    private final TByteLongIterator it;

                    {
                        this.it = TByteLongMapDecorator.this._map.iterator();
                    }

                    public Entry<Byte, Long> next() {
                        this.it.advance();
                        byte b0 = this.it.key();
                        final Byte obyte = b0 == TByteLongMapDecorator.this._map.getNoEntryKey() ? null : TByteLongMapDecorator.this.wrapKey(b0);
                        long i = this.it.value();
                        final Long olong = i == TByteLongMapDecorator.this._map.getNoEntryValue() ? null : TByteLongMapDecorator.this.wrapValue(i);

                        return new Entry<Byte, Long>() {
                            private Long val = olong;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(obyte) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Byte getKey() {
                                return obyte;
                            }

                            public Long getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return obyte.hashCode() + this.val.hashCode();
                            }

                            public Long setValue(Long olong1) {
                                this.val = olong1;
                                return TByteLongMapDecorator.this.put(obyte, olong1);
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

            public boolean add(Entry<Byte, Long> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Byte obyte = (Byte) ((Entry) object).getKey();

                    TByteLongMapDecorator.this._map.remove(TByteLongMapDecorator.this.unwrapKey(obyte));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Byte, Long>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TByteLongMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Long && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Byte, ? extends Long> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), (Long) entry.getValue());
        }

    }

    protected Byte wrapKey(byte b0) {
        return b0;
    }

    protected byte unwrapKey(Object object) {
        return (Byte) object;
    }

    protected Long wrapValue(long i) {
        return i;
    }

    protected long unwrapValue(Object object) {
        return (Long) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TByteLongMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
