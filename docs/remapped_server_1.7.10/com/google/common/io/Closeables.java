package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

@Beta
public final class Closeables {

    @VisibleForTesting
    static final Logger logger = Logger.getLogger(Closeables.class.getName());

    private Closeables() {}

    public static void close(@Nullable Closeable closeable, boolean flag) throws IOException {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ioexception) {
                if (!flag) {
                    throw ioexception;
                }

                Closeables.logger.log(Level.WARNING, "IOException thrown while closing Closeable.", ioexception);
            }

        }
    }
}
