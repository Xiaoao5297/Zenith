package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.world.World;

public class BlockBeacon extends BlockContainer {

    public BlockBeacon() {
        super(Material.field_151592_s);
        this.func_149711_c(3.0F);
        this.func_149647_a(CreativeTabs.field_78026_f);
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntityBeacon();
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else {
            TileEntityBeacon tileentitybeacon = (TileEntityBeacon) world.func_147438_o(i, j, k);

            if (tileentitybeacon != null) {
                entityplayer.func_146104_a(tileentitybeacon);
            }

            return true;
        }
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
    public int func_149645_b() {
        return 34;
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        super.func_149689_a(world, i, j, k, entitylivingbase, itemstack);
        if (itemstack.func_82837_s()) {
            ((TileEntityBeacon) world.func_147438_o(i, j, k)).func_145999_a(itemstack.func_82833_r());
        }

    }
}
