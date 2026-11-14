package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectDoubleMap<K> {

    double getNoEntryValue();

    int size();

    boolean isEmpty();

    boolean containsKey(Object object);

    boolean containsValue(double d0);

    double get(Object object);

    double put(K k0, double d0);

    double putIfAbsent(K k0, double d0);

    double remove(Object object);

    void putAll(Map<? extends K, ? extends Double> map);

    void putAll(TObjectDoubleMap<? extends K> tobjectdoublemap);

    void clear();

    Set<K> keySet();

    Object[] keys();

    K[] keys(K[] ak);

    TDoubleCollection valueCollection();

    double[] values();

    double[] values(double[] adouble);

    TObjectDoubleIterator<K> iterator();

    boolean increment(K k0);

    boolean adjustValue(K k0, double d0);

    double adjustOrPutValue(K k0, double d0, double d1);

    boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure);

    boolean forEachValue(TDoubleProcedure tdoubleprocedure);

    boolean forEachEntry(TObjectDoubleProcedure<? super K> tobjectdoubleprocedure);

    void transformValues(TDoubleFunction tdoublefunction);

    boolean retainEntries(TObjectDoubleProcedure<? super K> tobjectdoubleprocedure);

    boolean equals(Object object);

    int hashCode();
}
