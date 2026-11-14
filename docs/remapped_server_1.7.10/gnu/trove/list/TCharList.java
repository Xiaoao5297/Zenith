package gnu.trove.list;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.procedure.TCharProcedure;
import java.util.Random;

public interface TCharList extends TCharCollection {

    @Override
    char getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean add(char c0);

    void add(char[] achar);

    void add(char[] achar, int i, int j);

    void insert(int i, char c0);

    void insert(int i, char[] achar);

    void insert(int i, char[] achar, int j, int k);

    char get(int i);

    char set(int i, char c0);

    void set(int i, char[] achar);

    void set(int i, char[] achar, int j, int k);

    char replace(int i, char c0);

    @Override
    void clear();

    @Override
    boolean remove(char c0);

    char removeAt(int i);

    void remove(int i, int j);

    void transformValues(TCharFunction tcharfunction);

    void reverse();

    void reverse(int i, int j);

    void shuffle(Random random);

    TCharList subList(int i, int j);

    @Override
    char[] toArray();

    char[] toArray(int i, int j);

    @Override
    char[] toArray(char[] achar);

    char[] toArray(char[] achar, int i, int j);

    char[] toArray(char[] achar, int i, int j, int k);

    @Override
    boolean forEach(TCharProcedure tcharprocedure);

    boolean forEachDescending(TCharProcedure tcharprocedure);

    void sort();

    void sort(int i, int j);

    void fill(char c0);

    void fill(int i, int j, char c0);

    int binarySearch(char c0);

    int binarySearch(char c0, int i, int j);

    int indexOf(char c0);

    int indexOf(int i, char c0);

    int lastIndexOf(char c0);

    int lastIndexOf(int i, char c0);

    @Override
    boolean contains(char c0);

    TCharList grep(TCharProcedure tcharprocedure);

    TCharList inverseGrep(TCharProcedure tcharprocedure);

    char max();

    char min();

    char sum();
}
