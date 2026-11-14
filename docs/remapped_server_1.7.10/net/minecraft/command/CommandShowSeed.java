package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class CommandShowSeed extends CommandBase {

    public CommandShowSeed() {}

    @Override
    public boolean func_71519_b(ICommandSender icommandsender) {
        return MinecraftServer.func_71276_C().func_71264_H() || super.func_71519_b(icommandsender);
    }

    @Override
    public String func_71517_b() {
        return "seed";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.seed.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        Object object = icommandsender instanceof EntityPlayer ? ((EntityPlayer) icommandsender).field_70170_p : MinecraftServer.func_71276_C().func_71218_a(0);

        icommandsender.func_145747_a(new ChatComponentTranslation("commands.seed.success", new Object[]{((World) object).func_72905_C()}));
    }
}
