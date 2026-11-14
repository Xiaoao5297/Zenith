package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockBrewingStand extends BlockContainer {

    private Random field_149961_a = new Random();

    public BlockBrewingStand() {
        super(Material.field_151573_f);
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public int func_149645_b() {
        return 25;
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntityBrewingStand();
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public void func_149743_a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        this.func_149676_a(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
        super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        this.func_149683_g();
        super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
    }

    @Override
    public void func_149683_g() {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else {
            TileEntityBrewingStand tileentitybrewingstand = (TileEntityBrewingStand) world.func_147438_o(i, j, k);

            if (tileentitybrewingstand != null) {
                entityplayer.func_146098_a(tileentitybrewingstand);
            }

            return true;
        }
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        if (itemstack.func_82837_s()) {
            ((TileEntityBrewingStand) world.func_147438_o(i, j, k)).func_145937_a(itemstack.func_82833_r());
        }

    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        TileEntity tileentity = world.func_147438_o(i, j, k);

        if (tileentity instanceof TileEntityBrewingStand) {
            TileEntityBrewingStand tileentitybrewingstand = (TileEntityBrewingStand) tileentity;

            for (int i1 = 0; i1 < tileentitybrewingstand.func_70302_i_(); ++i1) {
                ItemStack itemstack = tileentitybrewingstand.func_70301_a(i1);

                if (itemstack != null) {
                    float f = this.field_149961_a.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.field_149961_a.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.field_149961_a.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.field_77994_a > 0) {
                        int j1 = this.field_149961_a.nextInt(21) + 10;

                        if (j1 > itemstack.field_77994_a) {
                            j1 = itemstack.field_77994_a;
                        }

                        itemstack.field_77994_a -= j1;
                        EntityItem entityitem = new EntityItem(world, (double) ((float) i + f), (double) ((float) j + f1), (double) ((float) k + f2), new ItemStack(itemstack.func_77973_b(), j1, itemstack.func_77960_j()));
                        float f3 = 0.05F;

                        entityitem.field_70159_w = (double) ((float) this.field_149961_a.nextGaussian() * f3);
                        entityitem.field_70181_x = (double) ((float) this.field_149961_a.nextGaussian() * f3 + 0.2F);
                        entityitem.field_70179_y = (double) ((float) this.field_149961_a.nextGaussian() * f3);
                        world.func_72838_d(entityitem);
                    }
                }
            }
        }

        super.func_149749_a(world, i, j, k, block, l);
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151067_bt;
    }

    @Override
    public boolean func_149740_M() {
        return true;
    }

    @Override
    public int func_149736_g(World world, int i, int j, int k, int l) {
        return Container.func_94526_b((IInventory) world.func_147438_o(i, j, k));
    }
}
