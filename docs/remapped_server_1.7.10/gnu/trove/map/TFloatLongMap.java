package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TFloatLongIterator;
import gnu.trove.procedure.TFloatLongProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Map;

public interface TFloatLongMap {

    float getNoEntryKey();

    long getNoEntryValue();

    long put(float f, long i);

    long putIfAbsent(float f, long i);

    void putAll(Map<? extends Float, ? extends Long> map);

    void putAll(TFloatLongMap tfloatlongmap);

    long get(float f);

    void clear();

    boolean isEmpty();

    long remove(float f);

    int size();

    TFloatSet keySet();

    float[] keys();

    float[] keys(float[] afloat);

    TLongCollection valueCollection();

    long[] values();

    long[] values(long[] along);

    boolean containsValue(long i);

    boolean containsKey(float f);

    TFloatLongIterator iterator();

    boolean forEachKey(TFloatProcedure tfloatprocedure);

    boolean forEachValue(TLongProcedure tlongprocedure);

    boolean forEachEntry(TFloatLongProcedure tfloatlongprocedure);

    void transformValues(TLongFunction tlongfunction);

    boolean retainEntries(TFloatLongProcedure tfloatlongprocedure);

    boolean increment(float f);

    boolean adjustValue(float f, long i);

    long adjustOrPutValue(float f, long i, long j);
}
