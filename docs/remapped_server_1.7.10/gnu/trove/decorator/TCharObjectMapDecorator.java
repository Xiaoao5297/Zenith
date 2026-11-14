package gnu.trove.decorator;

import gnu.trove.iterator.TCharObjectIterator;
import gnu.trove.map.TCharObjectMap;
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

public class TCharObjectMapDecorator<V> extends AbstractMap<Character, V> implements Map<Character, V>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TCharObjectMap<V> _map;

    public TCharObjectMapDecorator() {}

    public TCharObjectMapDecorator(TCharObjectMap<V> tcharobjectmap) {
        this._map = tcharobjectmap;
    }

    public TCharObjectMap<V> getMap() {
        return this._map;
    }

    public V put(Character character, V v0) {
        char c0;

        if (character == null) {
            c0 = this._map.getNoEntryKey();
        } else {
            c0 = this.unwrapKey(character);
        }

        return this._map.put(c0, v0);
    }

    public V get(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey((Character) object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        return this._map.get(c0);
    }

    public void clear() {
        this._map.clear();
    }

    public V remove(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey((Character) object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        return this._map.remove(c0);
    }

    public Set<Entry<Character, V>> entrySet() {
        return new AbstractSet<Entry<Character, V>>() {
            public int size() {
                return TCharObjectMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TCharObjectMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TCharObjectMapDecorator.this.containsKey(object1) && TCharObjectMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Character, V>> iterator() {
                return new Iterator<Entry<Character, V>>() {
                    private final TCharObjectIterator<V> it;

                    {
                        this.it = TCharObjectMapDecorator.this._map.iterator();
                    }

                    public Entry<Character, V> next() {
                        this.it.advance();
                        char c0 = this.it.key();
                        final Character character = c0 == TCharObjectMapDecorator.this._map.getNoEntryKey() ? null : TCharObjectMapDecorator.this.wrapKey(c0);
                        final Object object = this.it.value();

                        return new Entry<Character, V>() {
                            private V val = object;

                            public boolean equals(Object object1) {
                                return object1 instanceof Entry && ((Entry) object1).getKey().equals(character) && ((Entry) object1).getValue().equals(this.val);
                            }

                            public Character getKey() {
                                return character;
                            }

                            public V getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return character.hashCode() + this.val.hashCode();
                            }

                            public V setValue(V v0) {
                                this.val = v0;
                                return TCharObjectMapDecorator.this.put(character, v0);
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

            public boolean add(Entry<Character, V> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Character character = (Character) ((Entry) object).getKey();

                    TCharObjectMapDecorator.this._map.remove(TCharObjectMapDecorator.this.unwrapKey(character));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Character, V>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TCharObjectMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return this._map.containsValue(object);
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Character && this._map.containsKey((Character) object);
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Character, ? extends V> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), entry.getValue());
        }

    }

    protected Character wrapKey(char c0) {
        return c0;
    }

    protected char unwrapKey(Character character) {
        return character;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TCharObjectMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
