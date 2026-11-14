package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TIntFloatProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.util.Map;

public interface TIntFloatMap {

    int getNoEntryKey();

    float getNoEntryValue();

    float put(int i, float f);

    float putIfAbsent(int i, float f);

    void putAll(Map<? extends Integer, ? extends Float> map);

    void putAll(TIntFloatMap tintfloatmap);

    float get(int i);

    void clear();

    boolean isEmpty();

    float remove(int i);

    int size();

    TIntSet keySet();

    int[] keys();

    int[] keys(int[] aint);

    TFloatCollection valueCollection();

    float[] values();

    float[] values(float[] afloat);

    boolean containsValue(float f);

    boolean containsKey(int i);

    TIntFloatIterator iterator();

    boolean forEachKey(TIntProcedure tintprocedure);

    boolean forEachValue(TFloatProcedure tfloatprocedure);

    boolean forEachEntry(TIntFloatProcedure tintfloatprocedure);

    void transformValues(TFloatFunction tfloatfunction);

    boolean retainEntries(TIntFloatProcedure tintfloatprocedure);

    boolean increment(int i);

    boolean adjustValue(int i, float f);

    float adjustOrPutValue(int i, float f, float f1);
}
