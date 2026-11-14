package gnu.trove.stack;

public interface TFloatStack {

    float getNoEntryValue();

    void push(float f);

    float pop();

    float peek();

    int size();

    void clear();

    float[] toArray();

    void toArray(float[] afloat);
}
