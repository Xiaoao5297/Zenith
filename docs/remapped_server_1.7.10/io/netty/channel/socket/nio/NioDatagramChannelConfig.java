package io.netty.channel.socket.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DefaultDatagramChannelConfig;
import io.netty.util.internal.PlatformDependent;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.NetworkChannel;
import java.util.Enumeration;

class NioDatagramChannelConfig extends DefaultDatagramChannelConfig {

    private static final Object IP_MULTICAST_TTL;
    private static final Object IP_MULTICAST_IF;
    private static final Object IP_MULTICAST_LOOP;
    private static final Method GET_OPTION;
    private static final Method SET_OPTION;
    private final DatagramChannel javaChannel;

    NioDatagramChannelConfig(NioDatagramChannel niodatagramchannel, DatagramChannel datagramchannel) {
        super(niodatagramchannel, datagramchannel.socket());
        this.javaChannel = datagramchannel;
    }

    @Override
    public int getTimeToLive() {
        return (Integer) this.getOption0(NioDatagramChannelConfig.IP_MULTICAST_TTL);
    }

    @Override
    public DatagramChannelConfig setTimeToLive(int i) {
        this.setOption0(NioDatagramChannelConfig.IP_MULTICAST_TTL, i);
        return this;
    }

    @Override
    public InetAddress getInterface() {
        NetworkInterface networkinterface = this.getNetworkInterface();

        if (networkinterface == null) {
            return null;
        } else {
            Enumeration enumeration = networkinterface.getInetAddresses();

            return enumeration.hasMoreElements() ? (InetAddress) enumeration.nextElement() : null;
        }
    }

    @Override
    public DatagramChannelConfig setInterface(InetAddress inetaddress) {
        try {
            this.setNetworkInterface(NetworkInterface.getByInetAddress(inetaddress));
            return this;
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public NetworkInterface getNetworkInterface() {
        return (NetworkInterface) this.getOption0(NioDatagramChannelConfig.IP_MULTICAST_IF);
    }

    @Override
    public DatagramChannelConfig setNetworkInterface(NetworkInterface networkinterface) {
        this.setOption0(NioDatagramChannelConfig.IP_MULTICAST_IF, networkinterface);
        return this;
    }

    @Override
    public boolean isLoopbackModeDisabled() {
        return (Boolean) this.getOption0(NioDatagramChannelConfig.IP_MULTICAST_LOOP);
    }

    @Override
    public DatagramChannelConfig setLoopbackModeDisabled(boolean flag) {
        this.setOption0(NioDatagramChannelConfig.IP_MULTICAST_LOOP, flag);
        return this;
    }

    private Object getOption0(Object object) {
        if (PlatformDependent.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        } else {
            try {
                return NioDatagramChannelConfig.GET_OPTION.invoke(this.javaChannel, object);
            } catch (Exception exception) {
                throw new ChannelException(exception);
            }
        }
    }

    private void setOption0(Object object, Object object1) {
        if (PlatformDependent.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        } else {
            try {
                NioDatagramChannelConfig.SET_OPTION.invoke(this.javaChannel, object, object1);
            } catch (Exception exception) {
                throw new ChannelException(exception);
            }
        }
    }

    static {
        ClassLoader classloader = DatagramChannel.class.getClassLoader();
        Class oclass = null;

        try {
            oclass = Class.forName("java.net.SocketOption", true, classloader);
        } catch (Exception exception) {
            ;
        }

        Class oclass1 = null;

        try {
            oclass1 = Class.forName("java.net.StandardSocketOptions", true, classloader);
        } catch (Exception exception1) {
            ;
        }

        Object object = null;
        Object object1 = null;
        Object object2 = null;
        Method method = null;
        Method method1 = null;

        if (oclass != null) {
            try {
                object = oclass1.getDeclaredField("IP_MULTICAST_TTL").get((Object) null);
            } catch (Exception exception2) {
                throw new Error("cannot locate the IP_MULTICAST_TTL field", exception2);
            }

            try {
                object1 = oclass1.getDeclaredField("IP_MULTICAST_IF").get((Object) null);
            } catch (Exception exception3) {
                throw new Error("cannot locate the IP_MULTICAST_IF field", exception3);
            }

            try {
                object2 = oclass1.getDeclaredField("IP_MULTICAST_LOOP").get((Object) null);
            } catch (Exception exception4) {
                throw new Error("cannot locate the IP_MULTICAST_LOOP field", exception4);
            }

            try {
                method = NetworkChannel.class.getDeclaredMethod("getOption", oclass);
            } catch (Exception exception5) {
                throw new Error("cannot locate the getOption() method", exception5);
            }

            try {
                method1 = NetworkChannel.class.getDeclaredMethod("setOption", oclass, Object.class);
            } catch (Exception exception6) {
                throw new Error("cannot locate the setOption() method", exception6);
            }
        }

        IP_MULTICAST_TTL = object;
        IP_MULTICAST_IF = object1;
        IP_MULTICAST_LOOP = object2;
        GET_OPTION = method;
        SET_OPTION = method1;
    }
}
