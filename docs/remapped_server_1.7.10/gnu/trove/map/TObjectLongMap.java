package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectLongMap<K> {

    long getNoEntryValue();

    int size();

    boolean isEmpty();

    boolean containsKey(Object object);

    boolean containsValue(long i);

    long get(Object object);

    long put(K k0, long i);

    long putIfAbsent(K k0, long i);

    long remove(Object object);

    void putAll(Map<? extends K, ? extends Long> map);

    void putAll(TObjectLongMap<? extends K> tobjectlongmap);

    void clear();

    Set<K> keySet();

    Object[] keys();

    K[] keys(K[] ak);

    TLongCollection valueCollection();

    long[] values();

    long[] values(long[] along);

    TObjectLongIterator<K> iterator();

    boolean increment(K k0);

    boolean adjustValue(K k0, long i);

    long adjustOrPutValue(K k0, long i, long j);

    boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure);

    boolean forEachValue(TLongProcedure tlongprocedure);

    boolean forEachEntry(TObjectLongProcedure<? super K> tobjectlongprocedure);

    void transformValues(TLongFunction tlongfunction);

    boolean retainEntries(TObjectLongProcedure<? super K> tobjectlongprocedure);

    boolean equals(Object object);

    int hashCode();
}
