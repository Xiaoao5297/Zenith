package com.google.common.base;

import com.google.common.annotations.Beta;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class Throwables {

    private Throwables() {}

    public static <X extends Throwable> void propagateIfInstanceOf(@Nullable Throwable throwable, Class<X> oclass) throws X {
        if (throwable != null && oclass.isInstance(throwable)) {
            throw (Throwable) oclass.cast(throwable);
        }
    }

    public static void propagateIfPossible(@Nullable Throwable throwable) {
        propagateIfInstanceOf(throwable, Error.class);
        propagateIfInstanceOf(throwable, RuntimeException.class);
    }

    public static <X extends Throwable> void propagateIfPossible(@Nullable Throwable throwable, Class<X> oclass) throws X {
        propagateIfInstanceOf(throwable, oclass);
        propagateIfPossible(throwable);
    }

    public static <X1 extends Throwable, X2 extends Throwable> void propagateIfPossible(@Nullable Throwable throwable, Class<X1> oclass, Class<X2> oclass1) throws X1, X2 {
        Preconditions.checkNotNull(oclass1);
        propagateIfInstanceOf(throwable, oclass);
        propagateIfPossible(throwable, oclass1);
    }

    public static RuntimeException propagate(Throwable throwable) {
        propagateIfPossible((Throwable) Preconditions.checkNotNull(throwable));
        throw new RuntimeException(throwable);
    }

    public static Throwable getRootCause(Throwable throwable) {
        Throwable throwable1;

        while ((throwable1 = throwable.getCause()) != null) {
            throwable = throwable1;
        }

        return throwable;
    }

    @Beta
    public static List<Throwable> getCausalChain(Throwable throwable) {
        Preconditions.checkNotNull(throwable);

        ArrayList arraylist;

        for (arraylist = new ArrayList(4); throwable != null; throwable = throwable.getCause()) {
            arraylist.add(throwable);
        }

        return Collections.unmodifiableList(arraylist);
    }

    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringwriter = new StringWriter();

        throwable.printStackTrace(new PrintWriter(stringwriter));
        return stringwriter.toString();
    }
}
