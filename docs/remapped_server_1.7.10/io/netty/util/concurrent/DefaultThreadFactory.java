package io.netty.util.concurrent;

import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolId = new AtomicInteger();
    private final AtomicInteger nextId;
    private final String prefix;
    private final boolean daemon;
    private final int priority;

    public DefaultThreadFactory(Class<?> oclass) {
        this(oclass, false, 5);
    }

    public DefaultThreadFactory(String s) {
        this(s, false, 5);
    }

    public DefaultThreadFactory(Class<?> oclass, boolean flag) {
        this(oclass, flag, 5);
    }

    public DefaultThreadFactory(String s, boolean flag) {
        this(s, flag, 5);
    }

    public DefaultThreadFactory(Class<?> oclass, int i) {
        this(oclass, false, i);
    }

    public DefaultThreadFactory(String s, int i) {
        this(s, false, i);
    }

    public DefaultThreadFactory(Class<?> oclass, boolean flag, int i) {
        this(toPoolName(oclass), flag, i);
    }

    private static String toPoolName(Class<?> oclass) {
        if (oclass == null) {
            throw new NullPointerException("poolType");
        } else {
            Package package = oclass.getPackage();
            String s;

            if (package != null) {
                s = oclass.getName().substring(package.getName().length() + 1);
            } else {
                s = oclass.getName();
            }

            switch (s.length()) {
                case 0:
                    return "unknown";
                case 1:
                    return s.toLowerCase(Locale.US);
                default:
                    return Character.isUpperCase(s.charAt(0)) && Character.isLowerCase(s.charAt(1)) ? Character.toLowerCase(s.charAt(0)) + s.substring(1) : s;
            }
        }
    }

    public DefaultThreadFactory(String s, boolean flag, int i) {
        this.nextId = new AtomicInteger();
        if (s == null) {
            throw new NullPointerException("poolName");
        } else if (i >= 1 && i <= 10) {
            this.prefix = s + '-' + DefaultThreadFactory.poolId.incrementAndGet() + '-';
            this.daemon = flag;
            this.priority = i;
        } else {
            throw new IllegalArgumentException("priority: " + i + " (expected: Thread.MIN_PRIORITY <= priority <= Thread.MAX_PRIORITY)");
        }
    }

    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, this.prefix + this.nextId.incrementAndGet());

        try {
            if (thread.isDaemon()) {
                if (!this.daemon) {
                    thread.setDaemon(false);
                }
            } else if (this.daemon) {
                thread.setDaemon(true);
            }

            if (thread.getPriority() != this.priority) {
                thread.setPriority(this.priority);
            }
        } catch (Exception exception) {
            ;
        }

        return thread;
    }
}
