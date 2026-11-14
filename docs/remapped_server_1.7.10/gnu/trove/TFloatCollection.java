package gnu.trove;

import gnu.trove.iterator.TFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Collection;

public interface TFloatCollection {

    long serialVersionUID = 1L;

    float getNoEntryValue();

    int size();

    boolean isEmpty();

    boolean contains(float f);

    TFloatIterator iterator();

    float[] toArray();

    float[] toArray(float[] afloat);

    boolean add(float f);

    boolean remove(float f);

    boolean containsAll(Collection<?> collection);

    boolean containsAll(TFloatCollection tfloatcollection);

    boolean containsAll(float[] afloat);

    boolean addAll(Collection<? extends Float> collection);

    boolean addAll(TFloatCollection tfloatcollection);

    boolean addAll(float[] afloat);

    boolean retainAll(Collection<?> collection);

    boolean retainAll(TFloatCollection tfloatcollection);

    boolean retainAll(float[] afloat);

    boolean removeAll(Collection<?> collection);

    boolean removeAll(TFloatCollection tfloatcollection);

    boolean removeAll(float[] afloat);

    void clear();

    boolean forEach(TFloatProcedure tfloatprocedure);

    boolean equals(Object object);

    int hashCode();
}
