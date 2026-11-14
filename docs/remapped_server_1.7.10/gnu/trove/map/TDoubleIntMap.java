package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TDoubleIntIterator;
import gnu.trove.procedure.TDoubleIntProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleIntMap {

    double getNoEntryKey();

    int getNoEntryValue();

    int put(double d0, int i);

    int putIfAbsent(double d0, int i);

    void putAll(Map<? extends Double, ? extends Integer> map);

    void putAll(TDoubleIntMap tdoubleintmap);

    int get(double d0);

    void clear();

    boolean isEmpty();

    int remove(double d0);

    int size();

    TDoubleSet keySet();

    double[] keys();

    double[] keys(double[] adouble);

    TIntCollection valueCollection();

    int[] values();

    int[] values(int[] aint);

    boolean containsValue(int i);

    boolean containsKey(double d0);

    TDoubleIntIterator iterator();

    boolean forEachKey(TDoubleProcedure tdoubleprocedure);

    boolean forEachValue(TIntProcedure tintprocedure);

    boolean forEachEntry(TDoubleIntProcedure tdoubleintprocedure);

    void transformValues(TIntFunction tintfunction);

    boolean retainEntries(TDoubleIntProcedure tdoubleintprocedure);

    boolean increment(double d0);

    boolean adjustValue(double d0, int i);

    int adjustOrPutValue(double d0, int i, int j);
}
