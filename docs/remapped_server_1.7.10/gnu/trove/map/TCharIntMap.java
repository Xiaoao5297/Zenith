package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TCharIntIterator;
import gnu.trove.procedure.TCharIntProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TCharSet;
import java.util.Map;

public interface TCharIntMap {

    char getNoEntryKey();

    int getNoEntryValue();

    int put(char c0, int i);

    int putIfAbsent(char c0, int i);

    void putAll(Map<? extends Character, ? extends Integer> map);

    void putAll(TCharIntMap tcharintmap);

    int get(char c0);

    void clear();

    boolean isEmpty();

    int remove(char c0);

    int size();

    TCharSet keySet();

    char[] keys();

    char[] keys(char[] achar);

    TIntCollection valueCollection();

    int[] values();

    int[] values(int[] aint);

    boolean containsValue(int i);

    boolean containsKey(char c0);

    TCharIntIterator iterator();

    boolean forEachKey(TCharProcedure tcharprocedure);

    boolean forEachValue(TIntProcedure tintprocedure);

    boolean forEachEntry(TCharIntProcedure tcharintprocedure);

    void transformValues(TIntFunction tintfunction);

    boolean retainEntries(TCharIntProcedure tcharintprocedure);

    boolean increment(char c0);

    boolean adjustValue(char c0, int i);

    int adjustOrPutValue(char c0, int i, int j);
}
