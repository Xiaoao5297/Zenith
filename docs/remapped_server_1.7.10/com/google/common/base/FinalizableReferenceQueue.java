package com.google.common.base;

import com.google.common.annotations.VisibleForTesting;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FinalizableReferenceQueue implements Closeable {

    private static final Logger logger = Logger.getLogger(FinalizableReferenceQueue.class.getName());
    private static final String FINALIZER_CLASS_NAME = "com.google.common.base.internal.Finalizer";
    private static final Method startFinalizer;
    final ReferenceQueue<Object> queue = new ReferenceQueue();
    final PhantomReference<Object> frqRef;
    final boolean threadStarted;

    public FinalizableReferenceQueue() {
        this.frqRef = new PhantomReference(this, this.queue);
        boolean flag = false;

        try {
            FinalizableReferenceQueue.startFinalizer.invoke((Object) null, FinalizableReference.class, this.queue, this.frqRef);
            flag = true;
        } catch (IllegalAccessException illegalaccessexception) {
            throw new AssertionError(illegalaccessexception);
        } catch (Throwable throwable) {
            FinalizableReferenceQueue.logger.log(Level.INFO, "Failed to start reference finalizer thread. Reference cleanup will only occur when new references are created.", throwable);
        }

        this.threadStarted = flag;
    }

    public void close() {
        this.frqRef.enqueue();
        this.cleanUp();
    }

    void cleanUp() {
        if (!this.threadStarted) {
            Reference reference;

            while ((reference = this.queue.poll()) != null) {
                reference.clear();

                try {
                    ((FinalizableReference) reference).finalizeReferent();
                } catch (Throwable throwable) {
                    FinalizableReferenceQueue.logger.log(Level.SEVERE, "Error cleaning up after reference.", throwable);
                }
            }

        }
    }

    private static Class<?> loadFinalizer(FinalizableReferenceQueue.FinalizerLoader... afinalizablereferencequeue_finalizerloader) {
        FinalizableReferenceQueue.FinalizerLoader[] afinalizablereferencequeue_finalizerloader1 = afinalizablereferencequeue_finalizerloader;
        int i = afinalizablereferencequeue_finalizerloader.length;

        for (int j = 0; j < i; ++j) {
            FinalizableReferenceQueue.FinalizerLoader finalizablereferencequeue_finalizerloader = afinalizablereferencequeue_finalizerloader1[j];
            Class oclass = finalizablereferencequeue_finalizerloader.loadFinalizer();

            if (oclass != null) {
                return oclass;
            }
        }

        throw new AssertionError();
    }

    static Method getStartFinalizer(Class<?> oclass) {
        try {
            return oclass.getMethod("startFinalizer", Class.class, ReferenceQueue.class, PhantomReference.class);
        } catch (NoSuchMethodException nosuchmethodexception) {
            throw new AssertionError(nosuchmethodexception);
        }
    }

    static {
        Class oclass = loadFinalizer(new FinalizableReferenceQueue.SystemLoader(), new FinalizableReferenceQueue.DecoupledLoader(), new FinalizableReferenceQueue.DirectLoader());

        startFinalizer = getStartFinalizer(oclass);
    }

    static class DirectLoader implements FinalizableReferenceQueue.FinalizerLoader {

        DirectLoader() {}

        @Override
        public Class<?> loadFinalizer() {
            try {
                return Class.forName("com.google.common.base.internal.Finalizer");
            } catch (ClassNotFoundException classnotfoundexception) {
                throw new AssertionError(classnotfoundexception);
            }
        }
    }

    static class DecoupledLoader implements FinalizableReferenceQueue.FinalizerLoader {

        private static final String LOADING_ERROR = "Could not load Finalizer in its own class loader.Loading Finalizer in the current class loader instead. As a result, you will not be ableto garbage collect this class loader. To support reclaiming this class loader, eitherresolve the underlying issue, or move Google Collections to your system class path.";

        DecoupledLoader() {}

        @Override
        public Class<?> loadFinalizer() {
            try {
                URLClassLoader urlclassloader = this.newLoader(this.getBaseUrl());

                return urlclassloader.loadClass("com.google.common.base.internal.Finalizer");
            } catch (Exception exception) {
                FinalizableReferenceQueue.logger.log(Level.WARNING, "Could not load Finalizer in its own class loader.Loading Finalizer in the current class loader instead. As a result, you will not be ableto garbage collect this class loader. To support reclaiming this class loader, eitherresolve the underlying issue, or move Google Collections to your system class path.", exception);
                return null;
            }
        }

        URL getBaseUrl() throws IOException {
            String s = "com.google.common.base.internal.Finalizer".replace('.', '/') + ".class";
            URL url = this.getClass().getClassLoader().getResource(s);

            if (url == null) {
                throw new FileNotFoundException(s);
            } else {
                String s1 = url.toString();

                if (!s1.endsWith(s)) {
                    throw new IOException("Unsupported path style: " + s1);
                } else {
                    s1 = s1.substring(0, s1.length() - s.length());
                    return new URL(url, s1);
                }
            }
        }

        URLClassLoader newLoader(URL url) {
            return new URLClassLoader(new URL[]{url}, (ClassLoader) null);
        }
    }

    static class SystemLoader implements FinalizableReferenceQueue.FinalizerLoader {

        @VisibleForTesting
        static boolean disabled;

        SystemLoader() {}

        @Override
        public Class<?> loadFinalizer() {
            if (FinalizableReferenceQueue.SystemLoader.disabled) {
                return null;
            } else {
                ClassLoader classloader;

                try {
                    classloader = ClassLoader.getSystemClassLoader();
                } catch (SecurityException securityexception) {
                    FinalizableReferenceQueue.logger.info("Not allowed to access system class loader.");
                    return null;
                }

                if (classloader != null) {
                    try {
                        return classloader.loadClass("com.google.common.base.internal.Finalizer");
                    } catch (ClassNotFoundException classnotfoundexception) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }
    }

    interface FinalizerLoader {

        Class<?> loadFinalizer();
    }
}
