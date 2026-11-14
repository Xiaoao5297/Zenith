package io.netty.handler.codec.http.multipart;

import java.io.IOException;

public interface Attribute extends HttpData {

    String getValue() throws IOException;

    void setValue(String s) throws IOException;

    @Override
    Attribute copy();

    @Override
    Attribute duplicate();

    @Override
    Attribute retain();

    @Override
    Attribute retain(int i);
}
