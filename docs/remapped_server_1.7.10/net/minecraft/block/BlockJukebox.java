package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockJukebox extends BlockContainer {

    protected BlockJukebox() {
        super(Material.field_151575_d);
        this.func_149647_a(CreativeTabs.field_78031_c);
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (world.func_72805_g(i, j, k) == 0) {
            return false;
        } else {
            this.func_149925_e(world, i, j, k);
            return true;
        }
    }

    public void func_149926_b(World world, int i, int j, int k, ItemStack itemstack) {
        if (!world.field_72995_K) {
            BlockJukebox$TileEntityJukebox blockjukebox$tileentityjukebox = (BlockJukebox$TileEntityJukebox) world.func_147438_o(i, j, k);

            if (blockjukebox$tileentityjukebox != null) {
                blockjukebox$tileentityjukebox.func_145857_a(itemstack.func_77946_l());
                world.func_72921_c(i, j, k, 1, 2);
            }
        }
    }

    public void func_149925_e(World world, int i, int j, int k) {
        if (!world.field_72995_K) {
            BlockJukebox$TileEntityJukebox blockjukebox$tileentityjukebox = (BlockJukebox$TileEntityJukebox) world.func_147438_o(i, j, k);

            if (blockjukebox$tileentityjukebox != null) {
                ItemStack itemstack = blockjukebox$tileentityjukebox.func_145856_a();

                if (itemstack != null) {
                    world.func_72926_e(1005, i, j, k, 0);
                    world.func_72934_a((String) null, i, j, k);
                    blockjukebox$tileentityjukebox.func_145857_a((ItemStack) null);
                    world.func_72921_c(i, j, k, 0, 2);
                    float f = 0.7F;
                    double d0 = (double) (world.field_73012_v.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    double d1 = (double) (world.field_73012_v.nextFloat() * f) + (double) (1.0F - f) * 0.2D + 0.6D;
                    double d2 = (double) (world.field_73012_v.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    ItemStack itemstack1 = itemstack.func_77946_l();
                    EntityItem entityitem = new EntityItem(world, (double) i + d0, (double) j + d1, (double) k + d2, itemstack1);

                    entityitem.field_145804_b = 10;
                    world.func_72838_d(entityitem);
                }
            }
        }
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        this.func_149925_e(world, i, j, k);
        super.func_149749_a(world, i, j, k, block, l);
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {
        if (!world.field_72995_K) {
            super.func_149690_a(world, i, j, k, l, f, 0);
        }
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new BlockJukebox$TileEntityJukebox();
    }

    @Override
    public boolean func_149740_M() {
        return true;
    }

    @Override
    public int func_149736_g(World world, int i, int j, int k, int l) {
        ItemStack itemstack = ((BlockJukebox$TileEntityJukebox) world.func_147438_o(i, j, k)).func_145856_a();

        return itemstack == null ? 0 : Item.func_150891_b(itemstack.func_77973_b()) + 1 - Item.func_150891_b(Items.field_151096_cd);
    }
}
