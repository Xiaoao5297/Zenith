package io.netty.util.internal.logging;

import java.util.logging.Logger;

public class JdkLoggerFactory extends InternalLoggerFactory {

    public JdkLoggerFactory() {}

    @Override
    public InternalLogger newInstance(String s) {
        return new JdkLogger(Logger.getLogger(s));
    }
}
