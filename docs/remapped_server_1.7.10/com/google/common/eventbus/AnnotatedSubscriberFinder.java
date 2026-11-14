package com.google.common.eventbus;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

class AnnotatedSubscriberFinder implements SubscriberFindingStrategy {

    private static final LoadingCache<Class<?>, ImmutableList<Method>> subscriberMethodsCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Class<?>, ImmutableList<Method>>() {
        public ImmutableList<Method> load(Class<?> oclass) throws Exception {
            return AnnotatedSubscriberFinder.getAnnotatedMethodsInternal(oclass);
        }
    });

    AnnotatedSubscriberFinder() {}

    @Override
    public Multimap<Class<?>, EventSubscriber> findAllSubscribers(Object object) {
        HashMultimap hashmultimap = HashMultimap.create();
        Class oclass = object.getClass();
        Iterator iterator = getAnnotatedMethods(oclass).iterator();

        while (iterator.hasNext()) {
            Method method = (Method) iterator.next();
            Class[] aclass = method.getParameterTypes();
            Class oclass1 = aclass[0];
            EventSubscriber eventsubscriber = makeSubscriber(object, method);

            hashmultimap.put(oclass1, eventsubscriber);
        }

        return hashmultimap;
    }

    private static ImmutableList<Method> getAnnotatedMethods(Class<?> oclass) {
        try {
            return (ImmutableList) AnnotatedSubscriberFinder.subscriberMethodsCache.getUnchecked(oclass);
        } catch (UncheckedExecutionException uncheckedexecutionexception) {
            throw Throwables.propagate(uncheckedexecutionexception.getCause());
        }
    }

    private static ImmutableList<Method> getAnnotatedMethodsInternal(Class<?> oclass) {
        Set set = TypeToken.of(oclass).getTypes().rawTypes();
        HashMap hashmap = Maps.newHashMap();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Class oclass1 = (Class) iterator.next();
            Method[] amethod = oclass1.getMethods();
            int i = amethod.length;

            for (int j = 0; j < i; ++j) {
                Method method = amethod[j];

                if (method.isAnnotationPresent(Subscribe.class)) {
                    Class[] aclass = method.getParameterTypes();

                    if (aclass.length != 1) {
                        throw new IllegalArgumentException("Method " + method + " has @Subscribe annotation, but requires " + aclass.length + " arguments.  Event subscriber methods must require a single argument.");
                    }

                    AnnotatedSubscriberFinder.MethodIdentifier annotatedsubscriberfinder_methodidentifier = new AnnotatedSubscriberFinder.MethodIdentifier(method);

                    if (!hashmap.containsKey(annotatedsubscriberfinder_methodidentifier)) {
                        hashmap.put(annotatedsubscriberfinder_methodidentifier, method);
                    }
                }
            }
        }

        return ImmutableList.copyOf(hashmap.values());
    }

    private static EventSubscriber makeSubscriber(Object object, Method method) {
        Object object1;

        if (methodIsDeclaredThreadSafe(method)) {
            object1 = new EventSubscriber(object, method);
        } else {
            object1 = new SynchronizedEventSubscriber(object, method);
        }

        return (EventSubscriber) object1;
    }

    private static boolean methodIsDeclaredThreadSafe(Method method) {
        return method.getAnnotation(AllowConcurrentEvents.class) != null;
    }

    private static final class MethodIdentifier {

        private final String name;
        private final List<Class<?>> parameterTypes;

        MethodIdentifier(Method method) {
            this.name = method.getName();
            this.parameterTypes = Arrays.asList(method.getParameterTypes());
        }

        public int hashCode() {
            return Objects.hashCode(this.name, this.parameterTypes);
        }

        public boolean equals(@Nullable Object object) {
            if (!(object instanceof AnnotatedSubscriberFinder.MethodIdentifier)) {
                return false;
            } else {
                AnnotatedSubscriberFinder.MethodIdentifier annotatedsubscriberfinder_methodidentifier = (AnnotatedSubscriberFinder.MethodIdentifier) object;

                return this.name.equals(annotatedsubscriberfinder_methodidentifier.name) && this.parameterTypes.equals(annotatedsubscriberfinder_methodidentifier.parameterTypes);
            }
        }
    }
}
