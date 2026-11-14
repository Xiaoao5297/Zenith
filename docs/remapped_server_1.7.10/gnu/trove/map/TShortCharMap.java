package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TShortCharIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TShortCharProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Map;

public interface TShortCharMap {

    short getNoEntryKey();

    char getNoEntryValue();

    char put(short short0, char c0);

    char putIfAbsent(short short0, char c0);

    void putAll(Map<? extends Short, ? extends Character> map);

    void putAll(TShortCharMap tshortcharmap);

    char get(short short0);

    void clear();

    boolean isEmpty();

    char remove(short short0);

    int size();

    TShortSet keySet();

    short[] keys();

    short[] keys(short[] ashort);

    TCharCollection valueCollection();

    char[] values();

    char[] values(char[] achar);

    boolean containsValue(char c0);

    boolean containsKey(short short0);

    TShortCharIterator iterator();

    boolean forEachKey(TShortProcedure tshortprocedure);

    boolean forEachValue(TCharProcedure tcharprocedure);

    boolean forEachEntry(TShortCharProcedure tshortcharprocedure);

    void transformValues(TCharFunction tcharfunction);

    boolean retainEntries(TShortCharProcedure tshortcharprocedure);

    boolean increment(short short0);

    boolean adjustValue(short short0, char c0);

    char adjustOrPutValue(short short0, char c0, char c1);
}
