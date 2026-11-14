package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;

public class ThreadLocalMarshallerProvider implements MarshallerProvider {

    private final ThreadLocal<Marshaller> marshallers = new ThreadLocal();
    private final MarshallerFactory factory;
    private final MarshallingConfiguration config;

    public ThreadLocalMarshallerProvider(MarshallerFactory marshallerfactory, MarshallingConfiguration marshallingconfiguration) {
        this.factory = marshallerfactory;
        this.config = marshallingconfiguration;
    }

    @Override
    public Marshaller getMarshaller(ChannelHandlerContext channelhandlercontext) throws Exception {
        Marshaller marshaller = (Marshaller) this.marshallers.get();

        if (marshaller == null) {
            marshaller = this.factory.createMarshaller(this.config);
            this.marshallers.set(marshaller);
        }

        return marshaller;
    }
}
