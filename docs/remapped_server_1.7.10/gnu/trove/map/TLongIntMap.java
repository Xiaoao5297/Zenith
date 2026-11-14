package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.util.Map;

public interface TLongIntMap {

    long getNoEntryKey();

    int getNoEntryValue();

    int put(long i, int j);

    int putIfAbsent(long i, int j);

    void putAll(Map<? extends Long, ? extends Integer> map);

    void putAll(TLongIntMap tlongintmap);

    int get(long i);

    void clear();

    boolean isEmpty();

    int remove(long i);

    int size();

    TLongSet keySet();

    long[] keys();

    long[] keys(long[] along);

    TIntCollection valueCollection();

    int[] values();

    int[] values(int[] aint);

    boolean containsValue(int i);

    boolean containsKey(long i);

    TLongIntIterator iterator();

    boolean forEachKey(TLongProcedure tlongprocedure);

    boolean forEachValue(TIntProcedure tintprocedure);

    boolean forEachEntry(TLongIntProcedure tlongintprocedure);

    void transformValues(TIntFunction tintfunction);

    boolean retainEntries(TLongIntProcedure tlongintprocedure);

    boolean increment(long i);

    boolean adjustValue(long i, int j);

    int adjustOrPutValue(long i, int j, int k);
}
