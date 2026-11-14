package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

abstract class StructureVillagePieces$Village extends StructureComponent {

    protected int field_143015_k = -1;
    private int field_74896_a;
    private boolean field_143014_b;

    public StructureVillagePieces$Village() {}

    protected StructureVillagePieces$Village(StructureVillagePieces$Start structurevillagepieces$start, int i) {
        super(i);
        if (structurevillagepieces$start != null) {
            this.field_143014_b = structurevillagepieces$start.field_74927_b;
        }

    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74768_a("HPos", this.field_143015_k);
        nbttagcompound.func_74768_a("VCount", this.field_74896_a);
        nbttagcompound.func_74757_a("Desert", this.field_143014_b);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        this.field_143015_k = nbttagcompound.func_74762_e("HPos");
        this.field_74896_a = nbttagcompound.func_74762_e("VCount");
        this.field_143014_b = nbttagcompound.func_74767_n("Desert");
    }

    protected StructureComponent func_74891_a(StructureVillagePieces$Start structurevillagepieces$start, List list, Random random, int i, int j) {
        switch (this.field_74885_f) {
            case 0:
                return StructureVillagePieces.access$000(structurevillagepieces$start, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c + j, 1, this.func_74877_c());
            case 1:
                return StructureVillagePieces.access$000(structurevillagepieces$start, list, random, this.field_74887_e.field_78897_a + j, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c - 1, 2, this.func_74877_c());
            case 2:
                return StructureVillagePieces.access$000(structurevillagepieces$start, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c + j, 1, this.func_74877_c());
            case 3:
                return StructureVillagePieces.access$000(structurevillagepieces$start, list, random, this.field_74887_e.field_78897_a + j, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c - 1, 2, this.func_74877_c());
            default:
                return null;
        }
    }

    protected StructureComponent func_74894_b(StructureVillagePieces$Start structurevillagepieces$start, List list, Random random, int i, int j) {
        switch (this.field_74885_f) {
            case 0:
                return StructureVillagePieces.access$000(structurevillagepieces$start, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c + j, 3, this.func_74877_c());
            case 1:
                return StructureVillagePieces.access$000(structurevillagepieces$start, list, random, this.field_74887_e.field_78897_a + j, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78892_f + 1, 0, this.func_74877_c());
            case 2:
                return StructureVillagePieces.access$000(structurevillagepieces$start, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c + j, 3, this.func_74877_c());
            case 3:
                return StructureVillagePieces.access$000(structurevillagepieces$start, list, random, this.field_74887_e.field_78897_a + j, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78892_f + 1, 0, this.func_74877_c());
            default:
                return null;
        }
    }

    protected int func_74889_b(World world, StructureBoundingBox structureboundingbox) {
        int i = 0;
        int j = 0;

        for (int k = this.field_74887_e.field_78896_c; k <= this.field_74887_e.field_78892_f; ++k) {
            for (int l = this.field_74887_e.field_78897_a; l <= this.field_74887_e.field_78893_d; ++l) {
                if (structureboundingbox.func_78890_b(l, 64, k)) {
                    i += Math.max(world.func_72825_h(l, k), world.field_73011_w.func_76557_i());
                    ++j;
                }
            }
        }

        if (j == 0) {
            return -1;
        } else {
            return i / j;
        }
    }

    protected static boolean func_74895_a(StructureBoundingBox structureboundingbox) {
        return structureboundingbox != null && structureboundingbox.field_78895_b > 10;
    }

    protected void func_74893_a(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l) {
        if (this.field_74896_a < l) {
            for (int i1 = this.field_74896_a; i1 < l; ++i1) {
                int j1 = this.func_74865_a(i + i1, k);
                int k1 = this.func_74862_a(j);
                int l1 = this.func_74873_b(i + i1, k);

                if (!structureboundingbox.func_78890_b(j1, k1, l1)) {
                    break;
                }

                ++this.field_74896_a;
                EntityVillager entityvillager = new EntityVillager(world, this.func_74888_b(i1));

                entityvillager.func_70012_b((double) j1 + 0.5D, (double) k1, (double) l1 + 0.5D, 0.0F, 0.0F);
                world.func_72838_d(entityvillager);
            }

        }
    }

    protected int func_74888_b(int i) {
        return 0;
    }

    protected Block func_151558_b(Block block, int i) {
        if (this.field_143014_b) {
            if (block == Blocks.field_150364_r || block == Blocks.field_150363_s) {
                return Blocks.field_150322_A;
            }

            if (block == Blocks.field_150347_e) {
                return Blocks.field_150322_A;
            }

            if (block == Blocks.field_150344_f) {
                return Blocks.field_150322_A;
            }

            if (block == Blocks.field_150476_ad) {
                return Blocks.field_150372_bz;
            }

            if (block == Blocks.field_150446_ar) {
                return Blocks.field_150372_bz;
            }

            if (block == Blocks.field_150351_n) {
                return Blocks.field_150322_A;
            }
        }

        return block;
    }

    protected int func_151557_c(Block block, int i) {
        if (this.field_143014_b) {
            if (block == Blocks.field_150364_r || block == Blocks.field_150363_s) {
                return 0;
            }

            if (block == Blocks.field_150347_e) {
                return 0;
            }

            if (block == Blocks.field_150344_f) {
                return 2;
            }
        }

        return i;
    }

    @Override
    protected void func_151550_a(World world, Block block, int i, int j, int k, int l, StructureBoundingBox structureboundingbox) {
        Block block1 = this.func_151558_b(block, i);
        int i1 = this.func_151557_c(block, i);

        super.func_151550_a(world, block1, i1, j, k, l, structureboundingbox);
    }

    @Override
    protected void func_151549_a(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, Block block, Block block1, boolean flag) {
        Block block2 = this.func_151558_b(block, 0);
        int k1 = this.func_151557_c(block, 0);
        Block block3 = this.func_151558_b(block1, 0);
        int l1 = this.func_151557_c(block1, 0);

        super.func_151556_a(world, structureboundingbox, i, j, k, l, i1, j1, block2, k1, block3, l1, flag);
    }

    @Override
    protected void func_151554_b(World world, Block block, int i, int j, int k, int l, StructureBoundingBox structureboundingbox) {
        Block block1 = this.func_151558_b(block, i);
        int i1 = this.func_151557_c(block, i);

        super.func_151554_b(world, block1, i1, j, k, l, structureboundingbox);
    }
}
