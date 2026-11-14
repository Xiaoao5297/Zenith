package io.netty.util.internal.logging;

import java.io.ObjectStreamException;
import java.io.Serializable;

public abstract class AbstractInternalLogger implements InternalLogger, Serializable {

    private static final long serialVersionUID = -6382972526573193470L;
    private final String name;

    protected AbstractInternalLogger(String s) {
        if (s == null) {
            throw new NullPointerException("name");
        } else {
            this.name = s;
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean isEnabled(InternalLogLevel internalloglevel) {
        switch (internalloglevel) {
            case TRACE:
                return this.isTraceEnabled();
            case DEBUG:
                return this.isDebugEnabled();
            case INFO:
                return this.isInfoEnabled();
            case WARN:
                return this.isWarnEnabled();
            case ERROR:
                return this.isErrorEnabled();
            default:
                throw new Error();
        }
    }

    @Override
    public void log(InternalLogLevel internalloglevel, String s, Throwable throwable) {
        switch (internalloglevel) {
            case TRACE:
                this.trace(s, throwable);
                break;
            case DEBUG:
                this.debug(s, throwable);
                break;
            case INFO:
                this.info(s, throwable);
                break;
            case WARN:
                this.warn(s, throwable);
                break;
            case ERROR:
                this.error(s, throwable);
                break;
            default:
                throw new Error();
        }

    }

    @Override
    public void log(InternalLogLevel internalloglevel, String s) {
        switch (internalloglevel) {
            case TRACE:
                this.trace(s);
                break;
            case DEBUG:
                this.debug(s);
                break;
            case INFO:
                this.info(s);
                break;
            case WARN:
                this.warn(s);
                break;
            case ERROR:
                this.error(s);
                break;
            default:
                throw new Error();
        }

    }

    @Override
    public void log(InternalLogLevel internalloglevel, String s, Object object) {
        switch (internalloglevel) {
            case TRACE:
                this.trace(s, object);
                break;
            case DEBUG:
                this.debug(s, object);
                break;
            case INFO:
                this.info(s, object);
                break;
            case WARN:
                this.warn(s, object);
                break;
            case ERROR:
                this.error(s, object);
                break;
            default:
                throw new Error();
        }

    }

    @Override
    public void log(InternalLogLevel internalloglevel, String s, Object object, Object object1) {
        switch (internalloglevel) {
            case TRACE:
                this.trace(s, object, object1);
                break;
            case DEBUG:
                this.debug(s, object, object1);
                break;
            case INFO:
                this.info(s, object, object1);
                break;
            case WARN:
                this.warn(s, object, object1);
                break;
            case ERROR:
                this.error(s, object, object1);
                break;
            default:
                throw new Error();
        }

    }

    @Override
    public void log(InternalLogLevel internalloglevel, String s, Object... aobject) {
        switch (internalloglevel) {
            case TRACE:
                this.trace(s, aobject);
                break;
            case DEBUG:
                this.debug(s, aobject);
                break;
            case INFO:
                this.info(s, aobject);
                break;
            case WARN:
                this.warn(s, aobject);
                break;
            case ERROR:
                this.error(s, aobject);
                break;
            default:
                throw new Error();
        }

    }

    protected Object readResolve() throws ObjectStreamException {
        return InternalLoggerFactory.getInstance(this.name());
    }

    public String toString() {
        return this.getClass().getSimpleName() + '(' + this.name() + ')';
    }
}
