package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Beta
@GwtCompatible
@Immutable
public final class MediaType {

    private static final String CHARSET_ATTRIBUTE = "charset";
    private static final ImmutableListMultimap<String, String> UTF_8_CONSTANT_PARAMETERS = ImmutableListMultimap.of("charset", Ascii.toLowerCase(Charsets.UTF_8.name()));
    private static final CharMatcher TOKEN_MATCHER = CharMatcher.ASCII.and(CharMatcher.JAVA_ISO_CONTROL.negate()).and(CharMatcher.isNot(' ')).and(CharMatcher.noneOf("()<>@,;:\\\"/[]?="));
    private static final CharMatcher QUOTED_TEXT_MATCHER = CharMatcher.ASCII.and(CharMatcher.noneOf("\"\\\r"));
    private static final CharMatcher LINEAR_WHITE_SPACE = CharMatcher.anyOf(" \t\r\n");
    private static final String APPLICATION_TYPE = "application";
    private static final String AUDIO_TYPE = "audio";
    private static final String IMAGE_TYPE = "image";
    private static final String TEXT_TYPE = "text";
    private static final String VIDEO_TYPE = "video";
    private static final String WILDCARD = "*";
    private static final Map<MediaType, MediaType> KNOWN_TYPES = Maps.newHashMap();
    public static final MediaType ANY_TYPE = createConstant("*", "*");
    public static final MediaType ANY_TEXT_TYPE = createConstant("text", "*");
    public static final MediaType ANY_IMAGE_TYPE = createConstant("image", "*");
    public static final MediaType ANY_AUDIO_TYPE = createConstant("audio", "*");
    public static final MediaType ANY_VIDEO_TYPE = createConstant("video", "*");
    public static final MediaType ANY_APPLICATION_TYPE = createConstant("application", "*");
    public static final MediaType CACHE_MANIFEST_UTF_8 = createConstantUtf8("text", "cache-manifest");
    public static final MediaType CSS_UTF_8 = createConstantUtf8("text", "css");
    public static final MediaType CSV_UTF_8 = createConstantUtf8("text", "csv");
    public static final MediaType HTML_UTF_8 = createConstantUtf8("text", "html");
    public static final MediaType I_CALENDAR_UTF_8 = createConstantUtf8("text", "calendar");
    public static final MediaType PLAIN_TEXT_UTF_8 = createConstantUtf8("text", "plain");
    public static final MediaType TEXT_JAVASCRIPT_UTF_8 = createConstantUtf8("text", "javascript");
    public static final MediaType TSV_UTF_8 = createConstantUtf8("text", "tab-separated-values");
    public static final MediaType VCARD_UTF_8 = createConstantUtf8("text", "vcard");
    public static final MediaType WML_UTF_8 = createConstantUtf8("text", "vnd.wap.wml");
    public static final MediaType XML_UTF_8 = createConstantUtf8("text", "xml");
    public static final MediaType BMP = createConstant("image", "bmp");
    public static final MediaType CRW = createConstant("image", "x-canon-crw");
    public static final MediaType GIF = createConstant("image", "gif");
    public static final MediaType ICO = createConstant("image", "vnd.microsoft.icon");
    public static final MediaType JPEG = createConstant("image", "jpeg");
    public static final MediaType PNG = createConstant("image", "png");
    public static final MediaType PSD = createConstant("image", "vnd.adobe.photoshop");
    public static final MediaType SVG_UTF_8 = createConstantUtf8("image", "svg+xml");
    public static final MediaType TIFF = createConstant("image", "tiff");
    public static final MediaType WEBP = createConstant("image", "webp");
    public static final MediaType MP4_AUDIO = createConstant("audio", "mp4");
    public static final MediaType MPEG_AUDIO = createConstant("audio", "mpeg");
    public static final MediaType OGG_AUDIO = createConstant("audio", "ogg");
    public static final MediaType WEBM_AUDIO = createConstant("audio", "webm");
    public static final MediaType MP4_VIDEO = createConstant("video", "mp4");
    public static final MediaType MPEG_VIDEO = createConstant("video", "mpeg");
    public static final MediaType OGG_VIDEO = createConstant("video", "ogg");
    public static final MediaType QUICKTIME = createConstant("video", "quicktime");
    public static final MediaType WEBM_VIDEO = createConstant("video", "webm");
    public static final MediaType WMV = createConstant("video", "x-ms-wmv");
    public static final MediaType APPLICATION_XML_UTF_8 = createConstantUtf8("application", "xml");
    public static final MediaType ATOM_UTF_8 = createConstantUtf8("application", "atom+xml");
    public static final MediaType BZIP2 = createConstant("application", "x-bzip2");
    public static final MediaType EPUB = createConstant("application", "epub+zip");
    public static final MediaType FORM_DATA = createConstant("application", "x-www-form-urlencoded");
    public static final MediaType KEY_ARCHIVE = createConstant("application", "pkcs12");
    public static final MediaType APPLICATION_BINARY = createConstant("application", "binary");
    public static final MediaType GZIP = createConstant("application", "x-gzip");
    public static final MediaType JAVASCRIPT_UTF_8 = createConstantUtf8("application", "javascript");
    public static final MediaType JSON_UTF_8 = createConstantUtf8("application", "json");
    public static final MediaType KML = createConstant("application", "vnd.google-earth.kml+xml");
    public static final MediaType KMZ = createConstant("application", "vnd.google-earth.kmz");
    public static final MediaType MBOX = createConstant("application", "mbox");
    public static final MediaType MICROSOFT_EXCEL = createConstant("application", "vnd.ms-excel");
    public static final MediaType MICROSOFT_POWERPOINT = createConstant("application", "vnd.ms-powerpoint");
    public static final MediaType MICROSOFT_WORD = createConstant("application", "msword");
    public static final MediaType OCTET_STREAM = createConstant("application", "octet-stream");
    public static final MediaType OGG_CONTAINER = createConstant("application", "ogg");
    public static final MediaType OOXML_DOCUMENT = createConstant("application", "vnd.openxmlformats-officedocument.wordprocessingml.document");
    public static final MediaType OOXML_PRESENTATION = createConstant("application", "vnd.openxmlformats-officedocument.presentationml.presentation");
    public static final MediaType OOXML_SHEET = createConstant("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    public static final MediaType OPENDOCUMENT_GRAPHICS = createConstant("application", "vnd.oasis.opendocument.graphics");
    public static final MediaType OPENDOCUMENT_PRESENTATION = createConstant("application", "vnd.oasis.opendocument.presentation");
    public static final MediaType OPENDOCUMENT_SPREADSHEET = createConstant("application", "vnd.oasis.opendocument.spreadsheet");
    public static final MediaType OPENDOCUMENT_TEXT = createConstant("application", "vnd.oasis.opendocument.text");
    public static final MediaType PDF = createConstant("application", "pdf");
    public static final MediaType POSTSCRIPT = createConstant("application", "postscript");
    public static final MediaType PROTOBUF = createConstant("application", "protobuf");
    public static final MediaType RDF_XML_UTF_8 = createConstantUtf8("application", "rdf+xml");
    public static final MediaType RTF_UTF_8 = createConstantUtf8("application", "rtf");
    public static final MediaType SHOCKWAVE_FLASH = createConstant("application", "x-shockwave-flash");
    public static final MediaType SKETCHUP = createConstant("application", "vnd.sketchup.skp");
    public static final MediaType TAR = createConstant("application", "x-tar");
    public static final MediaType XHTML_UTF_8 = createConstantUtf8("application", "xhtml+xml");
    public static final MediaType XRD_UTF_8 = createConstantUtf8("application", "xrd+xml");
    public static final MediaType ZIP = createConstant("application", "zip");
    private final String type;
    private final String subtype;
    private final ImmutableListMultimap<String, String> parameters;
    private static final Joiner.MapJoiner PARAMETER_JOINER = Joiner.on("; ").withKeyValueSeparator("=");

    private static MediaType createConstant(String s, String s1) {
        return addKnownType(new MediaType(s, s1, ImmutableListMultimap.of()));
    }

    private static MediaType createConstantUtf8(String s, String s1) {
        return addKnownType(new MediaType(s, s1, MediaType.UTF_8_CONSTANT_PARAMETERS));
    }

    private static MediaType addKnownType(MediaType mediatype) {
        MediaType.KNOWN_TYPES.put(mediatype, mediatype);
        return mediatype;
    }

    private MediaType(String s, String s1, ImmutableListMultimap<String, String> immutablelistmultimap) {
        this.type = s;
        this.subtype = s1;
        this.parameters = immutablelistmultimap;
    }

    public String type() {
        return this.type;
    }

    public String subtype() {
        return this.subtype;
    }

    public ImmutableListMultimap<String, String> parameters() {
        return this.parameters;
    }

    private Map<String, ImmutableMultiset<String>> parametersAsMap() {
        return Maps.transformValues((Map) this.parameters.asMap(), new Function<Collection<String>, ImmutableMultiset<String>>() {
            public ImmutableMultiset<String> apply(Collection<String> collection) {
                return ImmutableMultiset.copyOf((Iterable) collection);
            }
        });
    }

    public Optional<Charset> charset() {
        ImmutableSet immutableset = ImmutableSet.copyOf((Collection) this.parameters.get("charset"));

        switch (immutableset.size()) {
            case 0:
                return Optional.absent();
            case 1:
                return Optional.of(Charset.forName((String) Iterables.getOnlyElement(immutableset)));
            default:
                throw new IllegalStateException("Multiple charset values defined: " + immutableset);
        }
    }

    public MediaType withoutParameters() {
        return this.parameters.isEmpty() ? this : create(this.type, this.subtype);
    }

    public MediaType withParameters(Multimap<String, String> multimap) {
        return create(this.type, this.subtype, multimap);
    }

    public MediaType withParameter(String s, String s1) {
        Preconditions.checkNotNull(s);
        Preconditions.checkNotNull(s1);
        String s2 = normalizeToken(s);
        ImmutableListMultimap.Builder immutablelistmultimap_builder = ImmutableListMultimap.builder();
        Iterator iterator = this.parameters.entries().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String s3 = (String) entry.getKey();

            if (!s2.equals(s3)) {
                immutablelistmultimap_builder.put(s3, entry.getValue());
            }
        }

        immutablelistmultimap_builder.put(s2, normalizeParameterValue(s2, s1));
        MediaType mediatype = new MediaType(this.type, this.subtype, immutablelistmultimap_builder.build());

        return (MediaType) Objects.firstNonNull(MediaType.KNOWN_TYPES.get(mediatype), mediatype);
    }

    public MediaType withCharset(Charset charset) {
        Preconditions.checkNotNull(charset);
        return this.withParameter("charset", charset.name());
    }

    public boolean hasWildcard() {
        return "*".equals(this.type) || "*".equals(this.subtype);
    }

    public boolean is(MediaType mediatype) {
        return (mediatype.type.equals("*") || mediatype.type.equals(this.type)) && (mediatype.subtype.equals("*") || mediatype.subtype.equals(this.subtype)) && this.parameters.entries().containsAll(mediatype.parameters.entries());
    }

    public static MediaType create(String s, String s1) {
        return create(s, s1, ImmutableListMultimap.of());
    }

    static MediaType createApplicationType(String s) {
        return create("application", s);
    }

    static MediaType createAudioType(String s) {
        return create("audio", s);
    }

    static MediaType createImageType(String s) {
        return create("image", s);
    }

    static MediaType createTextType(String s) {
        return create("text", s);
    }

    static MediaType createVideoType(String s) {
        return create("video", s);
    }

    private static MediaType create(String s, String s1, Multimap<String, String> multimap) {
        Preconditions.checkNotNull(s);
        Preconditions.checkNotNull(s1);
        Preconditions.checkNotNull(multimap);
        String s2 = normalizeToken(s);
        String s3 = normalizeToken(s1);

        Preconditions.checkArgument(!"*".equals(s2) || "*".equals(s3), "A wildcard type cannot be used with a non-wildcard subtype");
        ImmutableListMultimap.Builder immutablelistmultimap_builder = ImmutableListMultimap.builder();
        Iterator iterator = multimap.entries().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String s4 = normalizeToken((String) entry.getKey());

            immutablelistmultimap_builder.put(s4, normalizeParameterValue(s4, (String) entry.getValue()));
        }

        MediaType mediatype = new MediaType(s2, s3, immutablelistmultimap_builder.build());

        return (MediaType) Objects.firstNonNull(MediaType.KNOWN_TYPES.get(mediatype), mediatype);
    }

    private static String normalizeToken(String s) {
        Preconditions.checkArgument(MediaType.TOKEN_MATCHER.matchesAllOf(s));
        return Ascii.toLowerCase(s);
    }

    private static String normalizeParameterValue(String s, String s1) {
        return "charset".equals(s) ? Ascii.toLowerCase(s1) : s1;
    }

    public static MediaType parse(String s) {
        Preconditions.checkNotNull(s);
        MediaType.Tokenizer mediatype_tokenizer = new MediaType.Tokenizer(s);

        try {
            String s1 = mediatype_tokenizer.consumeToken(MediaType.TOKEN_MATCHER);

            mediatype_tokenizer.consumeCharacter('/');
            String s2 = mediatype_tokenizer.consumeToken(MediaType.TOKEN_MATCHER);

            String s3;
            String s4;
            ImmutableListMultimap.Builder immutablelistmultimap_builder;

            for (immutablelistmultimap_builder = ImmutableListMultimap.builder(); mediatype_tokenizer.hasMore(); immutablelistmultimap_builder.put(s3, s4)) {
                mediatype_tokenizer.consumeCharacter(';');
                mediatype_tokenizer.consumeTokenIfPresent(MediaType.LINEAR_WHITE_SPACE);
                s3 = mediatype_tokenizer.consumeToken(MediaType.TOKEN_MATCHER);
                mediatype_tokenizer.consumeCharacter('=');
                if ('"' == mediatype_tokenizer.previewChar()) {
                    mediatype_tokenizer.consumeCharacter('"');
                    StringBuilder stringbuilder = new StringBuilder();

                    while ('"' != mediatype_tokenizer.previewChar()) {
                        if ('\\' == mediatype_tokenizer.previewChar()) {
                            mediatype_tokenizer.consumeCharacter('\\');
                            stringbuilder.append(mediatype_tokenizer.consumeCharacter(CharMatcher.ASCII));
                        } else {
                            stringbuilder.append(mediatype_tokenizer.consumeToken(MediaType.QUOTED_TEXT_MATCHER));
                        }
                    }

                    s4 = stringbuilder.toString();
                    mediatype_tokenizer.consumeCharacter('"');
                } else {
                    s4 = mediatype_tokenizer.consumeToken(MediaType.TOKEN_MATCHER);
                }
            }

            return create(s1, s2, immutablelistmultimap_builder.build());
        } catch (IllegalStateException illegalstateexception) {
            throw new IllegalArgumentException(illegalstateexception);
        }
    }

    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof MediaType)) {
            return false;
        } else {
            MediaType mediatype = (MediaType) object;

            return this.type.equals(mediatype.type) && this.subtype.equals(mediatype.subtype) && this.parametersAsMap().equals(mediatype.parametersAsMap());
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.type, this.subtype, this.parametersAsMap());
    }

    public String toString() {
        StringBuilder stringbuilder = (new StringBuilder()).append(this.type).append('/').append(this.subtype);

        if (!this.parameters.isEmpty()) {
            stringbuilder.append("; ");
            ListMultimap listmultimap = Multimaps.transformValues((ListMultimap) this.parameters, new Function<String, String>() {
                public String apply(String s) {
                    return MediaType.TOKEN_MATCHER.matchesAllOf(s) ? s : MediaType.escapeAndQuote(s);
                }
            });

            MediaType.PARAMETER_JOINER.appendTo(stringbuilder, (Iterable) listmultimap.entries());
        }

        return stringbuilder.toString();
    }

    private static String escapeAndQuote(String s) {
        StringBuilder stringbuilder = (new StringBuilder(s.length() + 16)).append('"');
        char[] achar = s.toCharArray();
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char c0 = achar[j];

            if (c0 == '\r' || c0 == '\\' || c0 == '"') {
                stringbuilder.append('\\');
            }

            stringbuilder.append(c0);
        }

        return stringbuilder.append('"').toString();
    }

    private static final class Tokenizer {

        final String input;
        int position = 0;

        Tokenizer(String s) {
            this.input = s;
        }

        String consumeTokenIfPresent(CharMatcher charmatcher) {
            Preconditions.checkState(this.hasMore());
            int i = this.position;

            this.position = charmatcher.negate().indexIn(this.input, i);
            return this.hasMore() ? this.input.substring(i, this.position) : this.input.substring(i);
        }

        String consumeToken(CharMatcher charmatcher) {
            int i = this.position;
            String s = this.consumeTokenIfPresent(charmatcher);

            Preconditions.checkState(this.position != i);
            return s;
        }

        char consumeCharacter(CharMatcher charmatcher) {
            Preconditions.checkState(this.hasMore());
            char c0 = this.previewChar();

            Preconditions.checkState(charmatcher.matches(c0));
            ++this.position;
            return c0;
        }

        char consumeCharacter(char c0) {
            Preconditions.checkState(this.hasMore());
            Preconditions.checkState(this.previewChar() == c0);
            ++this.position;
            return c0;
        }

        char previewChar() {
            Preconditions.checkState(this.hasMore());
            return this.input.charAt(this.position);
        }

        boolean hasMore() {
            return this.position >= 0 && this.position < this.input.length();
        }
    }
}
