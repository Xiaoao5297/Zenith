package org.apache.commons.lang3.concurrent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class MultiBackgroundInitializer extends BackgroundInitializer<MultiBackgroundInitializer.MultiBackgroundInitializerResults> {

    private final Map<String, BackgroundInitializer<?>> childInitializers = new HashMap();

    public MultiBackgroundInitializer() {}

    public MultiBackgroundInitializer(ExecutorService executorservice) {
        super(executorservice);
    }

    public void addInitializer(String s, BackgroundInitializer<?> backgroundinitializer) {
        if (s == null) {
            throw new IllegalArgumentException("Name of child initializer must not be null!");
        } else if (backgroundinitializer == null) {
            throw new IllegalArgumentException("Child initializer must not be null!");
        } else {
            synchronized (this) {
                if (this.isStarted()) {
                    throw new IllegalStateException("addInitializer() must not be called after start()!");
                } else {
                    this.childInitializers.put(s, backgroundinitializer);
                }
            }
        }
    }

    @Override
    protected int getTaskCount() {
        int i = 1;

        BackgroundInitializer backgroundinitializer;

        for (Iterator iterator = this.childInitializers.values().iterator(); iterator.hasNext(); i += backgroundinitializer.getTaskCount()) {
            backgroundinitializer = (BackgroundInitializer) iterator.next();
        }

        return i;
    }

    @Override
    protected MultiBackgroundInitializer.MultiBackgroundInitializerResults initialize() throws Exception {
        HashMap hashmap;

        synchronized (this) {
            hashmap = new HashMap(this.childInitializers);
        }

        ExecutorService executorservice = this.getActiveExecutor();

        BackgroundInitializer backgroundinitializer;

        for (Iterator iterator = hashmap.values().iterator(); iterator.hasNext(); backgroundinitializer.start()) {
            backgroundinitializer = (BackgroundInitializer) iterator.next();
            if (backgroundinitializer.getExternalExecutor() == null) {
                backgroundinitializer.setExternalExecutor(executorservice);
            }
        }

        HashMap hashmap1 = new HashMap();
        HashMap hashmap2 = new HashMap();
        Iterator iterator1 = hashmap.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry entry = (Entry) iterator1.next();

            try {
                hashmap1.put(entry.getKey(), ((BackgroundInitializer) entry.getValue()).get());
            } catch (ConcurrentException concurrentexception) {
                hashmap2.put(entry.getKey(), concurrentexception);
            }
        }

        return new MultiBackgroundInitializer.MultiBackgroundInitializerResults(hashmap, hashmap1, hashmap2);
    }

    public static class MultiBackgroundInitializerResults {

        private final Map<String, BackgroundInitializer<?>> initializers;
        private final Map<String, Object> resultObjects;
        private final Map<String, ConcurrentException> exceptions;

        private MultiBackgroundInitializerResults(Map<String, BackgroundInitializer<?>> map, Map<String, Object> map1, Map<String, ConcurrentException> map2) {
            this.initializers = map;
            this.resultObjects = map1;
            this.exceptions = map2;
        }

        public BackgroundInitializer<?> getInitializer(String s) {
            return this.checkName(s);
        }

        public Object getResultObject(String s) {
            this.checkName(s);
            return this.resultObjects.get(s);
        }

        public boolean isException(String s) {
            this.checkName(s);
            return this.exceptions.containsKey(s);
        }

        public ConcurrentException getException(String s) {
            this.checkName(s);
            return (ConcurrentException) this.exceptions.get(s);
        }

        public Set<String> initializerNames() {
            return Collections.unmodifiableSet(this.initializers.keySet());
        }

        public boolean isSuccessful() {
            return this.exceptions.isEmpty();
        }

        private BackgroundInitializer<?> checkName(String s) {
            BackgroundInitializer backgroundinitializer = (BackgroundInitializer) this.initializers.get(s);

            if (backgroundinitializer == null) {
                throw new NoSuchElementException("No child initializer with name " + s);
            } else {
                return backgroundinitializer;
            }
        }
    }
}
