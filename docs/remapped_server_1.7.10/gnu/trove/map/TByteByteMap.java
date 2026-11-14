package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TByteByteIterator;
import gnu.trove.procedure.TByteByteProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.set.TByteSet;
import java.util.Map;

public interface TByteByteMap {

    byte getNoEntryKey();

    byte getNoEntryValue();

    byte put(byte b0, byte b1);

    byte putIfAbsent(byte b0, byte b1);

    void putAll(Map<? extends Byte, ? extends Byte> map);

    void putAll(TByteByteMap tbytebytemap);

    byte get(byte b0);

    void clear();

    boolean isEmpty();

    byte remove(byte b0);

    int size();

    TByteSet keySet();

    byte[] keys();

    byte[] keys(byte[] abyte);

    TByteCollection valueCollection();

    byte[] values();

    byte[] values(byte[] abyte);

    boolean containsValue(byte b0);

    boolean containsKey(byte b0);

    TByteByteIterator iterator();

    boolean forEachKey(TByteProcedure tbyteprocedure);

    boolean forEachValue(TByteProcedure tbyteprocedure);

    boolean forEachEntry(TByteByteProcedure tbytebyteprocedure);

    void transformValues(TByteFunction tbytefunction);

    boolean retainEntries(TByteByteProcedure tbytebyteprocedure);

    boolean increment(byte b0);

    boolean adjustValue(byte b0, byte b1);

    byte adjustOrPutValue(byte b0, byte b1, byte b2);
}
