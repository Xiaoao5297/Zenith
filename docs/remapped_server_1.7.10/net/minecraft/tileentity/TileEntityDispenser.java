package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityDispenser extends TileEntity implements IInventory {

    private ItemStack[] field_146022_i = new ItemStack[9];
    private Random field_146021_j = new Random();
    protected String field_146020_a;

    public TileEntityDispenser() {}

    @Override
    public int func_70302_i_() {
        return 9;
    }

    @Override
    public ItemStack func_70301_a(int i) {
        return this.field_146022_i[i];
    }

    @Override
    public ItemStack func_70298_a(int i, int j) {
        if (this.field_146022_i[i] != null) {
            ItemStack itemstack;

            if (this.field_146022_i[i].field_77994_a <= j) {
                itemstack = this.field_146022_i[i];
                this.field_146022_i[i] = null;
                this.func_70296_d();
                return itemstack;
            } else {
                itemstack = this.field_146022_i[i].func_77979_a(j);
                if (this.field_146022_i[i].field_77994_a == 0) {
                    this.field_146022_i[i] = null;
                }

                this.func_70296_d();
                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack func_70304_b(int i) {
        if (this.field_146022_i[i] != null) {
            ItemStack itemstack = this.field_146022_i[i];

            this.field_146022_i[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public int func_146017_i() {
        int i = -1;
        int j = 1;

        for (int k = 0; k < this.field_146022_i.length; ++k) {
            if (this.field_146022_i[k] != null && this.field_146021_j.nextInt(j++) == 0) {
                i = k;
            }
        }

        return i;
    }

    @Override
    public void func_70299_a(int i, ItemStack itemstack) {
        this.field_146022_i[i] = itemstack;
        if (itemstack != null && itemstack.field_77994_a > this.func_70297_j_()) {
            itemstack.field_77994_a = this.func_70297_j_();
        }

        this.func_70296_d();
    }

    public int func_146019_a(ItemStack itemstack) {
        for (int i = 0; i < this.field_146022_i.length; ++i) {
            if (this.field_146022_i[i] == null || this.field_146022_i[i].func_77973_b() == null) {
                this.func_70299_a(i, itemstack);
                return i;
            }
        }

        return -1;
    }

    @Override
    public String func_145825_b() {
        return this.func_145818_k_() ? this.field_146020_a : "container.dispenser";
    }

    public void func_146018_a(String s) {
        this.field_146020_a = s;
    }

    @Override
    public boolean func_145818_k_() {
        return this.field_146020_a != null;
    }

    @Override
    public void func_145839_a(NBTTagCompound nbttagcompound) {
        super.func_145839_a(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.func_150295_c("Items", 10);

        this.field_146022_i = new ItemStack[this.func_70302_i_()];

        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
            int j = nbttagcompound1.func_74771_c("Slot") & 255;

            if (j >= 0 && j < this.field_146022_i.length) {
                this.field_146022_i[j] = ItemStack.func_77949_a(nbttagcompound1);
            }
        }

        if (nbttagcompound.func_150297_b("CustomName", 8)) {
            this.field_146020_a = nbttagcompound.func_74779_i("CustomName");
        }

    }

    @Override
    public void func_145841_b(NBTTagCompound nbttagcompound) {
        super.func_145841_b(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.field_146022_i.length; ++i) {
            if (this.field_146022_i[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.func_74774_a("Slot", (byte) i);
                this.field_146022_i[i].func_77955_b(nbttagcompound1);
                nbttaglist.func_74742_a(nbttagcompound1);
            }
        }

        nbttagcompound.func_74782_a("Items", nbttaglist);
        if (this.func_145818_k_()) {
            nbttagcompound.func_74778_a("CustomName", this.field_146020_a);
        }

    }

    @Override
    public int func_70297_j_() {
        return 64;
    }

    @Override
    public boolean func_70300_a(EntityPlayer entityplayer) {
        return this.field_145850_b.func_147438_o(this.field_145851_c, this.field_145848_d, this.field_145849_e) != this ? false : entityplayer.func_70092_e((double) this.field_145851_c + 0.5D, (double) this.field_145848_d + 0.5D, (double) this.field_145849_e + 0.5D) <= 64.0D;
    }

    @Override
    public void func_70295_k_() {}

    @Override
    public void func_70305_f() {}

    @Override
    public boolean func_94041_b(int i, ItemStack itemstack) {
        return true;
    }
}
