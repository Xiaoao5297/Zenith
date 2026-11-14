package io.netty.handler.codec.socks;

public abstract class SocksRequest extends SocksMessage {

    private final SocksRequestType requestType;

    protected SocksRequest(SocksRequestType socksrequesttype) {
        super(SocksMessageType.REQUEST);
        if (socksrequesttype == null) {
            throw new NullPointerException("requestType");
        } else {
            this.requestType = socksrequesttype;
        }
    }

    public SocksRequestType requestType() {
        return this.requestType;
    }
}
