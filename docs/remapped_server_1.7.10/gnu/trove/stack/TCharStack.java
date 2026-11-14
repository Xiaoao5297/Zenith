package gnu.trove.stack;

public interface TCharStack {

    char getNoEntryValue();

    void push(char c0);

    char pop();

    char peek();

    int size();

    void clear();

    char[] toArray();

    void toArray(char[] achar);
}
