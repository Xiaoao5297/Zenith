package org.apache.logging.log4j.core.layout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "SerializedLayout", category = "Core", elementType = "layout", printObject = true)
public final class SerializedLayout extends AbstractLayout<LogEvent> {

    private static byte[] header;

    private SerializedLayout() {}

    @Override
    public byte[] toByteArray(LogEvent logevent) {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

        try {
            SerializedLayout.PrivateObjectOutputStream serializedlayout_privateobjectoutputstream = new SerializedLayout.PrivateObjectOutputStream(bytearrayoutputstream);

            try {
                serializedlayout_privateobjectoutputstream.writeObject(logevent);
                serializedlayout_privateobjectoutputstream.reset();
            } finally {
                serializedlayout_privateobjectoutputstream.close();
            }
        } catch (IOException ioexception) {
            SerializedLayout.LOGGER.error("Serialization of LogEvent failed.", (Throwable) ioexception);
        }

        return bytearrayoutputstream.toByteArray();
    }

    @Override
    public LogEvent toSerializable(LogEvent logevent) {
        return logevent;
    }

    @PluginFactory
    public static SerializedLayout createLayout() {
        return new SerializedLayout();
    }

    @Override
    public byte[] getHeader() {
        return SerializedLayout.header;
    }

    @Override
    public Map<String, String> getContentFormat() {
        return new HashMap();
    }

    @Override
    public String getContentType() {
        return "application/octet-stream";
    }

    static {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

        try {
            ObjectOutputStream objectoutputstream = new ObjectOutputStream(bytearrayoutputstream);

            objectoutputstream.close();
            SerializedLayout.header = bytearrayoutputstream.toByteArray();
        } catch (Exception exception) {
            SerializedLayout.LOGGER.error("Unable to generate Object stream header", (Throwable) exception);
        }

    }

    private class PrivateObjectOutputStream extends ObjectOutputStream {

        public PrivateObjectOutputStream(OutputStream outputstream) throws IOException {
            super(outputstream);
        }

        protected void writeStreamHeader() {}
    }
}
