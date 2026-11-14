package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TByteShortIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TByteShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TByteSet;
import java.util.Map;

public interface TByteShortMap {

    byte getNoEntryKey();

    short getNoEntryValue();

    short put(byte b0, short short0);

    short putIfAbsent(byte b0, short short0);

    void putAll(Map<? extends Byte, ? extends Short> map);

    void putAll(TByteShortMap tbyteshortmap);

    short get(byte b0);

    void clear();

    boolean isEmpty();

    short remove(byte b0);

    int size();

    TByteSet keySet();

    byte[] keys();

    byte[] keys(byte[] abyte);

    TShortCollection valueCollection();

    short[] values();

    short[] values(short[] ashort);

    boolean containsValue(short short0);

    boolean containsKey(byte b0);

    TByteShortIterator iterator();

    boolean forEachKey(TByteProcedure tbyteprocedure);

    boolean forEachValue(TShortProcedure tshortprocedure);

    boolean forEachEntry(TByteShortProcedure tbyteshortprocedure);

    void transformValues(TShortFunction tshortfunction);

    boolean retainEntries(TByteShortProcedure tbyteshortprocedure);

    boolean increment(byte b0);

    boolean adjustValue(byte b0, short short0);

    short adjustOrPutValue(byte b0, short short0, short short1);
}
