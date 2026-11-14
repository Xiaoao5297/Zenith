package com.google.common.eventbus;

import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.Nullable;

class EventSubscriber {

    private final Object target;
    private final Method method;

    EventSubscriber(Object object, Method method) {
        Preconditions.checkNotNull(object, "EventSubscriber target cannot be null.");
        Preconditions.checkNotNull(method, "EventSubscriber method cannot be null.");
        this.target = object;
        this.method = method;
        method.setAccessible(true);
    }

    public void handleEvent(Object object) throws InvocationTargetException {
        Preconditions.checkNotNull(object);

        try {
            this.method.invoke(this.target, object);
        } catch (IllegalArgumentException illegalargumentexception) {
            throw new Error("Method rejected target/argument: " + object, illegalargumentexception);
        } catch (IllegalAccessException illegalaccessexception) {
            throw new Error("Method became inaccessible: " + object, illegalaccessexception);
        } catch (InvocationTargetException invocationtargetexception) {
            if (invocationtargetexception.getCause() instanceof Error) {
                throw (Error) invocationtargetexception.getCause();
            } else {
                throw invocationtargetexception;
            }
        }
    }

    public String toString() {
        return "[wrapper " + this.method + "]";
    }

    public int hashCode() {
        boolean flag = true;

        return (31 + this.method.hashCode()) * 31 + System.identityHashCode(this.target);
    }

    public boolean equals(@Nullable Object object) {
        if (!(object instanceof EventSubscriber)) {
            return false;
        } else {
            EventSubscriber eventsubscriber = (EventSubscriber) object;

            return this.target == eventsubscriber.target && this.method.equals(eventsubscriber.method);
        }
    }

    public Object getSubscriber() {
        return this.target;
    }

    public Method getMethod() {
        return this.method;
    }
}
