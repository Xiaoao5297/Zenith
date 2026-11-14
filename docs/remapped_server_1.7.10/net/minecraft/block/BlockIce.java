package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BlockIce extends BlockBreakable {

    public BlockIce() {
        super("ice", Material.field_151588_w, false);
        this.field_149765_K = 0.98F;
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public void func_149636_a(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
        entityplayer.func_71064_a(StatList.field_75934_C[Block.func_149682_b(this)], 1);
        entityplayer.func_71020_j(0.025F);
        if (this.func_149700_E() && EnchantmentHelper.func_77502_d(entityplayer)) {
            ItemStack itemstack = this.func_149644_j(l);

            if (itemstack != null) {
                this.func_149642_a(world, i, j, k, itemstack);
            }
        } else {
            if (world.field_73011_w.field_76575_d) {
                world.func_147468_f(i, j, k);
                return;
            }

            int i1 = EnchantmentHelper.func_77517_e(entityplayer);

            this.func_149697_b(world, i, j, k, l, i1);
            Material material = world.func_147439_a(i, j - 1, k).func_149688_o();

            if (material.func_76230_c() || material.func_76224_d()) {
                world.func_147449_b(i, j, k, Blocks.field_150358_i);
            }
        }

    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (world.func_72972_b(EnumSkyBlock.Block, i, j, k) > 11 - this.func_149717_k()) {
            if (world.field_73011_w.field_76575_d) {
                world.func_147468_f(i, j, k);
                return;
            }

            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147449_b(i, j, k, Blocks.field_150355_j);
        }

    }

    @Override
    public int func_149656_h() {
        return 0;
    }
}
