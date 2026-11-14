package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandTestFor extends CommandBase {

    public CommandTestFor() {}

    @Override
    public String func_71517_b() {
        return "testfor";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.testfor.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length != 1) {
            throw new WrongUsageException("commands.testfor.usage", new Object[0]);
        } else if (!(icommandsender instanceof CommandBlockLogic)) {
            throw new CommandException("commands.testfor.failed", new Object[0]);
        } else {
            func_82359_c(icommandsender, astring[0]);
        }
    }

    @Override
    public boolean func_82358_a(String[] astring, int i) {
        return i == 0;
    }
}
