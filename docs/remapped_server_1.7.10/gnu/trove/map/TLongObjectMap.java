package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
import java.util.Collection;
import java.util.Map;

public interface TLongObjectMap<V> {

    long getNoEntryKey();

    int size();

    boolean isEmpty();

    boolean containsKey(long i);

    boolean containsValue(Object object);

    V get(long i);

    V put(long i, V v0);

    V putIfAbsent(long i, V v0);

    V remove(long i);

    void putAll(Map<? extends Long, ? extends V> map);

    void putAll(TLongObjectMap<? extends V> tlongobjectmap);

    void clear();

    TLongSet keySet();

    long[] keys();

    long[] keys(long[] along);

    Collection<V> valueCollection();

    Object[] values();

    V[] values(V[] av);

    TLongObjectIterator<V> iterator();

    boolean forEachKey(TLongProcedure tlongprocedure);

    boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure);

    boolean forEachEntry(TLongObjectProcedure<? super V> tlongobjectprocedure);

    void transformValues(TObjectFunction<V, V> tobjectfunction);

    boolean retainEntries(TLongObjectProcedure<? super V> tlongobjectprocedure);

    boolean equals(Object object);

    int hashCode();
}
