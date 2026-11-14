package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class DefaultHttpDataFactory implements HttpDataFactory {

    public static final long MINSIZE = 16384L;
    private final boolean useDisk;
    private final boolean checkSize;
    private long minSize;
    private final ConcurrentMap<HttpRequest, List<HttpData>> requestFileDeleteMap = PlatformDependent.newConcurrentHashMap();

    public DefaultHttpDataFactory() {
        this.useDisk = false;
        this.checkSize = true;
        this.minSize = 16384L;
    }

    public DefaultHttpDataFactory(boolean flag) {
        this.useDisk = flag;
        this.checkSize = false;
    }

    public DefaultHttpDataFactory(long i) {
        this.useDisk = false;
        this.checkSize = true;
        this.minSize = i;
    }

    private List<HttpData> getList(HttpRequest httprequest) {
        Object object = (List) this.requestFileDeleteMap.get(httprequest);

        if (object == null) {
            object = new ArrayList();
            this.requestFileDeleteMap.put(httprequest, object);
        }

        return (List) object;
    }

    @Override
    public Attribute createAttribute(HttpRequest httprequest, String s) {
        List list;

        if (this.useDisk) {
            DiskAttribute diskattribute = new DiskAttribute(s);

            list = this.getList(httprequest);
            list.add(diskattribute);
            return diskattribute;
        } else if (this.checkSize) {
            MixedAttribute mixedattribute = new MixedAttribute(s, this.minSize);

            list = this.getList(httprequest);
            list.add(mixedattribute);
            return mixedattribute;
        } else {
            return new MemoryAttribute(s);
        }
    }

    @Override
    public Attribute createAttribute(HttpRequest httprequest, String s, String s1) {
        List list;

        if (this.useDisk) {
            Object object;

            try {
                object = new DiskAttribute(s, s1);
            } catch (IOException ioexception) {
                object = new MixedAttribute(s, s1, this.minSize);
            }

            list = this.getList(httprequest);
            list.add(object);
            return (Attribute) object;
        } else if (this.checkSize) {
            MixedAttribute mixedattribute = new MixedAttribute(s, s1, this.minSize);

            list = this.getList(httprequest);
            list.add(mixedattribute);
            return mixedattribute;
        } else {
            try {
                return new MemoryAttribute(s, s1);
            } catch (IOException ioexception1) {
                throw new IllegalArgumentException(ioexception1);
            }
        }
    }

    @Override
    public FileUpload createFileUpload(HttpRequest httprequest, String s, String s1, String s2, String s3, Charset charset, long i) {
        List list;

        if (this.useDisk) {
            DiskFileUpload diskfileupload = new DiskFileUpload(s, s1, s2, s3, charset, i);

            list = this.getList(httprequest);
            list.add(diskfileupload);
            return diskfileupload;
        } else if (this.checkSize) {
            MixedFileUpload mixedfileupload = new MixedFileUpload(s, s1, s2, s3, charset, i, this.minSize);

            list = this.getList(httprequest);
            list.add(mixedfileupload);
            return mixedfileupload;
        } else {
            return new MemoryFileUpload(s, s1, s2, s3, charset, i);
        }
    }

    @Override
    public void removeHttpDataFromClean(HttpRequest httprequest, InterfaceHttpData interfacehttpdata) {
        if (interfacehttpdata instanceof HttpData) {
            List list = this.getList(httprequest);

            list.remove(interfacehttpdata);
        }

    }

    @Override
    public void cleanRequestHttpDatas(HttpRequest httprequest) {
        List list = (List) this.requestFileDeleteMap.remove(httprequest);

        if (list != null) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                HttpData httpdata = (HttpData) iterator.next();

                httpdata.delete();
            }

            list.clear();
        }

    }

    @Override
    public void cleanAllHttpDatas() {
        HttpRequest httprequest;

        for (Iterator iterator = this.requestFileDeleteMap.keySet().iterator(); iterator.hasNext(); this.requestFileDeleteMap.remove(httprequest)) {
            httprequest = (HttpRequest) iterator.next();
            List list = (List) this.requestFileDeleteMap.get(httprequest);

            if (list != null) {
                Iterator iterator1 = list.iterator();

                while (iterator1.hasNext()) {
                    HttpData httpdata = (HttpData) iterator1.next();

                    httpdata.delete();
                }

                list.clear();
            }
        }

    }
}
