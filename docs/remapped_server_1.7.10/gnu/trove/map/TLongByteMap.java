package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TLongByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.util.Map;

public interface TLongByteMap {

    long getNoEntryKey();

    byte getNoEntryValue();

    byte put(long i, byte b0);

    byte putIfAbsent(long i, byte b0);

    void putAll(Map<? extends Long, ? extends Byte> map);

    void putAll(TLongByteMap tlongbytemap);

    byte get(long i);

    void clear();

    boolean isEmpty();

    byte remove(long i);

    int size();

    TLongSet keySet();

    long[] keys();

    long[] keys(long[] along);

    TByteCollection valueCollection();

    byte[] values();

    byte[] values(byte[] abyte);

    boolean containsValue(byte b0);

    boolean containsKey(long i);

    TLongByteIterator iterator();

    boolean forEachKey(TLongProcedure tlongprocedure);

    boolean forEachValue(TByteProcedure tbyteprocedure);

    boolean forEachEntry(TLongByteProcedure tlongbyteprocedure);

    void transformValues(TByteFunction tbytefunction);

    boolean retainEntries(TLongByteProcedure tlongbyteprocedure);

    boolean increment(long i);

    boolean adjustValue(long i, byte b0);

    byte adjustOrPutValue(long i, byte b0, byte b1);
}
