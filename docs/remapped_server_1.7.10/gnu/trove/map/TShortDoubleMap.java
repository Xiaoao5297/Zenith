package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TShortDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TShortDoubleProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Map;

public interface TShortDoubleMap {

    short getNoEntryKey();

    double getNoEntryValue();

    double put(short short0, double d0);

    double putIfAbsent(short short0, double d0);

    void putAll(Map<? extends Short, ? extends Double> map);

    void putAll(TShortDoubleMap tshortdoublemap);

    double get(short short0);

    void clear();

    boolean isEmpty();

    double remove(short short0);

    int size();

    TShortSet keySet();

    short[] keys();

    short[] keys(short[] ashort);

    TDoubleCollection valueCollection();

    double[] values();

    double[] values(double[] adouble);

    boolean containsValue(double d0);

    boolean containsKey(short short0);

    TShortDoubleIterator iterator();

    boolean forEachKey(TShortProcedure tshortprocedure);

    boolean forEachValue(TDoubleProcedure tdoubleprocedure);

    boolean forEachEntry(TShortDoubleProcedure tshortdoubleprocedure);

    void transformValues(TDoubleFunction tdoublefunction);

    boolean retainEntries(TShortDoubleProcedure tshortdoubleprocedure);

    boolean increment(short short0);

    boolean adjustValue(short short0, double d0);

    double adjustOrPutValue(short short0, double d0, double d1);
}
