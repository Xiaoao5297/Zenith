package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

@Beta
public final class CacheBuilderSpec {

    private static final Splitter KEYS_SPLITTER = Splitter.on(',').trimResults();
    private static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').trimResults();
    private static final ImmutableMap<String, CacheBuilderSpec.ValueParser> VALUE_PARSERS = ImmutableMap.builder().put("initialCapacity", new CacheBuilderSpec.InitialCapacityParser()).put("maximumSize", new CacheBuilderSpec.MaximumSizeParser()).put("maximumWeight", new CacheBuilderSpec.MaximumWeightParser()).put("concurrencyLevel", new CacheBuilderSpec.ConcurrencyLevelParser()).put("weakKeys", new CacheBuilderSpec.KeyStrengthParser(LocalCache.Strength.WEAK)).put("softValues", new CacheBuilderSpec.ValueStrengthParser(LocalCache.Strength.SOFT)).put("weakValues", new CacheBuilderSpec.ValueStrengthParser(LocalCache.Strength.WEAK)).put("recordStats", new CacheBuilderSpec.RecordStatsParser()).put("expireAfterAccess", new CacheBuilderSpec.AccessDurationParser()).put("expireAfterWrite", new CacheBuilderSpec.WriteDurationParser()).put("refreshAfterWrite", new CacheBuilderSpec.RefreshDurationParser()).put("refreshInterval", new CacheBuilderSpec.RefreshDurationParser()).build();
    @VisibleForTesting
    Integer initialCapacity;
    @VisibleForTesting
    Long maximumSize;
    @VisibleForTesting
    Long maximumWeight;
    @VisibleForTesting
    Integer concurrencyLevel;
    @VisibleForTesting
    LocalCache.Strength keyStrength;
    @VisibleForTesting
    LocalCache.Strength valueStrength;
    @VisibleForTesting
    Boolean recordStats;
    @VisibleForTesting
    long writeExpirationDuration;
    @VisibleForTesting
    TimeUnit writeExpirationTimeUnit;
    @VisibleForTesting
    long accessExpirationDuration;
    @VisibleForTesting
    TimeUnit accessExpirationTimeUnit;
    @VisibleForTesting
    long refreshDuration;
    @VisibleForTesting
    TimeUnit refreshTimeUnit;
    private final String specification;

    private CacheBuilderSpec(String s) {
        this.specification = s;
    }

    public static CacheBuilderSpec parse(String s) {
        CacheBuilderSpec cachebuilderspec = new CacheBuilderSpec(s);

        if (!s.isEmpty()) {
            Iterator iterator = CacheBuilderSpec.KEYS_SPLITTER.split(s).iterator();

            while (iterator.hasNext()) {
                String s1 = (String) iterator.next();
                ImmutableList immutablelist = ImmutableList.copyOf(CacheBuilderSpec.KEY_VALUE_SPLITTER.split(s1));

                Preconditions.checkArgument(!immutablelist.isEmpty(), "blank key-value pair");
                Preconditions.checkArgument(immutablelist.size() <= 2, "key-value pair %s with more than one equals sign", s1);
                String s2 = (String) immutablelist.get(0);
                CacheBuilderSpec.ValueParser cachebuilderspec_valueparser = (CacheBuilderSpec.ValueParser) CacheBuilderSpec.VALUE_PARSERS.get(s2);

                Preconditions.checkArgument(cachebuilderspec_valueparser != null, "unknown key %s", s2);
                String s3 = immutablelist.size() == 1 ? null : (String) immutablelist.get(1);

                cachebuilderspec_valueparser.parse(cachebuilderspec, s2, s3);
            }
        }

        return cachebuilderspec;
    }

    public static CacheBuilderSpec disableCaching() {
        return parse("maximumSize=0");
    }

    CacheBuilder<Object, Object> toCacheBuilder() {
        CacheBuilder cachebuilder = CacheBuilder.newBuilder();

        if (this.initialCapacity != null) {
            cachebuilder.initialCapacity(this.initialCapacity);
        }

        if (this.maximumSize != null) {
            cachebuilder.maximumSize(this.maximumSize);
        }

        if (this.maximumWeight != null) {
            cachebuilder.maximumWeight(this.maximumWeight);
        }

        if (this.concurrencyLevel != null) {
            cachebuilder.concurrencyLevel(this.concurrencyLevel);
        }

        if (this.keyStrength != null) {
            switch (this.keyStrength) {
                case WEAK:
                    cachebuilder.weakKeys();
                    break;
                default:
                    throw new AssertionError();
            }
        }

        if (this.valueStrength != null) {
            switch (this.valueStrength) {
                case WEAK:
                    cachebuilder.weakValues();
                    break;
                case SOFT:
                    cachebuilder.softValues();
                    break;
                default:
                    throw new AssertionError();
            }
        }

        if (this.recordStats != null && this.recordStats) {
            cachebuilder.recordStats();
        }

        if (this.writeExpirationTimeUnit != null) {
            cachebuilder.expireAfterWrite(this.writeExpirationDuration, this.writeExpirationTimeUnit);
        }

        if (this.accessExpirationTimeUnit != null) {
            cachebuilder.expireAfterAccess(this.accessExpirationDuration, this.accessExpirationTimeUnit);
        }

        if (this.refreshTimeUnit != null) {
            cachebuilder.refreshAfterWrite(this.refreshDuration, this.refreshTimeUnit);
        }

        return cachebuilder;
    }

    public String toParsableString() {
        return this.specification;
    }

    public String toString() {
        return Objects.toStringHelper((Object) this).addValue(this.toParsableString()).toString();
    }

    public int hashCode() {
        return Objects.hashCode(this.initialCapacity, this.maximumSize, this.maximumWeight, this.concurrencyLevel, this.keyStrength, this.valueStrength, this.recordStats, durationInNanos(this.writeExpirationDuration, this.writeExpirationTimeUnit), durationInNanos(this.accessExpirationDuration, this.accessExpirationTimeUnit), durationInNanos(this.refreshDuration, this.refreshTimeUnit));
    }

    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof CacheBuilderSpec)) {
            return false;
        } else {
            CacheBuilderSpec cachebuilderspec = (CacheBuilderSpec) object;

            return Objects.equal(this.initialCapacity, cachebuilderspec.initialCapacity) && Objects.equal(this.maximumSize, cachebuilderspec.maximumSize) && Objects.equal(this.maximumWeight, cachebuilderspec.maximumWeight) && Objects.equal(this.concurrencyLevel, cachebuilderspec.concurrencyLevel) && Objects.equal(this.keyStrength, cachebuilderspec.keyStrength) && Objects.equal(this.valueStrength, cachebuilderspec.valueStrength) && Objects.equal(this.recordStats, cachebuilderspec.recordStats) && Objects.equal(durationInNanos(this.writeExpirationDuration, this.writeExpirationTimeUnit), durationInNanos(cachebuilderspec.writeExpirationDuration, cachebuilderspec.writeExpirationTimeUnit)) && Objects.equal(durationInNanos(this.accessExpirationDuration, this.accessExpirationTimeUnit), durationInNanos(cachebuilderspec.accessExpirationDuration, cachebuilderspec.accessExpirationTimeUnit)) && Objects.equal(durationInNanos(this.refreshDuration, this.refreshTimeUnit), durationInNanos(cachebuilderspec.refreshDuration, cachebuilderspec.refreshTimeUnit));
        }
    }

    @Nullable
    private static Long durationInNanos(long i, @Nullable TimeUnit timeunit) {
        return timeunit == null ? null : timeunit.toNanos(i);
    }

    static class RefreshDurationParser extends CacheBuilderSpec.DurationParser {

        RefreshDurationParser() {}

        @Override
        protected void parseDuration(CacheBuilderSpec cachebuilderspec, long i, TimeUnit timeunit) {
            Preconditions.checkArgument(cachebuilderspec.refreshTimeUnit == null, "refreshAfterWrite already set");
            cachebuilderspec.refreshDuration = i;
            cachebuilderspec.refreshTimeUnit = timeunit;
        }
    }

    static class WriteDurationParser extends CacheBuilderSpec.DurationParser {

        WriteDurationParser() {}

        @Override
        protected void parseDuration(CacheBuilderSpec cachebuilderspec, long i, TimeUnit timeunit) {
            Preconditions.checkArgument(cachebuilderspec.writeExpirationTimeUnit == null, "expireAfterWrite already set");
            cachebuilderspec.writeExpirationDuration = i;
            cachebuilderspec.writeExpirationTimeUnit = timeunit;
        }
    }

    static class AccessDurationParser extends CacheBuilderSpec.DurationParser {

        AccessDurationParser() {}

        @Override
        protected void parseDuration(CacheBuilderSpec cachebuilderspec, long i, TimeUnit timeunit) {
            Preconditions.checkArgument(cachebuilderspec.accessExpirationTimeUnit == null, "expireAfterAccess already set");
            cachebuilderspec.accessExpirationDuration = i;
            cachebuilderspec.accessExpirationTimeUnit = timeunit;
        }
    }

    abstract static class DurationParser implements CacheBuilderSpec.ValueParser {

        DurationParser() {}

        protected abstract void parseDuration(CacheBuilderSpec cachebuilderspec, long i, TimeUnit timeunit);

        @Override
        public void parse(CacheBuilderSpec cachebuilderspec, String s, String s1) {
            Preconditions.checkArgument(s1 != null && !s1.isEmpty(), "value of key %s omitted", s);

            try {
                char c0 = s1.charAt(s1.length() - 1);
                TimeUnit timeunit;

                switch (c0) {
                    case 'd':
                        timeunit = TimeUnit.DAYS;
                        break;
                    case 'h':
                        timeunit = TimeUnit.HOURS;
                        break;
                    case 'm':
                        timeunit = TimeUnit.MINUTES;
                        break;
                    case 's':
                        timeunit = TimeUnit.SECONDS;
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("key %s invalid format.  was %s, must end with one of [dDhHmMsS]", s, s1));
                }

                long i = Long.parseLong(s1.substring(0, s1.length() - 1));

                this.parseDuration(cachebuilderspec, i, timeunit);
            } catch (NumberFormatException numberformatexception) {
                throw new IllegalArgumentException(String.format("key %s value set to %s, must be integer", s, s1));
            }
        }
    }

    static class RecordStatsParser implements CacheBuilderSpec.ValueParser {

        RecordStatsParser() {}

        @Override
        public void parse(CacheBuilderSpec cachebuilderspec, String s, @Nullable String s1) {
            Preconditions.checkArgument(s1 == null, "recordStats does not take values");
            Preconditions.checkArgument(cachebuilderspec.recordStats == null, "recordStats already set");
            cachebuilderspec.recordStats = true;
        }
    }

    static class ValueStrengthParser implements CacheBuilderSpec.ValueParser {

        private final LocalCache.Strength strength;

        public ValueStrengthParser(LocalCache.Strength localcache_strength) {
            this.strength = localcache_strength;
        }

        @Override
        public void parse(CacheBuilderSpec cachebuilderspec, String s, @Nullable String s1) {
            Preconditions.checkArgument(s1 == null, "key %s does not take values", s);
            Preconditions.checkArgument(cachebuilderspec.valueStrength == null, "%s was already set to %s", s, cachebuilderspec.valueStrength);
            cachebuilderspec.valueStrength = this.strength;
        }
    }

    static class KeyStrengthParser implements CacheBuilderSpec.ValueParser {

        private final LocalCache.Strength strength;

        public KeyStrengthParser(LocalCache.Strength localcache_strength) {
            this.strength = localcache_strength;
        }

        @Override
        public void parse(CacheBuilderSpec cachebuilderspec, String s, @Nullable String s1) {
            Preconditions.checkArgument(s1 == null, "key %s does not take values", s);
            Preconditions.checkArgument(cachebuilderspec.keyStrength == null, "%s was already set to %s", s, cachebuilderspec.keyStrength);
            cachebuilderspec.keyStrength = this.strength;
        }
    }

    static class ConcurrencyLevelParser extends CacheBuilderSpec.IntegerParser {

        ConcurrencyLevelParser() {}

        @Override
        protected void parseInteger(CacheBuilderSpec cachebuilderspec, int i) {
            Preconditions.checkArgument(cachebuilderspec.concurrencyLevel == null, "concurrency level was already set to ", cachebuilderspec.concurrencyLevel);
            cachebuilderspec.concurrencyLevel = i;
        }
    }

    static class MaximumWeightParser extends CacheBuilderSpec.LongParser {

        MaximumWeightParser() {}

        @Override
        protected void parseLong(CacheBuilderSpec cachebuilderspec, long i) {
            Preconditions.checkArgument(cachebuilderspec.maximumWeight == null, "maximum weight was already set to ", cachebuilderspec.maximumWeight);
            Preconditions.checkArgument(cachebuilderspec.maximumSize == null, "maximum size was already set to ", cachebuilderspec.maximumSize);
            cachebuilderspec.maximumWeight = i;
        }
    }

    static class MaximumSizeParser extends CacheBuilderSpec.LongParser {

        MaximumSizeParser() {}

        @Override
        protected void parseLong(CacheBuilderSpec cachebuilderspec, long i) {
            Preconditions.checkArgument(cachebuilderspec.maximumSize == null, "maximum size was already set to ", cachebuilderspec.maximumSize);
            Preconditions.checkArgument(cachebuilderspec.maximumWeight == null, "maximum weight was already set to ", cachebuilderspec.maximumWeight);
            cachebuilderspec.maximumSize = i;
        }
    }

    static class InitialCapacityParser extends CacheBuilderSpec.IntegerParser {

        InitialCapacityParser() {}

        @Override
        protected void parseInteger(CacheBuilderSpec cachebuilderspec, int i) {
            Preconditions.checkArgument(cachebuilderspec.initialCapacity == null, "initial capacity was already set to ", cachebuilderspec.initialCapacity);
            cachebuilderspec.initialCapacity = i;
        }
    }

    abstract static class LongParser implements CacheBuilderSpec.ValueParser {

        LongParser() {}

        protected abstract void parseLong(CacheBuilderSpec cachebuilderspec, long i);

        @Override
        public void parse(CacheBuilderSpec cachebuilderspec, String s, String s1) {
            Preconditions.checkArgument(s1 != null && !s1.isEmpty(), "value of key %s omitted", s);

            try {
                this.parseLong(cachebuilderspec, Long.parseLong(s1));
            } catch (NumberFormatException numberformatexception) {
                throw new IllegalArgumentException(String.format("key %s value set to %s, must be integer", s, s1), numberformatexception);
            }
        }
    }

    abstract static class IntegerParser implements CacheBuilderSpec.ValueParser {

        IntegerParser() {}

        protected abstract void parseInteger(CacheBuilderSpec cachebuilderspec, int i);

        @Override
        public void parse(CacheBuilderSpec cachebuilderspec, String s, String s1) {
            Preconditions.checkArgument(s1 != null && !s1.isEmpty(), "value of key %s omitted", s);

            try {
                this.parseInteger(cachebuilderspec, Integer.parseInt(s1));
            } catch (NumberFormatException numberformatexception) {
                throw new IllegalArgumentException(String.format("key %s value set to %s, must be integer", s, s1), numberformatexception);
            }
        }
    }

    private interface ValueParser {

        void parse(CacheBuilderSpec cachebuilderspec, String s, @Nullable String s1);
    }
}
