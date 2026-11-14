package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

public class ContextBoundUnmarshallerProvider extends DefaultUnmarshallerProvider {

    private static final AttributeKey<Unmarshaller> UNMARSHALLER = new AttributeKey<>(ContextBoundUnmarshallerProvider.class.getName() + ".unmarshaller");

    public ContextBoundUnmarshallerProvider(MarshallerFactory marshallerfactory, MarshallingConfiguration marshallingconfiguration) {
        super(marshallerfactory, marshallingconfiguration);
    }

    @Override
    public Unmarshaller getUnmarshaller(ChannelHandlerContext channelhandlercontext) throws Exception {
        Attribute attribute = channelhandlercontext.attr(ContextBoundUnmarshallerProvider.UNMARSHALLER);
        Unmarshaller unmarshaller = (Unmarshaller) attribute.get();

        if (unmarshaller == null) {
            unmarshaller = super.getUnmarshaller(channelhandlercontext);
            attribute.set(unmarshaller);
        }

        return unmarshaller;
    }
}
