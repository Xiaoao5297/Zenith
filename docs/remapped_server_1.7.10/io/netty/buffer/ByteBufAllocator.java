package io.netty.buffer;

public interface ByteBufAllocator {

    ByteBuf buffer();

    ByteBuf buffer(int i);

    ByteBuf buffer(int i, int j);

    ByteBuf ioBuffer();

    ByteBuf ioBuffer(int i);

    ByteBuf ioBuffer(int i, int j);

    ByteBuf heapBuffer();

    ByteBuf heapBuffer(int i);

    ByteBuf heapBuffer(int i, int j);

    ByteBuf directBuffer();

    ByteBuf directBuffer(int i);

    ByteBuf directBuffer(int i, int j);

    CompositeByteBuf compositeBuffer();

    CompositeByteBuf compositeBuffer(int i);

    CompositeByteBuf compositeHeapBuffer();

    CompositeByteBuf compositeHeapBuffer(int i);

    CompositeByteBuf compositeDirectBuffer();

    CompositeByteBuf compositeDirectBuffer(int i);

    boolean isDirectBufferPooled();
}
