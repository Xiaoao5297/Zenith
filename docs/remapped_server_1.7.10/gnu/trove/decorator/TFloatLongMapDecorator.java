package gnu.trove.decorator;

import gnu.trove.iterator.TFloatLongIterator;
import gnu.trove.map.TFloatLongMap;
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

public class TFloatLongMapDecorator extends AbstractMap<Float, Long> implements Map<Float, Long>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TFloatLongMap _map;

    public TFloatLongMapDecorator() {}

    public TFloatLongMapDecorator(TFloatLongMap tfloatlongmap) {
        this._map = tfloatlongmap;
    }

    public TFloatLongMap getMap() {
        return this._map;
    }

    public Long put(Float ofloat, Long olong) {
        float f;

        if (ofloat == null) {
            f = this._map.getNoEntryKey();
        } else {
            f = this.unwrapKey(ofloat);
        }

        long i;

        if (olong == null) {
            i = this._map.getNoEntryValue();
        } else {
            i = this.unwrapValue(olong);
        }

        long j = this._map.put(f, i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public Long get(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey(object);
        } else {
            f = this._map.getNoEntryKey();
        }

        long i = this._map.get(f);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public void clear() {
        this._map.clear();
    }

    public Long remove(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey(object);
        } else {
            f = this._map.getNoEntryKey();
        }

        long i = this._map.remove(f);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public Set<Entry<Float, Long>> entrySet() {
        return new AbstractSet<Entry<Float, Long>>() {
            public int size() {
                return TFloatLongMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TFloatLongMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TFloatLongMapDecorator.this.containsKey(object1) && TFloatLongMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Float, Long>> iterator() {
                return new Iterator<Entry<Float, Long>>() {
                    private final TFloatLongIterator it;

                    {
                        this.it = TFloatLongMapDecorator.this._map.iterator();
                    }

                    public Entry<Float, Long> next() {
                        this.it.advance();
                        float f = this.it.key();
                        final Float ofloat = f == TFloatLongMapDecorator.this._map.getNoEntryKey() ? null : TFloatLongMapDecorator.this.wrapKey(f);
                        long i = this.it.value();
                        final Long olong = i == TFloatLongMapDecorator.this._map.getNoEntryValue() ? null : TFloatLongMapDecorator.this.wrapValue(i);

                        return new Entry<Float, Long>() {
                            private Long val = olong;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(ofloat) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Float getKey() {
                                return ofloat;
                            }

                            public Long getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return ofloat.hashCode() + this.val.hashCode();
                            }

                            public Long setValue(Long olong1) {
                                this.val = olong1;
                                return TFloatLongMapDecorator.this.put(ofloat, olong1);
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

            public boolean add(Entry<Float, Long> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Float ofloat = (Float) ((Entry) object).getKey();

                    TFloatLongMapDecorator.this._map.remove(TFloatLongMapDecorator.this.unwrapKey(ofloat));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Float, Long>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TFloatLongMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Long && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Float, ? extends Long> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Float) entry.getKey(), (Long) entry.getValue());
        }

    }

    protected Float wrapKey(float f) {
        return f;
    }

    protected float unwrapKey(Object object) {
        return (Float) object;
    }

    protected Long wrapValue(long i) {
        return i;
    }

    protected long unwrapValue(Object object) {
        return (Long) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TFloatLongMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
