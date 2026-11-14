package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;

public class AbstractLoggerWrapper extends AbstractLogger {

    protected final AbstractLogger logger;

    public AbstractLoggerWrapper(AbstractLogger abstractlogger, String s, MessageFactory messagefactory) {
        super(s, messagefactory);
        this.logger = abstractlogger;
    }

    @Override
    public void log(Marker marker, String s, Level level, Message message, Throwable throwable) {
        this.logger.log(marker, s, level, message, throwable);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String s) {
        return this.logger.isEnabled(level, marker, s);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String s, Throwable throwable) {
        return this.logger.isEnabled(level, marker, s, throwable);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String s, Object... aobject) {
        return this.logger.isEnabled(level, marker, s, aobject);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, Object object, Throwable throwable) {
        return this.logger.isEnabled(level, marker, object, throwable);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, Message message, Throwable throwable) {
        return this.logger.isEnabled(level, marker, message, throwable);
    }
}
