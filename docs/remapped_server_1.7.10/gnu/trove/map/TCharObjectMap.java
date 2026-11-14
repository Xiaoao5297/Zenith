package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TCharObjectIterator;
import gnu.trove.procedure.TCharObjectProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TCharSet;
import java.util.Collection;
import java.util.Map;

public interface TCharObjectMap<V> {

    char getNoEntryKey();

    int size();

    boolean isEmpty();

    boolean containsKey(char c0);

    boolean containsValue(Object object);

    V get(char c0);

    V put(char c0, V v0);

    V putIfAbsent(char c0, V v0);

    V remove(char c0);

    void putAll(Map<? extends Character, ? extends V> map);

    void putAll(TCharObjectMap<? extends V> tcharobjectmap);

    void clear();

    TCharSet keySet();

    char[] keys();

    char[] keys(char[] achar);

    Collection<V> valueCollection();

    Object[] values();

    V[] values(V[] av);

    TCharObjectIterator<V> iterator();

    boolean forEachKey(TCharProcedure tcharprocedure);

    boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure);

    boolean forEachEntry(TCharObjectProcedure<? super V> tcharobjectprocedure);

    void transformValues(TObjectFunction<V, V> tobjectfunction);

    boolean retainEntries(TCharObjectProcedure<? super V> tcharobjectprocedure);

    boolean equals(Object object);

    int hashCode();
}
