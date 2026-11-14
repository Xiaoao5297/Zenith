package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TByteCharIterator;
import gnu.trove.procedure.TByteCharProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TByteSet;
import java.util.Map;

public interface TByteCharMap {

    byte getNoEntryKey();

    char getNoEntryValue();

    char put(byte b0, char c0);

    char putIfAbsent(byte b0, char c0);

    void putAll(Map<? extends Byte, ? extends Character> map);

    void putAll(TByteCharMap tbytecharmap);

    char get(byte b0);

    void clear();

    boolean isEmpty();

    char remove(byte b0);

    int size();

    TByteSet keySet();

    byte[] keys();

    byte[] keys(byte[] abyte);

    TCharCollection valueCollection();

    char[] values();

    char[] values(char[] achar);

    boolean containsValue(char c0);

    boolean containsKey(byte b0);

    TByteCharIterator iterator();

    boolean forEachKey(TByteProcedure tbyteprocedure);

    boolean forEachValue(TCharProcedure tcharprocedure);

    boolean forEachEntry(TByteCharProcedure tbytecharprocedure);

    void transformValues(TCharFunction tcharfunction);

    boolean retainEntries(TByteCharProcedure tbytecharprocedure);

    boolean increment(byte b0);

    boolean adjustValue(byte b0, char c0);

    char adjustOrPutValue(byte b0, char c0, char c1);
}
