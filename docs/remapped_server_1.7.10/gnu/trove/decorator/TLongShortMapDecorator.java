package gnu.trove.decorator;

import gnu.trove.iterator.TLongShortIterator;
import gnu.trove.map.TLongShortMap;
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

public class TLongShortMapDecorator extends AbstractMap<Long, Short> implements Map<Long, Short>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TLongShortMap _map;

    public TLongShortMapDecorator() {}

    public TLongShortMapDecorator(TLongShortMap tlongshortmap) {
        this._map = tlongshortmap;
    }

    public TLongShortMap getMap() {
        return this._map;
    }

    public Short put(Long olong, Short oshort) {
        long i;

        if (olong == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(olong);
        }

        short short0;

        if (oshort == null) {
            short0 = this._map.getNoEntryValue();
        } else {
            short0 = this.unwrapValue(oshort);
        }

        short short1 = this._map.put(i, short0);

        return short1 == this._map.getNoEntryValue() ? null : this.wrapValue(short1);
    }

    public Short get(Object object) {
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        short short0 = this._map.get(i);

        return short0 == this._map.getNoEntryValue() ? null : this.wrapValue(short0);
    }

    public void clear() {
        this._map.clear();
    }

    public Short remove(Object object) {
        long i;

        if (object != null) {
            if (!(object instanceof Long)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        short short0 = this._map.remove(i);

        return short0 == this._map.getNoEntryValue() ? null : this.wrapValue(short0);
    }

    public Set<Entry<Long, Short>> entrySet() {
        return new AbstractSet<Entry<Long, Short>>() {
            public int size() {
                return TLongShortMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TLongShortMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TLongShortMapDecorator.this.containsKey(object1) && TLongShortMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Long, Short>> iterator() {
                return new Iterator<Entry<Long, Short>>() {
                    private final TLongShortIterator it;

                    {
                        this.it = TLongShortMapDecorator.this._map.iterator();
                    }

                    public Entry<Long, Short> next() {
                        this.it.advance();
                        long i = this.it.key();
                        final Long olong = i == TLongShortMapDecorator.this._map.getNoEntryKey() ? null : TLongShortMapDecorator.this.wrapKey(i);
                        short short0 = this.it.value();
                        final Short oshort = short0 == TLongShortMapDecorator.this._map.getNoEntryValue() ? null : TLongShortMapDecorator.this.wrapValue(short0);

                        return new Entry<Long, Short>() {
                            private Short val = oshort;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(olong) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Long getKey() {
                                return olong;
                            }

                            public Short getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return olong.hashCode() + this.val.hashCode();
                            }

                            public Short setValue(Short oshort1) {
                                this.val = oshort1;
                                return TLongShortMapDecorator.this.put(olong, oshort1);
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

            public boolean add(Entry<Long, Short> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Long olong = (Long) ((Entry) object).getKey();

                    TLongShortMapDecorator.this._map.remove(TLongShortMapDecorator.this.unwrapKey(olong));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Long, Short>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TLongShortMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Short && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Long, ? extends Short> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Long) entry.getKey(), (Short) entry.getValue());
        }

    }

    protected Long wrapKey(long i) {
        return i;
    }

    protected long unwrapKey(Object object) {
        return (Long) object;
    }

    protected Short wrapValue(short short0) {
        return short0;
    }

    protected short unwrapValue(Object object) {
        return (Short) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TLongShortMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
