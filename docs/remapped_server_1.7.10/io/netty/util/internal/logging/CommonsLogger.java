package io.netty.util.internal.logging;

import org.apache.commons.logging.Log;

class CommonsLogger extends AbstractInternalLogger {

    private static final long serialVersionUID = 8647838678388394885L;
    private final transient Log logger;

    CommonsLogger(Log log, String s) {
        super(s);
        if (log == null) {
            throw new NullPointerException("logger");
        } else {
            this.logger = log;
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    @Override
    public void trace(String s) {
        this.logger.trace(s);
    }

    @Override
    public void trace(String s, Object object) {
        if (this.logger.isTraceEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.logger.trace(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void trace(String s, Object object, Object object1) {
        if (this.logger.isTraceEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.logger.trace(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void trace(String s, Object... aobject) {
        if (this.logger.isTraceEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.logger.trace(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void trace(String s, Throwable throwable) {
        this.logger.trace(s, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override
    public void debug(String s) {
        this.logger.debug(s);
    }

    @Override
    public void debug(String s, Object object) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.logger.debug(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void debug(String s, Object object, Object object1) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.logger.debug(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void debug(String s, Object... aobject) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.logger.debug(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void debug(String s, Throwable throwable) {
        this.logger.debug(s, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override
    public void info(String s) {
        this.logger.info(s);
    }

    @Override
    public void info(String s, Object object) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.logger.info(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void info(String s, Object object, Object object1) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.logger.info(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void info(String s, Object... aobject) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.logger.info(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void info(String s, Throwable throwable) {
        this.logger.info(s, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    @Override
    public void warn(String s) {
        this.logger.warn(s);
    }

    @Override
    public void warn(String s, Object object) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.logger.warn(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void warn(String s, Object object, Object object1) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.logger.warn(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void warn(String s, Object... aobject) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.logger.warn(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void warn(String s, Throwable throwable) {
        this.logger.warn(s, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    @Override
    public void error(String s) {
        this.logger.error(s);
    }

    @Override
    public void error(String s, Object object) {
        if (this.logger.isErrorEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.logger.error(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void error(String s, Object object, Object object1) {
        if (this.logger.isErrorEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.logger.error(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void error(String s, Object... aobject) {
        if (this.logger.isErrorEnabled()) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.logger.error(formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void error(String s, Throwable throwable) {
        this.logger.error(s, throwable);
    }
}
