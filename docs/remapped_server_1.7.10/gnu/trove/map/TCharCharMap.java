package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TCharCharIterator;
import gnu.trove.procedure.TCharCharProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TCharSet;
import java.util.Map;

public interface TCharCharMap {

    char getNoEntryKey();

    char getNoEntryValue();

    char put(char c0, char c1);

    char putIfAbsent(char c0, char c1);

    void putAll(Map<? extends Character, ? extends Character> map);

    void putAll(TCharCharMap tcharcharmap);

    char get(char c0);

    void clear();

    boolean isEmpty();

    char remove(char c0);

    int size();

    TCharSet keySet();

    char[] keys();

    char[] keys(char[] achar);

    TCharCollection valueCollection();

    char[] values();

    char[] values(char[] achar);

    boolean containsValue(char c0);

    boolean containsKey(char c0);

    TCharCharIterator iterator();

    boolean forEachKey(TCharProcedure tcharprocedure);

    boolean forEachValue(TCharProcedure tcharprocedure);

    boolean forEachEntry(TCharCharProcedure tcharcharprocedure);

    void transformValues(TCharFunction tcharfunction);

    boolean retainEntries(TCharCharProcedure tcharcharprocedure);

    boolean increment(char c0);

    boolean adjustValue(char c0, char c1);

    char adjustOrPutValue(char c0, char c1, char c2);
}
