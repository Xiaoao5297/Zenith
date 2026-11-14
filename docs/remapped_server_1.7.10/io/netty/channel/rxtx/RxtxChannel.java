package io.netty.channel.rxtx;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.oio.OioByteStreamChannel;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class RxtxChannel extends OioByteStreamChannel {

    private static final RxtxDeviceAddress LOCAL_ADDRESS = new RxtxDeviceAddress("localhost");
    private final RxtxChannelConfig config = new DefaultRxtxChannelConfig(this);
    private boolean open = true;
    private RxtxDeviceAddress deviceAddress;
    private SerialPort serialPort;

    public RxtxChannel() {
        super((Channel) null);
    }

    @Override
    public RxtxChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    @Override
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new RxtxChannel.RxtxUnsafe();
    }

    @Override
    protected void doConnect(SocketAddress socketaddress, SocketAddress socketaddress1) throws Exception {
        RxtxDeviceAddress rxtxdeviceaddress = (RxtxDeviceAddress) socketaddress;
        CommPortIdentifier commportidentifier = CommPortIdentifier.getPortIdentifier(rxtxdeviceaddress.value());
        CommPort commport = commportidentifier.open(this.getClass().getName(), 1000);

        this.deviceAddress = rxtxdeviceaddress;
        this.serialPort = (SerialPort) commport;
    }

    protected void doInit() throws Exception {
        this.serialPort.setSerialPortParams((Integer) this.config().getOption(RxtxChannelOption.BAUD_RATE), ((RxtxChannelConfig.Databits) this.config().getOption(RxtxChannelOption.DATA_BITS)).value(), ((RxtxChannelConfig.Stopbits) this.config().getOption(RxtxChannelOption.STOP_BITS)).value(), ((RxtxChannelConfig.Paritybit) this.config().getOption(RxtxChannelOption.PARITY_BIT)).value());
        this.serialPort.setDTR((Boolean) this.config().getOption(RxtxChannelOption.DTR));
        this.serialPort.setRTS((Boolean) this.config().getOption(RxtxChannelOption.RTS));
        this.activate(this.serialPort.getInputStream(), this.serialPort.getOutputStream());
    }

    @Override
    public RxtxDeviceAddress localAddress() {
        return (RxtxDeviceAddress) super.localAddress();
    }

    @Override
    public RxtxDeviceAddress remoteAddress() {
        return (RxtxDeviceAddress) super.remoteAddress();
    }

    @Override
    protected RxtxDeviceAddress localAddress0() {
        return RxtxChannel.LOCAL_ADDRESS;
    }

    @Override
    protected RxtxDeviceAddress remoteAddress0() {
        return this.deviceAddress;
    }

    @Override
    protected void doBind(SocketAddress socketaddress) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doClose() throws Exception {
        this.open = false;

        try {
            super.doClose();
        } finally {
            if (this.serialPort != null) {
                this.serialPort.removeEventListener();
                this.serialPort.close();
                this.serialPort = null;
            }

        }

    }

    private final class RxtxUnsafe extends AbstractChannel.AbstractUnsafe {

        private RxtxUnsafe() {
            super();
        }

        @Override
        public void connect(SocketAddress socketaddress, SocketAddress socketaddress1, final ChannelPromise channelpromise) {
            if (this.ensureOpen(channelpromise)) {
                try {
                    final boolean flag = RxtxChannel.this.isActive();

                    RxtxChannel.this.doConnect(socketaddress, socketaddress1);
                    int i = (Integer) RxtxChannel.this.config().getOption(RxtxChannelOption.WAIT_TIME);

                    if (i > 0) {
                        RxtxChannel.this.eventLoop().schedule(new Runnable() {
                            public void run() {
                                try {
                                    RxtxChannel.this.doInit();
                                    channelpromise.setSuccess();
                                    if (!flag && RxtxChannel.this.isActive()) {
                                        RxtxChannel.this.pipeline().fireChannelActive();
                                    }
                                } catch (Throwable throwable) {
                                    channelpromise.setFailure(throwable);
                                    RxtxUnsafe.this.closeIfClosed();
                                }

                            }
                        }, (long) i, TimeUnit.MILLISECONDS);
                    } else {
                        RxtxChannel.this.doInit();
                        channelpromise.setSuccess();
                        if (!flag && RxtxChannel.this.isActive()) {
                            RxtxChannel.this.pipeline().fireChannelActive();
                        }
                    }
                } catch (Throwable throwable) {
                    channelpromise.setFailure(throwable);
                    this.closeIfClosed();
                }

            }
        }
    }
}
