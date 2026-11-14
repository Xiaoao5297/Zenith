package net.minecraft.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem {

    private static final Logger field_151275_b = LogManager.getLogger();
    private static final NioEventLoopGroup field_151276_c = new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty IO #%d").setDaemon(true).build());
    private final MinecraftServer field_151273_d;
    public volatile boolean field_151277_a;
    private final List field_151274_e = Collections.synchronizedList(new ArrayList());
    private final List field_151272_f = Collections.synchronizedList(new ArrayList());

    public NetworkSystem(MinecraftServer minecraftserver) {
        this.field_151273_d = minecraftserver;
        this.field_151277_a = true;
    }

    public void func_151265_a(InetAddress inetaddress, int i) {
        List list = this.field_151274_e;

        synchronized (this.field_151274_e) {
            this.field_151274_e.add(((ServerBootstrap) ((ServerBootstrap) (new ServerBootstrap()).channel(NioServerSocketChannel.class)).childHandler(new NetworkSystem$1(this)).group(NetworkSystem.field_151276_c).localAddress(inetaddress, i)).bind().syncUninterruptibly());
        }
    }

    public void func_151268_b() {
        this.field_151277_a = false;
        Iterator iterator = this.field_151274_e.iterator();

        while (iterator.hasNext()) {
            ChannelFuture channelfuture = (ChannelFuture) iterator.next();

            channelfuture.channel().close().syncUninterruptibly();
        }

    }

    public void func_151269_c() {
        List list = this.field_151272_f;

        synchronized (this.field_151272_f) {
            Iterator iterator = this.field_151272_f.iterator();

            while (iterator.hasNext()) {
                NetworkManager networkmanager = (NetworkManager) iterator.next();

                if (!networkmanager.func_150724_d()) {
                    iterator.remove();
                    if (networkmanager.func_150730_f() != null) {
                        networkmanager.func_150729_e().func_147231_a(networkmanager.func_150730_f());
                    } else if (networkmanager.func_150729_e() != null) {
                        networkmanager.func_150729_e().func_147231_a(new ChatComponentText("Disconnected"));
                    }
                } else {
                    try {
                        networkmanager.func_74428_b();
                    } catch (Exception exception) {
                        if (networkmanager.func_150731_c()) {
                            CrashReport crashreport = CrashReport.func_85055_a(exception, "Ticking memory connection");
                            CrashReportCategory crashreportcategory = crashreport.func_85058_a("Ticking connection");

                            crashreportcategory.func_71500_a("Connection", new NetworkSystem$3(this, networkmanager));
                            throw new ReportedException(crashreport);
                        }

                        NetworkSystem.field_151275_b.warn("Failed to handle packet for " + networkmanager.func_74430_c(), (Throwable) exception);
                        ChatComponentText chatcomponenttext = new ChatComponentText("Internal server error");

                        networkmanager.func_150725_a(new S40PacketDisconnect(chatcomponenttext), new NetworkSystem$4(this, networkmanager, chatcomponenttext));
                        networkmanager.func_150721_g();
                    }
                }
            }

        }
    }

    public MinecraftServer func_151267_d() {
        return this.field_151273_d;
    }
}
