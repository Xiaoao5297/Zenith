package net.minecraft.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PingResponseHandler extends ChannelInboundHandlerAdapter {

    private static final Logger field_151258_a = LogManager.getLogger();
    private NetworkSystem field_151257_b;

    public PingResponseHandler(NetworkSystem networksystem) {
        this.field_151257_b = networksystem;
    }

    @Override
    public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) {
        ByteBuf bytebuf = (ByteBuf) object;

        bytebuf.markReaderIndex();
        boolean flag = true;

        try {
            if (bytebuf.readUnsignedByte() != 254) {
                return;
            }

            InetSocketAddress inetsocketaddress = (InetSocketAddress) channelhandlercontext.channel().remoteAddress();
            MinecraftServer minecraftserver = this.field_151257_b.func_151267_d();
            int i = bytebuf.readableBytes();
            String s;

            switch (i) {
                case 0:
                    PingResponseHandler.field_151258_a.debug("Ping: (<1.3.x) from {}:{}", inetsocketaddress.getAddress(), inetsocketaddress.getPort());
                    s = String.format("%s\u00a7%d\u00a7%d", minecraftserver.func_71273_Y(), minecraftserver.func_71233_x(), minecraftserver.func_71275_y());
                    this.func_151256_a(channelhandlercontext, this.func_151255_a(s));
                    break;
                case 1:
                    if (bytebuf.readUnsignedByte() != 1) {
                        return;
                    }

                    PingResponseHandler.field_151258_a.debug("Ping: (1.4-1.5.x) from {}:{}", inetsocketaddress.getAddress(), inetsocketaddress.getPort());
                    s = String.format("\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, minecraftserver.func_71249_w(), minecraftserver.func_71273_Y(), minecraftserver.func_71233_x(), minecraftserver.func_71275_y());
                    this.func_151256_a(channelhandlercontext, this.func_151255_a(s));
                    break;
                default:
                    boolean flag1 = bytebuf.readUnsignedByte() == 1;

                    flag1 &= bytebuf.readUnsignedByte() == 250;
                    flag1 &= "MC|PingHost".equals(new String(bytebuf.readBytes(bytebuf.readShort() * 2).array(), Charsets.UTF_16BE));
                    int j = bytebuf.readUnsignedShort();

                    flag1 &= bytebuf.readUnsignedByte() >= 73;
                    flag1 &= 3 + bytebuf.readBytes(bytebuf.readShort() * 2).array().length + 4 == j;
                    flag1 &= bytebuf.readInt() <= 65535;
                    flag1 &= bytebuf.readableBytes() == 0;
                    if (!flag1) {
                        return;
                    }

                    PingResponseHandler.field_151258_a.debug("Ping: (1.6) from {}:{}", inetsocketaddress.getAddress(), inetsocketaddress.getPort());
                    String s1 = String.format("\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, minecraftserver.func_71249_w(), minecraftserver.func_71273_Y(), minecraftserver.func_71233_x(), minecraftserver.func_71275_y());
                    ByteBuf bytebuf1 = this.func_151255_a(s1);

                    try {
                        this.func_151256_a(channelhandlercontext, bytebuf1);
                    } finally {
                        bytebuf1.release();
                    }
            }

            bytebuf.release();
            flag = false;
        } catch (RuntimeException runtimeexception) {
            ;
        } finally {
            if (flag) {
                bytebuf.resetReaderIndex();
                channelhandlercontext.channel().pipeline().remove("legacy_query");
                channelhandlercontext.fireChannelRead(object);
            }

        }

    }

    private void func_151256_a(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf) {
        channelhandlercontext.pipeline().firstContext().writeAndFlush(bytebuf).addListener(ChannelFutureListener.CLOSE);
    }

    private ByteBuf func_151255_a(String s) {
        ByteBuf bytebuf = Unpooled.buffer();

        bytebuf.writeByte(255);
        char[] achar = s.toCharArray();

        bytebuf.writeShort(achar.length);
        char[] achar1 = achar;
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char c0 = achar1[j];

            bytebuf.writeChar(c0);
        }

        return bytebuf;
    }
}
