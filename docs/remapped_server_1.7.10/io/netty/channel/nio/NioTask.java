package io.netty.channel.nio;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public interface NioTask<C extends SelectableChannel> {

    void channelReady(C c0, SelectionKey selectionkey) throws Exception;

    void channelUnregistered(C c0, Throwable throwable) throws Exception;
}
