package gnu.trove.set;

import gnu.trove.TDoubleCollection;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Collection;

public interface TDoubleSet extends TDoubleCollection {

    @Override
    double getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean contains(double d0);

    @Override
    TDoubleIterator iterator();

    @Override
    double[] toArray();

    @Override
    double[] toArray(double[] adouble);

    @Override
    boolean add(double d0);

    @Override
    boolean remove(double d0);

    @Override
    boolean containsAll(Collection<?> collection);

    @Override
    boolean containsAll(TDoubleCollection tdoublecollection);

    @Override
    boolean containsAll(double[] adouble);

    @Override
    boolean addAll(Collection<? extends Double> collection);

    @Override
    boolean addAll(TDoubleCollection tdoublecollection);

    @Override
    boolean addAll(double[] adouble);

    @Override
    boolean retainAll(Collection<?> collection);

    @Override
    boolean retainAll(TDoubleCollection tdoublecollection);

    @Override
    boolean retainAll(double[] adouble);

    @Override
    boolean removeAll(Collection<?> collection);

    @Override
    boolean removeAll(TDoubleCollection tdoublecollection);

    @Override
    boolean removeAll(double[] adouble);

    @Override
    void clear();

    @Override
    boolean forEach(TDoubleProcedure tdoubleprocedure);

    @Override
    boolean equals(Object object);

    @Override
    int hashCode();
}
