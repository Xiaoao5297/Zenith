package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TCharLongIterator;
import gnu.trove.procedure.TCharLongProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TCharSet;
import java.util.Map;

public interface TCharLongMap {

    char getNoEntryKey();

    long getNoEntryValue();

    long put(char c0, long i);

    long putIfAbsent(char c0, long i);

    void putAll(Map<? extends Character, ? extends Long> map);

    void putAll(TCharLongMap tcharlongmap);

    long get(char c0);

    void clear();

    boolean isEmpty();

    long remove(char c0);

    int size();

    TCharSet keySet();

    char[] keys();

    char[] keys(char[] achar);

    TLongCollection valueCollection();

    long[] values();

    long[] values(long[] along);

    boolean containsValue(long i);

    boolean containsKey(char c0);

    TCharLongIterator iterator();

    boolean forEachKey(TCharProcedure tcharprocedure);

    boolean forEachValue(TLongProcedure tlongprocedure);

    boolean forEachEntry(TCharLongProcedure tcharlongprocedure);

    void transformValues(TLongFunction tlongfunction);

    boolean retainEntries(TCharLongProcedure tcharlongprocedure);

    boolean increment(char c0);

    boolean adjustValue(char c0, long i);

    long adjustOrPutValue(char c0, long i, long j);
}
