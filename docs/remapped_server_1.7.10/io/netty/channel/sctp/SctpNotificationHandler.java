package io.netty.channel.sctp;

import com.sun.nio.sctp.AbstractNotificationHandler;
import com.sun.nio.sctp.AssociationChangeNotification;
import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.PeerAddressChangeNotification;
import com.sun.nio.sctp.SendFailedNotification;
import com.sun.nio.sctp.ShutdownNotification;

public final class SctpNotificationHandler extends AbstractNotificationHandler<Object> {

    private final SctpChannel sctpChannel;

    public SctpNotificationHandler(SctpChannel sctpchannel) {
        if (sctpchannel == null) {
            throw new NullPointerException("sctpChannel");
        } else {
            this.sctpChannel = sctpchannel;
        }
    }

    public HandlerResult handleNotification(AssociationChangeNotification associationchangenotification, Object object) {
        this.fireEvent(associationchangenotification);
        return HandlerResult.CONTINUE;
    }

    public HandlerResult handleNotification(PeerAddressChangeNotification peeraddresschangenotification, Object object) {
        this.fireEvent(peeraddresschangenotification);
        return HandlerResult.CONTINUE;
    }

    public HandlerResult handleNotification(SendFailedNotification sendfailednotification, Object object) {
        this.fireEvent(sendfailednotification);
        return HandlerResult.CONTINUE;
    }

    public HandlerResult handleNotification(ShutdownNotification shutdownnotification, Object object) {
        this.fireEvent(shutdownnotification);
        this.sctpChannel.close();
        return HandlerResult.RETURN;
    }

    private void fireEvent(Notification notification) {
        this.sctpChannel.pipeline().fireUserEventTriggered(notification);
    }
}
