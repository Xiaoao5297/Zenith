package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class BlockFlower extends BlockBush {

    private static final String[][] field_149860_M = new String[][]{{"flower_dandelion"}, {"flower_rose", "flower_blue_orchid", "flower_allium", "flower_houstonia", "flower_tulip_red", "flower_tulip_orange", "flower_tulip_white", "flower_tulip_pink", "flower_oxeye_daisy"}};
    public static final String[] field_149859_a = new String[]{"poppy", "blueOrchid", "allium", "houstonia", "tulipRed", "tulipOrange", "tulipWhite", "tulipPink", "oxeyeDaisy"};
    public static final String[] field_149858_b = new String[]{"dandelion"};
    private int field_149862_O;

    protected BlockFlower(int i) {
        super(Material.field_151585_k);
        this.field_149862_O = i;
    }

    @Override
    public int func_149692_a(int i) {
        return i;
    }

    public static BlockFlower func_149857_e(String s) {
        String[] astring = BlockFlower.field_149858_b;
        int i = astring.length;

        String s1;
        int j;

        for (j = 0; j < i; ++j) {
            s1 = astring[j];
            if (s1.equals(s)) {
                return Blocks.field_150327_N;
            }
        }

        astring = BlockFlower.field_149859_a;
        i = astring.length;

        for (j = 0; j < i; ++j) {
            s1 = astring[j];
            if (s1.equals(s)) {
                return Blocks.field_150328_O;
            }
        }

        return null;
    }

    public static int func_149856_f(String s) {
        int i;

        for (i = 0; i < BlockFlower.field_149858_b.length; ++i) {
            if (BlockFlower.field_149858_b[i].equals(s)) {
                return i;
            }
        }

        for (i = 0; i < BlockFlower.field_149859_a.length; ++i) {
            if (BlockFlower.field_149859_a[i].equals(s)) {
                return i;
            }
        }

        return 0;
    }
}
