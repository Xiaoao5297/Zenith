package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureStrongholdPieces$Library extends StructureStrongholdPieces$Stronghold {

    private static final WeightedRandomChestContent[] field_75007_b = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151122_aG, 0, 1, 3, 20), new WeightedRandomChestContent(Items.field_151121_aF, 0, 2, 7, 20), new WeightedRandomChestContent(Items.field_151148_bJ, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151111_aL, 0, 1, 1, 1)};
    private boolean field_75008_c;

    public StructureStrongholdPieces$Library() {}

    public StructureStrongholdPieces$Library(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_143013_d = this.func_74988_a(random);
        this.field_74887_e = structureboundingbox;
        this.field_75008_c = structureboundingbox.func_78882_c() > 6;
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74757_a("Tall", this.field_75008_c);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_75008_c = nbttagcompound.func_74767_n("Tall");
    }

    public static StructureStrongholdPieces$Library func_75006_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -4, -1, 0, 14, 11, 15, l);

        if (!func_74991_a(structureboundingbox) || StructureComponent.func_74883_a(list, structureboundingbox) != null) {
            structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -4, -1, 0, 14, 6, 15, l);
            if (!func_74991_a(structureboundingbox) || StructureComponent.func_74883_a(list, structureboundingbox) != null) {
                return null;
            }
        }

        return new StructureStrongholdPieces$Library(i1, random, structureboundingbox, l);
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            byte b0 = 11;

            if (!this.field_75008_c) {
                b0 = 6;
            }

            this.func_74882_a(world, structureboundingbox, 0, 0, 0, 13, b0 - 1, 14, true, random, StructureStrongholdPieces.access$200());
            this.func_74990_a(world, random, structureboundingbox, this.field_143013_d, 4, 1, 0);
            this.func_151551_a(world, structureboundingbox, random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.field_150321_G, Blocks.field_150321_G, false);
            boolean flag = true;
            boolean flag1 = true;

            int i;

            for (i = 1; i <= 13; ++i) {
                if ((i - 1) % 4 == 0) {
                    this.func_151549_a(world, structureboundingbox, 1, 1, i, 1, 4, i, Blocks.field_150344_f, Blocks.field_150344_f, false);
                    this.func_151549_a(world, structureboundingbox, 12, 1, i, 12, 4, i, Blocks.field_150344_f, Blocks.field_150344_f, false);
                    this.func_151550_a(world, Blocks.field_150478_aa, 0, 2, 3, i, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150478_aa, 0, 11, 3, i, structureboundingbox);
                    if (this.field_75008_c) {
                        this.func_151549_a(world, structureboundingbox, 1, 6, i, 1, 9, i, Blocks.field_150344_f, Blocks.field_150344_f, false);
                        this.func_151549_a(world, structureboundingbox, 12, 6, i, 12, 9, i, Blocks.field_150344_f, Blocks.field_150344_f, false);
                    }
                } else {
                    this.func_151549_a(world, structureboundingbox, 1, 1, i, 1, 4, i, Blocks.field_150342_X, Blocks.field_150342_X, false);
                    this.func_151549_a(world, structureboundingbox, 12, 1, i, 12, 4, i, Blocks.field_150342_X, Blocks.field_150342_X, false);
                    if (this.field_75008_c) {
                        this.func_151549_a(world, structureboundingbox, 1, 6, i, 1, 9, i, Blocks.field_150342_X, Blocks.field_150342_X, false);
                        this.func_151549_a(world, structureboundingbox, 12, 6, i, 12, 9, i, Blocks.field_150342_X, Blocks.field_150342_X, false);
                    }
                }
            }

            for (i = 3; i < 12; i += 2) {
                this.func_151549_a(world, structureboundingbox, 3, 1, i, 4, 3, i, Blocks.field_150342_X, Blocks.field_150342_X, false);
                this.func_151549_a(world, structureboundingbox, 6, 1, i, 7, 3, i, Blocks.field_150342_X, Blocks.field_150342_X, false);
                this.func_151549_a(world, structureboundingbox, 9, 1, i, 10, 3, i, Blocks.field_150342_X, Blocks.field_150342_X, false);
            }

            if (this.field_75008_c) {
                this.func_151549_a(world, structureboundingbox, 1, 5, 1, 3, 5, 13, Blocks.field_150344_f, Blocks.field_150344_f, false);
                this.func_151549_a(world, structureboundingbox, 10, 5, 1, 12, 5, 13, Blocks.field_150344_f, Blocks.field_150344_f, false);
                this.func_151549_a(world, structureboundingbox, 4, 5, 1, 9, 5, 2, Blocks.field_150344_f, Blocks.field_150344_f, false);
                this.func_151549_a(world, structureboundingbox, 4, 5, 12, 9, 5, 13, Blocks.field_150344_f, Blocks.field_150344_f, false);
                this.func_151550_a(world, Blocks.field_150344_f, 0, 9, 5, 11, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150344_f, 0, 8, 5, 11, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150344_f, 0, 9, 5, 10, structureboundingbox);
                this.func_151549_a(world, structureboundingbox, 3, 6, 2, 3, 6, 12, Blocks.field_150422_aJ, Blocks.field_150422_aJ, false);
                this.func_151549_a(world, structureboundingbox, 10, 6, 2, 10, 6, 10, Blocks.field_150422_aJ, Blocks.field_150422_aJ, false);
                this.func_151549_a(world, structureboundingbox, 4, 6, 2, 9, 6, 2, Blocks.field_150422_aJ, Blocks.field_150422_aJ, false);
                this.func_151549_a(world, structureboundingbox, 4, 6, 12, 8, 6, 12, Blocks.field_150422_aJ, Blocks.field_150422_aJ, false);
                this.func_151550_a(world, Blocks.field_150422_aJ, 0, 9, 6, 11, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150422_aJ, 0, 8, 6, 11, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150422_aJ, 0, 9, 6, 10, structureboundingbox);
                i = this.func_151555_a(Blocks.field_150468_ap, 3);
                this.func_151550_a(world, Blocks.field_150468_ap, i, 10, 1, 13, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150468_ap, i, 10, 2, 13, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150468_ap, i, 10, 3, 13, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150468_ap, i, 10, 4, 13, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150468_ap, i, 10, 5, 13, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150468_ap, i, 10, 6, 13, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150468_ap, i, 10, 7, 13, structureboundingbox);
                byte b1 = 7;
                byte b2 = 7;

                this.func_151550_a(world, Blocks.field_150422_aJ, 0, b1 - 1, 9, b2, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150422_aJ, 0, b1, 9, b2, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150422_aJ, 0, b1 - 1, 8, b2, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150422_aJ, 0, b1, 8, b2, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150422_aJ, 0, b1 - 1, 7, b2, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150422_aJ, 0, b1, 7, b2, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150422_aJ, 0, b1 - 2, 7, b2, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150422_aJ, 0, b1 + 1, 7, b2, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150422_aJ, 0, b1 - 1, 7, b2 - 1, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150422_aJ, 0, b1 - 1, 7, b2 + 1, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150422_aJ, 0, b1, 7, b2 - 1, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150422_aJ, 0, b1, 7, b2 + 1, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150478_aa, 0, b1 - 2, 8, b2, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150478_aa, 0, b1 + 1, 8, b2, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150478_aa, 0, b1 - 1, 8, b2 - 1, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150478_aa, 0, b1 - 1, 8, b2 + 1, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150478_aa, 0, b1, 8, b2 - 1, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150478_aa, 0, b1, 8, b2 + 1, structureboundingbox);
            }

            this.func_74879_a(world, structureboundingbox, random, 3, 3, 5, WeightedRandomChestContent.func_92080_a(StructureStrongholdPieces$Library.field_75007_b, Items.field_151134_bR.func_92112_a(random, 1, 5, 2)), 1 + random.nextInt(4));
            if (this.field_75008_c) {
                this.func_151550_a(world, Blocks.field_150350_a, 0, 12, 9, 1, structureboundingbox);
                this.func_74879_a(world, structureboundingbox, random, 12, 8, 1, WeightedRandomChestContent.func_92080_a(StructureStrongholdPieces$Library.field_75007_b, Items.field_151134_bR.func_92112_a(random, 1, 5, 2)), 1 + random.nextInt(4));
            }

            return true;
        }
    }
}
