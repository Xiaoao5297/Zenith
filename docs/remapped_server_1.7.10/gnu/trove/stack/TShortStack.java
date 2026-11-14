package gnu.trove.stack;

public interface TShortStack {

    short getNoEntryValue();

    void push(short short0);

    short pop();

    short peek();

    int size();

    void clear();

    short[] toArray();

    void toArray(short[] ashort);
}
