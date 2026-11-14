package gnu.trove.decorator;

import gnu.trove.iterator.TIntCharIterator;
import gnu.trove.map.TIntCharMap;
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

public class TIntCharMapDecorator extends AbstractMap<Integer, Character> implements Map<Integer, Character>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TIntCharMap _map;

    public TIntCharMapDecorator() {}

    public TIntCharMapDecorator(TIntCharMap tintcharmap) {
        this._map = tintcharmap;
    }

    public TIntCharMap getMap() {
        return this._map;
    }

    public Character put(Integer integer, Character character) {
        int i;

        if (integer == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(integer);
        }

        char c0;

        if (character == null) {
            c0 = this._map.getNoEntryValue();
        } else {
            c0 = this.unwrapValue(character);
        }

        char c1 = this._map.put(i, c0);

        return c1 == this._map.getNoEntryValue() ? null : this.wrapValue(c1);
    }

    public Character get(Object object) {
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        char c0 = this._map.get(i);

        return c0 == this._map.getNoEntryValue() ? null : this.wrapValue(c0);
    }

    public void clear() {
        this._map.clear();
    }

    public Character remove(Object object) {
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        char c0 = this._map.remove(i);

        return c0 == this._map.getNoEntryValue() ? null : this.wrapValue(c0);
    }

    public Set<Entry<Integer, Character>> entrySet() {
        return new AbstractSet<Entry<Integer, Character>>() {
            public int size() {
                return TIntCharMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TIntCharMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TIntCharMapDecorator.this.containsKey(object1) && TIntCharMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Integer, Character>> iterator() {
                return new Iterator<Entry<Integer, Character>>() {
                    private final TIntCharIterator it;

                    {
                        this.it = TIntCharMapDecorator.this._map.iterator();
                    }

                    public Entry<Integer, Character> next() {
                        this.it.advance();
                        int i = this.it.key();
                        final Integer integer = i == TIntCharMapDecorator.this._map.getNoEntryKey() ? null : TIntCharMapDecorator.this.wrapKey(i);
                        char c0 = this.it.value();
                        final Character character = c0 == TIntCharMapDecorator.this._map.getNoEntryValue() ? null : TIntCharMapDecorator.this.wrapValue(c0);

                        return new Entry<Integer, Character>() {
                            private Character val = character;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(integer) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Integer getKey() {
                                return integer;
                            }

                            public Character getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return integer.hashCode() + this.val.hashCode();
                            }

                            public Character setValue(Character character1) {
                                this.val = character1;
                                return TIntCharMapDecorator.this.put(integer, character1);
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

            public boolean add(Entry<Integer, Character> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Integer integer = (Integer) ((Entry) object).getKey();

                    TIntCharMapDecorator.this._map.remove(TIntCharMapDecorator.this.unwrapKey(integer));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Integer, Character>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TIntCharMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Character && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Integer && this._map.containsKey(this.unwrapKey(object));
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Integer, ? extends Character> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Integer) entry.getKey(), (Character) entry.getValue());
        }

    }

    protected Integer wrapKey(int i) {
        return i;
    }

    protected int unwrapKey(Object object) {
        return (Integer) object;
    }

    protected Character wrapValue(char c0) {
        return c0;
    }

    protected char unwrapValue(Object object) {
        return (Character) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TIntCharMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
