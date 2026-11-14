package net.minecraft.command.server;

import java.util.List;
import java.util.regex.Matcher;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandPardonIp extends CommandBase {

    public CommandPardonIp() {}

    @Override
    public String func_71517_b() {
        return "pardon-ip";
    }

    @Override
    public int func_82362_a() {
        return 3;
    }

    @Override
    public boolean func_71519_b(ICommandSender icommandsender) {
        return MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152689_b() && super.func_71519_b(icommandsender);
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.unbanip.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length == 1 && astring[0].length() > 1) {
            Matcher matcher = CommandBanIp.field_147211_a.matcher(astring[0]);

            if (matcher.matches()) {
                MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152684_c(astring[0]);
                func_152373_a(icommandsender, this, "commands.unbanip.success", new Object[]{astring[0]});
            } else {
                throw new SyntaxErrorException("commands.unbanip.invalid", new Object[0]);
            }
        } else {
            throw new WrongUsageException("commands.unbanip.usage", new Object[0]);
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 1 ? func_71530_a(astring, MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152685_a()) : null;
    }
}
