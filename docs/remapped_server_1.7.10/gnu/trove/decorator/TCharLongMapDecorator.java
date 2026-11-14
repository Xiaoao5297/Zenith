package gnu.trove.decorator;

import gnu.trove.iterator.TCharLongIterator;
import gnu.trove.map.TCharLongMap;
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

public class TCharLongMapDecorator extends AbstractMap<Character, Long> implements Map<Character, Long>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TCharLongMap _map;

    public TCharLongMapDecorator() {}

    public TCharLongMapDecorator(TCharLongMap tcharlongmap) {
        this._map = tcharlongmap;
    }

    public TCharLongMap getMap() {
        return this._map;
    }

    public Long put(Character character, Long olong) {
        char c0;

        if (character == null) {
            c0 = this._map.getNoEntryKey();
        } else {
            c0 = this.unwrapKey(character);
        }

        long i;

        if (olong == null) {
            i = this._map.getNoEntryValue();
        } else {
            i = this.unwrapValue(olong);
        }

        long j = this._map.put(c0, i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public Long get(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey(object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        long i = this._map.get(c0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public void clear() {
        this._map.clear();
    }

    public Long remove(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey(object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        long i = this._map.remove(c0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public Set<Entry<Character, Long>> entrySet() {
        return new AbstractSet<Entry<Character, Long>>() {
            public int size() {
                return TCharLongMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TCharLongMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TCharLongMapDecorator.this.containsKey(object1) && TCharLongMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Character, Long>> iterator() {
                return new Iterator<Entry<Character, Long>>() {
                    private final TCharLongIterator it;

                    {
                        this.it = TCharLongMapDecorator.this._map.iterator();
                    }

                    public Entry<Character, Long> next() {
                        this.it.advance();
                        char c0 = this.it.key();
                        final Character character = c0 == TCharLongMapDecorator.this._map.getNoEntryKey() ? null : TCharLongMapDecorator.this.wrapKey(c0);
                        long i = this.it.value();
                        final Long olong = i == TCharLongMapDecorator.this._map.getNoEntryValue() ? null : TCharLongMapDecorator.this.wrapValue(i);

                        return new Entry<Character, Long>() {
                            private Long val = olong;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(character) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Character getKey() {
                                return character;
                            }

                            public Long getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return character.hashCode() + this.val.hashCode();
                            }

                            public Long setValue(Long olong1) {
                                this.val = olong1;
                                return TCharLongMapDecorator.this.put(character, olong1);
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

            public boolean add(Entry<Character, Long> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Character character = (Character) ((Entry) object).getKey();

                    TCharLongMapDecorator.this._map.remove(TCharLongMapDecorator.this.unwrapKey(character));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Character, Long>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TCharLongMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Long && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Character, ? extends Long> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), (Long) entry.getValue());
        }

    }

    protected Character wrapKey(char c0) {
        return c0;
    }

    protected char unwrapKey(Object object) {
        return (Character) object;
    }

    protected Long wrapValue(long i) {
        return i;
    }

    protected long unwrapValue(Object object) {
        return (Long) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TCharLongMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
