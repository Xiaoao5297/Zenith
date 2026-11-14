package gnu.trove.impl;

public final class HashFunctions {

    public HashFunctions() {}

    public static int hash(double d0) {
        assert !Double.isNaN(d0) : "Values of NaN are not supported.";

        long i = Double.doubleToLongBits(d0);

        return (int) (i ^ i >>> 32);
    }

    public static int hash(float f) {
        assert !Float.isNaN(f) : "Values of NaN are not supported.";

        return Float.floatToIntBits(f * 6.6360896E8F);
    }

    public static int hash(int i) {
        return i;
    }

    public static int hash(long i) {
        return (int) (i ^ i >>> 32);
    }

    public static int hash(Object object) {
        return object == null ? 0 : object.hashCode();
    }

    public static int fastCeil(float f) {
        int i = (int) f;

        if (f - (float) i > 0.0F) {
            ++i;
        }

        return i;
    }
}
