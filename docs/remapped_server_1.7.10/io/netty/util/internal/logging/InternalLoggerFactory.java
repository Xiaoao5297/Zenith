package io.netty.util.internal.logging;

public abstract class InternalLoggerFactory {

    private static volatile InternalLoggerFactory defaultFactory;

    public InternalLoggerFactory() {}

    public static InternalLoggerFactory getDefaultFactory() {
        return InternalLoggerFactory.defaultFactory;
    }

    public static void setDefaultFactory(InternalLoggerFactory internalloggerfactory) {
        if (internalloggerfactory == null) {
            throw new NullPointerException("defaultFactory");
        } else {
            InternalLoggerFactory.defaultFactory = internalloggerfactory;
        }
    }

    public static InternalLogger getInstance(Class<?> oclass) {
        return getInstance(oclass.getName());
    }

    public static InternalLogger getInstance(String s) {
        return getDefaultFactory().newInstance(s);
    }

    protected abstract InternalLogger newInstance(String s);

    static {
        String s = InternalLoggerFactory.class.getName();

        Object object;

        try {
            object = new Slf4JLoggerFactory(true);
            ((InternalLoggerFactory) object).newInstance(s).debug("Using SLF4J as the default logging framework");
            InternalLoggerFactory.defaultFactory = (InternalLoggerFactory) object;
        } catch (Throwable throwable) {
            try {
                object = new Log4JLoggerFactory();
                ((InternalLoggerFactory) object).newInstance(s).debug("Using Log4J as the default logging framework");
            } catch (Throwable throwable1) {
                object = new JdkLoggerFactory();
                ((InternalLoggerFactory) object).newInstance(s).debug("Using java.util.logging as the default logging framework");
            }
        }

        InternalLoggerFactory.defaultFactory = (InternalLoggerFactory) object;
    }
}
