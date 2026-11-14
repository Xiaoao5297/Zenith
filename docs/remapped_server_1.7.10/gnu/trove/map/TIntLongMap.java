package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.procedure.TIntLongProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TIntSet;
import java.util.Map;

public interface TIntLongMap {

    int getNoEntryKey();

    long getNoEntryValue();

    long put(int i, long j);

    long putIfAbsent(int i, long j);

    void putAll(Map<? extends Integer, ? extends Long> map);

    void putAll(TIntLongMap tintlongmap);

    long get(int i);

    void clear();

    boolean isEmpty();

    long remove(int i);

    int size();

    TIntSet keySet();

    int[] keys();

    int[] keys(int[] aint);

    TLongCollection valueCollection();

    long[] values();

    long[] values(long[] along);

    boolean containsValue(long i);

    boolean containsKey(int i);

    TIntLongIterator iterator();

    boolean forEachKey(TIntProcedure tintprocedure);

    boolean forEachValue(TLongProcedure tlongprocedure);

    boolean forEachEntry(TIntLongProcedure tintlongprocedure);

    void transformValues(TLongFunction tlongfunction);

    boolean retainEntries(TIntLongProcedure tintlongprocedure);

    boolean increment(int i);

    boolean adjustValue(int i, long j);

    long adjustOrPutValue(int i, long j, long k);
}
