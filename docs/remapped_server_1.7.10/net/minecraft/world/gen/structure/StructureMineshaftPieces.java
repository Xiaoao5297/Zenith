package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.WeightedRandomChestContent;

public class StructureMineshaftPieces {

    private static final WeightedRandomChestContent[] field_78818_a = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10), new WeightedRandomChestContent(Items.field_151043_k, 0, 1, 3, 5), new WeightedRandomChestContent(Items.field_151137_ax, 0, 4, 9, 5), new WeightedRandomChestContent(Items.field_151100_aR, 4, 4, 9, 5), new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 2, 3), new WeightedRandomChestContent(Items.field_151044_h, 0, 3, 8, 10), new WeightedRandomChestContent(Items.field_151025_P, 0, 1, 3, 15), new WeightedRandomChestContent(Items.field_151035_b, 0, 1, 1, 1), new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150448_aq), 0, 4, 8, 1), new WeightedRandomChestContent(Items.field_151081_bc, 0, 2, 4, 10), new WeightedRandomChestContent(Items.field_151080_bb, 0, 2, 4, 10), new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 3), new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 1)};

    public static void func_143048_a() {
        MapGenStructureIO.func_143031_a(StructureMineshaftPieces$Corridor.class, "MSCorridor");
        MapGenStructureIO.func_143031_a(StructureMineshaftPieces$Cross.class, "MSCrossing");
        MapGenStructureIO.func_143031_a(StructureMineshaftPieces$Room.class, "MSRoom");
        MapGenStructureIO.func_143031_a(StructureMineshaftPieces$Stairs.class, "MSStairs");
    }

    private static StructureComponent func_78815_a(List list, Random random, int i, int j, int k, int l, int i1) {
        int j1 = random.nextInt(100);
        StructureBoundingBox structureboundingbox;

        if (j1 >= 80) {
            structureboundingbox = StructureMineshaftPieces$Cross.func_74951_a(list, random, i, j, k, l);
            if (structureboundingbox != null) {
                return new StructureMineshaftPieces$Cross(i1, random, structureboundingbox, l);
            }
        } else if (j1 >= 70) {
            structureboundingbox = StructureMineshaftPieces$Stairs.func_74950_a(list, random, i, j, k, l);
            if (structureboundingbox != null) {
                return new StructureMineshaftPieces$Stairs(i1, random, structureboundingbox, l);
            }
        } else {
            structureboundingbox = StructureMineshaftPieces$Corridor.func_74954_a(list, random, i, j, k, l);
            if (structureboundingbox != null) {
                return new StructureMineshaftPieces$Corridor(i1, random, structureboundingbox, l);
            }
        }

        return null;
    }

    private static StructureComponent func_78817_b(StructureComponent structurecomponent, List list, Random random, int i, int j, int k, int l, int i1) {
        if (i1 > 8) {
            return null;
        } else if (Math.abs(i - structurecomponent.func_74874_b().field_78897_a) <= 80 && Math.abs(k - structurecomponent.func_74874_b().field_78896_c) <= 80) {
            StructureComponent structurecomponent1 = func_78815_a(list, random, i, j, k, l, i1 + 1);

            if (structurecomponent1 != null) {
                list.add(structurecomponent1);
                structurecomponent1.func_74861_a(structurecomponent, list, random);
            }

            return structurecomponent1;
        } else {
            return null;
        }
    }
}
