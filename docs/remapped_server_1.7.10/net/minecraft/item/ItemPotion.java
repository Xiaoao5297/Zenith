package net.minecraft.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemPotion extends Item {

    private HashMap field_77836_a = new HashMap();
    private static final Map field_77835_b = new LinkedHashMap();

    public ItemPotion() {
        this.func_77625_d(1);
        this.func_77627_a(true);
        this.func_77656_e(0);
        this.func_77637_a(CreativeTabs.field_78038_k);
    }

    public List func_77832_l(ItemStack itemstack) {
        if (itemstack.func_77942_o() && itemstack.func_77978_p().func_150297_b("CustomPotionEffects", 9)) {
            ArrayList arraylist = new ArrayList();
            NBTTagList nbttaglist = itemstack.func_77978_p().func_150295_c("CustomPotionEffects", 10);

            for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.func_150305_b(i);
                PotionEffect potioneffect = PotionEffect.func_82722_b(nbttagcompound);

                if (potioneffect != null) {
                    arraylist.add(potioneffect);
                }
            }

            return arraylist;
        } else {
            List list = (List) this.field_77836_a.get(itemstack.func_77960_j());

            if (list == null) {
                list = PotionHelper.func_77917_b(itemstack.func_77960_j(), false);
                this.field_77836_a.put(itemstack.func_77960_j(), list);
            }

            return list;
        }
    }

    public List func_77834_f(int i) {
        List list = (List) this.field_77836_a.get(i);

        if (list == null) {
            list = PotionHelper.func_77917_b(i, false);
            this.field_77836_a.put(i, list);
        }

        return list;
    }

    @Override
    public ItemStack func_77654_b(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (!entityplayer.field_71075_bZ.field_75098_d) {
            --itemstack.field_77994_a;
        }

        if (!world.field_72995_K) {
            List list = this.func_77832_l(itemstack);

            if (list != null) {
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    PotionEffect potioneffect = (PotionEffect) iterator.next();

                    entityplayer.func_70690_d(new PotionEffect(potioneffect));
                }
            }
        }

        if (!entityplayer.field_71075_bZ.field_75098_d) {
            if (itemstack.field_77994_a <= 0) {
                return new ItemStack(Items.field_151069_bo);
            }

            entityplayer.field_71071_by.func_70441_a(new ItemStack(Items.field_151069_bo));
        }

        return itemstack;
    }

    @Override
    public int func_77626_a(ItemStack itemstack) {
        return 32;
    }

    @Override
    public EnumAction func_77661_b(ItemStack itemstack) {
        return EnumAction.drink;
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (func_77831_g(itemstack.func_77960_j())) {
            if (!entityplayer.field_71075_bZ.field_75098_d) {
                --itemstack.field_77994_a;
            }

            world.func_72956_a(entityplayer, "random.bow", 0.5F, 0.4F / (ItemPotion.field_77697_d.nextFloat() * 0.4F + 0.8F));
            if (!world.field_72995_K) {
                world.func_72838_d(new EntityPotion(world, entityplayer, itemstack));
            }

            return itemstack;
        } else {
            entityplayer.func_71008_a(itemstack, this.func_77626_a(itemstack));
            return itemstack;
        }
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        return false;
    }

    public static boolean func_77831_g(int i) {
        return (i & 16384) != 0;
    }

    @Override
    public String func_77653_i(ItemStack itemstack) {
        if (itemstack.func_77960_j() == 0) {
            return StatCollector.func_74838_a("item.emptyPotion.name").trim();
        } else {
            String s = "";

            if (func_77831_g(itemstack.func_77960_j())) {
                s = StatCollector.func_74838_a("potion.prefix.grenade").trim() + " ";
            }

            List list = Items.field_151068_bn.func_77832_l(itemstack);
            String s1;

            if (list != null && !list.isEmpty()) {
                s1 = ((PotionEffect) list.get(0)).func_76453_d();
                s1 = s1 + ".postfix";
                return s + StatCollector.func_74838_a(s1).trim();
            } else {
                s1 = PotionHelper.func_77905_c(itemstack.func_77960_j());
                return StatCollector.func_74838_a(s1).trim() + " " + super.func_77653_i(itemstack);
            }
        }
    }
}
