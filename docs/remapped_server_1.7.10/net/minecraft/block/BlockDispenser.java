package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistryDefaulted;
import net.minecraft.world.World;

public class BlockDispenser extends BlockContainer {

    public static final IRegistry field_149943_a = new RegistryDefaulted(new BehaviorDefaultDispenseItem());
    protected Random field_149942_b = new Random();

    protected BlockDispenser() {
        super(Material.field_151576_e);
        this.func_149647_a(CreativeTabs.field_78028_d);
    }

    @Override
    public int func_149738_a(World world) {
        return 4;
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        super.func_149726_b(world, i, j, k);
        this.func_149938_m(world, i, j, k);
    }

    private void func_149938_m(World world, int i, int j, int k) {
        if (!world.field_72995_K) {
            Block block = world.func_147439_a(i, j, k - 1);
            Block block1 = world.func_147439_a(i, j, k + 1);
            Block block2 = world.func_147439_a(i - 1, j, k);
            Block block3 = world.func_147439_a(i + 1, j, k);
            byte b0 = 3;

            if (block.func_149730_j() && !block1.func_149730_j()) {
                b0 = 3;
            }

            if (block1.func_149730_j() && !block.func_149730_j()) {
                b0 = 2;
            }

            if (block2.func_149730_j() && !block3.func_149730_j()) {
                b0 = 5;
            }

            if (block3.func_149730_j() && !block2.func_149730_j()) {
                b0 = 4;
            }

            world.func_72921_c(i, j, k, b0, 2);
        }
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else {
            TileEntityDispenser tileentitydispenser = (TileEntityDispenser) world.func_147438_o(i, j, k);

            if (tileentitydispenser != null) {
                entityplayer.func_146102_a(tileentitydispenser);
            }

            return true;
        }
    }

    protected void func_149941_e(World world, int i, int j, int k) {
        BlockSourceImpl blocksourceimpl = new BlockSourceImpl(world, i, j, k);
        TileEntityDispenser tileentitydispenser = (TileEntityDispenser) blocksourceimpl.func_150835_j();

        if (tileentitydispenser != null) {
            int l = tileentitydispenser.func_146017_i();

            if (l < 0) {
                world.func_72926_e(1001, i, j, k, 0);
            } else {
                ItemStack itemstack = tileentitydispenser.func_70301_a(l);
                IBehaviorDispenseItem ibehaviordispenseitem = this.func_149940_a(itemstack);

                if (ibehaviordispenseitem != IBehaviorDispenseItem.field_82483_a) {
                    ItemStack itemstack1 = ibehaviordispenseitem.func_82482_a(blocksourceimpl, itemstack);

                    tileentitydispenser.func_70299_a(l, itemstack1.field_77994_a == 0 ? null : itemstack1);
                }
            }

        }
    }

    protected IBehaviorDispenseItem func_149940_a(ItemStack itemstack) {
        return (IBehaviorDispenseItem) BlockDispenser.field_149943_a.func_82594_a(itemstack.func_77973_b());
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        boolean flag = world.func_72864_z(i, j, k) || world.func_72864_z(i, j + 1, k);
        int l = world.func_72805_g(i, j, k);
        boolean flag1 = (l & 8) != 0;

        if (flag && !flag1) {
            world.func_147464_a(i, j, k, this, this.func_149738_a(world));
            world.func_72921_c(i, j, k, l | 8, 4);
        } else if (!flag && flag1) {
            world.func_72921_c(i, j, k, l & -9, 4);
        }

    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (!world.field_72995_K) {
            this.func_149941_e(world, i, j, k);
        }

    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntityDispenser();
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        int l = BlockPistonBase.func_150071_a(world, i, j, k, entitylivingbase);

        world.func_72921_c(i, j, k, l, 2);
        if (itemstack.func_82837_s()) {
            ((TileEntityDispenser) world.func_147438_o(i, j, k)).func_146018_a(itemstack.func_82833_r());
        }

    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        TileEntityDispenser tileentitydispenser = (TileEntityDispenser) world.func_147438_o(i, j, k);

        if (tileentitydispenser != null) {
            for (int i1 = 0; i1 < tileentitydispenser.func_70302_i_(); ++i1) {
                ItemStack itemstack = tileentitydispenser.func_70301_a(i1);

                if (itemstack != null) {
                    float f = this.field_149942_b.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.field_149942_b.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.field_149942_b.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.field_77994_a > 0) {
                        int j1 = this.field_149942_b.nextInt(21) + 10;

                        if (j1 > itemstack.field_77994_a) {
                            j1 = itemstack.field_77994_a;
                        }

                        itemstack.field_77994_a -= j1;
                        EntityItem entityitem = new EntityItem(world, (double) ((float) i + f), (double) ((float) j + f1), (double) ((float) k + f2), new ItemStack(itemstack.func_77973_b(), j1, itemstack.func_77960_j()));

                        if (itemstack.func_77942_o()) {
                            entityitem.func_92059_d().func_77982_d((NBTTagCompound) itemstack.func_77978_p().func_74737_b());
                        }

                        float f3 = 0.05F;

                        entityitem.field_70159_w = (double) ((float) this.field_149942_b.nextGaussian() * f3);
                        entityitem.field_70181_x = (double) ((float) this.field_149942_b.nextGaussian() * f3 + 0.2F);
                        entityitem.field_70179_y = (double) ((float) this.field_149942_b.nextGaussian() * f3);
                        world.func_72838_d(entityitem);
                    }
                }
            }

            world.func_147453_f(i, j, k, block);
        }

        super.func_149749_a(world, i, j, k, block, l);
    }

    public static IPosition func_149939_a(IBlockSource iblocksource) {
        EnumFacing enumfacing = func_149937_b(iblocksource.func_82620_h());
        double d0 = iblocksource.func_82615_a() + 0.7D * (double) enumfacing.func_82601_c();
        double d1 = iblocksource.func_82617_b() + 0.7D * (double) enumfacing.func_96559_d();
        double d2 = iblocksource.func_82616_c() + 0.7D * (double) enumfacing.func_82599_e();

        return new PositionImpl(d0, d1, d2);
    }

    public static EnumFacing func_149937_b(int i) {
        return EnumFacing.func_82600_a(i & 7);
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
