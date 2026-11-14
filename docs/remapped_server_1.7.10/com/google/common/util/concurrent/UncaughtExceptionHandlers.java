package com.google.common.util.concurrent;

import com.google.common.annotations.VisibleForTesting;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class UncaughtExceptionHandlers {

    private UncaughtExceptionHandlers() {}

    public static UncaughtExceptionHandler systemExit() {
        return new UncaughtExceptionHandlers.Exiter(Runtime.getRuntime());
    }

    @VisibleForTesting
    static final class Exiter implements UncaughtExceptionHandler {

        private static final Logger logger = Logger.getLogger(UncaughtExceptionHandlers.Exiter.class.getName());
        private final Runtime runtime;

        Exiter(Runtime runtime) {
            this.runtime = runtime;
        }

        public void uncaughtException(Thread thread, Throwable throwable) {
            try {
                UncaughtExceptionHandlers.Exiter.logger.log(Level.SEVERE, String.format("Caught an exception in %s.  Shutting down.", thread), throwable);
            } catch (Throwable throwable1) {
                System.err.println(throwable.getMessage());
                System.err.println(throwable1.getMessage());
            } finally {
                this.runtime.exit(1);
            }

        }
    }
}
