package org.apache.logging.log4j.core.helpers;

public class SystemClock implements Clock {

    public SystemClock() {}

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
