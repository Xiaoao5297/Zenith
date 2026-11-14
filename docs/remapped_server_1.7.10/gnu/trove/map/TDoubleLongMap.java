package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TDoubleLongIterator;
import gnu.trove.procedure.TDoubleLongProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleLongMap {

    double getNoEntryKey();

    long getNoEntryValue();

    long put(double d0, long i);

    long putIfAbsent(double d0, long i);

    void putAll(Map<? extends Double, ? extends Long> map);

    void putAll(TDoubleLongMap tdoublelongmap);

    long get(double d0);

    void clear();

    boolean isEmpty();

    long remove(double d0);

    int size();

    TDoubleSet keySet();

    double[] keys();

    double[] keys(double[] adouble);

    TLongCollection valueCollection();

    long[] values();

    long[] values(long[] along);

    boolean containsValue(long i);

    boolean containsKey(double d0);

    TDoubleLongIterator iterator();

    boolean forEachKey(TDoubleProcedure tdoubleprocedure);

    boolean forEachValue(TLongProcedure tlongprocedure);

    boolean forEachEntry(TDoubleLongProcedure tdoublelongprocedure);

    void transformValues(TLongFunction tlongfunction);

    boolean retainEntries(TDoubleLongProcedure tdoublelongprocedure);

    boolean increment(double d0);

    boolean adjustValue(double d0, long i);

    long adjustOrPutValue(double d0, long i, long j);
}
