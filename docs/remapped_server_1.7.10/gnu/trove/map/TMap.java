package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;

public interface TMap<K, V> extends Map<K, V> {

    V putIfAbsent(K k0, V v0);

    boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure);

    boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure);

    boolean forEachEntry(TObjectObjectProcedure<? super K, ? super V> tobjectobjectprocedure);

    boolean retainEntries(TObjectObjectProcedure<? super K, ? super V> tobjectobjectprocedure);

    void transformValues(TObjectFunction<V, V> tobjectfunction);
}
