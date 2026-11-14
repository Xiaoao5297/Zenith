package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.util.Map;

public interface TIntDoubleMap {

    int getNoEntryKey();

    double getNoEntryValue();

    double put(int i, double d0);

    double putIfAbsent(int i, double d0);

    void putAll(Map<? extends Integer, ? extends Double> map);

    void putAll(TIntDoubleMap tintdoublemap);

    double get(int i);

    void clear();

    boolean isEmpty();

    double remove(int i);

    int size();

    TIntSet keySet();

    int[] keys();

    int[] keys(int[] aint);

    TDoubleCollection valueCollection();

    double[] values();

    double[] values(double[] adouble);

    boolean containsValue(double d0);

    boolean containsKey(int i);

    TIntDoubleIterator iterator();

    boolean forEachKey(TIntProcedure tintprocedure);

    boolean forEachValue(TDoubleProcedure tdoubleprocedure);

    boolean forEachEntry(TIntDoubleProcedure tintdoubleprocedure);

    void transformValues(TDoubleFunction tdoublefunction);

    boolean retainEntries(TIntDoubleProcedure tintdoubleprocedure);

    boolean increment(int i);

    boolean adjustValue(int i, double d0);

    double adjustOrPutValue(int i, double d0, double d1);
}
