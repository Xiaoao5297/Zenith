package com.google.common.eventbus;

public interface SubscriberExceptionHandler {

    void handleException(Throwable throwable, SubscriberExceptionContext subscriberexceptioncontext);
}
