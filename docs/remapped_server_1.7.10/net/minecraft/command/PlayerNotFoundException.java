package net.minecraft.command;

public class PlayerNotFoundException extends CommandException {

    public PlayerNotFoundException() {
        this("commands.generic.player.notFound");
    }

    public PlayerNotFoundException(String s, Object... aobject) {
        super(s, aobject);
    }
}
