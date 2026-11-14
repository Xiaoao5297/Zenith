package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TShortFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TShortFloatProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Map;

public interface TShortFloatMap {

    short getNoEntryKey();

    float getNoEntryValue();

    float put(short short0, float f);

    float putIfAbsent(short short0, float f);

    void putAll(Map<? extends Short, ? extends Float> map);

    void putAll(TShortFloatMap tshortfloatmap);

    float get(short short0);

    void clear();

    boolean isEmpty();

    float remove(short short0);

    int size();

    TShortSet keySet();

    short[] keys();

    short[] keys(short[] ashort);

    TFloatCollection valueCollection();

    float[] values();

    float[] values(float[] afloat);

    boolean containsValue(float f);

    boolean containsKey(short short0);

    TShortFloatIterator iterator();

    boolean forEachKey(TShortProcedure tshortprocedure);

    boolean forEachValue(TFloatProcedure tfloatprocedure);

    boolean forEachEntry(TShortFloatProcedure tshortfloatprocedure);

    void transformValues(TFloatFunction tfloatfunction);

    boolean retainEntries(TShortFloatProcedure tshortfloatprocedure);

    boolean increment(short short0);

    boolean adjustValue(short short0, float f);

    float adjustOrPutValue(short short0, float f, float f1);
}
