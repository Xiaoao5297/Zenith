package net.minecraft.village;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MerchantRecipe {

    private ItemStack field_77403_a;
    private ItemStack field_77401_b;
    private ItemStack field_77402_c;
    private int field_77400_d;
    private int field_82786_e;

    public MerchantRecipe(NBTTagCompound nbttagcompound) {
        this.func_77390_a(nbttagcompound);
    }

    public MerchantRecipe(ItemStack itemstack, ItemStack itemstack1, ItemStack itemstack2) {
        this.field_77403_a = itemstack;
        this.field_77401_b = itemstack1;
        this.field_77402_c = itemstack2;
        this.field_82786_e = 7;
    }

    public MerchantRecipe(ItemStack itemstack, ItemStack itemstack1) {
        this(itemstack, (ItemStack) null, itemstack1);
    }

    public MerchantRecipe(ItemStack itemstack, Item item) {
        this(itemstack, new ItemStack(item));
    }

    public ItemStack func_77394_a() {
        return this.field_77403_a;
    }

    public ItemStack func_77396_b() {
        return this.field_77401_b;
    }

    public boolean func_77398_c() {
        return this.field_77401_b != null;
    }

    public ItemStack func_77397_d() {
        return this.field_77402_c;
    }

    public boolean func_77393_a(MerchantRecipe merchantrecipe) {
        return this.field_77403_a.func_77973_b() == merchantrecipe.field_77403_a.func_77973_b() && this.field_77402_c.func_77973_b() == merchantrecipe.field_77402_c.func_77973_b() ? this.field_77401_b == null && merchantrecipe.field_77401_b == null || this.field_77401_b != null && merchantrecipe.field_77401_b != null && this.field_77401_b.func_77973_b() == merchantrecipe.field_77401_b.func_77973_b() : false;
    }

    public boolean func_77391_b(MerchantRecipe merchantrecipe) {
        return this.func_77393_a(merchantrecipe) && (this.field_77403_a.field_77994_a < merchantrecipe.field_77403_a.field_77994_a || this.field_77401_b != null && this.field_77401_b.field_77994_a < merchantrecipe.field_77401_b.field_77994_a);
    }

    public void func_77399_f() {
        ++this.field_77400_d;
    }

    public void func_82783_a(int i) {
        this.field_82786_e += i;
    }

    public boolean func_82784_g() {
        return this.field_77400_d >= this.field_82786_e;
    }

    public void func_77390_a(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.func_74775_l("buy");

        this.field_77403_a = ItemStack.func_77949_a(nbttagcompound1);
        NBTTagCompound nbttagcompound2 = nbttagcompound.func_74775_l("sell");

        this.field_77402_c = ItemStack.func_77949_a(nbttagcompound2);
        if (nbttagcompound.func_150297_b("buyB", 10)) {
            this.field_77401_b = ItemStack.func_77949_a(nbttagcompound.func_74775_l("buyB"));
        }

        if (nbttagcompound.func_150297_b("uses", 99)) {
            this.field_77400_d = nbttagcompound.func_74762_e("uses");
        }

        if (nbttagcompound.func_150297_b("maxUses", 99)) {
            this.field_82786_e = nbttagcompound.func_74762_e("maxUses");
        } else {
            this.field_82786_e = 7;
        }

    }

    public NBTTagCompound func_77395_g() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.func_74782_a("buy", this.field_77403_a.func_77955_b(new NBTTagCompound()));
        nbttagcompound.func_74782_a("sell", this.field_77402_c.func_77955_b(new NBTTagCompound()));
        if (this.field_77401_b != null) {
            nbttagcompound.func_74782_a("buyB", this.field_77401_b.func_77955_b(new NBTTagCompound()));
        }

        nbttagcompound.func_74768_a("uses", this.field_77400_d);
        nbttagcompound.func_74768_a("maxUses", this.field_82786_e);
        return nbttagcompound;
    }
}
