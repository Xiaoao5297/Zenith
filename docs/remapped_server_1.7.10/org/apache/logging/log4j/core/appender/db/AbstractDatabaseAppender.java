package org.apache.logging.log4j.core.appender.db;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;

public abstract class AbstractDatabaseAppender<T extends AbstractDatabaseManager> extends AbstractAppender {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock;
    private final Lock writeLock;
    private T manager;

    protected AbstractDatabaseAppender(String s, Filter filter, boolean flag, T t0) {
        super(s, filter, (Layout) null, flag);
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.manager = t0;
    }

    @Override
    public final Layout<LogEvent> getLayout() {
        return null;
    }

    public final T getManager() {
        return this.manager;
    }

    @Override
    public final void start() {
        if (this.getManager() == null) {
            AbstractDatabaseAppender.LOGGER.error("No AbstractDatabaseManager set for the appender named [{}].", this.getName());
        }

        super.start();
        if (this.getManager() != null) {
            this.getManager().connect();
        }

    }

    @Override
    public final void stop() {
        super.stop();
        if (this.getManager() != null) {
            this.getManager().release();
        }

    }

    @Override
    public final void append(LogEvent logevent) {
        this.readLock.lock();

        try {
            this.getManager().write(logevent);
        } catch (LoggingException loggingexception) {
            AbstractDatabaseAppender.LOGGER.error("Unable to write to database [{}] for appender [{}].", this.getManager().getName(), this.getName(), loggingexception);
            throw loggingexception;
        } catch (Exception exception) {
            AbstractDatabaseAppender.LOGGER.error("Unable to write to database [{}] for appender [{}].", this.getManager().getName(), this.getName(), exception);
            throw new AppenderLoggingException("Unable to write to database in appender: " + exception.getMessage(), exception);
        } finally {
            this.readLock.unlock();
        }

    }

    protected final void replaceManager(T t0) {
        this.writeLock.lock();

        try {
            AbstractDatabaseManager abstractdatabasemanager = this.getManager();

            if (!t0.isConnected()) {
                t0.connect();
            }

            this.manager = t0;
            abstractdatabasemanager.release();
        } finally {
            this.writeLock.unlock();
        }

    }
}
