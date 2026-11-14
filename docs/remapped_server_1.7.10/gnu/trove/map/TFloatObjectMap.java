package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.procedure.TFloatObjectProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Collection;
import java.util.Map;

public interface TFloatObjectMap<V> {

    float getNoEntryKey();

    int size();

    boolean isEmpty();

    boolean containsKey(float f);

    boolean containsValue(Object object);

    V get(float f);

    V put(float f, V v0);

    V putIfAbsent(float f, V v0);

    V remove(float f);

    void putAll(Map<? extends Float, ? extends V> map);

    void putAll(TFloatObjectMap<? extends V> tfloatobjectmap);

    void clear();

    TFloatSet keySet();

    float[] keys();

    float[] keys(float[] afloat);

    Collection<V> valueCollection();

    Object[] values();

    V[] values(V[] av);

    TFloatObjectIterator<V> iterator();

    boolean forEachKey(TFloatProcedure tfloatprocedure);

    boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure);

    boolean forEachEntry(TFloatObjectProcedure<? super V> tfloatobjectprocedure);

    void transformValues(TObjectFunction<V, V> tobjectfunction);

    boolean retainEntries(TFloatObjectProcedure<? super V> tfloatobjectprocedure);

    boolean equals(Object object);

    int hashCode();
}
