package io.netty.channel.udt.nio;

import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.SocketChannelUDT;
import io.netty.channel.ChannelMetadata;
import java.util.List;

public class NioUdtMessageAcceptorChannel extends NioUdtAcceptorChannel {

    private static final ChannelMetadata METADATA = new ChannelMetadata(false);

    public NioUdtMessageAcceptorChannel() {
        super(TypeUDT.DATAGRAM);
    }

    @Override
    protected int doReadMessages(List<Object> list) throws Exception {
        SocketChannelUDT socketchanneludt = this.javaChannel().accept();

        if (socketchanneludt == null) {
            return 0;
        } else {
            list.add(new NioUdtMessageConnectorChannel(this, socketchanneludt));
            return 1;
        }
    }

    @Override
    public ChannelMetadata metadata() {
        return NioUdtMessageAcceptorChannel.METADATA;
    }
}
