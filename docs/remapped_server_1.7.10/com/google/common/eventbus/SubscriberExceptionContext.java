package com.google.common.eventbus;

import com.google.common.base.Preconditions;
import java.lang.reflect.Method;

public class SubscriberExceptionContext {

    private final EventBus eventBus;
    private final Object event;
    private final Object subscriber;
    private final Method subscriberMethod;

    SubscriberExceptionContext(EventBus eventbus, Object object, Object object1, Method method) {
        this.eventBus = (EventBus) Preconditions.checkNotNull(eventbus);
        this.event = Preconditions.checkNotNull(object);
        this.subscriber = Preconditions.checkNotNull(object1);
        this.subscriberMethod = (Method) Preconditions.checkNotNull(method);
    }

    public EventBus getEventBus() {
        return this.eventBus;
    }

    public Object getEvent() {
        return this.event;
    }

    public Object getSubscriber() {
        return this.subscriber;
    }

    public Method getSubscriberMethod() {
        return this.subscriberMethod;
    }
}
