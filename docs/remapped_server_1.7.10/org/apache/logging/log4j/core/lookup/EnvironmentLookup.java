package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "env", category = "Lookup")
public class EnvironmentLookup implements StrLookup {

    public EnvironmentLookup() {}

    @Override
    public String lookup(String s) {
        return System.getenv(s);
    }

    @Override
    public String lookup(LogEvent logevent, String s) {
        return System.getenv(s);
    }
}
