package net.minecraft.command.server;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent$Serializer;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class CommandMessageRaw extends CommandBase {

    public CommandMessageRaw() {}

    @Override
    public String func_71517_b() {
        return "tellraw";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.tellraw.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length < 2) {
            throw new WrongUsageException("commands.tellraw.usage", new Object[0]);
        } else {
            EntityPlayerMP entityplayermp = func_82359_c(icommandsender, astring[0]);
            String s = func_82360_a(icommandsender, astring, 1);

            try {
                IChatComponent ichatcomponent = IChatComponent$Serializer.func_150699_a(s);

                entityplayermp.func_145747_a(ichatcomponent);
            } catch (JsonParseException jsonparseexception) {
                Throwable throwable = ExceptionUtils.getRootCause(jsonparseexception);

                throw new SyntaxErrorException("commands.tellraw.jsonException", new Object[]{throwable == null ? "" : throwable.getMessage()});
            }
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 1 ? func_71530_a(astring, MinecraftServer.func_71276_C().func_71213_z()) : null;
    }

    @Override
    public boolean func_82358_a(String[] astring, int i) {
        return i == 0;
    }
}
