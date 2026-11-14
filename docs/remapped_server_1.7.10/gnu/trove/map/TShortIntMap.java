package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TShortIntIterator;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TShortIntProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Map;

public interface TShortIntMap {

    short getNoEntryKey();

    int getNoEntryValue();

    int put(short short0, int i);

    int putIfAbsent(short short0, int i);

    void putAll(Map<? extends Short, ? extends Integer> map);

    void putAll(TShortIntMap tshortintmap);

    int get(short short0);

    void clear();

    boolean isEmpty();

    int remove(short short0);

    int size();

    TShortSet keySet();

    short[] keys();

    short[] keys(short[] ashort);

    TIntCollection valueCollection();

    int[] values();

    int[] values(int[] aint);

    boolean containsValue(int i);

    boolean containsKey(short short0);

    TShortIntIterator iterator();

    boolean forEachKey(TShortProcedure tshortprocedure);

    boolean forEachValue(TIntProcedure tintprocedure);

    boolean forEachEntry(TShortIntProcedure tshortintprocedure);

    void transformValues(TIntFunction tintfunction);

    boolean retainEntries(TShortIntProcedure tshortintprocedure);

    boolean increment(short short0);

    boolean adjustValue(short short0, int i);

    int adjustOrPutValue(short short0, int i, int j);
}
