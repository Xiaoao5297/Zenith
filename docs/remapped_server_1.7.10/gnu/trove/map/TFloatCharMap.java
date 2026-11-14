package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TFloatCharIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Map;

public interface TFloatCharMap {

    float getNoEntryKey();

    char getNoEntryValue();

    char put(float f, char c0);

    char putIfAbsent(float f, char c0);

    void putAll(Map<? extends Float, ? extends Character> map);

    void putAll(TFloatCharMap tfloatcharmap);

    char get(float f);

    void clear();

    boolean isEmpty();

    char remove(float f);

    int size();

    TFloatSet keySet();

    float[] keys();

    float[] keys(float[] afloat);

    TCharCollection valueCollection();

    char[] values();

    char[] values(char[] achar);

    boolean containsValue(char c0);

    boolean containsKey(float f);

    TFloatCharIterator iterator();

    boolean forEachKey(TFloatProcedure tfloatprocedure);

    boolean forEachValue(TCharProcedure tcharprocedure);

    boolean forEachEntry(TFloatCharProcedure tfloatcharprocedure);

    void transformValues(TCharFunction tcharfunction);

    boolean retainEntries(TFloatCharProcedure tfloatcharprocedure);

    boolean increment(float f);

    boolean adjustValue(float f, char c0);

    char adjustOrPutValue(float f, char c0, char c1);
}
