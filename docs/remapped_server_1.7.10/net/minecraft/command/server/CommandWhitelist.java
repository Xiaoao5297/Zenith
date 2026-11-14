package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandWhitelist extends CommandBase {

    public CommandWhitelist() {}

    @Override
    public String func_71517_b() {
        return "whitelist";
    }

    @Override
    public int func_82362_a() {
        return 3;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.whitelist.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length >= 1) {
            MinecraftServer minecraftserver = MinecraftServer.func_71276_C();

            if (astring[0].equals("on")) {
                minecraftserver.func_71203_ab().func_72371_a(true);
                func_152373_a(icommandsender, this, "commands.whitelist.enabled", new Object[0]);
                return;
            }

            if (astring[0].equals("off")) {
                minecraftserver.func_71203_ab().func_72371_a(false);
                func_152373_a(icommandsender, this, "commands.whitelist.disabled", new Object[0]);
                return;
            }

            if (astring[0].equals("list")) {
                icommandsender.func_145747_a(new ChatComponentTranslation("commands.whitelist.list", new Object[]{minecraftserver.func_71203_ab().func_152598_l().length, minecraftserver.func_71203_ab().func_72373_m().length}));
                String[] astring1 = minecraftserver.func_71203_ab().func_152598_l();

                icommandsender.func_145747_a(new ChatComponentText(func_71527_a(astring1)));
                return;
            }

            GameProfile gameprofile;

            if (astring[0].equals("add")) {
                if (astring.length < 2) {
                    throw new WrongUsageException("commands.whitelist.add.usage", new Object[0]);
                }

                gameprofile = minecraftserver.func_152358_ax().func_152655_a(astring[1]);
                if (gameprofile == null) {
                    throw new CommandException("commands.whitelist.add.failed", new Object[]{astring[1]});
                }

                minecraftserver.func_71203_ab().func_152601_d(gameprofile);
                func_152373_a(icommandsender, this, "commands.whitelist.add.success", new Object[]{astring[1]});
                return;
            }

            if (astring[0].equals("remove")) {
                if (astring.length < 2) {
                    throw new WrongUsageException("commands.whitelist.remove.usage", new Object[0]);
                }

                gameprofile = minecraftserver.func_71203_ab().func_152599_k().func_152706_a(astring[1]);
                if (gameprofile == null) {
                    throw new CommandException("commands.whitelist.remove.failed", new Object[]{astring[1]});
                }

                minecraftserver.func_71203_ab().func_152597_c(gameprofile);
                func_152373_a(icommandsender, this, "commands.whitelist.remove.success", new Object[]{astring[1]});
                return;
            }

            if (astring[0].equals("reload")) {
                minecraftserver.func_71203_ab().func_72362_j();
                func_152373_a(icommandsender, this, "commands.whitelist.reloaded", new Object[0]);
                return;
            }
        }

        throw new WrongUsageException("commands.whitelist.usage", new Object[0]);
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        if (astring.length == 1) {
            return func_71530_a(astring, new String[]{"on", "off", "list", "add", "remove", "reload"});
        } else {
            if (astring.length == 2) {
                if (astring[0].equals("remove")) {
                    return func_71530_a(astring, MinecraftServer.func_71276_C().func_71203_ab().func_152598_l());
                }

                if (astring[0].equals("add")) {
                    return func_71530_a(astring, MinecraftServer.func_71276_C().func_152358_ax().func_152654_a());
                }
            }

            return null;
        }
    }
}
