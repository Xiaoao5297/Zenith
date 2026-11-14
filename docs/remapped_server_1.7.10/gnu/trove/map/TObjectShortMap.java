package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TObjectShortIterator;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TObjectShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectShortMap<K> {

    short getNoEntryValue();

    int size();

    boolean isEmpty();

    boolean containsKey(Object object);

    boolean containsValue(short short0);

    short get(Object object);

    short put(K k0, short short0);

    short putIfAbsent(K k0, short short0);

    short remove(Object object);

    void putAll(Map<? extends K, ? extends Short> map);

    void putAll(TObjectShortMap<? extends K> tobjectshortmap);

    void clear();

    Set<K> keySet();

    Object[] keys();

    K[] keys(K[] ak);

    TShortCollection valueCollection();

    short[] values();

    short[] values(short[] ashort);

    TObjectShortIterator<K> iterator();

    boolean increment(K k0);

    boolean adjustValue(K k0, short short0);

    short adjustOrPutValue(K k0, short short0, short short1);

    boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure);

    boolean forEachValue(TShortProcedure tshortprocedure);

    boolean forEachEntry(TObjectShortProcedure<? super K> tobjectshortprocedure);

    void transformValues(TShortFunction tshortfunction);

    boolean retainEntries(TObjectShortProcedure<? super K> tobjectshortprocedure);

    boolean equals(Object object);

    int hashCode();
}
