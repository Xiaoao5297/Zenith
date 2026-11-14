package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TFloatDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Map;

public interface TFloatDoubleMap {

    float getNoEntryKey();

    double getNoEntryValue();

    double put(float f, double d0);

    double putIfAbsent(float f, double d0);

    void putAll(Map<? extends Float, ? extends Double> map);

    void putAll(TFloatDoubleMap tfloatdoublemap);

    double get(float f);

    void clear();

    boolean isEmpty();

    double remove(float f);

    int size();

    TFloatSet keySet();

    float[] keys();

    float[] keys(float[] afloat);

    TDoubleCollection valueCollection();

    double[] values();

    double[] values(double[] adouble);

    boolean containsValue(double d0);

    boolean containsKey(float f);

    TFloatDoubleIterator iterator();

    boolean forEachKey(TFloatProcedure tfloatprocedure);

    boolean forEachValue(TDoubleProcedure tdoubleprocedure);

    boolean forEachEntry(TFloatDoubleProcedure tfloatdoubleprocedure);

    void transformValues(TDoubleFunction tdoublefunction);

    boolean retainEntries(TFloatDoubleProcedure tfloatdoubleprocedure);

    boolean increment(float f);

    boolean adjustValue(float f, double d0);

    double adjustOrPutValue(float f, double d0, double d1);
}
