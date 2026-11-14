package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.world.World;

public class BlockEnchantmentTable extends BlockContainer {

    protected BlockEnchantmentTable() {
        super(Material.field_151576_e);
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
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
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntityEnchantmentTable();
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else {
            TileEntityEnchantmentTable tileentityenchantmenttable = (TileEntityEnchantmentTable) world.func_147438_o(i, j, k);

            entityplayer.func_71002_c(i, j, k, tileentityenchantmenttable.func_145921_b() ? tileentityenchantmenttable.func_145919_a() : null);
            return true;
        }
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        super.func_149689_a(world, i, j, k, entitylivingbase, itemstack);
        if (itemstack.func_82837_s()) {
            ((TileEntityEnchantmentTable) world.func_147438_o(i, j, k)).func_145920_a(itemstack.func_82833_r());
        }

    }
}
