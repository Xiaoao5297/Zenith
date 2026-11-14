package gnu.trove.decorator;

import gnu.trove.iterator.TIntShortIterator;
import gnu.trove.map.TIntShortMap;
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

public class TIntShortMapDecorator extends AbstractMap<Integer, Short> implements Map<Integer, Short>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TIntShortMap _map;

    public TIntShortMapDecorator() {}

    public TIntShortMapDecorator(TIntShortMap tintshortmap) {
        this._map = tintshortmap;
    }

    public TIntShortMap getMap() {
        return this._map;
    }

    public Short put(Integer integer, Short oshort) {
        int i;

        if (integer == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(integer);
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
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
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
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        short short0 = this._map.remove(i);

        return short0 == this._map.getNoEntryValue() ? null : this.wrapValue(short0);
    }

    public Set<Entry<Integer, Short>> entrySet() {
        return new AbstractSet<Entry<Integer, Short>>() {
            public int size() {
                return TIntShortMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TIntShortMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TIntShortMapDecorator.this.containsKey(object1) && TIntShortMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Integer, Short>> iterator() {
                return new Iterator<Entry<Integer, Short>>() {
                    private final TIntShortIterator it;

                    {
                        this.it = TIntShortMapDecorator.this._map.iterator();
                    }

                    public Entry<Integer, Short> next() {
                        this.it.advance();
                        int i = this.it.key();
                        final Integer integer = i == TIntShortMapDecorator.this._map.getNoEntryKey() ? null : TIntShortMapDecorator.this.wrapKey(i);
                        short short0 = this.it.value();
                        final Short oshort = short0 == TIntShortMapDecorator.this._map.getNoEntryValue() ? null : TIntShortMapDecorator.this.wrapValue(short0);

                        return new Entry<Integer, Short>() {
                            private Short val = oshort;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(integer) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Integer getKey() {
                                return integer;
                            }

                            public Short getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return integer.hashCode() + this.val.hashCode();
                            }

                            public Short setValue(Short oshort1) {
                                this.val = oshort1;
                                return TIntShortMapDecorator.this.put(integer, oshort1);
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

            public boolean add(Entry<Integer, Short> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Integer integer = (Integer) ((Entry) object).getKey();

                    TIntShortMapDecorator.this._map.remove(TIntShortMapDecorator.this.unwrapKey(integer));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Integer, Short>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TIntShortMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Short && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Integer, ? extends Short> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Integer) entry.getKey(), (Short) entry.getValue());
        }

    }

    protected Integer wrapKey(int i) {
        return i;
    }

    protected int unwrapKey(Object object) {
        return (Integer) object;
    }

    protected Short wrapValue(short short0) {
        return short0;
    }

    protected short unwrapValue(Object object) {
        return (Short) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TIntShortMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
