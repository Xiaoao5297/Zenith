package net.minecraft.entity.player;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ReportedException;

public class InventoryPlayer implements IInventory {

    public ItemStack[] field_70462_a = new ItemStack[36];
    public ItemStack[] field_70460_b = new ItemStack[4];
    public int field_70461_c;
    public EntityPlayer field_70458_d;
    private ItemStack field_70457_g;
    public boolean field_70459_e;

    public InventoryPlayer(EntityPlayer entityplayer) {
        this.field_70458_d = entityplayer;
    }

    public ItemStack func_70448_g() {
        return this.field_70461_c < 9 && this.field_70461_c >= 0 ? this.field_70462_a[this.field_70461_c] : null;
    }

    public static int func_70451_h() {
        return 9;
    }

    private int func_146029_c(Item item) {
        for (int i = 0; i < this.field_70462_a.length; ++i) {
            if (this.field_70462_a[i] != null && this.field_70462_a[i].func_77973_b() == item) {
                return i;
            }
        }

        return -1;
    }

    private int func_70432_d(ItemStack itemstack) {
        for (int i = 0; i < this.field_70462_a.length; ++i) {
            if (this.field_70462_a[i] != null && this.field_70462_a[i].func_77973_b() == itemstack.func_77973_b() && this.field_70462_a[i].func_77985_e() && this.field_70462_a[i].field_77994_a < this.field_70462_a[i].func_77976_d() && this.field_70462_a[i].field_77994_a < this.func_70297_j_() && (!this.field_70462_a[i].func_77981_g() || this.field_70462_a[i].func_77960_j() == itemstack.func_77960_j()) && ItemStack.func_77970_a(this.field_70462_a[i], itemstack)) {
                return i;
            }
        }

        return -1;
    }

    public int func_70447_i() {
        for (int i = 0; i < this.field_70462_a.length; ++i) {
            if (this.field_70462_a[i] == null) {
                return i;
            }
        }

        return -1;
    }

    public int func_146027_a(Item item, int i) {
        int j = 0;

        ItemStack itemstack;
        int k;

        for (k = 0; k < this.field_70462_a.length; ++k) {
            itemstack = this.field_70462_a[k];
            if (itemstack != null && (item == null || itemstack.func_77973_b() == item) && (i <= -1 || itemstack.func_77960_j() == i)) {
                j += itemstack.field_77994_a;
                this.field_70462_a[k] = null;
            }
        }

        for (k = 0; k < this.field_70460_b.length; ++k) {
            itemstack = this.field_70460_b[k];
            if (itemstack != null && (item == null || itemstack.func_77973_b() == item) && (i <= -1 || itemstack.func_77960_j() == i)) {
                j += itemstack.field_77994_a;
                this.field_70460_b[k] = null;
            }
        }

        if (this.field_70457_g != null) {
            if (item != null && this.field_70457_g.func_77973_b() != item) {
                return j;
            }

            if (i > -1 && this.field_70457_g.func_77960_j() != i) {
                return j;
            }

            j += this.field_70457_g.field_77994_a;
            this.func_70437_b((ItemStack) null);
        }

        return j;
    }

    private int func_70452_e(ItemStack itemstack) {
        Item item = itemstack.func_77973_b();
        int i = itemstack.field_77994_a;
        int j;

        if (itemstack.func_77976_d() == 1) {
            j = this.func_70447_i();
            if (j < 0) {
                return i;
            } else {
                if (this.field_70462_a[j] == null) {
                    this.field_70462_a[j] = ItemStack.func_77944_b(itemstack);
                }

                return 0;
            }
        } else {
            j = this.func_70432_d(itemstack);
            if (j < 0) {
                j = this.func_70447_i();
            }

            if (j < 0) {
                return i;
            } else {
                if (this.field_70462_a[j] == null) {
                    this.field_70462_a[j] = new ItemStack(item, 0, itemstack.func_77960_j());
                    if (itemstack.func_77942_o()) {
                        this.field_70462_a[j].func_77982_d((NBTTagCompound) itemstack.func_77978_p().func_74737_b());
                    }
                }

                int k = i;

                if (i > this.field_70462_a[j].func_77976_d() - this.field_70462_a[j].field_77994_a) {
                    k = this.field_70462_a[j].func_77976_d() - this.field_70462_a[j].field_77994_a;
                }

                if (k > this.func_70297_j_() - this.field_70462_a[j].field_77994_a) {
                    k = this.func_70297_j_() - this.field_70462_a[j].field_77994_a;
                }

                if (k == 0) {
                    return i;
                } else {
                    i -= k;
                    this.field_70462_a[j].field_77994_a += k;
                    this.field_70462_a[j].field_77992_b = 5;
                    return i;
                }
            }
        }
    }

    public void func_70429_k() {
        for (int i = 0; i < this.field_70462_a.length; ++i) {
            if (this.field_70462_a[i] != null) {
                this.field_70462_a[i].func_77945_a(this.field_70458_d.field_70170_p, this.field_70458_d, i, this.field_70461_c == i);
            }
        }

    }

    public boolean func_146026_a(Item item) {
        int i = this.func_146029_c(item);

        if (i < 0) {
            return false;
        } else {
            if (--this.field_70462_a[i].field_77994_a <= 0) {
                this.field_70462_a[i] = null;
            }

            return true;
        }
    }

    public boolean func_146028_b(Item item) {
        int i = this.func_146029_c(item);

        return i >= 0;
    }

    public boolean func_70441_a(ItemStack itemstack) {
        if (itemstack != null && itemstack.field_77994_a != 0 && itemstack.func_77973_b() != null) {
            try {
                int i;

                if (itemstack.func_77951_h()) {
                    i = this.func_70447_i();
                    if (i >= 0) {
                        this.field_70462_a[i] = ItemStack.func_77944_b(itemstack);
                        this.field_70462_a[i].field_77992_b = 5;
                        itemstack.field_77994_a = 0;
                        return true;
                    } else if (this.field_70458_d.field_71075_bZ.field_75098_d) {
                        itemstack.field_77994_a = 0;
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    do {
                        i = itemstack.field_77994_a;
                        itemstack.field_77994_a = this.func_70452_e(itemstack);
                    } while (itemstack.field_77994_a > 0 && itemstack.field_77994_a < i);

                    if (itemstack.field_77994_a == i && this.field_70458_d.field_71075_bZ.field_75098_d) {
                        itemstack.field_77994_a = 0;
                        return true;
                    } else {
                        return itemstack.field_77994_a < i;
                    }
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.func_85055_a(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.func_85058_a("Item being added");

                crashreportcategory.func_71507_a("Item ID", Item.func_150891_b(itemstack.func_77973_b()));
                crashreportcategory.func_71507_a("Item data", itemstack.func_77960_j());
                crashreportcategory.func_71500_a("Item name", new InventoryPlayer$1(this, itemstack));
                throw new ReportedException(crashreport);
            }
        } else {
            return false;
        }
    }

    @Override
    public ItemStack func_70298_a(int i, int j) {
        ItemStack[] aitemstack = this.field_70462_a;

        if (i >= this.field_70462_a.length) {
            aitemstack = this.field_70460_b;
            i -= this.field_70462_a.length;
        }

        if (aitemstack[i] != null) {
            ItemStack itemstack;

            if (aitemstack[i].field_77994_a <= j) {
                itemstack = aitemstack[i];
                aitemstack[i] = null;
                return itemstack;
            } else {
                itemstack = aitemstack[i].func_77979_a(j);
                if (aitemstack[i].field_77994_a == 0) {
                    aitemstack[i] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack func_70304_b(int i) {
        ItemStack[] aitemstack = this.field_70462_a;

        if (i >= this.field_70462_a.length) {
            aitemstack = this.field_70460_b;
            i -= this.field_70462_a.length;
        }

        if (aitemstack[i] != null) {
            ItemStack itemstack = aitemstack[i];

            aitemstack[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void func_70299_a(int i, ItemStack itemstack) {
        ItemStack[] aitemstack = this.field_70462_a;

        if (i >= aitemstack.length) {
            i -= aitemstack.length;
            aitemstack = this.field_70460_b;
        }

        aitemstack[i] = itemstack;
    }

    public float func_146023_a(Block block) {
        float f = 1.0F;

        if (this.field_70462_a[this.field_70461_c] != null) {
            f *= this.field_70462_a[this.field_70461_c].func_150997_a(block);
        }

        return f;
    }

    public NBTTagList func_70442_a(NBTTagList nbttaglist) {
        NBTTagCompound nbttagcompound;
        int i;

        for (i = 0; i < this.field_70462_a.length; ++i) {
            if (this.field_70462_a[i] != null) {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.func_74774_a("Slot", (byte) i);
                this.field_70462_a[i].func_77955_b(nbttagcompound);
                nbttaglist.func_74742_a(nbttagcompound);
            }
        }

        for (i = 0; i < this.field_70460_b.length; ++i) {
            if (this.field_70460_b[i] != null) {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.func_74774_a("Slot", (byte) (i + 100));
                this.field_70460_b[i].func_77955_b(nbttagcompound);
                nbttaglist.func_74742_a(nbttagcompound);
            }
        }

        return nbttaglist;
    }

    public void func_70443_b(NBTTagList nbttaglist) {
        this.field_70462_a = new ItemStack[36];
        this.field_70460_b = new ItemStack[4];

        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.func_150305_b(i);
            int j = nbttagcompound.func_74771_c("Slot") & 255;
            ItemStack itemstack = ItemStack.func_77949_a(nbttagcompound);

            if (itemstack != null) {
                if (j >= 0 && j < this.field_70462_a.length) {
                    this.field_70462_a[j] = itemstack;
                }

                if (j >= 100 && j < this.field_70460_b.length + 100) {
                    this.field_70460_b[j - 100] = itemstack;
                }
            }
        }

    }

    @Override
    public int func_70302_i_() {
        return this.field_70462_a.length + 4;
    }

    @Override
    public ItemStack func_70301_a(int i) {
        ItemStack[] aitemstack = this.field_70462_a;

        if (i >= aitemstack.length) {
            i -= aitemstack.length;
            aitemstack = this.field_70460_b;
        }

        return aitemstack[i];
    }

    @Override
    public String func_145825_b() {
        return "container.inventory";
    }

    @Override
    public boolean func_145818_k_() {
        return false;
    }

    @Override
    public int func_70297_j_() {
        return 64;
    }

    public boolean func_146025_b(Block block) {
        if (block.func_149688_o().func_76229_l()) {
            return true;
        } else {
            ItemStack itemstack = this.func_70301_a(this.field_70461_c);

            return itemstack != null ? itemstack.func_150998_b(block) : false;
        }
    }

    public ItemStack func_70440_f(int i) {
        return this.field_70460_b[i];
    }

    public int func_70430_l() {
        int i = 0;

        for (int j = 0; j < this.field_70460_b.length; ++j) {
            if (this.field_70460_b[j] != null && this.field_70460_b[j].func_77973_b() instanceof ItemArmor) {
                int k = ((ItemArmor) this.field_70460_b[j].func_77973_b()).field_77879_b;

                i += k;
            }
        }

        return i;
    }

    public void func_70449_g(float f) {
        f /= 4.0F;
        if (f < 1.0F) {
            f = 1.0F;
        }

        for (int i = 0; i < this.field_70460_b.length; ++i) {
            if (this.field_70460_b[i] != null && this.field_70460_b[i].func_77973_b() instanceof ItemArmor) {
                this.field_70460_b[i].func_77972_a((int) f, this.field_70458_d);
                if (this.field_70460_b[i].field_77994_a == 0) {
                    this.field_70460_b[i] = null;
                }
            }
        }

    }

    public void func_70436_m() {
        int i;

        for (i = 0; i < this.field_70462_a.length; ++i) {
            if (this.field_70462_a[i] != null) {
                this.field_70458_d.func_146097_a(this.field_70462_a[i], true, false);
                this.field_70462_a[i] = null;
            }
        }

        for (i = 0; i < this.field_70460_b.length; ++i) {
            if (this.field_70460_b[i] != null) {
                this.field_70458_d.func_146097_a(this.field_70460_b[i], true, false);
                this.field_70460_b[i] = null;
            }
        }

    }

    @Override
    public void func_70296_d() {
        this.field_70459_e = true;
    }

    public void func_70437_b(ItemStack itemstack) {
        this.field_70457_g = itemstack;
    }

    public ItemStack func_70445_o() {
        return this.field_70457_g;
    }

    @Override
    public boolean func_70300_a(EntityPlayer entityplayer) {
        return this.field_70458_d.field_70128_L ? false : entityplayer.func_70068_e(this.field_70458_d) <= 64.0D;
    }

    public boolean func_70431_c(ItemStack itemstack) {
        int i;

        for (i = 0; i < this.field_70460_b.length; ++i) {
            if (this.field_70460_b[i] != null && this.field_70460_b[i].func_77969_a(itemstack)) {
                return true;
            }
        }

        for (i = 0; i < this.field_70462_a.length; ++i) {
            if (this.field_70462_a[i] != null && this.field_70462_a[i].func_77969_a(itemstack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void func_70295_k_() {}

    @Override
    public void func_70305_f() {}

    @Override
    public boolean func_94041_b(int i, ItemStack itemstack) {
        return true;
    }

    public void func_70455_b(InventoryPlayer inventoryplayer) {
        int i;

        for (i = 0; i < this.field_70462_a.length; ++i) {
            this.field_70462_a[i] = ItemStack.func_77944_b(inventoryplayer.field_70462_a[i]);
        }

        for (i = 0; i < this.field_70460_b.length; ++i) {
            this.field_70460_b[i] = ItemStack.func_77944_b(inventoryplayer.field_70460_b[i]);
        }

        this.field_70461_c = inventoryplayer.field_70461_c;
    }
}
