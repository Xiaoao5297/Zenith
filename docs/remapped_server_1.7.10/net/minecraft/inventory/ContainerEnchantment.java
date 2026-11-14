package net.minecraft.inventory;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerEnchantment extends Container {

    public IInventory field_75168_e = new ContainerEnchantment$1(this, "Enchant", true, 1);
    private World field_75172_h;
    private int field_75173_i;
    private int field_75170_j;
    private int field_75171_k;
    private Random field_75169_l = new Random();
    public long field_75166_f;
    public int[] field_75167_g = new int[3];

    public ContainerEnchantment(InventoryPlayer inventoryplayer, World world, int i, int j, int k) {
        this.field_75172_h = world;
        this.field_75173_i = i;
        this.field_75170_j = j;
        this.field_75171_k = k;
        this.func_75146_a(new ContainerEnchantment$2(this, this.field_75168_e, 0, 25, 47));

        int l;

        for (l = 0; l < 3; ++l) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.func_75146_a(new Slot(inventoryplayer, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));
            }
        }

        for (l = 0; l < 9; ++l) {
            this.func_75146_a(new Slot(inventoryplayer, l, 8 + l * 18, 142));
        }

    }

    @Override
    public void func_75132_a(ICrafting icrafting) {
        super.func_75132_a(icrafting);
        icrafting.func_71112_a(this, 0, this.field_75167_g[0]);
        icrafting.func_71112_a(this, 1, this.field_75167_g[1]);
        icrafting.func_71112_a(this, 2, this.field_75167_g[2]);
    }

    @Override
    public void func_75142_b() {
        super.func_75142_b();

        for (int i = 0; i < this.field_75149_d.size(); ++i) {
            ICrafting icrafting = (ICrafting) this.field_75149_d.get(i);

            icrafting.func_71112_a(this, 0, this.field_75167_g[0]);
            icrafting.func_71112_a(this, 1, this.field_75167_g[1]);
            icrafting.func_71112_a(this, 2, this.field_75167_g[2]);
        }

    }

    @Override
    public void func_75130_a(IInventory iinventory) {
        if (iinventory == this.field_75168_e) {
            ItemStack itemstack = iinventory.func_70301_a(0);
            int i;

            if (itemstack != null && itemstack.func_77956_u()) {
                this.field_75166_f = this.field_75169_l.nextLong();
                if (!this.field_75172_h.field_72995_K) {
                    i = 0;

                    int j;

                    for (j = -1; j <= 1; ++j) {
                        for (int k = -1; k <= 1; ++k) {
                            if ((j != 0 || k != 0) && this.field_75172_h.func_147437_c(this.field_75173_i + k, this.field_75170_j, this.field_75171_k + j) && this.field_75172_h.func_147437_c(this.field_75173_i + k, this.field_75170_j + 1, this.field_75171_k + j)) {
                                if (this.field_75172_h.func_147439_a(this.field_75173_i + k * 2, this.field_75170_j, this.field_75171_k + j * 2) == Blocks.field_150342_X) {
                                    ++i;
                                }

                                if (this.field_75172_h.func_147439_a(this.field_75173_i + k * 2, this.field_75170_j + 1, this.field_75171_k + j * 2) == Blocks.field_150342_X) {
                                    ++i;
                                }

                                if (k != 0 && j != 0) {
                                    if (this.field_75172_h.func_147439_a(this.field_75173_i + k * 2, this.field_75170_j, this.field_75171_k + j) == Blocks.field_150342_X) {
                                        ++i;
                                    }

                                    if (this.field_75172_h.func_147439_a(this.field_75173_i + k * 2, this.field_75170_j + 1, this.field_75171_k + j) == Blocks.field_150342_X) {
                                        ++i;
                                    }

                                    if (this.field_75172_h.func_147439_a(this.field_75173_i + k, this.field_75170_j, this.field_75171_k + j * 2) == Blocks.field_150342_X) {
                                        ++i;
                                    }

                                    if (this.field_75172_h.func_147439_a(this.field_75173_i + k, this.field_75170_j + 1, this.field_75171_k + j * 2) == Blocks.field_150342_X) {
                                        ++i;
                                    }
                                }
                            }
                        }
                    }

                    for (j = 0; j < 3; ++j) {
                        this.field_75167_g[j] = EnchantmentHelper.func_77514_a(this.field_75169_l, j, i, itemstack);
                    }

                    this.func_75142_b();
                }
            } else {
                for (i = 0; i < 3; ++i) {
                    this.field_75167_g[i] = 0;
                }
            }
        }

    }

    @Override
    public boolean func_75140_a(EntityPlayer entityplayer, int i) {
        ItemStack itemstack = this.field_75168_e.func_70301_a(0);

        if (this.field_75167_g[i] > 0 && itemstack != null && (entityplayer.field_71068_ca >= this.field_75167_g[i] || entityplayer.field_71075_bZ.field_75098_d)) {
            if (!this.field_75172_h.field_72995_K) {
                List list = EnchantmentHelper.func_77513_b(this.field_75169_l, itemstack, this.field_75167_g[i]);
                boolean flag = itemstack.func_77973_b() == Items.field_151122_aG;

                if (list != null) {
                    entityplayer.func_82242_a(-this.field_75167_g[i]);
                    if (flag) {
                        itemstack.func_150996_a(Items.field_151134_bR);
                    }

                    int j = flag && list.size() > 1 ? this.field_75169_l.nextInt(list.size()) : -1;

                    for (int k = 0; k < list.size(); ++k) {
                        EnchantmentData enchantmentdata = (EnchantmentData) list.get(k);

                        if (!flag || k != j) {
                            if (flag) {
                                Items.field_151134_bR.func_92115_a(itemstack, enchantmentdata);
                            } else {
                                itemstack.func_77966_a(enchantmentdata.field_76302_b, enchantmentdata.field_76303_c);
                            }
                        }
                    }

                    this.func_75130_a(this.field_75168_e);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void func_75134_a(EntityPlayer entityplayer) {
        super.func_75134_a(entityplayer);
        if (!this.field_75172_h.field_72995_K) {
            ItemStack itemstack = this.field_75168_e.func_70304_b(0);

            if (itemstack != null) {
                entityplayer.func_71019_a(itemstack, false);
            }

        }
    }

    @Override
    public boolean func_75145_c(EntityPlayer entityplayer) {
        return this.field_75172_h.func_147439_a(this.field_75173_i, this.field_75170_j, this.field_75171_k) != Blocks.field_150381_bn ? false : entityplayer.func_70092_e((double) this.field_75173_i + 0.5D, (double) this.field_75170_j + 0.5D, (double) this.field_75171_k + 0.5D) <= 64.0D;
    }

    @Override
    public ItemStack func_82846_b(EntityPlayer entityplayer, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.field_75151_b.get(i);

        if (slot != null && slot.func_75216_d()) {
            ItemStack itemstack1 = slot.func_75211_c();

            itemstack = itemstack1.func_77946_l();
            if (i == 0) {
                if (!this.func_75135_a(itemstack1, 1, 37, true)) {
                    return null;
                }
            } else {
                if (((Slot) this.field_75151_b.get(0)).func_75216_d() || !((Slot) this.field_75151_b.get(0)).func_75214_a(itemstack1)) {
                    return null;
                }

                if (itemstack1.func_77942_o() && itemstack1.field_77994_a == 1) {
                    ((Slot) this.field_75151_b.get(0)).func_75215_d(itemstack1.func_77946_l());
                    itemstack1.field_77994_a = 0;
                } else if (itemstack1.field_77994_a >= 1) {
                    ((Slot) this.field_75151_b.get(0)).func_75215_d(new ItemStack(itemstack1.func_77973_b(), 1, itemstack1.func_77960_j()));
                    --itemstack1.field_77994_a;
                }
            }

            if (itemstack1.field_77994_a == 0) {
                slot.func_75215_d((ItemStack) null);
            } else {
                slot.func_75218_e();
            }

            if (itemstack1.field_77994_a == itemstack.field_77994_a) {
                return null;
            }

            slot.func_82870_a(entityplayer, itemstack1);
        }

        return itemstack;
    }
}
