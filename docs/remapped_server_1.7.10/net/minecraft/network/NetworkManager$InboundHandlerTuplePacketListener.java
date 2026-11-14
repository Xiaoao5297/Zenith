package net.minecraft.network;

import io.netty.util.concurrent.GenericFutureListener;

class NetworkManager$InboundHandlerTuplePacketListener {

    private final Packet field_150774_a;
    private final GenericFutureListener[] field_150773_b;

    public NetworkManager$InboundHandlerTuplePacketListener(Packet packet, GenericFutureListener... agenericfuturelistener) {
        this.field_150774_a = packet;
        this.field_150773_b = agenericfuturelistener;
    }
}
