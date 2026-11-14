package org.apache.logging.log4j.core.impl;

import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.message.Message;

public interface LogEventFactory {

    LogEvent createEvent(String s, Marker marker, String s1, Level level, Message message, List<Property> list, Throwable throwable);
}
