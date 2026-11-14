package com.google.common.xml;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;

@Beta
@GwtCompatible
public class XmlEscapers {

    private static final char MIN_ASCII_CONTROL_CHAR = '\u0000';
    private static final char MAX_ASCII_CONTROL_CHAR = '\u001f';
    private static final Escaper XML_ESCAPER;
    private static final Escaper XML_CONTENT_ESCAPER;
    private static final Escaper XML_ATTRIBUTE_ESCAPER;

    private XmlEscapers() {}

    public static Escaper xmlContentEscaper() {
        return XmlEscapers.XML_CONTENT_ESCAPER;
    }

    public static Escaper xmlAttributeEscaper() {
        return XmlEscapers.XML_ATTRIBUTE_ESCAPER;
    }

    static {
        Escapers.Builder escapers_builder = Escapers.builder();

        escapers_builder.setSafeRange('\u0000', '\uffff');
        escapers_builder.setUnsafeReplacement("");

        for (char c0 = 0; c0 <= 31; ++c0) {
            if (c0 != '\t' && c0 != '\n' && c0 != '\r') {
                escapers_builder.addEscape(c0, "");
            }
        }

        escapers_builder.addEscape('&', "&amp;");
        escapers_builder.addEscape('<', "&lt;");
        escapers_builder.addEscape('>', "&gt;");
        XML_CONTENT_ESCAPER = escapers_builder.build();
        escapers_builder.addEscape('\'', "&apos;");
        escapers_builder.addEscape('"', "&quot;");
        XML_ESCAPER = escapers_builder.build();
        escapers_builder.addEscape('\t', "&#x9;");
        escapers_builder.addEscape('\n', "&#xA;");
        escapers_builder.addEscape('\r', "&#xD;");
        XML_ATTRIBUTE_ESCAPER = escapers_builder.build();
    }
}
