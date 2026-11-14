package gnu.trove;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.procedure.TLongProcedure;
import java.util.Collection;

public interface TLongCollection {

    long serialVersionUID = 1L;

    long getNoEntryValue();

    int size();

    boolean isEmpty();

    boolean contains(long i);

    TLongIterator iterator();

    long[] toArray();

    long[] toArray(long[] along);

    boolean add(long i);

    boolean remove(long i);

    boolean containsAll(Collection<?> collection);

    boolean containsAll(TLongCollection tlongcollection);

    boolean containsAll(long[] along);

    boolean addAll(Collection<? extends Long> collection);

    boolean addAll(TLongCollection tlongcollection);

    boolean addAll(long[] along);

    boolean retainAll(Collection<?> collection);

    boolean retainAll(TLongCollection tlongcollection);

    boolean retainAll(long[] along);

    boolean removeAll(Collection<?> collection);

    boolean removeAll(TLongCollection tlongcollection);

    boolean removeAll(long[] along);

    void clear();

    boolean forEach(TLongProcedure tlongprocedure);

    boolean equals(Object object);

    int hashCode();
}
