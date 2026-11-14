package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TByteFloatIterator;
import gnu.trove.procedure.TByteFloatProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TByteSet;
import java.util.Map;

public interface TByteFloatMap {

    byte getNoEntryKey();

    float getNoEntryValue();

    float put(byte b0, float f);

    float putIfAbsent(byte b0, float f);

    void putAll(Map<? extends Byte, ? extends Float> map);

    void putAll(TByteFloatMap tbytefloatmap);

    float get(byte b0);

    void clear();

    boolean isEmpty();

    float remove(byte b0);

    int size();

    TByteSet keySet();

    byte[] keys();

    byte[] keys(byte[] abyte);

    TFloatCollection valueCollection();

    float[] values();

    float[] values(float[] afloat);

    boolean containsValue(float f);

    boolean containsKey(byte b0);

    TByteFloatIterator iterator();

    boolean forEachKey(TByteProcedure tbyteprocedure);

    boolean forEachValue(TFloatProcedure tfloatprocedure);

    boolean forEachEntry(TByteFloatProcedure tbytefloatprocedure);

    void transformValues(TFloatFunction tfloatfunction);

    boolean retainEntries(TByteFloatProcedure tbytefloatprocedure);

    boolean increment(byte b0);

    boolean adjustValue(byte b0, float f);

    float adjustOrPutValue(byte b0, float f, float f1);
}
