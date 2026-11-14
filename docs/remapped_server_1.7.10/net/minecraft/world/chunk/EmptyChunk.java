package net.minecraft.world.chunk;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EmptyChunk extends Chunk {

    public EmptyChunk(World world, int i, int j) {
        super(world, i, j);
    }

    @Override
    public boolean func_76600_a(int i, int j) {
        return i == this.field_76635_g && j == this.field_76647_h;
    }

    @Override
    public int func_76611_b(int i, int j) {
        return 0;
    }

    @Override
    public void func_76603_b() {}

    @Override
    public Block func_150810_a(int i, int j, int k) {
        return Blocks.field_150350_a;
    }

    @Override
    public int func_150808_b(int i, int j, int k) {
        return 255;
    }

    @Override
    public boolean func_150807_a(int i, int j, int k, Block block, int l) {
        return true;
    }

    @Override
    public int func_76628_c(int i, int j, int k) {
        return 0;
    }

    @Override
    public boolean func_76589_b(int i, int j, int k, int l) {
        return false;
    }

    @Override
    public int func_76614_a(EnumSkyBlock enumskyblock, int i, int j, int k) {
        return 0;
    }

    @Override
    public void func_76633_a(EnumSkyBlock enumskyblock, int i, int j, int k, int l) {}

    @Override
    public int func_76629_c(int i, int j, int k, int l) {
        return 0;
    }

    @Override
    public void func_76612_a(Entity entity) {}

    @Override
    public void func_76622_b(Entity entity) {}

    @Override
    public void func_76608_a(Entity entity, int i) {}

    @Override
    public boolean func_76619_d(int i, int j, int k) {
        return false;
    }

    @Override
    public TileEntity func_150806_e(int i, int j, int k) {
        return null;
    }

    @Override
    public void func_150813_a(TileEntity tileentity) {}

    @Override
    public void func_150812_a(int i, int j, int k, TileEntity tileentity) {}

    @Override
    public void func_150805_f(int i, int j, int k) {}

    @Override
    public void func_76631_c() {}

    @Override
    public void func_76623_d() {}

    @Override
    public void func_76630_e() {}

    @Override
    public void func_76588_a(Entity entity, AxisAlignedBB axisalignedbb, List list, IEntitySelector ientityselector) {}

    @Override
    public void func_76618_a(Class oclass, AxisAlignedBB axisalignedbb, List list, IEntitySelector ientityselector) {}

    @Override
    public boolean func_76601_a(boolean flag) {
        return false;
    }

    @Override
    public Random func_76617_a(long i) {
        return new Random(this.field_76637_e.func_72905_C() + (long) (this.field_76635_g * this.field_76635_g * 4987142) + (long) (this.field_76635_g * 5947611) + (long) (this.field_76647_h * this.field_76647_h) * 4392871L + (long) (this.field_76647_h * 389711) ^ i);
    }

    @Override
    public boolean func_76621_g() {
        return true;
    }

    @Override
    public boolean func_76606_c(int i, int j) {
        return true;
    }
}
