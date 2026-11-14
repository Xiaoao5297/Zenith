package gnu.trove.decorator;

import gnu.trove.iterator.TByteShortIterator;
import gnu.trove.map.TByteShortMap;
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

public class TByteShortMapDecorator extends AbstractMap<Byte, Short> implements Map<Byte, Short>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TByteShortMap _map;

    public TByteShortMapDecorator() {}

    public TByteShortMapDecorator(TByteShortMap tbyteshortmap) {
        this._map = tbyteshortmap;
    }

    public TByteShortMap getMap() {
        return this._map;
    }

    public Short put(Byte obyte, Short oshort) {
        byte b0;

        if (obyte == null) {
            b0 = this._map.getNoEntryKey();
        } else {
            b0 = this.unwrapKey(obyte);
        }

        short short0;

        if (oshort == null) {
            short0 = this._map.getNoEntryValue();
        } else {
            short0 = this.unwrapValue(oshort);
        }

        short short1 = this._map.put(b0, short0);

        return short1 == this._map.getNoEntryValue() ? null : this.wrapValue(short1);
    }

    public Short get(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey(object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        short short0 = this._map.get(b0);

        return short0 == this._map.getNoEntryValue() ? null : this.wrapValue(short0);
    }

    public void clear() {
        this._map.clear();
    }

    public Short remove(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey(object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        short short0 = this._map.remove(b0);

        return short0 == this._map.getNoEntryValue() ? null : this.wrapValue(short0);
    }

    public Set<Entry<Byte, Short>> entrySet() {
        return new AbstractSet<Entry<Byte, Short>>() {
            public int size() {
                return TByteShortMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TByteShortMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TByteShortMapDecorator.this.containsKey(object1) && TByteShortMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Byte, Short>> iterator() {
                return new Iterator<Entry<Byte, Short>>() {
                    private final TByteShortIterator it;

                    {
                        this.it = TByteShortMapDecorator.this._map.iterator();
                    }

                    public Entry<Byte, Short> next() {
                        this.it.advance();
                        byte b0 = this.it.key();
                        final Byte obyte = b0 == TByteShortMapDecorator.this._map.getNoEntryKey() ? null : TByteShortMapDecorator.this.wrapKey(b0);
                        short short0 = this.it.value();
                        final Short oshort = short0 == TByteShortMapDecorator.this._map.getNoEntryValue() ? null : TByteShortMapDecorator.this.wrapValue(short0);

                        return new Entry<Byte, Short>() {
                            private Short val = oshort;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(obyte) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Byte getKey() {
                                return obyte;
                            }

                            public Short getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return obyte.hashCode() + this.val.hashCode();
                            }

                            public Short setValue(Short oshort1) {
                                this.val = oshort1;
                                return TByteShortMapDecorator.this.put(obyte, oshort1);
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

            public boolean add(Entry<Byte, Short> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Byte obyte = (Byte) ((Entry) object).getKey();

                    TByteShortMapDecorator.this._map.remove(TByteShortMapDecorator.this.unwrapKey(obyte));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Byte, Short>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TByteShortMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Short && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Byte, ? extends Short> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), (Short) entry.getValue());
        }

    }

    protected Byte wrapKey(byte b0) {
        return b0;
    }

    protected byte unwrapKey(Object object) {
        return (Byte) object;
    }

    protected Short wrapValue(short short0) {
        return short0;
    }

    protected short unwrapValue(Object object) {
        return (Short) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TByteShortMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
