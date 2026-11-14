package gnu.trove.set;

import gnu.trove.TByteCollection;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;
import java.util.Collection;

public interface TByteSet extends TByteCollection {

    @Override
    byte getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean contains(byte b0);

    @Override
    TByteIterator iterator();

    @Override
    byte[] toArray();

    @Override
    byte[] toArray(byte[] abyte);

    @Override
    boolean add(byte b0);

    @Override
    boolean remove(byte b0);

    @Override
    boolean containsAll(Collection<?> collection);

    @Override
    boolean containsAll(TByteCollection tbytecollection);

    @Override
    boolean containsAll(byte[] abyte);

    @Override
    boolean addAll(Collection<? extends Byte> collection);

    @Override
    boolean addAll(TByteCollection tbytecollection);

    @Override
    boolean addAll(byte[] abyte);

    @Override
    boolean retainAll(Collection<?> collection);

    @Override
    boolean retainAll(TByteCollection tbytecollection);

    @Override
    boolean retainAll(byte[] abyte);

    @Override
    boolean removeAll(Collection<?> collection);

    @Override
    boolean removeAll(TByteCollection tbytecollection);

    @Override
    boolean removeAll(byte[] abyte);

    @Override
    void clear();

    @Override
    boolean forEach(TByteProcedure tbyteprocedure);

    @Override
    boolean equals(Object object);

    @Override
    int hashCode();
}
