package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TIntSet;
import java.util.Collection;
import java.util.Map;

public interface TIntObjectMap<V> {

    int getNoEntryKey();

    int size();

    boolean isEmpty();

    boolean containsKey(int i);

    boolean containsValue(Object object);

    V get(int i);

    V put(int i, V v0);

    V putIfAbsent(int i, V v0);

    V remove(int i);

    void putAll(Map<? extends Integer, ? extends V> map);

    void putAll(TIntObjectMap<? extends V> tintobjectmap);

    void clear();

    TIntSet keySet();

    int[] keys();

    int[] keys(int[] aint);

    Collection<V> valueCollection();

    Object[] values();

    V[] values(V[] av);

    TIntObjectIterator<V> iterator();

    boolean forEachKey(TIntProcedure tintprocedure);

    boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure);

    boolean forEachEntry(TIntObjectProcedure<? super V> tintobjectprocedure);

    void transformValues(TObjectFunction<V, V> tobjectfunction);

    boolean retainEntries(TIntObjectProcedure<? super V> tintobjectprocedure);

    boolean equals(Object object);

    int hashCode();
}
