package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TShortObjectProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Collection;
import java.util.Map;

public interface TShortObjectMap<V> {

    short getNoEntryKey();

    int size();

    boolean isEmpty();

    boolean containsKey(short short0);

    boolean containsValue(Object object);

    V get(short short0);

    V put(short short0, V v0);

    V putIfAbsent(short short0, V v0);

    V remove(short short0);

    void putAll(Map<? extends Short, ? extends V> map);

    void putAll(TShortObjectMap<? extends V> tshortobjectmap);

    void clear();

    TShortSet keySet();

    short[] keys();

    short[] keys(short[] ashort);

    Collection<V> valueCollection();

    Object[] values();

    V[] values(V[] av);

    TShortObjectIterator<V> iterator();

    boolean forEachKey(TShortProcedure tshortprocedure);

    boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure);

    boolean forEachEntry(TShortObjectProcedure<? super V> tshortobjectprocedure);

    void transformValues(TObjectFunction<V, V> tobjectfunction);

    boolean retainEntries(TShortObjectProcedure<? super V> tshortobjectprocedure);

    boolean equals(Object object);

    int hashCode();
}
