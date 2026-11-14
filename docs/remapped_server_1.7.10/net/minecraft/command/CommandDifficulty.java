package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumDifficulty;

public class CommandDifficulty extends CommandBase {

    public CommandDifficulty() {}

    @Override
    public String func_71517_b() {
        return "difficulty";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.difficulty.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length > 0) {
            EnumDifficulty enumdifficulty = this.func_147201_h(icommandsender, astring[0]);

            MinecraftServer.func_71276_C().func_147139_a(enumdifficulty);
            func_152373_a(icommandsender, this, "commands.difficulty.success", new Object[]{new ChatComponentTranslation(enumdifficulty.func_151526_b(), new Object[0])});
        } else {
            throw new WrongUsageException("commands.difficulty.usage", new Object[0]);
        }
    }

    protected EnumDifficulty func_147201_h(ICommandSender icommandsender, String s) {
        return !s.equalsIgnoreCase("peaceful") && !s.equalsIgnoreCase("p") ? (!s.equalsIgnoreCase("easy") && !s.equalsIgnoreCase("e") ? (!s.equalsIgnoreCase("normal") && !s.equalsIgnoreCase("n") ? (!s.equalsIgnoreCase("hard") && !s.equalsIgnoreCase("h") ? EnumDifficulty.func_151523_a(func_71532_a(icommandsender, s, 0, 3)) : EnumDifficulty.HARD) : EnumDifficulty.NORMAL) : EnumDifficulty.EASY) : EnumDifficulty.PEACEFUL;
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 1 ? func_71530_a(astring, new String[]{"peaceful", "easy", "normal", "hard"}) : null;
    }
}
