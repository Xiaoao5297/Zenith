package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.procedure.TByteObjectProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TByteSet;
import java.util.Collection;
import java.util.Map;

public interface TByteObjectMap<V> {

    byte getNoEntryKey();

    int size();

    boolean isEmpty();

    boolean containsKey(byte b0);

    boolean containsValue(Object object);

    V get(byte b0);

    V put(byte b0, V v0);

    V putIfAbsent(byte b0, V v0);

    V remove(byte b0);

    void putAll(Map<? extends Byte, ? extends V> map);

    void putAll(TByteObjectMap<? extends V> tbyteobjectmap);

    void clear();

    TByteSet keySet();

    byte[] keys();

    byte[] keys(byte[] abyte);

    Collection<V> valueCollection();

    Object[] values();

    V[] values(V[] av);

    TByteObjectIterator<V> iterator();

    boolean forEachKey(TByteProcedure tbyteprocedure);

    boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure);

    boolean forEachEntry(TByteObjectProcedure<? super V> tbyteobjectprocedure);

    void transformValues(TObjectFunction<V, V> tobjectfunction);

    boolean retainEntries(TByteObjectProcedure<? super V> tbyteobjectprocedure);

    boolean equals(Object object);

    int hashCode();
}
