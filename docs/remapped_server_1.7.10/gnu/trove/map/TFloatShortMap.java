package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TFloatShortIterator;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TFloatShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Map;

public interface TFloatShortMap {

    float getNoEntryKey();

    short getNoEntryValue();

    short put(float f, short short0);

    short putIfAbsent(float f, short short0);

    void putAll(Map<? extends Float, ? extends Short> map);

    void putAll(TFloatShortMap tfloatshortmap);

    short get(float f);

    void clear();

    boolean isEmpty();

    short remove(float f);

    int size();

    TFloatSet keySet();

    float[] keys();

    float[] keys(float[] afloat);

    TShortCollection valueCollection();

    short[] values();

    short[] values(short[] ashort);

    boolean containsValue(short short0);

    boolean containsKey(float f);

    TFloatShortIterator iterator();

    boolean forEachKey(TFloatProcedure tfloatprocedure);

    boolean forEachValue(TShortProcedure tshortprocedure);

    boolean forEachEntry(TFloatShortProcedure tfloatshortprocedure);

    void transformValues(TShortFunction tshortfunction);

    boolean retainEntries(TFloatShortProcedure tfloatshortprocedure);

    boolean increment(float f);

    boolean adjustValue(float f, short short0);

    short adjustOrPutValue(float f, short short0, short short1);
}
