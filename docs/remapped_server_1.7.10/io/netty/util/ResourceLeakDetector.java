package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ResourceLeakDetector<T> {

    private static boolean disabled;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ResourceLeakDetector.class);
    private static final int DEFAULT_SAMPLING_INTERVAL = 113;
    private final ResourceLeakDetector<T>.DefaultResourceLeak head;
    private final ResourceLeakDetector<T>.DefaultResourceLeak tail;
    private final ReferenceQueue<Object> refQueue;
    private final ConcurrentMap<Exception, Boolean> reportedLeaks;
    private final String resourceType;
    private final int samplingInterval;
    private final long maxActive;
    private long active;
    private final AtomicBoolean loggedTooManyActive;
    private long leakCheckCnt;

    public static void setEnabled(boolean flag) {
        ResourceLeakDetector.disabled = !flag;
    }

    public static boolean isEnabled() {
        return !ResourceLeakDetector.disabled;
    }

    public ResourceLeakDetector(Class<?> oclass) {
        this(oclass.getSimpleName());
    }

    public ResourceLeakDetector(String s) {
        this(s, 113, Long.MAX_VALUE);
    }

    public ResourceLeakDetector(Class<?> oclass, int i, long j) {
        this(oclass.getSimpleName(), i, j);
    }

    public ResourceLeakDetector(String s, int i, long j) {
        this.head = new ResourceLeakDetector.DefaultResourceLeak((Object) null);
        this.tail = new ResourceLeakDetector.DefaultResourceLeak((Object) null);
        this.refQueue = new ReferenceQueue();
        this.reportedLeaks = PlatformDependent.newConcurrentHashMap();
        this.loggedTooManyActive = new AtomicBoolean();
        if (s == null) {
            throw new NullPointerException("resourceType");
        } else if (i <= 0) {
            throw new IllegalArgumentException("samplingInterval: " + i + " (expected: 1+)");
        } else if (j <= 0L) {
            throw new IllegalArgumentException("maxActive: " + j + " (expected: 1+)");
        } else {
            this.resourceType = s;
            this.samplingInterval = i;
            this.maxActive = j;
            this.head.next = this.tail;
            this.tail.prev = this.head;
        }
    }

    public ResourceLeak open(T t0) {
        if (!ResourceLeakDetector.disabled && this.leakCheckCnt++ % (long) this.samplingInterval == 0L) {
            this.reportLeak();
            return new ResourceLeakDetector.DefaultResourceLeak(t0);
        } else {
            return null;
        }
    }

    private void reportLeak() {
        ResourceLeakDetector.DefaultResourceLeak resourceleakdetector_defaultresourceleak;

        if (ResourceLeakDetector.logger.isWarnEnabled()) {
            if (this.active * (long) this.samplingInterval > this.maxActive && this.loggedTooManyActive.compareAndSet(false, true)) {
                ResourceLeakDetector.logger.warn("LEAK: You are creating too many " + this.resourceType + " instances.  " + this.resourceType + " is a shared resource that must be reused across the JVM," + "so that only a few instances are created.");
            }

            while (true) {
                resourceleakdetector_defaultresourceleak = (ResourceLeakDetector.DefaultResourceLeak) this.refQueue.poll();
                if (resourceleakdetector_defaultresourceleak == null) {
                    return;
                }

                resourceleakdetector_defaultresourceleak.clear();
                if (resourceleakdetector_defaultresourceleak.close() && this.reportedLeaks.putIfAbsent(resourceleakdetector_defaultresourceleak.exception, Boolean.TRUE) == null) {
                    ResourceLeakDetector.logger.warn("LEAK: " + this.resourceType + " was GC'd before being released correctly.  " + "The following stack trace shows where the leaked object was created, " + "rather than where you failed to release it.", (Throwable) resourceleakdetector_defaultresourceleak.exception);
                }
            }
        } else {
            while (true) {
                resourceleakdetector_defaultresourceleak = (ResourceLeakDetector.DefaultResourceLeak) this.refQueue.poll();
                if (resourceleakdetector_defaultresourceleak == null) {
                    return;
                }

                resourceleakdetector_defaultresourceleak.close();
            }
        }
    }

    static {
        boolean flag = SystemPropertyUtil.getBoolean("io.netty.noResourceLeakDetection", false);

        ResourceLeakDetector.logger.debug("-Dio.netty.noResourceLeakDetection: {}", (Object) flag);
        ResourceLeakDetector.disabled = flag;
    }

    private final class DefaultResourceLeak extends PhantomReference<Object> implements ResourceLeak {

        private final ResourceLeakException exception;
        private final AtomicBoolean freed;
        private ResourceLeakDetector<T>.DefaultResourceLeak prev;
        private ResourceLeakDetector<T>.DefaultResourceLeak next;

        public DefaultResourceLeak(Object object) {
            super(object, object != null ? ResourceLeakDetector.this.refQueue : null);
            if (object != null) {
                this.exception = new ResourceLeakException(object.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(object)));
                synchronized (ResourceLeakDetector.this.head) {
                    this.prev = ResourceLeakDetector.this.head;
                    this.next = ResourceLeakDetector.this.head.next;
                    ResourceLeakDetector.this.head.next.prev = this;
                    ResourceLeakDetector.this.head.next = this;
                    ResourceLeakDetector.this.active++;
                }

                this.freed = new AtomicBoolean();
            } else {
                this.exception = null;
                this.freed = new AtomicBoolean(true);
            }

        }

        @Override
        public boolean close() {
            if (this.freed.compareAndSet(false, true)) {
                synchronized (ResourceLeakDetector.this.head) {
                    ResourceLeakDetector.this.active--;
                    this.prev.next = this.next;
                    this.next.prev = this.prev;
                    this.prev = null;
                    this.next = null;
                    return true;
                }
            } else {
                return false;
            }
        }
    }
}
