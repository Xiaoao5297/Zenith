package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;

public class CommandSetSpawnpoint extends CommandBase {

    public CommandSetSpawnpoint() {}

    @Override
    public String func_71517_b() {
        return "spawnpoint";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.spawnpoint.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        EntityPlayerMP entityplayermp = astring.length == 0 ? func_71521_c(icommandsender) : func_82359_c(icommandsender, astring[0]);

        if (astring.length == 4) {
            if (entityplayermp.field_70170_p != null) {
                int i = 1;
                int j = 30000000;
                int k = func_71532_a(icommandsender, astring[i++], -j, j);
                int l = func_71532_a(icommandsender, astring[i++], 0, 256);
                int i1 = func_71532_a(icommandsender, astring[i++], -j, j);

                entityplayermp.func_71063_a(new ChunkCoordinates(k, l, i1), true);
                func_152373_a(icommandsender, this, "commands.spawnpoint.success", new Object[]{entityplayermp.func_70005_c_(), k, l, i1});
            }
        } else {
            if (astring.length > 1) {
                throw new WrongUsageException("commands.spawnpoint.usage", new Object[0]);
            }

            ChunkCoordinates chunkcoordinates = entityplayermp.func_82114_b();

            entityplayermp.func_71063_a(chunkcoordinates, true);
            func_152373_a(icommandsender, this, "commands.spawnpoint.success", new Object[]{entityplayermp.func_70005_c_(), chunkcoordinates.field_71574_a, chunkcoordinates.field_71572_b, chunkcoordinates.field_71573_c});
        }

    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length != 1 && astring.length != 2 ? null : func_71530_a(astring, MinecraftServer.func_71276_C().func_71213_z());
    }

    @Override
    public boolean func_82358_a(String[] astring, int i) {
        return i == 0;
    }
}
