package gnu.trove.decorator;

import gnu.trove.iterator.TShortLongIterator;
import gnu.trove.map.TShortLongMap;
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

public class TShortLongMapDecorator extends AbstractMap<Short, Long> implements Map<Short, Long>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TShortLongMap _map;

    public TShortLongMapDecorator() {}

    public TShortLongMapDecorator(TShortLongMap tshortlongmap) {
        this._map = tshortlongmap;
    }

    public TShortLongMap getMap() {
        return this._map;
    }

    public Long put(Short oshort, Long olong) {
        short short0;

        if (oshort == null) {
            short0 = this._map.getNoEntryKey();
        } else {
            short0 = this.unwrapKey(oshort);
        }

        long i;

        if (olong == null) {
            i = this._map.getNoEntryValue();
        } else {
            i = this.unwrapValue(olong);
        }

        long j = this._map.put(short0, i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public Long get(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey(object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        long i = this._map.get(short0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public void clear() {
        this._map.clear();
    }

    public Long remove(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey(object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        long i = this._map.remove(short0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public Set<Entry<Short, Long>> entrySet() {
        return new AbstractSet<Entry<Short, Long>>() {
            public int size() {
                return TShortLongMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TShortLongMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TShortLongMapDecorator.this.containsKey(object1) && TShortLongMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Short, Long>> iterator() {
                return new Iterator<Entry<Short, Long>>() {
                    private final TShortLongIterator it;

                    {
                        this.it = TShortLongMapDecorator.this._map.iterator();
                    }

                    public Entry<Short, Long> next() {
                        this.it.advance();
                        short short0 = this.it.key();
                        final Short oshort = short0 == TShortLongMapDecorator.this._map.getNoEntryKey() ? null : TShortLongMapDecorator.this.wrapKey(short0);
                        long i = this.it.value();
                        final Long olong = i == TShortLongMapDecorator.this._map.getNoEntryValue() ? null : TShortLongMapDecorator.this.wrapValue(i);

                        return new Entry<Short, Long>() {
                            private Long val = olong;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(oshort) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Short getKey() {
                                return oshort;
                            }

                            public Long getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return oshort.hashCode() + this.val.hashCode();
                            }

                            public Long setValue(Long olong1) {
                                this.val = olong1;
                                return TShortLongMapDecorator.this.put(oshort, olong1);
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

            public boolean add(Entry<Short, Long> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Short oshort = (Short) ((Entry) object).getKey();

                    TShortLongMapDecorator.this._map.remove(TShortLongMapDecorator.this.unwrapKey(oshort));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Short, Long>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TShortLongMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Long && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Short, ? extends Long> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Short) entry.getKey(), (Long) entry.getValue());
        }

    }

    protected Short wrapKey(short short0) {
        return short0;
    }

    protected short unwrapKey(Object object) {
        return (Short) object;
    }

    protected Long wrapValue(long i) {
        return i;
    }

    protected long unwrapValue(Object object) {
        return (Long) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TShortLongMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
