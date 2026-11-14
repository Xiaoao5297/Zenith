package gnu.trove.decorator;

import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.map.TLongFloatMap;
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

public class TLongFloatMapDecorator extends AbstractMap<Long, Float> implements Map<Long, Float>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TLongFloatMap _map;

    public TLongFloatMapDecorator() {}

    public TLongFloatMapDecorator(TLongFloatMap tlongfloatmap) {
        this._map = tlongfloatmap;
    }

    public TLongFloatMap getMap() {
        return this._map;
    }

    public Float put(Long olong, Float ofloat) {
        long i;

        if (olong == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(olong);
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
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
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
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        float f = this._map.remove(i);

        return f == this._map.getNoEntryValue() ? null : this.wrapValue(f);
    }

    public Set<Entry<Long, Float>> entrySet() {
        return new AbstractSet<Entry<Long, Float>>() {
            public int size() {
                return TLongFloatMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TLongFloatMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TLongFloatMapDecorator.this.containsKey(object1) && TLongFloatMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Long, Float>> iterator() {
                return new Iterator<Entry<Long, Float>>() {
                    private final TLongFloatIterator it;

                    {
                        this.it = TLongFloatMapDecorator.this._map.iterator();
                    }

                    public Entry<Long, Float> next() {
                        this.it.advance();
                        long i = this.it.key();
                        final Long olong = i == TLongFloatMapDecorator.this._map.getNoEntryKey() ? null : TLongFloatMapDecorator.this.wrapKey(i);
                        float f = this.it.value();
                        final Float ofloat = f == TLongFloatMapDecorator.this._map.getNoEntryValue() ? null : TLongFloatMapDecorator.this.wrapValue(f);

                        return new Entry<Long, Float>() {
                            private Float val = ofloat;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(olong) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Long getKey() {
                                return olong;
                            }

                            public Float getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return olong.hashCode() + this.val.hashCode();
                            }

                            public Float setValue(Float ofloat1) {
                                this.val = ofloat1;
                                return TLongFloatMapDecorator.this.put(olong, ofloat1);
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

            public boolean add(Entry<Long, Float> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Long olong = (Long) ((Entry) object).getKey();

                    TLongFloatMapDecorator.this._map.remove(TLongFloatMapDecorator.this.unwrapKey(olong));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Long, Float>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TLongFloatMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Float && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Long, ? extends Float> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Long) entry.getKey(), (Float) entry.getValue());
        }

    }

    protected Long wrapKey(long i) {
        return i;
    }

    protected long unwrapKey(Object object) {
        return (Long) object;
    }

    protected Float wrapValue(float f) {
        return f;
    }

    protected float unwrapValue(Object object) {
        return (Float) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TLongFloatMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
