package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureStrongholdPieces$RoomCrossing extends StructureStrongholdPieces$Stronghold {

    private static final WeightedRandomChestContent[] field_75014_c = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10), new WeightedRandomChestContent(Items.field_151043_k, 0, 1, 3, 5), new WeightedRandomChestContent(Items.field_151137_ax, 0, 4, 9, 5), new WeightedRandomChestContent(Items.field_151044_h, 0, 3, 8, 10), new WeightedRandomChestContent(Items.field_151025_P, 0, 1, 3, 15), new WeightedRandomChestContent(Items.field_151034_e, 0, 1, 3, 15), new WeightedRandomChestContent(Items.field_151035_b, 0, 1, 1, 1)};
    protected int field_75013_b;

    public StructureStrongholdPieces$RoomCrossing() {}

    public StructureStrongholdPieces$RoomCrossing(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_143013_d = this.func_74988_a(random);
        this.field_74887_e = structureboundingbox;
        this.field_75013_b = random.nextInt(5);
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74768_a("Type", this.field_75013_b);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_75013_b = nbttagcompound.func_74762_e("Type");
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        this.func_74986_a((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 4, 1);
        this.func_74989_b((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 1, 4);
        this.func_74987_c((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 1, 4);
    }

    public static StructureStrongholdPieces$RoomCrossing func_75012_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -4, -1, 0, 11, 7, 11, l);

        return func_74991_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureStrongholdPieces$RoomCrossing(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            this.func_74882_a(world, structureboundingbox, 0, 0, 0, 10, 6, 10, true, random, StructureStrongholdPieces.access$200());
            this.func_74990_a(world, random, structureboundingbox, this.field_143013_d, 4, 1, 0);
            this.func_151549_a(world, structureboundingbox, 4, 1, 10, 6, 3, 10, Blocks.field_150350_a, Blocks.field_150350_a, false);
            this.func_151549_a(world, structureboundingbox, 0, 1, 4, 0, 3, 6, Blocks.field_150350_a, Blocks.field_150350_a, false);
            this.func_151549_a(world, structureboundingbox, 10, 1, 4, 10, 3, 6, Blocks.field_150350_a, Blocks.field_150350_a, false);
            int i;

            switch (this.field_75013_b) {
                case 0:
                    this.func_151550_a(world, Blocks.field_150417_aV, 0, 5, 1, 5, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150417_aV, 0, 5, 2, 5, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150417_aV, 0, 5, 3, 5, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150478_aa, 0, 4, 3, 5, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150478_aa, 0, 6, 3, 5, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150478_aa, 0, 5, 3, 4, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150478_aa, 0, 5, 3, 6, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150333_U, 0, 4, 1, 4, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150333_U, 0, 4, 1, 5, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150333_U, 0, 4, 1, 6, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150333_U, 0, 6, 1, 4, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150333_U, 0, 6, 1, 5, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150333_U, 0, 6, 1, 6, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150333_U, 0, 5, 1, 4, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150333_U, 0, 5, 1, 6, structureboundingbox);
                    break;
                case 1:
                    for (i = 0; i < 5; ++i) {
                        this.func_151550_a(world, Blocks.field_150417_aV, 0, 3, 1, 3 + i, structureboundingbox);
                        this.func_151550_a(world, Blocks.field_150417_aV, 0, 7, 1, 3 + i, structureboundingbox);
                        this.func_151550_a(world, Blocks.field_150417_aV, 0, 3 + i, 1, 3, structureboundingbox);
                        this.func_151550_a(world, Blocks.field_150417_aV, 0, 3 + i, 1, 7, structureboundingbox);
                    }

                    this.func_151550_a(world, Blocks.field_150417_aV, 0, 5, 1, 5, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150417_aV, 0, 5, 2, 5, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150417_aV, 0, 5, 3, 5, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150358_i, 0, 5, 4, 5, structureboundingbox);
                    break;
                case 2:
                    for (i = 1; i <= 9; ++i) {
                        this.func_151550_a(world, Blocks.field_150347_e, 0, 1, 3, i, structureboundingbox);
                        this.func_151550_a(world, Blocks.field_150347_e, 0, 9, 3, i, structureboundingbox);
                    }

                    for (i = 1; i <= 9; ++i) {
                        this.func_151550_a(world, Blocks.field_150347_e, 0, i, 3, 1, structureboundingbox);
                        this.func_151550_a(world, Blocks.field_150347_e, 0, i, 3, 9, structureboundingbox);
                    }

                    this.func_151550_a(world, Blocks.field_150347_e, 0, 5, 1, 4, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150347_e, 0, 5, 1, 6, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150347_e, 0, 5, 3, 4, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150347_e, 0, 5, 3, 6, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150347_e, 0, 4, 1, 5, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150347_e, 0, 6, 1, 5, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150347_e, 0, 4, 3, 5, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150347_e, 0, 6, 3, 5, structureboundingbox);

                    for (i = 1; i <= 3; ++i) {
                        this.func_151550_a(world, Blocks.field_150347_e, 0, 4, i, 4, structureboundingbox);
                        this.func_151550_a(world, Blocks.field_150347_e, 0, 6, i, 4, structureboundingbox);
                        this.func_151550_a(world, Blocks.field_150347_e, 0, 4, i, 6, structureboundingbox);
                        this.func_151550_a(world, Blocks.field_150347_e, 0, 6, i, 6, structureboundingbox);
                    }

                    this.func_151550_a(world, Blocks.field_150478_aa, 0, 5, 3, 5, structureboundingbox);

                    for (i = 2; i <= 8; ++i) {
                        this.func_151550_a(world, Blocks.field_150344_f, 0, 2, 3, i, structureboundingbox);
                        this.func_151550_a(world, Blocks.field_150344_f, 0, 3, 3, i, structureboundingbox);
                        if (i <= 3 || i >= 7) {
                            this.func_151550_a(world, Blocks.field_150344_f, 0, 4, 3, i, structureboundingbox);
                            this.func_151550_a(world, Blocks.field_150344_f, 0, 5, 3, i, structureboundingbox);
                            this.func_151550_a(world, Blocks.field_150344_f, 0, 6, 3, i, structureboundingbox);
                        }

                        this.func_151550_a(world, Blocks.field_150344_f, 0, 7, 3, i, structureboundingbox);
                        this.func_151550_a(world, Blocks.field_150344_f, 0, 8, 3, i, structureboundingbox);
                    }

                    this.func_151550_a(world, Blocks.field_150468_ap, this.func_151555_a(Blocks.field_150468_ap, 4), 9, 1, 3, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150468_ap, this.func_151555_a(Blocks.field_150468_ap, 4), 9, 2, 3, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150468_ap, this.func_151555_a(Blocks.field_150468_ap, 4), 9, 3, 3, structureboundingbox);
                    this.func_74879_a(world, structureboundingbox, random, 3, 4, 8, WeightedRandomChestContent.func_92080_a(StructureStrongholdPieces$RoomCrossing.field_75014_c, Items.field_151134_bR.func_92114_b(random)), 1 + random.nextInt(4));
            }

            return true;
        }
    }
}
