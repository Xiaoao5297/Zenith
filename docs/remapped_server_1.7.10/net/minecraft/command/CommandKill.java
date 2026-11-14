package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;

public class CommandKill extends CommandBase {

    public CommandKill() {}

    @Override
    public String func_71517_b() {
        return "kill";
    }

    @Override
    public int func_82362_a() {
        return 0;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.kill.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        EntityPlayerMP entityplayermp = func_71521_c(icommandsender);

        entityplayermp.func_70097_a(DamageSource.field_76380_i, Float.MAX_VALUE);
        icommandsender.func_145747_a(new ChatComponentTranslation("commands.kill.success", new Object[0]));
    }
}
