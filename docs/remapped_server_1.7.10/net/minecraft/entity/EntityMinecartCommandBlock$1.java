package net.minecraft.entity;

import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent$Serializer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

class EntityMinecartCommandBlock$1 extends CommandBlockLogic {

    EntityMinecartCommandBlock$1(EntityMinecartCommandBlock entityminecartcommandblock) {
        this.field_145768_a = entityminecartcommandblock;
    }

    @Override
    public void func_145756_e() {
        this.field_145768_a.func_70096_w().func_75692_b(23, this.func_145753_i());
        this.field_145768_a.func_70096_w().func_75692_b(24, IChatComponent$Serializer.func_150696_a(this.func_145749_h()));
    }

    @Override
    public ChunkCoordinates func_82114_b() {
        return new ChunkCoordinates(MathHelper.func_76128_c(this.field_145768_a.field_70165_t), MathHelper.func_76128_c(this.field_145768_a.field_70163_u + 0.5D), MathHelper.func_76128_c(this.field_145768_a.field_70161_v));
    }

    @Override
    public World func_130014_f_() {
        return this.field_145768_a.field_70170_p;
    }
}
