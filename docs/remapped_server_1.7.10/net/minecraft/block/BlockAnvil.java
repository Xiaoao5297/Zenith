package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAnvil extends BlockFalling {

    public static final String[] field_149834_a = new String[]{"intact", "slightlyDamaged", "veryDamaged"};
    private static final String[] field_149835_N = new String[]{"anvil_top_damaged_0", "anvil_top_damaged_1", "anvil_top_damaged_2"};

    protected BlockAnvil() {
        super(Material.field_151574_g);
        this.func_149713_g(0);
        this.func_149647_a(CreativeTabs.field_78031_c);
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
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        int l = MathHelper.func_76128_c((double) (entitylivingbase.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3;
        int i1 = world.func_72805_g(i, j, k) >> 2;

        ++l;
        l %= 4;
        if (l == 0) {
            world.func_72921_c(i, j, k, 2 | i1 << 2, 2);
        }

        if (l == 1) {
            world.func_72921_c(i, j, k, 3 | i1 << 2, 2);
        }

        if (l == 2) {
            world.func_72921_c(i, j, k, 0 | i1 << 2, 2);
        }

        if (l == 3) {
            world.func_72921_c(i, j, k, 1 | i1 << 2, 2);
        }

    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else {
            entityplayer.func_82244_d(i, j, k);
            return true;
        }
    }

    @Override
    public int func_149645_b() {
        return 35;
    }

    @Override
    public int func_149692_a(int i) {
        return i >> 2;
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.func_72805_g(i, j, k) & 3;

        if (l != 3 && l != 1) {
            this.func_149676_a(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
        } else {
            this.func_149676_a(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
        }

    }

    @Override
    protected void func_149829_a(EntityFallingBlock entityfallingblock) {
        entityfallingblock.func_145806_a(true);
    }

    @Override
    public void func_149828_a(World world, int i, int j, int k, int l) {
        world.func_72926_e(1022, i, j, k, 0);
    }
}
