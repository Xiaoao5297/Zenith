package com.google.common.eventbus;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public class DeadEvent {

    private final Object source;
    private final Object event;

    public DeadEvent(Object object, Object object1) {
        this.source = Preconditions.checkNotNull(object);
        this.event = Preconditions.checkNotNull(object1);
    }

    public Object getSource() {
        return this.source;
    }

    public Object getEvent() {
        return this.event;
    }
}
