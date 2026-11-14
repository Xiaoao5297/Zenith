package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.Queue;
import javax.crypto.SecretKey;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkManager extends SimpleChannelInboundHandler {

    private static final Logger field_150735_g = LogManager.getLogger();
    public static final Marker field_150740_a = MarkerManager.getMarker("NETWORK");
    public static final Marker field_150738_b = MarkerManager.getMarker("NETWORK_PACKETS", NetworkManager.field_150740_a);
    public static final Marker field_152461_c = MarkerManager.getMarker("NETWORK_STAT", NetworkManager.field_150740_a);
    public static final AttributeKey field_150739_c = new AttributeKey<>("protocol");
    public static final AttributeKey field_150736_d = new AttributeKey<>("receivable_packets");
    public static final AttributeKey field_150737_e = new AttributeKey<>("sendable_packets");
    public static final NioEventLoopGroup field_150734_f = new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
    public static final NetworkStatistics field_152462_h = new NetworkStatistics();
    private final boolean field_150747_h;
    private final Queue field_150748_i = Queues.newConcurrentLinkedQueue();
    private final Queue field_150745_j = Queues.newConcurrentLinkedQueue();
    private Channel field_150746_k;
    private SocketAddress field_150743_l;
    private INetHandler field_150744_m;
    private EnumConnectionState field_150741_n;
    private IChatComponent field_150742_o;
    private boolean field_152463_r;

    public NetworkManager(boolean flag) {
        this.field_150747_h = flag;
    }

    @Override
    public void channelActive(ChannelHandlerContext channelhandlercontext) {
        super.channelActive(channelhandlercontext);
        this.field_150746_k = channelhandlercontext.channel();
        this.field_150743_l = this.field_150746_k.remoteAddress();
        this.func_150723_a(EnumConnectionState.HANDSHAKING);
    }

    public void func_150723_a(EnumConnectionState enumconnectionstate) {
        this.field_150741_n = (EnumConnectionState) this.field_150746_k.attr(NetworkManager.field_150739_c).getAndSet(enumconnectionstate);
        this.field_150746_k.attr(NetworkManager.field_150736_d).set(enumconnectionstate.func_150757_a(this.field_150747_h));
        this.field_150746_k.attr(NetworkManager.field_150737_e).set(enumconnectionstate.func_150754_b(this.field_150747_h));
        this.field_150746_k.config().setAutoRead(true);
        NetworkManager.field_150735_g.debug("Enabled auto read");
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) {
        this.func_150718_a(new ChatComponentTranslation("disconnect.endOfStream", new Object[0]));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) {
        ChatComponentTranslation chatcomponenttranslation;

        if (throwable instanceof TimeoutException) {
            chatcomponenttranslation = new ChatComponentTranslation("disconnect.timeout", new Object[0]);
        } else {
            chatcomponenttranslation = new ChatComponentTranslation("disconnect.genericReason", new Object[]{"Internal Exception: " + throwable});
        }

        this.func_150718_a(chatcomponenttranslation);
    }

    protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet packet) {
        if (this.field_150746_k.isOpen()) {
            if (packet.func_148836_a()) {
                packet.func_148833_a(this.field_150744_m);
            } else {
                this.field_150748_i.add(packet);
            }
        }

    }

    public void func_150719_a(INetHandler inethandler) {
        Validate.notNull(inethandler, "packetListener");
        NetworkManager.field_150735_g.debug("Set listener of {} to {}", this, inethandler);
        this.field_150744_m = inethandler;
    }

    public void func_150725_a(Packet packet, GenericFutureListener... agenericfuturelistener) {
        if (this.field_150746_k != null && this.field_150746_k.isOpen()) {
            this.func_150733_h();
            this.func_150732_b(packet, agenericfuturelistener);
        } else {
            this.field_150745_j.add(new NetworkManager$InboundHandlerTuplePacketListener(packet, agenericfuturelistener));
        }

    }

    private void func_150732_b(Packet packet, GenericFutureListener[] agenericfuturelistener) {
        EnumConnectionState enumconnectionstate = EnumConnectionState.func_150752_a(packet);
        EnumConnectionState enumconnectionstate1 = (EnumConnectionState) this.field_150746_k.attr(NetworkManager.field_150739_c).get();

        if (enumconnectionstate1 != enumconnectionstate) {
            NetworkManager.field_150735_g.debug("Disabled auto read");
            this.field_150746_k.config().setAutoRead(false);
        }

        if (this.field_150746_k.eventLoop().inEventLoop()) {
            if (enumconnectionstate != enumconnectionstate1) {
                this.func_150723_a(enumconnectionstate);
            }

            this.field_150746_k.writeAndFlush(packet).addListeners(agenericfuturelistener).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.field_150746_k.eventLoop().execute(new NetworkManager$1(this, enumconnectionstate, enumconnectionstate1, packet, agenericfuturelistener));
        }

    }

    private void func_150733_h() {
        if (this.field_150746_k != null && this.field_150746_k.isOpen()) {
            while (!this.field_150745_j.isEmpty()) {
                NetworkManager$InboundHandlerTuplePacketListener networkmanager$inboundhandlertuplepacketlistener = (NetworkManager$InboundHandlerTuplePacketListener) this.field_150745_j.poll();

                this.func_150732_b(NetworkManager$InboundHandlerTuplePacketListener.access$100(networkmanager$inboundhandlertuplepacketlistener), NetworkManager$InboundHandlerTuplePacketListener.access$200(networkmanager$inboundhandlertuplepacketlistener));
            }

        }
    }

    public void func_74428_b() {
        this.func_150733_h();
        EnumConnectionState enumconnectionstate = (EnumConnectionState) this.field_150746_k.attr(NetworkManager.field_150739_c).get();

        if (this.field_150741_n != enumconnectionstate) {
            if (this.field_150741_n != null) {
                this.field_150744_m.func_147232_a(this.field_150741_n, enumconnectionstate);
            }

            this.field_150741_n = enumconnectionstate;
        }

        if (this.field_150744_m != null) {
            for (int i = 1000; !this.field_150748_i.isEmpty() && i >= 0; --i) {
                Packet packet = (Packet) this.field_150748_i.poll();

                packet.func_148833_a(this.field_150744_m);
            }

            this.field_150744_m.func_147233_a();
        }

        this.field_150746_k.flush();
    }

    public SocketAddress func_74430_c() {
        return this.field_150743_l;
    }

    public void func_150718_a(IChatComponent ichatcomponent) {
        if (this.field_150746_k.isOpen()) {
            this.field_150746_k.close();
            this.field_150742_o = ichatcomponent;
        }

    }

    public boolean func_150731_c() {
        return this.field_150746_k instanceof LocalChannel || this.field_150746_k instanceof LocalServerChannel;
    }

    public void func_150727_a(SecretKey secretkey) {
        this.field_150746_k.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.func_151229_a(2, secretkey)));
        this.field_150746_k.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.func_151229_a(1, secretkey)));
        this.field_152463_r = true;
    }

    public boolean func_150724_d() {
        return this.field_150746_k != null && this.field_150746_k.isOpen();
    }

    public INetHandler func_150729_e() {
        return this.field_150744_m;
    }

    public IChatComponent func_150730_f() {
        return this.field_150742_o;
    }

    public void func_150721_g() {
        this.field_150746_k.config().setAutoRead(false);
    }
}
