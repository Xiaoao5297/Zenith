package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TLongCharIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongCharProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.util.Map;

public interface TLongCharMap {

    long getNoEntryKey();

    char getNoEntryValue();

    char put(long i, char c0);

    char putIfAbsent(long i, char c0);

    void putAll(Map<? extends Long, ? extends Character> map);

    void putAll(TLongCharMap tlongcharmap);

    char get(long i);

    void clear();

    boolean isEmpty();

    char remove(long i);

    int size();

    TLongSet keySet();

    long[] keys();

    long[] keys(long[] along);

    TCharCollection valueCollection();

    char[] values();

    char[] values(char[] achar);

    boolean containsValue(char c0);

    boolean containsKey(long i);

    TLongCharIterator iterator();

    boolean forEachKey(TLongProcedure tlongprocedure);

    boolean forEachValue(TCharProcedure tcharprocedure);

    boolean forEachEntry(TLongCharProcedure tlongcharprocedure);

    void transformValues(TCharFunction tcharfunction);

    boolean retainEntries(TLongCharProcedure tlongcharprocedure);

    boolean increment(long i);

    boolean adjustValue(long i, char c0);

    char adjustOrPutValue(long i, char c0, char c1);
}
