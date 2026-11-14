package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TByteLongIterator;
import gnu.trove.procedure.TByteLongProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TByteSet;
import java.util.Map;

public interface TByteLongMap {

    byte getNoEntryKey();

    long getNoEntryValue();

    long put(byte b0, long i);

    long putIfAbsent(byte b0, long i);

    void putAll(Map<? extends Byte, ? extends Long> map);

    void putAll(TByteLongMap tbytelongmap);

    long get(byte b0);

    void clear();

    boolean isEmpty();

    long remove(byte b0);

    int size();

    TByteSet keySet();

    byte[] keys();

    byte[] keys(byte[] abyte);

    TLongCollection valueCollection();

    long[] values();

    long[] values(long[] along);

    boolean containsValue(long i);

    boolean containsKey(byte b0);

    TByteLongIterator iterator();

    boolean forEachKey(TByteProcedure tbyteprocedure);

    boolean forEachValue(TLongProcedure tlongprocedure);

    boolean forEachEntry(TByteLongProcedure tbytelongprocedure);

    void transformValues(TLongFunction tlongfunction);

    boolean retainEntries(TByteLongProcedure tbytelongprocedure);

    boolean increment(byte b0);

    boolean adjustValue(byte b0, long i);

    long adjustOrPutValue(byte b0, long i, long j);
}
