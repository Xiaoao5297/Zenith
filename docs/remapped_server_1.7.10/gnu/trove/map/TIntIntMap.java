package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.util.Map;

public interface TIntIntMap {

    int getNoEntryKey();

    int getNoEntryValue();

    int put(int i, int j);

    int putIfAbsent(int i, int j);

    void putAll(Map<? extends Integer, ? extends Integer> map);

    void putAll(TIntIntMap tintintmap);

    int get(int i);

    void clear();

    boolean isEmpty();

    int remove(int i);

    int size();

    TIntSet keySet();

    int[] keys();

    int[] keys(int[] aint);

    TIntCollection valueCollection();

    int[] values();

    int[] values(int[] aint);

    boolean containsValue(int i);

    boolean containsKey(int i);

    TIntIntIterator iterator();

    boolean forEachKey(TIntProcedure tintprocedure);

    boolean forEachValue(TIntProcedure tintprocedure);

    boolean forEachEntry(TIntIntProcedure tintintprocedure);

    void transformValues(TIntFunction tintfunction);

    boolean retainEntries(TIntIntProcedure tintintprocedure);

    boolean increment(int i);

    boolean adjustValue(int i, int j);

    int adjustOrPutValue(int i, int j, int k);
}
