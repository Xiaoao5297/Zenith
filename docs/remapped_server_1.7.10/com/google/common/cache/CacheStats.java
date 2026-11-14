package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class CacheStats {

    private final long hitCount;
    private final long missCount;
    private final long loadSuccessCount;
    private final long loadExceptionCount;
    private final long totalLoadTime;
    private final long evictionCount;

    public CacheStats(long i, long j, long k, long l, long i1, long j1) {
        Preconditions.checkArgument(i >= 0L);
        Preconditions.checkArgument(j >= 0L);
        Preconditions.checkArgument(k >= 0L);
        Preconditions.checkArgument(l >= 0L);
        Preconditions.checkArgument(i1 >= 0L);
        Preconditions.checkArgument(j1 >= 0L);
        this.hitCount = i;
        this.missCount = j;
        this.loadSuccessCount = k;
        this.loadExceptionCount = l;
        this.totalLoadTime = i1;
        this.evictionCount = j1;
    }

    public long requestCount() {
        return this.hitCount + this.missCount;
    }

    public long hitCount() {
        return this.hitCount;
    }

    public double hitRate() {
        long i = this.requestCount();

        return i == 0L ? 1.0D : (double) this.hitCount / (double) i;
    }

    public long missCount() {
        return this.missCount;
    }

    public double missRate() {
        long i = this.requestCount();

        return i == 0L ? 0.0D : (double) this.missCount / (double) i;
    }

    public long loadCount() {
        return this.loadSuccessCount + this.loadExceptionCount;
    }

    public long loadSuccessCount() {
        return this.loadSuccessCount;
    }

    public long loadExceptionCount() {
        return this.loadExceptionCount;
    }

    public double loadExceptionRate() {
        long i = this.loadSuccessCount + this.loadExceptionCount;

        return i == 0L ? 0.0D : (double) this.loadExceptionCount / (double) i;
    }

    public long totalLoadTime() {
        return this.totalLoadTime;
    }

    public double averageLoadPenalty() {
        long i = this.loadSuccessCount + this.loadExceptionCount;

        return i == 0L ? 0.0D : (double) this.totalLoadTime / (double) i;
    }

    public long evictionCount() {
        return this.evictionCount;
    }

    public CacheStats minus(CacheStats cachestats) {
        return new CacheStats(Math.max(0L, this.hitCount - cachestats.hitCount), Math.max(0L, this.missCount - cachestats.missCount), Math.max(0L, this.loadSuccessCount - cachestats.loadSuccessCount), Math.max(0L, this.loadExceptionCount - cachestats.loadExceptionCount), Math.max(0L, this.totalLoadTime - cachestats.totalLoadTime), Math.max(0L, this.evictionCount - cachestats.evictionCount));
    }

    public CacheStats plus(CacheStats cachestats) {
        return new CacheStats(this.hitCount + cachestats.hitCount, this.missCount + cachestats.missCount, this.loadSuccessCount + cachestats.loadSuccessCount, this.loadExceptionCount + cachestats.loadExceptionCount, this.totalLoadTime + cachestats.totalLoadTime, this.evictionCount + cachestats.evictionCount);
    }

    public int hashCode() {
        return Objects.hashCode(this.hitCount, this.missCount, this.loadSuccessCount, this.loadExceptionCount, this.totalLoadTime, this.evictionCount);
    }

    public boolean equals(@Nullable Object object) {
        if (!(object instanceof CacheStats)) {
            return false;
        } else {
            CacheStats cachestats = (CacheStats) object;

            return this.hitCount == cachestats.hitCount && this.missCount == cachestats.missCount && this.loadSuccessCount == cachestats.loadSuccessCount && this.loadExceptionCount == cachestats.loadExceptionCount && this.totalLoadTime == cachestats.totalLoadTime && this.evictionCount == cachestats.evictionCount;
        }
    }

    public String toString() {
        return Objects.toStringHelper((Object) this).add("hitCount", this.hitCount).add("missCount", this.missCount).add("loadSuccessCount", this.loadSuccessCount).add("loadExceptionCount", this.loadExceptionCount).add("totalLoadTime", this.totalLoadTime).add("evictionCount", this.evictionCount).toString();
    }
}
