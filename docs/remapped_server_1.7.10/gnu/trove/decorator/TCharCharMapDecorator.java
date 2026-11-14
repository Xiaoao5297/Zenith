package gnu.trove.decorator;

import gnu.trove.iterator.TCharCharIterator;
import gnu.trove.map.TCharCharMap;
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

public class TCharCharMapDecorator extends AbstractMap<Character, Character> implements Map<Character, Character>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TCharCharMap _map;

    public TCharCharMapDecorator() {}

    public TCharCharMapDecorator(TCharCharMap tcharcharmap) {
        this._map = tcharcharmap;
    }

    public TCharCharMap getMap() {
        return this._map;
    }

    public Character put(Character character, Character character1) {
        char c0;

        if (character == null) {
            c0 = this._map.getNoEntryKey();
        } else {
            c0 = this.unwrapKey(character);
        }

        char c1;

        if (character1 == null) {
            c1 = this._map.getNoEntryValue();
        } else {
            c1 = this.unwrapValue(character1);
        }

        char c2 = this._map.put(c0, c1);

        return c2 == this._map.getNoEntryValue() ? null : this.wrapValue(c2);
    }

    public Character get(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey(object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        char c1 = this._map.get(c0);

        return c1 == this._map.getNoEntryValue() ? null : this.wrapValue(c1);
    }

    public void clear() {
        this._map.clear();
    }

    public Character remove(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey(object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        char c1 = this._map.remove(c0);

        return c1 == this._map.getNoEntryValue() ? null : this.wrapValue(c1);
    }

    public Set<Entry<Character, Character>> entrySet() {
        return new AbstractSet<Entry<Character, Character>>() {
            public int size() {
                return TCharCharMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TCharCharMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TCharCharMapDecorator.this.containsKey(object1) && TCharCharMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Character, Character>> iterator() {
                return new Iterator<Entry<Character, Character>>() {
                    private final TCharCharIterator it;

                    {
                        this.it = TCharCharMapDecorator.this._map.iterator();
                    }

                    public Entry<Character, Character> next() {
                        this.it.advance();
                        char c0 = this.it.key();
                        final Character character = c0 == TCharCharMapDecorator.this._map.getNoEntryKey() ? null : TCharCharMapDecorator.this.wrapKey(c0);
                        char c1 = this.it.value();
                        final Character character1 = c1 == TCharCharMapDecorator.this._map.getNoEntryValue() ? null : TCharCharMapDecorator.this.wrapValue(c1);

                        return new Entry<Character, Character>() {
                            private Character val = character1;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(character) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Character getKey() {
                                return character;
                            }

                            public Character getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return character.hashCode() + this.val.hashCode();
                            }

                            public Character setValue(Character character2) {
                                this.val = character2;
                                return TCharCharMapDecorator.this.put(character, character2);
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

            public boolean add(Entry<Character, Character> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Character character = (Character) ((Entry) object).getKey();

                    TCharCharMapDecorator.this._map.remove(TCharCharMapDecorator.this.unwrapKey(character));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Character, Character>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TCharCharMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Character && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Character, ? extends Character> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), (Character) entry.getValue());
        }

    }

    protected Character wrapKey(char c0) {
        return c0;
    }

    protected char unwrapKey(Object object) {
        return (Character) object;
    }

    protected Character wrapValue(char c0) {
        return c0;
    }

    protected char unwrapValue(Object object) {
        return (Character) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TCharCharMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
