package io.netty.buffer;

import io.netty.util.ReferenceCounted;

public interface ByteBufHolder extends ReferenceCounted {

    ByteBuf content();

    ByteBufHolder copy();

    ByteBufHolder duplicate();

    @Override
    ByteBufHolder retain();

    @Override
    ByteBufHolder retain(int i);
}
