package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityMinecartEmpty extends EntityMinecart {

    public EntityMinecartEmpty(World world) {
        super(world);
    }

    public EntityMinecartEmpty(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    public boolean func_130002_c(EntityPlayer entityplayer) {
        if (this.field_70153_n != null && this.field_70153_n instanceof EntityPlayer && this.field_70153_n != entityplayer) {
            return true;
        } else if (this.field_70153_n != null && this.field_70153_n != entityplayer) {
            return false;
        } else {
            if (!this.field_70170_p.field_72995_K) {
                entityplayer.func_70078_a(this);
            }

            return true;
        }
    }

    @Override
    public int func_94087_l() {
        return 0;
    }
}
