package gnu.trove.stack;

public interface TDoubleStack {

    double getNoEntryValue();

    void push(double d0);

    double pop();

    double peek();

    int size();

    void clear();

    double[] toArray();

    void toArray(double[] adouble);
}
