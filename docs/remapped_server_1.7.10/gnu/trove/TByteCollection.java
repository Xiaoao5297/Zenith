package gnu.trove;

import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;
import java.util.Collection;

public interface TByteCollection {

    long serialVersionUID = 1L;

    byte getNoEntryValue();

    int size();

    boolean isEmpty();

    boolean contains(byte b0);

    TByteIterator iterator();

    byte[] toArray();

    byte[] toArray(byte[] abyte);

    boolean add(byte b0);

    boolean remove(byte b0);

    boolean containsAll(Collection<?> collection);

    boolean containsAll(TByteCollection tbytecollection);

    boolean containsAll(byte[] abyte);

    boolean addAll(Collection<? extends Byte> collection);

    boolean addAll(TByteCollection tbytecollection);

    boolean addAll(byte[] abyte);

    boolean retainAll(Collection<?> collection);

    boolean retainAll(TByteCollection tbytecollection);

    boolean retainAll(byte[] abyte);

    boolean removeAll(Collection<?> collection);

    boolean removeAll(TByteCollection tbytecollection);

    boolean removeAll(byte[] abyte);

    void clear();

    boolean forEach(TByteProcedure tbyteprocedure);

    boolean equals(Object object);

    int hashCode();
}
