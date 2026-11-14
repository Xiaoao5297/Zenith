package net.minecraft.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class WeightedRandom {

    public static int func_76272_a(Collection collection) {
        int i = 0;

        WeightedRandom$Item weightedrandom$item;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); i += weightedrandom$item.field_76292_a) {
            weightedrandom$item = (WeightedRandom$Item) iterator.next();
        }

        return i;
    }

    public static WeightedRandom$Item func_76273_a(Random random, Collection collection, int i) {
        if (i <= 0) {
            throw new IllegalArgumentException();
        } else {
            int j = random.nextInt(i);
            Iterator iterator = collection.iterator();

            WeightedRandom$Item weightedrandom$item;

            do {
                if (!iterator.hasNext()) {
                    return null;
                }

                weightedrandom$item = (WeightedRandom$Item) iterator.next();
                j -= weightedrandom$item.field_76292_a;
            } while (j >= 0);

            return weightedrandom$item;
        }
    }

    public static WeightedRandom$Item func_76271_a(Random random, Collection collection) {
        return func_76273_a(random, collection, func_76272_a(collection));
    }

    public static int func_76270_a(WeightedRandom$Item[] aweightedrandom$item) {
        int i = 0;
        WeightedRandom$Item[] aweightedrandom$item1 = aweightedrandom$item;
        int j = aweightedrandom$item.length;

        for (int k = 0; k < j; ++k) {
            WeightedRandom$Item weightedrandom$item = aweightedrandom$item1[k];

            i += weightedrandom$item.field_76292_a;
        }

        return i;
    }

    public static WeightedRandom$Item func_76269_a(Random random, WeightedRandom$Item[] aweightedrandom$item, int i) {
        if (i <= 0) {
            throw new IllegalArgumentException();
        } else {
            int j = random.nextInt(i);
            WeightedRandom$Item[] aweightedrandom$item1 = aweightedrandom$item;
            int k = aweightedrandom$item.length;

            for (int l = 0; l < k; ++l) {
                WeightedRandom$Item weightedrandom$item = aweightedrandom$item1[l];

                j -= weightedrandom$item.field_76292_a;
                if (j < 0) {
                    return weightedrandom$item;
                }
            }

            return null;
        }
    }

    public static WeightedRandom$Item func_76274_a(Random random, WeightedRandom$Item[] aweightedrandom$item) {
        return func_76269_a(random, aweightedrandom$item, func_76270_a(aweightedrandom$item));
    }
}
