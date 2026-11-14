package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.HttpRequest;
import java.nio.charset.Charset;

public interface HttpDataFactory {

    Attribute createAttribute(HttpRequest httprequest, String s);

    Attribute createAttribute(HttpRequest httprequest, String s, String s1);

    FileUpload createFileUpload(HttpRequest httprequest, String s, String s1, String s2, String s3, Charset charset, long i);

    void removeHttpDataFromClean(HttpRequest httprequest, InterfaceHttpData interfacehttpdata);

    void cleanRequestHttpDatas(HttpRequest httprequest);

    void cleanAllHttpDatas();
}
