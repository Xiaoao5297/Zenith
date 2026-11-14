package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class ItemHangingEntity extends Item {

    private final Class field_82811_a;

    public ItemHangingEntity(Class oclass) {
        this.field_82811_a = oclass;
        this.func_77637_a(CreativeTabs.field_78031_c);
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (l == 0) {
            return false;
        } else if (l == 1) {
            return false;
        } else {
            int i1 = Direction.field_71579_d[l];
            EntityHanging entityhanging = this.func_82810_a(world, i, j, k, i1);

            if (!entityplayer.func_82247_a(i, j, k, l, itemstack)) {
                return false;
            } else {
                if (entityhanging != null && entityhanging.func_70518_d()) {
                    if (!world.field_72995_K) {
                        world.func_72838_d(entityhanging);
                    }

                    --itemstack.field_77994_a;
                }

                return true;
            }
        }
    }

    private EntityHanging func_82810_a(World world, int i, int j, int k, int l) {
        return (EntityHanging) (this.field_82811_a == EntityPainting.class ? new EntityPainting(world, i, j, k, l) : (this.field_82811_a == EntityItemFrame.class ? new EntityItemFrame(world, i, j, k, l) : null));
    }
}
