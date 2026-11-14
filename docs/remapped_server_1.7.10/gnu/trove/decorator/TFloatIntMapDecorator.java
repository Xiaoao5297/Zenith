package gnu.trove.decorator;

import gnu.trove.iterator.TFloatIntIterator;
import gnu.trove.map.TFloatIntMap;
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

public class TFloatIntMapDecorator extends AbstractMap<Float, Integer> implements Map<Float, Integer>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TFloatIntMap _map;

    public TFloatIntMapDecorator() {}

    public TFloatIntMapDecorator(TFloatIntMap tfloatintmap) {
        this._map = tfloatintmap;
    }

    public TFloatIntMap getMap() {
        return this._map;
    }

    public Integer put(Float ofloat, Integer integer) {
        float f;

        if (ofloat == null) {
            f = this._map.getNoEntryKey();
        } else {
            f = this.unwrapKey(ofloat);
        }

        int i;

        if (integer == null) {
            i = this._map.getNoEntryValue();
        } else {
            i = this.unwrapValue(integer);
        }

        int j = this._map.put(f, i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public Integer get(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey(object);
        } else {
            f = this._map.getNoEntryKey();
        }

        int i = this._map.get(f);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public void clear() {
        this._map.clear();
    }

    public Integer remove(Object object) {
        float f;

        if (object != null) {
            if (!(object instanceof Float)) {
                return null;
            }

            f = this.unwrapKey(object);
        } else {
            f = this._map.getNoEntryKey();
        }

        int i = this._map.remove(f);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public Set<Entry<Float, Integer>> entrySet() {
        return new AbstractSet<Entry<Float, Integer>>() {
            public int size() {
                return TFloatIntMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TFloatIntMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TFloatIntMapDecorator.this.containsKey(object1) && TFloatIntMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Float, Integer>> iterator() {
                return new Iterator<Entry<Float, Integer>>() {
                    private final TFloatIntIterator it;

                    {
                        this.it = TFloatIntMapDecorator.this._map.iterator();
                    }

                    public Entry<Float, Integer> next() {
                        this.it.advance();
                        float f = this.it.key();
                        final Float ofloat = f == TFloatIntMapDecorator.this._map.getNoEntryKey() ? null : TFloatIntMapDecorator.this.wrapKey(f);
                        int i = this.it.value();
                        final Integer integer = i == TFloatIntMapDecorator.this._map.getNoEntryValue() ? null : TFloatIntMapDecorator.this.wrapValue(i);

                        return new Entry<Float, Integer>() {
                            private Integer val = integer;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(ofloat) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Float getKey() {
                                return ofloat;
                            }

                            public Integer getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return ofloat.hashCode() + this.val.hashCode();
                            }

                            public Integer setValue(Integer integer1) {
                                this.val = integer1;
                                return TFloatIntMapDecorator.this.put(ofloat, integer1);
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

            public boolean add(Entry<Float, Integer> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Float ofloat = (Float) ((Entry) object).getKey();

                    TFloatIntMapDecorator.this._map.remove(TFloatIntMapDecorator.this.unwrapKey(ofloat));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Float, Integer>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TFloatIntMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Integer && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Float, ? extends Integer> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Float) entry.getKey(), (Integer) entry.getValue());
        }

    }

    protected Float wrapKey(float f) {
        return f;
    }

    protected float unwrapKey(Object object) {
        return (Float) object;
    }

    protected Integer wrapValue(int i) {
        return i;
    }

    protected int unwrapValue(Object object) {
        return (Integer) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TFloatIntMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
