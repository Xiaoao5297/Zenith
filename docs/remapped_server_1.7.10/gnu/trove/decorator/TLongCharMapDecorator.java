package gnu.trove.decorator;

import gnu.trove.iterator.TLongCharIterator;
import gnu.trove.map.TLongCharMap;
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

public class TLongCharMapDecorator extends AbstractMap<Long, Character> implements Map<Long, Character>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TLongCharMap _map;

    public TLongCharMapDecorator() {}

    public TLongCharMapDecorator(TLongCharMap tlongcharmap) {
        this._map = tlongcharmap;
    }

    public TLongCharMap getMap() {
        return this._map;
    }

    public Character put(Long olong, Character character) {
        long i;

        if (olong == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(olong);
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
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
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
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        char c0 = this._map.remove(i);

        return c0 == this._map.getNoEntryValue() ? null : this.wrapValue(c0);
    }

    public Set<Entry<Long, Character>> entrySet() {
        return new AbstractSet<Entry<Long, Character>>() {
            public int size() {
                return TLongCharMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TLongCharMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TLongCharMapDecorator.this.containsKey(object1) && TLongCharMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Long, Character>> iterator() {
                return new Iterator<Entry<Long, Character>>() {
                    private final TLongCharIterator it;

                    {
                        this.it = TLongCharMapDecorator.this._map.iterator();
                    }

                    public Entry<Long, Character> next() {
                        this.it.advance();
                        long i = this.it.key();
                        final Long olong = i == TLongCharMapDecorator.this._map.getNoEntryKey() ? null : TLongCharMapDecorator.this.wrapKey(i);
                        char c0 = this.it.value();
                        final Character character = c0 == TLongCharMapDecorator.this._map.getNoEntryValue() ? null : TLongCharMapDecorator.this.wrapValue(c0);

                        return new Entry<Long, Character>() {
                            private Character val = character;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(olong) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Long getKey() {
                                return olong;
                            }

                            public Character getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return olong.hashCode() + this.val.hashCode();
                            }

                            public Character setValue(Character character1) {
                                this.val = character1;
                                return TLongCharMapDecorator.this.put(olong, character1);
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

            public boolean add(Entry<Long, Character> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Long olong = (Long) ((Entry) object).getKey();

                    TLongCharMapDecorator.this._map.remove(TLongCharMapDecorator.this.unwrapKey(olong));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Long, Character>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TLongCharMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Character && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Long && this._map.containsKey(this.unwrapKey(object));
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Long, ? extends Character> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Long) entry.getKey(), (Character) entry.getValue());
        }

    }

    protected Long wrapKey(long i) {
        return i;
    }

    protected long unwrapKey(Object object) {
        return (Long) object;
    }

    protected Character wrapValue(char c0) {
        return c0;
    }

    protected char unwrapValue(Object object) {
        return (Character) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TLongCharMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
