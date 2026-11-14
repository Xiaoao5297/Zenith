package gnu.trove.list;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Random;

public interface TFloatList extends TFloatCollection {

    @Override
    float getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean add(float f);

    void add(float[] afloat);

    void add(float[] afloat, int i, int j);

    void insert(int i, float f);

    void insert(int i, float[] afloat);

    void insert(int i, float[] afloat, int j, int k);

    float get(int i);

    float set(int i, float f);

    void set(int i, float[] afloat);

    void set(int i, float[] afloat, int j, int k);

    float replace(int i, float f);

    @Override
    void clear();

    @Override
    boolean remove(float f);

    float removeAt(int i);

    void remove(int i, int j);

    void transformValues(TFloatFunction tfloatfunction);

    void reverse();

    void reverse(int i, int j);

    void shuffle(Random random);

    TFloatList subList(int i, int j);

    @Override
    float[] toArray();

    float[] toArray(int i, int j);

    @Override
    float[] toArray(float[] afloat);

    float[] toArray(float[] afloat, int i, int j);

    float[] toArray(float[] afloat, int i, int j, int k);

    @Override
    boolean forEach(TFloatProcedure tfloatprocedure);

    boolean forEachDescending(TFloatProcedure tfloatprocedure);

    void sort();

    void sort(int i, int j);

    void fill(float f);

    void fill(int i, int j, float f);

    int binarySearch(float f);

    int binarySearch(float f, int i, int j);

    int indexOf(float f);

    int indexOf(int i, float f);

    int lastIndexOf(float f);

    int lastIndexOf(int i, float f);

    @Override
    boolean contains(float f);

    TFloatList grep(TFloatProcedure tfloatprocedure);

    TFloatList inverseGrep(TFloatProcedure tfloatprocedure);

    float max();

    float min();

    float sum();
}
