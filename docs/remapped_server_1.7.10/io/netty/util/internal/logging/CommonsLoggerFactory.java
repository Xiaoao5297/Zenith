package io.netty.util.internal.logging;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.LogFactory;

public class CommonsLoggerFactory extends InternalLoggerFactory {

    Map<String, InternalLogger> loggerMap = new HashMap();

    public CommonsLoggerFactory() {}

    @Override
    public InternalLogger newInstance(String s) {
        return new CommonsLogger(LogFactory.getLog(s), s);
    }
}
