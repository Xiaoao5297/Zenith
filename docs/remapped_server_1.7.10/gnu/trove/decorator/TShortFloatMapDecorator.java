package gnu.trove.decorator;

import gnu.trove.iterator.TShortFloatIterator;
import gnu.trove.map.TShortFloatMap;
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

public class TShortFloatMapDecorator extends AbstractMap<Short, Float> implements Map<Short, Float>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TShortFloatMap _map;

    public TShortFloatMapDecorator() {}

    public TShortFloatMapDecorator(TShortFloatMap tshortfloatmap) {
        this._map = tshortfloatmap;
    }

    public TShortFloatMap getMap() {
        return this._map;
    }

    public Float put(Short oshort, Float ofloat) {
        short short0;

        if (oshort == null) {
            short0 = this._map.getNoEntryKey();
        } else {
            short0 = this.unwrapKey(oshort);
        }

        float f;

        if (ofloat == null) {
            f = this._map.getNoEntryValue();
        } else {
            f = this.unwrapValue(ofloat);
        }

        float f1 = this._map.put(short0, f);

        return f1 == this._map.getNoEntryValue() ? null : this.wrapValue(f1);
    }

    public Float get(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey(object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        float f = this._map.get(short0);

        return f == this._map.getNoEntryValue() ? null : this.wrapValue(f);
    }

    public void clear() {
        this._map.clear();
    }

    public Float remove(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey(object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        float f = this._map.remove(short0);

        return f == this._map.getNoEntryValue() ? null : this.wrapValue(f);
    }

    public Set<Entry<Short, Float>> entrySet() {
        return new AbstractSet<Entry<Short, Float>>() {
            public int size() {
                return TShortFloatMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TShortFloatMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TShortFloatMapDecorator.this.containsKey(object1) && TShortFloatMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Short, Float>> iterator() {
                return new Iterator<Entry<Short, Float>>() {
                    private final TShortFloatIterator it;

                    {
                        this.it = TShortFloatMapDecorator.this._map.iterator();
                    }

                    public Entry<Short, Float> next() {
                        this.it.advance();
                        short short0 = this.it.key();
                        final Short oshort = short0 == TShortFloatMapDecorator.this._map.getNoEntryKey() ? null : TShortFloatMapDecorator.this.wrapKey(short0);
                        float f = this.it.value();
                        final Float ofloat = f == TShortFloatMapDecorator.this._map.getNoEntryValue() ? null : TShortFloatMapDecorator.this.wrapValue(f);

                        return new Entry<Short, Float>() {
                            private Float val = ofloat;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(oshort) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Short getKey() {
                                return oshort;
                            }

                            public Float getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return oshort.hashCode() + this.val.hashCode();
                            }

                            public Float setValue(Float ofloat1) {
                                this.val = ofloat1;
                                return TShortFloatMapDecorator.this.put(oshort, ofloat1);
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

            public boolean add(Entry<Short, Float> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Short oshort = (Short) ((Entry) object).getKey();

                    TShortFloatMapDecorator.this._map.remove(TShortFloatMapDecorator.this.unwrapKey(oshort));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Short, Float>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TShortFloatMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Float && this._map.containsValue(this.unwrapValue(object));
    }

    public boolean containsKey(Object object) {
        return object == null ? this._map.containsKey(this._map.getNoEntryKey()) : object instanceof Short && this._map.containsKey(this.unwrapKey(object));
    }

    public int size() {
        return this._map.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void putAll(Map<? extends Short, ? extends Float> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Short) entry.getKey(), (Float) entry.getValue());
        }

    }

    protected Short wrapKey(short short0) {
        return short0;
    }

    protected short unwrapKey(Object object) {
        return (Short) object;
    }

    protected Float wrapValue(float f) {
        return f;
    }

    protected float unwrapValue(Object object) {
        return (Float) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TShortFloatMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
