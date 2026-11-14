package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityFurnace extends TileEntity implements ISidedInventory {

    private static final int[] field_145962_k = new int[]{0};
    private static final int[] field_145959_l = new int[]{2, 1};
    private static final int[] field_145960_m = new int[]{1};
    private ItemStack[] field_145957_n = new ItemStack[3];
    public int field_145956_a;
    public int field_145963_i;
    public int field_145961_j;
    private String field_145958_o;

    public TileEntityFurnace() {}

    @Override
    public int func_70302_i_() {
        return this.field_145957_n.length;
    }

    @Override
    public ItemStack func_70301_a(int i) {
        return this.field_145957_n[i];
    }

    @Override
    public ItemStack func_70298_a(int i, int j) {
        if (this.field_145957_n[i] != null) {
            ItemStack itemstack;

            if (this.field_145957_n[i].field_77994_a <= j) {
                itemstack = this.field_145957_n[i];
                this.field_145957_n[i] = null;
                return itemstack;
            } else {
                itemstack = this.field_145957_n[i].func_77979_a(j);
                if (this.field_145957_n[i].field_77994_a == 0) {
                    this.field_145957_n[i] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack func_70304_b(int i) {
        if (this.field_145957_n[i] != null) {
            ItemStack itemstack = this.field_145957_n[i];

            this.field_145957_n[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void func_70299_a(int i, ItemStack itemstack) {
        this.field_145957_n[i] = itemstack;
        if (itemstack != null && itemstack.field_77994_a > this.func_70297_j_()) {
            itemstack.field_77994_a = this.func_70297_j_();
        }

    }

    @Override
    public String func_145825_b() {
        return this.func_145818_k_() ? this.field_145958_o : "container.furnace";
    }

    @Override
    public boolean func_145818_k_() {
        return this.field_145958_o != null && this.field_145958_o.length() > 0;
    }

    public void func_145951_a(String s) {
        this.field_145958_o = s;
    }

    @Override
    public void func_145839_a(NBTTagCompound nbttagcompound) {
        super.func_145839_a(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.func_150295_c("Items", 10);

        this.field_145957_n = new ItemStack[this.func_70302_i_()];

        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
            byte b0 = nbttagcompound1.func_74771_c("Slot");

            if (b0 >= 0 && b0 < this.field_145957_n.length) {
                this.field_145957_n[b0] = ItemStack.func_77949_a(nbttagcompound1);
            }
        }

        this.field_145956_a = nbttagcompound.func_74765_d("BurnTime");
        this.field_145961_j = nbttagcompound.func_74765_d("CookTime");
        this.field_145963_i = func_145952_a(this.field_145957_n[1]);
        if (nbttagcompound.func_150297_b("CustomName", 8)) {
            this.field_145958_o = nbttagcompound.func_74779_i("CustomName");
        }

    }

    @Override
    public void func_145841_b(NBTTagCompound nbttagcompound) {
        super.func_145841_b(nbttagcompound);
        nbttagcompound.func_74777_a("BurnTime", (short) this.field_145956_a);
        nbttagcompound.func_74777_a("CookTime", (short) this.field_145961_j);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.field_145957_n.length; ++i) {
            if (this.field_145957_n[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.func_74774_a("Slot", (byte) i);
                this.field_145957_n[i].func_77955_b(nbttagcompound1);
                nbttaglist.func_74742_a(nbttagcompound1);
            }
        }

        nbttagcompound.func_74782_a("Items", nbttaglist);
        if (this.func_145818_k_()) {
            nbttagcompound.func_74778_a("CustomName", this.field_145958_o);
        }

    }

    @Override
    public int func_70297_j_() {
        return 64;
    }

    public boolean func_145950_i() {
        return this.field_145956_a > 0;
    }

    @Override
    public void func_145845_h() {
        boolean flag = this.field_145956_a > 0;
        boolean flag1 = false;

        if (this.field_145956_a > 0) {
            --this.field_145956_a;
        }

        if (!this.field_145850_b.field_72995_K) {
            if (this.field_145956_a != 0 || this.field_145957_n[1] != null && this.field_145957_n[0] != null) {
                if (this.field_145956_a == 0 && this.func_145948_k()) {
                    this.field_145963_i = this.field_145956_a = func_145952_a(this.field_145957_n[1]);
                    if (this.field_145956_a > 0) {
                        flag1 = true;
                        if (this.field_145957_n[1] != null) {
                            --this.field_145957_n[1].field_77994_a;
                            if (this.field_145957_n[1].field_77994_a == 0) {
                                Item item = this.field_145957_n[1].func_77973_b().func_77668_q();

                                this.field_145957_n[1] = item != null ? new ItemStack(item) : null;
                            }
                        }
                    }
                }

                if (this.func_145950_i() && this.func_145948_k()) {
                    ++this.field_145961_j;
                    if (this.field_145961_j == 200) {
                        this.field_145961_j = 0;
                        this.func_145949_j();
                        flag1 = true;
                    }
                } else {
                    this.field_145961_j = 0;
                }
            }

            if (flag != this.field_145956_a > 0) {
                flag1 = true;
                BlockFurnace.func_149931_a(this.field_145956_a > 0, this.field_145850_b, this.field_145851_c, this.field_145848_d, this.field_145849_e);
            }
        }

        if (flag1) {
            this.func_70296_d();
        }

    }

    private boolean func_145948_k() {
        if (this.field_145957_n[0] == null) {
            return false;
        } else {
            ItemStack itemstack = FurnaceRecipes.func_77602_a().func_151395_a(this.field_145957_n[0]);

            return itemstack == null ? false : (this.field_145957_n[2] == null ? true : (!this.field_145957_n[2].func_77969_a(itemstack) ? false : (this.field_145957_n[2].field_77994_a < this.func_70297_j_() && this.field_145957_n[2].field_77994_a < this.field_145957_n[2].func_77976_d() ? true : this.field_145957_n[2].field_77994_a < itemstack.func_77976_d())));
        }
    }

    public void func_145949_j() {
        if (this.func_145948_k()) {
            ItemStack itemstack = FurnaceRecipes.func_77602_a().func_151395_a(this.field_145957_n[0]);

            if (this.field_145957_n[2] == null) {
                this.field_145957_n[2] = itemstack.func_77946_l();
            } else if (this.field_145957_n[2].func_77973_b() == itemstack.func_77973_b()) {
                ++this.field_145957_n[2].field_77994_a;
            }

            --this.field_145957_n[0].field_77994_a;
            if (this.field_145957_n[0].field_77994_a <= 0) {
                this.field_145957_n[0] = null;
            }

        }
    }

    public static int func_145952_a(ItemStack itemstack) {
        if (itemstack == null) {
            return 0;
        } else {
            Item item = itemstack.func_77973_b();

            if (item instanceof ItemBlock && Block.func_149634_a(item) != Blocks.field_150350_a) {
                Block block = Block.func_149634_a(item);

                if (block == Blocks.field_150376_bx) {
                    return 150;
                }

                if (block.func_149688_o() == Material.field_151575_d) {
                    return 300;
                }

                if (block == Blocks.field_150402_ci) {
                    return 16000;
                }
            }

            return item instanceof ItemTool && ((ItemTool) item).func_77861_e().equals("WOOD") ? 200 : (item instanceof ItemSword && ((ItemSword) item).func_150932_j().equals("WOOD") ? 200 : (item instanceof ItemHoe && ((ItemHoe) item).func_77842_f().equals("WOOD") ? 200 : (item == Items.field_151055_y ? 100 : (item == Items.field_151044_h ? 1600 : (item == Items.field_151129_at ? 20000 : (item == Item.func_150898_a(Blocks.field_150345_g) ? 100 : (item == Items.field_151072_bj ? 2400 : 0)))))));
        }
    }

    public static boolean func_145954_b(ItemStack itemstack) {
        return func_145952_a(itemstack) > 0;
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
        return i == 2 ? false : (i == 1 ? func_145954_b(itemstack) : true);
    }

    @Override
    public int[] func_94128_d(int i) {
        return i == 0 ? TileEntityFurnace.field_145959_l : (i == 1 ? TileEntityFurnace.field_145962_k : TileEntityFurnace.field_145960_m);
    }

    @Override
    public boolean func_102007_a(int i, ItemStack itemstack, int j) {
        return this.func_94041_b(i, itemstack);
    }

    @Override
    public boolean func_102008_b(int i, ItemStack itemstack, int j) {
        return j != 0 || i != 1 || itemstack.func_77973_b() == Items.field_151133_ar;
    }
}
