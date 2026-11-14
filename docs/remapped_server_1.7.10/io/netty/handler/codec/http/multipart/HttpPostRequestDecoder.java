package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpPostRequestDecoder {

    private static final int DEFAULT_DISCARD_THRESHOLD = 10485760;
    private final HttpDataFactory factory;
    private final HttpRequest request;
    private final Charset charset;
    private boolean bodyToDecode;
    private boolean isLastChunk;
    private final List<InterfaceHttpData> bodyListHttpData;
    private final Map<String, List<InterfaceHttpData>> bodyMapHttpData;
    private ByteBuf undecodedChunk;
    private boolean isMultipart;
    private int bodyListHttpDataRank;
    private String multipartDataBoundary;
    private String multipartMixedBoundary;
    private HttpPostRequestDecoder.MultiPartStatus currentStatus;
    private Map<String, Attribute> currentFieldAttributes;
    private FileUpload currentFileUpload;
    private Attribute currentAttribute;
    private boolean destroyed;
    private int discardThreshold;

    public HttpPostRequestDecoder(HttpRequest httprequest) throws HttpPostRequestDecoder.ErrorDataDecoderException, HttpPostRequestDecoder.IncompatibleDataDecoderException {
        this(new DefaultHttpDataFactory(16384L), httprequest, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostRequestDecoder(HttpDataFactory httpdatafactory, HttpRequest httprequest) throws HttpPostRequestDecoder.ErrorDataDecoderException, HttpPostRequestDecoder.IncompatibleDataDecoderException {
        this(httpdatafactory, httprequest, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostRequestDecoder(HttpDataFactory httpdatafactory, HttpRequest httprequest, Charset charset) throws HttpPostRequestDecoder.ErrorDataDecoderException, HttpPostRequestDecoder.IncompatibleDataDecoderException {
        this.bodyListHttpData = new ArrayList();
        this.bodyMapHttpData = new TreeMap(CaseIgnoringComparator.INSTANCE);
        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
        this.discardThreshold = 10485760;
        if (httpdatafactory == null) {
            throw new NullPointerException("factory");
        } else if (httprequest == null) {
            throw new NullPointerException("request");
        } else if (charset == null) {
            throw new NullPointerException("charset");
        } else {
            this.request = httprequest;
            HttpMethod httpmethod = httprequest.getMethod();

            if (httpmethod.equals(HttpMethod.POST) || httpmethod.equals(HttpMethod.PUT) || httpmethod.equals(HttpMethod.PATCH)) {
                this.bodyToDecode = true;
            }

            this.charset = charset;
            this.factory = httpdatafactory;
            String s = this.request.headers().get("Content-Type");

            if (s != null) {
                this.checkMultipart(s);
            } else {
                this.isMultipart = false;
            }

            if (!this.bodyToDecode) {
                throw new HttpPostRequestDecoder.IncompatibleDataDecoderException("No Body to decode");
            } else {
                if (httprequest instanceof HttpContent) {
                    this.offer((HttpContent) httprequest);
                } else {
                    this.undecodedChunk = Unpooled.buffer();
                    this.parseBody();
                }

            }
        }
    }

    private void checkMultipart(String s) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        String[] astring = splitHeaderContentType(s);

        if (astring[0].toLowerCase().startsWith("multipart/form-data") && astring[1].toLowerCase().startsWith("boundary")) {
            String[] astring1 = StringUtil.split(astring[1], '=');

            if (astring1.length != 2) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Needs a boundary value");
            }

            this.multipartDataBoundary = "--" + astring1[1];
            this.isMultipart = true;
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
        } else {
            this.isMultipart = false;
        }

    }

    private void checkDestroyed() {
        if (this.destroyed) {
            throw new IllegalStateException(HttpPostRequestDecoder.class.getSimpleName() + " was destroyed already");
        }
    }

    public boolean isMultipart() {
        this.checkDestroyed();
        return this.isMultipart;
    }

    public void setDiscardThreshold(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("discardThreshold must be >= 0");
        } else {
            this.discardThreshold = i;
        }
    }

    public int getDiscardThreshold() {
        return this.discardThreshold;
    }

    public List<InterfaceHttpData> getBodyHttpDatas() throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        this.checkDestroyed();
        if (!this.isLastChunk) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        } else {
            return this.bodyListHttpData;
        }
    }

    public List<InterfaceHttpData> getBodyHttpDatas(String s) throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        this.checkDestroyed();
        if (!this.isLastChunk) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        } else {
            return (List) this.bodyMapHttpData.get(s);
        }
    }

    public InterfaceHttpData getBodyHttpData(String s) throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        this.checkDestroyed();
        if (!this.isLastChunk) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        } else {
            List list = (List) this.bodyMapHttpData.get(s);

            return list != null ? (InterfaceHttpData) list.get(0) : null;
        }
    }

    public HttpPostRequestDecoder offer(HttpContent httpcontent) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        this.checkDestroyed();
        ByteBuf bytebuf = httpcontent.content();

        if (this.undecodedChunk == null) {
            this.undecodedChunk = bytebuf.copy();
        } else {
            this.undecodedChunk.writeBytes(bytebuf);
        }

        if (httpcontent instanceof LastHttpContent) {
            this.isLastChunk = true;
        }

        this.parseBody();
        if (this.undecodedChunk != null && this.undecodedChunk.writerIndex() > this.discardThreshold) {
            this.undecodedChunk.discardReadBytes();
        }

        return this;
    }

    public boolean hasNext() throws HttpPostRequestDecoder.EndOfDataDecoderException {
        this.checkDestroyed();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE && this.bodyListHttpDataRank >= this.bodyListHttpData.size()) {
            throw new HttpPostRequestDecoder.EndOfDataDecoderException();
        } else {
            return !this.bodyListHttpData.isEmpty() && this.bodyListHttpDataRank < this.bodyListHttpData.size();
        }
    }

    public InterfaceHttpData next() throws HttpPostRequestDecoder.EndOfDataDecoderException {
        this.checkDestroyed();
        return this.hasNext() ? (InterfaceHttpData) this.bodyListHttpData.get(this.bodyListHttpDataRank++) : null;
    }

    private void parseBody() throws HttpPostRequestDecoder.ErrorDataDecoderException {
        if (this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE && this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
            if (this.isMultipart) {
                this.parseBodyMultipart();
            } else {
                this.parseBodyAttributes();
            }

        } else {
            if (this.isLastChunk) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            }

        }
    }

    protected void addHttpData(InterfaceHttpData interfacehttpdata) {
        if (interfacehttpdata != null) {
            Object object = (List) this.bodyMapHttpData.get(interfacehttpdata.getName());

            if (object == null) {
                object = new ArrayList(1);
                this.bodyMapHttpData.put(interfacehttpdata.getName(), object);
            }

            ((List) object).add(interfacehttpdata);
            this.bodyListHttpData.add(interfacehttpdata);
        }
    }

    private void parseBodyAttributesStandard() throws HttpPostRequestDecoder.ErrorDataDecoderException {
        int i = this.undecodedChunk.readerIndex();
        int j = i;

        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
        }

        boolean flag = true;

        try {
            while (this.undecodedChunk.isReadable() && flag) {
                char c0 = (char) this.undecodedChunk.readUnsignedByte();

                ++j;
                int k;

                switch (this.currentStatus) {
                    case DISPOSITION:
                        String s;

                        if (c0 == '=') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                            int l = j - 1;

                            s = decodeAttribute(this.undecodedChunk.toString(i, l - i, this.charset), this.charset);
                            this.currentAttribute = this.factory.createAttribute(this.request, s);
                            i = j;
                        } else if (c0 == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            k = j - 1;
                            s = decodeAttribute(this.undecodedChunk.toString(i, k - i, this.charset), this.charset);
                            this.currentAttribute = this.factory.createAttribute(this.request, s);
                            this.currentAttribute.setValue("");
                            this.addHttpData(this.currentAttribute);
                            this.currentAttribute = null;
                            i = j;
                            flag = true;
                        }
                        break;
                    case FIELD:
                        if (c0 == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            k = j - 1;
                            this.setFinalBuffer(this.undecodedChunk.slice(i, k - i).retain());
                            i = j;
                            flag = true;
                        } else if (c0 == '\r') {
                            if (this.undecodedChunk.isReadable()) {
                                c0 = (char) this.undecodedChunk.readUnsignedByte();
                                ++j;
                                if (c0 != '\n') {
                                    throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
                                }

                                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                                k = j - 2;
                                this.setFinalBuffer(this.undecodedChunk.slice(i, k - i).retain());
                                i = j;
                                flag = false;
                            } else {
                                --j;
                            }
                        } else if (c0 == '\n') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                            k = j - 1;
                            this.setFinalBuffer(this.undecodedChunk.slice(i, k - i).retain());
                            i = j;
                            flag = false;
                        }
                        break;
                    default:
                        flag = false;
                }
            }

            if (this.isLastChunk && this.currentAttribute != null) {
                if (j > i) {
                    this.setFinalBuffer(this.undecodedChunk.slice(i, j - i).retain());
                } else if (!this.currentAttribute.isCompleted()) {
                    this.setFinalBuffer(Unpooled.EMPTY_BUFFER);
                }

                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            } else {
                if (flag && this.currentAttribute != null) {
                    if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
                        this.currentAttribute.addContent(this.undecodedChunk.slice(i, j - i).retain(), false);
                        i = j;
                    }

                    this.undecodedChunk.readerIndex(i);
                }

            }
        } catch (HttpPostRequestDecoder.ErrorDataDecoderException httppostrequestdecoder_errordatadecoderexception) {
            this.undecodedChunk.readerIndex(i);
            throw httppostrequestdecoder_errordatadecoderexception;
        } catch (IOException ioexception) {
            this.undecodedChunk.readerIndex(i);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception);
        }
    }

    private void parseBodyAttributes() throws HttpPostRequestDecoder.ErrorDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize httppostbodyutil_seekaheadoptimize;

        try {
            httppostbodyutil_seekaheadoptimize = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException httppostbodyutil_seekaheadnobackarrayexception) {
            this.parseBodyAttributesStandard();
            return;
        }

        int i = this.undecodedChunk.readerIndex();
        int j = i;

        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
        }

        boolean flag = true;

        try {
            label84:
            while (httppostbodyutil_seekaheadoptimize.pos < httppostbodyutil_seekaheadoptimize.limit) {
                char c0 = (char) (httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++] & 255);

                ++j;
                int k;

                switch (this.currentStatus) {
                    case DISPOSITION:
                        String s;

                        if (c0 == '=') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                            int l = j - 1;

                            s = decodeAttribute(this.undecodedChunk.toString(i, l - i, this.charset), this.charset);
                            this.currentAttribute = this.factory.createAttribute(this.request, s);
                            i = j;
                        } else if (c0 == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            k = j - 1;
                            s = decodeAttribute(this.undecodedChunk.toString(i, k - i, this.charset), this.charset);
                            this.currentAttribute = this.factory.createAttribute(this.request, s);
                            this.currentAttribute.setValue("");
                            this.addHttpData(this.currentAttribute);
                            this.currentAttribute = null;
                            i = j;
                            flag = true;
                        }
                        break;
                    case FIELD:
                        if (c0 == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            k = j - 1;
                            this.setFinalBuffer(this.undecodedChunk.slice(i, k - i).retain());
                            i = j;
                            flag = true;
                        } else if (c0 == '\r') {
                            if (httppostbodyutil_seekaheadoptimize.pos < httppostbodyutil_seekaheadoptimize.limit) {
                                c0 = (char) (httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++] & 255);
                                ++j;
                                if (c0 != '\n') {
                                    httppostbodyutil_seekaheadoptimize.setReadPosition(0);
                                    throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
                                }

                                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                                k = j - 2;
                                httppostbodyutil_seekaheadoptimize.setReadPosition(0);
                                this.setFinalBuffer(this.undecodedChunk.slice(i, k - i).retain());
                                i = j;
                                flag = false;
                                break label84;
                            }

                            if (httppostbodyutil_seekaheadoptimize.limit > 0) {
                                --j;
                            }
                        } else {
                            if (c0 != '\n') {
                                continue;
                            }

                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                            k = j - 1;
                            httppostbodyutil_seekaheadoptimize.setReadPosition(0);
                            this.setFinalBuffer(this.undecodedChunk.slice(i, k - i).retain());
                            i = j;
                            flag = false;
                            break label84;
                        }
                        break;
                    default:
                        httppostbodyutil_seekaheadoptimize.setReadPosition(0);
                        flag = false;
                        break label84;
                }
            }

            if (this.isLastChunk && this.currentAttribute != null) {
                if (j > i) {
                    this.setFinalBuffer(this.undecodedChunk.slice(i, j - i).retain());
                } else if (!this.currentAttribute.isCompleted()) {
                    this.setFinalBuffer(Unpooled.EMPTY_BUFFER);
                }

                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            } else {
                if (flag && this.currentAttribute != null) {
                    if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
                        this.currentAttribute.addContent(this.undecodedChunk.slice(i, j - i).retain(), false);
                        i = j;
                    }

                    this.undecodedChunk.readerIndex(i);
                }

            }
        } catch (HttpPostRequestDecoder.ErrorDataDecoderException httppostrequestdecoder_errordatadecoderexception) {
            this.undecodedChunk.readerIndex(i);
            throw httppostrequestdecoder_errordatadecoderexception;
        } catch (IOException ioexception) {
            this.undecodedChunk.readerIndex(i);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception);
        }
    }

    private void setFinalBuffer(ByteBuf bytebuf) throws HttpPostRequestDecoder.ErrorDataDecoderException, IOException {
        this.currentAttribute.addContent(bytebuf, true);
        String s = decodeAttribute(this.currentAttribute.getByteBuf().toString(this.charset), this.charset);

        this.currentAttribute.setValue(s);
        this.addHttpData(this.currentAttribute);
        this.currentAttribute = null;
    }

    private static String decodeAttribute(String s, Charset charset) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        if (s == null) {
            return "";
        } else {
            try {
                return URLDecoder.decode(s, charset.name());
            } catch (UnsupportedEncodingException unsupportedencodingexception) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(charset.toString(), unsupportedencodingexception);
            } catch (IllegalArgumentException illegalargumentexception) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad string: '" + s + '\'', illegalargumentexception);
            }
        }
    }

    private void parseBodyMultipart() throws HttpPostRequestDecoder.ErrorDataDecoderException {
        if (this.undecodedChunk != null && this.undecodedChunk.readableBytes() != 0) {
            for (InterfaceHttpData interfacehttpdata = this.decodeMultipart(this.currentStatus); interfacehttpdata != null; interfacehttpdata = this.decodeMultipart(this.currentStatus)) {
                this.addHttpData(interfacehttpdata);
                if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
                    break;
                }
            }

        }
    }

    private InterfaceHttpData decodeMultipart(HttpPostRequestDecoder.MultiPartStatus httppostrequestdecoder_multipartstatus) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        switch (httppostrequestdecoder_multipartstatus) {
            case DISPOSITION:
                return this.findMultipartDisposition();
            case FIELD:
                Charset charset = null;
                Attribute attribute = (Attribute) this.currentFieldAttributes.get("charset");

                if (attribute != null) {
                    try {
                        charset = Charset.forName(attribute.getValue());
                    } catch (IOException ioexception) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception);
                    }
                }

                Attribute attribute1 = (Attribute) this.currentFieldAttributes.get("name");

                if (this.currentAttribute == null) {
                    try {
                        this.currentAttribute = this.factory.createAttribute(this.request, cleanString(attribute1.getValue()));
                    } catch (NullPointerException nullpointerexception) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(nullpointerexception);
                    } catch (IllegalArgumentException illegalargumentexception) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(illegalargumentexception);
                    } catch (IOException ioexception1) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception1);
                    }

                    if (charset != null) {
                        this.currentAttribute.setCharset(charset);
                    }
                }

                try {
                    this.loadFieldMultipart(this.multipartDataBoundary);
                } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException httppostrequestdecoder_notenoughdatadecoderexception) {
                    return null;
                }

                Attribute attribute2 = this.currentAttribute;

                this.currentAttribute = null;
                this.currentFieldAttributes = null;
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
                return attribute2;
            case NOTSTARTED:
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
            case PREAMBLE:
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
            case HEADERDELIMITER:
                return this.findMultipartDelimiter(this.multipartDataBoundary, HttpPostRequestDecoder.MultiPartStatus.DISPOSITION, HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE);
            case FILEUPLOAD:
                return this.getFileUpload(this.multipartDataBoundary);
            case MIXEDDELIMITER:
                return this.findMultipartDelimiter(this.multipartMixedBoundary, HttpPostRequestDecoder.MultiPartStatus.MIXEDDISPOSITION, HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
            case MIXEDDISPOSITION:
                return this.findMultipartDisposition();
            case MIXEDFILEUPLOAD:
                return this.getFileUpload(this.multipartMixedBoundary);
            case PREEPILOGUE:
                return null;
            case EPILOGUE:
                return null;
            default:
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Shouldn't reach here.");
        }
    }

    void skipControlCharacters() throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize httppostbodyutil_seekaheadoptimize;

        try {
            httppostbodyutil_seekaheadoptimize = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException httppostbodyutil_seekaheadnobackarrayexception) {
            try {
                this.skipControlCharactersStandard();
                return;
            } catch (IndexOutOfBoundsException indexoutofboundsexception) {
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(indexoutofboundsexception);
            }
        }

        char c0;

        do {
            if (httppostbodyutil_seekaheadoptimize.pos >= httppostbodyutil_seekaheadoptimize.limit) {
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException("Access out of bounds");
            }

            c0 = (char) (httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++] & 255);
        } while (Character.isISOControl(c0) || Character.isWhitespace(c0));

        httppostbodyutil_seekaheadoptimize.setReadPosition(1);
    }

    void skipControlCharactersStandard() {
        char c0;

        do {
            c0 = (char) this.undecodedChunk.readUnsignedByte();
        } while (Character.isISOControl(c0) || Character.isWhitespace(c0));

        this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
    }

    private InterfaceHttpData findMultipartDelimiter(String s, HttpPostRequestDecoder.MultiPartStatus httppostrequestdecoder_multipartstatus, HttpPostRequestDecoder.MultiPartStatus httppostrequestdecoder_multipartstatus1) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        int i = this.undecodedChunk.readerIndex();

        try {
            this.skipControlCharacters();
        } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException httppostrequestdecoder_notenoughdatadecoderexception) {
            this.undecodedChunk.readerIndex(i);
            return null;
        }

        this.skipOneLine();

        String s1;

        try {
            s1 = this.readDelimiter(s);
        } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException httppostrequestdecoder_notenoughdatadecoderexception1) {
            this.undecodedChunk.readerIndex(i);
            return null;
        }

        if (s1.equals(s)) {
            this.currentStatus = httppostrequestdecoder_multipartstatus;
            return this.decodeMultipart(httppostrequestdecoder_multipartstatus);
        } else if (s1.equals(s + "--")) {
            this.currentStatus = httppostrequestdecoder_multipartstatus1;
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER) {
                this.currentFieldAttributes = null;
                return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
            } else {
                return null;
            }
        } else {
            this.undecodedChunk.readerIndex(i);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("No Multipart delimiter found");
        }
    }

    private InterfaceHttpData findMultipartDisposition() throws HttpPostRequestDecoder.ErrorDataDecoderException {
        int i = this.undecodedChunk.readerIndex();

        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
            this.currentFieldAttributes = new TreeMap(CaseIgnoringComparator.INSTANCE);
        }

        while (!this.skipOneLine()) {
            String s;

            try {
                this.skipControlCharacters();
                s = this.readLine();
            } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException httppostrequestdecoder_notenoughdatadecoderexception) {
                this.undecodedChunk.readerIndex(i);
                return null;
            }

            String[] astring = splitMultipartHeader(s);

            if (!astring[0].equalsIgnoreCase("Content-Disposition")) {
                Attribute attribute;

                if (astring[0].equalsIgnoreCase("Content-Transfer-Encoding")) {
                    try {
                        attribute = this.factory.createAttribute(this.request, "Content-Transfer-Encoding", cleanString(astring[1]));
                    } catch (NullPointerException nullpointerexception) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(nullpointerexception);
                    } catch (IllegalArgumentException illegalargumentexception) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(illegalargumentexception);
                    }

                    this.currentFieldAttributes.put("Content-Transfer-Encoding", attribute);
                } else if (astring[0].equalsIgnoreCase("Content-Length")) {
                    try {
                        attribute = this.factory.createAttribute(this.request, "Content-Length", cleanString(astring[1]));
                    } catch (NullPointerException nullpointerexception1) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(nullpointerexception1);
                    } catch (IllegalArgumentException illegalargumentexception1) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(illegalargumentexception1);
                    }

                    this.currentFieldAttributes.put("Content-Length", attribute);
                } else {
                    if (!astring[0].equalsIgnoreCase("Content-Type")) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException("Unknown Params: " + s);
                    }

                    if (astring[1].equalsIgnoreCase("multipart/mixed")) {
                        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
                            String[] astring1 = StringUtil.split(astring[2], '=');

                            this.multipartMixedBoundary = "--" + astring1[1];
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
                            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER);
                        }

                        throw new HttpPostRequestDecoder.ErrorDataDecoderException("Mixed Multipart found in a previous Mixed Multipart");
                    }

                    for (int j = 1; j < astring.length; ++j) {
                        if (astring[j].toLowerCase().startsWith("charset")) {
                            String[] astring2 = StringUtil.split(astring[j], '=');

                            Attribute attribute1;

                            try {
                                attribute1 = this.factory.createAttribute(this.request, "charset", cleanString(astring2[1]));
                            } catch (NullPointerException nullpointerexception2) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException(nullpointerexception2);
                            } catch (IllegalArgumentException illegalargumentexception2) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException(illegalargumentexception2);
                            }

                            this.currentFieldAttributes.put("charset", attribute1);
                        } else {
                            Attribute attribute2;

                            try {
                                attribute2 = this.factory.createAttribute(this.request, cleanString(astring[0]), astring[j]);
                            } catch (NullPointerException nullpointerexception3) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException(nullpointerexception3);
                            } catch (IllegalArgumentException illegalargumentexception3) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException(illegalargumentexception3);
                            }

                            this.currentFieldAttributes.put(attribute2.getName(), attribute2);
                        }
                    }
                }
            } else {
                boolean flag;

                if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
                    flag = astring[1].equalsIgnoreCase("form-data");
                } else {
                    flag = astring[1].equalsIgnoreCase("attachment") || astring[1].equalsIgnoreCase("file");
                }

                if (flag) {
                    for (int k = 2; k < astring.length; ++k) {
                        String[] astring3 = StringUtil.split(astring[k], '=');

                        Attribute attribute3;

                        try {
                            String s1 = cleanString(astring3[0]);
                            String s2 = astring3[1];

                            if ("filename".equals(s1)) {
                                s2 = s2.substring(1, s2.length() - 1);
                            } else {
                                s2 = cleanString(s2);
                            }

                            attribute3 = this.factory.createAttribute(this.request, s1, s2);
                        } catch (NullPointerException nullpointerexception4) {
                            throw new HttpPostRequestDecoder.ErrorDataDecoderException(nullpointerexception4);
                        } catch (IllegalArgumentException illegalargumentexception4) {
                            throw new HttpPostRequestDecoder.ErrorDataDecoderException(illegalargumentexception4);
                        }

                        this.currentFieldAttributes.put(attribute3.getName(), attribute3);
                    }
                }
            }
        }

        Attribute attribute4 = (Attribute) this.currentFieldAttributes.get("filename");

        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
            if (attribute4 != null) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD;
                return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD);
            } else {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FIELD);
            }
        } else if (attribute4 != null) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD);
        } else {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Filename not found");
        }
    }

    protected InterfaceHttpData getFileUpload(String s) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        Attribute attribute = (Attribute) this.currentFieldAttributes.get("Content-Transfer-Encoding");
        Charset charset = this.charset;
        HttpPostBodyUtil.TransferEncodingMechanism httppostbodyutil_transferencodingmechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT7;

        if (attribute != null) {
            String s1;

            try {
                s1 = attribute.getValue().toLowerCase();
            } catch (IOException ioexception) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception);
            }

            if (s1.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT7.value())) {
                charset = HttpPostBodyUtil.US_ASCII;
            } else if (s1.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT8.value())) {
                charset = HttpPostBodyUtil.ISO_8859_1;
                httppostbodyutil_transferencodingmechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT8;
            } else {
                if (!s1.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException("TransferEncoding Unknown: " + s1);
                }

                httppostbodyutil_transferencodingmechanism = HttpPostBodyUtil.TransferEncodingMechanism.BINARY;
            }
        }

        Attribute attribute1 = (Attribute) this.currentFieldAttributes.get("charset");

        if (attribute1 != null) {
            try {
                charset = Charset.forName(attribute1.getValue());
            } catch (IOException ioexception1) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception1);
            }
        }

        if (this.currentFileUpload == null) {
            Attribute attribute2 = (Attribute) this.currentFieldAttributes.get("filename");
            Attribute attribute3 = (Attribute) this.currentFieldAttributes.get("name");
            Attribute attribute4 = (Attribute) this.currentFieldAttributes.get("Content-Type");

            if (attribute4 == null) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Content-Type is absent but required");
            }

            Attribute attribute5 = (Attribute) this.currentFieldAttributes.get("Content-Length");

            long i;

            try {
                i = attribute5 != null ? Long.parseLong(attribute5.getValue()) : 0L;
            } catch (IOException ioexception2) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception2);
            } catch (NumberFormatException numberformatexception) {
                i = 0L;
            }

            try {
                this.currentFileUpload = this.factory.createFileUpload(this.request, cleanString(attribute3.getValue()), cleanString(attribute2.getValue()), attribute4.getValue(), httppostbodyutil_transferencodingmechanism.value(), charset, i);
            } catch (NullPointerException nullpointerexception) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(nullpointerexception);
            } catch (IllegalArgumentException illegalargumentexception) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(illegalargumentexception);
            } catch (IOException ioexception3) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception3);
            }
        }

        try {
            this.readFileUploadByteMultipart(s);
        } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException httppostrequestdecoder_notenoughdatadecoderexception) {
            return null;
        }

        if (this.currentFileUpload.isCompleted()) {
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
                this.currentFieldAttributes = null;
            } else {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
                this.cleanMixedAttributes();
            }

            FileUpload fileupload = this.currentFileUpload;

            this.currentFileUpload = null;
            return fileupload;
        } else {
            return null;
        }
    }

    public void destroy() {
        this.checkDestroyed();
        this.cleanFiles();
        this.destroyed = true;
        if (this.undecodedChunk != null && this.undecodedChunk.refCnt() > 0) {
            this.undecodedChunk.release();
            this.undecodedChunk = null;
        }

        for (int i = this.bodyListHttpDataRank; i < this.bodyListHttpData.size(); ++i) {
            ((InterfaceHttpData) this.bodyListHttpData.get(i)).release();
        }

    }

    public void cleanFiles() {
        this.checkDestroyed();
        this.factory.cleanRequestHttpDatas(this.request);
    }

    public void removeHttpDataFromClean(InterfaceHttpData interfacehttpdata) {
        this.checkDestroyed();
        this.factory.removeHttpDataFromClean(this.request, interfacehttpdata);
    }

    private void cleanMixedAttributes() {
        this.currentFieldAttributes.remove("charset");
        this.currentFieldAttributes.remove("Content-Length");
        this.currentFieldAttributes.remove("Content-Transfer-Encoding");
        this.currentFieldAttributes.remove("Content-Type");
        this.currentFieldAttributes.remove("filename");
    }

    private String readLineStandard() throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        int i = this.undecodedChunk.readerIndex();

        try {
            ByteBuf bytebuf = Unpooled.buffer(64);

            while (this.undecodedChunk.isReadable()) {
                byte b0 = this.undecodedChunk.readByte();

                if (b0 == 13) {
                    b0 = this.undecodedChunk.readByte();
                    if (b0 == 10) {
                        return bytebuf.toString(this.charset);
                    }
                } else {
                    if (b0 == 10) {
                        return bytebuf.toString(this.charset);
                    }

                    bytebuf.writeByte(b0);
                }
            }
        } catch (IndexOutOfBoundsException indexoutofboundsexception) {
            this.undecodedChunk.readerIndex(i);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(indexoutofboundsexception);
        }

        this.undecodedChunk.readerIndex(i);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }

    private String readLine() throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize httppostbodyutil_seekaheadoptimize;

        try {
            httppostbodyutil_seekaheadoptimize = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException httppostbodyutil_seekaheadnobackarrayexception) {
            return this.readLineStandard();
        }

        int i = this.undecodedChunk.readerIndex();

        try {
            ByteBuf bytebuf = Unpooled.buffer(64);

            while (httppostbodyutil_seekaheadoptimize.pos < httppostbodyutil_seekaheadoptimize.limit) {
                byte b0 = httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++];

                if (b0 == 13) {
                    if (httppostbodyutil_seekaheadoptimize.pos < httppostbodyutil_seekaheadoptimize.limit) {
                        b0 = httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++];
                        if (b0 == 10) {
                            httppostbodyutil_seekaheadoptimize.setReadPosition(0);
                            return bytebuf.toString(this.charset);
                        }
                    } else {
                        bytebuf.writeByte(b0);
                    }
                } else {
                    if (b0 == 10) {
                        httppostbodyutil_seekaheadoptimize.setReadPosition(0);
                        return bytebuf.toString(this.charset);
                    }

                    bytebuf.writeByte(b0);
                }
            }
        } catch (IndexOutOfBoundsException indexoutofboundsexception) {
            this.undecodedChunk.readerIndex(i);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(indexoutofboundsexception);
        }

        this.undecodedChunk.readerIndex(i);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }

    private String readDelimiterStandard(String s) throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        int i = this.undecodedChunk.readerIndex();

        try {
            StringBuilder stringbuilder = new StringBuilder(64);
            int j = 0;
            int k = s.length();

            byte b0;

            while (this.undecodedChunk.isReadable() && j < k) {
                b0 = this.undecodedChunk.readByte();
                if (b0 != s.charAt(j)) {
                    this.undecodedChunk.readerIndex(i);
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }

                ++j;
                stringbuilder.append((char) b0);
            }

            if (this.undecodedChunk.isReadable()) {
                b0 = this.undecodedChunk.readByte();
                if (b0 == 13) {
                    b0 = this.undecodedChunk.readByte();
                    if (b0 == 10) {
                        return stringbuilder.toString();
                    }

                    this.undecodedChunk.readerIndex(i);
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }

                if (b0 == 10) {
                    return stringbuilder.toString();
                }

                if (b0 == 45) {
                    stringbuilder.append('-');
                    b0 = this.undecodedChunk.readByte();
                    if (b0 == 45) {
                        stringbuilder.append('-');
                        if (this.undecodedChunk.isReadable()) {
                            b0 = this.undecodedChunk.readByte();
                            if (b0 == 13) {
                                b0 = this.undecodedChunk.readByte();
                                if (b0 == 10) {
                                    return stringbuilder.toString();
                                }

                                this.undecodedChunk.readerIndex(i);
                                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                            }

                            if (b0 == 10) {
                                return stringbuilder.toString();
                            }

                            this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
                            return stringbuilder.toString();
                        }

                        return stringbuilder.toString();
                    }
                }
            }
        } catch (IndexOutOfBoundsException indexoutofboundsexception) {
            this.undecodedChunk.readerIndex(i);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(indexoutofboundsexception);
        }

        this.undecodedChunk.readerIndex(i);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }

    private String readDelimiter(String s) throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize httppostbodyutil_seekaheadoptimize;

        try {
            httppostbodyutil_seekaheadoptimize = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException httppostbodyutil_seekaheadnobackarrayexception) {
            return this.readDelimiterStandard(s);
        }

        int i = this.undecodedChunk.readerIndex();
        int j = 0;
        int k = s.length();

        try {
            StringBuilder stringbuilder = new StringBuilder(64);

            byte b0;

            while (httppostbodyutil_seekaheadoptimize.pos < httppostbodyutil_seekaheadoptimize.limit && j < k) {
                b0 = httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++];
                if (b0 != s.charAt(j)) {
                    this.undecodedChunk.readerIndex(i);
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }

                ++j;
                stringbuilder.append((char) b0);
            }

            if (httppostbodyutil_seekaheadoptimize.pos < httppostbodyutil_seekaheadoptimize.limit) {
                b0 = httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++];
                if (b0 == 13) {
                    if (httppostbodyutil_seekaheadoptimize.pos >= httppostbodyutil_seekaheadoptimize.limit) {
                        this.undecodedChunk.readerIndex(i);
                        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                    }

                    b0 = httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++];
                    if (b0 == 10) {
                        httppostbodyutil_seekaheadoptimize.setReadPosition(0);
                        return stringbuilder.toString();
                    }
                } else {
                    if (b0 == 10) {
                        httppostbodyutil_seekaheadoptimize.setReadPosition(0);
                        return stringbuilder.toString();
                    }

                    if (b0 == 45) {
                        stringbuilder.append('-');
                        if (httppostbodyutil_seekaheadoptimize.pos < httppostbodyutil_seekaheadoptimize.limit) {
                            b0 = httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++];
                            if (b0 == 45) {
                                stringbuilder.append('-');
                                if (httppostbodyutil_seekaheadoptimize.pos < httppostbodyutil_seekaheadoptimize.limit) {
                                    b0 = httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++];
                                    if (b0 != 13) {
                                        if (b0 == 10) {
                                            httppostbodyutil_seekaheadoptimize.setReadPosition(0);
                                            return stringbuilder.toString();
                                        }

                                        httppostbodyutil_seekaheadoptimize.setReadPosition(1);
                                        return stringbuilder.toString();
                                    }

                                    if (httppostbodyutil_seekaheadoptimize.pos >= httppostbodyutil_seekaheadoptimize.limit) {
                                        this.undecodedChunk.readerIndex(i);
                                        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                                    }

                                    b0 = httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++];
                                    if (b0 == 10) {
                                        httppostbodyutil_seekaheadoptimize.setReadPosition(0);
                                        return stringbuilder.toString();
                                    }
                                }

                                httppostbodyutil_seekaheadoptimize.setReadPosition(0);
                                return stringbuilder.toString();
                            }
                        }
                    }
                }
            }
        } catch (IndexOutOfBoundsException indexoutofboundsexception) {
            this.undecodedChunk.readerIndex(i);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(indexoutofboundsexception);
        }

        this.undecodedChunk.readerIndex(i);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }

    private void readFileUploadByteMultipartStandard(String s) throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException {
        int i = this.undecodedChunk.readerIndex();
        boolean flag = true;
        int j = 0;
        int k = this.undecodedChunk.readerIndex();
        boolean flag1 = false;

        while (this.undecodedChunk.isReadable()) {
            byte b0 = this.undecodedChunk.readByte();

            if (flag) {
                if (b0 == s.codePointAt(j)) {
                    ++j;
                    if (s.length() == j) {
                        flag1 = true;
                        break;
                    }
                } else {
                    flag = false;
                    j = 0;
                    if (b0 == 13) {
                        if (this.undecodedChunk.isReadable()) {
                            b0 = this.undecodedChunk.readByte();
                            if (b0 == 10) {
                                flag = true;
                                j = 0;
                                k = this.undecodedChunk.readerIndex() - 2;
                            } else {
                                k = this.undecodedChunk.readerIndex() - 1;
                                this.undecodedChunk.readerIndex(k);
                            }
                        }
                    } else if (b0 == 10) {
                        flag = true;
                        j = 0;
                        k = this.undecodedChunk.readerIndex() - 1;
                    } else {
                        k = this.undecodedChunk.readerIndex();
                    }
                }
            } else if (b0 == 13) {
                if (this.undecodedChunk.isReadable()) {
                    b0 = this.undecodedChunk.readByte();
                    if (b0 == 10) {
                        flag = true;
                        j = 0;
                        k = this.undecodedChunk.readerIndex() - 2;
                    } else {
                        k = this.undecodedChunk.readerIndex() - 1;
                        this.undecodedChunk.readerIndex(k);
                    }
                }
            } else if (b0 == 10) {
                flag = true;
                j = 0;
                k = this.undecodedChunk.readerIndex() - 1;
            } else {
                k = this.undecodedChunk.readerIndex();
            }
        }

        ByteBuf bytebuf = this.undecodedChunk.slice(i, k - i).retain();

        if (flag1) {
            try {
                this.currentFileUpload.addContent(bytebuf, true);
                this.undecodedChunk.readerIndex(k);
            } catch (IOException ioexception) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception);
            }
        } else {
            try {
                this.currentFileUpload.addContent(bytebuf, false);
                this.undecodedChunk.readerIndex(k);
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            } catch (IOException ioexception1) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception1);
            }
        }
    }

    private void readFileUploadByteMultipart(String s) throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize httppostbodyutil_seekaheadoptimize;

        try {
            httppostbodyutil_seekaheadoptimize = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException httppostbodyutil_seekaheadnobackarrayexception) {
            this.readFileUploadByteMultipartStandard(s);
            return;
        }

        int i = this.undecodedChunk.readerIndex();
        boolean flag = true;
        int j = 0;
        int k = httppostbodyutil_seekaheadoptimize.pos;
        boolean flag1 = false;

        while (httppostbodyutil_seekaheadoptimize.pos < httppostbodyutil_seekaheadoptimize.limit) {
            byte b0 = httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++];

            if (flag) {
                if (b0 == s.codePointAt(j)) {
                    ++j;
                    if (s.length() == j) {
                        flag1 = true;
                        break;
                    }
                } else {
                    flag = false;
                    j = 0;
                    if (b0 == 13) {
                        if (httppostbodyutil_seekaheadoptimize.pos < httppostbodyutil_seekaheadoptimize.limit) {
                            b0 = httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++];
                            if (b0 == 10) {
                                flag = true;
                                j = 0;
                                k = httppostbodyutil_seekaheadoptimize.pos - 2;
                            } else {
                                --httppostbodyutil_seekaheadoptimize.pos;
                                k = httppostbodyutil_seekaheadoptimize.pos;
                            }
                        }
                    } else if (b0 == 10) {
                        flag = true;
                        j = 0;
                        k = httppostbodyutil_seekaheadoptimize.pos - 1;
                    } else {
                        k = httppostbodyutil_seekaheadoptimize.pos;
                    }
                }
            } else if (b0 == 13) {
                if (httppostbodyutil_seekaheadoptimize.pos < httppostbodyutil_seekaheadoptimize.limit) {
                    b0 = httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++];
                    if (b0 == 10) {
                        flag = true;
                        j = 0;
                        k = httppostbodyutil_seekaheadoptimize.pos - 2;
                    } else {
                        --httppostbodyutil_seekaheadoptimize.pos;
                        k = httppostbodyutil_seekaheadoptimize.pos;
                    }
                }
            } else if (b0 == 10) {
                flag = true;
                j = 0;
                k = httppostbodyutil_seekaheadoptimize.pos - 1;
            } else {
                k = httppostbodyutil_seekaheadoptimize.pos;
            }
        }

        int l = httppostbodyutil_seekaheadoptimize.getReadPosition(k);
        ByteBuf bytebuf = this.undecodedChunk.slice(i, l - i).retain();

        if (flag1) {
            try {
                this.currentFileUpload.addContent(bytebuf, true);
                this.undecodedChunk.readerIndex(l);
            } catch (IOException ioexception) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception);
            }
        } else {
            try {
                this.currentFileUpload.addContent(bytebuf, false);
                this.undecodedChunk.readerIndex(l);
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            } catch (IOException ioexception1) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception1);
            }
        }
    }

    private void loadFieldMultipartStandard(String s) throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException {
        int i = this.undecodedChunk.readerIndex();

        try {
            boolean flag = true;
            int j = 0;
            int k = this.undecodedChunk.readerIndex();
            boolean flag1 = false;

            while (this.undecodedChunk.isReadable()) {
                byte b0 = this.undecodedChunk.readByte();

                if (flag) {
                    if (b0 == s.codePointAt(j)) {
                        ++j;
                        if (s.length() == j) {
                            flag1 = true;
                            break;
                        }
                    } else {
                        flag = false;
                        j = 0;
                        if (b0 == 13) {
                            if (this.undecodedChunk.isReadable()) {
                                b0 = this.undecodedChunk.readByte();
                                if (b0 == 10) {
                                    flag = true;
                                    j = 0;
                                    k = this.undecodedChunk.readerIndex() - 2;
                                }
                            }
                        } else if (b0 == 10) {
                            flag = true;
                            j = 0;
                            k = this.undecodedChunk.readerIndex() - 1;
                        } else {
                            k = this.undecodedChunk.readerIndex();
                        }
                    }
                } else if (b0 == 13) {
                    if (this.undecodedChunk.isReadable()) {
                        b0 = this.undecodedChunk.readByte();
                        if (b0 == 10) {
                            flag = true;
                            j = 0;
                            k = this.undecodedChunk.readerIndex() - 2;
                        }
                    }
                } else if (b0 == 10) {
                    flag = true;
                    j = 0;
                    k = this.undecodedChunk.readerIndex() - 1;
                } else {
                    k = this.undecodedChunk.readerIndex();
                }
            }

            if (flag1) {
                try {
                    this.currentAttribute.addContent(this.undecodedChunk.slice(i, k - i).retain(), true);
                } catch (IOException ioexception) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception);
                }

                this.undecodedChunk.readerIndex(k);
            } else {
                try {
                    this.currentAttribute.addContent(this.undecodedChunk.slice(i, k - i).retain(), false);
                } catch (IOException ioexception1) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception1);
                }

                this.undecodedChunk.readerIndex(k);
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }
        } catch (IndexOutOfBoundsException indexoutofboundsexception) {
            this.undecodedChunk.readerIndex(i);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(indexoutofboundsexception);
        }
    }

    private void loadFieldMultipart(String s) throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize httppostbodyutil_seekaheadoptimize;

        try {
            httppostbodyutil_seekaheadoptimize = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException httppostbodyutil_seekaheadnobackarrayexception) {
            this.loadFieldMultipartStandard(s);
            return;
        }

        int i = this.undecodedChunk.readerIndex();

        try {
            boolean flag = true;
            int j = 0;
            int k = httppostbodyutil_seekaheadoptimize.pos;
            boolean flag1 = false;

            while (httppostbodyutil_seekaheadoptimize.pos < httppostbodyutil_seekaheadoptimize.limit) {
                byte b0 = httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++];

                if (flag) {
                    if (b0 == s.codePointAt(j)) {
                        ++j;
                        if (s.length() == j) {
                            flag1 = true;
                            break;
                        }
                    } else {
                        flag = false;
                        j = 0;
                        if (b0 == 13) {
                            if (httppostbodyutil_seekaheadoptimize.pos < httppostbodyutil_seekaheadoptimize.limit) {
                                b0 = httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++];
                                if (b0 == 10) {
                                    flag = true;
                                    j = 0;
                                    k = httppostbodyutil_seekaheadoptimize.pos - 2;
                                }
                            }
                        } else if (b0 == 10) {
                            flag = true;
                            j = 0;
                            k = httppostbodyutil_seekaheadoptimize.pos - 1;
                        } else {
                            k = httppostbodyutil_seekaheadoptimize.pos;
                        }
                    }
                } else if (b0 == 13) {
                    if (httppostbodyutil_seekaheadoptimize.pos < httppostbodyutil_seekaheadoptimize.limit) {
                        b0 = httppostbodyutil_seekaheadoptimize.bytes[httppostbodyutil_seekaheadoptimize.pos++];
                        if (b0 == 10) {
                            flag = true;
                            j = 0;
                            k = httppostbodyutil_seekaheadoptimize.pos - 2;
                        }
                    }
                } else if (b0 == 10) {
                    flag = true;
                    j = 0;
                    k = httppostbodyutil_seekaheadoptimize.pos - 1;
                } else {
                    k = httppostbodyutil_seekaheadoptimize.pos;
                }
            }

            int l = httppostbodyutil_seekaheadoptimize.getReadPosition(k);

            if (flag1) {
                try {
                    this.currentAttribute.addContent(this.undecodedChunk.slice(i, l - i).retain(), true);
                } catch (IOException ioexception) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception);
                }

                this.undecodedChunk.readerIndex(l);
            } else {
                try {
                    this.currentAttribute.addContent(this.undecodedChunk.slice(i, l - i).retain(), false);
                } catch (IOException ioexception1) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(ioexception1);
                }

                this.undecodedChunk.readerIndex(l);
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }
        } catch (IndexOutOfBoundsException indexoutofboundsexception) {
            this.undecodedChunk.readerIndex(i);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(indexoutofboundsexception);
        }
    }

    private static String cleanString(String s) {
        StringBuilder stringbuilder = new StringBuilder(s.length());

        for (int i = 0; i < s.length(); ++i) {
            char c0 = s.charAt(i);

            if (c0 == ':') {
                stringbuilder.append(32);
            } else if (c0 == ',') {
                stringbuilder.append(32);
            } else if (c0 == '=') {
                stringbuilder.append(32);
            } else if (c0 == ';') {
                stringbuilder.append(32);
            } else if (c0 == '\t') {
                stringbuilder.append(32);
            } else if (c0 != '"') {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString().trim();
    }

    private boolean skipOneLine() {
        if (!this.undecodedChunk.isReadable()) {
            return false;
        } else {
            byte b0 = this.undecodedChunk.readByte();

            if (b0 == 13) {
                if (!this.undecodedChunk.isReadable()) {
                    this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
                    return false;
                } else {
                    b0 = this.undecodedChunk.readByte();
                    if (b0 == 10) {
                        return true;
                    } else {
                        this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 2);
                        return false;
                    }
                }
            } else if (b0 == 10) {
                return true;
            } else {
                this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
                return false;
            }
        }
    }

    private static String[] splitHeaderContentType(String s) {
        int i = HttpPostBodyUtil.findNonWhitespace(s, 0);
        int j = s.indexOf(59);

        if (j == -1) {
            return new String[]{s, ""};
        } else {
            if (s.charAt(j - 1) == ' ') {
                --j;
            }

            int k = HttpPostBodyUtil.findNonWhitespace(s, j + 1);
            int l = HttpPostBodyUtil.findEndOfString(s);

            return new String[]{s.substring(i, j), s.substring(k, l)};
        }
    }

    private static String[] splitMultipartHeader(String s) {
        ArrayList arraylist = new ArrayList(1);
        int i = HttpPostBodyUtil.findNonWhitespace(s, 0);

        int j;

        for (j = i; j < s.length(); ++j) {
            char c0 = s.charAt(j);

            if (c0 == ':' || Character.isWhitespace(c0)) {
                break;
            }
        }

        int k;

        for (k = j; k < s.length(); ++k) {
            if (s.charAt(k) == ':') {
                ++k;
                break;
            }
        }

        int l = HttpPostBodyUtil.findNonWhitespace(s, k);
        int i1 = HttpPostBodyUtil.findEndOfString(s);

        arraylist.add(s.substring(i, j));
        String s1 = s.substring(l, i1);
        String[] astring;

        if (s1.indexOf(59) >= 0) {
            astring = StringUtil.split(s1, ';');
        } else {
            astring = StringUtil.split(s1, ',');
        }

        String[] astring1 = astring;
        int j1 = astring.length;

        for (int k1 = 0; k1 < j1; ++k1) {
            String s2 = astring1[k1];

            arraylist.add(s2.trim());
        }

        astring1 = new String[arraylist.size()];

        for (j1 = 0; j1 < arraylist.size(); ++j1) {
            astring1[j1] = (String) arraylist.get(j1);
        }

        return astring1;
    }

    public static class IncompatibleDataDecoderException extends DecoderException {

        private static final long serialVersionUID = -953268047926250267L;

        public IncompatibleDataDecoderException() {}

        public IncompatibleDataDecoderException(String s) {
            super(s);
        }

        public IncompatibleDataDecoderException(Throwable throwable) {
            super(throwable);
        }

        public IncompatibleDataDecoderException(String s, Throwable throwable) {
            super(s, throwable);
        }
    }

    public static class ErrorDataDecoderException extends DecoderException {

        private static final long serialVersionUID = 5020247425493164465L;

        public ErrorDataDecoderException() {}

        public ErrorDataDecoderException(String s) {
            super(s);
        }

        public ErrorDataDecoderException(Throwable throwable) {
            super(throwable);
        }

        public ErrorDataDecoderException(String s, Throwable throwable) {
            super(s, throwable);
        }
    }

    public static class EndOfDataDecoderException extends DecoderException {

        private static final long serialVersionUID = 1336267941020800769L;

        public EndOfDataDecoderException() {}
    }

    public static class NotEnoughDataDecoderException extends DecoderException {

        private static final long serialVersionUID = -7846841864603865638L;

        public NotEnoughDataDecoderException() {}

        public NotEnoughDataDecoderException(String s) {
            super(s);
        }

        public NotEnoughDataDecoderException(Throwable throwable) {
            super(throwable);
        }

        public NotEnoughDataDecoderException(String s, Throwable throwable) {
            super(s, throwable);
        }
    }

    private static enum MultiPartStatus {

        NOTSTARTED, PREAMBLE, HEADERDELIMITER, DISPOSITION, FIELD, FILEUPLOAD, MIXEDPREAMBLE, MIXEDDELIMITER, MIXEDDISPOSITION, MIXEDFILEUPLOAD, MIXEDCLOSEDELIMITER, CLOSEDELIMITER, PREEPILOGUE, EPILOGUE;

        private MultiPartStatus() {}
    }
}
