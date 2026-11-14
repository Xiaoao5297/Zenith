package com.google.common.eventbus;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

@Beta
public class EventBus {

    private static final LoadingCache<Class<?>, Set<Class<?>>> flattenHierarchyCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Class<?>, Set<Class<?>>>() {
        public Set<Class<?>> load(Class<?> oclass) {
            return TypeToken.of(oclass).getTypes().rawTypes();
        }
    });
    private final SetMultimap<Class<?>, EventSubscriber> subscribersByType;
    private final ReadWriteLock subscribersByTypeLock;
    private final SubscriberFindingStrategy finder;
    private final ThreadLocal<Queue<EventBus.EventWithSubscriber>> eventsToDispatch;
    private final ThreadLocal<Boolean> isDispatching;
    private SubscriberExceptionHandler subscriberExceptionHandler;

    public EventBus() {
        this("default");
    }

    public EventBus(String s) {
        this((SubscriberExceptionHandler) (new EventBus.LoggingSubscriberExceptionHandler(s)));
    }

    public EventBus(SubscriberExceptionHandler subscriberexceptionhandler) {
        this.subscribersByType = HashMultimap.create();
        this.subscribersByTypeLock = new ReentrantReadWriteLock();
        this.finder = new AnnotatedSubscriberFinder();
        this.eventsToDispatch = new ThreadLocal<Queue<EventBus.EventWithSubscriber>>() {
            protected Queue<EventBus.EventWithSubscriber> initialValue() {
                return new LinkedList();
            }
        };
        this.isDispatching = new ThreadLocal<Boolean>() {
            protected Boolean initialValue() {
                return false;
            }
        };
        this.subscriberExceptionHandler = (SubscriberExceptionHandler) Preconditions.checkNotNull(subscriberexceptionhandler);
    }

    public void register(Object object) {
        Multimap multimap = this.finder.findAllSubscribers(object);

        this.subscribersByTypeLock.writeLock().lock();

        try {
            this.subscribersByType.putAll(multimap);
        } finally {
            this.subscribersByTypeLock.writeLock().unlock();
        }

    }

    public void unregister(Object object) {
        Multimap multimap = this.finder.findAllSubscribers(object);
        Iterator iterator = multimap.asMap().entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Class oclass = (Class) entry.getKey();
            Collection collection = (Collection) entry.getValue();

            this.subscribersByTypeLock.writeLock().lock();

            try {
                Set set = this.subscribersByType.get(oclass);

                if (!set.containsAll(collection)) {
                    throw new IllegalArgumentException("missing event subscriber for an annotated method. Is " + object + " registered?");
                }

                set.removeAll(collection);
            } finally {
                this.subscribersByTypeLock.writeLock().unlock();
            }
        }

    }

    public void post(Object object) {
        Set set = this.flattenHierarchy(object.getClass());
        boolean flag = false;
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Class oclass = (Class) iterator.next();

            this.subscribersByTypeLock.readLock().lock();

            try {
                Set set1 = this.subscribersByType.get(oclass);

                if (!set1.isEmpty()) {
                    flag = true;
                    Iterator iterator1 = set1.iterator();

                    while (iterator1.hasNext()) {
                        EventSubscriber eventsubscriber = (EventSubscriber) iterator1.next();

                        this.enqueueEvent(object, eventsubscriber);
                    }
                }
            } finally {
                this.subscribersByTypeLock.readLock().unlock();
            }
        }

        if (!flag && !(object instanceof DeadEvent)) {
            this.post(new DeadEvent(this, object));
        }

        this.dispatchQueuedEvents();
    }

    void enqueueEvent(Object object, EventSubscriber eventsubscriber) {
        ((Queue) this.eventsToDispatch.get()).offer(new EventBus.EventWithSubscriber(object, eventsubscriber));
    }

    void dispatchQueuedEvents() {
        if (!(Boolean) this.isDispatching.get()) {
            this.isDispatching.set(true);

            try {
                Queue queue = (Queue) this.eventsToDispatch.get();

                EventBus.EventWithSubscriber eventbus_eventwithsubscriber;

                while ((eventbus_eventwithsubscriber = (EventBus.EventWithSubscriber) queue.poll()) != null) {
                    this.dispatch(eventbus_eventwithsubscriber.event, eventbus_eventwithsubscriber.subscriber);
                }
            } finally {
                this.isDispatching.remove();
                this.eventsToDispatch.remove();
            }

        }
    }

    void dispatch(Object object, EventSubscriber eventsubscriber) {
        try {
            eventsubscriber.handleEvent(object);
        } catch (InvocationTargetException invocationtargetexception) {
            InvocationTargetException invocationtargetexception1 = invocationtargetexception;

            try {
                this.subscriberExceptionHandler.handleException(invocationtargetexception1.getCause(), new SubscriberExceptionContext(this, object, eventsubscriber.getSubscriber(), eventsubscriber.getMethod()));
            } catch (Throwable throwable) {
                Logger.getLogger(EventBus.class.getName()).log(Level.SEVERE, String.format("Exception %s thrown while handling exception: %s", throwable, invocationtargetexception.getCause()), throwable);
            }
        }

    }

    @VisibleForTesting
    Set<Class<?>> flattenHierarchy(Class<?> oclass) {
        try {
            return (Set) EventBus.flattenHierarchyCache.getUnchecked(oclass);
        } catch (UncheckedExecutionException uncheckedexecutionexception) {
            throw Throwables.propagate(uncheckedexecutionexception.getCause());
        }
    }

    static class EventWithSubscriber {

        final Object event;
        final EventSubscriber subscriber;

        public EventWithSubscriber(Object object, EventSubscriber eventsubscriber) {
            this.event = Preconditions.checkNotNull(object);
            this.subscriber = (EventSubscriber) Preconditions.checkNotNull(eventsubscriber);
        }
    }

    private static final class LoggingSubscriberExceptionHandler implements SubscriberExceptionHandler {

        private final Logger logger;

        public LoggingSubscriberExceptionHandler(String s) {
            this.logger = Logger.getLogger(EventBus.class.getName() + "." + (String) Preconditions.checkNotNull(s));
        }

        @Override
        public void handleException(Throwable throwable, SubscriberExceptionContext subscriberexceptioncontext) {
            this.logger.log(Level.SEVERE, "Could not dispatch event: " + subscriberexceptioncontext.getSubscriber() + " to " + subscriberexceptioncontext.getSubscriberMethod(), throwable.getCause());
        }
    }
}
