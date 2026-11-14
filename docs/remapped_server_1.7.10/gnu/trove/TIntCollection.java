package gnu.trove;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import java.util.Collection;

public interface TIntCollection {

    long serialVersionUID = 1L;

    int getNoEntryValue();

    int size();

    boolean isEmpty();

    boolean contains(int i);

    TIntIterator iterator();

    int[] toArray();

    int[] toArray(int[] aint);

    boolean add(int i);

    boolean remove(int i);

    boolean containsAll(Collection<?> collection);

    boolean containsAll(TIntCollection tintcollection);

    boolean containsAll(int[] aint);

    boolean addAll(Collection<? extends Integer> collection);

    boolean addAll(TIntCollection tintcollection);

    boolean addAll(int[] aint);

    boolean retainAll(Collection<?> collection);

    boolean retainAll(TIntCollection tintcollection);

    boolean retainAll(int[] aint);

    boolean removeAll(Collection<?> collection);

    boolean removeAll(TIntCollection tintcollection);

    boolean removeAll(int[] aint);

    void clear();

    boolean forEach(TIntProcedure tintprocedure);

    boolean equals(Object object);

    int hashCode();
}
