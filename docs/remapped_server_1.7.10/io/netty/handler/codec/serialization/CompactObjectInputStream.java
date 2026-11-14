package io.netty.handler.codec.serialization;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;

class CompactObjectInputStream extends ObjectInputStream {

    private final ClassResolver classResolver;

    CompactObjectInputStream(InputStream inputstream, ClassResolver classresolver) throws IOException {
        super(inputstream);
        this.classResolver = classresolver;
    }

    protected void readStreamHeader() throws IOException {
        int i = this.readByte() & 255;

        if (i != 5) {
            throw new StreamCorruptedException("Unsupported version: " + i);
        }
    }

    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        int i = this.read();

        if (i < 0) {
            throw new EOFException();
        } else {
            switch (i) {
                case 0:
                    return super.readClassDescriptor();
                case 1:
                    String s = this.readUTF();
                    Class oclass = this.classResolver.resolve(s);

                    return ObjectStreamClass.lookupAny(oclass);
                default:
                    throw new StreamCorruptedException("Unexpected class descriptor type: " + i);
            }
        }
    }

    protected Class<?> resolveClass(ObjectStreamClass objectstreamclass) throws IOException, ClassNotFoundException {
        Class oclass;

        try {
            oclass = this.classResolver.resolve(objectstreamclass.getName());
        } catch (ClassNotFoundException classnotfoundexception) {
            oclass = super.resolveClass(objectstreamclass);
        }

        return oclass;
    }
}
