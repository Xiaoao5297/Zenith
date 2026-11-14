package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TIntShortIterator;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TIntShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TIntSet;
import java.util.Map;

public interface TIntShortMap {

    int getNoEntryKey();

    short getNoEntryValue();

    short put(int i, short short0);

    short putIfAbsent(int i, short short0);

    void putAll(Map<? extends Integer, ? extends Short> map);

    void putAll(TIntShortMap tintshortmap);

    short get(int i);

    void clear();

    boolean isEmpty();

    short remove(int i);

    int size();

    TIntSet keySet();

    int[] keys();

    int[] keys(int[] aint);

    TShortCollection valueCollection();

    short[] values();

    short[] values(short[] ashort);

    boolean containsValue(short short0);

    boolean containsKey(int i);

    TIntShortIterator iterator();

    boolean forEachKey(TIntProcedure tintprocedure);

    boolean forEachValue(TShortProcedure tshortprocedure);

    boolean forEachEntry(TIntShortProcedure tintshortprocedure);

    void transformValues(TShortFunction tshortfunction);

    boolean retainEntries(TIntShortProcedure tintshortprocedure);

    boolean increment(int i);

    boolean adjustValue(int i, short short0);

    short adjustOrPutValue(int i, short short0, short short1);
}
