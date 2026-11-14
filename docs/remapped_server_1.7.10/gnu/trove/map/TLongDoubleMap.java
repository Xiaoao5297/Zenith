package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.util.Map;

public interface TLongDoubleMap {

    long getNoEntryKey();

    double getNoEntryValue();

    double put(long i, double d0);

    double putIfAbsent(long i, double d0);

    void putAll(Map<? extends Long, ? extends Double> map);

    void putAll(TLongDoubleMap tlongdoublemap);

    double get(long i);

    void clear();

    boolean isEmpty();

    double remove(long i);

    int size();

    TLongSet keySet();

    long[] keys();

    long[] keys(long[] along);

    TDoubleCollection valueCollection();

    double[] values();

    double[] values(double[] adouble);

    boolean containsValue(double d0);

    boolean containsKey(long i);

    TLongDoubleIterator iterator();

    boolean forEachKey(TLongProcedure tlongprocedure);

    boolean forEachValue(TDoubleProcedure tdoubleprocedure);

    boolean forEachEntry(TLongDoubleProcedure tlongdoubleprocedure);

    void transformValues(TDoubleFunction tdoublefunction);

    boolean retainEntries(TLongDoubleProcedure tlongdoubleprocedure);

    boolean increment(long i);

    boolean adjustValue(long i, double d0);

    double adjustOrPutValue(long i, double d0, double d1);
}
