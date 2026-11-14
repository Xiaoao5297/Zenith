package io.netty.util.internal.logging;

public interface InternalLogger {

    String name();

    boolean isTraceEnabled();

    void trace(String s);

    void trace(String s, Object object);

    void trace(String s, Object object, Object object1);

    void trace(String s, Object... aobject);

    void trace(String s, Throwable throwable);

    boolean isDebugEnabled();

    void debug(String s);

    void debug(String s, Object object);

    void debug(String s, Object object, Object object1);

    void debug(String s, Object... aobject);

    void debug(String s, Throwable throwable);

    boolean isInfoEnabled();

    void info(String s);

    void info(String s, Object object);

    void info(String s, Object object, Object object1);

    void info(String s, Object... aobject);

    void info(String s, Throwable throwable);

    boolean isWarnEnabled();

    void warn(String s);

    void warn(String s, Object object);

    void warn(String s, Object... aobject);

    void warn(String s, Object object, Object object1);

    void warn(String s, Throwable throwable);

    boolean isErrorEnabled();

    void error(String s);

    void error(String s, Object object);

    void error(String s, Object object, Object object1);

    void error(String s, Object... aobject);

    void error(String s, Throwable throwable);

    boolean isEnabled(InternalLogLevel internalloglevel);

    void log(InternalLogLevel internalloglevel, String s);

    void log(InternalLogLevel internalloglevel, String s, Object object);

    void log(InternalLogLevel internalloglevel, String s, Object object, Object object1);

    void log(InternalLogLevel internalloglevel, String s, Object... aobject);

    void log(InternalLogLevel internalloglevel, String s, Throwable throwable);
}
