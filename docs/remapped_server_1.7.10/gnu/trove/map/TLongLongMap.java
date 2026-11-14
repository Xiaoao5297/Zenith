package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.procedure.TLongLongProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.util.Map;

public interface TLongLongMap {

    long getNoEntryKey();

    long getNoEntryValue();

    long put(long i, long j);

    long putIfAbsent(long i, long j);

    void putAll(Map<? extends Long, ? extends Long> map);

    void putAll(TLongLongMap tlonglongmap);

    long get(long i);

    void clear();

    boolean isEmpty();

    long remove(long i);

    int size();

    TLongSet keySet();

    long[] keys();

    long[] keys(long[] along);

    TLongCollection valueCollection();

    long[] values();

    long[] values(long[] along);

    boolean containsValue(long i);

    boolean containsKey(long i);

    TLongLongIterator iterator();

    boolean forEachKey(TLongProcedure tlongprocedure);

    boolean forEachValue(TLongProcedure tlongprocedure);

    boolean forEachEntry(TLongLongProcedure tlonglongprocedure);

    void transformValues(TLongFunction tlongfunction);

    boolean retainEntries(TLongLongProcedure tlonglongprocedure);

    boolean increment(long i);

    boolean adjustValue(long i, long j);

    long adjustOrPutValue(long i, long j, long k);
}
