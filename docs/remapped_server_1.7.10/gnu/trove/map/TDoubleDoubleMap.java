package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.procedure.TDoubleDoubleProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleDoubleMap {

    double getNoEntryKey();

    double getNoEntryValue();

    double put(double d0, double d1);

    double putIfAbsent(double d0, double d1);

    void putAll(Map<? extends Double, ? extends Double> map);

    void putAll(TDoubleDoubleMap tdoubledoublemap);

    double get(double d0);

    void clear();

    boolean isEmpty();

    double remove(double d0);

    int size();

    TDoubleSet keySet();

    double[] keys();

    double[] keys(double[] adouble);

    TDoubleCollection valueCollection();

    double[] values();

    double[] values(double[] adouble);

    boolean containsValue(double d0);

    boolean containsKey(double d0);

    TDoubleDoubleIterator iterator();

    boolean forEachKey(TDoubleProcedure tdoubleprocedure);

    boolean forEachValue(TDoubleProcedure tdoubleprocedure);

    boolean forEachEntry(TDoubleDoubleProcedure tdoubledoubleprocedure);

    void transformValues(TDoubleFunction tdoublefunction);

    boolean retainEntries(TDoubleDoubleProcedure tdoubledoubleprocedure);

    boolean increment(double d0);

    boolean adjustValue(double d0, double d1);

    double adjustOrPutValue(double d0, double d1, double d2);
}
