package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHopper extends BlockContainer {

    private final Random field_149922_a = new Random();

    public BlockHopper() {
        super(Material.field_151573_f);
        this.func_149647_a(CreativeTabs.field_78028_d);
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void func_149743_a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        float f = 0.125F;

        this.func_149676_a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        this.func_149676_a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        this.func_149676_a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public int func_149660_a(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        int j1 = Facing.field_71588_a[l];

        if (j1 == 1) {
            j1 = 0;
        }

        return j1;
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntityHopper();
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        super.func_149689_a(world, i, j, k, entitylivingbase, itemstack);
        if (itemstack.func_82837_s()) {
            TileEntityHopper tileentityhopper = func_149920_e(world, i, j, k);

            tileentityhopper.func_145886_a(itemstack.func_82833_r());
        }

    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        super.func_149726_b(world, i, j, k);
        this.func_149919_e(world, i, j, k);
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else {
            TileEntityHopper tileentityhopper = func_149920_e(world, i, j, k);

            if (tileentityhopper != null) {
                entityplayer.func_146093_a(tileentityhopper);
            }

            return true;
        }
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        this.func_149919_e(world, i, j, k);
    }

    private void func_149919_e(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);
        int i1 = func_149918_b(l);
        boolean flag = !world.func_72864_z(i, j, k);
        boolean flag1 = func_149917_c(l);

        if (flag != flag1) {
            world.func_72921_c(i, j, k, i1 | (flag ? 0 : 8), 4);
        }

    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        TileEntityHopper tileentityhopper = (TileEntityHopper) world.func_147438_o(i, j, k);

        if (tileentityhopper != null) {
            for (int i1 = 0; i1 < tileentityhopper.func_70302_i_(); ++i1) {
                ItemStack itemstack = tileentityhopper.func_70301_a(i1);

                if (itemstack != null) {
                    float f = this.field_149922_a.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.field_149922_a.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.field_149922_a.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.field_77994_a > 0) {
                        int j1 = this.field_149922_a.nextInt(21) + 10;

                        if (j1 > itemstack.field_77994_a) {
                            j1 = itemstack.field_77994_a;
                        }

                        itemstack.field_77994_a -= j1;
                        EntityItem entityitem = new EntityItem(world, (double) ((float) i + f), (double) ((float) j + f1), (double) ((float) k + f2), new ItemStack(itemstack.func_77973_b(), j1, itemstack.func_77960_j()));

                        if (itemstack.func_77942_o()) {
                            entityitem.func_92059_d().func_77982_d((NBTTagCompound) itemstack.func_77978_p().func_74737_b());
                        }

                        float f3 = 0.05F;

                        entityitem.field_70159_w = (double) ((float) this.field_149922_a.nextGaussian() * f3);
                        entityitem.field_70181_x = (double) ((float) this.field_149922_a.nextGaussian() * f3 + 0.2F);
                        entityitem.field_70179_y = (double) ((float) this.field_149922_a.nextGaussian() * f3);
                        world.func_72838_d(entityitem);
                    }
                }
            }

            world.func_147453_f(i, j, k, block);
        }

        super.func_149749_a(world, i, j, k, block, l);
    }

    @Override
    public int func_149645_b() {
        return 38;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    public static int func_149918_b(int i) {
        return i & 7;
    }

    public static boolean func_149917_c(int i) {
        return (i & 8) != 8;
    }

    @Override
    public boolean func_149740_M() {
        return true;
    }

    @Override
    public int func_149736_g(World world, int i, int j, int k, int l) {
        return Container.func_94526_b(func_149920_e(world, i, j, k));
    }

    public static TileEntityHopper func_149920_e(IBlockAccess iblockaccess, int i, int j, int k) {
        return (TileEntityHopper) iblockaccess.func_147438_o(i, j, k);
    }
}
