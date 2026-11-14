package net.minecraft.enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.WeightedRandom;

public class EnchantmentHelper {

    private static final Random field_77522_a = new Random();
    private static final EnchantmentHelper$ModifierDamage field_77520_b = new EnchantmentHelper$ModifierDamage((EnchantmentHelper$1) null);
    private static final EnchantmentHelper$ModifierLiving field_77521_c = new EnchantmentHelper$ModifierLiving((EnchantmentHelper$1) null);
    private static final EnchantmentHelper$HurtIterator field_151388_d = new EnchantmentHelper$HurtIterator((EnchantmentHelper$1) null);
    private static final EnchantmentHelper$DamageIterator field_151389_e = new EnchantmentHelper$DamageIterator((EnchantmentHelper$1) null);

    public static int func_77506_a(int i, ItemStack itemstack) {
        if (itemstack == null) {
            return 0;
        } else {
            NBTTagList nbttaglist = itemstack.func_77986_q();

            if (nbttaglist == null) {
                return 0;
            } else {
                for (int j = 0; j < nbttaglist.func_74745_c(); ++j) {
                    short short0 = nbttaglist.func_150305_b(j).func_74765_d("id");
                    short short1 = nbttaglist.func_150305_b(j).func_74765_d("lvl");

                    if (short0 == i) {
                        return short1;
                    }
                }

                return 0;
            }
        }
    }

    public static Map func_82781_a(ItemStack itemstack) {
        LinkedHashMap linkedhashmap = new LinkedHashMap();
        NBTTagList nbttaglist = itemstack.func_77973_b() == Items.field_151134_bR ? Items.field_151134_bR.func_92110_g(itemstack) : itemstack.func_77986_q();

        if (nbttaglist != null) {
            for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
                short short0 = nbttaglist.func_150305_b(i).func_74765_d("id");
                short short1 = nbttaglist.func_150305_b(i).func_74765_d("lvl");

                linkedhashmap.put(Integer.valueOf(short0), Integer.valueOf(short1));
            }
        }

        return linkedhashmap;
    }

    public static void func_82782_a(Map map, ItemStack itemstack) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            int i = (Integer) iterator.next();
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.func_74777_a("id", (short) i);
            nbttagcompound.func_74777_a("lvl", (short) (Integer) map.get(i));
            nbttaglist.func_74742_a(nbttagcompound);
            if (itemstack.func_77973_b() == Items.field_151134_bR) {
                Items.field_151134_bR.func_92115_a(itemstack, new EnchantmentData(i, (Integer) map.get(i)));
            }
        }

        if (nbttaglist.func_74745_c() > 0) {
            if (itemstack.func_77973_b() != Items.field_151134_bR) {
                itemstack.func_77983_a("ench", nbttaglist);
            }
        } else if (itemstack.func_77942_o()) {
            itemstack.func_77978_p().func_82580_o("ench");
        }

    }

    public static int func_77511_a(int i, ItemStack[] aitemstack) {
        if (aitemstack == null) {
            return 0;
        } else {
            int j = 0;
            ItemStack[] aitemstack1 = aitemstack;
            int k = aitemstack.length;

            for (int l = 0; l < k; ++l) {
                ItemStack itemstack = aitemstack1[l];
                int i1 = func_77506_a(i, itemstack);

                if (i1 > j) {
                    j = i1;
                }
            }

            return j;
        }
    }

    private static void func_77518_a(EnchantmentHelper$IModifier enchantmenthelper$imodifier, ItemStack itemstack) {
        if (itemstack != null) {
            NBTTagList nbttaglist = itemstack.func_77986_q();

            if (nbttaglist != null) {
                for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
                    short short0 = nbttaglist.func_150305_b(i).func_74765_d("id");
                    short short1 = nbttaglist.func_150305_b(i).func_74765_d("lvl");

                    if (Enchantment.field_77331_b[short0] != null) {
                        enchantmenthelper$imodifier.func_77493_a(Enchantment.field_77331_b[short0], short1);
                    }
                }

            }
        }
    }

    private static void func_77516_a(EnchantmentHelper$IModifier enchantmenthelper$imodifier, ItemStack[] aitemstack) {
        ItemStack[] aitemstack1 = aitemstack;
        int i = aitemstack.length;

        for (int j = 0; j < i; ++j) {
            ItemStack itemstack = aitemstack1[j];

            func_77518_a(enchantmenthelper$imodifier, itemstack);
        }

    }

    public static int func_77508_a(ItemStack[] aitemstack, DamageSource damagesource) {
        EnchantmentHelper.field_77520_b.field_77497_a = 0;
        EnchantmentHelper.field_77520_b.field_77496_b = damagesource;
        func_77516_a(EnchantmentHelper.field_77520_b, aitemstack);
        if (EnchantmentHelper.field_77520_b.field_77497_a > 25) {
            EnchantmentHelper.field_77520_b.field_77497_a = 25;
        }

        return (EnchantmentHelper.field_77520_b.field_77497_a + 1 >> 1) + EnchantmentHelper.field_77522_a.nextInt((EnchantmentHelper.field_77520_b.field_77497_a >> 1) + 1);
    }

    public static float func_77512_a(EntityLivingBase entitylivingbase, EntityLivingBase entitylivingbase1) {
        return func_152377_a(entitylivingbase.func_70694_bm(), entitylivingbase1.func_70668_bt());
    }

    public static float func_152377_a(ItemStack itemstack, EnumCreatureAttribute enumcreatureattribute) {
        EnchantmentHelper.field_77521_c.field_77495_a = 0.0F;
        EnchantmentHelper.field_77521_c.field_77494_b = enumcreatureattribute;
        func_77518_a(EnchantmentHelper.field_77521_c, itemstack);
        return EnchantmentHelper.field_77521_c.field_77495_a;
    }

    public static void func_151384_a(EntityLivingBase entitylivingbase, Entity entity) {
        EnchantmentHelper.field_151388_d.field_151363_b = entity;
        EnchantmentHelper.field_151388_d.field_151364_a = entitylivingbase;
        func_77516_a(EnchantmentHelper.field_151388_d, entitylivingbase.func_70035_c());
        if (entity instanceof EntityPlayer) {
            func_77518_a(EnchantmentHelper.field_151388_d, entitylivingbase.func_70694_bm());
        }

    }

    public static void func_151385_b(EntityLivingBase entitylivingbase, Entity entity) {
        EnchantmentHelper.field_151389_e.field_151366_a = entitylivingbase;
        EnchantmentHelper.field_151389_e.field_151365_b = entity;
        func_77516_a(EnchantmentHelper.field_151389_e, entitylivingbase.func_70035_c());
        if (entitylivingbase instanceof EntityPlayer) {
            func_77518_a(EnchantmentHelper.field_151389_e, entitylivingbase.func_70694_bm());
        }

    }

    public static int func_77507_b(EntityLivingBase entitylivingbase, EntityLivingBase entitylivingbase1) {
        return func_77506_a(Enchantment.field_77337_m.field_77352_x, entitylivingbase.func_70694_bm());
    }

    public static int func_90036_a(EntityLivingBase entitylivingbase) {
        return func_77506_a(Enchantment.field_77334_n.field_77352_x, entitylivingbase.func_70694_bm());
    }

    public static int func_77501_a(EntityLivingBase entitylivingbase) {
        return func_77511_a(Enchantment.field_77340_h.field_77352_x, entitylivingbase.func_70035_c());
    }

    public static int func_77509_b(EntityLivingBase entitylivingbase) {
        return func_77506_a(Enchantment.field_77349_p.field_77352_x, entitylivingbase.func_70694_bm());
    }

    public static boolean func_77502_d(EntityLivingBase entitylivingbase) {
        return func_77506_a(Enchantment.field_77348_q.field_77352_x, entitylivingbase.func_70694_bm()) > 0;
    }

    public static int func_77517_e(EntityLivingBase entitylivingbase) {
        return func_77506_a(Enchantment.field_77346_s.field_77352_x, entitylivingbase.func_70694_bm());
    }

    public static int func_151386_g(EntityLivingBase entitylivingbase) {
        return func_77506_a(Enchantment.field_151370_z.field_77352_x, entitylivingbase.func_70694_bm());
    }

    public static int func_151387_h(EntityLivingBase entitylivingbase) {
        return func_77506_a(Enchantment.field_151369_A.field_77352_x, entitylivingbase.func_70694_bm());
    }

    public static int func_77519_f(EntityLivingBase entitylivingbase) {
        return func_77506_a(Enchantment.field_77335_o.field_77352_x, entitylivingbase.func_70694_bm());
    }

    public static boolean func_77510_g(EntityLivingBase entitylivingbase) {
        return func_77511_a(Enchantment.field_77341_i.field_77352_x, entitylivingbase.func_70035_c()) > 0;
    }

    public static ItemStack func_92099_a(Enchantment enchantment, EntityLivingBase entitylivingbase) {
        ItemStack[] aitemstack = entitylivingbase.func_70035_c();
        int i = aitemstack.length;

        for (int j = 0; j < i; ++j) {
            ItemStack itemstack = aitemstack[j];

            if (itemstack != null && func_77506_a(enchantment.field_77352_x, itemstack) > 0) {
                return itemstack;
            }
        }

        return null;
    }

    public static int func_77514_a(Random random, int i, int j, ItemStack itemstack) {
        Item item = itemstack.func_77973_b();
        int k = item.func_77619_b();

        if (k <= 0) {
            return 0;
        } else {
            if (j > 15) {
                j = 15;
            }

            int l = random.nextInt(8) + 1 + (j >> 1) + random.nextInt(j + 1);

            return i == 0 ? Math.max(l / 3, 1) : (i == 1 ? l * 2 / 3 + 1 : Math.max(l, j * 2));
        }
    }

    public static ItemStack func_77504_a(Random random, ItemStack itemstack, int i) {
        List list = func_77513_b(random, itemstack, i);
        boolean flag = itemstack.func_77973_b() == Items.field_151122_aG;

        if (flag) {
            itemstack.func_150996_a(Items.field_151134_bR);
        }

        if (list != null) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EnchantmentData enchantmentdata = (EnchantmentData) iterator.next();

                if (flag) {
                    Items.field_151134_bR.func_92115_a(itemstack, enchantmentdata);
                } else {
                    itemstack.func_77966_a(enchantmentdata.field_76302_b, enchantmentdata.field_76303_c);
                }
            }
        }

        return itemstack;
    }

    public static List func_77513_b(Random random, ItemStack itemstack, int i) {
        Item item = itemstack.func_77973_b();
        int j = item.func_77619_b();

        if (j <= 0) {
            return null;
        } else {
            j /= 2;
            j = 1 + random.nextInt((j >> 1) + 1) + random.nextInt((j >> 1) + 1);
            int k = j + i;
            float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
            int l = (int) ((float) k * (1.0F + f) + 0.5F);

            if (l < 1) {
                l = 1;
            }

            ArrayList arraylist = null;
            Map map = func_77505_b(l, itemstack);

            if (map != null && !map.isEmpty()) {
                EnchantmentData enchantmentdata = (EnchantmentData) WeightedRandom.func_76271_a(random, map.values());

                if (enchantmentdata != null) {
                    arraylist = new ArrayList();
                    arraylist.add(enchantmentdata);

                    for (int i1 = l; random.nextInt(50) <= i1; i1 >>= 1) {
                        Iterator iterator = map.keySet().iterator();

                        while (iterator.hasNext()) {
                            Integer integer = (Integer) iterator.next();
                            boolean flag = true;
                            Iterator iterator1 = arraylist.iterator();

                            while (true) {
                                if (iterator1.hasNext()) {
                                    EnchantmentData enchantmentdata1 = (EnchantmentData) iterator1.next();

                                    if (enchantmentdata1.field_76302_b.func_77326_a(Enchantment.field_77331_b[integer])) {
                                        continue;
                                    }

                                    flag = false;
                                }

                                if (!flag) {
                                    iterator.remove();
                                }
                                break;
                            }
                        }

                        if (!map.isEmpty()) {
                            EnchantmentData enchantmentdata2 = (EnchantmentData) WeightedRandom.func_76271_a(random, map.values());

                            arraylist.add(enchantmentdata2);
                        }
                    }
                }
            }

            return arraylist;
        }
    }

    public static Map func_77505_b(int i, ItemStack itemstack) {
        Item item = itemstack.func_77973_b();
        HashMap hashmap = null;
        boolean flag = itemstack.func_77973_b() == Items.field_151122_aG;
        Enchantment[] aenchantment = Enchantment.field_77331_b;
        int j = aenchantment.length;

        for (int k = 0; k < j; ++k) {
            Enchantment enchantment = aenchantment[k];

            if (enchantment != null && (enchantment.field_77351_y.func_77557_a(item) || flag)) {
                for (int l = enchantment.func_77319_d(); l <= enchantment.func_77325_b(); ++l) {
                    if (i >= enchantment.func_77321_a(l) && i <= enchantment.func_77317_b(l)) {
                        if (hashmap == null) {
                            hashmap = new HashMap();
                        }

                        hashmap.put(enchantment.field_77352_x, new EnchantmentData(enchantment, l));
                    }
                }
            }
        }

        return hashmap;
    }
}
