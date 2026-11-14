package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TDoubleObjectIterator;
import gnu.trove.procedure.TDoubleObjectProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Collection;
import java.util.Map;

public interface TDoubleObjectMap<V> {

    double getNoEntryKey();

    int size();

    boolean isEmpty();

    boolean containsKey(double d0);

    boolean containsValue(Object object);

    V get(double d0);

    V put(double d0, V v0);

    V putIfAbsent(double d0, V v0);

    V remove(double d0);

    void putAll(Map<? extends Double, ? extends V> map);

    void putAll(TDoubleObjectMap<? extends V> tdoubleobjectmap);

    void clear();

    TDoubleSet keySet();

    double[] keys();

    double[] keys(double[] adouble);

    Collection<V> valueCollection();

    Object[] values();

    V[] values(V[] av);

    TDoubleObjectIterator<V> iterator();

    boolean forEachKey(TDoubleProcedure tdoubleprocedure);

    boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure);

    boolean forEachEntry(TDoubleObjectProcedure<? super V> tdoubleobjectprocedure);

    void transformValues(TObjectFunction<V, V> tobjectfunction);

    boolean retainEntries(TDoubleObjectProcedure<? super V> tdoubleobjectprocedure);

    boolean equals(Object object);

    int hashCode();
}
