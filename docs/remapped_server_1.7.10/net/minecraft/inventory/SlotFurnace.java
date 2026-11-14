package net.minecraft.inventory;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;

public class SlotFurnace extends Slot {

    private EntityPlayer field_75229_a;
    private int field_75228_b;

    public SlotFurnace(EntityPlayer entityplayer, IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
        this.field_75229_a = entityplayer;
    }

    @Override
    public boolean func_75214_a(ItemStack itemstack) {
        return false;
    }

    @Override
    public ItemStack func_75209_a(int i) {
        if (this.func_75216_d()) {
            this.field_75228_b += Math.min(i, this.func_75211_c().field_77994_a);
        }

        return super.func_75209_a(i);
    }

    @Override
    public void func_82870_a(EntityPlayer entityplayer, ItemStack itemstack) {
        this.func_75208_c(itemstack);
        super.func_82870_a(entityplayer, itemstack);
    }

    @Override
    protected void func_75210_a(ItemStack itemstack, int i) {
        this.field_75228_b += i;
        this.func_75208_c(itemstack);
    }

    @Override
    protected void func_75208_c(ItemStack itemstack) {
        itemstack.func_77980_a(this.field_75229_a.field_70170_p, this.field_75229_a, this.field_75228_b);
        if (!this.field_75229_a.field_70170_p.field_72995_K) {
            int i = this.field_75228_b;
            float f = FurnaceRecipes.func_77602_a().func_151398_b(itemstack);
            int j;

            if (f == 0.0F) {
                i = 0;
            } else if (f < 1.0F) {
                j = MathHelper.func_76141_d((float) i * f);
                if (j < MathHelper.func_76123_f((float) i * f) && (float) Math.random() < (float) i * f - (float) j) {
                    ++j;
                }

                i = j;
            }

            while (i > 0) {
                j = EntityXPOrb.func_70527_a(i);
                i -= j;
                this.field_75229_a.field_70170_p.func_72838_d(new EntityXPOrb(this.field_75229_a.field_70170_p, this.field_75229_a.field_70165_t, this.field_75229_a.field_70163_u + 0.5D, this.field_75229_a.field_70161_v + 0.5D, j));
            }
        }

        this.field_75228_b = 0;
        if (itemstack.func_77973_b() == Items.field_151042_j) {
            this.field_75229_a.func_71064_a(AchievementList.field_76016_k, 1);
        }

        if (itemstack.func_77973_b() == Items.field_151101_aQ) {
            this.field_75229_a.func_71064_a(AchievementList.field_76026_p, 1);
        }

    }
}
