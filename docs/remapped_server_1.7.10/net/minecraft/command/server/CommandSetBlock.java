package net.minecraft.command.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandSetBlock extends CommandBase {

    public CommandSetBlock() {}

    @Override
    public String func_71517_b() {
        return "setblock";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.setblock.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length >= 4) {
            int i = icommandsender.func_82114_b().field_71574_a;
            int j = icommandsender.func_82114_b().field_71572_b;
            int k = icommandsender.func_82114_b().field_71573_c;

            i = MathHelper.func_76128_c(func_110666_a(icommandsender, (double) i, astring[0]));
            j = MathHelper.func_76128_c(func_110666_a(icommandsender, (double) j, astring[1]));
            k = MathHelper.func_76128_c(func_110666_a(icommandsender, (double) k, astring[2]));
            Block block = CommandBase.func_147180_g(icommandsender, astring[3]);
            int l = 0;

            if (astring.length >= 5) {
                l = func_71532_a(icommandsender, astring[4], 0, 15);
            }

            World world = icommandsender.func_130014_f_();

            if (!world.func_72899_e(i, j, k)) {
                throw new CommandException("commands.setblock.outOfWorld", new Object[0]);
            } else {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                boolean flag = false;

                if (astring.length >= 7 && block.func_149716_u()) {
                    String s = func_147178_a(icommandsender, astring, 6).func_150260_c();

                    try {
                        NBTBase nbtbase = JsonToNBT.func_150315_a(s);

                        if (!(nbtbase instanceof NBTTagCompound)) {
                            throw new CommandException("commands.setblock.tagError", new Object[]{"Not a valid tag"});
                        }

                        nbttagcompound = (NBTTagCompound) nbtbase;
                        flag = true;
                    } catch (NBTException nbtexception) {
                        throw new CommandException("commands.setblock.tagError", new Object[]{nbtexception.getMessage()});
                    }
                }

                if (astring.length >= 6) {
                    if (astring[5].equals("destroy")) {
                        world.func_147480_a(i, j, k, true);
                    } else if (astring[5].equals("keep") && !world.func_147437_c(i, j, k)) {
                        throw new CommandException("commands.setblock.noChange", new Object[0]);
                    }
                }

                if (!world.func_147465_d(i, j, k, block, l, 3)) {
                    throw new CommandException("commands.setblock.noChange", new Object[0]);
                } else {
                    if (flag) {
                        TileEntity tileentity = world.func_147438_o(i, j, k);

                        if (tileentity != null) {
                            nbttagcompound.func_74768_a("x", i);
                            nbttagcompound.func_74768_a("y", j);
                            nbttagcompound.func_74768_a("z", k);
                            tileentity.func_145839_a(nbttagcompound);
                        }
                    }

                    func_152373_a(icommandsender, this, "commands.setblock.success", new Object[0]);
                }
            }
        } else {
            throw new WrongUsageException("commands.setblock.usage", new Object[0]);
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 4 ? func_71531_a(astring, Block.field_149771_c.func_148742_b()) : (astring.length == 6 ? func_71530_a(astring, new String[]{"replace", "destroy", "keep"}) : null);
    }
}
