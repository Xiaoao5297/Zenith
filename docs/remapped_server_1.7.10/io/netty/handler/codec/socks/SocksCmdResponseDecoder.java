package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;
import java.util.List;

public class SocksCmdResponseDecoder extends ReplayingDecoder<SocksCmdResponseDecoder.State> {

    private static final String name = "SOCKS_CMD_RESPONSE_DECODER";
    private SocksProtocolVersion version;
    private int fieldLength;
    private SocksCmdStatus cmdStatus;
    private SocksAddressType addressType;
    private byte reserved;
    private String host;
    private int port;
    private SocksResponse msg;

    public static String getName() {
        return "SOCKS_CMD_RESPONSE_DECODER";
    }

    public SocksCmdResponseDecoder() {
        super(SocksCmdResponseDecoder.State.CHECK_PROTOCOL_VERSION);
        this.msg = SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE;
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        switch ((SocksCmdResponseDecoder.State) this.state()) {
            case CHECK_PROTOCOL_VERSION:
                this.version = SocksProtocolVersion.fromByte(bytebuf.readByte());
                if (this.version != SocksProtocolVersion.SOCKS5) {
                    break;
                }

                this.checkpoint(SocksCmdResponseDecoder.State.READ_CMD_HEADER);
            case READ_CMD_HEADER:
                this.cmdStatus = SocksCmdStatus.fromByte(bytebuf.readByte());
                this.reserved = bytebuf.readByte();
                this.addressType = SocksAddressType.fromByte(bytebuf.readByte());
                this.checkpoint(SocksCmdResponseDecoder.State.READ_CMD_ADDRESS);
            case READ_CMD_ADDRESS:
                switch (this.addressType) {
                    case IPv4:
                        this.host = SocksCommonUtils.intToIp(bytebuf.readInt());
                        this.port = bytebuf.readUnsignedShort();
                        this.msg = new SocksCmdResponse(this.cmdStatus, this.addressType);
                        break;
                    case DOMAIN:
                        this.fieldLength = bytebuf.readByte();
                        this.host = bytebuf.readBytes(this.fieldLength).toString(CharsetUtil.US_ASCII);
                        this.port = bytebuf.readUnsignedShort();
                        this.msg = new SocksCmdResponse(this.cmdStatus, this.addressType);
                        break;
                    case IPv6:
                        this.host = SocksCommonUtils.ipv6toStr(bytebuf.readBytes(16).array());
                        this.port = bytebuf.readUnsignedShort();
                        this.msg = new SocksCmdResponse(this.cmdStatus, this.addressType);
                    case UNKNOWN:
                }
        }

        channelhandlercontext.pipeline().remove((ChannelHandler) this);
        list.add(this.msg);
    }

    static enum State {

        CHECK_PROTOCOL_VERSION, READ_CMD_HEADER, READ_CMD_ADDRESS;

        private State() {}
    }
}
