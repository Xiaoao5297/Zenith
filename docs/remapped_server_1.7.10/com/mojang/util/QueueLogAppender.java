package com.mojang.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.RegexReplacement;

@Plugin(name = "Queue", category = "Core", elementType = "appender", printObject = true)
public class QueueLogAppender extends AbstractAppender {

    private static final int MAX_CAPACITY = 250;
    private static final Map<String, BlockingQueue<String>> QUEUES = new HashMap();
    private static final ReadWriteLock QUEUE_LOCK = new ReentrantReadWriteLock();
    private final BlockingQueue<String> queue;

    public QueueLogAppender(String s, Filter filter, Layout<? extends Serializable> layout, boolean flag, BlockingQueue<String> blockingqueue) {
        super(s, filter, layout, flag);
        this.queue = blockingqueue;
    }

    @Override
    public void append(LogEvent logevent) {
        if (this.queue.size() >= 250) {
            this.queue.clear();
        }

        this.queue.add(this.getLayout().toSerializable(logevent).toString());
    }

    @PluginFactory
    public static QueueLogAppender createAppender(@PluginAttribute("name") String s, @PluginAttribute("ignoreExceptions") String s1, @PluginElement("Layout") Layout<? extends Serializable> layout, @PluginElement("Filters") Filter filter) {
        boolean flag = Boolean.parseBoolean(s1);

        if (s == null) {
            QueueLogAppender.LOGGER.error("No name provided for QueueLogAppender");
            return null;
        } else {
            QueueLogAppender.QUEUE_LOCK.writeLock().lock();
            Object object = (BlockingQueue) QueueLogAppender.QUEUES.get(s);

            if (object == null) {
                object = new LinkedBlockingQueue();
                QueueLogAppender.QUEUES.put(s, object);
            }

            QueueLogAppender.QUEUE_LOCK.writeLock().unlock();
            if (layout == null) {
                layout = PatternLayout.createLayout((String) null, (Configuration) null, (RegexReplacement) null, (String) null, (String) null);
            }

            return new QueueLogAppender(s, filter, (Layout) layout, flag, (BlockingQueue) object);
        }
    }

    public static String getNextLogEvent(String s) {
        QueueLogAppender.QUEUE_LOCK.readLock().lock();
        BlockingQueue blockingqueue = (BlockingQueue) QueueLogAppender.QUEUES.get(s);

        QueueLogAppender.QUEUE_LOCK.readLock().unlock();
        if (blockingqueue != null) {
            try {
                return (String) blockingqueue.take();
            } catch (InterruptedException interruptedexception) {
                ;
            }
        }

        return null;
    }
}
