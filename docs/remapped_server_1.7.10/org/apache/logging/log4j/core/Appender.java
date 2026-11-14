package org.apache.logging.log4j.core;

import java.io.Serializable;

public interface Appender extends LifeCycle {

    void append(LogEvent logevent);

    String getName();

    Layout<? extends Serializable> getLayout();

    boolean ignoreExceptions();

    ErrorHandler getHandler();

    void setHandler(ErrorHandler errorhandler);
}
