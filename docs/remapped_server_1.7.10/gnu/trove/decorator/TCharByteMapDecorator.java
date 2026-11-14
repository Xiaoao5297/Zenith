package gnu.trove.decorator;

import gnu.trove.iterator.TCharByteIterator;
import gnu.trove.map.TCharByteMap;
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

public class TCharByteMapDecorator extends AbstractMap<Character, Byte> implements Map<Character, Byte>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TCharByteMap _map;

    public TCharByteMapDecorator() {}

    public TCharByteMapDecorator(TCharByteMap tcharbytemap) {
        this._map = tcharbytemap;
    }

    public TCharByteMap getMap() {
        return this._map;
    }

    public Byte put(Character character, Byte obyte) {
        char c0;

        if (character == null) {
            c0 = this._map.getNoEntryKey();
        } else {
            c0 = this.unwrapKey(character);
        }

        byte b0;

        if (obyte == null) {
            b0 = this._map.getNoEntryValue();
        } else {
            b0 = this.unwrapValue(obyte);
        }

        byte b1 = this._map.put(c0, b0);

        return b1 == this._map.getNoEntryValue() ? null : this.wrapValue(b1);
    }

    public Byte get(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey(object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        byte b0 = this._map.get(c0);

        return b0 == this._map.getNoEntryValue() ? null : this.wrapValue(b0);
    }

    public void clear() {
        this._map.clear();
    }

    public Byte remove(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey(object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        byte b0 = this._map.remove(c0);

        return b0 == this._map.getNoEntryValue() ? null : this.wrapValue(b0);
    }

    public Set<Entry<Character, Byte>> entrySet() {
        return new AbstractSet<Entry<Character, Byte>>() {
            public int size() {
                return TCharByteMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TCharByteMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TCharByteMapDecorator.this.containsKey(object1) && TCharByteMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Character, Byte>> iterator() {
                return new Iterator<Entry<Character, Byte>>() {
                    private final TCharByteIterator it;

                    {
                        this.it = TCharByteMapDecorator.this._map.iterator();
                    }

                    public Entry<Character, Byte> next() {
                        this.it.advance();
                        char c0 = this.it.key();
                        final Character character = c0 == TCharByteMapDecorator.this._map.getNoEntryKey() ? null : TCharByteMapDecorator.this.wrapKey(c0);
                        byte b0 = this.it.value();
                        final Byte obyte = b0 == TCharByteMapDecorator.this._map.getNoEntryValue() ? null : TCharByteMapDecorator.this.wrapValue(b0);

                        return new Entry<Character, Byte>() {
                            private Byte val = obyte;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(character) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Character getKey() {
                                return character;
                            }

                            public Byte getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return character.hashCode() + this.val.hashCode();
                            }

                            public Byte setValue(Byte obyte1) {
                                this.val = obyte1;
                                return TCharByteMapDecorator.this.put(character, obyte1);
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

            public boolean add(Entry<Character, Byte> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Character character = (Character) ((Entry) object).getKey();

                    TCharByteMapDecorator.this._map.remove(TCharByteMapDecorator.this.unwrapKey(character));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Character, Byte>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TCharByteMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Byte && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Character && this._map.containsKey(this.unwrapKey(object));
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Character, ? extends Byte> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), (Byte) entry.getValue());
        }

    }

    protected Character wrapKey(char c0) {
        return c0;
    }

    protected char unwrapKey(Object object) {
        return (Character) object;
    }

    protected Byte wrapValue(byte b0) {
        return b0;
    }

    protected byte unwrapValue(Object object) {
        return (Byte) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TCharByteMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
