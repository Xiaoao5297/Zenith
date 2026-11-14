package gnu.trove.decorator;

import gnu.trove.iterator.TFloatShortIterator;
import gnu.trove.map.TFloatShortMap;
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

public class TFloatShortMapDecorator extends AbstractMap<Float, Short> implements Map<Float, Short>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TFloatShortMap _map;

    public TFloatShortMapDecorator() {}

    public TFloatShortMapDecorator(TFloatShortMap tfloatshortmap) {
        this._map = tfloatshortmap;
    }

    public TFloatShortMap getMap() {
        return this._map;
    }

    public Short put(Float ofloat, Short oshort) {
        float f;

        if (ofloat == null) {
            f = this._map.getNoEntryKey();
        } else {
            f = this.unwrapKey(ofloat);
        }

        short short0;

        if (oshort == null) {
            short0 = this._map.getNoEntryValue();
        } else {
            short0 = this.unwrapValue(oshort);
        }

        short short1 = this._map.put(f, short0);

        return short1 == this._map.getNoEntryValue() ? null : this.wrapValue(short1);
    }

    public Short get(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey(object);
        } else {
            f = this._map.getNoEntryKey();
        }

        short short0 = this._map.get(f);

        return short0 == this._map.getNoEntryValue() ? null : this.wrapValue(short0);
    }

    public void clear() {
        this._map.clear();
    }

    public Short remove(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey(object);
        } else {
            f = this._map.getNoEntryKey();
        }

        short short0 = this._map.remove(f);

        return short0 == this._map.getNoEntryValue() ? null : this.wrapValue(short0);
    }

    public Set<Entry<Float, Short>> entrySet() {
        return new AbstractSet<Entry<Float, Short>>() {
            public int size() {
                return TFloatShortMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TFloatShortMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TFloatShortMapDecorator.this.containsKey(object1) && TFloatShortMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Float, Short>> iterator() {
                return new Iterator<Entry<Float, Short>>() {
                    private final TFloatShortIterator it;

                    {
                        this.it = TFloatShortMapDecorator.this._map.iterator();
                    }

                    public Entry<Float, Short> next() {
                        this.it.advance();
                        float f = this.it.key();
                        final Float ofloat = f == TFloatShortMapDecorator.this._map.getNoEntryKey() ? null : TFloatShortMapDecorator.this.wrapKey(f);
                        short short0 = this.it.value();
                        final Short oshort = short0 == TFloatShortMapDecorator.this._map.getNoEntryValue() ? null : TFloatShortMapDecorator.this.wrapValue(short0);

                        return new Entry<Float, Short>() {
                            private Short val = oshort;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(ofloat) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Float getKey() {
                                return ofloat;
                            }

                            public Short getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return ofloat.hashCode() + this.val.hashCode();
                            }

                            public Short setValue(Short oshort1) {
                                this.val = oshort1;
                                return TFloatShortMapDecorator.this.put(ofloat, oshort1);
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

            public boolean add(Entry<Float, Short> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Float ofloat = (Float) ((Entry) object).getKey();

                    TFloatShortMapDecorator.this._map.remove(TFloatShortMapDecorator.this.unwrapKey(ofloat));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Float, Short>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TFloatShortMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Short && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Float, ? extends Short> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Float) entry.getKey(), (Short) entry.getValue());
        }

    }

    protected Float wrapKey(float f) {
        return f;
    }

    protected float unwrapKey(Object object) {
        return (Float) object;
    }

    protected Short wrapValue(short short0) {
        return short0;
    }

    protected short unwrapValue(Object object) {
        return (Short) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TFloatShortMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
