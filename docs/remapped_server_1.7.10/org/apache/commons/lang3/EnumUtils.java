package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EnumUtils {

    private static final String NULL_ELEMENTS_NOT_PERMITTED = "null elements not permitted";
    private static final String CANNOT_STORE_S_S_VALUES_IN_S_BITS = "Cannot store %s %s values in %s bits";
    private static final String S_DOES_NOT_SEEM_TO_BE_AN_ENUM_TYPE = "%s does not seem to be an Enum type";
    private static final String ENUM_CLASS_MUST_BE_DEFINED = "EnumClass must be defined.";

    public EnumUtils() {}

    public static <E extends Enum<E>> Map<String, E> getEnumMap(Class<E> oclass) {
        LinkedHashMap linkedhashmap = new LinkedHashMap();
        Enum[] aenum = (Enum[]) oclass.getEnumConstants();
        int i = aenum.length;

        for (int j = 0; j < i; ++j) {
            Enum oenum = aenum[j];

            linkedhashmap.put(oenum.name(), oenum);
        }

        return linkedhashmap;
    }

    public static <E extends Enum<E>> List<E> getEnumList(Class<E> oclass) {
        return new ArrayList(Arrays.asList(oclass.getEnumConstants()));
    }

    public static <E extends Enum<E>> boolean isValidEnum(Class<E> oclass, String s) {
        if (s == null) {
            return false;
        } else {
            try {
                Enum.valueOf(oclass, s);
                return true;
            } catch (IllegalArgumentException illegalargumentexception) {
                return false;
            }
        }
    }

    public static <E extends Enum<E>> E getEnum(Class<E> oclass, String s) {
        if (s == null) {
            return null;
        } else {
            try {
                return Enum.valueOf(oclass, s);
            } catch (IllegalArgumentException illegalargumentexception) {
                return null;
            }
        }
    }

    public static <E extends Enum<E>> long generateBitVector(Class<E> oclass, Iterable<E> iterable) {
        checkBitVectorable(oclass);
        Validate.notNull(iterable);
        long i = 0L;

        Enum oenum;

        for (Iterator iterator = iterable.iterator(); iterator.hasNext(); i |= (long) (1 << oenum.ordinal())) {
            oenum = (Enum) iterator.next();
            Validate.isTrue(oenum != null, "null elements not permitted");
        }

        return i;
    }

    public static <E extends Enum<E>> long[] generateBitVectors(Class<E> oclass, Iterable<E> iterable) {
        asEnum(oclass);
        Validate.notNull(iterable);
        EnumSet enumset = EnumSet.noneOf(oclass);
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Enum oenum = (Enum) iterator.next();

            Validate.isTrue(oenum != null, "null elements not permitted");
            enumset.add(oenum);
        }

        long[] along = new long[(((Enum[]) oclass.getEnumConstants()).length - 1) / 64 + 1];

        Enum oenum1;
        int i;

        for (Iterator iterator1 = enumset.iterator(); iterator1.hasNext(); along[i] |= (long) (1 << oenum1.ordinal() % 64)) {
            oenum1 = (Enum) iterator1.next();
            i = oenum1.ordinal() / 64;
        }

        ArrayUtils.reverse(along);
        return along;
    }

    public static <E extends Enum<E>> long generateBitVector(Class<E> oclass, E... ae) {
        Validate.noNullElements((Object[]) ae);
        return generateBitVector(oclass, (Iterable) Arrays.asList(ae));
    }

    public static <E extends Enum<E>> long[] generateBitVectors(Class<E> oclass, E... ae) {
        asEnum(oclass);
        Validate.noNullElements((Object[]) ae);
        EnumSet enumset = EnumSet.noneOf(oclass);

        Collections.addAll(enumset, ae);
        long[] along = new long[(((Enum[]) oclass.getEnumConstants()).length - 1) / 64 + 1];

        Enum oenum;
        int i;

        for (Iterator iterator = enumset.iterator(); iterator.hasNext(); along[i] |= (long) (1 << oenum.ordinal() % 64)) {
            oenum = (Enum) iterator.next();
            i = oenum.ordinal() / 64;
        }

        ArrayUtils.reverse(along);
        return along;
    }

    public static <E extends Enum<E>> EnumSet<E> processBitVector(Class<E> oclass, long i) {
        checkBitVectorable(oclass).getEnumConstants();
        return processBitVectors(oclass, i);
    }

    public static <E extends Enum<E>> EnumSet<E> processBitVectors(Class<E> oclass, long... along) {
        EnumSet enumset = EnumSet.noneOf(asEnum(oclass));

        along = ArrayUtils.clone((long[]) Validate.notNull(along));
        ArrayUtils.reverse(along);
        Enum[] aenum = (Enum[]) oclass.getEnumConstants();
        int i = aenum.length;

        for (int j = 0; j < i; ++j) {
            Enum oenum = aenum[j];
            int k = oenum.ordinal() / 64;

            if (k < along.length && (along[k] & (long) (1 << oenum.ordinal() % 64)) != 0L) {
                enumset.add(oenum);
            }
        }

        return enumset;
    }

    private static <E extends Enum<E>> Class<E> checkBitVectorable(Class<E> oclass) {
        Enum[] aenum = (Enum[]) asEnum(oclass).getEnumConstants();

        Validate.isTrue(aenum.length <= 64, "Cannot store %s %s values in %s bits", aenum.length, oclass.getSimpleName(), 64);
        return oclass;
    }

    private static <E extends Enum<E>> Class<E> asEnum(Class<E> oclass) {
        Validate.notNull(oclass, "EnumClass must be defined.");
        Validate.isTrue(oclass.isEnum(), "%s does not seem to be an Enum type", oclass);
        return oclass;
    }
}
