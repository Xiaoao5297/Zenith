package org.apache.logging.log4j.message;

public final class StringFormatterMessageFactory extends AbstractMessageFactory {

    public static final StringFormatterMessageFactory INSTANCE = new StringFormatterMessageFactory();

    public StringFormatterMessageFactory() {}

    @Override
    public Message newMessage(String s, Object... aobject) {
        return new StringFormattedMessage(s, aobject);
    }
}
