package io.netty.util.internal.logging;

import org.apache.log4j.Logger;

public class Log4JLoggerFactory extends InternalLoggerFactory {

    public Log4JLoggerFactory() {}

    @Override
    public InternalLogger newInstance(String s) {
        return new Log4JLogger(Logger.getLogger(s));
    }
}
