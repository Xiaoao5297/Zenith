package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkStatistics$PacketStat;
import net.minecraft.util.ChatComponentText;

public class CommandNetstat extends CommandBase {

    public CommandNetstat() {}

    @Override
    public String func_71517_b() {
        return "netstat";
    }

    @Override
    public int func_82362_a() {
        return 0;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.players.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (icommandsender instanceof EntityPlayer) {
            icommandsender.func_145747_a(new ChatComponentText("Command is not available for players"));
        } else {
            if (astring.length > 0 && astring[0].length() > 1) {
                if ("hottest-read".equals(astring[0])) {
                    icommandsender.func_145747_a(new ChatComponentText(NetworkManager.field_152462_h.func_152477_e().toString()));
                } else if ("hottest-write".equals(astring[0])) {
                    icommandsender.func_145747_a(new ChatComponentText(NetworkManager.field_152462_h.func_152475_g().toString()));
                } else if ("most-read".equals(astring[0])) {
                    icommandsender.func_145747_a(new ChatComponentText(NetworkManager.field_152462_h.func_152467_f().toString()));
                } else if ("most-write".equals(astring[0])) {
                    icommandsender.func_145747_a(new ChatComponentText(NetworkManager.field_152462_h.func_152470_h().toString()));
                } else {
                    int i;
                    NetworkStatistics$PacketStat networkstatistics$packetstat;

                    if ("packet-read".equals(astring[0])) {
                        if (astring.length > 1 && astring[1].length() > 0) {
                            try {
                                i = Integer.parseInt(astring[1].trim());
                                networkstatistics$packetstat = NetworkManager.field_152462_h.func_152466_a(i);
                                this.func_152375_a(icommandsender, i, networkstatistics$packetstat);
                            } catch (Exception exception) {
                                icommandsender.func_145747_a(new ChatComponentText("Packet " + astring[1] + " not found!"));
                            }
                        } else {
                            icommandsender.func_145747_a(new ChatComponentText("Packet id is missing"));
                        }
                    } else if ("packet-write".equals(astring[0])) {
                        if (astring.length > 1 && astring[1].length() > 0) {
                            try {
                                i = Integer.parseInt(astring[1].trim());
                                networkstatistics$packetstat = NetworkManager.field_152462_h.func_152468_b(i);
                                this.func_152375_a(icommandsender, i, networkstatistics$packetstat);
                            } catch (Exception exception1) {
                                icommandsender.func_145747_a(new ChatComponentText("Packet " + astring[1] + " not found!"));
                            }
                        } else {
                            icommandsender.func_145747_a(new ChatComponentText("Packet id is missing"));
                        }
                    } else if ("read-count".equals(astring[0])) {
                        icommandsender.func_145747_a(new ChatComponentText("total-read-count" + String.valueOf(NetworkManager.field_152462_h.func_152472_c())));
                    } else if ("write-count".equals(astring[0])) {
                        icommandsender.func_145747_a(new ChatComponentText("total-write-count" + String.valueOf(NetworkManager.field_152462_h.func_152473_d())));
                    } else {
                        icommandsender.func_145747_a(new ChatComponentText("Unrecognized: " + astring[0]));
                    }
                }
            } else {
                String s = "reads: " + NetworkManager.field_152462_h.func_152465_a();

                s = s + ", writes: " + NetworkManager.field_152462_h.func_152471_b();
                icommandsender.func_145747_a(new ChatComponentText(s));
            }

        }
    }

    private void func_152375_a(ICommandSender icommandsender, int i, NetworkStatistics$PacketStat networkstatistics$packetstat) {
        if (networkstatistics$packetstat != null) {
            icommandsender.func_145747_a(new ChatComponentText(networkstatistics$packetstat.toString()));
        } else {
            icommandsender.func_145747_a(new ChatComponentText("Packet " + i + " not found!"));
        }

    }
}
