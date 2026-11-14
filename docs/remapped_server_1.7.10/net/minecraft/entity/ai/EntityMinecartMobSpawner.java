package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.world.World;

public class EntityMinecartMobSpawner extends EntityMinecart {

    private final MobSpawnerBaseLogic field_98040_a = new EntityMinecartMobSpawner$1(this);

    public EntityMinecartMobSpawner(World world) {
        super(world);
    }

    public EntityMinecartMobSpawner(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    public int func_94087_l() {
        return 4;
    }

    @Override
    public Block func_145817_o() {
        return Blocks.field_150474_ac;
    }

    @Override
    protected void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        this.field_98040_a.func_98270_a(nbttagcompound);
    }

    @Override
    protected void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        this.field_98040_a.func_98280_b(nbttagcompound);
    }

    @Override
    public void func_70071_h_() {
        super.func_70071_h_();
        this.field_98040_a.func_98278_g();
    }
}
