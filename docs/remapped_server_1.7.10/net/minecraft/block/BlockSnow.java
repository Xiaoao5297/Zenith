package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSnow extends Block {

    protected BlockSnow() {
        super(Material.field_151597_y);
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.field_78031_c);
        this.func_150154_b(0);
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k) & 7;
        float f = 0.125F;

        return AxisAlignedBB.func_72330_a((double) i + this.field_149759_B, (double) j + this.field_149760_C, (double) k + this.field_149754_D, (double) i + this.field_149755_E, (double) ((float) j + (float) l * f), (double) k + this.field_149757_G);
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public void func_149683_g() {
        this.func_150154_b(0);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        this.func_150154_b(iblockaccess.func_72805_g(i, j, k));
    }

    protected void func_150154_b(int i) {
        int j = i & 7;
        float f = (float) (2 * (1 + j)) / 16.0F;

        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        Block block = world.func_147439_a(i, j - 1, k);

        return block != Blocks.field_150432_aD && block != Blocks.field_150403_cj ? (block.func_149688_o() == Material.field_151584_j ? true : (block == this && (world.func_72805_g(i, j - 1, k) & 7) == 7 ? true : block.func_149662_c() && block.field_149764_J.func_76230_c())) : false;
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        this.func_150155_m(world, i, j, k);
    }

    private boolean func_150155_m(World world, int i, int j, int k) {
        if (!this.func_149742_c(world, i, j, k)) {
            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147468_f(i, j, k);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void func_149636_a(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
        int i1 = l & 7;

        this.func_149642_a(world, i, j, k, new ItemStack(Items.field_151126_ay, i1 + 1, 0));
        world.func_147468_f(i, j, k);
        entityplayer.func_71064_a(StatList.field_75934_C[Block.func_149682_b(this)], 1);
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151126_ay;
    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (world.func_72972_b(EnumSkyBlock.Block, i, j, k) > 11) {
            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147468_f(i, j, k);
        }

    }
}
