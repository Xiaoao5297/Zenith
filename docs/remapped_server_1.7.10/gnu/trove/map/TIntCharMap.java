package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TIntCharIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntCharProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.util.Map;

public interface TIntCharMap {

    int getNoEntryKey();

    char getNoEntryValue();

    char put(int i, char c0);

    char putIfAbsent(int i, char c0);

    void putAll(Map<? extends Integer, ? extends Character> map);

    void putAll(TIntCharMap tintcharmap);

    char get(int i);

    void clear();

    boolean isEmpty();

    char remove(int i);

    int size();

    TIntSet keySet();

    int[] keys();

    int[] keys(int[] aint);

    TCharCollection valueCollection();

    char[] values();

    char[] values(char[] achar);

    boolean containsValue(char c0);

    boolean containsKey(int i);

    TIntCharIterator iterator();

    boolean forEachKey(TIntProcedure tintprocedure);

    boolean forEachValue(TCharProcedure tcharprocedure);

    boolean forEachEntry(TIntCharProcedure tintcharprocedure);

    void transformValues(TCharFunction tcharfunction);

    boolean retainEntries(TIntCharProcedure tintcharprocedure);

    boolean increment(int i);

    boolean adjustValue(int i, char c0);

    char adjustOrPutValue(int i, char c0, char c1);
}
