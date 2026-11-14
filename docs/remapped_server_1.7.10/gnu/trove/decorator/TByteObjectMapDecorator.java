package gnu.trove.decorator;

import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.map.TByteObjectMap;
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

public class TByteObjectMapDecorator<V> extends AbstractMap<Byte, V> implements Map<Byte, V>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TByteObjectMap<V> _map;

    public TByteObjectMapDecorator() {}

    public TByteObjectMapDecorator(TByteObjectMap<V> tbyteobjectmap) {
        this._map = tbyteobjectmap;
    }

    public TByteObjectMap<V> getMap() {
        return this._map;
    }

    public V put(Byte obyte, V v0) {
        byte b0;

        if (obyte == null) {
            b0 = this._map.getNoEntryKey();
        } else {
            b0 = this.unwrapKey(obyte);
        }

        return this._map.put(b0, v0);
    }

    public V get(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey((Byte) object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        return this._map.get(b0);
    }

    public void clear() {
        this._map.clear();
    }

    public V remove(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey((Byte) object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        return this._map.remove(b0);
    }

    public Set<Entry<Byte, V>> entrySet() {
        return new AbstractSet<Entry<Byte, V>>() {
            public int size() {
                return TByteObjectMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TByteObjectMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TByteObjectMapDecorator.this.containsKey(object1) && TByteObjectMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Byte, V>> iterator() {
                return new Iterator<Entry<Byte, V>>() {
                    private final TByteObjectIterator<V> it;

                    {
                        this.it = TByteObjectMapDecorator.this._map.iterator();
                    }

                    public Entry<Byte, V> next() {
                        this.it.advance();
                        byte b0 = this.it.key();
                        final Byte obyte = b0 == TByteObjectMapDecorator.this._map.getNoEntryKey() ? null : TByteObjectMapDecorator.this.wrapKey(b0);
                        final Object object = this.it.value();

                        return new Entry<Byte, V>() {
                            private V val = object;

                            public boolean equals(Object object1) {
                                return object1 instanceof Entry && ((Entry) object1).getKey().equals(obyte) && ((Entry) object1).getValue().equals(this.val);
                            }

                            public Byte getKey() {
                                return obyte;
                            }

                            public V getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return obyte.hashCode() + this.val.hashCode();
                            }

                            public V setValue(V v0) {
                                this.val = v0;
                                return TByteObjectMapDecorator.this.put(obyte, v0);
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

            public boolean add(Entry<Byte, V> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Byte obyte = (Byte) ((Entry) object).getKey();

                    TByteObjectMapDecorator.this._map.remove(TByteObjectMapDecorator.this.unwrapKey(obyte));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Byte, V>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TByteObjectMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return this._map.containsValue(object);
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Byte && this._map.containsKey((Byte) object);
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Byte, ? extends V> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), entry.getValue());
        }

    }

    protected Byte wrapKey(byte b0) {
        return b0;
    }

    protected byte unwrapKey(Byte obyte) {
        return obyte;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TByteObjectMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
