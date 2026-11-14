package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TCharShortIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TCharShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TCharSet;
import java.util.Map;

public interface TCharShortMap {

    char getNoEntryKey();

    short getNoEntryValue();

    short put(char c0, short short0);

    short putIfAbsent(char c0, short short0);

    void putAll(Map<? extends Character, ? extends Short> map);

    void putAll(TCharShortMap tcharshortmap);

    short get(char c0);

    void clear();

    boolean isEmpty();

    short remove(char c0);

    int size();

    TCharSet keySet();

    char[] keys();

    char[] keys(char[] achar);

    TShortCollection valueCollection();

    short[] values();

    short[] values(short[] ashort);

    boolean containsValue(short short0);

    boolean containsKey(char c0);

    TCharShortIterator iterator();

    boolean forEachKey(TCharProcedure tcharprocedure);

    boolean forEachValue(TShortProcedure tshortprocedure);

    boolean forEachEntry(TCharShortProcedure tcharshortprocedure);

    void transformValues(TShortFunction tshortfunction);

    boolean retainEntries(TCharShortProcedure tcharshortprocedure);

    boolean increment(char c0);

    boolean adjustValue(char c0, short short0);

    short adjustOrPutValue(char c0, short short0, short short1);
}
