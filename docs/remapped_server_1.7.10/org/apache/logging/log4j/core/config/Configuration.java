package org.apache.logging.log4j.core.config;

import java.util.Map;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.Filterable;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.net.Advertiser;

public interface Configuration extends Filterable {

    String CONTEXT_PROPERTIES = "ContextProperties";

    String getName();

    LoggerConfig getLoggerConfig(String s);

    Map<String, Appender> getAppenders();

    Map<String, LoggerConfig> getLoggers();

    void addLoggerAppender(Logger logger, Appender appender);

    void addLoggerFilter(Logger logger, Filter filter);

    void setLoggerAdditive(Logger logger, boolean flag);

    Map<String, String> getProperties();

    void start();

    void stop();

    void addListener(ConfigurationListener configurationlistener);

    void removeListener(ConfigurationListener configurationlistener);

    StrSubstitutor getStrSubstitutor();

    void createConfiguration(Node node, LogEvent logevent);

    <T> T getComponent(String s);

    void addComponent(String s, Object object);

    void setConfigurationMonitor(ConfigurationMonitor configurationmonitor);

    ConfigurationMonitor getConfigurationMonitor();

    void setAdvertiser(Advertiser advertiser);

    Advertiser getAdvertiser();

    boolean isShutdownHookEnabled();
}
