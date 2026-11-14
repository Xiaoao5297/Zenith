package gnu.trove.decorator;

import gnu.trove.iterator.TCharShortIterator;
import gnu.trove.map.TCharShortMap;
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

public class TCharShortMapDecorator extends AbstractMap<Character, Short> implements Map<Character, Short>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TCharShortMap _map;

    public TCharShortMapDecorator() {}

    public TCharShortMapDecorator(TCharShortMap tcharshortmap) {
        this._map = tcharshortmap;
    }

    public TCharShortMap getMap() {
        return this._map;
    }

    public Short put(Character character, Short oshort) {
        char c0;

        if (character == null) {
            c0 = this._map.getNoEntryKey();
        } else {
            c0 = this.unwrapKey(character);
        }

        short short0;

        if (oshort == null) {
            short0 = this._map.getNoEntryValue();
        } else {
            short0 = this.unwrapValue(oshort);
        }

        short short1 = this._map.put(c0, short0);

        return short1 == this._map.getNoEntryValue() ? null : this.wrapValue(short1);
    }

    public Short get(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey(object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        short short0 = this._map.get(c0);

        return short0 == this._map.getNoEntryValue() ? null : this.wrapValue(short0);
    }

    public void clear() {
        this._map.clear();
    }

    public Short remove(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey(object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        short short0 = this._map.remove(c0);

        return short0 == this._map.getNoEntryValue() ? null : this.wrapValue(short0);
    }

    public Set<Entry<Character, Short>> entrySet() {
        return new AbstractSet<Entry<Character, Short>>() {
            public int size() {
                return TCharShortMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TCharShortMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TCharShortMapDecorator.this.containsKey(object1) && TCharShortMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Character, Short>> iterator() {
                return new Iterator<Entry<Character, Short>>() {
                    private final TCharShortIterator it;

                    {
                        this.it = TCharShortMapDecorator.this._map.iterator();
                    }

                    public Entry<Character, Short> next() {
                        this.it.advance();
                        char c0 = this.it.key();
                        final Character character = c0 == TCharShortMapDecorator.this._map.getNoEntryKey() ? null : TCharShortMapDecorator.this.wrapKey(c0);
                        short short0 = this.it.value();
                        final Short oshort = short0 == TCharShortMapDecorator.this._map.getNoEntryValue() ? null : TCharShortMapDecorator.this.wrapValue(short0);

                        return new Entry<Character, Short>() {
                            private Short val = oshort;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(character) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Character getKey() {
                                return character;
                            }

                            public Short getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return character.hashCode() + this.val.hashCode();
                            }

                            public Short setValue(Short oshort1) {
                                this.val = oshort1;
                                return TCharShortMapDecorator.this.put(character, oshort1);
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

            public boolean add(Entry<Character, Short> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Character character = (Character) ((Entry) object).getKey();

                    TCharShortMapDecorator.this._map.remove(TCharShortMapDecorator.this.unwrapKey(character));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Character, Short>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TCharShortMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Short && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Character, ? extends Short> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), (Short) entry.getValue());
        }

    }

    protected Character wrapKey(char c0) {
        return c0;
    }

    protected char unwrapKey(Object object) {
        return (Character) object;
    }

    protected Short wrapValue(short short0) {
        return short0;
    }

    protected short unwrapValue(Object object) {
        return (Short) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TCharShortMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
