package org.apache.logging.log4j.simple;

import java.net.URI;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;

public class SimpleLoggerContextFactory implements LoggerContextFactory {

    private static LoggerContext context = new SimpleLoggerContext();

    public SimpleLoggerContextFactory() {}

    @Override
    public LoggerContext getContext(String s, ClassLoader classloader, boolean flag) {
        return SimpleLoggerContextFactory.context;
    }

    @Override
    public LoggerContext getContext(String s, ClassLoader classloader, boolean flag, URI uri) {
        return SimpleLoggerContextFactory.context;
    }

    @Override
    public void removeContext(LoggerContext loggercontext) {}
}
