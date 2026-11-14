package io.netty.buffer;

import java.nio.ByteBuffer;

final class PoolThreadCache {

    final PoolArena<byte[]> heapArena;
    final PoolArena<ByteBuffer> directArena;

    PoolThreadCache(PoolArena<byte[]> poolarena, PoolArena<ByteBuffer> poolarena1) {
        this.heapArena = poolarena;
        this.directArena = poolarena1;
    }
}
