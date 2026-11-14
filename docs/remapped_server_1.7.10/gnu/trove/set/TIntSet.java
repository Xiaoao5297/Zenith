package gnu.trove.set;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import java.util.Collection;

public interface TIntSet extends TIntCollection {

    @Override
    int getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean contains(int i);

    @Override
    TIntIterator iterator();

    @Override
    int[] toArray();

    @Override
    int[] toArray(int[] aint);

    @Override
    boolean add(int i);

    @Override
    boolean remove(int i);

    @Override
    boolean containsAll(Collection<?> collection);

    @Override
    boolean containsAll(TIntCollection tintcollection);

    @Override
    boolean containsAll(int[] aint);

    @Override
    boolean addAll(Collection<? extends Integer> collection);

    @Override
    boolean addAll(TIntCollection tintcollection);

    @Override
    boolean addAll(int[] aint);

    @Override
    boolean retainAll(Collection<?> collection);

    @Override
    boolean retainAll(TIntCollection tintcollection);

    @Override
    boolean retainAll(int[] aint);

    @Override
    boolean removeAll(Collection<?> collection);

    @Override
    boolean removeAll(TIntCollection tintcollection);

    @Override
    boolean removeAll(int[] aint);

    @Override
    void clear();

    @Override
    boolean forEach(TIntProcedure tintprocedure);

    @Override
    boolean equals(Object object);

    @Override
    int hashCode();
}
