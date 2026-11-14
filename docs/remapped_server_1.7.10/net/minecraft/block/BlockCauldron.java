package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor$ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCauldron extends Block {

    public BlockCauldron() {
        super(Material.field_151573_f);
    }

    @Override
    public void func_149743_a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
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
        this.func_149683_g();
    }

    @Override
    public void func_149683_g() {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public int func_149645_b() {
        return 24;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public void func_149670_a(World world, int i, int j, int k, Entity entity) {
        int l = func_150027_b(world.func_72805_g(i, j, k));
        float f = (float) j + (6.0F + (float) (3 * l)) / 16.0F;

        if (!world.field_72995_K && entity.func_70027_ad() && l > 0 && entity.field_70121_D.field_72338_b <= (double) f) {
            entity.func_70066_B();
            this.func_150024_a(world, i, j, k, l - 1);
        }

    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else {
            ItemStack itemstack = entityplayer.field_71071_by.func_70448_g();

            if (itemstack == null) {
                return true;
            } else {
                int i1 = world.func_72805_g(i, j, k);
                int j1 = func_150027_b(i1);

                if (itemstack.func_77973_b() == Items.field_151131_as) {
                    if (j1 < 3) {
                        if (!entityplayer.field_71075_bZ.field_75098_d) {
                            entityplayer.field_71071_by.func_70299_a(entityplayer.field_71071_by.field_70461_c, new ItemStack(Items.field_151133_ar));
                        }

                        this.func_150024_a(world, i, j, k, 3);
                    }

                    return true;
                } else {
                    if (itemstack.func_77973_b() == Items.field_151069_bo) {
                        if (j1 > 0) {
                            if (!entityplayer.field_71075_bZ.field_75098_d) {
                                ItemStack itemstack1 = new ItemStack(Items.field_151068_bn, 1, 0);

                                if (!entityplayer.field_71071_by.func_70441_a(itemstack1)) {
                                    world.func_72838_d(new EntityItem(world, (double) i + 0.5D, (double) j + 1.5D, (double) k + 0.5D, itemstack1));
                                } else if (entityplayer instanceof EntityPlayerMP) {
                                    ((EntityPlayerMP) entityplayer).func_71120_a(entityplayer.field_71069_bz);
                                }

                                --itemstack.field_77994_a;
                                if (itemstack.field_77994_a <= 0) {
                                    entityplayer.field_71071_by.func_70299_a(entityplayer.field_71071_by.field_70461_c, (ItemStack) null);
                                }
                            }

                            this.func_150024_a(world, i, j, k, j1 - 1);
                        }
                    } else if (j1 > 0 && itemstack.func_77973_b() instanceof ItemArmor && ((ItemArmor) itemstack.func_77973_b()).func_82812_d() == ItemArmor$ArmorMaterial.CLOTH) {
                        ItemArmor itemarmor = (ItemArmor) itemstack.func_77973_b();

                        itemarmor.func_82815_c(itemstack);
                        this.func_150024_a(world, i, j, k, j1 - 1);
                        return true;
                    }

                    return false;
                }
            }
        }
    }

    public void func_150024_a(World world, int i, int j, int k, int l) {
        world.func_72921_c(i, j, k, MathHelper.func_76125_a(l, 0, 3), 2);
        world.func_147453_f(i, j, k, this);
    }

    @Override
    public void func_149639_l(World world, int i, int j, int k) {
        if (world.field_73012_v.nextInt(20) == 1) {
            int l = world.func_72805_g(i, j, k);

            if (l < 3) {
                world.func_72921_c(i, j, k, l + 1, 2);
            }

        }
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151066_bu;
    }

    @Override
    public boolean func_149740_M() {
        return true;
    }

    @Override
    public int func_149736_g(World world, int i, int j, int k, int l) {
        int i1 = world.func_72805_g(i, j, k);

        return func_150027_b(i1);
    }

    public static int func_150027_b(int i) {
        return i;
    }
}
