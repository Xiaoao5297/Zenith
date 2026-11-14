package io.netty.util.internal.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

class JdkLogger extends AbstractInternalLogger {

    private static final long serialVersionUID = -1767272577989225979L;
    final transient Logger logger;
    static final String SELF = JdkLogger.class.getName();
    static final String SUPER = AbstractInternalLogger.class.getName();

    JdkLogger(Logger logger) {
        super(logger.getName());
        this.logger = logger;
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isLoggable(Level.FINEST);
    }

    @Override
    public void trace(String s) {
        if (this.logger.isLoggable(Level.FINEST)) {
            this.log(JdkLogger.SELF, Level.FINEST, s, (Throwable) null);
        }

    }

    @Override
    public void trace(String s, Object object) {
        if (this.logger.isLoggable(Level.FINEST)) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.log(JdkLogger.SELF, Level.FINEST, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void trace(String s, Object object, Object object1) {
        if (this.logger.isLoggable(Level.FINEST)) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.log(JdkLogger.SELF, Level.FINEST, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void trace(String s, Object... aobject) {
        if (this.logger.isLoggable(Level.FINEST)) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.log(JdkLogger.SELF, Level.FINEST, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void trace(String s, Throwable throwable) {
        if (this.logger.isLoggable(Level.FINEST)) {
            this.log(JdkLogger.SELF, Level.FINEST, s, throwable);
        }

    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isLoggable(Level.FINE);
    }

    @Override
    public void debug(String s) {
        if (this.logger.isLoggable(Level.FINE)) {
            this.log(JdkLogger.SELF, Level.FINE, s, (Throwable) null);
        }

    }

    @Override
    public void debug(String s, Object object) {
        if (this.logger.isLoggable(Level.FINE)) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.log(JdkLogger.SELF, Level.FINE, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void debug(String s, Object object, Object object1) {
        if (this.logger.isLoggable(Level.FINE)) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.log(JdkLogger.SELF, Level.FINE, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void debug(String s, Object... aobject) {
        if (this.logger.isLoggable(Level.FINE)) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.log(JdkLogger.SELF, Level.FINE, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void debug(String s, Throwable throwable) {
        if (this.logger.isLoggable(Level.FINE)) {
            this.log(JdkLogger.SELF, Level.FINE, s, throwable);
        }

    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isLoggable(Level.INFO);
    }

    @Override
    public void info(String s) {
        if (this.logger.isLoggable(Level.INFO)) {
            this.log(JdkLogger.SELF, Level.INFO, s, (Throwable) null);
        }

    }

    @Override
    public void info(String s, Object object) {
        if (this.logger.isLoggable(Level.INFO)) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.log(JdkLogger.SELF, Level.INFO, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void info(String s, Object object, Object object1) {
        if (this.logger.isLoggable(Level.INFO)) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.log(JdkLogger.SELF, Level.INFO, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void info(String s, Object... aobject) {
        if (this.logger.isLoggable(Level.INFO)) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.log(JdkLogger.SELF, Level.INFO, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void info(String s, Throwable throwable) {
        if (this.logger.isLoggable(Level.INFO)) {
            this.log(JdkLogger.SELF, Level.INFO, s, throwable);
        }

    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isLoggable(Level.WARNING);
    }

    @Override
    public void warn(String s) {
        if (this.logger.isLoggable(Level.WARNING)) {
            this.log(JdkLogger.SELF, Level.WARNING, s, (Throwable) null);
        }

    }

    @Override
    public void warn(String s, Object object) {
        if (this.logger.isLoggable(Level.WARNING)) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.log(JdkLogger.SELF, Level.WARNING, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void warn(String s, Object object, Object object1) {
        if (this.logger.isLoggable(Level.WARNING)) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.log(JdkLogger.SELF, Level.WARNING, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void warn(String s, Object... aobject) {
        if (this.logger.isLoggable(Level.WARNING)) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.log(JdkLogger.SELF, Level.WARNING, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void warn(String s, Throwable throwable) {
        if (this.logger.isLoggable(Level.WARNING)) {
            this.log(JdkLogger.SELF, Level.WARNING, s, throwable);
        }

    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isLoggable(Level.SEVERE);
    }

    @Override
    public void error(String s) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            this.log(JdkLogger.SELF, Level.SEVERE, s, (Throwable) null);
        }

    }

    @Override
    public void error(String s, Object object) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object);

            this.log(JdkLogger.SELF, Level.SEVERE, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void error(String s, Object object, Object object1) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            FormattingTuple formattingtuple = MessageFormatter.format(s, object, object1);

            this.log(JdkLogger.SELF, Level.SEVERE, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void error(String s, Object... aobject) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            FormattingTuple formattingtuple = MessageFormatter.arrayFormat(s, aobject);

            this.log(JdkLogger.SELF, Level.SEVERE, formattingtuple.getMessage(), formattingtuple.getThrowable());
        }

    }

    @Override
    public void error(String s, Throwable throwable) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            this.log(JdkLogger.SELF, Level.SEVERE, s, throwable);
        }

    }

    private void log(String s, Level level, String s1, Throwable throwable) {
        LogRecord logrecord = new LogRecord(level, s1);

        logrecord.setLoggerName(this.name());
        logrecord.setThrown(throwable);
        fillCallerData(s, logrecord);
        this.logger.log(logrecord);
    }

    private static void fillCallerData(String s, LogRecord logrecord) {
        StackTraceElement[] astacktraceelement = (new Throwable()).getStackTrace();
        int i = -1;

        int j;

        for (j = 0; j < astacktraceelement.length; ++j) {
            String s1 = astacktraceelement[j].getClassName();

            if (s1.equals(s) || s1.equals(JdkLogger.SUPER)) {
                i = j;
                break;
            }
        }

        j = -1;

        for (int k = i + 1; k < astacktraceelement.length; ++k) {
            String s2 = astacktraceelement[k].getClassName();

            if (!s2.equals(s) && !s2.equals(JdkLogger.SUPER)) {
                j = k;
                break;
            }
        }

        if (j != -1) {
            StackTraceElement stacktraceelement = astacktraceelement[j];

            logrecord.setSourceClassName(stacktraceelement.getClassName());
            logrecord.setSourceMethodName(stacktraceelement.getMethodName());
        }

    }
}
