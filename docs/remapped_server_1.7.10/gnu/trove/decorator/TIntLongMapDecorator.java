package gnu.trove.decorator;

import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.TIntLongMap;
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

public class TIntLongMapDecorator extends AbstractMap<Integer, Long> implements Map<Integer, Long>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TIntLongMap _map;

    public TIntLongMapDecorator() {}

    public TIntLongMapDecorator(TIntLongMap tintlongmap) {
        this._map = tintlongmap;
    }

    public TIntLongMap getMap() {
        return this._map;
    }

    public Long put(Integer integer, Long olong) {
        int i;

        if (integer == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(integer);
        }

        long j;

        if (olong == null) {
            j = this._map.getNoEntryValue();
        } else {
            j = this.unwrapValue(olong);
        }

        long k = this._map.put(i, j);

        return k == this._map.getNoEntryValue() ? null : this.wrapValue(k);
    }

    public Long get(Object object) {
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        long j = this._map.get(i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public void clear() {
        this._map.clear();
    }

    public Long remove(Object object) {
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        long j = this._map.remove(i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public Set<Entry<Integer, Long>> entrySet() {
        return new AbstractSet<Entry<Integer, Long>>() {
            public int size() {
                return TIntLongMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TIntLongMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TIntLongMapDecorator.this.containsKey(object1) && TIntLongMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Integer, Long>> iterator() {
                return new Iterator<Entry<Integer, Long>>() {
                    private final TIntLongIterator it;

                    {
                        this.it = TIntLongMapDecorator.this._map.iterator();
                    }

                    public Entry<Integer, Long> next() {
                        this.it.advance();
                        int i = this.it.key();
                        final Integer integer = i == TIntLongMapDecorator.this._map.getNoEntryKey() ? null : TIntLongMapDecorator.this.wrapKey(i);
                        long j = this.it.value();
                        final Long olong = j == TIntLongMapDecorator.this._map.getNoEntryValue() ? null : TIntLongMapDecorator.this.wrapValue(j);

                        return new Entry<Integer, Long>() {
                            private Long val = olong;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(integer) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Integer getKey() {
                                return integer;
                            }

                            public Long getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return integer.hashCode() + this.val.hashCode();
                            }

                            public Long setValue(Long olong1) {
                                this.val = olong1;
                                return TIntLongMapDecorator.this.put(integer, olong1);
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

            public boolean add(Entry<Integer, Long> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Integer integer = (Integer) ((Entry) object).getKey();

                    TIntLongMapDecorator.this._map.remove(TIntLongMapDecorator.this.unwrapKey(integer));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Integer, Long>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TIntLongMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Long && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Integer, ? extends Long> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Integer) entry.getKey(), (Long) entry.getValue());
        }

    }

    protected Integer wrapKey(int i) {
        return i;
    }

    protected int unwrapKey(Object object) {
        return (Integer) object;
    }

    protected Long wrapValue(long i) {
        return i;
    }

    protected long unwrapValue(Object object) {
        return (Long) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TIntLongMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
