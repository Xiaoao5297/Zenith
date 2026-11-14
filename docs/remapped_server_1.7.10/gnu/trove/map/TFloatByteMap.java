package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TFloatByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Map;

public interface TFloatByteMap {

    float getNoEntryKey();

    byte getNoEntryValue();

    byte put(float f, byte b0);

    byte putIfAbsent(float f, byte b0);

    void putAll(Map<? extends Float, ? extends Byte> map);

    void putAll(TFloatByteMap tfloatbytemap);

    byte get(float f);

    void clear();

    boolean isEmpty();

    byte remove(float f);

    int size();

    TFloatSet keySet();

    float[] keys();

    float[] keys(float[] afloat);

    TByteCollection valueCollection();

    byte[] values();

    byte[] values(byte[] abyte);

    boolean containsValue(byte b0);

    boolean containsKey(float f);

    TFloatByteIterator iterator();

    boolean forEachKey(TFloatProcedure tfloatprocedure);

    boolean forEachValue(TByteProcedure tbyteprocedure);

    boolean forEachEntry(TFloatByteProcedure tfloatbyteprocedure);

    void transformValues(TByteFunction tbytefunction);

    boolean retainEntries(TFloatByteProcedure tfloatbyteprocedure);

    boolean increment(float f);

    boolean adjustValue(float f, byte b0);

    byte adjustOrPutValue(float f, byte b0, byte b1);
}
