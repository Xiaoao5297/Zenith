package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.net.InetAddress;
import java.text.ParseException;
import javax.annotation.Nullable;

@Beta
public final class HostSpecifier {

    private final String canonicalForm;

    private HostSpecifier(String s) {
        this.canonicalForm = s;
    }

    public static HostSpecifier fromValid(String s) {
        HostAndPort hostandport = HostAndPort.fromString(s);

        Preconditions.checkArgument(!hostandport.hasPort());
        String s1 = hostandport.getHostText();
        InetAddress inetaddress = null;

        try {
            inetaddress = InetAddresses.forString(s1);
        } catch (IllegalArgumentException illegalargumentexception) {
            ;
        }

        if (inetaddress != null) {
            return new HostSpecifier(InetAddresses.toUriString(inetaddress));
        } else {
            InternetDomainName internetdomainname = InternetDomainName.from(s1);

            if (internetdomainname.hasPublicSuffix()) {
                return new HostSpecifier(internetdomainname.toString());
            } else {
                throw new IllegalArgumentException("Domain name does not have a recognized public suffix: " + s1);
            }
        }
    }

    public static HostSpecifier from(String s) throws ParseException {
        try {
            return fromValid(s);
        } catch (IllegalArgumentException illegalargumentexception) {
            ParseException parseexception = new ParseException("Invalid host specifier: " + s, 0);

            parseexception.initCause(illegalargumentexception);
            throw parseexception;
        }
    }

    public static boolean isValid(String s) {
        try {
            fromValid(s);
            return true;
        } catch (IllegalArgumentException illegalargumentexception) {
            return false;
        }
    }

    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof HostSpecifier) {
            HostSpecifier hostspecifier = (HostSpecifier) object;

            return this.canonicalForm.equals(hostspecifier.canonicalForm);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.canonicalForm.hashCode();
    }

    public String toString() {
        return this.canonicalForm;
    }
}
