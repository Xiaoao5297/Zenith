package gnu.trove.list;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Random;

public interface TDoubleList extends TDoubleCollection {

    @Override
    double getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean add(double d0);

    void add(double[] adouble);

    void add(double[] adouble, int i, int j);

    void insert(int i, double d0);

    void insert(int i, double[] adouble);

    void insert(int i, double[] adouble, int j, int k);

    double get(int i);

    double set(int i, double d0);

    void set(int i, double[] adouble);

    void set(int i, double[] adouble, int j, int k);

    double replace(int i, double d0);

    @Override
    void clear();

    @Override
    boolean remove(double d0);

    double removeAt(int i);

    void remove(int i, int j);

    void transformValues(TDoubleFunction tdoublefunction);

    void reverse();

    void reverse(int i, int j);

    void shuffle(Random random);

    TDoubleList subList(int i, int j);

    @Override
    double[] toArray();

    double[] toArray(int i, int j);

    @Override
    double[] toArray(double[] adouble);

    double[] toArray(double[] adouble, int i, int j);

    double[] toArray(double[] adouble, int i, int j, int k);

    @Override
    boolean forEach(TDoubleProcedure tdoubleprocedure);

    boolean forEachDescending(TDoubleProcedure tdoubleprocedure);

    void sort();

    void sort(int i, int j);

    void fill(double d0);

    void fill(int i, int j, double d0);

    int binarySearch(double d0);

    int binarySearch(double d0, int i, int j);

    int indexOf(double d0);

    int indexOf(int i, double d0);

    int lastIndexOf(double d0);

    int lastIndexOf(int i, double d0);

    @Override
    boolean contains(double d0);

    TDoubleList grep(TDoubleProcedure tdoubleprocedure);

    TDoubleList inverseGrep(TDoubleProcedure tdoubleprocedure);

    double max();

    double min();

    double sum();
}
