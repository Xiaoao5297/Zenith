package org.apache.logging.log4j.core.jmx;

import javax.management.ObjectName;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.helpers.Assert;

public class AppenderAdmin implements AppenderAdminMBean {

    private final String contextName;
    private final Appender appender;
    private final ObjectName objectName;

    public AppenderAdmin(String s, Appender appender) {
        this.contextName = (String) Assert.isNotNull(s, "contextName");
        this.appender = (Appender) Assert.isNotNull(appender, "appender");

        try {
            String s1 = Server.escape(this.contextName);
            String s2 = Server.escape(appender.getName());
            String s3 = String.format("org.apache.logging.log4j2:type=LoggerContext,ctx=%s,sub=Appender,name=%s", s1, s2);

            this.objectName = new ObjectName(s3);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    public ObjectName getObjectName() {
        return this.objectName;
    }

    @Override
    public String getName() {
        return this.appender.getName();
    }

    @Override
    public String getLayout() {
        return String.valueOf(this.appender.getLayout());
    }

    @Override
    public boolean isExceptionSuppressed() {
        return this.appender.ignoreExceptions();
    }

    @Override
    public String getErrorHandler() {
        return String.valueOf(this.appender.getHandler());
    }
}
