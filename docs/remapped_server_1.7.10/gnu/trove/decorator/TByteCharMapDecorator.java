package gnu.trove.decorator;

import gnu.trove.iterator.TByteCharIterator;
import gnu.trove.map.TByteCharMap;
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

public class TByteCharMapDecorator extends AbstractMap<Byte, Character> implements Map<Byte, Character>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TByteCharMap _map;

    public TByteCharMapDecorator() {}

    public TByteCharMapDecorator(TByteCharMap tbytecharmap) {
        this._map = tbytecharmap;
    }

    public TByteCharMap getMap() {
        return this._map;
    }

    public Character put(Byte obyte, Character character) {
        byte b0;

        if (obyte == null) {
            b0 = this._map.getNoEntryKey();
        } else {
            b0 = this.unwrapKey(obyte);
        }

        char c0;

        if (character == null) {
            c0 = this._map.getNoEntryValue();
        } else {
            c0 = this.unwrapValue(character);
        }

        char c1 = this._map.put(b0, c0);

        return c1 == this._map.getNoEntryValue() ? null : this.wrapValue(c1);
    }

    public Character get(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey(object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        char c0 = this._map.get(b0);

        return c0 == this._map.getNoEntryValue() ? null : this.wrapValue(c0);
    }

    public void clear() {
        this._map.clear();
    }

    public Character remove(Object object) {
        byte b0;

        if (object != null) {
            if (!(object instanceof Byte)) {
                return null;
            }

            b0 = this.unwrapKey(object);
        } else {
            b0 = this._map.getNoEntryKey();
        }

        char c0 = this._map.remove(b0);

        return c0 == this._map.getNoEntryValue() ? null : this.wrapValue(c0);
    }

    public Set<Entry<Byte, Character>> entrySet() {
        return new AbstractSet<Entry<Byte, Character>>() {
            public int size() {
                return TByteCharMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TByteCharMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TByteCharMapDecorator.this.containsKey(object1) && TByteCharMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Byte, Character>> iterator() {
                return new Iterator<Entry<Byte, Character>>() {
                    private final TByteCharIterator it;

                    {
                        this.it = TByteCharMapDecorator.this._map.iterator();
                    }

                    public Entry<Byte, Character> next() {
                        this.it.advance();
                        byte b0 = this.it.key();
                        final Byte obyte = b0 == TByteCharMapDecorator.this._map.getNoEntryKey() ? null : TByteCharMapDecorator.this.wrapKey(b0);
                        char c0 = this.it.value();
                        final Character character = c0 == TByteCharMapDecorator.this._map.getNoEntryValue() ? null : TByteCharMapDecorator.this.wrapValue(c0);

                        return new Entry<Byte, Character>() {
                            private Character val = character;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(obyte) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Byte getKey() {
                                return obyte;
                            }

                            public Character getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return obyte.hashCode() + this.val.hashCode();
                            }

                            public Character setValue(Character character1) {
                                this.val = character1;
                                return TByteCharMapDecorator.this.put(obyte, character1);
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

            public boolean add(Entry<Byte, Character> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Byte obyte = (Byte) ((Entry) object).getKey();

                    TByteCharMapDecorator.this._map.remove(TByteCharMapDecorator.this.unwrapKey(obyte));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Byte, Character>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TByteCharMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Character && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Byte, ? extends Character> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), (Character) entry.getValue());
        }

    }

    protected Byte wrapKey(byte b0) {
        return b0;
    }

    protected byte unwrapKey(Object object) {
        return (Byte) object;
    }

    protected Character wrapValue(char c0) {
        return c0;
    }

    protected char unwrapValue(Object object) {
        return (Character) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TByteCharMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
