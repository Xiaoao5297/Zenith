package io.netty.handler.logging;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;

@ChannelHandler.Sharable
public class LoggingHandler extends ChannelDuplexHandler {

    private static final LogLevel DEFAULT_LEVEL = LogLevel.DEBUG;
    private static final String NEWLINE = String.format("%n");
    private static final String[] BYTE2HEX = new String[256];
    private static final String[] HEXPADDING = new String[16];
    private static final String[] BYTEPADDING = new String[16];
    private static final char[] BYTE2CHAR = new char[256];
    protected final InternalLogger logger;
    protected final InternalLogLevel internalLevel;
    private final LogLevel level;

    public LoggingHandler() {
        this(LoggingHandler.DEFAULT_LEVEL);
    }

    public LoggingHandler(LogLevel loglevel) {
        if (loglevel == null) {
            throw new NullPointerException("level");
        } else {
            this.logger = InternalLoggerFactory.getInstance(this.getClass());
            this.level = loglevel;
            this.internalLevel = loglevel.toInternalLevel();
        }
    }

    public LoggingHandler(Class<?> oclass) {
        this(oclass, LoggingHandler.DEFAULT_LEVEL);
    }

    public LoggingHandler(Class<?> oclass, LogLevel loglevel) {
        if (oclass == null) {
            throw new NullPointerException("clazz");
        } else if (loglevel == null) {
            throw new NullPointerException("level");
        } else {
            this.logger = InternalLoggerFactory.getInstance(oclass);
            this.level = loglevel;
            this.internalLevel = loglevel.toInternalLevel();
        }
    }

    public LoggingHandler(String s) {
        this(s, LoggingHandler.DEFAULT_LEVEL);
    }

    public LoggingHandler(String s, LogLevel loglevel) {
        if (s == null) {
            throw new NullPointerException("name");
        } else if (loglevel == null) {
            throw new NullPointerException("level");
        } else {
            this.logger = InternalLoggerFactory.getInstance(s);
            this.level = loglevel;
            this.internalLevel = loglevel.toInternalLevel();
        }
    }

    public LogLevel level() {
        return this.level;
    }

    protected String format(ChannelHandlerContext channelhandlercontext, String s) {
        String s1 = channelhandlercontext.channel().toString();
        StringBuilder stringbuilder = new StringBuilder(s1.length() + s.length() + 1);

        stringbuilder.append(s1);
        stringbuilder.append(' ');
        stringbuilder.append(s);
        return stringbuilder.toString();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(channelhandlercontext, "REGISTERED"));
        }

        super.channelRegistered(channelhandlercontext);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(channelhandlercontext, "UNREGISTERED"));
        }

        super.channelUnregistered(channelhandlercontext);
    }

    @Override
    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(channelhandlercontext, "ACTIVE"));
        }

        super.channelActive(channelhandlercontext);
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(channelhandlercontext, "INACTIVE"));
        }

        super.channelInactive(channelhandlercontext);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(channelhandlercontext, "EXCEPTION: " + throwable), throwable);
        }

        super.exceptionCaught(channelhandlercontext, throwable);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(channelhandlercontext, "USER_EVENT: " + object));
        }

        super.userEventTriggered(channelhandlercontext, object);
    }

    @Override
    public void bind(ChannelHandlerContext channelhandlercontext, SocketAddress socketaddress, ChannelPromise channelpromise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(channelhandlercontext, "BIND(" + socketaddress + ')'));
        }

        super.bind(channelhandlercontext, socketaddress, channelpromise);
    }

    @Override
    public void connect(ChannelHandlerContext channelhandlercontext, SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(channelhandlercontext, "CONNECT(" + socketaddress + ", " + socketaddress1 + ')'));
        }

        super.connect(channelhandlercontext, socketaddress, socketaddress1, channelpromise);
    }

    @Override
    public void disconnect(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(channelhandlercontext, "DISCONNECT()"));
        }

        super.disconnect(channelhandlercontext, channelpromise);
    }

    @Override
    public void close(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(channelhandlercontext, "CLOSE()"));
        }

        super.close(channelhandlercontext, channelpromise);
    }

    @Override
    public void deregister(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(channelhandlercontext, "DEREGISTER()"));
        }

        super.deregister(channelhandlercontext, channelpromise);
    }

    @Override
    public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        this.logMessage(channelhandlercontext, "RECEIVED", object);
        channelhandlercontext.fireChannelRead(object);
    }

    @Override
    public void write(ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception {
        this.logMessage(channelhandlercontext, "WRITE", object);
        channelhandlercontext.write(object, channelpromise);
    }

    @Override
    public void flush(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(channelhandlercontext, "FLUSH"));
        }

        channelhandlercontext.flush();
    }

    private void logMessage(ChannelHandlerContext channelhandlercontext, String s, Object object) {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(channelhandlercontext, this.formatMessage(s, object)));
        }

    }

    protected String formatMessage(String s, Object object) {
        return object instanceof ByteBuf ? this.formatByteBuf(s, (ByteBuf) object) : (object instanceof ByteBufHolder ? this.formatByteBufHolder(s, (ByteBufHolder) object) : this.formatNonByteBuf(s, object));
    }

    protected String formatByteBuf(String s, ByteBuf bytebuf) {
        int i = bytebuf.readableBytes();
        int j = i / 16 + (i % 15 == 0 ? 0 : 1) + 4;
        StringBuilder stringbuilder = new StringBuilder(j * 80 + s.length() + 16);

        stringbuilder.append(s).append('(').append(i).append('B').append(')');
        stringbuilder.append(LoggingHandler.NEWLINE + "         +-------------------------------------------------+" + LoggingHandler.NEWLINE + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |" + LoggingHandler.NEWLINE + "+--------+-------------------------------------------------+----------------+");
        int k = bytebuf.readerIndex();
        int l = bytebuf.writerIndex();

        int i1;
        int j1;
        int k1;

        for (j1 = k; j1 < l; ++j1) {
            i1 = j1 - k;
            k1 = i1 & 15;
            if (k1 == 0) {
                stringbuilder.append(LoggingHandler.NEWLINE);
                stringbuilder.append(Long.toHexString((long) i1 & 4294967295L | 4294967296L));
                stringbuilder.setCharAt(stringbuilder.length() - 9, '|');
                stringbuilder.append('|');
            }

            stringbuilder.append(LoggingHandler.BYTE2HEX[bytebuf.getUnsignedByte(j1)]);
            if (k1 == 15) {
                stringbuilder.append(" |");

                for (int l1 = j1 - 15; l1 <= j1; ++l1) {
                    stringbuilder.append(LoggingHandler.BYTE2CHAR[bytebuf.getUnsignedByte(l1)]);
                }

                stringbuilder.append('|');
            }
        }

        if ((j1 - k & 15) != 0) {
            i1 = i & 15;
            stringbuilder.append(LoggingHandler.HEXPADDING[i1]);
            stringbuilder.append(" |");

            for (k1 = j1 - i1; k1 < j1; ++k1) {
                stringbuilder.append(LoggingHandler.BYTE2CHAR[bytebuf.getUnsignedByte(k1)]);
            }

            stringbuilder.append(LoggingHandler.BYTEPADDING[i1]);
            stringbuilder.append('|');
        }

        stringbuilder.append(LoggingHandler.NEWLINE + "+--------+-------------------------------------------------+----------------+");
        return stringbuilder.toString();
    }

    protected String formatNonByteBuf(String s, Object object) {
        return s + ": " + object;
    }

    protected String formatByteBufHolder(String s, ByteBufHolder bytebufholder) {
        return this.formatByteBuf(s, bytebufholder.content());
    }

    static {
        StringBuilder stringbuilder;
        int i;

        for (i = 0; i < 10; ++i) {
            stringbuilder = new StringBuilder(3);
            stringbuilder.append(" 0");
            stringbuilder.append(i);
            LoggingHandler.BYTE2HEX[i] = stringbuilder.toString();
        }

        while (i < 16) {
            stringbuilder = new StringBuilder(3);
            stringbuilder.append(" 0");
            stringbuilder.append((char) (97 + i - 10));
            LoggingHandler.BYTE2HEX[i] = stringbuilder.toString();
            ++i;
        }

        while (i < LoggingHandler.BYTE2HEX.length) {
            stringbuilder = new StringBuilder(3);
            stringbuilder.append(' ');
            stringbuilder.append(Integer.toHexString(i));
            LoggingHandler.BYTE2HEX[i] = stringbuilder.toString();
            ++i;
        }

        int j;
        StringBuilder stringbuilder1;
        int k;

        for (i = 0; i < LoggingHandler.HEXPADDING.length; ++i) {
            j = LoggingHandler.HEXPADDING.length - i;
            stringbuilder1 = new StringBuilder(j * 3);

            for (k = 0; k < j; ++k) {
                stringbuilder1.append("   ");
            }

            LoggingHandler.HEXPADDING[i] = stringbuilder1.toString();
        }

        for (i = 0; i < LoggingHandler.BYTEPADDING.length; ++i) {
            j = LoggingHandler.BYTEPADDING.length - i;
            stringbuilder1 = new StringBuilder(j);

            for (k = 0; k < j; ++k) {
                stringbuilder1.append(' ');
            }

            LoggingHandler.BYTEPADDING[i] = stringbuilder1.toString();
        }

        for (i = 0; i < LoggingHandler.BYTE2CHAR.length; ++i) {
            if (i > 31 && i < 127) {
                LoggingHandler.BYTE2CHAR[i] = (char) i;
            } else {
                LoggingHandler.BYTE2CHAR[i] = '.';
            }
        }

    }
}
