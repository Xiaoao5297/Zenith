package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBeacon;

public class ContainerBeacon extends Container {

    private TileEntityBeacon field_82866_e;
    private final ContainerBeacon$BeaconSlot field_82864_f;
    private int field_82865_g;
    private int field_82867_h;
    private int field_82868_i;

    public ContainerBeacon(InventoryPlayer inventoryplayer, TileEntityBeacon tileentitybeacon) {
        this.field_82866_e = tileentitybeacon;
        this.func_75146_a(this.field_82864_f = new ContainerBeacon$BeaconSlot(this, tileentitybeacon, 0, 136, 110));
        byte b0 = 36;
        short short0 = 137;

        int i;

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.func_75146_a(new Slot(inventoryplayer, j + i * 9 + 9, b0 + j * 18, short0 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.func_75146_a(new Slot(inventoryplayer, i, b0 + i * 18, 58 + short0));
        }

        this.field_82865_g = tileentitybeacon.func_145998_l();
        this.field_82867_h = tileentitybeacon.func_146007_j();
        this.field_82868_i = tileentitybeacon.func_146006_k();
    }

    @Override
    public void func_75132_a(ICrafting icrafting) {
        super.func_75132_a(icrafting);
        icrafting.func_71112_a(this, 0, this.field_82865_g);
        icrafting.func_71112_a(this, 1, this.field_82867_h);
        icrafting.func_71112_a(this, 2, this.field_82868_i);
    }

    public TileEntityBeacon func_148327_e() {
        return this.field_82866_e;
    }

    @Override
    public boolean func_75145_c(EntityPlayer entityplayer) {
        return this.field_82866_e.func_70300_a(entityplayer);
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

                slot.func_75220_a(itemstack1, itemstack);
            } else if (!this.field_82864_f.func_75216_d() && this.field_82864_f.func_75214_a(itemstack1) && itemstack1.field_77994_a == 1) {
                if (!this.func_75135_a(itemstack1, 0, 1, false)) {
                    return null;
                }
            } else if (i >= 1 && i < 28) {
                if (!this.func_75135_a(itemstack1, 28, 37, false)) {
                    return null;
                }
            } else if (i >= 28 && i < 37) {
                if (!this.func_75135_a(itemstack1, 1, 28, false)) {
                    return null;
                }
            } else if (!this.func_75135_a(itemstack1, 1, 37, false)) {
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
}
