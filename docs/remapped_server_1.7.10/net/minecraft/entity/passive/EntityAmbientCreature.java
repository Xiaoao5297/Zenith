package net.minecraft.entity.passive;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class EntityAmbientCreature extends EntityLiving implements IAnimals {

    public EntityAmbientCreature(World world) {
        super(world);
    }

    @Override
    public boolean func_110164_bC() {
        return false;
    }

    @Override
    protected boolean func_70085_c(EntityPlayer entityplayer) {
        return false;
    }
}
