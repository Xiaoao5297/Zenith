package com.mojang.authlib.properties;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import org.apache.commons.codec.binary.Base64;

public class Property {

    private final String name;
    private final String value;
    private final String signature;

    public Property(String s, String s1) {
        this(s, s1, (String) null);
    }

    public Property(String s, String s1, String s2) {
        this.name = s;
        this.value = s1;
        this.signature = s2;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public String getSignature() {
        return this.signature;
    }

    public boolean hasSignature() {
        return this.signature != null;
    }

    public boolean isSignatureValid(PublicKey publickey) {
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");

            signature.initVerify(publickey);
            signature.update(this.value.getBytes());
            return signature.verify(Base64.decodeBase64(this.signature));
        } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
        } catch (InvalidKeyException invalidkeyexception) {
            invalidkeyexception.printStackTrace();
        } catch (SignatureException signatureexception) {
            signatureexception.printStackTrace();
        }

        return false;
    }
}
