package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.concurrent.ConcurrentMap;

@Beta
public final class Interners {

    private Interners() {}

    public static <E> Interner<E> newStrongInterner() {
        final ConcurrentMap concurrentmap = (new MapMaker()).makeMap();

        return new Interner<E>() {
            @Override
            public E intern(E e0) {
                Object object = concurrentmap.putIfAbsent(Preconditions.checkNotNull(e0), e0);

                return object == null ? e0 : object;
            }
        };
    }

    @GwtIncompatible("java.lang.ref.WeakReference")
    public static <E> Interner<E> newWeakInterner() {
        return new Interners.WeakInterner<>();
    }

    public static <E> Function<E, E> asFunction(Interner<E> interner) {
        return new Interners.InternerFunction<>((Interner) Preconditions.checkNotNull(interner));
    }

    private static class InternerFunction<E> implements Function<E, E> {

        private final Interner<E> interner;

        public InternerFunction(Interner<E> interner) {
            this.interner = interner;
        }

        @Override
        public E apply(E e0) {
            return this.interner.intern(e0);
        }

        public int hashCode() {
            return this.interner.hashCode();
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof Interners.InternerFunction) {
                Interners.InternerFunction interners_internerfunction = (Interners.InternerFunction) object;

                return this.interner.equals(interners_internerfunction.interner);
            } else {
                return false;
            }
        }
    }

    private static class WeakInterner<E> implements Interner<E> {

        private final MapMakerInternalMap<E, Interners.WeakInterner.Dummy> map;

        private WeakInterner() {
            this.map = (new MapMaker()).weakKeys().keyEquivalence(Equivalence.equals()).makeCustomMap();
        }

        @Override
        public E intern(E e0) {
            Interners.WeakInterner.Dummy interners_weakinterner_dummy;

            do {
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = this.map.getEntry(e0);

                if (mapmakerinternalmap_referenceentry != null) {
                    Object object = mapmakerinternalmap_referenceentry.getKey();

                    if (object != null) {
                        return object;
                    }
                }

                interners_weakinterner_dummy = (Interners.WeakInterner.Dummy) this.map.putIfAbsent(e0, Interners.WeakInterner.Dummy.VALUE);
            } while (interners_weakinterner_dummy != null);

            return e0;
        }

        private static enum Dummy {

            VALUE;

            private Dummy() {}
        }
    }
}
