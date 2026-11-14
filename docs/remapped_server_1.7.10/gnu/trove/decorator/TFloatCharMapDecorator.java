package gnu.trove.decorator;

import gnu.trove.iterator.TFloatCharIterator;
import gnu.trove.map.TFloatCharMap;
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

public class TFloatCharMapDecorator extends AbstractMap<Float, Character> implements Map<Float, Character>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TFloatCharMap _map;

    public TFloatCharMapDecorator() {}

    public TFloatCharMapDecorator(TFloatCharMap tfloatcharmap) {
        this._map = tfloatcharmap;
    }

    public TFloatCharMap getMap() {
        return this._map;
    }

    public Character put(Float ofloat, Character character) {
        float f;

        if (ofloat == null) {
            f = this._map.getNoEntryKey();
        } else {
            f = this.unwrapKey(ofloat);
        }

        char c0;

        if (character == null) {
            c0 = this._map.getNoEntryValue();
        } else {
            c0 = this.unwrapValue(character);
        }

        char c1 = this._map.put(f, c0);

        return c1 == this._map.getNoEntryValue() ? null : this.wrapValue(c1);
    }

    public Character get(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey(object);
        } else {
            f = this._map.getNoEntryKey();
        }

        char c0 = this._map.get(f);

        return c0 == this._map.getNoEntryValue() ? null : this.wrapValue(c0);
    }

    public void clear() {
        this._map.clear();
    }

    public Character remove(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey(object);
        } else {
            f = this._map.getNoEntryKey();
        }

        char c0 = this._map.remove(f);

        return c0 == this._map.getNoEntryValue() ? null : this.wrapValue(c0);
    }

    public Set<Entry<Float, Character>> entrySet() {
        return new AbstractSet<Entry<Float, Character>>() {
            public int size() {
                return TFloatCharMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TFloatCharMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TFloatCharMapDecorator.this.containsKey(object1) && TFloatCharMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Float, Character>> iterator() {
                return new Iterator<Entry<Float, Character>>() {
                    private final TFloatCharIterator it;

                    {
                        this.it = TFloatCharMapDecorator.this._map.iterator();
                    }

                    public Entry<Float, Character> next() {
                        this.it.advance();
                        float f = this.it.key();
                        final Float ofloat = f == TFloatCharMapDecorator.this._map.getNoEntryKey() ? null : TFloatCharMapDecorator.this.wrapKey(f);
                        char c0 = this.it.value();
                        final Character character = c0 == TFloatCharMapDecorator.this._map.getNoEntryValue() ? null : TFloatCharMapDecorator.this.wrapValue(c0);

                        return new Entry<Float, Character>() {
                            private Character val = character;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(ofloat) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Float getKey() {
                                return ofloat;
                            }

                            public Character getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return ofloat.hashCode() + this.val.hashCode();
                            }

                            public Character setValue(Character character1) {
                                this.val = character1;
                                return TFloatCharMapDecorator.this.put(ofloat, character1);
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

            public boolean add(Entry<Float, Character> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Float ofloat = (Float) ((Entry) object).getKey();

                    TFloatCharMapDecorator.this._map.remove(TFloatCharMapDecorator.this.unwrapKey(ofloat));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Float, Character>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TFloatCharMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Character && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Float && this._map.containsKey(this.unwrapKey(object));
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Float, ? extends Character> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Float) entry.getKey(), (Character) entry.getValue());
        }

    }

    protected Float wrapKey(float f) {
        return f;
    }

    protected float unwrapKey(Object object) {
        return (Float) object;
    }

    protected Character wrapValue(char c0) {
        return c0;
    }

    protected char unwrapValue(Object object) {
        return (Character) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TFloatCharMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
