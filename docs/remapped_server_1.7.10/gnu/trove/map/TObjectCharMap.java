package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectCharMap<K> {

    char getNoEntryValue();

    int size();

    boolean isEmpty();

    boolean containsKey(Object object);

    boolean containsValue(char c0);

    char get(Object object);

    char put(K k0, char c0);

    char putIfAbsent(K k0, char c0);

    char remove(Object object);

    void putAll(Map<? extends K, ? extends Character> map);

    void putAll(TObjectCharMap<? extends K> tobjectcharmap);

    void clear();

    Set<K> keySet();

    Object[] keys();

    K[] keys(K[] ak);

    TCharCollection valueCollection();

    char[] values();

    char[] values(char[] achar);

    TObjectCharIterator<K> iterator();

    boolean increment(K k0);

    boolean adjustValue(K k0, char c0);

    char adjustOrPutValue(K k0, char c0, char c1);

    boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure);

    boolean forEachValue(TCharProcedure tcharprocedure);

    boolean forEachEntry(TObjectCharProcedure<? super K> tobjectcharprocedure);

    void transformValues(TCharFunction tcharfunction);

    boolean retainEntries(TObjectCharProcedure<? super K> tobjectcharprocedure);

    boolean equals(Object object);

    int hashCode();
}
