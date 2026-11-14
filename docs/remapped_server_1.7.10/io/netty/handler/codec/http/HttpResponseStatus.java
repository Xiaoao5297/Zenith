package io.netty.handler.codec.http;

public class HttpResponseStatus implements Comparable<HttpResponseStatus> {

    public static final HttpResponseStatus CONTINUE = new HttpResponseStatus(100, "Continue");
    public static final HttpResponseStatus SWITCHING_PROTOCOLS = new HttpResponseStatus(101, "Switching Protocols");
    public static final HttpResponseStatus PROCESSING = new HttpResponseStatus(102, "Processing");
    public static final HttpResponseStatus OK = new HttpResponseStatus(200, "OK");
    public static final HttpResponseStatus CREATED = new HttpResponseStatus(201, "Created");
    public static final HttpResponseStatus ACCEPTED = new HttpResponseStatus(202, "Accepted");
    public static final HttpResponseStatus NON_AUTHORITATIVE_INFORMATION = new HttpResponseStatus(203, "Non-Authoritative Information");
    public static final HttpResponseStatus NO_CONTENT = new HttpResponseStatus(204, "No Content");
    public static final HttpResponseStatus RESET_CONTENT = new HttpResponseStatus(205, "Reset Content");
    public static final HttpResponseStatus PARTIAL_CONTENT = new HttpResponseStatus(206, "Partial Content");
    public static final HttpResponseStatus MULTI_STATUS = new HttpResponseStatus(207, "Multi-Status");
    public static final HttpResponseStatus MULTIPLE_CHOICES = new HttpResponseStatus(300, "Multiple Choices");
    public static final HttpResponseStatus MOVED_PERMANENTLY = new HttpResponseStatus(301, "Moved Permanently");
    public static final HttpResponseStatus FOUND = new HttpResponseStatus(302, "Found");
    public static final HttpResponseStatus SEE_OTHER = new HttpResponseStatus(303, "See Other");
    public static final HttpResponseStatus NOT_MODIFIED = new HttpResponseStatus(304, "Not Modified");
    public static final HttpResponseStatus USE_PROXY = new HttpResponseStatus(305, "Use Proxy");
    public static final HttpResponseStatus TEMPORARY_REDIRECT = new HttpResponseStatus(307, "Temporary Redirect");
    public static final HttpResponseStatus BAD_REQUEST = new HttpResponseStatus(400, "Bad Request");
    public static final HttpResponseStatus UNAUTHORIZED = new HttpResponseStatus(401, "Unauthorized");
    public static final HttpResponseStatus PAYMENT_REQUIRED = new HttpResponseStatus(402, "Payment Required");
    public static final HttpResponseStatus FORBIDDEN = new HttpResponseStatus(403, "Forbidden");
    public static final HttpResponseStatus NOT_FOUND = new HttpResponseStatus(404, "Not Found");
    public static final HttpResponseStatus METHOD_NOT_ALLOWED = new HttpResponseStatus(405, "Method Not Allowed");
    public static final HttpResponseStatus NOT_ACCEPTABLE = new HttpResponseStatus(406, "Not Acceptable");
    public static final HttpResponseStatus PROXY_AUTHENTICATION_REQUIRED = new HttpResponseStatus(407, "Proxy Authentication Required");
    public static final HttpResponseStatus REQUEST_TIMEOUT = new HttpResponseStatus(408, "Request Timeout");
    public static final HttpResponseStatus CONFLICT = new HttpResponseStatus(409, "Conflict");
    public static final HttpResponseStatus GONE = new HttpResponseStatus(410, "Gone");
    public static final HttpResponseStatus LENGTH_REQUIRED = new HttpResponseStatus(411, "Length Required");
    public static final HttpResponseStatus PRECONDITION_FAILED = new HttpResponseStatus(412, "Precondition Failed");
    public static final HttpResponseStatus REQUEST_ENTITY_TOO_LARGE = new HttpResponseStatus(413, "Request Entity Too Large");
    public static final HttpResponseStatus REQUEST_URI_TOO_LONG = new HttpResponseStatus(414, "Request-URI Too Long");
    public static final HttpResponseStatus UNSUPPORTED_MEDIA_TYPE = new HttpResponseStatus(415, "Unsupported Media Type");
    public static final HttpResponseStatus REQUESTED_RANGE_NOT_SATISFIABLE = new HttpResponseStatus(416, "Requested Range Not Satisfiable");
    public static final HttpResponseStatus EXPECTATION_FAILED = new HttpResponseStatus(417, "Expectation Failed");
    public static final HttpResponseStatus UNPROCESSABLE_ENTITY = new HttpResponseStatus(422, "Unprocessable Entity");
    public static final HttpResponseStatus LOCKED = new HttpResponseStatus(423, "Locked");
    public static final HttpResponseStatus FAILED_DEPENDENCY = new HttpResponseStatus(424, "Failed Dependency");
    public static final HttpResponseStatus UNORDERED_COLLECTION = new HttpResponseStatus(425, "Unordered Collection");
    public static final HttpResponseStatus UPGRADE_REQUIRED = new HttpResponseStatus(426, "Upgrade Required");
    public static final HttpResponseStatus PRECONDITION_REQUIRED = new HttpResponseStatus(428, "Precondition Required");
    public static final HttpResponseStatus TOO_MANY_REQUESTS = new HttpResponseStatus(429, "Too Many Requests");
    public static final HttpResponseStatus REQUEST_HEADER_FIELDS_TOO_LARGE = new HttpResponseStatus(431, "Request Header Fields Too Large");
    public static final HttpResponseStatus INTERNAL_SERVER_ERROR = new HttpResponseStatus(500, "Internal Server Error");
    public static final HttpResponseStatus NOT_IMPLEMENTED = new HttpResponseStatus(501, "Not Implemented");
    public static final HttpResponseStatus BAD_GATEWAY = new HttpResponseStatus(502, "Bad Gateway");
    public static final HttpResponseStatus SERVICE_UNAVAILABLE = new HttpResponseStatus(503, "Service Unavailable");
    public static final HttpResponseStatus GATEWAY_TIMEOUT = new HttpResponseStatus(504, "Gateway Timeout");
    public static final HttpResponseStatus HTTP_VERSION_NOT_SUPPORTED = new HttpResponseStatus(505, "HTTP Version Not Supported");
    public static final HttpResponseStatus VARIANT_ALSO_NEGOTIATES = new HttpResponseStatus(506, "Variant Also Negotiates");
    public static final HttpResponseStatus INSUFFICIENT_STORAGE = new HttpResponseStatus(507, "Insufficient Storage");
    public static final HttpResponseStatus NOT_EXTENDED = new HttpResponseStatus(510, "Not Extended");
    public static final HttpResponseStatus NETWORK_AUTHENTICATION_REQUIRED = new HttpResponseStatus(511, "Network Authentication Required");
    private final int code;
    private final String reasonPhrase;

    public static HttpResponseStatus valueOf(int i) {
        switch (i) {
            case 100:
                return HttpResponseStatus.CONTINUE;
            case 101:
                return HttpResponseStatus.SWITCHING_PROTOCOLS;
            case 102:
                return HttpResponseStatus.PROCESSING;
            case 200:
                return HttpResponseStatus.OK;
            case 201:
                return HttpResponseStatus.CREATED;
            case 202:
                return HttpResponseStatus.ACCEPTED;
            case 203:
                return HttpResponseStatus.NON_AUTHORITATIVE_INFORMATION;
            case 204:
                return HttpResponseStatus.NO_CONTENT;
            case 205:
                return HttpResponseStatus.RESET_CONTENT;
            case 206:
                return HttpResponseStatus.PARTIAL_CONTENT;
            case 207:
                return HttpResponseStatus.MULTI_STATUS;
            case 300:
                return HttpResponseStatus.MULTIPLE_CHOICES;
            case 301:
                return HttpResponseStatus.MOVED_PERMANENTLY;
            case 302:
                return HttpResponseStatus.FOUND;
            case 303:
                return HttpResponseStatus.SEE_OTHER;
            case 304:
                return HttpResponseStatus.NOT_MODIFIED;
            case 305:
                return HttpResponseStatus.USE_PROXY;
            case 307:
                return HttpResponseStatus.TEMPORARY_REDIRECT;
            case 400:
                return HttpResponseStatus.BAD_REQUEST;
            case 401:
                return HttpResponseStatus.UNAUTHORIZED;
            case 402:
                return HttpResponseStatus.PAYMENT_REQUIRED;
            case 403:
                return HttpResponseStatus.FORBIDDEN;
            case 404:
                return HttpResponseStatus.NOT_FOUND;
            case 405:
                return HttpResponseStatus.METHOD_NOT_ALLOWED;
            case 406:
                return HttpResponseStatus.NOT_ACCEPTABLE;
            case 407:
                return HttpResponseStatus.PROXY_AUTHENTICATION_REQUIRED;
            case 408:
                return HttpResponseStatus.REQUEST_TIMEOUT;
            case 409:
                return HttpResponseStatus.CONFLICT;
            case 410:
                return HttpResponseStatus.GONE;
            case 411:
                return HttpResponseStatus.LENGTH_REQUIRED;
            case 412:
                return HttpResponseStatus.PRECONDITION_FAILED;
            case 413:
                return HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE;
            case 414:
                return HttpResponseStatus.REQUEST_URI_TOO_LONG;
            case 415:
                return HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE;
            case 416:
                return HttpResponseStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
            case 417:
                return HttpResponseStatus.EXPECTATION_FAILED;
            case 422:
                return HttpResponseStatus.UNPROCESSABLE_ENTITY;
            case 423:
                return HttpResponseStatus.LOCKED;
            case 424:
                return HttpResponseStatus.FAILED_DEPENDENCY;
            case 425:
                return HttpResponseStatus.UNORDERED_COLLECTION;
            case 426:
                return HttpResponseStatus.UPGRADE_REQUIRED;
            case 428:
                return HttpResponseStatus.PRECONDITION_REQUIRED;
            case 429:
                return HttpResponseStatus.TOO_MANY_REQUESTS;
            case 431:
                return HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE;
            case 500:
                return HttpResponseStatus.INTERNAL_SERVER_ERROR;
            case 501:
                return HttpResponseStatus.NOT_IMPLEMENTED;
            case 502:
                return HttpResponseStatus.BAD_GATEWAY;
            case 503:
                return HttpResponseStatus.SERVICE_UNAVAILABLE;
            case 504:
                return HttpResponseStatus.GATEWAY_TIMEOUT;
            case 505:
                return HttpResponseStatus.HTTP_VERSION_NOT_SUPPORTED;
            case 506:
                return HttpResponseStatus.VARIANT_ALSO_NEGOTIATES;
            case 507:
                return HttpResponseStatus.INSUFFICIENT_STORAGE;
            case 510:
                return HttpResponseStatus.NOT_EXTENDED;
            case 511:
                return HttpResponseStatus.NETWORK_AUTHENTICATION_REQUIRED;
            default:
                String s;

                if (i < 100) {
                    s = "Unknown Status";
                } else if (i < 200) {
                    s = "Informational";
                } else if (i < 300) {
                    s = "Successful";
                } else if (i < 400) {
                    s = "Redirection";
                } else if (i < 500) {
                    s = "Client Error";
                } else if (i < 600) {
                    s = "Server Error";
                } else {
                    s = "Unknown Status";
                }

                return new HttpResponseStatus(i, s + " (" + i + ')');
        }
    }

    public HttpResponseStatus(int i, String s) {
        if (i < 0) {
            throw new IllegalArgumentException("code: " + i + " (expected: 0+)");
        } else if (s == null) {
            throw new NullPointerException("reasonPhrase");
        } else {
            int j = 0;

            while (j < s.length()) {
                char c0 = s.charAt(j);

                switch (c0) {
                    case '\n':
                    case '\r':
                        throw new IllegalArgumentException("reasonPhrase contains one of the following prohibited characters: \\r\\n: " + s);
                    default:
                        ++j;
                }
            }

            this.code = i;
            this.reasonPhrase = s;
        }
    }

    public int code() {
        return this.code;
    }

    public String reasonPhrase() {
        return this.reasonPhrase;
    }

    public int hashCode() {
        return this.code();
    }

    public boolean equals(Object object) {
        return !(object instanceof HttpResponseStatus) ? false : this.code() == ((HttpResponseStatus) object).code();
    }

    public int compareTo(HttpResponseStatus httpresponsestatus) {
        return this.code() - httpresponsestatus.code();
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder(this.reasonPhrase.length() + 5);

        stringbuilder.append(this.code);
        stringbuilder.append(' ');
        stringbuilder.append(this.reasonPhrase);
        return stringbuilder.toString();
    }
}
