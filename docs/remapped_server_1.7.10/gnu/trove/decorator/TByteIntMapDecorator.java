package gnu.trove.decorator;

import gnu.trove.iterator.TByteIntIterator;
import gnu.trove.map.TByteIntMap;
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

public class TByteIntMapDecorator extends AbstractMap<Byte, Integer> implements Map<Byte, Integer>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TByteIntMap _map;

    public TByteIntMapDecorator() {}

    public TByteIntMapDecorator(TByteIntMap tbyteintmap) {
        this._map = tbyteintmap;
    }

    public TByteIntMap getMap() {
        return this._map;
    }

    public Integer put(Byte obyte, Integer integer) {
        byte b0;

        if (obyte == null) {
            b0 = this._map.getNoEntryKey();
        } else {
            b0 = this.unwrapKey(obyte);
        }

        int i;

        if (integer == null) {
            i = this._map.getNoEntryValue();
        } else {
            i = this.unwrapValue(integer);
        }

        int j = this._map.put(b0, i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public Integer get(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey(object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        int i = this._map.get(b0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public void clear() {
        this._map.clear();
    }

    public Integer remove(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey(object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        int i = this._map.remove(b0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public Set<Entry<Byte, Integer>> entrySet() {
        return new AbstractSet<Entry<Byte, Integer>>() {
            public int size() {
                return TByteIntMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TByteIntMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TByteIntMapDecorator.this.containsKey(object1) && TByteIntMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Byte, Integer>> iterator() {
                return new Iterator<Entry<Byte, Integer>>() {
                    private final TByteIntIterator it;

                    {
                        this.it = TByteIntMapDecorator.this._map.iterator();
                    }

                    public Entry<Byte, Integer> next() {
                        this.it.advance();
                        byte b0 = this.it.key();
                        final Byte obyte = b0 == TByteIntMapDecorator.this._map.getNoEntryKey() ? null : TByteIntMapDecorator.this.wrapKey(b0);
                        int i = this.it.value();
                        final Integer integer = i == TByteIntMapDecorator.this._map.getNoEntryValue() ? null : TByteIntMapDecorator.this.wrapValue(i);

                        return new Entry<Byte, Integer>() {
                            private Integer val = integer;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(obyte) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Byte getKey() {
                                return obyte;
                            }

                            public Integer getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return obyte.hashCode() + this.val.hashCode();
                            }

                            public Integer setValue(Integer integer1) {
                                this.val = integer1;
                                return TByteIntMapDecorator.this.put(obyte, integer1);
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

            public boolean add(Entry<Byte, Integer> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Byte obyte = (Byte) ((Entry) object).getKey();

                    TByteIntMapDecorator.this._map.remove(TByteIntMapDecorator.this.unwrapKey(obyte));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Byte, Integer>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TByteIntMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Integer && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Byte, ? extends Integer> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), (Integer) entry.getValue());
        }

    }

    protected Byte wrapKey(byte b0) {
        return b0;
    }

    protected byte unwrapKey(Object object) {
        return (Byte) object;
    }

    protected Integer wrapValue(int i) {
        return i;
    }

    protected int unwrapValue(Object object) {
        return (Integer) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TByteIntMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
