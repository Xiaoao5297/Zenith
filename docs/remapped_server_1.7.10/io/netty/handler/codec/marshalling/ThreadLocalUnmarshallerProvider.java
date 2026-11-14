package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

public class ThreadLocalUnmarshallerProvider implements UnmarshallerProvider {

    private final ThreadLocal<Unmarshaller> unmarshallers = new ThreadLocal();
    private final MarshallerFactory factory;
    private final MarshallingConfiguration config;

    public ThreadLocalUnmarshallerProvider(MarshallerFactory marshallerfactory, MarshallingConfiguration marshallingconfiguration) {
        this.factory = marshallerfactory;
        this.config = marshallingconfiguration;
    }

    @Override
    public Unmarshaller getUnmarshaller(ChannelHandlerContext channelhandlercontext) throws Exception {
        Unmarshaller unmarshaller = (Unmarshaller) this.unmarshallers.get();

        if (unmarshaller == null) {
            unmarshaller = this.factory.createUnmarshaller(this.config);
            this.unmarshallers.set(unmarshaller);
        }

        return unmarshaller;
    }
}
