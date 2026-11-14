package com.google.common.collect;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReferenceArray;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

class ComputingConcurrentHashMap<K, V> extends MapMakerInternalMap<K, V> {

    final Function<? super K, ? extends V> computingFunction;
    private static final long serialVersionUID = 4L;

    ComputingConcurrentHashMap(MapMaker mapmaker, Function<? super K, ? extends V> function) {
        super(mapmaker);
        this.computingFunction = (Function) Preconditions.checkNotNull(function);
    }

    @Override
    MapMakerInternalMap.Segment<K, V> createSegment(int i, int j) {
        return new ComputingConcurrentHashMap.ComputingSegment<>(this, i, j);
    }

    @Override
    ComputingConcurrentHashMap.ComputingSegment<K, V> segmentFor(int i) {
        return (ComputingConcurrentHashMap.ComputingSegment) super.segmentFor(i);
    }

    V getOrCompute(K k0) throws ExecutionException {
        int i = this.hash(Preconditions.checkNotNull(k0));

        return this.segmentFor(i).getOrCompute(k0, i, this.computingFunction);
    }

    @Override
    Object writeReplace() {
        return new ComputingConcurrentHashMap.ComputingSerializationProxy<>(this.keyStrength, this.valueStrength, this.keyEquivalence, this.valueEquivalence, this.expireAfterWriteNanos, this.expireAfterAccessNanos, this.maximumSize, this.concurrencyLevel, this.removalListener, this, this.computingFunction);
    }

    static final class ComputingSerializationProxy<K, V> extends MapMakerInternalMap.AbstractSerializationProxy<K, V> {

        final Function<? super K, ? extends V> computingFunction;
        private static final long serialVersionUID = 4L;

        ComputingSerializationProxy(MapMakerInternalMap.Strength mapmakerinternalmap_strength, MapMakerInternalMap.Strength mapmakerinternalmap_strength1, Equivalence<Object> equivalence, Equivalence<Object> equivalence1, long i, long j, int k, int l, MapMaker.RemovalListener<? super K, ? super V> mapmaker_removallistener, ConcurrentMap<K, V> concurrentmap, Function<? super K, ? extends V> function) {
            super(mapmakerinternalmap_strength, mapmakerinternalmap_strength1, equivalence, equivalence1, i, j, k, l, mapmaker_removallistener, concurrentmap);
            this.computingFunction = function;
        }

        private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
            objectoutputstream.defaultWriteObject();
            this.writeMapTo(objectoutputstream);
        }

        private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
            objectinputstream.defaultReadObject();
            MapMaker mapmaker = this.readMapMaker(objectinputstream);

            this.delegate = mapmaker.makeComputingMap(this.computingFunction);
            this.readEntries(objectinputstream);
        }

        Object readResolve() {
            return this.delegate;
        }
    }

    private static final class ComputingValueReference<K, V> implements MapMakerInternalMap.ValueReference<K, V> {

        final Function<? super K, ? extends V> computingFunction;
        @GuardedBy("ComputingValueReference.this")
        volatile MapMakerInternalMap.ValueReference<K, V> computedReference = MapMakerInternalMap.unset();

        public ComputingValueReference(Function<? super K, ? extends V> function) {
            this.computingFunction = function;
        }

        @Override
        public V get() {
            return null;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getEntry() {
            return null;
        }

        @Override
        public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> referencequeue, @Nullable V v0, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            return this;
        }

        @Override
        public boolean isComputingReference() {
            return true;
        }

        @Override
        public V waitForValue() throws ExecutionException {
            if (this.computedReference == MapMakerInternalMap.UNSET) {
                boolean flag = false;

                try {
                    synchronized (this) {
                        while (this.computedReference == MapMakerInternalMap.UNSET) {
                            try {
                                this.wait();
                            } catch (InterruptedException interruptedexception) {
                                flag = true;
                            }
                        }
                    }
                } finally {
                    if (flag) {
                        Thread.currentThread().interrupt();
                    }

                }
            }

            return this.computedReference.waitForValue();
        }

        @Override
        public void clear(MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {
            this.setValueReference(mapmakerinternalmap_valuereference);
        }

        V compute(K k0, int i) throws ExecutionException {
            Object object;

            try {
                object = this.computingFunction.apply(k0);
            } catch (Throwable throwable) {
                this.setValueReference(new ComputingConcurrentHashMap.ComputationExceptionReference<>(throwable));
                throw new ExecutionException(throwable);
            }

            this.setValueReference(new ComputingConcurrentHashMap.ComputedReference<>(object));
            return object;
        }

        void setValueReference(MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {
            synchronized (this) {
                if (this.computedReference == MapMakerInternalMap.UNSET) {
                    this.computedReference = mapmakerinternalmap_valuereference;
                    this.notifyAll();
                }

            }
        }
    }

    private static final class ComputedReference<K, V> implements MapMakerInternalMap.ValueReference<K, V> {

        final V value;

        ComputedReference(@Nullable V v0) {
            this.value = v0;
        }

        @Override
        public V get() {
            return this.value;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getEntry() {
            return null;
        }

        @Override
        public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> referencequeue, V v0, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            return this;
        }

        @Override
        public boolean isComputingReference() {
            return false;
        }

        @Override
        public V waitForValue() {
            return this.get();
        }

        @Override
        public void clear(MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {}
    }

    private static final class ComputationExceptionReference<K, V> implements MapMakerInternalMap.ValueReference<K, V> {

        final Throwable t;

        ComputationExceptionReference(Throwable throwable) {
            this.t = throwable;
        }

        @Override
        public V get() {
            return null;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getEntry() {
            return null;
        }

        @Override
        public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> referencequeue, V v0, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            return this;
        }

        @Override
        public boolean isComputingReference() {
            return false;
        }

        @Override
        public V waitForValue() throws ExecutionException {
            throw new ExecutionException(this.t);
        }

        @Override
        public void clear(MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {}
    }

    static final class ComputingSegment<K, V> extends MapMakerInternalMap.Segment<K, V> {

        ComputingSegment(MapMakerInternalMap<K, V> mapmakerinternalmap, int i, int j) {
            super(mapmakerinternalmap, i, j);
        }

        V getOrCompute(K k0, int i, Function<? super K, ? extends V> function) throws ExecutionException {
            while (true) {
                try {
                    MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = this.getEntry(k0, i);
                    Object object;
                    Object object1;

                    if (mapmakerinternalmap_referenceentry != null) {
                        object = this.getLiveValue(mapmakerinternalmap_referenceentry);
                        if (object != null) {
                            this.recordRead(mapmakerinternalmap_referenceentry);
                            object1 = object;
                            return object1;
                        }
                    }

                    if (mapmakerinternalmap_referenceentry == null || !mapmakerinternalmap_referenceentry.getValueReference().isComputingReference()) {
                        boolean flag = true;
                        ComputingConcurrentHashMap.ComputingValueReference computingconcurrenthashmap_computingvaluereference = null;

                        this.lock();

                        try {
                            this.preWriteCleanup();
                            int j = this.count - 1;
                            AtomicReferenceArray atomicreferencearray = this.table;
                            int k = i & atomicreferencearray.length() - 1;
                            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(k);

                            for (mapmakerinternalmap_referenceentry = mapmakerinternalmap_referenceentry1; mapmakerinternalmap_referenceentry != null; mapmakerinternalmap_referenceentry = mapmakerinternalmap_referenceentry.getNext()) {
                                Object object2 = mapmakerinternalmap_referenceentry.getKey();

                                if (mapmakerinternalmap_referenceentry.getHash() == i && object2 != null && this.map.keyEquivalence.equivalent(k0, object2)) {
                                    MapMakerInternalMap.ValueReference mapmakerinternalmap_valuereference = mapmakerinternalmap_referenceentry.getValueReference();

                                    if (mapmakerinternalmap_valuereference.isComputingReference()) {
                                        flag = false;
                                    } else {
                                        Object object3 = mapmakerinternalmap_referenceentry.getValueReference().get();

                                        if (object3 == null) {
                                            this.enqueueNotification(object2, i, object3, MapMaker.RemovalCause.COLLECTED);
                                        } else {
                                            if (!this.map.expires() || !this.map.isExpired(mapmakerinternalmap_referenceentry)) {
                                                this.recordLockedRead(mapmakerinternalmap_referenceentry);
                                                Object object4 = object3;

                                                return object4;
                                            }

                                            this.enqueueNotification(object2, i, object3, MapMaker.RemovalCause.EXPIRED);
                                        }

                                        this.evictionQueue.remove(mapmakerinternalmap_referenceentry);
                                        this.expirationQueue.remove(mapmakerinternalmap_referenceentry);
                                        this.count = j;
                                    }
                                    break;
                                }
                            }

                            if (flag) {
                                computingconcurrenthashmap_computingvaluereference = new ComputingConcurrentHashMap.ComputingValueReference<>(function);
                                if (mapmakerinternalmap_referenceentry == null) {
                                    mapmakerinternalmap_referenceentry = this.newEntry(k0, i, mapmakerinternalmap_referenceentry1);
                                    mapmakerinternalmap_referenceentry.setValueReference(computingconcurrenthashmap_computingvaluereference);
                                    atomicreferencearray.set(k, mapmakerinternalmap_referenceentry);
                                } else {
                                    mapmakerinternalmap_referenceentry.setValueReference(computingconcurrenthashmap_computingvaluereference);
                                }
                            }
                        } finally {
                            this.unlock();
                            this.postWriteCleanup();
                        }

                        if (flag) {
                            Object object5 = this.compute(k0, i, mapmakerinternalmap_referenceentry, computingconcurrenthashmap_computingvaluereference);

                            return object5;
                        }
                    }

                    Preconditions.checkState(!Thread.holdsLock(mapmakerinternalmap_referenceentry), "Recursive computation");
                    object = mapmakerinternalmap_referenceentry.getValueReference().waitForValue();
                    if (object != null) {
                        this.recordRead(mapmakerinternalmap_referenceentry);
                        object1 = object;
                        return object1;
                    }
                } finally {
                    this.postReadCleanup();
                }
            }
        }

        V compute(K k0, int i, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, ComputingConcurrentHashMap.ComputingValueReference<K, V> computingconcurrenthashmap_computingvaluereference) throws ExecutionException {
            Object object = null;
            long j = System.nanoTime();
            long k = 0L;

            Object object1;

            try {
                synchronized (mapmakerinternalmap_referenceentry) {
                    object = computingconcurrenthashmap_computingvaluereference.compute(k0, i);
                    k = System.nanoTime();
                }

                if (object != null) {
                    object1 = this.put(k0, i, object, true);
                    if (object1 != null) {
                        this.enqueueNotification(k0, i, object, MapMaker.RemovalCause.REPLACED);
                    }
                }

                object1 = object;
            } finally {
                if (k == 0L) {
                    k = System.nanoTime();
                }

                if (object == null) {
                    this.clearValue(k0, i, computingconcurrenthashmap_computingvaluereference);
                }

            }

            return object1;
        }
    }
}
