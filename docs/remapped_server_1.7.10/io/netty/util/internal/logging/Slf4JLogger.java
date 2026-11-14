package io.netty.util.internal.logging;

import org.slf4j.Logger;

class Slf4JLogger extends AbstractInternalLogger {

    private static final long serialVersionUID = 108038972685130825L;
    private final transient Logger logger;

    Slf4JLogger(Logger logger) {
        super(logger.getName());
        this.logger = logger;
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
        this.logger.trace(s, object);
    }

    @Override
    public void trace(String s, Object object, Object object1) {
        this.logger.trace(s, object, object1);
    }

    @Override
    public void trace(String s, Object[] aobject) {
        this.logger.trace(s, aobject);
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
        this.logger.debug(s, object);
    }

    @Override
    public void debug(String s, Object object, Object object1) {
        this.logger.debug(s, object, object1);
    }

    @Override
    public void debug(String s, Object[] aobject) {
        this.logger.debug(s, aobject);
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
        this.logger.info(s, object);
    }

    @Override
    public void info(String s, Object object, Object object1) {
        this.logger.info(s, object, object1);
    }

    @Override
    public void info(String s, Object[] aobject) {
        this.logger.info(s, aobject);
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
        this.logger.warn(s, object);
    }

    @Override
    public void warn(String s, Object[] aobject) {
        this.logger.warn(s, aobject);
    }

    @Override
    public void warn(String s, Object object, Object object1) {
        this.logger.warn(s, object, object1);
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
        this.logger.error(s, object);
    }

    @Override
    public void error(String s, Object object, Object object1) {
        this.logger.error(s, object, object1);
    }

    @Override
    public void error(String s, Object[] aobject) {
        this.logger.error(s, aobject);
    }

    @Override
    public void error(String s, Throwable throwable) {
        this.logger.error(s, throwable);
    }
}
