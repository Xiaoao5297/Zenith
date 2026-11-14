package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectFloatMap<K> {

    float getNoEntryValue();

    int size();

    boolean isEmpty();

    boolean containsKey(Object object);

    boolean containsValue(float f);

    float get(Object object);

    float put(K k0, float f);

    float putIfAbsent(K k0, float f);

    float remove(Object object);

    void putAll(Map<? extends K, ? extends Float> map);

    void putAll(TObjectFloatMap<? extends K> tobjectfloatmap);

    void clear();

    Set<K> keySet();

    Object[] keys();

    K[] keys(K[] ak);

    TFloatCollection valueCollection();

    float[] values();

    float[] values(float[] afloat);

    TObjectFloatIterator<K> iterator();

    boolean increment(K k0);

    boolean adjustValue(K k0, float f);

    float adjustOrPutValue(K k0, float f, float f1);

    boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure);

    boolean forEachValue(TFloatProcedure tfloatprocedure);

    boolean forEachEntry(TObjectFloatProcedure<? super K> tobjectfloatprocedure);

    void transformValues(TFloatFunction tfloatfunction);

    boolean retainEntries(TObjectFloatProcedure<? super K> tobjectfloatprocedure);

    boolean equals(Object object);

    int hashCode();
}
