package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TShortShortIterator;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.procedure.TShortShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Map;

public interface TShortShortMap {

    short getNoEntryKey();

    short getNoEntryValue();

    short put(short short0, short short1);

    short putIfAbsent(short short0, short short1);

    void putAll(Map<? extends Short, ? extends Short> map);

    void putAll(TShortShortMap tshortshortmap);

    short get(short short0);

    void clear();

    boolean isEmpty();

    short remove(short short0);

    int size();

    TShortSet keySet();

    short[] keys();

    short[] keys(short[] ashort);

    TShortCollection valueCollection();

    short[] values();

    short[] values(short[] ashort);

    boolean containsValue(short short0);

    boolean containsKey(short short0);

    TShortShortIterator iterator();

    boolean forEachKey(TShortProcedure tshortprocedure);

    boolean forEachValue(TShortProcedure tshortprocedure);

    boolean forEachEntry(TShortShortProcedure tshortshortprocedure);

    void transformValues(TShortFunction tshortfunction);

    boolean retainEntries(TShortShortProcedure tshortshortprocedure);

    boolean increment(short short0);

    boolean adjustValue(short short0, short short1);

    short adjustOrPutValue(short short0, short short1, short short2);
}
