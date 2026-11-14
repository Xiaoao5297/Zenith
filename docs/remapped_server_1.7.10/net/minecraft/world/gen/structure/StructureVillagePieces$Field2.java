package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class StructureVillagePieces$Field2 extends StructureVillagePieces$Village {

    private Block field_82675_b;
    private Block field_82676_c;

    public StructureVillagePieces$Field2() {}

    public StructureVillagePieces$Field2(StructureVillagePieces$Start structurevillagepieces$start, int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(structurevillagepieces$start, i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
        this.field_82675_b = this.func_151560_a(random);
        this.field_82676_c = this.func_151560_a(random);
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74768_a("CA", Block.field_149771_c.func_148757_b(this.field_82675_b));
        nbttagcompound.func_74768_a("CB", Block.field_149771_c.func_148757_b(this.field_82676_c));
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_82675_b = Block.func_149729_e(nbttagcompound.func_74762_e("CA"));
        this.field_82676_c = Block.func_149729_e(nbttagcompound.func_74762_e("CB"));
    }

    private Block func_151560_a(Random random) {
        switch (random.nextInt(5)) {
            case 0:
                return Blocks.field_150459_bM;
            case 1:
                return Blocks.field_150469_bN;
            default:
                return Blocks.field_150464_aj;
        }
    }

    public static StructureVillagePieces$Field2 func_74902_a(StructureVillagePieces$Start structurevillagepieces$start, List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, 0, 0, 0, 7, 4, 9, l);

        return func_74895_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureVillagePieces$Field2(structurevillagepieces$start, i1, random, structureboundingbox, l) : null;
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

        this.func_151549_a(world, structureboundingbox, 0, 1, 0, 6, 4, 8, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 1, 0, 1, 2, 0, 7, Blocks.field_150458_ak, Blocks.field_150458_ak, false);
        this.func_151549_a(world, structureboundingbox, 4, 0, 1, 5, 0, 7, Blocks.field_150458_ak, Blocks.field_150458_ak, false);
        this.func_151549_a(world, structureboundingbox, 0, 0, 0, 0, 0, 8, Blocks.field_150364_r, Blocks.field_150364_r, false);
        this.func_151549_a(world, structureboundingbox, 6, 0, 0, 6, 0, 8, Blocks.field_150364_r, Blocks.field_150364_r, false);
        this.func_151549_a(world, structureboundingbox, 1, 0, 0, 5, 0, 0, Blocks.field_150364_r, Blocks.field_150364_r, false);
        this.func_151549_a(world, structureboundingbox, 1, 0, 8, 5, 0, 8, Blocks.field_150364_r, Blocks.field_150364_r, false);
        this.func_151549_a(world, structureboundingbox, 3, 0, 1, 3, 0, 7, Blocks.field_150355_j, Blocks.field_150355_j, false);

        int i;

        for (i = 1; i <= 7; ++i) {
            this.func_151550_a(world, this.field_82675_b, MathHelper.func_76136_a(random, 2, 7), 1, 1, i, structureboundingbox);
            this.func_151550_a(world, this.field_82675_b, MathHelper.func_76136_a(random, 2, 7), 2, 1, i, structureboundingbox);
            this.func_151550_a(world, this.field_82676_c, MathHelper.func_76136_a(random, 2, 7), 4, 1, i, structureboundingbox);
            this.func_151550_a(world, this.field_82676_c, MathHelper.func_76136_a(random, 2, 7), 5, 1, i, structureboundingbox);
        }

        for (i = 0; i < 9; ++i) {
            for (int j = 0; j < 7; ++j) {
                this.func_74871_b(world, j, 4, i, structureboundingbox);
                this.func_151554_b(world, Blocks.field_150346_d, 0, j, -1, i, structureboundingbox);
            }
        }

        return true;
    }
}
