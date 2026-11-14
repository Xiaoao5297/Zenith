package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class ItemCarrotOnAStick extends Item {

    public ItemCarrotOnAStick() {
        this.func_77637_a(CreativeTabs.field_78029_e);
        this.func_77625_d(1);
        this.func_77656_e(25);
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (entityplayer.func_70115_ae() && entityplayer.field_70154_o instanceof EntityPig) {
            EntityPig entitypig = (EntityPig) entityplayer.field_70154_o;

            if (entitypig.func_82183_n().func_82633_h() && itemstack.func_77958_k() - itemstack.func_77960_j() >= 7) {
                entitypig.func_82183_n().func_82632_g();
                itemstack.func_77972_a(7, entityplayer);
                if (itemstack.field_77994_a == 0) {
                    ItemStack itemstack1 = new ItemStack(Items.field_151112_aM);

                    itemstack1.func_77982_d(itemstack.field_77990_d);
                    return itemstack1;
                }
            }
        }

        return itemstack;
    }
}
