package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TDoubleShortIterator;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TDoubleShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleShortMap {

    double getNoEntryKey();

    short getNoEntryValue();

    short put(double d0, short short0);

    short putIfAbsent(double d0, short short0);

    void putAll(Map<? extends Double, ? extends Short> map);

    void putAll(TDoubleShortMap tdoubleshortmap);

    short get(double d0);

    void clear();

    boolean isEmpty();

    short remove(double d0);

    int size();

    TDoubleSet keySet();

    double[] keys();

    double[] keys(double[] adouble);

    TShortCollection valueCollection();

    short[] values();

    short[] values(short[] ashort);

    boolean containsValue(short short0);

    boolean containsKey(double d0);

    TDoubleShortIterator iterator();

    boolean forEachKey(TDoubleProcedure tdoubleprocedure);

    boolean forEachValue(TShortProcedure tshortprocedure);

    boolean forEachEntry(TDoubleShortProcedure tdoubleshortprocedure);

    void transformValues(TShortFunction tshortfunction);

    boolean retainEntries(TDoubleShortProcedure tdoubleshortprocedure);

    boolean increment(double d0);

    boolean adjustValue(double d0, short short0);

    short adjustOrPutValue(double d0, short short0, short short1);
}
