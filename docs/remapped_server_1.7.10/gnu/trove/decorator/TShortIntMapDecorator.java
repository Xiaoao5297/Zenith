package gnu.trove.decorator;

import gnu.trove.iterator.TShortIntIterator;
import gnu.trove.map.TShortIntMap;
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

public class TShortIntMapDecorator extends AbstractMap<Short, Integer> implements Map<Short, Integer>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TShortIntMap _map;

    public TShortIntMapDecorator() {}

    public TShortIntMapDecorator(TShortIntMap tshortintmap) {
        this._map = tshortintmap;
    }

    public TShortIntMap getMap() {
        return this._map;
    }

    public Integer put(Short oshort, Integer integer) {
        short short0;

        if (oshort == null) {
            short0 = this._map.getNoEntryKey();
        } else {
            short0 = this.unwrapKey(oshort);
        }

        int i;

        if (integer == null) {
            i = this._map.getNoEntryValue();
        } else {
            i = this.unwrapValue(integer);
        }

        int j = this._map.put(short0, i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public Integer get(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey(object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        int i = this._map.get(short0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public void clear() {
        this._map.clear();
    }

    public Integer remove(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey(object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        int i = this._map.remove(short0);

        return i == this._map.getNoEntryValue() ? null : this.wrapValue(i);
    }

    public Set<Entry<Short, Integer>> entrySet() {
        return new AbstractSet<Entry<Short, Integer>>() {
            public int size() {
                return TShortIntMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TShortIntMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TShortIntMapDecorator.this.containsKey(object1) && TShortIntMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Short, Integer>> iterator() {
                return new Iterator<Entry<Short, Integer>>() {
                    private final TShortIntIterator it;

                    {
                        this.it = TShortIntMapDecorator.this._map.iterator();
                    }

                    public Entry<Short, Integer> next() {
                        this.it.advance();
                        short short0 = this.it.key();
                        final Short oshort = short0 == TShortIntMapDecorator.this._map.getNoEntryKey() ? null : TShortIntMapDecorator.this.wrapKey(short0);
                        int i = this.it.value();
                        final Integer integer = i == TShortIntMapDecorator.this._map.getNoEntryValue() ? null : TShortIntMapDecorator.this.wrapValue(i);

                        return new Entry<Short, Integer>() {
                            private Integer val = integer;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(oshort) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Short getKey() {
                                return oshort;
                            }

                            public Integer getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return oshort.hashCode() + this.val.hashCode();
                            }

                            public Integer setValue(Integer integer1) {
                                this.val = integer1;
                                return TShortIntMapDecorator.this.put(oshort, integer1);
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

            public boolean add(Entry<Short, Integer> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Short oshort = (Short) ((Entry) object).getKey();

                    TShortIntMapDecorator.this._map.remove(TShortIntMapDecorator.this.unwrapKey(oshort));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Short, Integer>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TShortIntMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Integer && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Short, ? extends Integer> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Short) entry.getKey(), (Integer) entry.getValue());
        }

    }

    protected Short wrapKey(short short0) {
        return short0;
    }

    protected short unwrapKey(Object object) {
        return (Short) object;
    }

    protected Integer wrapValue(int i) {
        return i;
    }

    protected int unwrapValue(Object object) {
        return (Integer) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TShortIntMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
