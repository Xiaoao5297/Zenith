package gnu.trove.set;

import gnu.trove.TLongCollection;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.procedure.TLongProcedure;
import java.util.Collection;

public interface TLongSet extends TLongCollection {

    @Override
    long getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean contains(long i);

    @Override
    TLongIterator iterator();

    @Override
    long[] toArray();

    @Override
    long[] toArray(long[] along);

    @Override
    boolean add(long i);

    @Override
    boolean remove(long i);

    @Override
    boolean containsAll(Collection<?> collection);

    @Override
    boolean containsAll(TLongCollection tlongcollection);

    @Override
    boolean containsAll(long[] along);

    @Override
    boolean addAll(Collection<? extends Long> collection);

    @Override
    boolean addAll(TLongCollection tlongcollection);

    @Override
    boolean addAll(long[] along);

    @Override
    boolean retainAll(Collection<?> collection);

    @Override
    boolean retainAll(TLongCollection tlongcollection);

    @Override
    boolean retainAll(long[] along);

    @Override
    boolean removeAll(Collection<?> collection);

    @Override
    boolean removeAll(TLongCollection tlongcollection);

    @Override
    boolean removeAll(long[] along);

    @Override
    void clear();

    @Override
    boolean forEach(TLongProcedure tlongprocedure);

    @Override
    boolean equals(Object object);

    @Override
    int hashCode();
}
