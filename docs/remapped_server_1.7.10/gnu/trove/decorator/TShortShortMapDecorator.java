package gnu.trove.decorator;

import gnu.trove.iterator.TShortShortIterator;
import gnu.trove.map.TShortShortMap;
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

public class TShortShortMapDecorator extends AbstractMap<Short, Short> implements Map<Short, Short>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TShortShortMap _map;

    public TShortShortMapDecorator() {}

    public TShortShortMapDecorator(TShortShortMap tshortshortmap) {
        this._map = tshortshortmap;
    }

    public TShortShortMap getMap() {
        return this._map;
    }

    public Short put(Short oshort, Short oshort1) {
        short short0;

        if (oshort == null) {
            short0 = this._map.getNoEntryKey();
        } else {
            short0 = this.unwrapKey(oshort);
        }

        short short1;

        if (oshort1 == null) {
            short1 = this._map.getNoEntryValue();
        } else {
            short1 = this.unwrapValue(oshort1);
        }

        short short2 = this._map.put(short0, short1);

        return short2 == this._map.getNoEntryValue() ? null : this.wrapValue(short2);
    }

    public Short get(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey(object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        short short1 = this._map.get(short0);

        return short1 == this._map.getNoEntryValue() ? null : this.wrapValue(short1);
    }

    public void clear() {
        this._map.clear();
    }

    public Short remove(Object object) {
        short short0;

        if (object != null) {
            if (!(object instanceof Short)) {
                return null;
            }

            short0 = this.unwrapKey(object);
        } else {
            short0 = this._map.getNoEntryKey();
        }

        short short1 = this._map.remove(short0);

        return short1 == this._map.getNoEntryValue() ? null : this.wrapValue(short1);
    }

    public Set<Entry<Short, Short>> entrySet() {
        return new AbstractSet<Entry<Short, Short>>() {
            public int size() {
                return TShortShortMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TShortShortMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TShortShortMapDecorator.this.containsKey(object1) && TShortShortMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Short, Short>> iterator() {
                return new Iterator<Entry<Short, Short>>() {
                    private final TShortShortIterator it;

                    {
                        this.it = TShortShortMapDecorator.this._map.iterator();
                    }

                    public Entry<Short, Short> next() {
                        this.it.advance();
                        short short0 = this.it.key();
                        final Short oshort = short0 == TShortShortMapDecorator.this._map.getNoEntryKey() ? null : TShortShortMapDecorator.this.wrapKey(short0);
                        short short1 = this.it.value();
                        final Short oshort1 = short1 == TShortShortMapDecorator.this._map.getNoEntryValue() ? null : TShortShortMapDecorator.this.wrapValue(short1);

                        return new Entry<Short, Short>() {
                            private Short val = oshort1;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(oshort) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Short getKey() {
                                return oshort;
                            }

                            public Short getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return oshort.hashCode() + this.val.hashCode();
                            }

                            public Short setValue(Short oshort2) {
                                this.val = oshort2;
                                return TShortShortMapDecorator.this.put(oshort, oshort2);
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

            public boolean add(Entry<Short, Short> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Short oshort = (Short) ((Entry) object).getKey();

                    TShortShortMapDecorator.this._map.remove(TShortShortMapDecorator.this.unwrapKey(oshort));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Short, Short>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TShortShortMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Short && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Short, ? extends Short> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Short) entry.getKey(), (Short) entry.getValue());
        }

    }

    protected Short wrapKey(short short0) {
        return short0;
    }

    protected short unwrapKey(Object object) {
        return (Short) object;
    }

    protected Short wrapValue(short short0) {
        return short0;
    }

    protected short unwrapValue(Object object) {
        return (Short) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TShortShortMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
