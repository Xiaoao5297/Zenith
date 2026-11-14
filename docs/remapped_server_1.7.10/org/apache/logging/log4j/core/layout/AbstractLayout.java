package org.apache.logging.log4j.core.layout;

import java.io.Serializable;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.status.StatusLogger;

public abstract class AbstractLayout<T extends Serializable> implements Layout<T> {

    protected static final Logger LOGGER = StatusLogger.getLogger();
    protected byte[] header;
    protected byte[] footer;

    public AbstractLayout() {}

    @Override
    public byte[] getHeader() {
        return this.header;
    }

    public void setHeader(byte[] abyte) {
        this.header = abyte;
    }

    @Override
    public byte[] getFooter() {
        return this.footer;
    }

    public void setFooter(byte[] abyte) {
        this.footer = abyte;
    }
}
