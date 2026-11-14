package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TShortByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TShortByteProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Map;

public interface TShortByteMap {

    short getNoEntryKey();

    byte getNoEntryValue();

    byte put(short short0, byte b0);

    byte putIfAbsent(short short0, byte b0);

    void putAll(Map<? extends Short, ? extends Byte> map);

    void putAll(TShortByteMap tshortbytemap);

    byte get(short short0);

    void clear();

    boolean isEmpty();

    byte remove(short short0);

    int size();

    TShortSet keySet();

    short[] keys();

    short[] keys(short[] ashort);

    TByteCollection valueCollection();

    byte[] values();

    byte[] values(byte[] abyte);

    boolean containsValue(byte b0);

    boolean containsKey(short short0);

    TShortByteIterator iterator();

    boolean forEachKey(TShortProcedure tshortprocedure);

    boolean forEachValue(TByteProcedure tbyteprocedure);

    boolean forEachEntry(TShortByteProcedure tshortbyteprocedure);

    void transformValues(TByteFunction tbytefunction);

    boolean retainEntries(TShortByteProcedure tshortbyteprocedure);

    boolean increment(short short0);

    boolean adjustValue(short short0, byte b0);

    byte adjustOrPutValue(short short0, byte b0, byte b1);
}
