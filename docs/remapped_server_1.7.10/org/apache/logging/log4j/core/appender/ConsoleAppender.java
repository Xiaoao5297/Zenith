package org.apache.logging.log4j.core.appender;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Booleans;
import org.apache.logging.log4j.core.helpers.Loader;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.RegexReplacement;
import org.apache.logging.log4j.util.PropertiesUtil;

@Plugin(name = "Console", category = "Core", elementType = "appender", printObject = true)
public final class ConsoleAppender extends AbstractOutputStreamAppender {

    private static final String JANSI_CLASS = "org.fusesource.jansi.WindowsAnsiOutputStream";
    private static ConsoleAppender.ConsoleManagerFactory factory = new ConsoleAppender.ConsoleManagerFactory();

    private ConsoleAppender(String s, Layout<? extends Serializable> layout, Filter filter, OutputStreamManager outputstreammanager, boolean flag) {
        super(s, layout, filter, flag, true, outputstreammanager);
    }

    @PluginFactory
    public static ConsoleAppender createAppender(@PluginElement("Layout") Layout<? extends Serializable> layout, @PluginElement("Filters") Filter filter, @PluginAttribute("target") String s, @PluginAttribute("name") String s1, @PluginAttribute("follow") String s2, @PluginAttribute("ignoreExceptions") String s3) {
        if (s1 == null) {
            ConsoleAppender.LOGGER.error("No name provided for ConsoleAppender");
            return null;
        } else {
            if (layout == null) {
                layout = PatternLayout.createLayout((String) null, (Configuration) null, (RegexReplacement) null, (String) null, (String) null);
            }

            boolean flag = Boolean.parseBoolean(s2);
            boolean flag1 = Booleans.parseBoolean(s3, true);
            ConsoleAppender.Target consoleappender_target = s == null ? ConsoleAppender.Target.SYSTEM_OUT : ConsoleAppender.Target.valueOf(s);

            return new ConsoleAppender(s1, (Layout) layout, filter, getManager(flag, consoleappender_target, (Layout) layout), flag1);
        }
    }

    private static OutputStreamManager getManager(boolean flag, ConsoleAppender.Target consoleappender_target, Layout<? extends Serializable> layout) {
        String s = consoleappender_target.name();
        OutputStream outputstream = getOutputStream(flag, consoleappender_target);

        return OutputStreamManager.getManager(consoleappender_target.name() + "." + flag, (Object) (new ConsoleAppender.FactoryData(outputstream, s, layout)), (ManagerFactory) ConsoleAppender.factory);
    }

    private static OutputStream getOutputStream(boolean flag, ConsoleAppender.Target consoleappender_target) {
        String s = Charset.defaultCharset().name();
        PrintStream printstream = null;

        try {
            printstream = consoleappender_target == ConsoleAppender.Target.SYSTEM_OUT ? (flag ? new PrintStream(new ConsoleAppender.SystemOutStream(), true, s) : System.out) : (flag ? new PrintStream(new ConsoleAppender.SystemErrStream(), true, s) : System.err);
        } catch (UnsupportedEncodingException unsupportedencodingexception) {
            throw new IllegalStateException("Unsupported default encoding " + s, unsupportedencodingexception);
        }

        PropertiesUtil propertiesutil = PropertiesUtil.getProperties();

        if (propertiesutil.getStringProperty("os.name").startsWith("Windows") && !propertiesutil.getBooleanProperty("log4j.skipJansi")) {
            try {
                ClassLoader classloader = Loader.getClassLoader();
                Class oclass = classloader.loadClass("org.fusesource.jansi.WindowsAnsiOutputStream");
                Constructor constructor = oclass.getConstructor(OutputStream.class);

                return (OutputStream) constructor.newInstance(printstream);
            } catch (ClassNotFoundException classnotfoundexception) {
                ConsoleAppender.LOGGER.debug("Jansi is not installed, cannot find {}", "org.fusesource.jansi.WindowsAnsiOutputStream");
            } catch (NoSuchMethodException nosuchmethodexception) {
                ConsoleAppender.LOGGER.warn("{} is missing the proper constructor", "org.fusesource.jansi.WindowsAnsiOutputStream");
            } catch (Exception exception) {
                ConsoleAppender.LOGGER.warn("Unable to instantiate {}", "org.fusesource.jansi.WindowsAnsiOutputStream");
            }

            return printstream;
        } else {
            return printstream;
        }
    }

    private static class ConsoleManagerFactory implements ManagerFactory<OutputStreamManager, ConsoleAppender.FactoryData> {

        private ConsoleManagerFactory() {}

        public OutputStreamManager createManager(String s, ConsoleAppender.FactoryData consoleappender_factorydata) {
            return new OutputStreamManager(consoleappender_factorydata.os, consoleappender_factorydata.type, consoleappender_factorydata.layout);
        }
    }

    private static class FactoryData {

        private final OutputStream os;
        private final String type;
        private final Layout<? extends Serializable> layout;

        public FactoryData(OutputStream outputstream, String s, Layout<? extends Serializable> layout) {
            this.os = outputstream;
            this.type = s;
            this.layout = layout;
        }
    }

    private static class SystemOutStream extends OutputStream {

        public SystemOutStream() {}

        public void close() {}

        public void flush() {
            System.out.flush();
        }

        public void write(byte[] abyte) throws IOException {
            System.out.write(abyte);
        }

        public void write(byte[] abyte, int i, int j) throws IOException {
            System.out.write(abyte, i, j);
        }

        public void write(int i) throws IOException {
            System.out.write(i);
        }
    }

    private static class SystemErrStream extends OutputStream {

        public SystemErrStream() {}

        public void close() {}

        public void flush() {
            System.err.flush();
        }

        public void write(byte[] abyte) throws IOException {
            System.err.write(abyte);
        }

        public void write(byte[] abyte, int i, int j) throws IOException {
            System.err.write(abyte, i, j);
        }

        public void write(int i) {
            System.err.write(i);
        }
    }

    public static enum Target {

        SYSTEM_OUT, SYSTEM_ERR;

        private Target() {}
    }
}
