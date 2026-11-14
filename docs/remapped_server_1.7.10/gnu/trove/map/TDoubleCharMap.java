package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TDoubleCharIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleCharMap {

    double getNoEntryKey();

    char getNoEntryValue();

    char put(double d0, char c0);

    char putIfAbsent(double d0, char c0);

    void putAll(Map<? extends Double, ? extends Character> map);

    void putAll(TDoubleCharMap tdoublecharmap);

    char get(double d0);

    void clear();

    boolean isEmpty();

    char remove(double d0);

    int size();

    TDoubleSet keySet();

    double[] keys();

    double[] keys(double[] adouble);

    TCharCollection valueCollection();

    char[] values();

    char[] values(char[] achar);

    boolean containsValue(char c0);

    boolean containsKey(double d0);

    TDoubleCharIterator iterator();

    boolean forEachKey(TDoubleProcedure tdoubleprocedure);

    boolean forEachValue(TCharProcedure tcharprocedure);

    boolean forEachEntry(TDoubleCharProcedure tdoublecharprocedure);

    void transformValues(TCharFunction tcharfunction);

    boolean retainEntries(TDoubleCharProcedure tdoublecharprocedure);

    boolean increment(double d0);

    boolean adjustValue(double d0, char c0);

    char adjustOrPutValue(double d0, char c0, char c1);
}
