package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockFurnace extends BlockContainer {

    private final Random field_149933_a = new Random();
    private final boolean field_149932_b;
    private static boolean field_149934_M;

    protected BlockFurnace(boolean flag) {
        super(Material.field_151576_e);
        this.field_149932_b = flag;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Item.func_150898_a(Blocks.field_150460_al);
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        super.func_149726_b(world, i, j, k);
        this.func_149930_e(world, i, j, k);
    }

    private void func_149930_e(World world, int i, int j, int k) {
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
            TileEntityFurnace tileentityfurnace = (TileEntityFurnace) world.func_147438_o(i, j, k);

            if (tileentityfurnace != null) {
                entityplayer.func_146101_a(tileentityfurnace);
            }

            return true;
        }
    }

    public static void func_149931_a(boolean flag, World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);
        TileEntity tileentity = world.func_147438_o(i, j, k);

        BlockFurnace.field_149934_M = true;
        if (flag) {
            world.func_147449_b(i, j, k, Blocks.field_150470_am);
        } else {
            world.func_147449_b(i, j, k, Blocks.field_150460_al);
        }

        BlockFurnace.field_149934_M = false;
        world.func_72921_c(i, j, k, l, 2);
        if (tileentity != null) {
            tileentity.func_145829_t();
            world.func_147455_a(i, j, k, tileentity);
        }

    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntityFurnace();
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        int l = MathHelper.func_76128_c((double) (entitylivingbase.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0) {
            world.func_72921_c(i, j, k, 2, 2);
        }

        if (l == 1) {
            world.func_72921_c(i, j, k, 5, 2);
        }

        if (l == 2) {
            world.func_72921_c(i, j, k, 3, 2);
        }

        if (l == 3) {
            world.func_72921_c(i, j, k, 4, 2);
        }

        if (itemstack.func_82837_s()) {
            ((TileEntityFurnace) world.func_147438_o(i, j, k)).func_145951_a(itemstack.func_82833_r());
        }

    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        if (!BlockFurnace.field_149934_M) {
            TileEntityFurnace tileentityfurnace = (TileEntityFurnace) world.func_147438_o(i, j, k);

            if (tileentityfurnace != null) {
                for (int i1 = 0; i1 < tileentityfurnace.func_70302_i_(); ++i1) {
                    ItemStack itemstack = tileentityfurnace.func_70301_a(i1);

                    if (itemstack != null) {
                        float f = this.field_149933_a.nextFloat() * 0.8F + 0.1F;
                        float f1 = this.field_149933_a.nextFloat() * 0.8F + 0.1F;
                        float f2 = this.field_149933_a.nextFloat() * 0.8F + 0.1F;

                        while (itemstack.field_77994_a > 0) {
                            int j1 = this.field_149933_a.nextInt(21) + 10;

                            if (j1 > itemstack.field_77994_a) {
                                j1 = itemstack.field_77994_a;
                            }

                            itemstack.field_77994_a -= j1;
                            EntityItem entityitem = new EntityItem(world, (double) ((float) i + f), (double) ((float) j + f1), (double) ((float) k + f2), new ItemStack(itemstack.func_77973_b(), j1, itemstack.func_77960_j()));

                            if (itemstack.func_77942_o()) {
                                entityitem.func_92059_d().func_77982_d((NBTTagCompound) itemstack.func_77978_p().func_74737_b());
                            }

                            float f3 = 0.05F;

                            entityitem.field_70159_w = (double) ((float) this.field_149933_a.nextGaussian() * f3);
                            entityitem.field_70181_x = (double) ((float) this.field_149933_a.nextGaussian() * f3 + 0.2F);
                            entityitem.field_70179_y = (double) ((float) this.field_149933_a.nextGaussian() * f3);
                            world.func_72838_d(entityitem);
                        }
                    }
                }

                world.func_147453_f(i, j, k, block);
            }
        }

        super.func_149749_a(world, i, j, k, block, l);
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
