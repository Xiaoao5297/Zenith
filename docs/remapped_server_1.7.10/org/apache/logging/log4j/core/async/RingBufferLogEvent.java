package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.EventFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;

public class RingBufferLogEvent implements LogEvent {

    private static final long serialVersionUID = 8462119088943934758L;
    public static final RingBufferLogEvent.Factory FACTORY = new RingBufferLogEvent.Factory();
    private AsyncLogger asyncLogger;
    private String loggerName;
    private Marker marker;
    private String fqcn;
    private Level level;
    private Message message;
    private Throwable thrown;
    private Map<String, String> contextMap;
    private ThreadContext.ContextStack contextStack;
    private String threadName;
    private StackTraceElement location;
    private long currentTimeMillis;
    private boolean endOfBatch;
    private boolean includeLocation;

    public RingBufferLogEvent() {}

    public void setValues(AsyncLogger asynclogger, String s, Marker marker, String s1, Level level, Message message, Throwable throwable, Map<String, String> map, ThreadContext.ContextStack threadcontext_contextstack, String s2, StackTraceElement stacktraceelement, long i) {
        this.asyncLogger = asynclogger;
        this.loggerName = s;
        this.marker = marker;
        this.fqcn = s1;
        this.level = level;
        this.message = message;
        this.thrown = throwable;
        this.contextMap = map;
        this.contextStack = threadcontext_contextstack;
        this.threadName = s2;
        this.location = stacktraceelement;
        this.currentTimeMillis = i;
    }

    public void execute(boolean flag) {
        this.endOfBatch = flag;
        this.asyncLogger.actualAsyncLog(this);
    }

    @Override
    public boolean isEndOfBatch() {
        return this.endOfBatch;
    }

    @Override
    public void setEndOfBatch(boolean flag) {
        this.endOfBatch = flag;
    }

    @Override
    public boolean isIncludeLocation() {
        return this.includeLocation;
    }

    @Override
    public void setIncludeLocation(boolean flag) {
        this.includeLocation = flag;
    }

    @Override
    public String getLoggerName() {
        return this.loggerName;
    }

    @Override
    public Marker getMarker() {
        return this.marker;
    }

    @Override
    public String getFQCN() {
        return this.fqcn;
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    @Override
    public Message getMessage() {
        if (this.message == null) {
            this.message = new SimpleMessage("");
        }

        return this.message;
    }

    @Override
    public Throwable getThrown() {
        return this.thrown;
    }

    @Override
    public Map<String, String> getContextMap() {
        return this.contextMap;
    }

    @Override
    public ThreadContext.ContextStack getContextStack() {
        return this.contextStack;
    }

    @Override
    public String getThreadName() {
        return this.threadName;
    }

    @Override
    public StackTraceElement getSource() {
        return this.location;
    }

    @Override
    public long getMillis() {
        return this.currentTimeMillis;
    }

    public void mergePropertiesIntoContextMap(Map<Property, Boolean> map, StrSubstitutor strsubstitutor) {
        if (map != null) {
            HashMap hashmap = this.contextMap == null ? new HashMap() : new HashMap(this.contextMap);
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                Property property = (Property) entry.getKey();

                if (!hashmap.containsKey(property.getName())) {
                    String s = (Boolean) entry.getValue() ? strsubstitutor.replace(property.getValue()) : property.getValue();

                    hashmap.put(property.getName(), s);
                }
            }

            this.contextMap = hashmap;
        }
    }

    public void clear() {
        this.setValues((AsyncLogger) null, (String) null, (Marker) null, (String) null, (Level) null, (Message) null, (Throwable) null, (Map) null, (ThreadContext.ContextStack) null, (String) null, (StackTraceElement) null, 0L);
    }

    private static class Factory implements EventFactory<RingBufferLogEvent> {

        private Factory() {}

        public RingBufferLogEvent newInstance() {
            return new RingBufferLogEvent();
        }
    }
}
