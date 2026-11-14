package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class StructureVillagePieces$Field1 extends StructureVillagePieces$Village {

    private Block field_82679_b;
    private Block field_82680_c;
    private Block field_82678_d;
    private Block field_82681_h;

    public StructureVillagePieces$Field1() {}

    public StructureVillagePieces$Field1(StructureVillagePieces$Start structurevillagepieces$start, int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(structurevillagepieces$start, i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
        this.field_82679_b = this.func_151559_a(random);
        this.field_82680_c = this.func_151559_a(random);
        this.field_82678_d = this.func_151559_a(random);
        this.field_82681_h = this.func_151559_a(random);
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74768_a("CA", Block.field_149771_c.func_148757_b(this.field_82679_b));
        nbttagcompound.func_74768_a("CB", Block.field_149771_c.func_148757_b(this.field_82680_c));
        nbttagcompound.func_74768_a("CC", Block.field_149771_c.func_148757_b(this.field_82678_d));
        nbttagcompound.func_74768_a("CD", Block.field_149771_c.func_148757_b(this.field_82681_h));
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_82679_b = Block.func_149729_e(nbttagcompound.func_74762_e("CA"));
        this.field_82680_c = Block.func_149729_e(nbttagcompound.func_74762_e("CB"));
        this.field_82678_d = Block.func_149729_e(nbttagcompound.func_74762_e("CC"));
        this.field_82681_h = Block.func_149729_e(nbttagcompound.func_74762_e("CD"));
    }

    private Block func_151559_a(Random random) {
        switch (random.nextInt(5)) {
            case 0:
                return Blocks.field_150459_bM;
            case 1:
                return Blocks.field_150469_bN;
            default:
                return Blocks.field_150464_aj;
        }
    }

    public static StructureVillagePieces$Field1 func_74900_a(StructureVillagePieces$Start structurevillagepieces$start, List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, 0, 0, 0, 13, 4, 9, l);

        return func_74895_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureVillagePieces$Field1(structurevillagepieces$start, i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(world, structureboundingbox);
            if (this.field_143015_k < 0) {
                return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 4 - 1, 0);
        }

        this.func_151549_a(world, structureboundingbox, 0, 1, 0, 12, 4, 8, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 1, 0, 1, 2, 0, 7, Blocks.field_150458_ak, Blocks.field_150458_ak, false);
        this.func_151549_a(world, structureboundingbox, 4, 0, 1, 5, 0, 7, Blocks.field_150458_ak, Blocks.field_150458_ak, false);
        this.func_151549_a(world, structureboundingbox, 7, 0, 1, 8, 0, 7, Blocks.field_150458_ak, Blocks.field_150458_ak, false);
        this.func_151549_a(world, structureboundingbox, 10, 0, 1, 11, 0, 7, Blocks.field_150458_ak, Blocks.field_150458_ak, false);
        this.func_151549_a(world, structureboundingbox, 0, 0, 0, 0, 0, 8, Blocks.field_150364_r, Blocks.field_150364_r, false);
        this.func_151549_a(world, structureboundingbox, 6, 0, 0, 6, 0, 8, Blocks.field_150364_r, Blocks.field_150364_r, false);
        this.func_151549_a(world, structureboundingbox, 12, 0, 0, 12, 0, 8, Blocks.field_150364_r, Blocks.field_150364_r, false);
        this.func_151549_a(world, structureboundingbox, 1, 0, 0, 11, 0, 0, Blocks.field_150364_r, Blocks.field_150364_r, false);
        this.func_151549_a(world, structureboundingbox, 1, 0, 8, 11, 0, 8, Blocks.field_150364_r, Blocks.field_150364_r, false);
        this.func_151549_a(world, structureboundingbox, 3, 0, 1, 3, 0, 7, Blocks.field_150355_j, Blocks.field_150355_j, false);
        this.func_151549_a(world, structureboundingbox, 9, 0, 1, 9, 0, 7, Blocks.field_150355_j, Blocks.field_150355_j, false);

        int i;

        for (i = 1; i <= 7; ++i) {
            this.func_151550_a(world, this.field_82679_b, MathHelper.func_76136_a(random, 2, 7), 1, 1, i, structureboundingbox);
            this.func_151550_a(world, this.field_82679_b, MathHelper.func_76136_a(random, 2, 7), 2, 1, i, structureboundingbox);
            this.func_151550_a(world, this.field_82680_c, MathHelper.func_76136_a(random, 2, 7), 4, 1, i, structureboundingbox);
            this.func_151550_a(world, this.field_82680_c, MathHelper.func_76136_a(random, 2, 7), 5, 1, i, structureboundingbox);
            this.func_151550_a(world, this.field_82678_d, MathHelper.func_76136_a(random, 2, 7), 7, 1, i, structureboundingbox);
            this.func_151550_a(world, this.field_82678_d, MathHelper.func_76136_a(random, 2, 7), 8, 1, i, structureboundingbox);
            this.func_151550_a(world, this.field_82681_h, MathHelper.func_76136_a(random, 2, 7), 10, 1, i, structureboundingbox);
            this.func_151550_a(world, this.field_82681_h, MathHelper.func_76136_a(random, 2, 7), 11, 1, i, structureboundingbox);
        }

        for (i = 0; i < 9; ++i) {
            for (int j = 0; j < 13; ++j) {
                this.func_74871_b(world, j, 4, i, structureboundingbox);
                this.func_151554_b(world, Blocks.field_150346_d, 0, j, -1, i, structureboundingbox);
            }
        }

        return true;
    }
}
