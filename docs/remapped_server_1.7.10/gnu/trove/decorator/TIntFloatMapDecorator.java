package gnu.trove.decorator;

import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.map.TIntFloatMap;
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

public class TIntFloatMapDecorator extends AbstractMap<Integer, Float> implements Map<Integer, Float>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TIntFloatMap _map;

    public TIntFloatMapDecorator() {}

    public TIntFloatMapDecorator(TIntFloatMap tintfloatmap) {
        this._map = tintfloatmap;
    }

    public TIntFloatMap getMap() {
        return this._map;
    }

    public Float put(Integer integer, Float ofloat) {
        int i;

        if (integer == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(integer);
        }

        float f;

        if (ofloat == null) {
            f = this._map.getNoEntryValue();
        } else {
            f = this.unwrapValue(ofloat);
        }

        float f1 = this._map.put(i, f);

        return f1 == this._map.getNoEntryValue() ? null : this.wrapValue(f1);
    }

    public Float get(Object object) {
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        float f = this._map.get(i);

        return f == this._map.getNoEntryValue() ? null : this.wrapValue(f);
    }

    public void clear() {
        this._map.clear();
    }

    public Float remove(Object object) {
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        float f = this._map.remove(i);

        return f == this._map.getNoEntryValue() ? null : this.wrapValue(f);
    }

    public Set<Entry<Integer, Float>> entrySet() {
        return new AbstractSet<Entry<Integer, Float>>() {
            public int size() {
                return TIntFloatMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TIntFloatMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TIntFloatMapDecorator.this.containsKey(object1) && TIntFloatMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Integer, Float>> iterator() {
                return new Iterator<Entry<Integer, Float>>() {
                    private final TIntFloatIterator it;

                    {
                        this.it = TIntFloatMapDecorator.this._map.iterator();
                    }

                    public Entry<Integer, Float> next() {
                        this.it.advance();
                        int i = this.it.key();
                        final Integer integer = i == TIntFloatMapDecorator.this._map.getNoEntryKey() ? null : TIntFloatMapDecorator.this.wrapKey(i);
                        float f = this.it.value();
                        final Float ofloat = f == TIntFloatMapDecorator.this._map.getNoEntryValue() ? null : TIntFloatMapDecorator.this.wrapValue(f);

                        return new Entry<Integer, Float>() {
                            private Float val = ofloat;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(integer) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Integer getKey() {
                                return integer;
                            }

                            public Float getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return integer.hashCode() + this.val.hashCode();
                            }

                            public Float setValue(Float ofloat1) {
                                this.val = ofloat1;
                                return TIntFloatMapDecorator.this.put(integer, ofloat1);
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

            public boolean add(Entry<Integer, Float> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Integer integer = (Integer) ((Entry) object).getKey();

                    TIntFloatMapDecorator.this._map.remove(TIntFloatMapDecorator.this.unwrapKey(integer));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Integer, Float>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TIntFloatMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Float && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Integer, ? extends Float> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Integer) entry.getKey(), (Float) entry.getValue());
        }

    }

    protected Integer wrapKey(int i) {
        return i;
    }

    protected int unwrapKey(Object object) {
        return (Integer) object;
    }

    protected Float wrapValue(float f) {
        return f;
    }

    protected float unwrapValue(Object object) {
        return (Float) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TIntFloatMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
