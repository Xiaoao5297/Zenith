package io.netty.handler.codec.serialization;

public interface ClassResolver {

    Class<?> resolve(String s) throws ClassNotFoundException;
}
