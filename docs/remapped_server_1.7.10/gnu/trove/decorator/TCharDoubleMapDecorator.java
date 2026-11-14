package gnu.trove.decorator;

import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.map.TCharDoubleMap;
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

public class TCharDoubleMapDecorator extends AbstractMap<Character, Double> implements Map<Character, Double>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TCharDoubleMap _map;

    public TCharDoubleMapDecorator() {}

    public TCharDoubleMapDecorator(TCharDoubleMap tchardoublemap) {
        this._map = tchardoublemap;
    }

    public TCharDoubleMap getMap() {
        return this._map;
    }

    public Double put(Character character, Double odouble) {
        char c0;

        if (character == null) {
            c0 = this._map.getNoEntryKey();
        } else {
            c0 = this.unwrapKey(character);
        }

        double d0;

        if (odouble == null) {
            d0 = this._map.getNoEntryValue();
        } else {
            d0 = this.unwrapValue(odouble);
        }

        double d1 = this._map.put(c0, d0);

        return d1 == this._map.getNoEntryValue() ? null : this.wrapValue(d1);
    }

    public Double get(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey(object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        double d0 = this._map.get(c0);

        return d0 == this._map.getNoEntryValue() ? null : this.wrapValue(d0);
    }

    public void clear() {
        this._map.clear();
    }

    public Double remove(Object object) {
        char c0;

        if (object != null) {
            if (!(object instanceof Character)) {
                return null;
            }

            c0 = this.unwrapKey(object);
        } else {
            c0 = this._map.getNoEntryKey();
        }

        double d0 = this._map.remove(c0);

        return d0 == this._map.getNoEntryValue() ? null : this.wrapValue(d0);
    }

    public Set<Entry<Character, Double>> entrySet() {
        return new AbstractSet<Entry<Character, Double>>() {
            public int size() {
                return TCharDoubleMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TCharDoubleMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TCharDoubleMapDecorator.this.containsKey(object1) && TCharDoubleMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Character, Double>> iterator() {
                return new Iterator<Entry<Character, Double>>() {
                    private final TCharDoubleIterator it;

                    {
                        this.it = TCharDoubleMapDecorator.this._map.iterator();
                    }

                    public Entry<Character, Double> next() {
                        this.it.advance();
                        char c0 = this.it.key();
                        final Character character = c0 == TCharDoubleMapDecorator.this._map.getNoEntryKey() ? null : TCharDoubleMapDecorator.this.wrapKey(c0);
                        double d0 = this.it.value();
                        final Double odouble = d0 == TCharDoubleMapDecorator.this._map.getNoEntryValue() ? null : TCharDoubleMapDecorator.this.wrapValue(d0);

                        return new Entry<Character, Double>() {
                            private Double val = odouble;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(character) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Character getKey() {
                                return character;
                            }

                            public Double getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return character.hashCode() + this.val.hashCode();
                            }

                            public Double setValue(Double odouble1) {
                                this.val = odouble1;
                                return TCharDoubleMapDecorator.this.put(character, odouble1);
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

            public boolean add(Entry<Character, Double> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Character character = (Character) ((Entry) object).getKey();

                    TCharDoubleMapDecorator.this._map.remove(TCharDoubleMapDecorator.this.unwrapKey(character));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Character, Double>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TCharDoubleMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Double && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Character, ? extends Double> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), (Double) entry.getValue());
        }

    }

    protected Character wrapKey(char c0) {
        return c0;
    }

    protected char unwrapKey(Object object) {
        return (Character) object;
    }

    protected Double wrapValue(double d0) {
        return d0;
    }

    protected double unwrapValue(Object object) {
        return (Double) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TCharDoubleMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
