package io.netty.util;

public interface ReferenceCounted {

    int refCnt();

    ReferenceCounted retain();

    ReferenceCounted retain(int i);

    boolean release();

    boolean release(int i);
}
