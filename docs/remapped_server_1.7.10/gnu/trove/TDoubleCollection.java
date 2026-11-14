package gnu.trove;

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Collection;

public interface TDoubleCollection {

    long serialVersionUID = 1L;

    double getNoEntryValue();

    int size();

    boolean isEmpty();

    boolean contains(double d0);

    TDoubleIterator iterator();

    double[] toArray();

    double[] toArray(double[] adouble);

    boolean add(double d0);

    boolean remove(double d0);

    boolean containsAll(Collection<?> collection);

    boolean containsAll(TDoubleCollection tdoublecollection);

    boolean containsAll(double[] adouble);

    boolean addAll(Collection<? extends Double> collection);

    boolean addAll(TDoubleCollection tdoublecollection);

    boolean addAll(double[] adouble);

    boolean retainAll(Collection<?> collection);

    boolean retainAll(TDoubleCollection tdoublecollection);

    boolean retainAll(double[] adouble);

    boolean removeAll(Collection<?> collection);

    boolean removeAll(TDoubleCollection tdoublecollection);

    boolean removeAll(double[] adouble);

    void clear();

    boolean forEach(TDoubleProcedure tdoubleprocedure);

    boolean equals(Object object);

    int hashCode();
}
