package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCake extends Block {

    protected BlockCake() {
        super(Material.field_151568_F);
        this.func_149675_a(true);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.func_72805_g(i, j, k);
        float f = 0.0625F;
        float f1 = (float) (1 + l * 2) / 16.0F;
        float f2 = 0.5F;

        this.func_149676_a(f1, 0.0F, f, 1.0F - f, f2, 1.0F - f);
    }

    @Override
    public void func_149683_g() {
        float f = 0.0625F;
        float f1 = 0.5F;

        this.func_149676_a(f, 0.0F, f, 1.0F - f, f1, 1.0F - f);
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);
        float f = 0.0625F;
        float f1 = (float) (1 + l * 2) / 16.0F;
        float f2 = 0.5F;

        return AxisAlignedBB.func_72330_a((double) ((float) i + f1), (double) j, (double) ((float) k + f), (double) ((float) (i + 1) - f), (double) ((float) j + f2 - f), (double) ((float) (k + 1) - f));
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        this.func_150036_b(world, i, j, k, entityplayer);
        return true;
    }

    @Override
    public void func_149699_a(World world, int i, int j, int k, EntityPlayer entityplayer) {
        this.func_150036_b(world, i, j, k, entityplayer);
    }

    private void func_150036_b(World world, int i, int j, int k, EntityPlayer entityplayer) {
        if (entityplayer.func_71043_e(false)) {
            entityplayer.func_71024_bL().func_75122_a(2, 0.1F);
            int l = world.func_72805_g(i, j, k) + 1;

            if (l >= 6) {
                world.func_147468_f(i, j, k);
            } else {
                world.func_72921_c(i, j, k, l, 2);
            }
        }

    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return !super.func_149742_c(world, i, j, k) ? false : this.func_149718_j(world, i, j, k);
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!this.func_149718_j(world, i, j, k)) {
            world.func_147468_f(i, j, k);
        }

    }

    @Override
    public boolean func_149718_j(World world, int i, int j, int k) {
        return world.func_147439_a(i, j - 1, k).func_149688_o().func_76220_a();
    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return null;
    }
}
