package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.CheckReturnValue;

@GwtCompatible(emulated = true)
public final class Splitter {

    private final CharMatcher trimmer;
    private final boolean omitEmptyStrings;
    private final Splitter.Strategy strategy;
    private final int limit;

    private Splitter(Splitter.Strategy splitter_strategy) {
        this(splitter_strategy, false, CharMatcher.NONE, Integer.MAX_VALUE);
    }

    private Splitter(Splitter.Strategy splitter_strategy, boolean flag, CharMatcher charmatcher, int i) {
        this.strategy = splitter_strategy;
        this.omitEmptyStrings = flag;
        this.trimmer = charmatcher;
        this.limit = i;
    }

    public static Splitter on(char c0) {
        return on(CharMatcher.is(c0));
    }

    public static Splitter on(final CharMatcher charmatcher) {
        Preconditions.checkNotNull(charmatcher);
        return new Splitter(new Splitter.Strategy() {
            @Override
            public Splitter.SplittingIterator iterator(Splitter splitter, CharSequence charsequence) {
                return new Splitter.SplittingIterator(splitter, charsequence) {
                    @Override
                    int separatorStart(int i) {
                        return charmatcher.indexIn(this.toSplit, i);
                    }

                    @Override
                    int separatorEnd(int i) {
                        return i + 1;
                    }
                };
            }
        });
    }

    public static Splitter on(final String s) {
        Preconditions.checkArgument(s.length() != 0, "The separator may not be the empty string.");
        return new Splitter(new Splitter.Strategy() {
            @Override
            public Splitter.SplittingIterator iterator(Splitter splitter, CharSequence charsequence) {
                return new Splitter.SplittingIterator(splitter, charsequence) {
                    @Override
                    public int separatorStart(int i) {
                        int j = s.length();
                        int k = i;
                        int l = this.toSplit.length() - j;

                        label23:
                        while (k <= l) {
                            for (int i1 = 0; i1 < j; ++i1) {
                                if (this.toSplit.charAt(i1 + k) != s.charAt(i1)) {
                                    ++k;
                                    continue label23;
                                }
                            }

                            return k;
                        }

                        return -1;
                    }

                    @Override
                    public int separatorEnd(int i) {
                        return i + s.length();
                    }
                };
            }
        });
    }

    @GwtIncompatible("java.util.regex")
    public static Splitter on(final Pattern pattern) {
        Preconditions.checkNotNull(pattern);
        Preconditions.checkArgument(!pattern.matcher("").matches(), "The pattern may not match the empty string: %s", pattern);
        return new Splitter(new Splitter.Strategy() {
            @Override
            public Splitter.SplittingIterator iterator(Splitter splitter, CharSequence charsequence) {
                final Matcher matcher = pattern.matcher(charsequence);

                return new Splitter.SplittingIterator(splitter, charsequence) {
                    @Override
                    public int separatorStart(int i) {
                        return matcher.find(i) ? matcher.start() : -1;
                    }

                    @Override
                    public int separatorEnd(int i) {
                        return matcher.end();
                    }
                };
            }
        });
    }

    @GwtIncompatible("java.util.regex")
    public static Splitter onPattern(String s) {
        return on(Pattern.compile(s));
    }

    public static Splitter fixedLength(final int i) {
        Preconditions.checkArgument(i > 0, "The length may not be less than 1");
        return new Splitter(new Splitter.Strategy() {
            @Override
            public Splitter.SplittingIterator iterator(Splitter splitter, CharSequence charsequence) {
                return new Splitter.SplittingIterator(splitter, charsequence) {
                    @Override
                    public int separatorStart(int j) {
                        int k = j + i;

                        return k < this.toSplit.length() ? k : -1;
                    }

                    @Override
                    public int separatorEnd(int j) {
                        return j;
                    }
                };
            }
        });
    }

    @CheckReturnValue
    public Splitter omitEmptyStrings() {
        return new Splitter(this.strategy, true, this.trimmer, this.limit);
    }

    @CheckReturnValue
    public Splitter limit(int i) {
        Preconditions.checkArgument(i > 0, "must be greater than zero: %s", i);
        return new Splitter(this.strategy, this.omitEmptyStrings, this.trimmer, i);
    }

    @CheckReturnValue
    public Splitter trimResults() {
        return this.trimResults(CharMatcher.WHITESPACE);
    }

    @CheckReturnValue
    public Splitter trimResults(CharMatcher charmatcher) {
        Preconditions.checkNotNull(charmatcher);
        return new Splitter(this.strategy, this.omitEmptyStrings, charmatcher, this.limit);
    }

    public Iterable<String> split(final CharSequence charsequence) {
        Preconditions.checkNotNull(charsequence);
        return new Iterable<String>() {
            public Iterator<String> iterator() {
                return Splitter.this.splittingIterator(charsequence);
            }

            public String toString() {
                return Joiner.on(", ").appendTo((new StringBuilder()).append('['), (Iterable) this).append(']').toString();
            }
        };
    }

    private Iterator<String> splittingIterator(CharSequence charsequence) {
        return this.strategy.iterator(this, charsequence);
    }

    @Beta
    public List<String> splitToList(CharSequence charsequence) {
        Preconditions.checkNotNull(charsequence);
        Iterator iterator = this.splittingIterator(charsequence);
        ArrayList arraylist = new ArrayList();

        while (iterator.hasNext()) {
            arraylist.add(iterator.next());
        }

        return Collections.unmodifiableList(arraylist);
    }

    @CheckReturnValue
    @Beta
    public Splitter.MapSplitter withKeyValueSeparator(String s) {
        return this.withKeyValueSeparator(on(s));
    }

    @CheckReturnValue
    @Beta
    public Splitter.MapSplitter withKeyValueSeparator(char c0) {
        return this.withKeyValueSeparator(on(c0));
    }

    @CheckReturnValue
    @Beta
    public Splitter.MapSplitter withKeyValueSeparator(Splitter splitter) {
        return new Splitter.MapSplitter(this, splitter);
    }

    private abstract static class SplittingIterator extends AbstractIterator<String> {

        final CharSequence toSplit;
        final CharMatcher trimmer;
        final boolean omitEmptyStrings;
        int offset = 0;
        int limit;

        abstract int separatorStart(int i);

        abstract int separatorEnd(int i);

        protected SplittingIterator(Splitter splitter, CharSequence charsequence) {
            this.trimmer = splitter.trimmer;
            this.omitEmptyStrings = splitter.omitEmptyStrings;
            this.limit = splitter.limit;
            this.toSplit = charsequence;
        }

        @Override
        protected String computeNext() {
            int i = this.offset;

            while (true) {
                while (this.offset != -1) {
                    int j = i;
                    int k = this.separatorStart(this.offset);
                    int l;

                    if (k == -1) {
                        l = this.toSplit.length();
                        this.offset = -1;
                    } else {
                        l = k;
                        this.offset = this.separatorEnd(k);
                    }

                    if (this.offset != i) {
                        while (j < l && this.trimmer.matches(this.toSplit.charAt(j))) {
                            ++j;
                        }

                        while (l > j && this.trimmer.matches(this.toSplit.charAt(l - 1))) {
                            --l;
                        }

                        if (!this.omitEmptyStrings || j != l) {
                            if (this.limit == 1) {
                                l = this.toSplit.length();

                                for (this.offset = -1; l > j && this.trimmer.matches(this.toSplit.charAt(l - 1)); --l) {
                                    ;
                                }
                            } else {
                                --this.limit;
                            }

                            return this.toSplit.subSequence(j, l).toString();
                        }

                        i = this.offset;
                    } else {
                        ++this.offset;
                        if (this.offset >= this.toSplit.length()) {
                            this.offset = -1;
                        }
                    }
                }

                return (String) this.endOfData();
            }
        }
    }

    private interface Strategy {

        Iterator<String> iterator(Splitter splitter, CharSequence charsequence);
    }

    @Beta
    public static final class MapSplitter {

        private static final String INVALID_ENTRY_MESSAGE = "Chunk [%s] is not a valid entry";
        private final Splitter outerSplitter;
        private final Splitter entrySplitter;

        private MapSplitter(Splitter splitter, Splitter splitter1) {
            this.outerSplitter = splitter;
            this.entrySplitter = (Splitter) Preconditions.checkNotNull(splitter1);
        }

        public Map<String, String> split(CharSequence charsequence) {
            LinkedHashMap linkedhashmap = new LinkedHashMap();
            Iterator iterator = this.outerSplitter.split(charsequence).iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                Iterator iterator1 = this.entrySplitter.splittingIterator(s);

                Preconditions.checkArgument(iterator1.hasNext(), "Chunk [%s] is not a valid entry", s);
                String s1 = (String) iterator1.next();

                Preconditions.checkArgument(!linkedhashmap.containsKey(s1), "Duplicate key [%s] found.", s1);
                Preconditions.checkArgument(iterator1.hasNext(), "Chunk [%s] is not a valid entry", s);
                String s2 = (String) iterator1.next();

                linkedhashmap.put(s1, s2);
                Preconditions.checkArgument(!iterator1.hasNext(), "Chunk [%s] is not a valid entry", s);
            }

            return Collections.unmodifiableMap(linkedhashmap);
        }
    }
}
