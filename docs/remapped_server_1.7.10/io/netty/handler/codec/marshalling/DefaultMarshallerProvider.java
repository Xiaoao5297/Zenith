package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;

public class DefaultMarshallerProvider implements MarshallerProvider {

    private final MarshallerFactory factory;
    private final MarshallingConfiguration config;

    public DefaultMarshallerProvider(MarshallerFactory marshallerfactory, MarshallingConfiguration marshallingconfiguration) {
        this.factory = marshallerfactory;
        this.config = marshallingconfiguration;
    }

    @Override
    public Marshaller getMarshaller(ChannelHandlerContext channelhandlercontext) throws Exception {
        return this.factory.createMarshaller(this.config);
    }
}
