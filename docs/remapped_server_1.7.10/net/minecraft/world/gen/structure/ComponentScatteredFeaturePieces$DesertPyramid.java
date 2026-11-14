package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Direction;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class ComponentScatteredFeaturePieces$DesertPyramid extends ComponentScatteredFeaturePieces$Feature {

    private boolean[] field_74940_h = new boolean[4];
    private static final WeightedRandomChestContent[] field_74941_i = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 3, 3), new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10), new WeightedRandomChestContent(Items.field_151043_k, 0, 2, 7, 15), new WeightedRandomChestContent(Items.field_151166_bC, 0, 1, 3, 2), new WeightedRandomChestContent(Items.field_151103_aS, 0, 4, 6, 20), new WeightedRandomChestContent(Items.field_151078_bh, 0, 3, 7, 16), new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 3), new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151136_bY, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151125_bZ, 0, 1, 1, 1)};

    public ComponentScatteredFeaturePieces$DesertPyramid() {}

    public ComponentScatteredFeaturePieces$DesertPyramid(Random random, int i, int j) {
        super(random, i, 64, j, 21, 15, 21);
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74757_a("hasPlacedChest0", this.field_74940_h[0]);
        nbttagcompound.func_74757_a("hasPlacedChest1", this.field_74940_h[1]);
        nbttagcompound.func_74757_a("hasPlacedChest2", this.field_74940_h[2]);
        nbttagcompound.func_74757_a("hasPlacedChest3", this.field_74940_h[3]);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_74940_h[0] = nbttagcompound.func_74767_n("hasPlacedChest0");
        this.field_74940_h[1] = nbttagcompound.func_74767_n("hasPlacedChest1");
        this.field_74940_h[2] = nbttagcompound.func_74767_n("hasPlacedChest2");
        this.field_74940_h[3] = nbttagcompound.func_74767_n("hasPlacedChest3");
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        this.func_151549_a(world, structureboundingbox, 0, -4, 0, this.field_74939_a - 1, 0, this.field_74938_c - 1, Blocks.field_150322_A, Blocks.field_150322_A, false);

        int i;

        for (i = 1; i <= 9; ++i) {
            this.func_151549_a(world, structureboundingbox, i, i, i, this.field_74939_a - 1 - i, i, this.field_74938_c - 1 - i, Blocks.field_150322_A, Blocks.field_150322_A, false);
            this.func_151549_a(world, structureboundingbox, i + 1, i, i + 1, this.field_74939_a - 2 - i, i, this.field_74938_c - 2 - i, Blocks.field_150350_a, Blocks.field_150350_a, false);
        }

        int j;
        int k;

        for (i = 0; i < this.field_74939_a; ++i) {
            for (k = 0; k < this.field_74938_c; ++k) {
                j = -5;
                this.func_151554_b(world, Blocks.field_150322_A, 0, i, j, k, structureboundingbox);
            }
        }

        i = this.func_151555_a(Blocks.field_150372_bz, 3);
        k = this.func_151555_a(Blocks.field_150372_bz, 2);
        j = this.func_151555_a(Blocks.field_150372_bz, 0);
        int l = this.func_151555_a(Blocks.field_150372_bz, 1);
        byte b0 = 1;
        byte b1 = 11;

        this.func_151549_a(world, structureboundingbox, 0, 0, 0, 4, 9, 4, Blocks.field_150322_A, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 1, 10, 1, 3, 10, 3, Blocks.field_150322_A, Blocks.field_150322_A, false);
        this.func_151550_a(world, Blocks.field_150372_bz, i, 2, 10, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150372_bz, k, 2, 10, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150372_bz, j, 0, 10, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150372_bz, l, 4, 10, 2, structureboundingbox);
        this.func_151549_a(world, structureboundingbox, this.field_74939_a - 5, 0, 0, this.field_74939_a - 1, 9, 4, Blocks.field_150322_A, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, this.field_74939_a - 4, 10, 1, this.field_74939_a - 2, 10, 3, Blocks.field_150322_A, Blocks.field_150322_A, false);
        this.func_151550_a(world, Blocks.field_150372_bz, i, this.field_74939_a - 3, 10, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150372_bz, k, this.field_74939_a - 3, 10, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150372_bz, j, this.field_74939_a - 5, 10, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150372_bz, l, this.field_74939_a - 1, 10, 2, structureboundingbox);
        this.func_151549_a(world, structureboundingbox, 8, 0, 0, 12, 4, 4, Blocks.field_150322_A, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 9, 1, 0, 11, 3, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151550_a(world, Blocks.field_150322_A, 2, 9, 1, 1, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 2, 9, 2, 1, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 2, 9, 3, 1, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 2, 10, 3, 1, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 2, 11, 3, 1, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 2, 11, 2, 1, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 2, 11, 1, 1, structureboundingbox);
        this.func_151549_a(world, structureboundingbox, 4, 1, 1, 8, 3, 3, Blocks.field_150322_A, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 4, 1, 2, 8, 2, 2, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 12, 1, 1, 16, 3, 3, Blocks.field_150322_A, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 12, 1, 2, 16, 2, 2, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 5, 4, 5, this.field_74939_a - 6, 4, this.field_74938_c - 6, Blocks.field_150322_A, Blocks.field_150322_A, false);
        this.func_151549_a(world, structureboundingbox, 9, 4, 9, 11, 4, 11, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151556_a(world, structureboundingbox, 8, 1, 8, 8, 3, 8, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
        this.func_151556_a(world, structureboundingbox, 12, 1, 8, 12, 3, 8, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
        this.func_151556_a(world, structureboundingbox, 8, 1, 12, 8, 3, 12, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
        this.func_151556_a(world, structureboundingbox, 12, 1, 12, 12, 3, 12, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
        this.func_151549_a(world, structureboundingbox, 1, 1, 5, 4, 4, 11, Blocks.field_150322_A, Blocks.field_150322_A, false);
        this.func_151549_a(world, structureboundingbox, this.field_74939_a - 5, 1, 5, this.field_74939_a - 2, 4, 11, Blocks.field_150322_A, Blocks.field_150322_A, false);
        this.func_151549_a(world, structureboundingbox, 6, 7, 9, 6, 7, 11, Blocks.field_150322_A, Blocks.field_150322_A, false);
        this.func_151549_a(world, structureboundingbox, this.field_74939_a - 7, 7, 9, this.field_74939_a - 7, 7, 11, Blocks.field_150322_A, Blocks.field_150322_A, false);
        this.func_151556_a(world, structureboundingbox, 5, 5, 9, 5, 7, 11, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
        this.func_151556_a(world, structureboundingbox, this.field_74939_a - 6, 5, 9, this.field_74939_a - 6, 7, 11, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 5, 5, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 5, 6, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 6, 6, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, this.field_74939_a - 6, 5, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, this.field_74939_a - 6, 6, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, this.field_74939_a - 7, 6, 10, structureboundingbox);
        this.func_151549_a(world, structureboundingbox, 2, 4, 4, 2, 6, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, this.field_74939_a - 3, 4, 4, this.field_74939_a - 3, 6, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151550_a(world, Blocks.field_150372_bz, i, 2, 4, 5, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150372_bz, i, 2, 3, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150372_bz, i, this.field_74939_a - 3, 4, 5, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150372_bz, i, this.field_74939_a - 3, 3, 4, structureboundingbox);
        this.func_151549_a(world, structureboundingbox, 1, 1, 3, 2, 2, 3, Blocks.field_150322_A, Blocks.field_150322_A, false);
        this.func_151549_a(world, structureboundingbox, this.field_74939_a - 3, 1, 3, this.field_74939_a - 2, 2, 3, Blocks.field_150322_A, Blocks.field_150322_A, false);
        this.func_151550_a(world, Blocks.field_150372_bz, 0, 1, 1, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150372_bz, 0, this.field_74939_a - 2, 1, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150333_U, 1, 1, 2, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150333_U, 1, this.field_74939_a - 2, 2, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150372_bz, l, 2, 1, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150372_bz, j, this.field_74939_a - 3, 1, 2, structureboundingbox);
        this.func_151549_a(world, structureboundingbox, 4, 3, 5, 4, 3, 18, Blocks.field_150322_A, Blocks.field_150322_A, false);
        this.func_151549_a(world, structureboundingbox, this.field_74939_a - 5, 3, 5, this.field_74939_a - 5, 3, 17, Blocks.field_150322_A, Blocks.field_150322_A, false);
        this.func_151549_a(world, structureboundingbox, 3, 1, 5, 4, 2, 16, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, this.field_74939_a - 6, 1, 5, this.field_74939_a - 5, 2, 16, Blocks.field_150350_a, Blocks.field_150350_a, false);

        int i1;

        for (i1 = 5; i1 <= 17; i1 += 2) {
            this.func_151550_a(world, Blocks.field_150322_A, 2, 4, 1, i1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 1, 4, 2, i1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, this.field_74939_a - 5, 1, i1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 1, this.field_74939_a - 5, 2, i1, structureboundingbox);
        }

        this.func_151550_a(world, Blocks.field_150325_L, b0, 10, 0, 7, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, b0, 10, 0, 8, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, b0, 9, 0, 9, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, b0, 11, 0, 9, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, b0, 8, 0, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, b0, 12, 0, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, b0, 7, 0, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, b0, 13, 0, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, b0, 9, 0, 11, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, b0, 11, 0, 11, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, b0, 10, 0, 12, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, b0, 10, 0, 13, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, b1, 10, 0, 10, structureboundingbox);

        for (i1 = 0; i1 <= this.field_74939_a - 1; i1 += this.field_74939_a - 1) {
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1, 2, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1, 2, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1, 2, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1, 3, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1, 3, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1, 3, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1, 4, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 1, i1, 4, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1, 4, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1, 5, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1, 5, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1, 5, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1, 6, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 1, i1, 6, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1, 6, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1, 7, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1, 7, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1, 7, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1, 8, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1, 8, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1, 8, 3, structureboundingbox);
        }

        for (i1 = 2; i1 <= this.field_74939_a - 3; i1 += this.field_74939_a - 3 - 2) {
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1 - 1, 2, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1, 2, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1 + 1, 2, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1 - 1, 3, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1, 3, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1 + 1, 3, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1 - 1, 4, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 1, i1, 4, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1 + 1, 4, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1 - 1, 5, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1, 5, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1 + 1, 5, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1 - 1, 6, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 1, i1, 6, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1 + 1, 6, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1 - 1, 7, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1, 7, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150325_L, b0, i1 + 1, 7, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1 - 1, 8, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1, 8, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150322_A, 2, i1 + 1, 8, 0, structureboundingbox);
        }

        this.func_151556_a(world, structureboundingbox, 8, 4, 0, 12, 6, 0, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 8, 6, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 12, 6, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, b0, 9, 5, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 1, 10, 5, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, b0, 11, 5, 0, structureboundingbox);
        this.func_151556_a(world, structureboundingbox, 8, -14, 8, 12, -11, 12, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
        this.func_151556_a(world, structureboundingbox, 8, -10, 8, 12, -10, 12, Blocks.field_150322_A, 1, Blocks.field_150322_A, 1, false);
        this.func_151556_a(world, structureboundingbox, 8, -9, 8, 12, -9, 12, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
        this.func_151549_a(world, structureboundingbox, 8, -8, 8, 12, -1, 12, Blocks.field_150322_A, Blocks.field_150322_A, false);
        this.func_151549_a(world, structureboundingbox, 9, -11, 9, 11, -1, 11, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151550_a(world, Blocks.field_150456_au, 0, 10, -11, 10, structureboundingbox);
        this.func_151549_a(world, structureboundingbox, 9, -13, 9, 11, -13, 11, Blocks.field_150335_W, Blocks.field_150350_a, false);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 8, -11, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 8, -10, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 1, 7, -10, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 2, 7, -11, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 12, -11, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 12, -10, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 1, 13, -10, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 2, 13, -11, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 10, -11, 8, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 10, -10, 8, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 1, 10, -10, 7, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 2, 10, -11, 7, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 10, -11, 12, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 10, -10, 12, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 1, 10, -10, 13, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150322_A, 2, 10, -11, 13, structureboundingbox);

        for (i1 = 0; i1 < 4; ++i1) {
            if (!this.field_74940_h[i1]) {
                int j1 = Direction.field_71583_a[i1] * 2;
                int k1 = Direction.field_71581_b[i1] * 2;

                this.field_74940_h[i1] = this.func_74879_a(world, structureboundingbox, random, 10 + j1, -11, 10 + k1, WeightedRandomChestContent.func_92080_a(ComponentScatteredFeaturePieces$DesertPyramid.field_74941_i, Items.field_151134_bR.func_92114_b(random)), 2 + random.nextInt(5));
            }
        }

        return true;
    }
}
