package gnu.trove.stack;

public interface TIntStack {

    int getNoEntryValue();

    void push(int i);

    int pop();

    int peek();

    int size();

    void clear();

    int[] toArray();

    void toArray(int[] aint);
}
