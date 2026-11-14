package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TCharByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TCharSet;
import java.util.Map;

public interface TCharByteMap {

    char getNoEntryKey();

    byte getNoEntryValue();

    byte put(char c0, byte b0);

    byte putIfAbsent(char c0, byte b0);

    void putAll(Map<? extends Character, ? extends Byte> map);

    void putAll(TCharByteMap tcharbytemap);

    byte get(char c0);

    void clear();

    boolean isEmpty();

    byte remove(char c0);

    int size();

    TCharSet keySet();

    char[] keys();

    char[] keys(char[] achar);

    TByteCollection valueCollection();

    byte[] values();

    byte[] values(byte[] abyte);

    boolean containsValue(byte b0);

    boolean containsKey(char c0);

    TCharByteIterator iterator();

    boolean forEachKey(TCharProcedure tcharprocedure);

    boolean forEachValue(TByteProcedure tbyteprocedure);

    boolean forEachEntry(TCharByteProcedure tcharbyteprocedure);

    void transformValues(TByteFunction tbytefunction);

    boolean retainEntries(TCharByteProcedure tcharbyteprocedure);

    boolean increment(char c0);

    boolean adjustValue(char c0, byte b0);

    byte adjustOrPutValue(char c0, byte b0, byte b1);
}
