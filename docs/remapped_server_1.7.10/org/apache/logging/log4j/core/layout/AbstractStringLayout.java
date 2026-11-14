package org.apache.logging.log4j.core.layout;

import java.nio.charset.Charset;
import org.apache.logging.log4j.core.LogEvent;

public abstract class AbstractStringLayout extends AbstractLayout<String> {

    private final Charset charset;

    protected AbstractStringLayout(Charset charset) {
        this.charset = charset;
    }

    @Override
    public byte[] toByteArray(LogEvent logevent) {
        return ((String) this.toSerializable(logevent)).getBytes(this.charset);
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    protected Charset getCharset() {
        return this.charset;
    }
}
