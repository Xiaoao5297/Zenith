package gnu.trove;

import gnu.trove.iterator.TShortIterator;
import gnu.trove.procedure.TShortProcedure;
import java.util.Collection;

public interface TShortCollection {

    long serialVersionUID = 1L;

    short getNoEntryValue();

    int size();

    boolean isEmpty();

    boolean contains(short short0);

    TShortIterator iterator();

    short[] toArray();

    short[] toArray(short[] ashort);

    boolean add(short short0);

    boolean remove(short short0);

    boolean containsAll(Collection<?> collection);

    boolean containsAll(TShortCollection tshortcollection);

    boolean containsAll(short[] ashort);

    boolean addAll(Collection<? extends Short> collection);

    boolean addAll(TShortCollection tshortcollection);

    boolean addAll(short[] ashort);

    boolean retainAll(Collection<?> collection);

    boolean retainAll(TShortCollection tshortcollection);

    boolean retainAll(short[] ashort);

    boolean removeAll(Collection<?> collection);

    boolean removeAll(TShortCollection tshortcollection);

    boolean removeAll(short[] ashort);

    void clear();

    boolean forEach(TShortProcedure tshortprocedure);

    boolean equals(Object object);

    int hashCode();
}
