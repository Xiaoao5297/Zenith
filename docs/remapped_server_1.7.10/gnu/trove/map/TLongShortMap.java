package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TLongShortIterator;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TLongShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TLongSet;
import java.util.Map;

public interface TLongShortMap {

    long getNoEntryKey();

    short getNoEntryValue();

    short put(long i, short short0);

    short putIfAbsent(long i, short short0);

    void putAll(Map<? extends Long, ? extends Short> map);

    void putAll(TLongShortMap tlongshortmap);

    short get(long i);

    void clear();

    boolean isEmpty();

    short remove(long i);

    int size();

    TLongSet keySet();

    long[] keys();

    long[] keys(long[] along);

    TShortCollection valueCollection();

    short[] values();

    short[] values(short[] ashort);

    boolean containsValue(short short0);

    boolean containsKey(long i);

    TLongShortIterator iterator();

    boolean forEachKey(TLongProcedure tlongprocedure);

    boolean forEachValue(TShortProcedure tshortprocedure);

    boolean forEachEntry(TLongShortProcedure tlongshortprocedure);

    void transformValues(TShortFunction tshortfunction);

    boolean retainEntries(TLongShortProcedure tlongshortprocedure);

    boolean increment(long i);

    boolean adjustValue(long i, short short0);

    short adjustOrPutValue(long i, short short0, short short1);
}
