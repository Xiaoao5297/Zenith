package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TFloatFloatIterator;
import gnu.trove.procedure.TFloatFloatProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Map;

public interface TFloatFloatMap {

    float getNoEntryKey();

    float getNoEntryValue();

    float put(float f, float f1);

    float putIfAbsent(float f, float f1);

    void putAll(Map<? extends Float, ? extends Float> map);

    void putAll(TFloatFloatMap tfloatfloatmap);

    float get(float f);

    void clear();

    boolean isEmpty();

    float remove(float f);

    int size();

    TFloatSet keySet();

    float[] keys();

    float[] keys(float[] afloat);

    TFloatCollection valueCollection();

    float[] values();

    float[] values(float[] afloat);

    boolean containsValue(float f);

    boolean containsKey(float f);

    TFloatFloatIterator iterator();

    boolean forEachKey(TFloatProcedure tfloatprocedure);

    boolean forEachValue(TFloatProcedure tfloatprocedure);

    boolean forEachEntry(TFloatFloatProcedure tfloatfloatprocedure);

    void transformValues(TFloatFunction tfloatfunction);

    boolean retainEntries(TFloatFloatProcedure tfloatfloatprocedure);

    boolean increment(float f);

    boolean adjustValue(float f, float f1);

    float adjustOrPutValue(float f, float f1, float f2);
}
