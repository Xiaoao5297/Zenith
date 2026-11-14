package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureStrongholdPieces$ChestCorridor extends StructureStrongholdPieces$Stronghold {

    private static final WeightedRandomChestContent[] field_75003_a = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151079_bi, 0, 1, 1, 10), new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 3, 3), new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10), new WeightedRandomChestContent(Items.field_151043_k, 0, 1, 3, 5), new WeightedRandomChestContent(Items.field_151137_ax, 0, 4, 9, 5), new WeightedRandomChestContent(Items.field_151025_P, 0, 1, 3, 15), new WeightedRandomChestContent(Items.field_151034_e, 0, 1, 3, 15), new WeightedRandomChestContent(Items.field_151035_b, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151040_l, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151030_Z, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151028_Y, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151165_aa, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151167_ab, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151153_ao, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151136_bY, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151125_bZ, 0, 1, 1, 1)};
    private boolean field_75002_c;

    public StructureStrongholdPieces$ChestCorridor() {}

    public StructureStrongholdPieces$ChestCorridor(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_143013_d = this.func_74988_a(random);
        this.field_74887_e = structureboundingbox;
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74757_a("Chest", this.field_75002_c);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_75002_c = nbttagcompound.func_74767_n("Chest");
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        this.func_74986_a((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 1, 1);
    }

    public static StructureStrongholdPieces$ChestCorridor func_75000_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -1, -1, 0, 5, 5, 7, l);

        return func_74991_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureStrongholdPieces$ChestCorridor(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            this.func_74882_a(world, structureboundingbox, 0, 0, 0, 4, 4, 6, true, random, StructureStrongholdPieces.access$200());
            this.func_74990_a(world, random, structureboundingbox, this.field_143013_d, 1, 1, 0);
            this.func_74990_a(world, random, structureboundingbox, StructureStrongholdPieces$Stronghold$Door.OPENING, 1, 1, 6);
            this.func_151549_a(world, structureboundingbox, 3, 1, 2, 3, 1, 4, Blocks.field_150417_aV, Blocks.field_150417_aV, false);
            this.func_151550_a(world, Blocks.field_150333_U, 5, 3, 1, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150333_U, 5, 3, 1, 5, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150333_U, 5, 3, 2, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150333_U, 5, 3, 2, 4, structureboundingbox);

            int i;

            for (i = 2; i <= 4; ++i) {
                this.func_151550_a(world, Blocks.field_150333_U, 5, 2, 1, i, structureboundingbox);
            }

            if (!this.field_75002_c) {
                i = this.func_74862_a(2);
                int j = this.func_74865_a(3, 3);
                int k = this.func_74873_b(3, 3);

                if (structureboundingbox.func_78890_b(j, i, k)) {
                    this.field_75002_c = true;
                    this.func_74879_a(world, structureboundingbox, random, 3, 2, 3, WeightedRandomChestContent.func_92080_a(StructureStrongholdPieces$ChestCorridor.field_75003_a, Items.field_151134_bR.func_92114_b(random)), 2 + random.nextInt(2));
                }
            }

            return true;
        }
    }
}
