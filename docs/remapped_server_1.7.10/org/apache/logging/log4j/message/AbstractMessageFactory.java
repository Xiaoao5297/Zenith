package org.apache.logging.log4j.message;

public abstract class AbstractMessageFactory implements MessageFactory {

    public AbstractMessageFactory() {}

    @Override
    public Message newMessage(Object object) {
        return new ObjectMessage(object);
    }

    @Override
    public Message newMessage(String s) {
        return new SimpleMessage(s);
    }

    @Override
    public abstract Message newMessage(String s, Object... aobject);
}
