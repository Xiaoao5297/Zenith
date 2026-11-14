package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class CommandGive extends CommandBase {

    public CommandGive() {}

    @Override
    public String func_71517_b() {
        return "give";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.give.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length < 2) {
            throw new WrongUsageException("commands.give.usage", new Object[0]);
        } else {
            EntityPlayerMP entityplayermp = func_82359_c(icommandsender, astring[0]);
            Item item = func_147179_f(icommandsender, astring[1]);
            int i = 1;
            int j = 0;

            if (astring.length >= 3) {
                i = func_71532_a(icommandsender, astring[2], 1, 64);
            }

            if (astring.length >= 4) {
                j = func_71526_a(icommandsender, astring[3]);
            }

            ItemStack itemstack = new ItemStack(item, i, j);

            if (astring.length >= 5) {
                String s = func_147178_a(icommandsender, astring, 4).func_150260_c();

                try {
                    NBTBase nbtbase = JsonToNBT.func_150315_a(s);

                    if (!(nbtbase instanceof NBTTagCompound)) {
                        func_152373_a(icommandsender, this, "commands.give.tagError", new Object[]{"Not a valid tag"});
                        return;
                    }

                    itemstack.func_77982_d((NBTTagCompound) nbtbase);
                } catch (NBTException nbtexception) {
                    func_152373_a(icommandsender, this, "commands.give.tagError", new Object[]{nbtexception.getMessage()});
                    return;
                }
            }

            EntityItem entityitem = entityplayermp.func_71019_a(itemstack, false);

            entityitem.field_145804_b = 0;
            entityitem.func_145797_a(entityplayermp.func_70005_c_());
            func_152373_a(icommandsender, this, "commands.give.success", new Object[]{itemstack.func_151000_E(), i, entityplayermp.func_70005_c_()});
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 1 ? func_71530_a(astring, this.func_71536_c()) : (astring.length == 2 ? func_71531_a(astring, Item.field_150901_e.func_148742_b()) : null);
    }

    protected String[] func_71536_c() {
        return MinecraftServer.func_71276_C().func_71213_z();
    }

    @Override
    public boolean func_82358_a(String[] astring, int i) {
        return i == 0;
    }
}
