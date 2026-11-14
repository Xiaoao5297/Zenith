package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class HttpPostRequestEncoder implements ChunkedInput<HttpContent> {

    private static final Map<Pattern, String> percentEncodings = new HashMap();
    private final HttpDataFactory factory;
    private final HttpRequest request;
    private final Charset charset;
    private boolean isChunked;
    private final List<InterfaceHttpData> bodyListDatas;
    private final List<InterfaceHttpData> multipartHttpDatas;
    private final boolean isMultipart;
    private String multipartDataBoundary;
    private String multipartMixedBoundary;
    private boolean headerFinalized;
    private final HttpPostRequestEncoder.EncoderMode encoderMode;
    private boolean isLastChunk;
    private boolean isLastChunkSent;
    private FileUpload currentFileUpload;
    private boolean duringMixedMode;
    private long globalBodySize;
    private ListIterator<InterfaceHttpData> iterator;
    private ByteBuf currentBuffer;
    private InterfaceHttpData currentData;
    private boolean isKey;

    public HttpPostRequestEncoder(HttpRequest httprequest, boolean flag) throws HttpPostRequestEncoder.ErrorDataEncoderException {
        this(new DefaultHttpDataFactory(16384L), httprequest, flag, HttpConstants.DEFAULT_CHARSET, HttpPostRequestEncoder.EncoderMode.RFC1738);
    }

    public HttpPostRequestEncoder(HttpDataFactory httpdatafactory, HttpRequest httprequest, boolean flag) throws HttpPostRequestEncoder.ErrorDataEncoderException {
        this(httpdatafactory, httprequest, flag, HttpConstants.DEFAULT_CHARSET, HttpPostRequestEncoder.EncoderMode.RFC1738);
    }

    public HttpPostRequestEncoder(HttpDataFactory httpdatafactory, HttpRequest httprequest, boolean flag, Charset charset, HttpPostRequestEncoder.EncoderMode httppostrequestencoder_encodermode) throws HttpPostRequestEncoder.ErrorDataEncoderException {
        this.isKey = true;
        if (httpdatafactory == null) {
            throw new NullPointerException("factory");
        } else if (httprequest == null) {
            throw new NullPointerException("request");
        } else if (charset == null) {
            throw new NullPointerException("charset");
        } else if (httprequest.getMethod() != HttpMethod.POST) {
            throw new HttpPostRequestEncoder.ErrorDataEncoderException("Cannot create a Encoder if not a POST");
        } else {
            this.request = httprequest;
            this.charset = charset;
            this.factory = httpdatafactory;
            this.bodyListDatas = new ArrayList();
            this.isLastChunk = false;
            this.isLastChunkSent = false;
            this.isMultipart = flag;
            this.multipartHttpDatas = new ArrayList();
            this.encoderMode = httppostrequestencoder_encodermode;
            if (this.isMultipart) {
                this.initDataMultipart();
            }

        }
    }

    public void cleanFiles() {
        this.factory.cleanRequestHttpDatas(this.request);
    }

    public boolean isMultipart() {
        return this.isMultipart;
    }

    private void initDataMultipart() {
        this.multipartDataBoundary = getNewMultipartDelimiter();
    }

    private void initMixedMultipart() {
        this.multipartMixedBoundary = getNewMultipartDelimiter();
    }

    private static String getNewMultipartDelimiter() {
        return Long.toHexString(ThreadLocalRandom.current().nextLong()).toLowerCase();
    }

    public List<InterfaceHttpData> getBodyListAttributes() {
        return this.bodyListDatas;
    }

    public void setBodyHttpDatas(List<InterfaceHttpData> list) throws HttpPostRequestEncoder.ErrorDataEncoderException {
        if (list == null) {
            throw new NullPointerException("datas");
        } else {
            this.globalBodySize = 0L;
            this.bodyListDatas.clear();
            this.currentFileUpload = null;
            this.duringMixedMode = false;
            this.multipartHttpDatas.clear();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                InterfaceHttpData interfacehttpdata = (InterfaceHttpData) iterator.next();

                this.addBodyHttpData(interfacehttpdata);
            }

        }
    }

    public void addBodyAttribute(String s, String s1) throws HttpPostRequestEncoder.ErrorDataEncoderException {
        if (s == null) {
            throw new NullPointerException("name");
        } else {
            String s2 = s1;

            if (s1 == null) {
                s2 = "";
            }

            Attribute attribute = this.factory.createAttribute(this.request, s, s2);

            this.addBodyHttpData(attribute);
        }
    }

    public void addBodyFileUpload(String s, File file, String s1, boolean flag) throws HttpPostRequestEncoder.ErrorDataEncoderException {
        if (s == null) {
            throw new NullPointerException("name");
        } else if (file == null) {
            throw new NullPointerException("file");
        } else {
            String s2 = s1;
            String s3 = null;

            if (s1 == null) {
                if (flag) {
                    s2 = "text/plain";
                } else {
                    s2 = "application/octet-stream";
                }
            }

            if (!flag) {
                s3 = HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value();
            }

            FileUpload fileupload = this.factory.createFileUpload(this.request, s, file.getName(), s2, s3, (Charset) null, file.length());

            try {
                fileupload.setContent(file);
            } catch (IOException ioexception) {
                throw new HttpPostRequestEncoder.ErrorDataEncoderException(ioexception);
            }

            this.addBodyHttpData(fileupload);
        }
    }

    public void addBodyFileUploads(String s, File[] afile, String[] astring, boolean[] aboolean) throws HttpPostRequestEncoder.ErrorDataEncoderException {
        if (afile.length != astring.length && afile.length != aboolean.length) {
            throw new NullPointerException("Different array length");
        } else {
            for (int i = 0; i < afile.length; ++i) {
                this.addBodyFileUpload(s, afile[i], astring[i], aboolean[i]);
            }

        }
    }

    public void addBodyHttpData(InterfaceHttpData interfacehttpdata) throws HttpPostRequestEncoder.ErrorDataEncoderException {
        if (this.headerFinalized) {
            throw new HttpPostRequestEncoder.ErrorDataEncoderException("Cannot add value once finalized");
        } else if (interfacehttpdata == null) {
            throw new NullPointerException("data");
        } else {
            this.bodyListDatas.add(interfacehttpdata);
            FileUpload fileupload;

            if (!this.isMultipart) {
                String s;
                String s1;
                Attribute attribute;

                if (interfacehttpdata instanceof Attribute) {
                    Attribute attribute1 = (Attribute) interfacehttpdata;

                    try {
                        s = this.encodeAttribute(attribute1.getName(), this.charset);
                        s1 = this.encodeAttribute(attribute1.getValue(), this.charset);
                        attribute = this.factory.createAttribute(this.request, s, s1);
                        this.multipartHttpDatas.add(attribute);
                        this.globalBodySize += (long) (attribute.getName().length() + 1) + attribute.length() + 1L;
                    } catch (IOException ioexception) {
                        throw new HttpPostRequestEncoder.ErrorDataEncoderException(ioexception);
                    }
                } else if (interfacehttpdata instanceof FileUpload) {
                    fileupload = (FileUpload) interfacehttpdata;
                    s = this.encodeAttribute(fileupload.getName(), this.charset);
                    s1 = this.encodeAttribute(fileupload.getFilename(), this.charset);
                    attribute = this.factory.createAttribute(this.request, s, s1);
                    this.multipartHttpDatas.add(attribute);
                    this.globalBodySize += (long) (attribute.getName().length() + 1) + attribute.length() + 1L;
                }

            } else {
                if (interfacehttpdata instanceof Attribute) {
                    InternalAttribute internalattribute;

                    if (this.duringMixedMode) {
                        internalattribute = new InternalAttribute(this.charset);
                        internalattribute.addValue("\r\n--" + this.multipartMixedBoundary + "--");
                        this.multipartHttpDatas.add(internalattribute);
                        this.multipartMixedBoundary = null;
                        this.currentFileUpload = null;
                        this.duringMixedMode = false;
                    }

                    internalattribute = new InternalAttribute(this.charset);
                    if (!this.multipartHttpDatas.isEmpty()) {
                        internalattribute.addValue("\r\n");
                    }

                    internalattribute.addValue("--" + this.multipartDataBoundary + "\r\n");
                    Attribute attribute2 = (Attribute) interfacehttpdata;

                    internalattribute.addValue("Content-Disposition: form-data; name=\"" + attribute2.getName() + "\"\r\n");
                    Charset charset = attribute2.getCharset();

                    if (charset != null) {
                        internalattribute.addValue("Content-Type: text/plain; charset=" + charset + "\r\n");
                    }

                    internalattribute.addValue("\r\n");
                    this.multipartHttpDatas.add(internalattribute);
                    this.multipartHttpDatas.add(interfacehttpdata);
                    this.globalBodySize += attribute2.length() + (long) internalattribute.size();
                } else if (interfacehttpdata instanceof FileUpload) {
                    fileupload = (FileUpload) interfacehttpdata;
                    InternalAttribute internalattribute1 = new InternalAttribute(this.charset);

                    if (!this.multipartHttpDatas.isEmpty()) {
                        internalattribute1.addValue("\r\n");
                    }

                    boolean flag;

                    if (this.duringMixedMode) {
                        if (this.currentFileUpload != null && this.currentFileUpload.getName().equals(fileupload.getName())) {
                            flag = true;
                        } else {
                            internalattribute1.addValue("--" + this.multipartMixedBoundary + "--");
                            this.multipartHttpDatas.add(internalattribute1);
                            this.multipartMixedBoundary = null;
                            internalattribute1 = new InternalAttribute(this.charset);
                            internalattribute1.addValue("\r\n");
                            flag = false;
                            this.currentFileUpload = fileupload;
                            this.duringMixedMode = false;
                        }
                    } else if (this.currentFileUpload != null && this.currentFileUpload.getName().equals(fileupload.getName())) {
                        this.initMixedMultipart();
                        InternalAttribute internalattribute2 = (InternalAttribute) this.multipartHttpDatas.get(this.multipartHttpDatas.size() - 2);

                        this.globalBodySize -= (long) internalattribute2.size();
                        String s2 = "Content-Disposition: form-data; name=\"" + fileupload.getName() + "\"\r\n";

                        s2 = s2 + "Content-Type: multipart/mixed; boundary=" + this.multipartMixedBoundary + "\r\n\r\n";
                        s2 = s2 + "--" + this.multipartMixedBoundary + "\r\n";
                        s2 = s2 + "Content-Disposition: file; filename=\"" + fileupload.getFilename() + "\"\r\n";
                        internalattribute2.setValue(s2, 1);
                        this.globalBodySize += (long) internalattribute2.size();
                        flag = true;
                        this.duringMixedMode = true;
                    } else {
                        flag = false;
                        this.currentFileUpload = fileupload;
                        this.duringMixedMode = false;
                    }

                    if (flag) {
                        internalattribute1.addValue("--" + this.multipartMixedBoundary + "\r\n");
                        internalattribute1.addValue("Content-Disposition: file; filename=\"" + fileupload.getFilename() + "\"\r\n");
                    } else {
                        internalattribute1.addValue("--" + this.multipartDataBoundary + "\r\n");
                        internalattribute1.addValue("Content-Disposition: form-data; name=\"" + fileupload.getName() + "\"; " + "filename" + "=\"" + fileupload.getFilename() + "\"\r\n");
                    }

                    internalattribute1.addValue("Content-Type: " + fileupload.getContentType());
                    String s3 = fileupload.getContentTransferEncoding();

                    if (s3 != null && s3.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
                        internalattribute1.addValue("\r\nContent-Transfer-Encoding: " + HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value() + "\r\n\r\n");
                    } else if (fileupload.getCharset() != null) {
                        internalattribute1.addValue("; charset=" + fileupload.getCharset() + "\r\n\r\n");
                    } else {
                        internalattribute1.addValue("\r\n\r\n");
                    }

                    this.multipartHttpDatas.add(internalattribute1);
                    this.multipartHttpDatas.add(interfacehttpdata);
                    this.globalBodySize += fileupload.length() + (long) internalattribute1.size();
                }

            }
        }
    }

    public HttpRequest finalizeRequest() throws HttpPostRequestEncoder.ErrorDataEncoderException {
        if (!this.headerFinalized) {
            if (this.isMultipart) {
                InternalAttribute internalattribute = new InternalAttribute(this.charset);

                if (this.duringMixedMode) {
                    internalattribute.addValue("\r\n--" + this.multipartMixedBoundary + "--");
                }

                internalattribute.addValue("\r\n--" + this.multipartDataBoundary + "--\r\n");
                this.multipartHttpDatas.add(internalattribute);
                this.multipartMixedBoundary = null;
                this.currentFileUpload = null;
                this.duringMixedMode = false;
                this.globalBodySize += (long) internalattribute.size();
            }

            this.headerFinalized = true;
            HttpHeaders httpheaders = this.request.headers();
            List list = httpheaders.getAll("Content-Type");
            List list1 = httpheaders.getAll("Transfer-Encoding");

            if (list != null) {
                httpheaders.remove("Content-Type");
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();

                    if (!s.toLowerCase().startsWith("multipart/form-data") && !s.toLowerCase().startsWith("application/x-www-form-urlencoded")) {
                        httpheaders.add("Content-Type", (Object) s);
                    }
                }
            }

            if (this.isMultipart) {
                String s1 = "multipart/form-data; boundary=" + this.multipartDataBoundary;

                httpheaders.add("Content-Type", (Object) s1);
            } else {
                httpheaders.add("Content-Type", (Object) "application/x-www-form-urlencoded");
            }

            long i = this.globalBodySize;

            if (this.isMultipart) {
                this.iterator = this.multipartHttpDatas.listIterator();
            } else {
                --i;
                this.iterator = this.multipartHttpDatas.listIterator();
            }

            httpheaders.set("Content-Length", (Object) String.valueOf(i));
            if (i <= 8096L && !this.isMultipart) {
                HttpContent httpcontent = this.nextChunk();

                if (this.request instanceof FullHttpRequest) {
                    FullHttpRequest fullhttprequest = (FullHttpRequest) this.request;
                    ByteBuf bytebuf = httpcontent.content();

                    if (fullhttprequest.content() != bytebuf) {
                        fullhttprequest.content().clear().writeBytes(bytebuf);
                        bytebuf.release();
                    }

                    return fullhttprequest;
                } else {
                    return new HttpPostRequestEncoder.WrappedFullHttpRequest(this.request, httpcontent);
                }
            } else {
                this.isChunked = true;
                if (list1 != null) {
                    httpheaders.remove("Transfer-Encoding");
                    Iterator iterator1 = list1.iterator();

                    while (iterator1.hasNext()) {
                        String s2 = (String) iterator1.next();

                        if (!s2.equalsIgnoreCase("chunked")) {
                            httpheaders.add("Transfer-Encoding", (Object) s2);
                        }
                    }
                }

                HttpHeaders.setTransferEncodingChunked(this.request);
                return new HttpPostRequestEncoder.WrappedHttpRequest(this.request);
            }
        } else {
            throw new HttpPostRequestEncoder.ErrorDataEncoderException("Header already encoded");
        }
    }

    public boolean isChunked() {
        return this.isChunked;
    }

    private String encodeAttribute(String s, Charset charset) throws HttpPostRequestEncoder.ErrorDataEncoderException {
        if (s == null) {
            return "";
        } else {
            try {
                String s1 = URLEncoder.encode(s, charset.name());
                Entry entry;
                String s2;

                if (this.encoderMode == HttpPostRequestEncoder.EncoderMode.RFC3986) {
                    for (Iterator iterator = HttpPostRequestEncoder.percentEncodings.entrySet().iterator(); iterator.hasNext(); s1 = ((Pattern) entry.getKey()).matcher(s1).replaceAll(s2)) {
                        entry = (Entry) iterator.next();
                        s2 = (String) entry.getValue();
                    }
                }

                return s1;
            } catch (UnsupportedEncodingException unsupportedencodingexception) {
                throw new HttpPostRequestEncoder.ErrorDataEncoderException(charset.name(), unsupportedencodingexception);
            }
        }
    }

    private ByteBuf fillByteBuf() {
        int i = this.currentBuffer.readableBytes();
        ByteBuf bytebuf;

        if (i > 8096) {
            bytebuf = this.currentBuffer.slice(this.currentBuffer.readerIndex(), 8096);
            this.currentBuffer.skipBytes(8096);
            return bytebuf;
        } else {
            bytebuf = this.currentBuffer;
            this.currentBuffer = null;
            return bytebuf;
        }
    }

    private HttpContent encodeNextChunkMultipart(int i) throws HttpPostRequestEncoder.ErrorDataEncoderException {
        if (this.currentData == null) {
            return null;
        } else {
            ByteBuf bytebuf;

            if (this.currentData instanceof InternalAttribute) {
                bytebuf = ((InternalAttribute) this.currentData).toByteBuf();
                this.currentData = null;
            } else {
                if (this.currentData instanceof Attribute) {
                    try {
                        bytebuf = ((Attribute) this.currentData).getChunk(i);
                    } catch (IOException ioexception) {
                        throw new HttpPostRequestEncoder.ErrorDataEncoderException(ioexception);
                    }
                } else {
                    try {
                        bytebuf = ((HttpData) this.currentData).getChunk(i);
                    } catch (IOException ioexception1) {
                        throw new HttpPostRequestEncoder.ErrorDataEncoderException(ioexception1);
                    }
                }

                if (bytebuf.capacity() == 0) {
                    this.currentData = null;
                    return null;
                }
            }

            if (this.currentBuffer == null) {
                this.currentBuffer = bytebuf;
            } else {
                this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, bytebuf);
            }

            if (this.currentBuffer.readableBytes() < 8096) {
                this.currentData = null;
                return null;
            } else {
                bytebuf = this.fillByteBuf();
                return new DefaultHttpContent(bytebuf);
            }
        }
    }

    private HttpContent encodeNextChunkUrlEncoded(int i) throws HttpPostRequestEncoder.ErrorDataEncoderException {
        if (this.currentData == null) {
            return null;
        } else {
            int j = i;
            ByteBuf bytebuf;

            if (this.isKey) {
                String s = this.currentData.getName();

                bytebuf = Unpooled.wrappedBuffer(s.getBytes());
                this.isKey = false;
                if (this.currentBuffer == null) {
                    this.currentBuffer = Unpooled.wrappedBuffer(bytebuf, Unpooled.wrappedBuffer("=".getBytes()));
                    j = i - (bytebuf.readableBytes() + 1);
                } else {
                    this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, bytebuf, Unpooled.wrappedBuffer("=".getBytes()));
                    j = i - (bytebuf.readableBytes() + 1);
                }

                if (this.currentBuffer.readableBytes() >= 8096) {
                    bytebuf = this.fillByteBuf();
                    return new DefaultHttpContent(bytebuf);
                }
            }

            try {
                bytebuf = ((HttpData) this.currentData).getChunk(j);
            } catch (IOException ioexception) {
                throw new HttpPostRequestEncoder.ErrorDataEncoderException(ioexception);
            }

            ByteBuf bytebuf1 = null;

            if (bytebuf.readableBytes() < j) {
                this.isKey = true;
                bytebuf1 = this.iterator.hasNext() ? Unpooled.wrappedBuffer("&".getBytes()) : null;
            }

            if (bytebuf.capacity() == 0) {
                this.currentData = null;
                if (this.currentBuffer == null) {
                    this.currentBuffer = bytebuf1;
                } else if (bytebuf1 != null) {
                    this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, bytebuf1);
                }

                if (this.currentBuffer.readableBytes() >= 8096) {
                    bytebuf = this.fillByteBuf();
                    return new DefaultHttpContent(bytebuf);
                } else {
                    return null;
                }
            } else {
                if (this.currentBuffer == null) {
                    if (bytebuf1 != null) {
                        this.currentBuffer = Unpooled.wrappedBuffer(bytebuf, bytebuf1);
                    } else {
                        this.currentBuffer = bytebuf;
                    }
                } else if (bytebuf1 != null) {
                    this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, bytebuf, bytebuf1);
                } else {
                    this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, bytebuf);
                }

                if (this.currentBuffer.readableBytes() < 8096) {
                    this.currentData = null;
                    this.isKey = true;
                    return null;
                } else {
                    bytebuf = this.fillByteBuf();
                    return new DefaultHttpContent(bytebuf);
                }
            }
        }
    }

    @Override
    public void close() throws Exception {}

    @Override
    public HttpContent readChunk(ChannelHandlerContext channelhandlercontext) throws Exception {
        return this.isLastChunkSent ? null : this.nextChunk();
    }

    private HttpContent nextChunk() throws HttpPostRequestEncoder.ErrorDataEncoderException {
        if (this.isLastChunk) {
            this.isLastChunkSent = true;
            return LastHttpContent.EMPTY_LAST_CONTENT;
        } else {
            int i = 8096;

            if (this.currentBuffer != null) {
                i -= this.currentBuffer.readableBytes();
            }

            ByteBuf bytebuf;

            if (i <= 0) {
                bytebuf = this.fillByteBuf();
                return new DefaultHttpContent(bytebuf);
            } else {
                HttpContent httpcontent;

                if (this.currentData != null) {
                    if (this.isMultipart) {
                        httpcontent = this.encodeNextChunkMultipart(i);
                        if (httpcontent != null) {
                            return httpcontent;
                        }
                    } else {
                        httpcontent = this.encodeNextChunkUrlEncoded(i);
                        if (httpcontent != null) {
                            return httpcontent;
                        }
                    }

                    i = 8096 - this.currentBuffer.readableBytes();
                }

                if (!this.iterator.hasNext()) {
                    this.isLastChunk = true;
                    bytebuf = this.currentBuffer;
                    this.currentBuffer = null;
                    return new DefaultHttpContent(bytebuf);
                } else {
                    while (i > 0 && this.iterator.hasNext()) {
                        this.currentData = (InterfaceHttpData) this.iterator.next();
                        if (this.isMultipart) {
                            httpcontent = this.encodeNextChunkMultipart(i);
                        } else {
                            httpcontent = this.encodeNextChunkUrlEncoded(i);
                        }

                        if (httpcontent != null) {
                            return httpcontent;
                        }

                        i = 8096 - this.currentBuffer.readableBytes();
                    }

                    this.isLastChunk = true;
                    if (this.currentBuffer == null) {
                        this.isLastChunkSent = true;
                        return LastHttpContent.EMPTY_LAST_CONTENT;
                    } else {
                        bytebuf = this.currentBuffer;
                        this.currentBuffer = null;
                        return new DefaultHttpContent(bytebuf);
                    }
                }
            }
        }
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        return this.isLastChunkSent;
    }

    static {
        HttpPostRequestEncoder.percentEncodings.put(Pattern.compile("\\*"), "%2A");
        HttpPostRequestEncoder.percentEncodings.put(Pattern.compile("\\+"), "%20");
        HttpPostRequestEncoder.percentEncodings.put(Pattern.compile("%7E"), "~");
    }

    private static final class WrappedFullHttpRequest extends HttpPostRequestEncoder.WrappedHttpRequest implements FullHttpRequest {

        private final HttpContent content;

        private WrappedFullHttpRequest(HttpRequest httprequest, HttpContent httpcontent) {
            super(httprequest);
            this.content = httpcontent;
        }

        @Override
        public FullHttpRequest setProtocolVersion(HttpVersion httpversion) {
            super.setProtocolVersion(httpversion);
            return this;
        }

        @Override
        public FullHttpRequest setMethod(HttpMethod httpmethod) {
            super.setMethod(httpmethod);
            return this;
        }

        @Override
        public FullHttpRequest setUri(String s) {
            super.setUri(s);
            return this;
        }

        @Override
        public FullHttpRequest copy() {
            DefaultFullHttpRequest defaultfullhttprequest = new DefaultFullHttpRequest(this.getProtocolVersion(), this.getMethod(), this.getUri(), this.content().copy());

            defaultfullhttprequest.headers().set(this.headers());
            defaultfullhttprequest.trailingHeaders().set(this.trailingHeaders());
            return defaultfullhttprequest;
        }

        @Override
        public FullHttpRequest duplicate() {
            DefaultFullHttpRequest defaultfullhttprequest = new DefaultFullHttpRequest(this.getProtocolVersion(), this.getMethod(), this.getUri(), this.content().duplicate());

            defaultfullhttprequest.headers().set(this.headers());
            defaultfullhttprequest.trailingHeaders().set(this.trailingHeaders());
            return defaultfullhttprequest;
        }

        @Override
        public FullHttpRequest retain(int i) {
            this.content.retain(i);
            return this;
        }

        @Override
        public FullHttpRequest retain() {
            this.content.retain();
            return this;
        }

        @Override
        public ByteBuf content() {
            return this.content.content();
        }

        @Override
        public HttpHeaders trailingHeaders() {
            return this.content instanceof LastHttpContent ? ((LastHttpContent) this.content).trailingHeaders() : HttpHeaders.EMPTY_HEADERS;
        }

        @Override
        public int refCnt() {
            return this.content.refCnt();
        }

        @Override
        public boolean release() {
            return this.content.release();
        }

        @Override
        public boolean release(int i) {
            return this.content.release(i);
        }
    }

    private static class WrappedHttpRequest implements HttpRequest {

        private final HttpRequest request;

        WrappedHttpRequest(HttpRequest httprequest) {
            this.request = httprequest;
        }

        @Override
        public HttpRequest setProtocolVersion(HttpVersion httpversion) {
            this.request.setProtocolVersion(httpversion);
            return this;
        }

        @Override
        public HttpRequest setMethod(HttpMethod httpmethod) {
            this.request.setMethod(httpmethod);
            return this;
        }

        @Override
        public HttpRequest setUri(String s) {
            this.request.setUri(s);
            return this;
        }

        @Override
        public HttpMethod getMethod() {
            return this.request.getMethod();
        }

        @Override
        public String getUri() {
            return this.request.getUri();
        }

        @Override
        public HttpVersion getProtocolVersion() {
            return this.request.getProtocolVersion();
        }

        @Override
        public HttpHeaders headers() {
            return this.request.headers();
        }

        @Override
        public DecoderResult getDecoderResult() {
            return this.request.getDecoderResult();
        }

        @Override
        public void setDecoderResult(DecoderResult decoderresult) {
            this.request.setDecoderResult(decoderresult);
        }
    }

    public static class ErrorDataEncoderException extends Exception {

        private static final long serialVersionUID = 5020247425493164465L;

        public ErrorDataEncoderException() {}

        public ErrorDataEncoderException(String s) {
            super(s);
        }

        public ErrorDataEncoderException(Throwable throwable) {
            super(throwable);
        }

        public ErrorDataEncoderException(String s, Throwable throwable) {
            super(s, throwable);
        }
    }

    public static enum EncoderMode {

        RFC1738, RFC3986;

        private EncoderMode() {}
    }
}
