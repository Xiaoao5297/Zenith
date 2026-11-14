package gnu.trove.decorator;

import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
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

public class TLongLongMapDecorator extends AbstractMap<Long, Long> implements Map<Long, Long>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TLongLongMap _map;

    public TLongLongMapDecorator() {}

    public TLongLongMapDecorator(TLongLongMap tlonglongmap) {
        this._map = tlonglongmap;
    }

    public TLongLongMap getMap() {
        return this._map;
    }

    public Long put(Long olong, Long olong1) {
        long i;

        if (olong == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(olong);
        }

        long j;

        if (olong1 == null) {
            j = this._map.getNoEntryValue();
        } else {
            j = this.unwrapValue(olong1);
        }

        long k = this._map.put(i, j);

        return k == this._map.getNoEntryValue() ? null : this.wrapValue(k);
    }

    public Long get(Object object) {
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
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
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        long j = this._map.remove(i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public Set<Entry<Long, Long>> entrySet() {
        return new AbstractSet<Entry<Long, Long>>() {
            public int size() {
                return TLongLongMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TLongLongMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TLongLongMapDecorator.this.containsKey(object1) && TLongLongMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Long, Long>> iterator() {
                return new Iterator<Entry<Long, Long>>() {
                    private final TLongLongIterator it;

                    {
                        this.it = TLongLongMapDecorator.this._map.iterator();
                    }

                    public Entry<Long, Long> next() {
                        this.it.advance();
                        long i = this.it.key();
                        final Long olong = i == TLongLongMapDecorator.this._map.getNoEntryKey() ? null : TLongLongMapDecorator.this.wrapKey(i);
                        long j = this.it.value();
                        final Long olong1 = j == TLongLongMapDecorator.this._map.getNoEntryValue() ? null : TLongLongMapDecorator.this.wrapValue(j);

                        return new Entry<Long, Long>() {
                            private Long val = olong1;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(olong) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Long getKey() {
                                return olong;
                            }

                            public Long getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return olong.hashCode() + this.val.hashCode();
                            }

                            public Long setValue(Long olong2) {
                                this.val = olong2;
                                return TLongLongMapDecorator.this.put(olong, olong2);
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

            public boolean add(Entry<Long, Long> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Long olong = (Long) ((Entry) object).getKey();

                    TLongLongMapDecorator.this._map.remove(TLongLongMapDecorator.this.unwrapKey(olong));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Long, Long>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TLongLongMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Long && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Long, ? extends Long> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Long) entry.getKey(), (Long) entry.getValue());
        }

    }

    protected Long wrapKey(long i) {
        return i;
    }

    protected long unwrapKey(Object object) {
        return (Long) object;
    }

    protected Long wrapValue(long i) {
        return i;
    }

    protected long unwrapValue(Object object) {
        return (Long) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TLongLongMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
