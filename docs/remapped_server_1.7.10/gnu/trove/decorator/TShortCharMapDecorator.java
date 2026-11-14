package gnu.trove.decorator;

import gnu.trove.iterator.TShortCharIterator;
import gnu.trove.map.TShortCharMap;
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

public class TShortCharMapDecorator extends AbstractMap<Short, Character> implements Map<Short, Character>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TShortCharMap _map;

    public TShortCharMapDecorator() {}

    public TShortCharMapDecorator(TShortCharMap tshortcharmap) {
        this._map = tshortcharmap;
    }

    public TShortCharMap getMap() {
        return this._map;
    }

    public Character put(Short oshort, Character character) {
        short short0;

        if (oshort == null) {
            short0 = this._map.getNoEntryKey();
        } else {
            short0 = this.unwrapKey(oshort);
        }

        char c0;

        if (character == null) {
            c0 = this._map.getNoEntryValue();
        } else {
            c0 = this.unwrapValue(character);
        }

        char c1 = this._map.put(short0, c0);

        return c1 == this._map.getNoEntryValue() ? null : this.wrapValue(c1);
    }

    public Character get(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey(object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        char c0 = this._map.get(short0);

        return c0 == this._map.getNoEntryValue() ? null : this.wrapValue(c0);
    }

    public void clear() {
        this._map.clear();
    }

    public Character remove(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey(object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        char c0 = this._map.remove(short0);

        return c0 == this._map.getNoEntryValue() ? null : this.wrapValue(c0);
    }

    public Set<Entry<Short, Character>> entrySet() {
        return new AbstractSet<Entry<Short, Character>>() {
            public int size() {
                return TShortCharMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TShortCharMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TShortCharMapDecorator.this.containsKey(object1) && TShortCharMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Short, Character>> iterator() {
                return new Iterator<Entry<Short, Character>>() {
                    private final TShortCharIterator it;

                    {
                        this.it = TShortCharMapDecorator.this._map.iterator();
                    }

                    public Entry<Short, Character> next() {
                        this.it.advance();
                        short short0 = this.it.key();
                        final Short oshort = short0 == TShortCharMapDecorator.this._map.getNoEntryKey() ? null : TShortCharMapDecorator.this.wrapKey(short0);
                        char c0 = this.it.value();
                        final Character character = c0 == TShortCharMapDecorator.this._map.getNoEntryValue() ? null : TShortCharMapDecorator.this.wrapValue(c0);

                        return new Entry<Short, Character>() {
                            private Character val = character;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(oshort) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Short getKey() {
                                return oshort;
                            }

                            public Character getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return oshort.hashCode() + this.val.hashCode();
                            }

                            public Character setValue(Character character1) {
                                this.val = character1;
                                return TShortCharMapDecorator.this.put(oshort, character1);
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

            public boolean add(Entry<Short, Character> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Short oshort = (Short) ((Entry) object).getKey();

                    TShortCharMapDecorator.this._map.remove(TShortCharMapDecorator.this.unwrapKey(oshort));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Short, Character>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TShortCharMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Character && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Short && this._map.containsKey(this.unwrapKey(object));
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Short, ? extends Character> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Short) entry.getKey(), (Character) entry.getValue());
        }

    }

    protected Short wrapKey(short short0) {
        return short0;
    }

    protected short unwrapKey(Object object) {
        return (Short) object;
    }

    protected Character wrapValue(char c0) {
        return c0;
    }

    protected char unwrapValue(Object object) {
        return (Character) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TShortCharMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
