package gnu.trove.list;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.procedure.TIntProcedure;
import java.util.Random;

public interface TIntList extends TIntCollection {

    @Override
    int getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean add(int i);

    void add(int[] aint);

    void add(int[] aint, int i, int j);

    void insert(int i, int j);

    void insert(int i, int[] aint);

    void insert(int i, int[] aint, int j, int k);

    int get(int i);

    int set(int i, int j);

    void set(int i, int[] aint);

    void set(int i, int[] aint, int j, int k);

    int replace(int i, int j);

    @Override
    void clear();

    @Override
    boolean remove(int i);

    int removeAt(int i);

    void remove(int i, int j);

    void transformValues(TIntFunction tintfunction);

    void reverse();

    void reverse(int i, int j);

    void shuffle(Random random);

    TIntList subList(int i, int j);

    @Override
    int[] toArray();

    int[] toArray(int i, int j);

    @Override
    int[] toArray(int[] aint);

    int[] toArray(int[] aint, int i, int j);

    int[] toArray(int[] aint, int i, int j, int k);

    @Override
    boolean forEach(TIntProcedure tintprocedure);

    boolean forEachDescending(TIntProcedure tintprocedure);

    void sort();

    void sort(int i, int j);

    void fill(int i);

    void fill(int i, int j, int k);

    int binarySearch(int i);

    int binarySearch(int i, int j, int k);

    int indexOf(int i);

    int indexOf(int i, int j);

    int lastIndexOf(int i);

    int lastIndexOf(int i, int j);

    @Override
    boolean contains(int i);

    TIntList grep(TIntProcedure tintprocedure);

    TIntList inverseGrep(TIntProcedure tintprocedure);

    int max();

    int min();

    int sum();
}
