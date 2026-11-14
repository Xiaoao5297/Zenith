package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TFloatIntIterator;
import gnu.trove.procedure.TFloatIntProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Map;

public interface TFloatIntMap {

    float getNoEntryKey();

    int getNoEntryValue();

    int put(float f, int i);

    int putIfAbsent(float f, int i);

    void putAll(Map<? extends Float, ? extends Integer> map);

    void putAll(TFloatIntMap tfloatintmap);

    int get(float f);

    void clear();

    boolean isEmpty();

    int remove(float f);

    int size();

    TFloatSet keySet();

    float[] keys();

    float[] keys(float[] afloat);

    TIntCollection valueCollection();

    int[] values();

    int[] values(int[] aint);

    boolean containsValue(int i);

    boolean containsKey(float f);

    TFloatIntIterator iterator();

    boolean forEachKey(TFloatProcedure tfloatprocedure);

    boolean forEachValue(TIntProcedure tintprocedure);

    boolean forEachEntry(TFloatIntProcedure tfloatintprocedure);

    void transformValues(TIntFunction tintfunction);

    boolean retainEntries(TFloatIntProcedure tfloatintprocedure);

    boolean increment(float f);

    boolean adjustValue(float f, int i);

    int adjustOrPutValue(float f, int i, int j);
}
