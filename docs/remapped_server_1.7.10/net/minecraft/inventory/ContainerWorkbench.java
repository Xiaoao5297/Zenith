package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

public class ContainerWorkbench extends Container {

    public InventoryCrafting field_75162_e = new InventoryCrafting(this, 3, 3);
    public IInventory field_75160_f = new InventoryCraftResult();
    private World field_75161_g;
    private int field_75164_h;
    private int field_75165_i;
    private int field_75163_j;

    public ContainerWorkbench(InventoryPlayer inventoryplayer, World world, int i, int j, int k) {
        this.field_75161_g = world;
        this.field_75164_h = i;
        this.field_75165_i = j;
        this.field_75163_j = k;
        this.func_75146_a(new SlotCrafting(inventoryplayer.field_70458_d, this.field_75162_e, this.field_75160_f, 0, 124, 35));

        int l;
        int i1;

        for (l = 0; l < 3; ++l) {
            for (i1 = 0; i1 < 3; ++i1) {
                this.func_75146_a(new Slot(this.field_75162_e, i1 + l * 3, 30 + i1 * 18, 17 + l * 18));
            }
        }

        for (l = 0; l < 3; ++l) {
            for (i1 = 0; i1 < 9; ++i1) {
                this.func_75146_a(new Slot(inventoryplayer, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));
            }
        }

        for (l = 0; l < 9; ++l) {
            this.func_75146_a(new Slot(inventoryplayer, l, 8 + l * 18, 142));
        }

        this.func_75130_a(this.field_75162_e);
    }

    @Override
    public void func_75130_a(IInventory iinventory) {
        this.field_75160_f.func_70299_a(0, CraftingManager.func_77594_a().func_82787_a(this.field_75162_e, this.field_75161_g));
    }

    @Override
    public void func_75134_a(EntityPlayer entityplayer) {
        super.func_75134_a(entityplayer);
        if (!this.field_75161_g.field_72995_K) {
            for (int i = 0; i < 9; ++i) {
                ItemStack itemstack = this.field_75162_e.func_70304_b(i);

                if (itemstack != null) {
                    entityplayer.func_71019_a(itemstack, false);
                }
            }

        }
    }

    @Override
    public boolean func_75145_c(EntityPlayer entityplayer) {
        return this.field_75161_g.func_147439_a(this.field_75164_h, this.field_75165_i, this.field_75163_j) != Blocks.field_150462_ai ? false : entityplayer.func_70092_e((double) this.field_75164_h + 0.5D, (double) this.field_75165_i + 0.5D, (double) this.field_75163_j + 0.5D) <= 64.0D;
    }

    @Override
    public ItemStack func_82846_b(EntityPlayer entityplayer, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.field_75151_b.get(i);

        if (slot != null && slot.func_75216_d()) {
            ItemStack itemstack1 = slot.func_75211_c();

            itemstack = itemstack1.func_77946_l();
            if (i == 0) {
                if (!this.func_75135_a(itemstack1, 10, 46, true)) {
                    return null;
                }

                slot.func_75220_a(itemstack1, itemstack);
            } else if (i >= 10 && i < 37) {
                if (!this.func_75135_a(itemstack1, 37, 46, false)) {
                    return null;
                }
            } else if (i >= 37 && i < 46) {
                if (!this.func_75135_a(itemstack1, 10, 37, false)) {
                    return null;
                }
            } else if (!this.func_75135_a(itemstack1, 10, 46, false)) {
                return null;
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

    @Override
    public boolean func_94530_a(ItemStack itemstack, Slot slot) {
        return slot.field_75224_c != this.field_75160_f && super.func_94530_a(itemstack, slot);
    }
}
