package io.netty.util.internal;

public final class NoOpTypeParameterMatcher extends TypeParameterMatcher {

    public NoOpTypeParameterMatcher() {}

    @Override
    public boolean match(Object object) {
        return true;
    }
}
