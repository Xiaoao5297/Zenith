package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockEnderChest extends BlockContainer {

    protected BlockEnderChest() {
        super(Material.field_151576_e);
        this.func_149647_a(CreativeTabs.field_78031_c);
        this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
        return 22;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Item.func_150898_a(Blocks.field_150343_Z);
    }

    @Override
    public int func_149745_a(Random random) {
        return 8;
    }

    @Override
    protected boolean func_149700_E() {
        return true;
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        byte b0 = 0;
        int l = MathHelper.func_76128_c((double) (entitylivingbase.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0) {
            b0 = 2;
        }

        if (l == 1) {
            b0 = 5;
        }

        if (l == 2) {
            b0 = 3;
        }

        if (l == 3) {
            b0 = 4;
        }

        world.func_72921_c(i, j, k, b0, 2);
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        InventoryEnderChest inventoryenderchest = entityplayer.func_71005_bN();
        TileEntityEnderChest tileentityenderchest = (TileEntityEnderChest) world.func_147438_o(i, j, k);

        if (inventoryenderchest != null && tileentityenderchest != null) {
            if (world.func_147439_a(i, j + 1, k).func_149721_r()) {
                return true;
            } else if (world.field_72995_K) {
                return true;
            } else {
                inventoryenderchest.func_146031_a(tileentityenderchest);
                entityplayer.func_71007_a(inventoryenderchest);
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntityEnderChest();
    }
}
