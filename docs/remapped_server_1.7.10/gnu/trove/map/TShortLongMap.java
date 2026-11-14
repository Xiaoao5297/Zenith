package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TShortLongIterator;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TShortLongProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Map;

public interface TShortLongMap {

    short getNoEntryKey();

    long getNoEntryValue();

    long put(short short0, long i);

    long putIfAbsent(short short0, long i);

    void putAll(Map<? extends Short, ? extends Long> map);

    void putAll(TShortLongMap tshortlongmap);

    long get(short short0);

    void clear();

    boolean isEmpty();

    long remove(short short0);

    int size();

    TShortSet keySet();

    short[] keys();

    short[] keys(short[] ashort);

    TLongCollection valueCollection();

    long[] values();

    long[] values(long[] along);

    boolean containsValue(long i);

    boolean containsKey(short short0);

    TShortLongIterator iterator();

    boolean forEachKey(TShortProcedure tshortprocedure);

    boolean forEachValue(TLongProcedure tlongprocedure);

    boolean forEachEntry(TShortLongProcedure tshortlongprocedure);

    void transformValues(TLongFunction tlongfunction);

    boolean retainEntries(TShortLongProcedure tshortlongprocedure);

    boolean increment(short short0);

    boolean adjustValue(short short0, long i);

    long adjustOrPutValue(short short0, long i, long j);
}
