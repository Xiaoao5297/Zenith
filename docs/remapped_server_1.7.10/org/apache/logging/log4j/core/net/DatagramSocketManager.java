package org.apache.logging.log4j.core.net;

import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.helpers.Strings;

public class DatagramSocketManager extends AbstractSocketManager {

    private static final DatagramSocketManager.DatagramSocketManagerFactory FACTORY = new DatagramSocketManager.DatagramSocketManagerFactory();

    protected DatagramSocketManager(String s, OutputStream outputstream, InetAddress inetaddress, String s1, int i, Layout<? extends Serializable> layout) {
        super(s, outputstream, inetaddress, s1, i, layout);
    }

    public static DatagramSocketManager getSocketManager(String s, int i, Layout<? extends Serializable> layout) {
        if (Strings.isEmpty(s)) {
            throw new IllegalArgumentException("A host name is required");
        } else if (i <= 0) {
            throw new IllegalArgumentException("A port value is required");
        } else {
            return (DatagramSocketManager) getManager("UDP:" + s + ":" + i, (Object) (new DatagramSocketManager.FactoryData(s, i, layout)), (ManagerFactory) DatagramSocketManager.FACTORY);
        }
    }

    @Override
    public Map<String, String> getContentFormat() {
        HashMap hashmap = new HashMap(super.getContentFormat());

        hashmap.put("protocol", "udp");
        hashmap.put("direction", "out");
        return hashmap;
    }

    private static class DatagramSocketManagerFactory implements ManagerFactory<DatagramSocketManager, DatagramSocketManager.FactoryData> {

        private DatagramSocketManagerFactory() {}

        public DatagramSocketManager createManager(String s, DatagramSocketManager.FactoryData datagramsocketmanager_factorydata) {
            DatagramOutputStream datagramoutputstream = new DatagramOutputStream(datagramsocketmanager_factorydata.host, datagramsocketmanager_factorydata.port, datagramsocketmanager_factorydata.layout.getHeader(), datagramsocketmanager_factorydata.layout.getFooter());

            InetAddress inetaddress;

            try {
                inetaddress = InetAddress.getByName(datagramsocketmanager_factorydata.host);
            } catch (UnknownHostException unknownhostexception) {
                DatagramSocketManager.LOGGER.error("Could not find address of " + datagramsocketmanager_factorydata.host, (Throwable) unknownhostexception);
                return null;
            }

            return new DatagramSocketManager(s, datagramoutputstream, inetaddress, datagramsocketmanager_factorydata.host, datagramsocketmanager_factorydata.port, datagramsocketmanager_factorydata.layout);
        }
    }

    private static class FactoryData {

        private final String host;
        private final int port;
        private final Layout<? extends Serializable> layout;

        public FactoryData(String s, int i, Layout<? extends Serializable> layout) {
            this.host = s;
            this.port = i;
            this.layout = layout;
        }
    }
}
