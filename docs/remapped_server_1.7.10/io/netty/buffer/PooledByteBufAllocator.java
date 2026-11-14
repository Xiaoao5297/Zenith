package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public class PooledByteBufAllocator extends AbstractByteBufAllocator {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PooledByteBufAllocator.class);
    private static final int DEFAULT_NUM_HEAP_ARENA;
    private static final int DEFAULT_NUM_DIRECT_ARENA;
    private static final int DEFAULT_PAGE_SIZE;
    private static final int DEFAULT_MAX_ORDER;
    private static final int MIN_PAGE_SIZE = 4096;
    private static final int MAX_CHUNK_SIZE = 1073741824;
    public static final PooledByteBufAllocator DEFAULT;
    private final PoolArena<byte[]>[] heapArenas;
    private final PoolArena<ByteBuffer>[] directArenas;
    final ThreadLocal<PoolThreadCache> threadCache;

    public PooledByteBufAllocator() {
        this(false);
    }

    public PooledByteBufAllocator(boolean flag) {
        this(flag, PooledByteBufAllocator.DEFAULT_NUM_HEAP_ARENA, PooledByteBufAllocator.DEFAULT_NUM_DIRECT_ARENA, PooledByteBufAllocator.DEFAULT_PAGE_SIZE, PooledByteBufAllocator.DEFAULT_MAX_ORDER);
    }

    public PooledByteBufAllocator(int i, int j, int k, int l) {
        this(false, i, j, k, l);
    }

    public PooledByteBufAllocator(boolean flag, int i, int j, int k, int l) {
        super(flag);
        this.threadCache = new ThreadLocal<PoolThreadCache>() {
            private final AtomicInteger index = new AtomicInteger();

            protected PoolThreadCache initialValue() {
                int i1 = this.index.getAndIncrement();
                PoolArena poolarena;

                if (PooledByteBufAllocator.this.heapArenas != null) {
                    poolarena = PooledByteBufAllocator.this.heapArenas[Math.abs(i1 % PooledByteBufAllocator.this.heapArenas.length)];
                } else {
                    poolarena = null;
                }

                PoolArena poolarena1;

                if (PooledByteBufAllocator.this.directArenas != null) {
                    poolarena1 = PooledByteBufAllocator.this.directArenas[Math.abs(i1 % PooledByteBufAllocator.this.directArenas.length)];
                } else {
                    poolarena1 = null;
                }

                return new PoolThreadCache(poolarena, poolarena1);
            }
        };
        int i1 = validateAndCalculateChunkSize(k, l);

        if (i < 0) {
            throw new IllegalArgumentException("nHeapArena: " + i + " (expected: >= 0)");
        } else if (j < 0) {
            throw new IllegalArgumentException("nDirectArea: " + j + " (expected: >= 0)");
        } else {
            int j1 = validateAndCalculatePageShifts(k);
            int k1;

            if (i > 0) {
                this.heapArenas = newArenaArray(i);

                for (k1 = 0; k1 < this.heapArenas.length; ++k1) {
                    this.heapArenas[k1] = new PoolArena.HeapArena(this, k, l, j1, i1);
                }
            } else {
                this.heapArenas = null;
            }

            if (j > 0) {
                this.directArenas = newArenaArray(j);

                for (k1 = 0; k1 < this.directArenas.length; ++k1) {
                    this.directArenas[k1] = new PoolArena.DirectArena(this, k, l, j1, i1);
                }
            } else {
                this.directArenas = null;
            }

        }
    }

    private static <T> PoolArena<T>[] newArenaArray(int i) {
        return new PoolArena[i];
    }

    private static int validateAndCalculatePageShifts(int i) {
        if (i < 4096) {
            throw new IllegalArgumentException("pageSize: " + i + " (expected: 4096+)");
        } else {
            boolean flag = false;
            int j = 0;

            for (int k = i; k != 0; k >>= 1) {
                if ((k & 1) != 0) {
                    if (flag) {
                        throw new IllegalArgumentException("pageSize: " + i + " (expected: power of 2");
                    }

                    flag = true;
                } else if (!flag) {
                    ++j;
                }
            }

            return j;
        }
    }

    private static int validateAndCalculateChunkSize(int i, int j) {
        if (j > 14) {
            throw new IllegalArgumentException("maxOrder: " + j + " (expected: 0-14)");
        } else {
            int k = i;

            for (int l = j; l > 0; --l) {
                if (k > 536870912) {
                    throw new IllegalArgumentException(String.format("pageSize (%d) << maxOrder (%d) must not exceed %d", i, j, 1073741824));
                }

                k <<= 1;
            }

            return k;
        }
    }

    @Override
    protected ByteBuf newHeapBuffer(int i, int j) {
        PoolThreadCache poolthreadcache = (PoolThreadCache) this.threadCache.get();
        PoolArena poolarena = poolthreadcache.heapArena;

        return (ByteBuf) (poolarena != null ? poolarena.allocate(poolthreadcache, i, j) : new UnpooledHeapByteBuf(this, i, j));
    }

    @Override
    protected ByteBuf newDirectBuffer(int i, int j) {
        PoolThreadCache poolthreadcache = (PoolThreadCache) this.threadCache.get();
        PoolArena poolarena = poolthreadcache.directArena;

        return (ByteBuf) (poolarena != null ? poolarena.allocate(poolthreadcache, i, j) : (PlatformDependent.hasUnsafe() ? new UnpooledUnsafeDirectByteBuf(this, i, j) : new UnpooledDirectByteBuf(this, i, j)));
    }

    @Override
    public boolean isDirectBufferPooled() {
        return this.directArenas != null;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.heapArenas.length);
        stringbuilder.append(" heap arena(s):");
        stringbuilder.append(StringUtil.NEWLINE);
        PoolArena[] apoolarena = this.heapArenas;
        int i = apoolarena.length;

        PoolArena poolarena;
        int j;

        for (j = 0; j < i; ++j) {
            poolarena = apoolarena[j];
            stringbuilder.append(poolarena);
        }

        stringbuilder.append(this.directArenas.length);
        stringbuilder.append(" direct arena(s):");
        stringbuilder.append(StringUtil.NEWLINE);
        apoolarena = this.directArenas;
        i = apoolarena.length;

        for (j = 0; j < i; ++j) {
            poolarena = apoolarena[j];
            stringbuilder.append(poolarena);
        }

        return stringbuilder.toString();
    }

    static {
        int i = SystemPropertyUtil.getInt("io.netty.allocator.pageSize", 8192);
        Throwable throwable = null;

        try {
            validateAndCalculatePageShifts(i);
        } catch (Throwable throwable1) {
            throwable = throwable1;
            i = 8192;
        }

        DEFAULT_PAGE_SIZE = i;
        int j = SystemPropertyUtil.getInt("io.netty.allocator.maxOrder", 11);
        Throwable throwable2 = null;

        try {
            validateAndCalculateChunkSize(PooledByteBufAllocator.DEFAULT_PAGE_SIZE, j);
        } catch (Throwable throwable3) {
            throwable2 = throwable3;
            j = 11;
        }

        DEFAULT_MAX_ORDER = j;
        Runtime runtime = Runtime.getRuntime();
        int k = PooledByteBufAllocator.DEFAULT_PAGE_SIZE << PooledByteBufAllocator.DEFAULT_MAX_ORDER;

        DEFAULT_NUM_HEAP_ARENA = Math.max(0, SystemPropertyUtil.getInt("io.netty.allocator.numHeapArenas", (int) Math.min((long) runtime.availableProcessors(), Runtime.getRuntime().maxMemory() / (long) k / 2L / 3L)));
        DEFAULT_NUM_DIRECT_ARENA = Math.max(0, SystemPropertyUtil.getInt("io.netty.allocator.numDirectArenas", (int) Math.min((long) runtime.availableProcessors(), PlatformDependent.maxDirectMemory() / (long) k / 2L / 3L)));
        if (PooledByteBufAllocator.logger.isDebugEnabled()) {
            PooledByteBufAllocator.logger.debug("-Dio.netty.allocator.numHeapArenas: {}", (Object) PooledByteBufAllocator.DEFAULT_NUM_HEAP_ARENA);
            PooledByteBufAllocator.logger.debug("-Dio.netty.allocator.numDirectArenas: {}", (Object) PooledByteBufAllocator.DEFAULT_NUM_DIRECT_ARENA);
            if (throwable == null) {
                PooledByteBufAllocator.logger.debug("-Dio.netty.allocator.pageSize: {}", (Object) PooledByteBufAllocator.DEFAULT_PAGE_SIZE);
            } else {
                PooledByteBufAllocator.logger.debug("-Dio.netty.allocator.pageSize: {}", PooledByteBufAllocator.DEFAULT_PAGE_SIZE, throwable);
            }

            if (throwable2 == null) {
                PooledByteBufAllocator.logger.debug("-Dio.netty.allocator.maxOrder: {}", (Object) PooledByteBufAllocator.DEFAULT_MAX_ORDER);
            } else {
                PooledByteBufAllocator.logger.debug("-Dio.netty.allocator.maxOrder: {}", PooledByteBufAllocator.DEFAULT_MAX_ORDER, throwable2);
            }

            PooledByteBufAllocator.logger.debug("-Dio.netty.allocator.chunkSize: {}", (Object) (PooledByteBufAllocator.DEFAULT_PAGE_SIZE << PooledByteBufAllocator.DEFAULT_MAX_ORDER));
        }

        DEFAULT = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
    }
}
