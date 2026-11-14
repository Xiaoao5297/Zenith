package net.minecraft.command.server;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandTestForBlock extends CommandBase {

    public CommandTestForBlock() {}

    @Override
    public String func_71517_b() {
        return "testforblock";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.testforblock.usage";
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
            Block block = Block.func_149684_b(astring[3]);

            if (block == null) {
                throw new NumberInvalidException("commands.setblock.notFound", new Object[]{astring[3]});
            } else {
                int l = -1;

                if (astring.length >= 5) {
                    l = func_71532_a(icommandsender, astring[4], -1, 15);
                }

                World world = icommandsender.func_130014_f_();

                if (!world.func_72899_e(i, j, k)) {
                    throw new CommandException("commands.testforblock.outOfWorld", new Object[0]);
                } else {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    boolean flag = false;

                    if (astring.length >= 6 && block.func_149716_u()) {
                        String s = func_147178_a(icommandsender, astring, 5).func_150260_c();

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

                    Block block1 = world.func_147439_a(i, j, k);

                    if (block1 != block) {
                        throw new CommandException("commands.testforblock.failed.tile", new Object[]{i, j, k, block1.func_149732_F(), block.func_149732_F()});
                    } else {
                        if (l > -1) {
                            int i1 = world.func_72805_g(i, j, k);

                            if (i1 != l) {
                                throw new CommandException("commands.testforblock.failed.data", new Object[]{i, j, k, i1, l});
                            }
                        }

                        if (flag) {
                            TileEntity tileentity = world.func_147438_o(i, j, k);

                            if (tileentity == null) {
                                throw new CommandException("commands.testforblock.failed.tileEntity", new Object[]{i, j, k});
                            }

                            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                            tileentity.func_145841_b(nbttagcompound1);
                            if (!this.func_147181_a(nbttagcompound, nbttagcompound1)) {
                                throw new CommandException("commands.testforblock.failed.nbt", new Object[]{i, j, k});
                            }
                        }

                        icommandsender.func_145747_a(new ChatComponentTranslation("commands.testforblock.success", new Object[]{i, j, k}));
                    }
                }
            }
        } else {
            throw new WrongUsageException("commands.testforblock.usage", new Object[0]);
        }
    }

    public boolean func_147181_a(NBTBase nbtbase, NBTBase nbtbase1) {
        if (nbtbase == nbtbase1) {
            return true;
        } else if (nbtbase == null) {
            return true;
        } else if (nbtbase1 == null) {
            return false;
        } else if (!nbtbase.getClass().equals(nbtbase1.getClass())) {
            return false;
        } else if (nbtbase instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
            NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbtbase1;
            Iterator iterator = nbttagcompound.func_150296_c().iterator();

            String s;
            NBTBase nbtbase2;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                s = (String) iterator.next();
                nbtbase2 = nbttagcompound.func_74781_a(s);
            } while (this.func_147181_a(nbtbase2, nbttagcompound1.func_74781_a(s)));

            return false;
        } else {
            return nbtbase.equals(nbtbase1);
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 4 ? func_71531_a(astring, Block.field_149771_c.func_148742_b()) : null;
    }
}
