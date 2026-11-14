package io.netty.handler.codec;

public class UnsupportedMessageTypeException extends CodecException {

    private static final long serialVersionUID = 2799598826487038726L;

    public UnsupportedMessageTypeException(Object object, Class<?>... aclass) {
        super(message(object == null ? "null" : object.getClass().getName(), aclass));
    }

    public UnsupportedMessageTypeException() {}

    public UnsupportedMessageTypeException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public UnsupportedMessageTypeException(String s) {
        super(s);
    }

    public UnsupportedMessageTypeException(Throwable throwable) {
        super(throwable);
    }

    private static String message(String s, Class<?>... aclass) {
        StringBuilder stringbuilder = new StringBuilder(s);

        if (aclass != null && aclass.length > 0) {
            stringbuilder.append(" (expected: ").append(aclass[0].getName());

            for (int i = 1; i < aclass.length; ++i) {
                Class oclass = aclass[i];

                if (oclass == null) {
                    break;
                }

                stringbuilder.append(", ").append(oclass.getName());
            }

            stringbuilder.append(')');
        }

        return stringbuilder.toString();
    }
}
