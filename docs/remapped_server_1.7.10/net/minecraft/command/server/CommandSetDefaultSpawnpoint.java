package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChunkCoordinates;

public class CommandSetDefaultSpawnpoint extends CommandBase {

    public CommandSetDefaultSpawnpoint() {}

    @Override
    public String func_71517_b() {
        return "setworldspawn";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.setworldspawn.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length == 3) {
            if (icommandsender.func_130014_f_() == null) {
                throw new WrongUsageException("commands.setworldspawn.usage", new Object[0]);
            }

            int i = 0;
            int j = func_71532_a(icommandsender, astring[i++], -30000000, 30000000);
            int k = func_71532_a(icommandsender, astring[i++], 0, 256);
            int l = func_71532_a(icommandsender, astring[i++], -30000000, 30000000);

            icommandsender.func_130014_f_().func_72950_A(j, k, l);
            func_152373_a(icommandsender, this, "commands.setworldspawn.success", new Object[]{j, k, l});
        } else {
            if (astring.length != 0) {
                throw new WrongUsageException("commands.setworldspawn.usage", new Object[0]);
            }

            ChunkCoordinates chunkcoordinates = func_71521_c(icommandsender).func_82114_b();

            icommandsender.func_130014_f_().func_72950_A(chunkcoordinates.field_71574_a, chunkcoordinates.field_71572_b, chunkcoordinates.field_71573_c);
            func_152373_a(icommandsender, this, "commands.setworldspawn.success", new Object[]{chunkcoordinates.field_71574_a, chunkcoordinates.field_71572_b, chunkcoordinates.field_71573_c});
        }

    }
}
