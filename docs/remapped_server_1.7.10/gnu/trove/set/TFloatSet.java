package gnu.trove.set;

import gnu.trove.TFloatCollection;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Collection;

public interface TFloatSet extends TFloatCollection {

    @Override
    float getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean contains(float f);

    @Override
    TFloatIterator iterator();

    @Override
    float[] toArray();

    @Override
    float[] toArray(float[] afloat);

    @Override
    boolean add(float f);

    @Override
    boolean remove(float f);

    @Override
    boolean containsAll(Collection<?> collection);

    @Override
    boolean containsAll(TFloatCollection tfloatcollection);

    @Override
    boolean containsAll(float[] afloat);

    @Override
    boolean addAll(Collection<? extends Float> collection);

    @Override
    boolean addAll(TFloatCollection tfloatcollection);

    @Override
    boolean addAll(float[] afloat);

    @Override
    boolean retainAll(Collection<?> collection);

    @Override
    boolean retainAll(TFloatCollection tfloatcollection);

    @Override
    boolean retainAll(float[] afloat);

    @Override
    boolean removeAll(Collection<?> collection);

    @Override
    boolean removeAll(TFloatCollection tfloatcollection);

    @Override
    boolean removeAll(float[] afloat);

    @Override
    void clear();

    @Override
    boolean forEach(TFloatProcedure tfloatprocedure);

    @Override
    boolean equals(Object object);

    @Override
    int hashCode();
}
