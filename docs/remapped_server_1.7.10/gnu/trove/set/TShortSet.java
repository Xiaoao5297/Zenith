package gnu.trove.set;

import gnu.trove.TShortCollection;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.procedure.TShortProcedure;
import java.util.Collection;

public interface TShortSet extends TShortCollection {

    @Override
    short getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean contains(short short0);

    @Override
    TShortIterator iterator();

    @Override
    short[] toArray();

    @Override
    short[] toArray(short[] ashort);

    @Override
    boolean add(short short0);

    @Override
    boolean remove(short short0);

    @Override
    boolean containsAll(Collection<?> collection);

    @Override
    boolean containsAll(TShortCollection tshortcollection);

    @Override
    boolean containsAll(short[] ashort);

    @Override
    boolean addAll(Collection<? extends Short> collection);

    @Override
    boolean addAll(TShortCollection tshortcollection);

    @Override
    boolean addAll(short[] ashort);

    @Override
    boolean retainAll(Collection<?> collection);

    @Override
    boolean retainAll(TShortCollection tshortcollection);

    @Override
    boolean retainAll(short[] ashort);

    @Override
    boolean removeAll(Collection<?> collection);

    @Override
    boolean removeAll(TShortCollection tshortcollection);

    @Override
    boolean removeAll(short[] ashort);

    @Override
    void clear();

    @Override
    boolean forEach(TShortProcedure tshortprocedure);

    @Override
    boolean equals(Object object);

    @Override
    int hashCode();
}
