package io.netty.util.internal.logging;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

public class Slf4JLoggerFactory extends InternalLoggerFactory {

    public Slf4JLoggerFactory() {}

    Slf4JLoggerFactory(boolean flag) {
        assert flag;

        final StringBuffer stringbuffer = new StringBuffer();
        PrintStream printstream = System.err;

        try {
            System.setErr(new PrintStream(new OutputStream() {
                public void write(int i) {
                    stringbuffer.append((char) i);
                }
            }, true, "US-ASCII"));
        } catch (UnsupportedEncodingException unsupportedencodingexception) {
            throw new Error(unsupportedencodingexception);
        }

        try {
            if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
                throw new NoClassDefFoundError(stringbuffer.toString());
            }

            printstream.print(stringbuffer.toString());
            printstream.flush();
        } finally {
            System.setErr(printstream);
        }

    }

    @Override
    public InternalLogger newInstance(String s) {
        return new Slf4JLogger(LoggerFactory.getLogger(s));
    }
}
