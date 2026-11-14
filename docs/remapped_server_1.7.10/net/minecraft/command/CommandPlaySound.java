package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;

public class CommandPlaySound extends CommandBase {

    public CommandPlaySound() {}

    @Override
    public String func_71517_b() {
        return "playsound";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.playsound.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length < 2) {
            throw new WrongUsageException(this.func_71518_a(icommandsender), new Object[0]);
        } else {
            int i = 0;
            String s = astring[i++];
            EntityPlayerMP entityplayermp = func_82359_c(icommandsender, astring[i++]);
            double d0 = (double) entityplayermp.func_82114_b().field_71574_a;
            double d1 = (double) entityplayermp.func_82114_b().field_71572_b;
            double d2 = (double) entityplayermp.func_82114_b().field_71573_c;
            double d3 = 1.0D;
            double d4 = 1.0D;
            double d5 = 0.0D;

            if (astring.length > i) {
                d0 = func_110666_a(icommandsender, d0, astring[i++]);
            }

            if (astring.length > i) {
                d1 = func_110665_a(icommandsender, d1, astring[i++], 0, 0);
            }

            if (astring.length > i) {
                d2 = func_110666_a(icommandsender, d2, astring[i++]);
            }

            if (astring.length > i) {
                d3 = func_110661_a(icommandsender, astring[i++], 0.0D, 3.4028234663852886E38D);
            }

            if (astring.length > i) {
                d4 = func_110661_a(icommandsender, astring[i++], 0.0D, 2.0D);
            }

            if (astring.length > i) {
                d5 = func_110661_a(icommandsender, astring[i++], 0.0D, 1.0D);
            }

            double d6 = d3 > 1.0D ? d3 * 16.0D : 16.0D;
            double d7 = entityplayermp.func_70011_f(d0, d1, d2);

            if (d7 > d6) {
                if (d5 <= 0.0D) {
                    throw new CommandException("commands.playsound.playerTooFar", new Object[]{entityplayermp.func_70005_c_()});
                }

                double d8 = d0 - entityplayermp.field_70165_t;
                double d9 = d1 - entityplayermp.field_70163_u;
                double d10 = d2 - entityplayermp.field_70161_v;
                double d11 = Math.sqrt(d8 * d8 + d9 * d9 + d10 * d10);
                double d12 = entityplayermp.field_70165_t;
                double d13 = entityplayermp.field_70163_u;
                double d14 = entityplayermp.field_70161_v;

                if (d11 > 0.0D) {
                    d12 += d8 / d11 * 2.0D;
                    d13 += d9 / d11 * 2.0D;
                    d14 += d10 / d11 * 2.0D;
                }

                entityplayermp.field_71135_a.func_147359_a(new S29PacketSoundEffect(s, d12, d13, d14, (float) d5, (float) d4));
            } else {
                entityplayermp.field_71135_a.func_147359_a(new S29PacketSoundEffect(s, d0, d1, d2, (float) d3, (float) d4));
            }

            func_152373_a(icommandsender, this, "commands.playsound.success", new Object[]{s, entityplayermp.func_70005_c_()});
        }
    }

    @Override
    public boolean func_82358_a(String[] astring, int i) {
        return i == 1;
    }
}
