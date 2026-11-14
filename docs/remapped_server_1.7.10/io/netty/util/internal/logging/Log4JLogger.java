package io.netty.util.internal.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class Log4JLogger extends AbstractInternalLogger {

    private static final long serialVersionUID = 2851357342488183058L;
    final transient Logger logger;
    static final String FQCN = Log4JLogger.class.getName();
    final boolean traceCapable;

    Log4JLogger(Logger logger) {
        super(logger.getName());
        this.logger = logger;
        this.traceCapable = this.isTraceCapable();
    }

    private boolean isTraceCapable() {
        try {
            this.logger.isTraceEnabled();
            return true;
        } catch (NoSuchMethodError nosuchmethoderror) {
            return false;
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return this.traceCapable ? this.logger.isTraceEnabled() : this.logger.isDebugEnabled();
    }

    @Override
    public void trace(String s) {
        this.logger.log(Log4JLogger.FQCN, this.traceCapable ? Level.TRACE : Level.DEBUG, s, (Throwable) null);
    }

    @Override
    public void trace(String s, Object object) {
        if (this.isTraceEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.logger.log(Log4JLogger.FQCN, this.traceCapable ? Level.TRACE : Level.DEBUG, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void trace(String s, Object object, Object object1) {
        if (this.isTraceEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.logger.log(Log4JLogger.FQCN, this.traceCapable ? Level.TRACE : Level.DEBUG, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void trace(String s, Object... aobject) {
        if (this.isTraceEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.logger.log(Log4JLogger.FQCN, this.traceCapable ? Level.TRACE : Level.DEBUG, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void trace(String s, Throwable throwable) {
        this.logger.log(Log4JLogger.FQCN, this.traceCapable ? Level.TRACE : Level.DEBUG, s, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override
    public void debug(String s) {
        this.logger.log(Log4JLogger.FQCN, Level.DEBUG, s, (Throwable) null);
    }

    @Override
    public void debug(String s, Object object) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.logger.log(Log4JLogger.FQCN, Level.DEBUG, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void debug(String s, Object object, Object object1) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.logger.log(Log4JLogger.FQCN, Level.DEBUG, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void debug(String s, Object... aobject) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.logger.log(Log4JLogger.FQCN, Level.DEBUG, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void debug(String s, Throwable throwable) {
        this.logger.log(Log4JLogger.FQCN, Level.DEBUG, s, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override
    public void info(String s) {
        this.logger.log(Log4JLogger.FQCN, Level.INFO, s, (Throwable) null);
    }

    @Override
    public void info(String s, Object object) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.logger.log(Log4JLogger.FQCN, Level.INFO, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void info(String s, Object object, Object object1) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.logger.log(Log4JLogger.FQCN, Level.INFO, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void info(String s, Object... aobject) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.logger.log(Log4JLogger.FQCN, Level.INFO, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void info(String s, Throwable throwable) {
        this.logger.log(Log4JLogger.FQCN, Level.INFO, s, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isEnabledFor(Level.WARN);
    }

    @Override
    public void warn(String s) {
        this.logger.log(Log4JLogger.FQCN, Level.WARN, s, (Throwable) null);
    }

    @Override
    public void warn(String s, Object object) {
        if (this.logger.isEnabledFor(Level.WARN)) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.logger.log(Log4JLogger.FQCN, Level.WARN, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void warn(String s, Object object, Object object1) {
        if (this.logger.isEnabledFor(Level.WARN)) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.logger.log(Log4JLogger.FQCN, Level.WARN, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void warn(String s, Object... aobject) {
        if (this.logger.isEnabledFor(Level.WARN)) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.logger.log(Log4JLogger.FQCN, Level.WARN, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void warn(String s, Throwable throwable) {
        this.logger.log(Log4JLogger.FQCN, Level.WARN, s, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isEnabledFor(Level.ERROR);
    }

    @Override
    public void error(String s) {
        this.logger.log(Log4JLogger.FQCN, Level.ERROR, s, (Throwable) null);
    }

    @Override
    public void error(String s, Object object) {
        if (this.logger.isEnabledFor(Level.ERROR)) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.logger.log(Log4JLogger.FQCN, Level.ERROR, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void error(String s, Object object, Object object1) {
        if (this.logger.isEnabledFor(Level.ERROR)) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.logger.log(Log4JLogger.FQCN, Level.ERROR, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void error(String s, Object... aobject) {
        if (this.logger.isEnabledFor(Level.ERROR)) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.logger.log(Log4JLogger.FQCN, Level.ERROR, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void error(String s, Throwable throwable) {
        this.logger.log(Log4JLogger.FQCN, Level.ERROR, s, throwable);
    }
}
