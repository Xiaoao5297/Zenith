package net.minecraft.tileentity;

import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

class TileEntityCommandBlock$1 extends CommandBlockLogic {

    TileEntityCommandBlock$1(TileEntityCommandBlock tileentitycommandblock) {
        this.field_145767_a = tileentitycommandblock;
    }

    @Override
    public ChunkCoordinates func_82114_b() {
        return new ChunkCoordinates(this.field_145767_a.field_145851_c, this.field_145767_a.field_145848_d, this.field_145767_a.field_145849_e);
    }

    @Override
    public World func_130014_f_() {
        return this.field_145767_a.func_145831_w();
    }

    @Override
    public void func_145752_a(String s) {
        super.func_145752_a(s);
        this.field_145767_a.func_70296_d();
    }

    @Override
    public void func_145756_e() {
        this.field_145767_a.func_145831_w().func_147471_g(this.field_145767_a.field_145851_c, this.field_145767_a.field_145848_d, this.field_145767_a.field_145849_e);
    }
}
