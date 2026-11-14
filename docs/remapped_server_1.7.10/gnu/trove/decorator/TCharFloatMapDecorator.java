package gnu.trove.decorator;

import gnu.trove.iterator.TCharFloatIterator;
import gnu.trove.map.TCharFloatMap;
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

public class TCharFloatMapDecorator extends AbstractMap<Character, Float> implements Map<Character, Float>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TCharFloatMap _map;

    public TCharFloatMapDecorator() {}

    public TCharFloatMapDecorator(TCharFloatMap tcharfloatmap) {
        this._map = tcharfloatmap;
    }

    public TCharFloatMap getMap() {
        return this._map;
    }

    public Float put(Character character, Float ofloat) {
        char c0;

        if (character == null) {
            c0 = this._map.getNoEntryKey();
        } else {
            c0 = this.unwrapKey(character);
        }

        float f;

        if (ofloat == null) {
            f = this._map.getNoEntryValue();
        } else {
            f = this.unwrapValue(ofloat);
        }

        float f1 = this._map.put(c0, f);

        return f1 == this._map.getNoEntryValue() ? null : this.wrapValue(f1);
    }

    public Float get(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey(object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        float f = this._map.get(c0);

        return f == this._map.getNoEntryValue() ? null : this.wrapValue(f);
    }

    public void clear() {
        this._map.clear();
    }

    public Float remove(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey(object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        float f = this._map.remove(c0);

        return f == this._map.getNoEntryValue() ? null : this.wrapValue(f);
    }

    public Set<Entry<Character, Float>> entrySet() {
        return new AbstractSet<Entry<Character, Float>>() {
            public int size() {
                return TCharFloatMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TCharFloatMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TCharFloatMapDecorator.this.containsKey(object1) && TCharFloatMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Character, Float>> iterator() {
                return new Iterator<Entry<Character, Float>>() {
                    private final TCharFloatIterator it;

                    {
                        this.it = TCharFloatMapDecorator.this._map.iterator();
                    }

                    public Entry<Character, Float> next() {
                        this.it.advance();
                        char c0 = this.it.key();
                        final Character character = c0 == TCharFloatMapDecorator.this._map.getNoEntryKey() ? null : TCharFloatMapDecorator.this.wrapKey(c0);
                        float f = this.it.value();
                        final Float ofloat = f == TCharFloatMapDecorator.this._map.getNoEntryValue() ? null : TCharFloatMapDecorator.this.wrapValue(f);

                        return new Entry<Character, Float>() {
                            private Float val = ofloat;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(character) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Character getKey() {
                                return character;
                            }

                            public Float getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return character.hashCode() + this.val.hashCode();
                            }

                            public Float setValue(Float ofloat1) {
                                this.val = ofloat1;
                                return TCharFloatMapDecorator.this.put(character, ofloat1);
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

            public boolean add(Entry<Character, Float> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Character character = (Character) ((Entry) object).getKey();

                    TCharFloatMapDecorator.this._map.remove(TCharFloatMapDecorator.this.unwrapKey(character));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Character, Float>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TCharFloatMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Float && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Character, ? extends Float> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), (Float) entry.getValue());
        }

    }

    protected Character wrapKey(char c0) {
        return c0;
    }

    protected char unwrapKey(Object object) {
        return (Character) object;
    }

    protected Float wrapValue(float f) {
        return f;
    }

    protected float unwrapValue(Object object) {
        return (Float) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TCharFloatMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
