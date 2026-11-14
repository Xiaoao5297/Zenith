package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible("uses NavigableMap")
public class TreeRangeSet<C extends Comparable<?>> extends AbstractRangeSet<C> {

    @VisibleForTesting
    final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound;
    private transient Set<Range<C>> asRanges;
    private transient RangeSet<C> complement;

    public static <C extends Comparable<?>> TreeRangeSet<C> create() {
        return new TreeRangeSet<>(new TreeMap());
    }

    public static <C extends Comparable<?>> TreeRangeSet<C> create(RangeSet<C> rangeset) {
        TreeRangeSet treerangeset = create();

        treerangeset.addAll(rangeset);
        return treerangeset;
    }

    private TreeRangeSet(NavigableMap<Cut<C>, Range<C>> navigablemap) {
        this.rangesByLowerBound = navigablemap;
    }

    @Override
    public Set<Range<C>> asRanges() {
        Set set = this.asRanges;

        return set == null ? (this.asRanges = new TreeRangeSet.AsRanges()) : set;
    }

    @Nullable
    @Override
    public Range<C> rangeContaining(C c0) {
        Preconditions.checkNotNull(c0);
        Entry entry = this.rangesByLowerBound.floorEntry(Cut.belowValue(c0));

        return entry != null && ((Range) entry.getValue()).contains(c0) ? (Range) entry.getValue() : null;
    }

    @Override
    public boolean encloses(Range<C> range) {
        Preconditions.checkNotNull(range);
        Entry entry = this.rangesByLowerBound.floorEntry(range.lowerBound);

        return entry != null && ((Range) entry.getValue()).encloses(range);
    }

    @Nullable
    private Range<C> rangeEnclosing(Range<C> range) {
        Preconditions.checkNotNull(range);
        Entry entry = this.rangesByLowerBound.floorEntry(range.lowerBound);

        return entry != null && ((Range) entry.getValue()).encloses(range) ? (Range) entry.getValue() : null;
    }

    @Override
    public Range<C> span() {
        Entry entry = this.rangesByLowerBound.firstEntry();
        Entry entry1 = this.rangesByLowerBound.lastEntry();

        if (entry == null) {
            throw new NoSuchElementException();
        } else {
            return Range.create(((Range) entry.getValue()).lowerBound, ((Range) entry1.getValue()).upperBound);
        }
    }

    @Override
    public void add(Range<C> range) {
        Preconditions.checkNotNull(range);
        if (!range.isEmpty()) {
            Cut cut = range.lowerBound;
            Cut cut1 = range.upperBound;
            Entry entry = this.rangesByLowerBound.lowerEntry(cut);

            if (entry != null) {
                Range range1 = (Range) entry.getValue();

                if (range1.upperBound.compareTo(cut) >= 0) {
                    if (range1.upperBound.compareTo(cut1) >= 0) {
                        cut1 = range1.upperBound;
                    }

                    cut = range1.lowerBound;
                }
            }

            Entry entry1 = this.rangesByLowerBound.floorEntry(cut1);

            if (entry1 != null) {
                Range range2 = (Range) entry1.getValue();

                if (range2.upperBound.compareTo(cut1) >= 0) {
                    cut1 = range2.upperBound;
                }
            }

            this.rangesByLowerBound.subMap(cut, cut1).clear();
            this.replaceRangeWithSameLowerBound(Range.create(cut, cut1));
        }
    }

    @Override
    public void remove(Range<C> range) {
        Preconditions.checkNotNull(range);
        if (!range.isEmpty()) {
            Entry entry = this.rangesByLowerBound.lowerEntry(range.lowerBound);

            if (entry != null) {
                Range range1 = (Range) entry.getValue();

                if (range1.upperBound.compareTo(range.lowerBound) >= 0) {
                    if (range.hasUpperBound() && range1.upperBound.compareTo(range.upperBound) >= 0) {
                        this.replaceRangeWithSameLowerBound(Range.create(range.upperBound, range1.upperBound));
                    }

                    this.replaceRangeWithSameLowerBound(Range.create(range1.lowerBound, range.lowerBound));
                }
            }

            Entry entry1 = this.rangesByLowerBound.floorEntry(range.upperBound);

            if (entry1 != null) {
                Range range2 = (Range) entry1.getValue();

                if (range.hasUpperBound() && range2.upperBound.compareTo(range.upperBound) >= 0) {
                    this.replaceRangeWithSameLowerBound(Range.create(range.upperBound, range2.upperBound));
                }
            }

            this.rangesByLowerBound.subMap(range.lowerBound, range.upperBound).clear();
        }
    }

    private void replaceRangeWithSameLowerBound(Range<C> range) {
        if (range.isEmpty()) {
            this.rangesByLowerBound.remove(range.lowerBound);
        } else {
            this.rangesByLowerBound.put(range.lowerBound, range);
        }

    }

    @Override
    public RangeSet<C> complement() {
        RangeSet rangeset = this.complement;

        return rangeset == null ? (this.complement = new TreeRangeSet.Complement()) : rangeset;
    }

    @Override
    public RangeSet<C> subRangeSet(Range<C> range) {
        return (RangeSet) (range.equals(Range.all()) ? this : new TreeRangeSet.SubRangeSet(range));
    }

    private final class SubRangeSet extends TreeRangeSet<C> {

        private final Range<C> restriction;

        SubRangeSet(Range<C> range) {
            super(new TreeRangeSet.SubRangeSetRangesByLowerBound<>(Range.all(), range, TreeRangeSet.this.rangesByLowerBound), null);
            this.restriction = range;
        }

        @Override
        public boolean encloses(Range<C> range) {
            if (!this.restriction.isEmpty() && this.restriction.encloses(range)) {
                Range range1 = TreeRangeSet.this.rangeEnclosing(range);

                return range1 != null && !range1.intersection(this.restriction).isEmpty();
            } else {
                return false;
            }
        }

        @Nullable
        @Override
        public Range<C> rangeContaining(C c0) {
            if (!this.restriction.contains(c0)) {
                return null;
            } else {
                Range range = TreeRangeSet.this.rangeContaining(c0);

                return range == null ? null : range.intersection(this.restriction);
            }
        }

        @Override
        public void add(Range<C> range) {
            Preconditions.checkArgument(this.restriction.encloses(range), "Cannot add range %s to subRangeSet(%s)", range, this.restriction);
            super.add(range);
        }

        @Override
        public void remove(Range<C> range) {
            if (range.isConnected(this.restriction)) {
                TreeRangeSet.this.remove(range.intersection(this.restriction));
            }

        }

        @Override
        public boolean contains(C c0) {
            return this.restriction.contains(c0) && TreeRangeSet.this.contains(c0);
        }

        @Override
        public void clear() {
            TreeRangeSet.this.remove(this.restriction);
        }

        @Override
        public RangeSet<C> subRangeSet(Range<C> range) {
            return (RangeSet) (range.encloses(this.restriction) ? this : (range.isConnected(this.restriction) ? new TreeRangeSet.SubRangeSet(this.restriction.intersection(range)) : ImmutableRangeSet.of()));
        }
    }

    private static final class SubRangeSetRangesByLowerBound<C extends Comparable<?>> extends AbstractNavigableMap<Cut<C>, Range<C>> {

        private final Range<Cut<C>> lowerBoundWindow;
        private final Range<C> restriction;
        private final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound;
        private final NavigableMap<Cut<C>, Range<C>> rangesByUpperBound;

        private SubRangeSetRangesByLowerBound(Range<Cut<C>> range, Range<C> range1, NavigableMap<Cut<C>, Range<C>> navigablemap) {
            this.lowerBoundWindow = (Range) Preconditions.checkNotNull(range);
            this.restriction = (Range) Preconditions.checkNotNull(range1);
            this.rangesByLowerBound = (NavigableMap) Preconditions.checkNotNull(navigablemap);
            this.rangesByUpperBound = new TreeRangeSet.RangesByUpperBound<>(navigablemap);
        }

        private NavigableMap<Cut<C>, Range<C>> subMap(Range<Cut<C>> range) {
            return (NavigableMap) (!range.isConnected(this.lowerBoundWindow) ? ImmutableSortedMap.of() : new TreeRangeSet.SubRangeSetRangesByLowerBound<>(this.lowerBoundWindow.intersection(range), this.restriction, this.rangesByLowerBound));
        }

        public NavigableMap<Cut<C>, Range<C>> subMap(Cut<C> cut, boolean flag, Cut<C> cut1, boolean flag1) {
            return this.subMap(Range.range(cut, BoundType.forBoolean(flag), cut1, BoundType.forBoolean(flag1)));
        }

        public NavigableMap<Cut<C>, Range<C>> headMap(Cut<C> cut, boolean flag) {
            return this.subMap(Range.upTo(cut, BoundType.forBoolean(flag)));
        }

        public NavigableMap<Cut<C>, Range<C>> tailMap(Cut<C> cut, boolean flag) {
            return this.subMap(Range.downTo(cut, BoundType.forBoolean(flag)));
        }

        public Comparator<? super Cut<C>> comparator() {
            return Ordering.natural();
        }

        public boolean containsKey(@Nullable Object object) {
            return this.get(object) != null;
        }

        @Nullable
        @Override
        public Range<C> get(@Nullable Object object) {
            if (object instanceof Cut) {
                try {
                    Cut cut = (Cut) object;

                    if (!this.lowerBoundWindow.contains(cut) || cut.compareTo(this.restriction.lowerBound) < 0 || cut.compareTo(this.restriction.upperBound) >= 0) {
                        return null;
                    }

                    Range range;

                    if (cut.equals(this.restriction.lowerBound)) {
                        range = (Range) Maps.valueOrNull(this.rangesByLowerBound.floorEntry(cut));
                        if (range != null && range.upperBound.compareTo(this.restriction.lowerBound) > 0) {
                            return range.intersection(this.restriction);
                        }
                    } else {
                        range = (Range) this.rangesByLowerBound.get(cut);
                        if (range != null) {
                            return range.intersection(this.restriction);
                        }
                    }
                } catch (ClassCastException classcastexception) {
                    return null;
                }
            }

            return null;
        }

        @Override
        Iterator<Entry<Cut<C>, Range<C>>> entryIterator() {
            if (this.restriction.isEmpty()) {
                return Iterators.emptyIterator();
            } else if (this.lowerBoundWindow.upperBound.isLessThan(this.restriction.lowerBound)) {
                return Iterators.emptyIterator();
            } else {
                final Iterator iterator;

                if (this.lowerBoundWindow.lowerBound.isLessThan(this.restriction.lowerBound)) {
                    iterator = this.rangesByUpperBound.tailMap(this.restriction.lowerBound, false).values().iterator();
                } else {
                    iterator = this.rangesByLowerBound.tailMap(this.lowerBoundWindow.lowerBound.endpoint(), this.lowerBoundWindow.lowerBoundType() == BoundType.CLOSED).values().iterator();
                }

                final Cut cut = (Cut) Ordering.natural().min(this.lowerBoundWindow.upperBound, Cut.belowValue(this.restriction.upperBound));

                return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
                    @Override
                    protected Entry<Cut<C>, Range<C>> computeNext() {
                        if (!iterator.hasNext()) {
                            return (Entry) this.endOfData();
                        } else {
                            Range range = (Range) iterator.next();

                            if (cut.isLessThan(range.lowerBound)) {
                                return (Entry) this.endOfData();
                            } else {
                                range = range.intersection(SubRangeSetRangesByLowerBound.this.restriction);
                                return Maps.immutableEntry(range.lowerBound, range);
                            }
                        }
                    }
                };
            }
        }

        @Override
        Iterator<Entry<Cut<C>, Range<C>>> descendingEntryIterator() {
            if (this.restriction.isEmpty()) {
                return Iterators.emptyIterator();
            } else {
                Cut cut = (Cut) Ordering.natural().min(this.lowerBoundWindow.upperBound, Cut.belowValue(this.restriction.upperBound));
                final Iterator iterator = this.rangesByLowerBound.headMap(cut.endpoint(), cut.typeAsUpperBound() == BoundType.CLOSED).descendingMap().values().iterator();

                return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
                    @Override
                    protected Entry<Cut<C>, Range<C>> computeNext() {
                        if (!iterator.hasNext()) {
                            return (Entry) this.endOfData();
                        } else {
                            Range range = (Range) iterator.next();

                            if (SubRangeSetRangesByLowerBound.this.restriction.lowerBound.compareTo(range.upperBound) >= 0) {
                                return (Entry) this.endOfData();
                            } else {
                                range = range.intersection(SubRangeSetRangesByLowerBound.this.restriction);
                                return SubRangeSetRangesByLowerBound.this.lowerBoundWindow.contains(range.lowerBound) ? Maps.immutableEntry(range.lowerBound, range) : (Entry) this.endOfData();
                            }
                        }
                    }
                };
            }
        }

        @Override
        public int size() {
            return Iterators.size(this.entryIterator());
        }
    }

    private final class Complement extends TreeRangeSet<C> {

        Complement() {
            super(new TreeRangeSet.ComplementRangesByLowerBound<>(TreeRangeSet.this.rangesByLowerBound), null);
        }

        @Override
        public void add(Range<C> range) {
            TreeRangeSet.this.remove(range);
        }

        @Override
        public void remove(Range<C> range) {
            TreeRangeSet.this.add(range);
        }

        @Override
        public boolean contains(C c0) {
            return !TreeRangeSet.this.contains(c0);
        }

        @Override
        public RangeSet<C> complement() {
            return TreeRangeSet.this;
        }
    }

    private static final class ComplementRangesByLowerBound<C extends Comparable<?>> extends AbstractNavigableMap<Cut<C>, Range<C>> {

        private final NavigableMap<Cut<C>, Range<C>> positiveRangesByLowerBound;
        private final NavigableMap<Cut<C>, Range<C>> positiveRangesByUpperBound;
        private final Range<Cut<C>> complementLowerBoundWindow;

        ComplementRangesByLowerBound(NavigableMap<Cut<C>, Range<C>> navigablemap) {
            this(navigablemap, Range.all());
        }

        private ComplementRangesByLowerBound(NavigableMap<Cut<C>, Range<C>> navigablemap, Range<Cut<C>> range) {
            this.positiveRangesByLowerBound = navigablemap;
            this.positiveRangesByUpperBound = new TreeRangeSet.RangesByUpperBound<>(navigablemap);
            this.complementLowerBoundWindow = range;
        }

        private NavigableMap<Cut<C>, Range<C>> subMap(Range<Cut<C>> range) {
            if (!this.complementLowerBoundWindow.isConnected(range)) {
                return ImmutableSortedMap.of();
            } else {
                range = range.intersection(this.complementLowerBoundWindow);
                return new TreeRangeSet.ComplementRangesByLowerBound<>(this.positiveRangesByLowerBound, range);
            }
        }

        public NavigableMap<Cut<C>, Range<C>> subMap(Cut<C> cut, boolean flag, Cut<C> cut1, boolean flag1) {
            return this.subMap(Range.range(cut, BoundType.forBoolean(flag), cut1, BoundType.forBoolean(flag1)));
        }

        public NavigableMap<Cut<C>, Range<C>> headMap(Cut<C> cut, boolean flag) {
            return this.subMap(Range.upTo(cut, BoundType.forBoolean(flag)));
        }

        public NavigableMap<Cut<C>, Range<C>> tailMap(Cut<C> cut, boolean flag) {
            return this.subMap(Range.downTo(cut, BoundType.forBoolean(flag)));
        }

        public Comparator<? super Cut<C>> comparator() {
            return Ordering.natural();
        }

        @Override
        Iterator<Entry<Cut<C>, Range<C>>> entryIterator() {
            Collection collection;

            if (this.complementLowerBoundWindow.hasLowerBound()) {
                collection = this.positiveRangesByUpperBound.tailMap(this.complementLowerBoundWindow.lowerEndpoint(), this.complementLowerBoundWindow.lowerBoundType() == BoundType.CLOSED).values();
            } else {
                collection = this.positiveRangesByUpperBound.values();
            }

            final PeekingIterator peekingiterator = Iterators.peekingIterator(collection.iterator());
            final Cut cut;

            if (this.complementLowerBoundWindow.contains(Cut.belowAll()) && (!peekingiterator.hasNext() || ((Range) peekingiterator.peek()).lowerBound != Cut.belowAll())) {
                cut = Cut.belowAll();
            } else {
                if (!peekingiterator.hasNext()) {
                    return Iterators.emptyIterator();
                }

                cut = ((Range) peekingiterator.next()).upperBound;
            }

            return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
                Cut<C> nextComplementRangeLowerBound = cut;

                @Override
                protected Entry<Cut<C>, Range<C>> computeNext() {
                    if (!ComplementRangesByLowerBound.this.complementLowerBoundWindow.upperBound.isLessThan(this.nextComplementRangeLowerBound) && this.nextComplementRangeLowerBound != Cut.aboveAll()) {
                        Range range;

                        if (peekingiterator.hasNext()) {
                            Range range1 = (Range) peekingiterator.next();

                            range = Range.create(this.nextComplementRangeLowerBound, range1.lowerBound);
                            this.nextComplementRangeLowerBound = range1.upperBound;
                        } else {
                            range = Range.create(this.nextComplementRangeLowerBound, Cut.aboveAll());
                            this.nextComplementRangeLowerBound = Cut.aboveAll();
                        }

                        return Maps.immutableEntry(range.lowerBound, range);
                    } else {
                        return (Entry) this.endOfData();
                    }
                }
            };
        }

        @Override
        Iterator<Entry<Cut<C>, Range<C>>> descendingEntryIterator() {
            Cut cut = this.complementLowerBoundWindow.hasUpperBound() ? (Cut) this.complementLowerBoundWindow.upperEndpoint() : Cut.aboveAll();
            boolean flag = this.complementLowerBoundWindow.hasUpperBound() && this.complementLowerBoundWindow.upperBoundType() == BoundType.CLOSED;
            final PeekingIterator peekingiterator = Iterators.peekingIterator(this.positiveRangesByUpperBound.headMap(cut, flag).descendingMap().values().iterator());
            Cut cut1;

            if (peekingiterator.hasNext()) {
                cut1 = ((Range) peekingiterator.peek()).upperBound == Cut.aboveAll() ? ((Range) peekingiterator.next()).lowerBound : (Cut) this.positiveRangesByLowerBound.higherKey(((Range) peekingiterator.peek()).upperBound);
            } else {
                if (!this.complementLowerBoundWindow.contains(Cut.belowAll()) || this.positiveRangesByLowerBound.containsKey(Cut.belowAll())) {
                    return Iterators.emptyIterator();
                }

                cut1 = (Cut) this.positiveRangesByLowerBound.higherKey(Cut.belowAll());
            }

            final Cut cut2 = (Cut) Objects.firstNonNull(cut1, Cut.aboveAll());

            return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
                Cut<C> nextComplementRangeUpperBound = cut2;

                @Override
                protected Entry<Cut<C>, Range<C>> computeNext() {
                    if (this.nextComplementRangeUpperBound == Cut.belowAll()) {
                        return (Entry) this.endOfData();
                    } else {
                        Range range;

                        if (peekingiterator.hasNext()) {
                            range = (Range) peekingiterator.next();
                            Range range1 = Range.create(range.upperBound, this.nextComplementRangeUpperBound);

                            this.nextComplementRangeUpperBound = range.lowerBound;
                            if (ComplementRangesByLowerBound.this.complementLowerBoundWindow.lowerBound.isLessThan(range1.lowerBound)) {
                                return Maps.immutableEntry(range1.lowerBound, range1);
                            }
                        } else if (ComplementRangesByLowerBound.this.complementLowerBoundWindow.lowerBound.isLessThan(Cut.belowAll())) {
                            range = Range.create(Cut.belowAll(), this.nextComplementRangeUpperBound);
                            this.nextComplementRangeUpperBound = Cut.belowAll();
                            return Maps.immutableEntry(Cut.belowAll(), range);
                        }

                        return (Entry) this.endOfData();
                    }
                }
            };
        }

        @Override
        public int size() {
            return Iterators.size(this.entryIterator());
        }

        @Nullable
        @Override
        public Range<C> get(Object object) {
            if (object instanceof Cut) {
                try {
                    Cut cut = (Cut) object;
                    Entry entry = this.tailMap(cut, true).firstEntry();

                    if (entry != null && ((Cut) entry.getKey()).equals(cut)) {
                        return (Range) entry.getValue();
                    }
                } catch (ClassCastException classcastexception) {
                    return null;
                }
            }

            return null;
        }

        public boolean containsKey(Object object) {
            return this.get(object) != null;
        }
    }

    @VisibleForTesting
    static final class RangesByUpperBound<C extends Comparable<?>> extends AbstractNavigableMap<Cut<C>, Range<C>> {

        private final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound;
        private final Range<Cut<C>> upperBoundWindow;

        RangesByUpperBound(NavigableMap<Cut<C>, Range<C>> navigablemap) {
            this.rangesByLowerBound = navigablemap;
            this.upperBoundWindow = Range.all();
        }

        private RangesByUpperBound(NavigableMap<Cut<C>, Range<C>> navigablemap, Range<Cut<C>> range) {
            this.rangesByLowerBound = navigablemap;
            this.upperBoundWindow = range;
        }

        private NavigableMap<Cut<C>, Range<C>> subMap(Range<Cut<C>> range) {
            return (NavigableMap) (range.isConnected(this.upperBoundWindow) ? new TreeRangeSet.RangesByUpperBound<>(this.rangesByLowerBound, range.intersection(this.upperBoundWindow)) : ImmutableSortedMap.of());
        }

        public NavigableMap<Cut<C>, Range<C>> subMap(Cut<C> cut, boolean flag, Cut<C> cut1, boolean flag1) {
            return this.subMap(Range.range(cut, BoundType.forBoolean(flag), cut1, BoundType.forBoolean(flag1)));
        }

        public NavigableMap<Cut<C>, Range<C>> headMap(Cut<C> cut, boolean flag) {
            return this.subMap(Range.upTo(cut, BoundType.forBoolean(flag)));
        }

        public NavigableMap<Cut<C>, Range<C>> tailMap(Cut<C> cut, boolean flag) {
            return this.subMap(Range.downTo(cut, BoundType.forBoolean(flag)));
        }

        public Comparator<? super Cut<C>> comparator() {
            return Ordering.natural();
        }

        public boolean containsKey(@Nullable Object object) {
            return this.get(object) != null;
        }

        @Override
        public Range<C> get(@Nullable Object object) {
            if (object instanceof Cut) {
                try {
                    Cut cut = (Cut) object;

                    if (!this.upperBoundWindow.contains(cut)) {
                        return null;
                    }

                    Entry entry = this.rangesByLowerBound.lowerEntry(cut);

                    if (entry != null && ((Range) entry.getValue()).upperBound.equals(cut)) {
                        return (Range) entry.getValue();
                    }
                } catch (ClassCastException classcastexception) {
                    return null;
                }
            }

            return null;
        }

        @Override
        Iterator<Entry<Cut<C>, Range<C>>> entryIterator() {
            final Iterator iterator;

            if (!this.upperBoundWindow.hasLowerBound()) {
                iterator = this.rangesByLowerBound.values().iterator();
            } else {
                Entry entry = this.rangesByLowerBound.lowerEntry(this.upperBoundWindow.lowerEndpoint());

                if (entry == null) {
                    iterator = this.rangesByLowerBound.values().iterator();
                } else if (this.upperBoundWindow.lowerBound.isLessThan(((Range) entry.getValue()).upperBound)) {
                    iterator = this.rangesByLowerBound.tailMap(entry.getKey(), true).values().iterator();
                } else {
                    iterator = this.rangesByLowerBound.tailMap(this.upperBoundWindow.lowerEndpoint(), true).values().iterator();
                }
            }

            return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
                @Override
                protected Entry<Cut<C>, Range<C>> computeNext() {
                    if (!iterator.hasNext()) {
                        return (Entry) this.endOfData();
                    } else {
                        Range range = (Range) iterator.next();

                        return RangesByUpperBound.this.upperBoundWindow.upperBound.isLessThan(range.upperBound) ? (Entry) this.endOfData() : Maps.immutableEntry(range.upperBound, range);
                    }
                }
            };
        }

        @Override
        Iterator<Entry<Cut<C>, Range<C>>> descendingEntryIterator() {
            Collection collection;

            if (this.upperBoundWindow.hasUpperBound()) {
                collection = this.rangesByLowerBound.headMap(this.upperBoundWindow.upperEndpoint(), false).descendingMap().values();
            } else {
                collection = this.rangesByLowerBound.descendingMap().values();
            }

            final PeekingIterator peekingiterator = Iterators.peekingIterator(collection.iterator());

            if (peekingiterator.hasNext() && this.upperBoundWindow.upperBound.isLessThan(((Range) peekingiterator.peek()).upperBound)) {
                peekingiterator.next();
            }

            return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
                @Override
                protected Entry<Cut<C>, Range<C>> computeNext() {
                    if (!peekingiterator.hasNext()) {
                        return (Entry) this.endOfData();
                    } else {
                        Range range = (Range) peekingiterator.next();

                        return RangesByUpperBound.this.upperBoundWindow.lowerBound.isLessThan(range.upperBound) ? Maps.immutableEntry(range.upperBound, range) : (Entry) this.endOfData();
                    }
                }
            };
        }

        @Override
        public int size() {
            return this.upperBoundWindow.equals(Range.all()) ? this.rangesByLowerBound.size() : Iterators.size(this.entryIterator());
        }

        public boolean isEmpty() {
            return this.upperBoundWindow.equals(Range.all()) ? this.rangesByLowerBound.isEmpty() : !this.entryIterator().hasNext();
        }
    }

    final class AsRanges extends ForwardingCollection<Range<C>> implements Set<Range<C>> {

        AsRanges() {}

        @Override
        protected Collection<Range<C>> delegate() {
            return TreeRangeSet.this.rangesByLowerBound.values();
        }

        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }

        public boolean equals(@Nullable Object object) {
            return Sets.equalsImpl(this, object);
        }
    }
}
