package com.google.common.eventbus;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

@Beta
public class AsyncEventBus extends EventBus {

    private final Executor executor;
    private final ConcurrentLinkedQueue<EventBus.EventWithSubscriber> eventsToDispatch = new ConcurrentLinkedQueue();

    public AsyncEventBus(String s, Executor executor) {
        super(s);
        this.executor = (Executor) Preconditions.checkNotNull(executor);
    }

    public AsyncEventBus(Executor executor, SubscriberExceptionHandler subscriberexceptionhandler) {
        super(subscriberexceptionhandler);
        this.executor = (Executor) Preconditions.checkNotNull(executor);
    }

    public AsyncEventBus(Executor executor) {
        super("default");
        this.executor = (Executor) Preconditions.checkNotNull(executor);
    }

    @Override
    void enqueueEvent(Object object, EventSubscriber eventsubscriber) {
        this.eventsToDispatch.offer(new EventBus.EventWithSubscriber(object, eventsubscriber));
    }

    @Override
    protected void dispatchQueuedEvents() {
        while (true) {
            EventBus.EventWithSubscriber eventbus_eventwithsubscriber = (EventBus.EventWithSubscriber) this.eventsToDispatch.poll();

            if (eventbus_eventwithsubscriber == null) {
                return;
            }

            this.dispatch(eventbus_eventwithsubscriber.event, eventbus_eventwithsubscriber.subscriber);
        }
    }

    @Override
    void dispatch(final Object object, final EventSubscriber eventsubscriber) {
        Preconditions.checkNotNull(object);
        Preconditions.checkNotNull(eventsubscriber);
        this.executor.execute(new Runnable() {
            public void run() {
                AsyncEventBus.super.dispatch(object, eventsubscriber);
            }
        });
    }
}
