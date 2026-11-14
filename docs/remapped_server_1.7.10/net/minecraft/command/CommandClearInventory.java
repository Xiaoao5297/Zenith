package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;

public class CommandClearInventory extends CommandBase {

    public CommandClearInventory() {}

    @Override
    public String func_71517_b() {
        return "clear";
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.clear.usage";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        EntityPlayerMP entityplayermp = astring.length == 0 ? func_71521_c(icommandsender) : func_82359_c(icommandsender, astring[0]);
        Item item = astring.length >= 2 ? func_147179_f(icommandsender, astring[1]) : null;
        int i = astring.length >= 3 ? func_71528_a(icommandsender, astring[2], 0) : -1;

        if (astring.length >= 2 && item == null) {
            throw new CommandException("commands.clear.failure", new Object[]{entityplayermp.func_70005_c_()});
        } else {
            int j = entityplayermp.field_71071_by.func_146027_a(item, i);

            entityplayermp.field_71069_bz.func_75142_b();
            if (!entityplayermp.field_71075_bZ.field_75098_d) {
                entityplayermp.func_71113_k();
            }

            if (j == 0) {
                throw new CommandException("commands.clear.failure", new Object[]{entityplayermp.func_70005_c_()});
            } else {
                func_152373_a(icommandsender, this, "commands.clear.success", new Object[]{entityplayermp.func_70005_c_(), j});
            }
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 1 ? func_71530_a(astring, this.func_147209_d()) : (astring.length == 2 ? func_71531_a(astring, Item.field_150901_e.func_148742_b()) : null);
    }

    protected String[] func_147209_d() {
        return MinecraftServer.func_71276_C().func_71213_z();
    }

    @Override
    public boolean func_82358_a(String[] astring, int i) {
        return i == 0;
    }
}
