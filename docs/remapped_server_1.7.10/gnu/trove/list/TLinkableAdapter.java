package gnu.trove.list;

public abstract class TLinkableAdapter<T extends TLinkable> implements TLinkable<T> {

    private volatile T next;
    private volatile T prev;

    public TLinkableAdapter() {}

    @Override
    public T getNext() {
        return this.next;
    }

    @Override
    public void setNext(T t0) {
        this.next = t0;
    }

    @Override
    public T getPrevious() {
        return this.prev;
    }

    @Override
    public void setPrevious(T t0) {
        this.prev = t0;
    }
}
