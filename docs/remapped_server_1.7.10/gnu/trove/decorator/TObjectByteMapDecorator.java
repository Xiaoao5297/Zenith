package gnu.trove.decorator;

import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.map.TObjectByteMap;
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

public class TObjectByteMapDecorator<K> extends AbstractMap<K, Byte> implements Map<K, Byte>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TObjectByteMap<K> _map;

    public TObjectByteMapDecorator() {}

    public TObjectByteMapDecorator(TObjectByteMap<K> tobjectbytemap) {
        this._map = tobjectbytemap;
    }

    public TObjectByteMap<K> getMap() {
        return this._map;
    }

    public Byte put(K k0, Byte obyte) {
        return obyte == null ? this.wrapValue(this._map.put(k0, this._map.getNoEntryValue())) : this.wrapValue(this._map.put(k0, this.unwrapValue(obyte)));
    }

    public Byte get(Object object) {
        byte b0 = this._map.get(object);

        return b0 == this._map.getNoEntryValue() ? null : this.wrapValue(b0);
    }

    public void clear() {
        this._map.clear();
    }

    public Byte remove(Object object) {
        byte b0 = this._map.remove(object);

        return b0 == this._map.getNoEntryValue() ? null : this.wrapValue(b0);
    }

    public Set<Entry<K, Byte>> entrySet() {
        return new AbstractSet<Entry<K, Byte>>() {
            public int size() {
                return TObjectByteMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TObjectByteMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TObjectByteMapDecorator.this.containsKey(object1) && TObjectByteMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<K, Byte>> iterator() {
                return new Iterator<Entry<K, Byte>>() {
                    private final TObjectByteIterator<K> it;

                    {
                        this.it = TObjectByteMapDecorator.this._map.iterator();
                    }

                    public Entry<K, Byte> next() {
                        this.it.advance();
                        final Object object = this.it.key();
                        final Byte obyte = TObjectByteMapDecorator.this.wrapValue(this.it.value());

                        return new Entry<K, Byte>() {
                            private Byte val = obyte;

                            public boolean equals(Object object1) {
                                return object1 instanceof Entry && ((Entry) object1).getKey().equals(object) && ((Entry) object1).getValue().equals(this.val);
                            }

                            public K getKey() {
                                return object;
                            }

                            public Byte getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return object.hashCode() + this.val.hashCode();
                            }

                            public Byte setValue(Byte obyte1) {
                                this.val = obyte1;
                                return TObjectByteMapDecorator.this.put(object, obyte1);
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

            public boolean add(Entry<K, Byte> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Object object1 = ((Entry) object).getKey();

                    TObjectByteMapDecorator.this._map.remove(object1);
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<K, Byte>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TObjectByteMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Byte && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return this._map.containsKey(object);
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this._map.size() == 0;
    }

    public void putAll(Map<? extends K, ? extends Byte> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Byte) entry.getValue());
        }

    }

    protected Byte wrapValue(byte b0) {
        return b0;
    }

    protected byte unwrapValue(Object object) {
        return (Byte) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TObjectByteMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
