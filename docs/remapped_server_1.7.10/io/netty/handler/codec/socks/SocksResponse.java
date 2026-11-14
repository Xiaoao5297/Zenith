package io.netty.handler.codec.socks;

public abstract class SocksResponse extends SocksMessage {

    private final SocksResponseType responseType;

    protected SocksResponse(SocksResponseType socksresponsetype) {
        super(SocksMessageType.RESPONSE);
        if (socksresponsetype == null) {
            throw new NullPointerException("responseType");
        } else {
            this.responseType = socksresponsetype;
        }
    }

    public SocksResponseType responseType() {
        return this.responseType;
    }
}
