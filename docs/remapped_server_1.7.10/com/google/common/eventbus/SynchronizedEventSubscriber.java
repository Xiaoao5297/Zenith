package com.google.common.eventbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class SynchronizedEventSubscriber extends EventSubscriber {

    public SynchronizedEventSubscriber(Object object, Method method) {
        super(object, method);
    }

    @Override
    public void handleEvent(Object object) throws InvocationTargetException {
        synchronized (this) {
            super.handleEvent(object);
        }
    }
}
