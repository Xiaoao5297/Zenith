package gnu.trove.decorator;

import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.map.TObjectCharMap;
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

public class TObjectCharMapDecorator<K> extends AbstractMap<K, Character> implements Map<K, Character>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TObjectCharMap<K> _map;

    public TObjectCharMapDecorator() {}

    public TObjectCharMapDecorator(TObjectCharMap<K> tobjectcharmap) {
        this._map = tobjectcharmap;
    }

    public TObjectCharMap<K> getMap() {
        return this._map;
    }

    public Character put(K k0, Character character) {
        return character == null ? this.wrapValue(this._map.put(k0, this._map.getNoEntryValue())) : this.wrapValue(this._map.put(k0, this.unwrapValue(character)));
    }

    public Character get(Object object) {
        char c0 = this._map.get(object);

        return c0 == this._map.getNoEntryValue() ? null : this.wrapValue(c0);
    }

    public void clear() {
        this._map.clear();
    }

    public Character remove(Object object) {
        char c0 = this._map.remove(object);

        return c0 == this._map.getNoEntryValue() ? null : this.wrapValue(c0);
    }

    public Set<Entry<K, Character>> entrySet() {
        return new AbstractSet<Entry<K, Character>>() {
            public int size() {
                return TObjectCharMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TObjectCharMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TObjectCharMapDecorator.this.containsKey(object1) && TObjectCharMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<K, Character>> iterator() {
                return new Iterator<Entry<K, Character>>() {
                    private final TObjectCharIterator<K> it;

                    {
                        this.it = TObjectCharMapDecorator.this._map.iterator();
                    }

                    public Entry<K, Character> next() {
                        this.it.advance();
                        final Object object = this.it.key();
                        final Character character = TObjectCharMapDecorator.this.wrapValue(this.it.value());

                        return new Entry<K, Character>() {
                            private Character val = character;

                            public boolean equals(Object object1) {
                                return object1 instanceof Entry && ((Entry) object1).getKey().equals(object) && ((Entry) object1).getValue().equals(this.val);
                            }

                            public K getKey() {
                                return object;
                            }

                            public Character getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return object.hashCode() + this.val.hashCode();
                            }

                            public Character setValue(Character character1) {
                                this.val = character1;
                                return TObjectCharMapDecorator.this.put(object, character1);
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

            public boolean add(Entry<K, Character> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Object object1 = ((Entry) object).getKey();

                    TObjectCharMapDecorator.this._map.remove(object1);
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<K, Character>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TObjectCharMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Character && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends K, ? extends Character> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Character) entry.getValue());
        }

    }

    protected Character wrapValue(char c0) {
        return c0;
    }

    protected char unwrapValue(Object object) {
        return (Character) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TObjectCharMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
