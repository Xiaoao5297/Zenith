package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.GameRules;

public class CommandGameRule extends CommandBase {

    public CommandGameRule() {}

    @Override
    public String func_71517_b() {
        return "gamerule";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.gamerule.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        String s;

        if (astring.length == 2) {
            s = astring[0];
            String s1 = astring[1];
            GameRules gamerules = this.func_82366_d();

            if (gamerules.func_82765_e(s)) {
                gamerules.func_82764_b(s, s1);
                func_152373_a(icommandsender, this, "commands.gamerule.success", new Object[0]);
            } else {
                func_152373_a(icommandsender, this, "commands.gamerule.norule", new Object[]{s});
            }

        } else if (astring.length == 1) {
            s = astring[0];
            GameRules gamerules1 = this.func_82366_d();

            if (gamerules1.func_82765_e(s)) {
                String s2 = gamerules1.func_82767_a(s);

                icommandsender.func_145747_a((new ChatComponentText(s)).func_150258_a(" = ").func_150258_a(s2));
            } else {
                func_152373_a(icommandsender, this, "commands.gamerule.norule", new Object[]{s});
            }

        } else if (astring.length == 0) {
            GameRules gamerules2 = this.func_82366_d();

            icommandsender.func_145747_a(new ChatComponentText(func_71527_a(gamerules2.func_82763_b())));
        } else {
            throw new WrongUsageException("commands.gamerule.usage", new Object[0]);
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 1 ? func_71530_a(astring, this.func_82366_d().func_82763_b()) : (astring.length == 2 ? func_71530_a(astring, new String[]{"true", "false"}) : null);
    }

    private GameRules func_82366_d() {
        return MinecraftServer.func_71276_C().func_71218_a(0).func_82736_K();
    }
}
