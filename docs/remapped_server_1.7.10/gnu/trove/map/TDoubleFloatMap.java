package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TDoubleFloatIterator;
import gnu.trove.procedure.TDoubleFloatProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleFloatMap {

    double getNoEntryKey();

    float getNoEntryValue();

    float put(double d0, float f);

    float putIfAbsent(double d0, float f);

    void putAll(Map<? extends Double, ? extends Float> map);

    void putAll(TDoubleFloatMap tdoublefloatmap);

    float get(double d0);

    void clear();

    boolean isEmpty();

    float remove(double d0);

    int size();

    TDoubleSet keySet();

    double[] keys();

    double[] keys(double[] adouble);

    TFloatCollection valueCollection();

    float[] values();

    float[] values(float[] afloat);

    boolean containsValue(float f);

    boolean containsKey(double d0);

    TDoubleFloatIterator iterator();

    boolean forEachKey(TDoubleProcedure tdoubleprocedure);

    boolean forEachValue(TFloatProcedure tfloatprocedure);

    boolean forEachEntry(TDoubleFloatProcedure tdoublefloatprocedure);

    void transformValues(TFloatFunction tfloatfunction);

    boolean retainEntries(TDoubleFloatProcedure tdoublefloatprocedure);

    boolean increment(double d0);

    boolean adjustValue(double d0, float f);

    float adjustOrPutValue(double d0, float f, float f1);
}
