package io.netty.util;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface Timer {

    Timeout newTimeout(TimerTask timertask, long i, TimeUnit timeunit);

    Set<Timeout> stop();
}
