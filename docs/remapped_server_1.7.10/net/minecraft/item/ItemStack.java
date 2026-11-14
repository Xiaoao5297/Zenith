package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent$Action;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public final class ItemStack {

    public static final DecimalFormat field_111284_a = new DecimalFormat("#.###");
    public int field_77994_a;
    public int field_77992_b;
    private Item field_151002_e;
    public NBTTagCompound field_77990_d;
    private int field_77991_e;
    private EntityItemFrame field_82843_f;

    public ItemStack(Block block) {
        this(block, 1);
    }

    public ItemStack(Block block, int i) {
        this(block, i, 0);
    }

    public ItemStack(Block block, int i, int j) {
        this(Item.func_150898_a(block), i, j);
    }

    public ItemStack(Item item) {
        this(item, 1);
    }

    public ItemStack(Item item, int i) {
        this(item, i, 0);
    }

    public ItemStack(Item item, int i, int j) {
        this.field_151002_e = item;
        this.field_77994_a = i;
        this.field_77991_e = j;
        if (this.field_77991_e < 0) {
            this.field_77991_e = 0;
        }

    }

    public static ItemStack func_77949_a(NBTTagCompound nbttagcompound) {
        ItemStack itemstack = new ItemStack();

        itemstack.func_77963_c(nbttagcompound);
        return itemstack.func_77973_b() != null ? itemstack : null;
    }

    private ItemStack() {}

    public ItemStack func_77979_a(int i) {
        ItemStack itemstack = new ItemStack(this.field_151002_e, i, this.field_77991_e);

        if (this.field_77990_d != null) {
            itemstack.field_77990_d = (NBTTagCompound) this.field_77990_d.func_74737_b();
        }

        this.field_77994_a -= i;
        return itemstack;
    }

    public Item func_77973_b() {
        return this.field_151002_e;
    }

    public boolean func_77943_a(EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        boolean flag = this.func_77973_b().func_77648_a(this, entityplayer, world, i, j, k, l, f, f1, f2);

        if (flag) {
            entityplayer.func_71064_a(StatList.field_75929_E[Item.func_150891_b(this.field_151002_e)], 1);
        }

        return flag;
    }

    public float func_150997_a(Block block) {
        return this.func_77973_b().func_150893_a(this, block);
    }

    public ItemStack func_77957_a(World world, EntityPlayer entityplayer) {
        return this.func_77973_b().func_77659_a(this, world, entityplayer);
    }

    public ItemStack func_77950_b(World world, EntityPlayer entityplayer) {
        return this.func_77973_b().func_77654_b(this, world, entityplayer);
    }

    public NBTTagCompound func_77955_b(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74777_a("id", (short) Item.func_150891_b(this.field_151002_e));
        nbttagcompound.func_74774_a("Count", (byte) this.field_77994_a);
        nbttagcompound.func_74777_a("Damage", (short) this.field_77991_e);
        if (this.field_77990_d != null) {
            nbttagcompound.func_74782_a("tag", this.field_77990_d);
        }

        return nbttagcompound;
    }

    public void func_77963_c(NBTTagCompound nbttagcompound) {
        this.field_151002_e = Item.func_150899_d(nbttagcompound.func_74765_d("id"));
        this.field_77994_a = nbttagcompound.func_74771_c("Count");
        this.field_77991_e = nbttagcompound.func_74765_d("Damage");
        if (this.field_77991_e < 0) {
            this.field_77991_e = 0;
        }

        if (nbttagcompound.func_150297_b("tag", 10)) {
            this.field_77990_d = nbttagcompound.func_74775_l("tag");
        }

    }

    public int func_77976_d() {
        return this.func_77973_b().func_77639_j();
    }

    public boolean func_77985_e() {
        return this.func_77976_d() > 1 && (!this.func_77984_f() || !this.func_77951_h());
    }

    public boolean func_77984_f() {
        return this.field_151002_e.func_77612_l() <= 0 ? false : !this.func_77942_o() || !this.func_77978_p().func_74767_n("Unbreakable");
    }

    public boolean func_77981_g() {
        return this.field_151002_e.func_77614_k();
    }

    public boolean func_77951_h() {
        return this.func_77984_f() && this.field_77991_e > 0;
    }

    public int func_77952_i() {
        return this.field_77991_e;
    }

    public int func_77960_j() {
        return this.field_77991_e;
    }

    public void func_77964_b(int i) {
        this.field_77991_e = i;
        if (this.field_77991_e < 0) {
            this.field_77991_e = 0;
        }

    }

    public int func_77958_k() {
        return this.field_151002_e.func_77612_l();
    }

    public boolean func_96631_a(int i, Random random) {
        if (!this.func_77984_f()) {
            return false;
        } else {
            if (i > 0) {
                int j = EnchantmentHelper.func_77506_a(Enchantment.field_77347_r.field_77352_x, this);
                int k = 0;

                for (int l = 0; j > 0 && l < i; ++l) {
                    if (EnchantmentDurability.func_92097_a(this, j, random)) {
                        ++k;
                    }
                }

                i -= k;
                if (i <= 0) {
                    return false;
                }
            }

            this.field_77991_e += i;
            return this.field_77991_e > this.func_77958_k();
        }
    }

    public void func_77972_a(int i, EntityLivingBase entitylivingbase) {
        if (!(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer) entitylivingbase).field_71075_bZ.field_75098_d) {
            if (this.func_77984_f()) {
                if (this.func_96631_a(i, entitylivingbase.func_70681_au())) {
                    entitylivingbase.func_70669_a(this);
                    --this.field_77994_a;
                    if (entitylivingbase instanceof EntityPlayer) {
                        EntityPlayer entityplayer = (EntityPlayer) entitylivingbase;

                        entityplayer.func_71064_a(StatList.field_75930_F[Item.func_150891_b(this.field_151002_e)], 1);
                        if (this.field_77994_a == 0 && this.func_77973_b() instanceof ItemBow) {
                            entityplayer.func_71028_bD();
                        }
                    }

                    if (this.field_77994_a < 0) {
                        this.field_77994_a = 0;
                    }

                    this.field_77991_e = 0;
                }

            }
        }
    }

    public void func_77961_a(EntityLivingBase entitylivingbase, EntityPlayer entityplayer) {
        boolean flag = this.field_151002_e.func_77644_a(this, entitylivingbase, entityplayer);

        if (flag) {
            entityplayer.func_71064_a(StatList.field_75929_E[Item.func_150891_b(this.field_151002_e)], 1);
        }

    }

    public void func_150999_a(World world, Block block, int i, int j, int k, EntityPlayer entityplayer) {
        boolean flag = this.field_151002_e.func_150894_a(this, world, block, i, j, k, entityplayer);

        if (flag) {
            entityplayer.func_71064_a(StatList.field_75929_E[Item.func_150891_b(this.field_151002_e)], 1);
        }

    }

    public boolean func_150998_b(Block block) {
        return this.field_151002_e.func_150897_b(block);
    }

    public boolean func_111282_a(EntityPlayer entityplayer, EntityLivingBase entitylivingbase) {
        return this.field_151002_e.func_111207_a(this, entityplayer, entitylivingbase);
    }

    public ItemStack func_77946_l() {
        ItemStack itemstack = new ItemStack(this.field_151002_e, this.field_77994_a, this.field_77991_e);

        if (this.field_77990_d != null) {
            itemstack.field_77990_d = (NBTTagCompound) this.field_77990_d.func_74737_b();
        }

        return itemstack;
    }

    public static boolean func_77970_a(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack == null && itemstack1 == null ? true : (itemstack != null && itemstack1 != null ? (itemstack.field_77990_d == null && itemstack1.field_77990_d != null ? false : itemstack.field_77990_d == null || itemstack.field_77990_d.equals(itemstack1.field_77990_d)) : false);
    }

    public static boolean func_77989_b(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack == null && itemstack1 == null ? true : (itemstack != null && itemstack1 != null ? itemstack.func_77959_d(itemstack1) : false);
    }

    private boolean func_77959_d(ItemStack itemstack) {
        return this.field_77994_a != itemstack.field_77994_a ? false : (this.field_151002_e != itemstack.field_151002_e ? false : (this.field_77991_e != itemstack.field_77991_e ? false : (this.field_77990_d == null && itemstack.field_77990_d != null ? false : this.field_77990_d == null || this.field_77990_d.equals(itemstack.field_77990_d))));
    }

    public boolean func_77969_a(ItemStack itemstack) {
        return this.field_151002_e == itemstack.field_151002_e && this.field_77991_e == itemstack.field_77991_e;
    }

    public String func_77977_a() {
        return this.field_151002_e.func_77667_c(this);
    }

    public static ItemStack func_77944_b(ItemStack itemstack) {
        return itemstack == null ? null : itemstack.func_77946_l();
    }

    public String toString() {
        return this.field_77994_a + "x" + this.field_151002_e.func_77658_a() + "@" + this.field_77991_e;
    }

    public void func_77945_a(World world, Entity entity, int i, boolean flag) {
        if (this.field_77992_b > 0) {
            --this.field_77992_b;
        }

        this.field_151002_e.func_77663_a(this, world, entity, i, flag);
    }

    public void func_77980_a(World world, EntityPlayer entityplayer, int i) {
        entityplayer.func_71064_a(StatList.field_75928_D[Item.func_150891_b(this.field_151002_e)], i);
        this.field_151002_e.func_77622_d(this, world, entityplayer);
    }

    public int func_77988_m() {
        return this.func_77973_b().func_77626_a(this);
    }

    public EnumAction func_77975_n() {
        return this.func_77973_b().func_77661_b(this);
    }

    public void func_77974_b(World world, EntityPlayer entityplayer, int i) {
        this.func_77973_b().func_77615_a(this, world, entityplayer, i);
    }

    public boolean func_77942_o() {
        return this.field_77990_d != null;
    }

    public NBTTagCompound func_77978_p() {
        return this.field_77990_d;
    }

    public NBTTagList func_77986_q() {
        return this.field_77990_d == null ? null : this.field_77990_d.func_150295_c("ench", 10);
    }

    public void func_77982_d(NBTTagCompound nbttagcompound) {
        this.field_77990_d = nbttagcompound;
    }

    public String func_82833_r() {
        String s = this.func_77973_b().func_77653_i(this);

        if (this.field_77990_d != null && this.field_77990_d.func_150297_b("display", 10)) {
            NBTTagCompound nbttagcompound = this.field_77990_d.func_74775_l("display");

            if (nbttagcompound.func_150297_b("Name", 8)) {
                s = nbttagcompound.func_74779_i("Name");
            }
        }

        return s;
    }

    public ItemStack func_151001_c(String s) {
        if (this.field_77990_d == null) {
            this.field_77990_d = new NBTTagCompound();
        }

        if (!this.field_77990_d.func_150297_b("display", 10)) {
            this.field_77990_d.func_74782_a("display", new NBTTagCompound());
        }

        this.field_77990_d.func_74775_l("display").func_74778_a("Name", s);
        return this;
    }

    public void func_135074_t() {
        if (this.field_77990_d != null) {
            if (this.field_77990_d.func_150297_b("display", 10)) {
                NBTTagCompound nbttagcompound = this.field_77990_d.func_74775_l("display");

                nbttagcompound.func_82580_o("Name");
                if (nbttagcompound.func_82582_d()) {
                    this.field_77990_d.func_82580_o("display");
                    if (this.field_77990_d.func_82582_d()) {
                        this.func_77982_d((NBTTagCompound) null);
                    }
                }

            }
        }
    }

    public boolean func_82837_s() {
        return this.field_77990_d == null ? false : (!this.field_77990_d.func_150297_b("display", 10) ? false : this.field_77990_d.func_74775_l("display").func_150297_b("Name", 8));
    }

    public EnumRarity func_77953_t() {
        return this.func_77973_b().func_77613_e(this);
    }

    public boolean func_77956_u() {
        return !this.func_77973_b().func_77616_k(this) ? false : !this.func_77948_v();
    }

    public void func_77966_a(Enchantment enchantment, int i) {
        if (this.field_77990_d == null) {
            this.func_77982_d(new NBTTagCompound());
        }

        if (!this.field_77990_d.func_150297_b("ench", 9)) {
            this.field_77990_d.func_74782_a("ench", new NBTTagList());
        }

        NBTTagList nbttaglist = this.field_77990_d.func_150295_c("ench", 10);
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.func_74777_a("id", (short) enchantment.field_77352_x);
        nbttagcompound.func_74777_a("lvl", (short) ((byte) i));
        nbttaglist.func_74742_a(nbttagcompound);
    }

    public boolean func_77948_v() {
        return this.field_77990_d != null && this.field_77990_d.func_150297_b("ench", 9);
    }

    public void func_77983_a(String s, NBTBase nbtbase) {
        if (this.field_77990_d == null) {
            this.func_77982_d(new NBTTagCompound());
        }

        this.field_77990_d.func_74782_a(s, nbtbase);
    }

    public boolean func_82835_x() {
        return this.func_77973_b().func_82788_x();
    }

    public boolean func_82839_y() {
        return this.field_82843_f != null;
    }

    public void func_82842_a(EntityItemFrame entityitemframe) {
        this.field_82843_f = entityitemframe;
    }

    public EntityItemFrame func_82836_z() {
        return this.field_82843_f;
    }

    public int func_82838_A() {
        return this.func_77942_o() && this.field_77990_d.func_150297_b("RepairCost", 3) ? this.field_77990_d.func_74762_e("RepairCost") : 0;
    }

    public void func_82841_c(int i) {
        if (!this.func_77942_o()) {
            this.field_77990_d = new NBTTagCompound();
        }

        this.field_77990_d.func_74768_a("RepairCost", i);
    }

    public Multimap func_111283_C() {
        Object object;

        if (this.func_77942_o() && this.field_77990_d.func_150297_b("AttributeModifiers", 9)) {
            object = HashMultimap.create();
            NBTTagList nbttaglist = this.field_77990_d.func_150295_c("AttributeModifiers", 10);

            for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.func_150305_b(i);
                AttributeModifier attributemodifier = SharedMonsterAttributes.func_111259_a(nbttagcompound);

                if (attributemodifier.func_111167_a().getLeastSignificantBits() != 0L && attributemodifier.func_111167_a().getMostSignificantBits() != 0L) {
                    ((Multimap) object).put(nbttagcompound.func_74779_i("AttributeName"), attributemodifier);
                }
            }
        } else {
            object = this.func_77973_b().func_111205_h();
        }

        return (Multimap) object;
    }

    public void func_150996_a(Item item) {
        this.field_151002_e = item;
    }

    public IChatComponent func_151000_E() {
        IChatComponent ichatcomponent = (new ChatComponentText("[")).func_150258_a(this.func_82833_r()).func_150258_a("]");

        if (this.field_151002_e != null) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            this.func_77955_b(nbttagcompound);
            ichatcomponent.func_150256_b().func_150209_a(new HoverEvent(HoverEvent$Action.SHOW_ITEM, new ChatComponentText(nbttagcompound.toString())));
            ichatcomponent.func_150256_b().func_150238_a(this.func_77953_t().field_77937_e);
        }

        return ichatcomponent;
    }
}
