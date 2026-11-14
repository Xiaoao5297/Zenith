package gnu.trove.decorator;

import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.map.TLongIntMap;
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

public class TLongIntMapDecorator extends AbstractMap<Long, Integer> implements Map<Long, Integer>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TLongIntMap _map;

    public TLongIntMapDecorator() {}

    public TLongIntMapDecorator(TLongIntMap tlongintmap) {
        this._map = tlongintmap;
    }

    public TLongIntMap getMap() {
        return this._map;
    }

    public Integer put(Long olong, Integer integer) {
        long i;

        if (olong == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(olong);
        }

        int j;

        if (integer == null) {
            j = this._map.getNoEntryValue();
        } else {
            j = this.unwrapValue(integer);
        }

        int k = this._map.put(i, j);

        return k == this._map.getNoEntryValue() ? null : this.wrapValue(k);
    }

    public Integer get(Object object) {
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        int j = this._map.get(i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public void clear() {
        this._map.clear();
    }

    public Integer remove(Object object) {
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        int j = this._map.remove(i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public Set<Entry<Long, Integer>> entrySet() {
        return new AbstractSet<Entry<Long, Integer>>() {
            public int size() {
                return TLongIntMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TLongIntMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TLongIntMapDecorator.this.containsKey(object1) && TLongIntMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Long, Integer>> iterator() {
                return new Iterator<Entry<Long, Integer>>() {
                    private final TLongIntIterator it;

                    {
                        this.it = TLongIntMapDecorator.this._map.iterator();
                    }

                    public Entry<Long, Integer> next() {
                        this.it.advance();
                        long i = this.it.key();
                        final Long olong = i == TLongIntMapDecorator.this._map.getNoEntryKey() ? null : TLongIntMapDecorator.this.wrapKey(i);
                        int j = this.it.value();
                        final Integer integer = j == TLongIntMapDecorator.this._map.getNoEntryValue() ? null : TLongIntMapDecorator.this.wrapValue(j);

                        return new Entry<Long, Integer>() {
                            private Integer val = integer;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(olong) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Long getKey() {
                                return olong;
                            }

                            public Integer getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return olong.hashCode() + this.val.hashCode();
                            }

                            public Integer setValue(Integer integer1) {
                                this.val = integer1;
                                return TLongIntMapDecorator.this.put(olong, integer1);
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

            public boolean add(Entry<Long, Integer> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Long olong = (Long) ((Entry) object).getKey();

                    TLongIntMapDecorator.this._map.remove(TLongIntMapDecorator.this.unwrapKey(olong));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Long, Integer>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TLongIntMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Integer && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Long, ? extends Integer> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Long) entry.getKey(), (Integer) entry.getValue());
        }

    }

    protected Long wrapKey(long i) {
        return i;
    }

    protected long unwrapKey(Object object) {
        return (Long) object;
    }

    protected Integer wrapValue(int i) {
        return i;
    }

    protected int unwrapValue(Object object) {
        return (Integer) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TLongIntMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
