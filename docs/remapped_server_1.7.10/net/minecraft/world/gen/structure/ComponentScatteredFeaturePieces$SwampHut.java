package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ComponentScatteredFeaturePieces$SwampHut extends ComponentScatteredFeaturePieces$Feature {

    private boolean field_82682_h;

    public ComponentScatteredFeaturePieces$SwampHut() {}

    public ComponentScatteredFeaturePieces$SwampHut(Random random, int i, int j) {
        super(random, i, 64, j, 7, 5, 9);
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74757_a("Witch", this.field_82682_h);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_82682_h = nbttagcompound.func_74767_n("Witch");
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (!this.func_74935_a(world, structureboundingbox, 0)) {
            return false;
        } else {
            this.func_151556_a(world, structureboundingbox, 1, 1, 1, 5, 1, 7, Blocks.field_150344_f, 1, Blocks.field_150344_f, 1, false);
            this.func_151556_a(world, structureboundingbox, 1, 4, 2, 5, 4, 7, Blocks.field_150344_f, 1, Blocks.field_150344_f, 1, false);
            this.func_151556_a(world, structureboundingbox, 2, 1, 0, 4, 1, 0, Blocks.field_150344_f, 1, Blocks.field_150344_f, 1, false);
            this.func_151556_a(world, structureboundingbox, 2, 2, 2, 3, 3, 2, Blocks.field_150344_f, 1, Blocks.field_150344_f, 1, false);
            this.func_151556_a(world, structureboundingbox, 1, 2, 3, 1, 3, 6, Blocks.field_150344_f, 1, Blocks.field_150344_f, 1, false);
            this.func_151556_a(world, structureboundingbox, 5, 2, 3, 5, 3, 6, Blocks.field_150344_f, 1, Blocks.field_150344_f, 1, false);
            this.func_151556_a(world, structureboundingbox, 2, 2, 7, 4, 3, 7, Blocks.field_150344_f, 1, Blocks.field_150344_f, 1, false);
            this.func_151549_a(world, structureboundingbox, 1, 0, 2, 1, 3, 2, Blocks.field_150364_r, Blocks.field_150364_r, false);
            this.func_151549_a(world, structureboundingbox, 5, 0, 2, 5, 3, 2, Blocks.field_150364_r, Blocks.field_150364_r, false);
            this.func_151549_a(world, structureboundingbox, 1, 0, 7, 1, 3, 7, Blocks.field_150364_r, Blocks.field_150364_r, false);
            this.func_151549_a(world, structureboundingbox, 5, 0, 7, 5, 3, 7, Blocks.field_150364_r, Blocks.field_150364_r, false);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 2, 3, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 3, 3, 7, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150350_a, 0, 1, 3, 4, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150350_a, 0, 5, 3, 4, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150350_a, 0, 5, 3, 5, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150457_bL, 7, 1, 3, 5, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150462_ai, 0, 3, 2, 6, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150383_bp, 0, 4, 2, 6, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 1, 2, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 5, 2, 1, structureboundingbox);
            int i = this.func_151555_a(Blocks.field_150476_ad, 3);
            int j = this.func_151555_a(Blocks.field_150476_ad, 1);
            int k = this.func_151555_a(Blocks.field_150476_ad, 0);
            int l = this.func_151555_a(Blocks.field_150476_ad, 2);

            this.func_151556_a(world, structureboundingbox, 0, 4, 1, 6, 4, 1, Blocks.field_150485_bF, i, Blocks.field_150485_bF, i, false);
            this.func_151556_a(world, structureboundingbox, 0, 4, 2, 0, 4, 7, Blocks.field_150485_bF, k, Blocks.field_150485_bF, k, false);
            this.func_151556_a(world, structureboundingbox, 6, 4, 2, 6, 4, 7, Blocks.field_150485_bF, j, Blocks.field_150485_bF, j, false);
            this.func_151556_a(world, structureboundingbox, 0, 4, 8, 6, 4, 8, Blocks.field_150485_bF, l, Blocks.field_150485_bF, l, false);

            int i1;
            int j1;

            for (j1 = 2; j1 <= 7; j1 += 5) {
                for (i1 = 1; i1 <= 5; i1 += 4) {
                    this.func_151554_b(world, Blocks.field_150364_r, 0, i1, -1, j1, structureboundingbox);
                }
            }

            if (!this.field_82682_h) {
                j1 = this.func_74865_a(2, 5);
                i1 = this.func_74862_a(2);
                int k1 = this.func_74873_b(2, 5);

                if (structureboundingbox.func_78890_b(j1, i1, k1)) {
                    this.field_82682_h = true;
                    EntityWitch entitywitch = new EntityWitch(world);

                    entitywitch.func_70012_b((double) j1 + 0.5D, (double) i1, (double) k1 + 0.5D, 0.0F, 0.0F);
                    entitywitch.func_110161_a((IEntityLivingData) null);
                    world.func_72838_d(entitywitch);
                }
            }

            return true;
        }
    }
}
