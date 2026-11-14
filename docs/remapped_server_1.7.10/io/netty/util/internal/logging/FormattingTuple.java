package io.netty.util.internal.logging;

class FormattingTuple {

    static final FormattingTuple NULL = new FormattingTuple((String) null);
    private final String message;
    private final Throwable throwable;
    private final Object[] argArray;

    FormattingTuple(String s) {
        this(s, (Object[]) null, (Throwable) null);
    }

    FormattingTuple(String s, Object[] aobject, Throwable throwable) {
        this.message = s;
        this.throwable = throwable;
        if (throwable == null) {
            this.argArray = aobject;
        } else {
            this.argArray = trimmedCopy(aobject);
        }

    }

    static Object[] trimmedCopy(Object[] aobject) {
        if (aobject != null && aobject.length != 0) {
            int i = aobject.length - 1;
            Object[] aobject1 = new Object[i];

            System.arraycopy(aobject, 0, aobject1, 0, i);
            return aobject1;
        } else {
            throw new IllegalStateException("non-sensical empty or null argument array");
        }
    }

    public String getMessage() {
        return this.message;
    }

    public Object[] getArgArray() {
        return this.argArray;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }
}
