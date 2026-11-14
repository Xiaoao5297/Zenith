package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

public class DefaultUnmarshallerProvider implements UnmarshallerProvider {

    private final MarshallerFactory factory;
    private final MarshallingConfiguration config;

    public DefaultUnmarshallerProvider(MarshallerFactory marshallerfactory, MarshallingConfiguration marshallingconfiguration) {
        this.factory = marshallerfactory;
        this.config = marshallingconfiguration;
    }

    @Override
    public Unmarshaller getUnmarshaller(ChannelHandlerContext channelhandlercontext) throws Exception {
        return this.factory.createUnmarshaller(this.config);
    }
}
