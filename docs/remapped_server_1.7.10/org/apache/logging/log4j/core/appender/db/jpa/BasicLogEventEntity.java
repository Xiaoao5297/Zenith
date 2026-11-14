package org.apache.logging.log4j.core.appender.db.jpa;

import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.db.jpa.converter.ContextMapAttributeConverter;
import org.apache.logging.log4j.core.appender.db.jpa.converter.ContextStackAttributeConverter;
import org.apache.logging.log4j.core.appender.db.jpa.converter.MarkerAttributeConverter;
import org.apache.logging.log4j.core.appender.db.jpa.converter.MessageAttributeConverter;
import org.apache.logging.log4j.core.appender.db.jpa.converter.StackTraceElementAttributeConverter;
import org.apache.logging.log4j.core.appender.db.jpa.converter.ThrowableAttributeConverter;
import org.apache.logging.log4j.message.Message;

@MappedSuperclass
public abstract class BasicLogEventEntity extends AbstractLogEventWrapperEntity {

    private static final long serialVersionUID = 1L;

    public BasicLogEventEntity() {}

    public BasicLogEventEntity(LogEvent logevent) {
        super(logevent);
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Override
    public Level getLevel() {
        return this.getWrappedEvent().getLevel();
    }

    @Basic
    @Override
    public String getLoggerName() {
        return this.getWrappedEvent().getLoggerName();
    }

    @Convert(converter = StackTraceElementAttributeConverter.class)
    @Override
    public StackTraceElement getSource() {
        return this.getWrappedEvent().getSource();
    }

    @Convert(converter = MessageAttributeConverter.class)
    @Override
    public Message getMessage() {
        return this.getWrappedEvent().getMessage();
    }

    @Convert(converter = MarkerAttributeConverter.class)
    @Override
    public Marker getMarker() {
        return this.getWrappedEvent().getMarker();
    }

    @Basic
    @Override
    public String getThreadName() {
        return this.getWrappedEvent().getThreadName();
    }

    @Basic
    @Override
    public long getMillis() {
        return this.getWrappedEvent().getMillis();
    }

    @Convert(converter = ThrowableAttributeConverter.class)
    @Override
    public Throwable getThrown() {
        return this.getWrappedEvent().getThrown();
    }

    @Convert(converter = ContextMapAttributeConverter.class)
    @Override
    public Map<String, String> getContextMap() {
        return this.getWrappedEvent().getContextMap();
    }

    @Convert(converter = ContextStackAttributeConverter.class)
    @Override
    public ThreadContext.ContextStack getContextStack() {
        return this.getWrappedEvent().getContextStack();
    }

    @Basic
    @Override
    public String getFQCN() {
        return this.getWrappedEvent().getFQCN();
    }
}
