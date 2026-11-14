package gnu.trove.stack;

public interface TByteStack {

    byte getNoEntryValue();

    void push(byte b0);

    byte pop();

    byte peek();

    int size();

    void clear();

    byte[] toArray();

    void toArray(byte[] abyte);
}
