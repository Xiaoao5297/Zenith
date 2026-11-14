package com.mojang.authlib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class HttpAuthenticationService extends BaseAuthenticationService {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Proxy proxy;

    protected HttpAuthenticationService(Proxy proxy) {
        Validate.notNull(proxy);
        this.proxy = proxy;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    protected HttpURLConnection createUrlConnection(URL url) throws IOException {
        Validate.notNull(url);
        HttpAuthenticationService.LOGGER.debug("Opening connection to " + url);
        HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection(this.proxy);

        httpurlconnection.setConnectTimeout(15000);
        httpurlconnection.setReadTimeout(15000);
        httpurlconnection.setUseCaches(false);
        return httpurlconnection;
    }

    public String performPostRequest(URL url, String s, String s1) throws IOException {
        Validate.notNull(url);
        Validate.notNull(s);
        Validate.notNull(s1);
        HttpURLConnection httpurlconnection = this.createUrlConnection(url);
        byte[] abyte = s.getBytes(Charsets.UTF_8);

        httpurlconnection.setRequestProperty("Content-Type", s1 + "; charset=utf-8");
        httpurlconnection.setRequestProperty("Content-Length", "" + abyte.length);
        httpurlconnection.setDoOutput(true);
        HttpAuthenticationService.LOGGER.debug("Writing POST data to " + url + ": " + s);
        OutputStream outputstream = null;

        try {
            outputstream = httpurlconnection.getOutputStream();
            IOUtils.write(abyte, outputstream);
        } finally {
            IOUtils.closeQuietly(outputstream);
        }

        HttpAuthenticationService.LOGGER.debug("Reading data from " + url);
        InputStream inputstream = null;

        String s2;

        try {
            String s3;

            try {
                inputstream = httpurlconnection.getInputStream();
                String s4 = IOUtils.toString(inputstream, Charsets.UTF_8);

                HttpAuthenticationService.LOGGER.debug("Successful read, server response was " + httpurlconnection.getResponseCode());
                HttpAuthenticationService.LOGGER.debug("Response: " + s4);
                s3 = s4;
                return s3;
            } catch (IOException ioexception) {
                IOUtils.closeQuietly(inputstream);
                inputstream = httpurlconnection.getErrorStream();
                if (inputstream == null) {
                    HttpAuthenticationService.LOGGER.debug("Request failed", (Throwable) ioexception);
                    throw ioexception;
                }

                HttpAuthenticationService.LOGGER.debug("Reading error page from " + url);
                s3 = IOUtils.toString(inputstream, Charsets.UTF_8);
                HttpAuthenticationService.LOGGER.debug("Successful read, server response was " + httpurlconnection.getResponseCode());
                HttpAuthenticationService.LOGGER.debug("Response: " + s3);
                s2 = s3;
            }
        } finally {
            IOUtils.closeQuietly(inputstream);
        }

        return s2;
    }

    public String performGetRequest(URL url) throws IOException {
        Validate.notNull(url);
        HttpURLConnection httpurlconnection = this.createUrlConnection(url);

        HttpAuthenticationService.LOGGER.debug("Reading data from " + url);
        InputStream inputstream = null;

        String s;

        try {
            String s1;

            try {
                inputstream = httpurlconnection.getInputStream();
                String s2 = IOUtils.toString(inputstream, Charsets.UTF_8);

                HttpAuthenticationService.LOGGER.debug("Successful read, server response was " + httpurlconnection.getResponseCode());
                HttpAuthenticationService.LOGGER.debug("Response: " + s2);
                s1 = s2;
                return s1;
            } catch (IOException ioexception) {
                IOUtils.closeQuietly(inputstream);
                inputstream = httpurlconnection.getErrorStream();
                if (inputstream == null) {
                    HttpAuthenticationService.LOGGER.debug("Request failed", (Throwable) ioexception);
                    throw ioexception;
                }

                HttpAuthenticationService.LOGGER.debug("Reading error page from " + url);
                s1 = IOUtils.toString(inputstream, Charsets.UTF_8);
                HttpAuthenticationService.LOGGER.debug("Successful read, server response was " + httpurlconnection.getResponseCode());
                HttpAuthenticationService.LOGGER.debug("Response: " + s1);
                s = s1;
            }
        } finally {
            IOUtils.closeQuietly(inputstream);
        }

        return s;
    }

    public static URL constantURL(String s) {
        try {
            return new URL(s);
        } catch (MalformedURLException malformedurlexception) {
            throw new Error("Couldn't create constant for " + s, malformedurlexception);
        }
    }

    public static String buildQuery(Map<String, Object> map) {
        if (map == null) {
            return "";
        } else {
            StringBuilder stringbuilder = new StringBuilder();
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                if (stringbuilder.length() > 0) {
                    stringbuilder.append('&');
                }

                try {
                    stringbuilder.append(URLEncoder.encode((String) entry.getKey(), "UTF-8"));
                } catch (UnsupportedEncodingException unsupportedencodingexception) {
                    HttpAuthenticationService.LOGGER.error("Unexpected exception building query", (Throwable) unsupportedencodingexception);
                }

                if (entry.getValue() != null) {
                    stringbuilder.append('=');

                    try {
                        stringbuilder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                    } catch (UnsupportedEncodingException unsupportedencodingexception1) {
                        HttpAuthenticationService.LOGGER.error("Unexpected exception building query", (Throwable) unsupportedencodingexception1);
                    }
                }
            }

            return stringbuilder.toString();
        }
    }

    public static URL concatenateURL(URL url, String s) {
        try {
            return url.getQuery() != null && url.getQuery().length() > 0 ? new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "&" + s) : new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "?" + s);
        } catch (MalformedURLException malformedurlexception) {
            throw new IllegalArgumentException("Could not concatenate given URL with GET arguments!", malformedurlexception);
        }
    }
}
