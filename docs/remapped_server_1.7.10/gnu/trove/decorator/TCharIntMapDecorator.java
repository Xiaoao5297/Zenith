package gnu.trove.decorator;

import gnu.trove.iterator.TCharIntIterator;
import gnu.trove.map.TCharIntMap;
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

public class TCharIntMapDecorator extends AbstractMap<Character, Integer> implements Map<Character, Integer>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TCharIntMap _map;

    public TCharIntMapDecorator() {}

    public TCharIntMapDecorator(TCharIntMap tcharintmap) {
        this._map = tcharintmap;
    }

    public TCharIntMap getMap() {
        return this._map;
    }

    public Integer put(Character character, Integer integer) {
        char c0;

        if (character == null) {
            c0 = this._map.getNoEntryKey();
        } else {
            c0 = this.unwrapKey(character);
        }

        int i;

        if (integer == null) {
            i = this._map.getNoEntryValue();
        } else {
            i = this.unwrapValue(integer);
        }

        int j = this._map.put(c0, i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public Integer get(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey(object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        int i = this._map.get(c0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public void clear() {
        this._map.clear();
    }

    public Integer remove(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey(object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        int i = this._map.remove(c0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public Set<Entry<Character, Integer>> entrySet() {
        return new AbstractSet<Entry<Character, Integer>>() {
            public int size() {
                return TCharIntMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TCharIntMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TCharIntMapDecorator.this.containsKey(object1) && TCharIntMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Character, Integer>> iterator() {
                return new Iterator<Entry<Character, Integer>>() {
                    private final TCharIntIterator it;

                    {
                        this.it = TCharIntMapDecorator.this._map.iterator();
                    }

                    public Entry<Character, Integer> next() {
                        this.it.advance();
                        char c0 = this.it.key();
                        final Character character = c0 == TCharIntMapDecorator.this._map.getNoEntryKey() ? null : TCharIntMapDecorator.this.wrapKey(c0);
                        int i = this.it.value();
                        final Integer integer = i == TCharIntMapDecorator.this._map.getNoEntryValue() ? null : TCharIntMapDecorator.this.wrapValue(i);

                        return new Entry<Character, Integer>() {
                            private Integer val = integer;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(character) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Character getKey() {
                                return character;
                            }

                            public Integer getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return character.hashCode() + this.val.hashCode();
                            }

                            public Integer setValue(Integer integer1) {
                                this.val = integer1;
                                return TCharIntMapDecorator.this.put(character, integer1);
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

            public boolean add(Entry<Character, Integer> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Character character = (Character) ((Entry) object).getKey();

                    TCharIntMapDecorator.this._map.remove(TCharIntMapDecorator.this.unwrapKey(character));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Character, Integer>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TCharIntMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Integer && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Character, ? extends Integer> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), (Integer) entry.getValue());
        }

    }

    protected Character wrapKey(char c0) {
        return c0;
    }

    protected char unwrapKey(Object object) {
        return (Character) object;
    }

    protected Integer wrapValue(int i) {
        return i;
    }

    protected int unwrapValue(Object object) {
        return (Integer) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TCharIntMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
