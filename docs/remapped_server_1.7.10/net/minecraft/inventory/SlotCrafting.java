package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item$ToolMaterial;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.stats.AchievementList;

public class SlotCrafting extends Slot {

    private final IInventory field_75239_a;
    private EntityPlayer field_75238_b;
    private int field_75237_g;

    public SlotCrafting(EntityPlayer entityplayer, IInventory iinventory, IInventory iinventory1, int i, int j, int k) {
        super(iinventory1, i, j, k);
        this.field_75238_b = entityplayer;
        this.field_75239_a = iinventory;
    }

    @Override
    public boolean func_75214_a(ItemStack itemstack) {
        return false;
    }

    @Override
    public ItemStack func_75209_a(int i) {
        if (this.func_75216_d()) {
            this.field_75237_g += Math.min(i, this.func_75211_c().field_77994_a);
        }

        return super.func_75209_a(i);
    }

    @Override
    protected void func_75210_a(ItemStack itemstack, int i) {
        this.field_75237_g += i;
        this.func_75208_c(itemstack);
    }

    @Override
    protected void func_75208_c(ItemStack itemstack) {
        itemstack.func_77980_a(this.field_75238_b.field_70170_p, this.field_75238_b, this.field_75237_g);
        this.field_75237_g = 0;
        if (itemstack.func_77973_b() == Item.func_150898_a(Blocks.field_150462_ai)) {
            this.field_75238_b.func_71064_a(AchievementList.field_76017_h, 1);
        }

        if (itemstack.func_77973_b() instanceof ItemPickaxe) {
            this.field_75238_b.func_71064_a(AchievementList.field_76018_i, 1);
        }

        if (itemstack.func_77973_b() == Item.func_150898_a(Blocks.field_150460_al)) {
            this.field_75238_b.func_71064_a(AchievementList.field_76015_j, 1);
        }

        if (itemstack.func_77973_b() instanceof ItemHoe) {
            this.field_75238_b.func_71064_a(AchievementList.field_76013_l, 1);
        }

        if (itemstack.func_77973_b() == Items.field_151025_P) {
            this.field_75238_b.func_71064_a(AchievementList.field_76014_m, 1);
        }

        if (itemstack.func_77973_b() == Items.field_151105_aU) {
            this.field_75238_b.func_71064_a(AchievementList.field_76011_n, 1);
        }

        if (itemstack.func_77973_b() instanceof ItemPickaxe && ((ItemPickaxe) itemstack.func_77973_b()).func_150913_i() != Item$ToolMaterial.WOOD) {
            this.field_75238_b.func_71064_a(AchievementList.field_76012_o, 1);
        }

        if (itemstack.func_77973_b() instanceof ItemSword) {
            this.field_75238_b.func_71064_a(AchievementList.field_76024_r, 1);
        }

        if (itemstack.func_77973_b() == Item.func_150898_a(Blocks.field_150381_bn)) {
            this.field_75238_b.func_71064_a(AchievementList.field_75998_D, 1);
        }

        if (itemstack.func_77973_b() == Item.func_150898_a(Blocks.field_150342_X)) {
            this.field_75238_b.func_71064_a(AchievementList.field_76000_F, 1);
        }

    }

    @Override
    public void func_82870_a(EntityPlayer entityplayer, ItemStack itemstack) {
        this.func_75208_c(itemstack);

        for (int i = 0; i < this.field_75239_a.func_70302_i_(); ++i) {
            ItemStack itemstack1 = this.field_75239_a.func_70301_a(i);

            if (itemstack1 != null) {
                this.field_75239_a.func_70298_a(i, 1);
                if (itemstack1.func_77973_b().func_77634_r()) {
                    ItemStack itemstack2 = new ItemStack(itemstack1.func_77973_b().func_77668_q());

                    if (!itemstack1.func_77973_b().func_77630_h(itemstack1) || !this.field_75238_b.field_71071_by.func_70441_a(itemstack2)) {
                        if (this.field_75239_a.func_70301_a(i) == null) {
                            this.field_75239_a.func_70299_a(i, itemstack2);
                        } else {
                            this.field_75238_b.func_71019_a(itemstack2, false);
                        }
                    }
                }
            }
        }

    }
}
