package gnu.trove.decorator;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
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

public class TIntIntMapDecorator extends AbstractMap<Integer, Integer> implements Map<Integer, Integer>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TIntIntMap _map;

    public TIntIntMapDecorator() {}

    public TIntIntMapDecorator(TIntIntMap tintintmap) {
        this._map = tintintmap;
    }

    public TIntIntMap getMap() {
        return this._map;
    }

    public Integer put(Integer integer, Integer integer1) {
        int i;

        if (integer == null) {
            i = this._map.getNoEntryKey();
        } else {
            i = this.unwrapKey(integer);
        }

        int j;

        if (integer1 == null) {
            j = this._map.getNoEntryValue();
        } else {
            j = this.unwrapValue(integer1);
        }

        int k = this._map.put(i, j);

        return k == this._map.getNoEntryValue() ? null : this.wrapValue(k);
    }

    public Integer get(Object object) {
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
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
        int i;

        if (object != null) {
            if (!(object instanceof Integer)) {
                return null;
            }

            i = this.unwrapKey(object);
        } else {
            i = this._map.getNoEntryKey();
        }

        int j = this._map.remove(i);

        return j == this._map.getNoEntryValue() ? null : this.wrapValue(j);
    }

    public Set<Entry<Integer, Integer>> entrySet() {
        return new AbstractSet<Entry<Integer, Integer>>() {
            public int size() {
                return TIntIntMapDecorator.this._map.size();
            }

            public boolean isEmpty() {
                return TIntIntMapDecorator.this.isEmpty();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Object object1 = ((Entry) object).getKey();
                    Object object2 = ((Entry) object).getValue();

                    return TIntIntMapDecorator.this.containsKey(object1) && TIntIntMapDecorator.this.get(object1).equals(object2);
                }
            }

            public Iterator<Entry<Integer, Integer>> iterator() {
                return new Iterator<Entry<Integer, Integer>>() {
                    private final TIntIntIterator it;

                    {
                        this.it = TIntIntMapDecorator.this._map.iterator();
                    }

                    public Entry<Integer, Integer> next() {
                        this.it.advance();
                        int i = this.it.key();
                        final Integer integer = i == TIntIntMapDecorator.this._map.getNoEntryKey() ? null : TIntIntMapDecorator.this.wrapKey(i);
                        int j = this.it.value();
                        final Integer integer1 = j == TIntIntMapDecorator.this._map.getNoEntryValue() ? null : TIntIntMapDecorator.this.wrapValue(j);

                        return new Entry<Integer, Integer>() {
                            private Integer val = integer1;

                            public boolean equals(Object object) {
                                return object instanceof Entry && ((Entry) object).getKey().equals(integer) && ((Entry) object).getValue().equals(this.val);
                            }

                            public Integer getKey() {
                                return integer;
                            }

                            public Integer getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return integer.hashCode() + this.val.hashCode();
                            }

                            public Integer setValue(Integer integer2) {
                                this.val = integer2;
                                return TIntIntMapDecorator.this.put(integer, integer2);
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

            public boolean add(Entry<Integer, Integer> entry) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object object) {
                boolean flag = false;

                if (this.contains(object)) {
                    Integer integer = (Integer) ((Entry) object).getKey();

                    TIntIntMapDecorator.this._map.remove(TIntIntMapDecorator.this.unwrapKey(integer));
                    flag = true;
                }

                return flag;
            }

            public boolean addAll(Collection<? extends Entry<Integer, Integer>> collection) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                TIntIntMapDecorator.this.clear();
            }
        };
    }

    public boolean containsValue(Object object) {
        return object instanceof Integer && this._map.containsValue(this.unwrapValue(object));
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

    public void putAll(Map<? extends Integer, ? extends Integer> map) {
        Iterator iterator = map.entrySet().iterator();
        int i = map.size();

        while (i-- > 0) {
            Entry entry = (Entry) iterator.next();

            this.put((Integer) entry.getKey(), (Integer) entry.getValue());
        }

    }

    protected Integer wrapKey(int i) {
        return i;
    }

    protected int unwrapKey(Object object) {
        return (Integer) object;
    }

    protected Integer wrapValue(int i) {
        return i;
    }

    protected int unwrapValue(Object object) {
        return (Integer) object;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._map = (TIntIntMap) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._map);
    }
}
