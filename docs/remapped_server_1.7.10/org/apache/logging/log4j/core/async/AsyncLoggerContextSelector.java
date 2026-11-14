package org.apache.logging.log4j.core.async;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.selector.ContextSelector;

public class AsyncLoggerContextSelector implements ContextSelector {

    private static final AsyncLoggerContext CONTEXT = new AsyncLoggerContext("AsyncLoggerContext");

    public AsyncLoggerContextSelector() {}

    @Override
    public LoggerContext getContext(String s, ClassLoader classloader, boolean flag) {
        return AsyncLoggerContextSelector.CONTEXT;
    }

    @Override
    public List<LoggerContext> getLoggerContexts() {
        ArrayList arraylist = new ArrayList();

        arraylist.add(AsyncLoggerContextSelector.CONTEXT);
        return Collections.unmodifiableList(arraylist);
    }

    @Override
    public LoggerContext getContext(String s, ClassLoader classloader, boolean flag, URI uri) {
        return AsyncLoggerContextSelector.CONTEXT;
    }

    @Override
    public void removeContext(LoggerContext loggercontext) {}
}
