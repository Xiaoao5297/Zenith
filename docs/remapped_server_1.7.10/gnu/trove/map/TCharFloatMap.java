package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TCharFloatIterator;
import gnu.trove.procedure.TCharFloatProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TCharSet;
import java.util.Map;

public interface TCharFloatMap {

    char getNoEntryKey();

    float getNoEntryValue();

    float put(char c0, float f);

    float putIfAbsent(char c0, float f);

    void putAll(Map<? extends Character, ? extends Float> map);

    void putAll(TCharFloatMap tcharfloatmap);

    float get(char c0);

    void clear();

    boolean isEmpty();

    float remove(char c0);

    int size();

    TCharSet keySet();

    char[] keys();

    char[] keys(char[] achar);

    TFloatCollection valueCollection();

    float[] values();

    float[] values(float[] afloat);

    boolean containsValue(float f);

    boolean containsKey(char c0);

    TCharFloatIterator iterator();

    boolean forEachKey(TCharProcedure tcharprocedure);

    boolean forEachValue(TFloatProcedure tfloatprocedure);

    boolean forEachEntry(TCharFloatProcedure tcharfloatprocedure);

    void transformValues(TFloatFunction tfloatfunction);

    boolean retainEntries(TCharFloatProcedure tcharfloatprocedure);

    boolean increment(char c0);

    boolean adjustValue(char c0, float f);

    float adjustOrPutValue(char c0, float f, float f1);
}
