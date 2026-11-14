package gnu.trove.list.linked;

import gnu.trove.list.TLinkable;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSequentialList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class TLinkedList<T extends TLinkable<T>> extends AbstractSequentialList<T> implements Externalizable {

    static final long serialVersionUID = 1L;
    protected T _head;
    protected T _tail;
    protected int _size = 0;

    public TLinkedList() {}

    public ListIterator<T> listIterator(int i) {
        return new TLinkedList.IteratorImpl(i);
    }

    public int size() {
        return this._size;
    }

    public void add(int i, T t0) {
        if (i >= 0 && i <= this.size()) {
            this.insert(i, t0);
        } else {
            throw new IndexOutOfBoundsException("index:" + i);
        }
    }

    public boolean add(T t0) {
        this.insert(this._size, t0);
        return true;
    }

    public void addFirst(T t0) {
        this.insert(0, t0);
    }

    public void addLast(T t0) {
        this.insert(this.size(), t0);
    }

    public void clear() {
        if (null != this._head) {
            for (TLinkable tlinkable = this._head.getNext(); tlinkable != null; tlinkable = tlinkable.getNext()) {
                TLinkable tlinkable1 = tlinkable.getPrevious();

                tlinkable1.setNext((TLinkable) null);
                tlinkable.setPrevious((TLinkable) null);
            }

            this._head = this._tail = null;
        }

        this._size = 0;
    }

    public Object[] toArray() {
        Object[] aobject = new Object[this._size];
        int i = 0;

        for (TLinkable tlinkable = this._head; tlinkable != null; tlinkable = tlinkable.getNext()) {
            aobject[i++] = tlinkable;
        }

        return aobject;
    }

    public Object[] toUnlinkedArray() {
        Object[] aobject = new Object[this._size];
        int i = 0;

        for (TLinkable tlinkable = this._head; tlinkable != null; ++i) {
            aobject[i] = tlinkable;
            TLinkable tlinkable1 = tlinkable;

            tlinkable = tlinkable.getNext();
            tlinkable1.setNext((TLinkable) null);
            tlinkable1.setPrevious((TLinkable) null);
        }

        this._size = 0;
        this._head = this._tail = null;
        return aobject;
    }

    public T[] toUnlinkedArray(T[] at) {
        int i = this.size();

        if (at.length < i) {
            at = (TLinkable[]) ((TLinkable[]) Array.newInstance(at.getClass().getComponentType(), i));
        }

        int j = 0;

        for (TLinkable tlinkable = this._head; tlinkable != null; ++j) {
            at[j] = tlinkable;
            TLinkable tlinkable1 = tlinkable;

            tlinkable = tlinkable.getNext();
            tlinkable1.setNext((TLinkable) null);
            tlinkable1.setPrevious((TLinkable) null);
        }

        this._size = 0;
        this._head = this._tail = null;
        return at;
    }

    public boolean contains(Object object) {
        for (TLinkable tlinkable = this._head; tlinkable != null; tlinkable = tlinkable.getNext()) {
            if (object.equals(tlinkable)) {
                return true;
            }
        }

        return false;
    }

    public T get(int i) {
        if (i >= 0 && i < this._size) {
            int j;
            TLinkable tlinkable;

            if (i > this._size >> 1) {
                j = this._size - 1;

                for (tlinkable = this._tail; j > i; --j) {
                    tlinkable = tlinkable.getPrevious();
                }

                return tlinkable;
            } else {
                j = 0;

                for (tlinkable = this._head; j < i; ++j) {
                    tlinkable = tlinkable.getNext();
                }

                return tlinkable;
            }
        } else {
            throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + this._size);
        }
    }

    public T getFirst() {
        return this._head;
    }

    public T getLast() {
        return this._tail;
    }

    public T getNext(T t0) {
        return t0.getNext();
    }

    public T getPrevious(T t0) {
        return t0.getPrevious();
    }

    public T removeFirst() {
        TLinkable tlinkable = this._head;

        if (tlinkable == null) {
            return null;
        } else {
            TLinkable tlinkable1 = tlinkable.getNext();

            tlinkable.setNext((TLinkable) null);
            if (null != tlinkable1) {
                tlinkable1.setPrevious((TLinkable) null);
            }

            this._head = tlinkable1;
            if (--this._size == 0) {
                this._tail = null;
            }

            return tlinkable;
        }
    }

    public T removeLast() {
        TLinkable tlinkable = this._tail;

        if (tlinkable == null) {
            return null;
        } else {
            TLinkable tlinkable1 = tlinkable.getPrevious();

            tlinkable.setPrevious((TLinkable) null);
            if (null != tlinkable1) {
                tlinkable1.setNext((TLinkable) null);
            }

            this._tail = tlinkable1;
            if (--this._size == 0) {
                this._head = null;
            }

            return tlinkable;
        }
    }

    protected void insert(int i, T t0) {
        if (this._size == 0) {
            this._head = this._tail = t0;
        } else if (i == 0) {
            t0.setNext(this._head);
            this._head.setPrevious(t0);
            this._head = t0;
        } else if (i == this._size) {
            this._tail.setNext(t0);
            t0.setPrevious(this._tail);
            this._tail = t0;
        } else {
            TLinkable tlinkable = this.get(i);
            TLinkable tlinkable1 = tlinkable.getPrevious();

            if (tlinkable1 != null) {
                tlinkable1.setNext(t0);
            }

            t0.setPrevious(tlinkable1);
            t0.setNext(tlinkable);
            tlinkable.setPrevious(t0);
        }

        ++this._size;
    }

    public boolean remove(Object object) {
        if (!(object instanceof TLinkable)) {
            return false;
        } else {
            TLinkable tlinkable = (TLinkable) object;
            TLinkable tlinkable1 = tlinkable.getPrevious();
            TLinkable tlinkable2 = tlinkable.getNext();

            if (tlinkable2 == null && tlinkable1 == null) {
                if (object != this._head) {
                    return false;
                }

                this._head = this._tail = null;
            } else if (tlinkable2 == null) {
                tlinkable.setPrevious((TLinkable) null);
                tlinkable1.setNext((TLinkable) null);
                this._tail = tlinkable1;
            } else if (tlinkable1 == null) {
                tlinkable.setNext((TLinkable) null);
                tlinkable2.setPrevious((TLinkable) null);
                this._head = tlinkable2;
            } else {
                tlinkable1.setNext(tlinkable2);
                tlinkable2.setPrevious(tlinkable1);
                tlinkable.setNext((TLinkable) null);
                tlinkable.setPrevious((TLinkable) null);
            }

            --this._size;
            return true;
        }
    }

    public void addBefore(T t0, T t1) {
        if (t0 == this._head) {
            this.addFirst(t1);
        } else if (t0 == null) {
            this.addLast(t1);
        } else {
            TLinkable tlinkable = t0.getPrevious();

            t1.setNext(t0);
            tlinkable.setNext(t1);
            t1.setPrevious(tlinkable);
            t0.setPrevious(t1);
            ++this._size;
        }

    }

    public void addAfter(T t0, T t1) {
        if (t0 == this._tail) {
            this.addLast(t1);
        } else if (t0 == null) {
            this.addFirst(t1);
        } else {
            TLinkable tlinkable = t0.getNext();

            t1.setPrevious(t0);
            t1.setNext(tlinkable);
            t0.setNext(t1);
            tlinkable.setPrevious(t1);
            ++this._size;
        }

    }

    public boolean forEachValue(TObjectProcedure<T> tobjectprocedure) {
        for (TLinkable tlinkable = this._head; tlinkable != null; tlinkable = tlinkable.getNext()) {
            boolean flag = tobjectprocedure.execute(tlinkable);

            if (!flag) {
                return false;
            }
        }

        return true;
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeInt(this._size);
        objectoutput.writeObject(this._head);
        objectoutput.writeObject(this._tail);
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._size = objectinput.readInt();
        this._head = (TLinkable) objectinput.readObject();
        this._tail = (TLinkable) objectinput.readObject();
    }

    protected final class IteratorImpl implements ListIterator<T> {

        private int _nextIndex = 0;
        private T _next;
        private T _lastReturned;

        IteratorImpl(int i) {
            if (i >= 0 && i <= TLinkedList.this._size) {
                this._nextIndex = i;
                if (i == 0) {
                    this._next = TLinkedList.this._head;
                } else if (i == TLinkedList.this._size) {
                    this._next = null;
                } else {
                    int j;

                    if (i < TLinkedList.this._size >> 1) {
                        j = 0;

                        for (this._next = TLinkedList.this._head; j < i; ++j) {
                            this._next = this._next.getNext();
                        }
                    } else {
                        j = TLinkedList.this._size - 1;

                        for (this._next = TLinkedList.this._tail; j > i; --j) {
                            this._next = this._next.getPrevious();
                        }
                    }
                }

            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        public final void add(T t0) {
            this._lastReturned = null;
            ++this._nextIndex;
            if (TLinkedList.this._size == 0) {
                TLinkedList.this.add(t0);
            } else {
                TLinkedList.this.addBefore(this._next, t0);
            }

        }

        public final boolean hasNext() {
            return this._nextIndex != TLinkedList.this._size;
        }

        public final boolean hasPrevious() {
            return this._nextIndex != 0;
        }

        public final T next() {
            if (this._nextIndex == TLinkedList.this._size) {
                throw new NoSuchElementException();
            } else {
                this._lastReturned = this._next;
                this._next = this._next.getNext();
                ++this._nextIndex;
                return this._lastReturned;
            }
        }

        public final int nextIndex() {
            return this._nextIndex;
        }

        public final T previous() {
            if (this._nextIndex == 0) {
                throw new NoSuchElementException();
            } else {
                if (this._nextIndex == TLinkedList.this._size) {
                    this._lastReturned = this._next = TLinkedList.this._tail;
                } else {
                    this._lastReturned = this._next = this._next.getPrevious();
                }

                --this._nextIndex;
                return this._lastReturned;
            }
        }

        public final int previousIndex() {
            return this._nextIndex - 1;
        }

        public final void remove() {
            if (this._lastReturned == null) {
                throw new IllegalStateException("must invoke next or previous before invoking remove");
            } else {
                if (this._lastReturned != this._next) {
                    --this._nextIndex;
                }

                this._next = this._lastReturned.getNext();
                TLinkedList.this.remove(this._lastReturned);
                this._lastReturned = null;
            }
        }

        public final void set(T t0) {
            if (this._lastReturned == null) {
                throw new IllegalStateException();
            } else {
                this.swap(this._lastReturned, t0);
                this._lastReturned = t0;
            }
        }

        private void swap(T t0, T t1) {
            TLinkable tlinkable = t0.getPrevious();
            TLinkable tlinkable1 = t0.getNext();
            TLinkable tlinkable2 = t1.getPrevious();
            TLinkable tlinkable3 = t1.getNext();

            if (tlinkable1 == t1) {
                if (tlinkable != null) {
                    tlinkable.setNext(t1);
                }

                t1.setPrevious(tlinkable);
                t1.setNext(t0);
                t0.setPrevious(t1);
                t0.setNext(tlinkable3);
                if (tlinkable3 != null) {
                    tlinkable3.setPrevious(t0);
                }
            } else if (tlinkable3 == t0) {
                if (tlinkable2 != null) {
                    tlinkable2.setNext(t1);
                }

                t1.setPrevious(t0);
                t1.setNext(tlinkable1);
                t0.setPrevious(tlinkable2);
                t0.setNext(t1);
                if (tlinkable1 != null) {
                    tlinkable1.setPrevious(t1);
                }
            } else {
                t0.setNext(tlinkable3);
                t0.setPrevious(tlinkable2);
                if (tlinkable2 != null) {
                    tlinkable2.setNext(t0);
                }

                if (tlinkable3 != null) {
                    tlinkable3.setPrevious(t0);
                }

                t1.setNext(tlinkable1);
                t1.setPrevious(tlinkable);
                if (tlinkable != null) {
                    tlinkable.setNext(t1);
                }

                if (tlinkable1 != null) {
                    tlinkable1.setPrevious(t1);
                }
            }

            if (TLinkedList.this._head == t0) {
                TLinkedList.this._head = t1;
            } else if (TLinkedList.this._head == t1) {
                TLinkedList.this._head = t0;
            }

            if (TLinkedList.this._tail == t0) {
                TLinkedList.this._tail = t1;
            } else if (TLinkedList.this._tail == t1) {
                TLinkedList.this._tail = t0;
            }

            if (this._lastReturned == t0) {
                this._lastReturned = t1;
            } else if (this._lastReturned == t1) {
                this._lastReturned = t0;
            }

            if (this._next == t0) {
                this._next = t1;
            } else if (this._next == t1) {
                this._next = t0;
            }

        }
    }
}
