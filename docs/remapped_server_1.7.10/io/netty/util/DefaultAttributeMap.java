package io.netty.util;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultAttributeMap implements AttributeMap {

    private static final AtomicReferenceFieldUpdater<DefaultAttributeMap, Map> updater = AtomicReferenceFieldUpdater.newUpdater(DefaultAttributeMap.class, Map.class, "map");
    private volatile Map<AttributeKey<?>, Attribute<?>> map;

    public DefaultAttributeMap() {}

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> attributekey) {
        Object object = this.map;

        if (object == null) {
            object = new IdentityHashMap(2);
            if (!DefaultAttributeMap.updater.compareAndSet(this, (Object) null, object)) {
                object = this.map;
            }
        }

        synchronized (object) {
            Object object1 = (Attribute) ((Map) object).get(attributekey);

            if (object1 == null) {
                object1 = new DefaultAttributeMap.DefaultAttribute<>((Map) object, attributekey);
                ((Map) object).put(attributekey, object1);
            }

            return (Attribute) object1;
        }
    }

    private static final class DefaultAttribute<T> extends AtomicReference<T> implements Attribute<T> {

        private static final long serialVersionUID = -2661411462200283011L;
        private final Map<AttributeKey<?>, Attribute<?>> map;
        private final AttributeKey<T> key;

        DefaultAttribute(Map<AttributeKey<?>, Attribute<?>> map, AttributeKey<T> attributekey) {
            this.map = map;
            this.key = attributekey;
        }

        @Override
        public AttributeKey<T> key() {
            return this.key;
        }

        @Override
        public T setIfAbsent(T t0) {
            while (true) {
                if (!this.compareAndSet((Object) null, t0)) {
                    Object object = this.get();

                    if (object == null) {
                        continue;
                    }

                    return object;
                }

                return null;
            }
        }

        @Override
        public T getAndRemove() {
            Object object = this.getAndSet((Object) null);

            this.remove0();
            return object;
        }

        @Override
        public void remove() {
            this.set((Object) null);
            this.remove0();
        }

        private void remove0() {
            Map map = this.map;

            synchronized (this.map) {
                this.map.remove(this.key);
            }
        }
    }
}
