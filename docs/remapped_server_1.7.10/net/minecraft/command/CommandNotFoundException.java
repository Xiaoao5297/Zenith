package net.minecraft.command;

public class CommandNotFoundException extends CommandException {

    public CommandNotFoundException() {
        this("commands.generic.notFound");
    }

    public CommandNotFoundException(String s, Object... aobject) {
        super(s, aobject);
    }
}
