package org.apache.logging.log4j.core.net;

import org.apache.logging.log4j.Level;

public enum Severity {

    EMERG(0), ALERT(1), CRITICAL(2), ERROR(3), WARNING(4), NOTICE(5), INFO(6), DEBUG(7);

    private final int code;

    private Severity(int i) {
        this.code = i;
    }

    public int getCode() {
        return this.code;
    }

    public boolean isEqual(String s) {
        return this.name().equalsIgnoreCase(s);
    }

    public static Severity getSeverity(Level level) {
        switch (level) {
            case ALL:
                return Severity.DEBUG;
            case TRACE:
                return Severity.DEBUG;
            case DEBUG:
                return Severity.DEBUG;
            case INFO:
                return Severity.INFO;
            case WARN:
                return Severity.WARNING;
            case ERROR:
                return Severity.ERROR;
            case FATAL:
                return Severity.ALERT;
            case OFF:
                return Severity.EMERG;
            default:
                return Severity.DEBUG;
        }
    }
}
