package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class ItemBow extends Item {

    public static final String[] field_94601_a = new String[]{"pulling_0", "pulling_1", "pulling_2"};

    public ItemBow() {
        this.field_77777_bU = 1;
        this.func_77656_e(384);
        this.func_77637_a(CreativeTabs.field_78037_j);
    }

    @Override
    public void func_77615_a(ItemStack itemstack, World world, EntityPlayer entityplayer, int i) {
        boolean flag = entityplayer.field_71075_bZ.field_75098_d || EnchantmentHelper.func_77506_a(Enchantment.field_77342_w.field_77352_x, itemstack) > 0;

        if (flag || entityplayer.field_71071_by.func_146028_b(Items.field_151032_g)) {
            int j = this.func_77626_a(itemstack) - i;
            float f = (float) j / 20.0F;

            f = (f * f + f * 2.0F) / 3.0F;
            if ((double) f < 0.1D) {
                return;
            }

            if (f > 1.0F) {
                f = 1.0F;
            }

            EntityArrow entityarrow = new EntityArrow(world, entityplayer, f * 2.0F);

            if (f == 1.0F) {
                entityarrow.func_70243_d(true);
            }

            int k = EnchantmentHelper.func_77506_a(Enchantment.field_77345_t.field_77352_x, itemstack);

            if (k > 0) {
                entityarrow.func_70239_b(entityarrow.func_70242_d() + (double) k * 0.5D + 0.5D);
            }

            int l = EnchantmentHelper.func_77506_a(Enchantment.field_77344_u.field_77352_x, itemstack);

            if (l > 0) {
                entityarrow.func_70240_a(l);
            }

            if (EnchantmentHelper.func_77506_a(Enchantment.field_77343_v.field_77352_x, itemstack) > 0) {
                entityarrow.func_70015_d(100);
            }

            itemstack.func_77972_a(1, entityplayer);
            world.func_72956_a(entityplayer, "random.bow", 1.0F, 1.0F / (ItemBow.field_77697_d.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
            if (flag) {
                entityarrow.field_70251_a = 2;
            } else {
                entityplayer.field_71071_by.func_146026_a(Items.field_151032_g);
            }

            if (!world.field_72995_K) {
                world.func_72838_d(entityarrow);
            }
        }

    }

    @Override
    public ItemStack func_77654_b(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        return itemstack;
    }

    @Override
    public int func_77626_a(ItemStack itemstack) {
        return 72000;
    }

    @Override
    public EnumAction func_77661_b(ItemStack itemstack) {
        return EnumAction.bow;
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (entityplayer.field_71075_bZ.field_75098_d || entityplayer.field_71071_by.func_146028_b(Items.field_151032_g)) {
            entityplayer.func_71008_a(itemstack, this.func_77626_a(itemstack));
        }

        return itemstack;
    }

    @Override
    public int func_77619_b() {
        return 1;
    }
}
