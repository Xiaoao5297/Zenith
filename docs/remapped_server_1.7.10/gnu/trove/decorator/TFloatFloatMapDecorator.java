package gnu.trove.decorator;

import gnu.trove.iterator.TFloatFloatIterator;
import gnu.trove.map.TFloatFloatMap;
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

public class TFloatFloatMapDecorator extends AbstractMap<Float, Float> implements Map<Float, Float>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TFloatFloatMap _map;

    public TFloatFloatMapDecorator() {}

    public TFloatFloatMapDecorator(TFloatFloatMap tfloatfloatmap) {
        this._map = tfloatfloatmap;
    }

    public TFloatFloatMap getMap() {
        return this._map;
    }

    public Float put(Float ofloat, Float ofloat1) {
        float f;

        if (ofloat == null) {
            f = this._map.getNoEntryKey();
        } else {
            f = this.unwrapKey(ofloat);
        }

        float f1;

        if (ofloat1 == null) {
            f1 = this._map.getNoEntryValue();
        } else {
            f1 = this.unwrapValue(ofloat1);
        }

        float f2 = this._map.put(f, f1);

        return f2 == this._map.getNoEntryValue() ? null : this.wrapValue(f2);
    }

    public Float get(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey(object);
        } else {
            f = this._map.getNoEntryKey();
        }

        float f1 = this._map.get(f);

        return f1 == this._map.getNoEntryValue() ? null : this.wrapValue(f1);
    }

    public void clear() {
        this._map.clear();
    }

    public Float remove(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey(object);
        } else {
            f = this._map.getNoEntryKey();
        }

        float f1 = this._map.remove(f);

        return f1 == this._map.getNoEntryValue() ? null : this.wrapValue(f1);
    }

    public Set<Entry<Float, Float>> entrySet() {
        return new AbstractSet<Entry<Float, Float>>() {
            public int size() {
                return TFloatFloatMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TFloatFloatMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TFloatFloatMapDecorator.this.containsKey(object1) && TFloatFloatMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Float, Float>> iterator() {
                return new Iterator<Entry<Float, Float>>() {
                    private final TFloatFloatIterator it;

                    {
                        this.it = TFloatFloatMapDecorator.this._map.iterator();
                    }

                    public Entry<Float, Float> next() {
                        this.it.advance();
                        float f = this.it.key();
                        final Float ofloat = f == TFloatFloatMapDecorator.this._map.getNoEntryKey() ? null : TFloatFloatMapDecorator.this.wrapKey(f);
                        float f1 = this.it.value();
                        final Float ofloat1 = f1 == TFloatFloatMapDecorator.this._map.getNoEntryValue() ? null : TFloatFloatMapDecorator.this.wrapValue(f1);

                        return new Entry<Float, Float>() {
                            private Float val = ofloat1;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(ofloat) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Float getKey() {
                                return ofloat;
                            }

                            public Float getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return ofloat.hashCode() + this.val.hashCode();
                            }

                            public Float setValue(Float ofloat2) {
                                this.val = ofloat2;
                                return TFloatFloatMapDecorator.this.put(ofloat, ofloat2);
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

            public boolean add(Entry<Float, Float> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Float ofloat = (Float) ((Entry) object).getKey();

                    TFloatFloatMapDecorator.this._map.remove(TFloatFloatMapDecorator.this.unwrapKey(ofloat));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Float, Float>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TFloatFloatMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Float && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Float, ? extends Float> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Float) entry.getKey(), (Float) entry.getValue());
        }

    }

    protected Float wrapKey(float f) {
        return f;
    }

    protected float unwrapKey(Object object) {
        return (Float) object;
    }

    protected Float wrapValue(float f) {
        return f;
    }

    protected float unwrapValue(Object object) {
        return (Float) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TFloatFloatMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
