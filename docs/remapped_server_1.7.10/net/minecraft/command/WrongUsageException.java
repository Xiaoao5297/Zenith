package net.minecraft.command;

public class WrongUsageException extends SyntaxErrorException {

    public WrongUsageException(String s, Object... aobject) {
        super(s, aobject);
    }
}
