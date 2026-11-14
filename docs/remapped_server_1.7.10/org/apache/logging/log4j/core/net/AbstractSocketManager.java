package org.apache.logging.log4j.core.net;

import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.OutputStreamManager;

public abstract class AbstractSocketManager extends OutputStreamManager {

    protected final InetAddress address;
    protected final String host;
    protected final int port;

    public AbstractSocketManager(String s, OutputStream outputstream, InetAddress inetaddress, String s1, int i, Layout<? extends Serializable> layout) {
        super(outputstream, s, layout);
        this.address = inetaddress;
        this.host = s1;
        this.port = i;
    }

    @Override
    public Map<String, String> getContentFormat() {
        HashMap hashmap = new HashMap(super.getContentFormat());

        hashmap.put("port", Integer.toString(this.port));
        hashmap.put("address", this.address.getHostAddress());
        return hashmap;
    }
}
