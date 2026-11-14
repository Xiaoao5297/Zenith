package net.minecraft.command;

public class SyntaxErrorException extends CommandException {

    public SyntaxErrorException() {
        this("commands.generic.snytax");
    }

    public SyntaxErrorException(String s, Object... aobject) {
        super(s, aobject);
    }
}
