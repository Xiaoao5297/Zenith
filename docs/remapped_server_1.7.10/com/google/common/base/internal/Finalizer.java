package com.google.common.base.internal;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Finalizer implements Runnable {

    private static final Logger logger = Logger.getLogger(Finalizer.class.getName());
    private static final String FINALIZABLE_REFERENCE = "com.google.common.base.FinalizableReference";
    private final WeakReference<Class<?>> finalizableReferenceClassReference;
    private final PhantomReference<Object> frqReference;
    private final ReferenceQueue<Object> queue;
    private static final Field inheritableThreadLocals = getInheritableThreadLocalsField();

    public static void startFinalizer(Class<?> oclass, ReferenceQueue<Object> referencequeue, PhantomReference<Object> phantomreference) {
        if (!oclass.getName().equals("com.google.common.base.FinalizableReference")) {
            throw new IllegalArgumentException("Expected com.google.common.base.FinalizableReference.");
        } else {
            Finalizer finalizer = new Finalizer(oclass, referencequeue, phantomreference);
            Thread thread = new Thread(finalizer);

            thread.setName(Finalizer.class.getName());
            thread.setDaemon(true);

            try {
                if (Finalizer.inheritableThreadLocals != null) {
                    Finalizer.inheritableThreadLocals.set(thread, (Object) null);
                }
            } catch (Throwable throwable) {
                Finalizer.logger.log(Level.INFO, "Failed to clear thread local values inherited by reference finalizer thread.", throwable);
            }

            thread.start();
        }
    }

    private Finalizer(Class<?> oclass, ReferenceQueue<Object> referencequeue, PhantomReference<Object> phantomreference) {
        this.queue = referencequeue;
        this.finalizableReferenceClassReference = new WeakReference(oclass);
        this.frqReference = phantomreference;
    }

    public void run() {
        while (true) {
            try {
                if (!this.cleanUp(this.queue.remove())) {
                    return;
                }
            } catch (InterruptedException interruptedexception) {
                ;
            }
        }
    }

    private boolean cleanUp(Reference<?> reference) {
        Method method = this.getFinalizeReferentMethod();

        if (method == null) {
            return false;
        } else {
            do {
                reference.clear();
                if (reference == this.frqReference) {
                    return false;
                }

                try {
                    method.invoke(reference);
                } catch (Throwable throwable) {
                    Finalizer.logger.log(Level.SEVERE, "Error cleaning up after reference.", throwable);
                }
            } while ((reference = this.queue.poll()) != null);

            return true;
        }
    }

    private Method getFinalizeReferentMethod() {
        Class oclass = (Class) this.finalizableReferenceClassReference.get();

        if (oclass == null) {
            return null;
        } else {
            try {
                return oclass.getMethod("finalizeReferent");
            } catch (NoSuchMethodException nosuchmethodexception) {
                throw new AssertionError(nosuchmethodexception);
            }
        }
    }

    public static Field getInheritableThreadLocalsField() {
        try {
            Field field = Thread.class.getDeclaredField("inheritableThreadLocals");

            field.setAccessible(true);
            return field;
        } catch (Throwable throwable) {
            Finalizer.logger.log(Level.INFO, "Couldn't access Thread.inheritableThreadLocals. Reference finalizer threads will inherit thread local values.");
            return null;
        }
    }
}
