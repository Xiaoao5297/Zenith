package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;

@Beta
@GwtCompatible
public abstract class Escaper {

    private final Function<String, String> asFunction = new Function<String, String>() {
        public String apply(String s) {
            return Escaper.this.escape(s);
        }
    };

    protected Escaper() {}

    public abstract String escape(String s);

    public final Function<String, String> asFunction() {
        return this.asFunction;
    }
}
