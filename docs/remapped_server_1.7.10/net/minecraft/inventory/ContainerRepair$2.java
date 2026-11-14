package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

class ContainerRepair$2 extends Slot {

    ContainerRepair$2(ContainerRepair containerrepair, IInventory iinventory, int i, int j, int k, World world, int l, int i1, int j1) {
        super(iinventory, i, j, k);
        this.field_135068_e = containerrepair;
        this.field_135071_a = world;
        this.field_135069_b = l;
        this.field_135070_c = i1;
        this.field_135067_d = j1;
    }

    @Override
    public boolean func_75214_a(ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean func_82869_a(EntityPlayer entityplayer) {
        return (entityplayer.field_71075_bZ.field_75098_d || entityplayer.field_71068_ca >= this.field_135068_e.field_82854_e) && this.field_135068_e.field_82854_e > 0 && this.func_75216_d();
    }

    @Override
    public void func_82870_a(EntityPlayer entityplayer, ItemStack itemstack) {
        if (!entityplayer.field_71075_bZ.field_75098_d) {
            entityplayer.func_82242_a(-this.field_135068_e.field_82854_e);
        }

        ContainerRepair.access$000(this.field_135068_e).func_70299_a(0, (ItemStack) null);
        if (ContainerRepair.access$100(this.field_135068_e) > 0) {
            ItemStack itemstack1 = ContainerRepair.access$000(this.field_135068_e).func_70301_a(1);

            if (itemstack1 != null && itemstack1.field_77994_a > ContainerRepair.access$100(this.field_135068_e)) {
                itemstack1.field_77994_a -= ContainerRepair.access$100(this.field_135068_e);
                ContainerRepair.access$000(this.field_135068_e).func_70299_a(1, itemstack1);
            } else {
                ContainerRepair.access$000(this.field_135068_e).func_70299_a(1, (ItemStack) null);
            }
        } else {
            ContainerRepair.access$000(this.field_135068_e).func_70299_a(1, (ItemStack) null);
        }

        this.field_135068_e.field_82854_e = 0;
        if (!entityplayer.field_71075_bZ.field_75098_d && !this.field_135071_a.field_72995_K && this.field_135071_a.func_147439_a(this.field_135069_b, this.field_135070_c, this.field_135067_d) == Blocks.field_150467_bQ && entityplayer.func_70681_au().nextFloat() < 0.12F) {
            int i = this.field_135071_a.func_72805_g(this.field_135069_b, this.field_135070_c, this.field_135067_d);
            int j = i & 3;
            int k = i >> 2;

            ++k;
            if (k > 2) {
                this.field_135071_a.func_147468_f(this.field_135069_b, this.field_135070_c, this.field_135067_d);
                this.field_135071_a.func_72926_e(1020, this.field_135069_b, this.field_135070_c, this.field_135067_d, 0);
            } else {
                this.field_135071_a.func_72921_c(this.field_135069_b, this.field_135070_c, this.field_135067_d, j | k << 2, 2);
                this.field_135071_a.func_72926_e(1021, this.field_135069_b, this.field_135070_c, this.field_135067_d, 0);
            }
        } else if (!this.field_135071_a.field_72995_K) {
            this.field_135071_a.func_72926_e(1021, this.field_135069_b, this.field_135070_c, this.field_135067_d, 0);
        }

    }
}
