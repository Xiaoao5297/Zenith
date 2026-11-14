package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectIntMap<K> {

    int getNoEntryValue();

    int size();

    boolean isEmpty();

    boolean containsKey(Object object);

    boolean containsValue(int i);

    int get(Object object);

    int put(K k0, int i);

    int putIfAbsent(K k0, int i);

    int remove(Object object);

    void putAll(Map<? extends K, ? extends Integer> map);

    void putAll(TObjectIntMap<? extends K> tobjectintmap);

    void clear();

    Set<K> keySet();

    Object[] keys();

    K[] keys(K[] ak);

    TIntCollection valueCollection();

    int[] values();

    int[] values(int[] aint);

    TObjectIntIterator<K> iterator();

    boolean increment(K k0);

    boolean adjustValue(K k0, int i);

    int adjustOrPutValue(K k0, int i, int j);

    boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure);

    boolean forEachValue(TIntProcedure tintprocedure);

    boolean forEachEntry(TObjectIntProcedure<? super K> tobjectintprocedure);

    void transformValues(TIntFunction tintfunction);

    boolean retainEntries(TObjectIntProcedure<? super K> tobjectintprocedure);

    boolean equals(Object object);

    int hashCode();
}
