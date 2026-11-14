package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TLongFloatProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.util.Map;

public interface TLongFloatMap {

    long getNoEntryKey();

    float getNoEntryValue();

    float put(long i, float f);

    float putIfAbsent(long i, float f);

    void putAll(Map<? extends Long, ? extends Float> map);

    void putAll(TLongFloatMap tlongfloatmap);

    float get(long i);

    void clear();

    boolean isEmpty();

    float remove(long i);

    int size();

    TLongSet keySet();

    long[] keys();

    long[] keys(long[] along);

    TFloatCollection valueCollection();

    float[] values();

    float[] values(float[] afloat);

    boolean containsValue(float f);

    boolean containsKey(long i);

    TLongFloatIterator iterator();

    boolean forEachKey(TLongProcedure tlongprocedure);

    boolean forEachValue(TFloatProcedure tfloatprocedure);

    boolean forEachEntry(TLongFloatProcedure tlongfloatprocedure);

    void transformValues(TFloatFunction tfloatfunction);

    boolean retainEntries(TLongFloatProcedure tlongfloatprocedure);

    boolean increment(long i);

    boolean adjustValue(long i, float f);

    float adjustOrPutValue(long i, float f, float f1);
}
