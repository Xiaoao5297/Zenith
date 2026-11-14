package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockChest extends BlockContainer {

    private final Random field_149955_b = new Random();
    public final int field_149956_a;

    protected BlockChest(int i) {
        super(Material.field_151575_d);
        this.field_149956_a = i;
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
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        if (iblockaccess.func_147439_a(i, j, k - 1) == this) {
            this.func_149676_a(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
        } else if (iblockaccess.func_147439_a(i, j, k + 1) == this) {
            this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
        } else if (iblockaccess.func_147439_a(i - 1, j, k) == this) {
            this.func_149676_a(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        } else if (iblockaccess.func_147439_a(i + 1, j, k) == this) {
            this.func_149676_a(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
        } else {
            this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }

    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        super.func_149726_b(world, i, j, k);
        this.func_149954_e(world, i, j, k);
        Block block = world.func_147439_a(i, j, k - 1);
        Block block1 = world.func_147439_a(i, j, k + 1);
        Block block2 = world.func_147439_a(i - 1, j, k);
        Block block3 = world.func_147439_a(i + 1, j, k);

        if (block == this) {
            this.func_149954_e(world, i, j, k - 1);
        }

        if (block1 == this) {
            this.func_149954_e(world, i, j, k + 1);
        }

        if (block2 == this) {
            this.func_149954_e(world, i - 1, j, k);
        }

        if (block3 == this) {
            this.func_149954_e(world, i + 1, j, k);
        }

    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        Block block = world.func_147439_a(i, j, k - 1);
        Block block1 = world.func_147439_a(i, j, k + 1);
        Block block2 = world.func_147439_a(i - 1, j, k);
        Block block3 = world.func_147439_a(i + 1, j, k);
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

        if (block != this && block1 != this && block2 != this && block3 != this) {
            world.func_72921_c(i, j, k, b0, 3);
        } else {
            if ((block == this || block1 == this) && (b0 == 4 || b0 == 5)) {
                if (block == this) {
                    world.func_72921_c(i, j, k - 1, b0, 3);
                } else {
                    world.func_72921_c(i, j, k + 1, b0, 3);
                }

                world.func_72921_c(i, j, k, b0, 3);
            }

            if ((block2 == this || block3 == this) && (b0 == 2 || b0 == 3)) {
                if (block2 == this) {
                    world.func_72921_c(i - 1, j, k, b0, 3);
                } else {
                    world.func_72921_c(i + 1, j, k, b0, 3);
                }

                world.func_72921_c(i, j, k, b0, 3);
            }
        }

        if (itemstack.func_82837_s()) {
            ((TileEntityChest) world.func_147438_o(i, j, k)).func_145976_a(itemstack.func_82833_r());
        }

    }

    public void func_149954_e(World world, int i, int j, int k) {
        if (!world.field_72995_K) {
            Block block = world.func_147439_a(i, j, k - 1);
            Block block1 = world.func_147439_a(i, j, k + 1);
            Block block2 = world.func_147439_a(i - 1, j, k);
            Block block3 = world.func_147439_a(i + 1, j, k);
            boolean flag = true;
            byte b0;
            int l;
            Block block4;
            int i1;
            Block block5;
            boolean flag1;
            int j1;

            if (block != this && block1 != this) {
                if (block2 != this && block3 != this) {
                    b0 = 3;
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
                } else {
                    l = block2 == this ? i - 1 : i + 1;
                    block4 = world.func_147439_a(l, j, k - 1);
                    i1 = block2 == this ? i - 1 : i + 1;
                    block5 = world.func_147439_a(i1, j, k + 1);
                    b0 = 3;
                    flag1 = true;
                    if (block2 == this) {
                        j1 = world.func_72805_g(i - 1, j, k);
                    } else {
                        j1 = world.func_72805_g(i + 1, j, k);
                    }

                    if (j1 == 2) {
                        b0 = 2;
                    }

                    if ((block.func_149730_j() || block4.func_149730_j()) && !block1.func_149730_j() && !block5.func_149730_j()) {
                        b0 = 3;
                    }

                    if ((block1.func_149730_j() || block5.func_149730_j()) && !block.func_149730_j() && !block4.func_149730_j()) {
                        b0 = 2;
                    }
                }
            } else {
                l = block == this ? k - 1 : k + 1;
                block4 = world.func_147439_a(i - 1, j, l);
                i1 = block == this ? k - 1 : k + 1;
                block5 = world.func_147439_a(i + 1, j, i1);
                b0 = 5;
                flag1 = true;
                if (block == this) {
                    j1 = world.func_72805_g(i, j, k - 1);
                } else {
                    j1 = world.func_72805_g(i, j, k + 1);
                }

                if (j1 == 4) {
                    b0 = 4;
                }

                if ((block2.func_149730_j() || block4.func_149730_j()) && !block3.func_149730_j() && !block5.func_149730_j()) {
                    b0 = 5;
                }

                if ((block3.func_149730_j() || block5.func_149730_j()) && !block2.func_149730_j() && !block4.func_149730_j()) {
                    b0 = 4;
                }
            }

            world.func_72921_c(i, j, k, b0, 3);
        }
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        int l = 0;

        if (world.func_147439_a(i - 1, j, k) == this) {
            ++l;
        }

        if (world.func_147439_a(i + 1, j, k) == this) {
            ++l;
        }

        if (world.func_147439_a(i, j, k - 1) == this) {
            ++l;
        }

        if (world.func_147439_a(i, j, k + 1) == this) {
            ++l;
        }

        return l > 1 ? false : (this.func_149952_n(world, i - 1, j, k) ? false : (this.func_149952_n(world, i + 1, j, k) ? false : (this.func_149952_n(world, i, j, k - 1) ? false : !this.func_149952_n(world, i, j, k + 1))));
    }

    private boolean func_149952_n(World world, int i, int j, int k) {
        return world.func_147439_a(i, j, k) != this ? false : (world.func_147439_a(i - 1, j, k) == this ? true : (world.func_147439_a(i + 1, j, k) == this ? true : (world.func_147439_a(i, j, k - 1) == this ? true : world.func_147439_a(i, j, k + 1) == this)));
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        super.func_149695_a(world, i, j, k, block);
        TileEntityChest tileentitychest = (TileEntityChest) world.func_147438_o(i, j, k);

        if (tileentitychest != null) {
            tileentitychest.func_145836_u();
        }

    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        TileEntityChest tileentitychest = (TileEntityChest) world.func_147438_o(i, j, k);

        if (tileentitychest != null) {
            for (int i1 = 0; i1 < tileentitychest.func_70302_i_(); ++i1) {
                ItemStack itemstack = tileentitychest.func_70301_a(i1);

                if (itemstack != null) {
                    float f = this.field_149955_b.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.field_149955_b.nextFloat() * 0.8F + 0.1F;

                    EntityItem entityitem;

                    for (float f2 = this.field_149955_b.nextFloat() * 0.8F + 0.1F; itemstack.field_77994_a > 0; world.func_72838_d(entityitem)) {
                        int j1 = this.field_149955_b.nextInt(21) + 10;

                        if (j1 > itemstack.field_77994_a) {
                            j1 = itemstack.field_77994_a;
                        }

                        itemstack.field_77994_a -= j1;
                        entityitem = new EntityItem(world, (double) ((float) i + f), (double) ((float) j + f1), (double) ((float) k + f2), new ItemStack(itemstack.func_77973_b(), j1, itemstack.func_77960_j()));
                        float f3 = 0.05F;

                        entityitem.field_70159_w = (double) ((float) this.field_149955_b.nextGaussian() * f3);
                        entityitem.field_70181_x = (double) ((float) this.field_149955_b.nextGaussian() * f3 + 0.2F);
                        entityitem.field_70179_y = (double) ((float) this.field_149955_b.nextGaussian() * f3);
                        if (itemstack.func_77942_o()) {
                            entityitem.func_92059_d().func_77982_d((NBTTagCompound) itemstack.func_77978_p().func_74737_b());
                        }
                    }
                }
            }

            world.func_147453_f(i, j, k, block);
        }

        super.func_149749_a(world, i, j, k, block, l);
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else {
            IInventory iinventory = this.func_149951_m(world, i, j, k);

            if (iinventory != null) {
                entityplayer.func_71007_a(iinventory);
            }

            return true;
        }
    }

    public IInventory func_149951_m(World world, int i, int j, int k) {
        Object object = (TileEntityChest) world.func_147438_o(i, j, k);

        if (object == null) {
            return null;
        } else if (world.func_147439_a(i, j + 1, k).func_149721_r()) {
            return null;
        } else if (func_149953_o(world, i, j, k)) {
            return null;
        } else if (world.func_147439_a(i - 1, j, k) == this && (world.func_147439_a(i - 1, j + 1, k).func_149721_r() || func_149953_o(world, i - 1, j, k))) {
            return null;
        } else if (world.func_147439_a(i + 1, j, k) == this && (world.func_147439_a(i + 1, j + 1, k).func_149721_r() || func_149953_o(world, i + 1, j, k))) {
            return null;
        } else if (world.func_147439_a(i, j, k - 1) == this && (world.func_147439_a(i, j + 1, k - 1).func_149721_r() || func_149953_o(world, i, j, k - 1))) {
            return null;
        } else if (world.func_147439_a(i, j, k + 1) == this && (world.func_147439_a(i, j + 1, k + 1).func_149721_r() || func_149953_o(world, i, j, k + 1))) {
            return null;
        } else {
            if (world.func_147439_a(i - 1, j, k) == this) {
                object = new InventoryLargeChest("container.chestDouble", (TileEntityChest) world.func_147438_o(i - 1, j, k), (IInventory) object);
            }

            if (world.func_147439_a(i + 1, j, k) == this) {
                object = new InventoryLargeChest("container.chestDouble", (IInventory) object, (TileEntityChest) world.func_147438_o(i + 1, j, k));
            }

            if (world.func_147439_a(i, j, k - 1) == this) {
                object = new InventoryLargeChest("container.chestDouble", (TileEntityChest) world.func_147438_o(i, j, k - 1), (IInventory) object);
            }

            if (world.func_147439_a(i, j, k + 1) == this) {
                object = new InventoryLargeChest("container.chestDouble", (IInventory) object, (TileEntityChest) world.func_147438_o(i, j, k + 1));
            }

            return (IInventory) object;
        }
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        TileEntityChest tileentitychest = new TileEntityChest();

        return tileentitychest;
    }

    @Override
    public boolean func_149744_f() {
        return this.field_149956_a == 1;
    }

    @Override
    public int func_149709_b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        if (!this.func_149744_f()) {
            return 0;
        } else {
            int i1 = ((TileEntityChest) iblockaccess.func_147438_o(i, j, k)).field_145987_o;

            return MathHelper.func_76125_a(i1, 0, 15);
        }
    }

    @Override
    public int func_149748_c(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return l == 1 ? this.func_149709_b(iblockaccess, i, j, k, l) : 0;
    }

    private static boolean func_149953_o(World world, int i, int j, int k) {
        Iterator iterator = world.func_72872_a(EntityOcelot.class, AxisAlignedBB.func_72330_a((double) i, (double) (j + 1), (double) k, (double) (i + 1), (double) (j + 2), (double) (k + 1))).iterator();

        EntityOcelot entityocelot;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            Entity entity = (Entity) iterator.next();

            entityocelot = (EntityOcelot) entity;
        } while (!entityocelot.func_70906_o());

        return true;
    }

    @Override
    public boolean func_149740_M() {
        return true;
    }

    @Override
    public int func_149736_g(World world, int i, int j, int k, int l) {
        return Container.func_94526_b(this.func_149951_m(world, i, j, k));
    }
}
