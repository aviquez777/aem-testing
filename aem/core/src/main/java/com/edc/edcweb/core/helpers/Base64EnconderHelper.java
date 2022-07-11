package com.edc.edcweb.core.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h1>Base64EnconderHelper</h1> The Base64EnconderHelper class receives a
 * string and returns its encoded or decoded string, null if no string provided
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class Base64EnconderHelper {
    private static final Logger log = LoggerFactory.getLogger(Base64EnconderHelper.class);

    @Inject
    @Optional
    private String toEncode;

    @Inject
    @Optional
    private String toDecode;

    private String encoded;

    private String decoded;

    /**
     * This is the init method which reads the node when invoked, reads the toEncode
     * /toDecode and return the values accordingly
     *
     * @param String toEncode /toDecode ;.
     * @return Nothing.
     */

    @PostConstruct
    protected void init() {
        if (StringUtils.isNotBlank(toEncode)) {
            encoded = encodeString(toEncode);
        }

        if (StringUtils.isNotBlank(toDecode)) {
            decoded = decodeString(toDecode);
        }
    }

    /**
     * Sling model getter
     * 
     * @return encoded string, null if no string provided
     */
    public String getEncoded() {
        return encoded;
    }

    /**
     * Sling model getter
     * 
     * @return decoded string, null if no string provided
     */
    public String getDecoded() {
        return decoded;
    }

    /**
     * Helper encoder function to be used on a Java Class
     * 
     * @param toEncodeString String to encode
     * @return encoded string, null if no string provided
     */
    public static String encodeString(String toEncodeString) {
        return Base64.getEncoder().encodeToString(toEncodeString.getBytes());
    }

    /**
     * Helper decoder function to be used on a Java Class
     * 
     * @param encodedString string to decode
     * @return decoded string, null if no string provided
     */
    public static String decodeString(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes);
    }

    /**
     * Helper decoder function to be used on a Java Class
     * 
     * @param encodedUrl
     * @param charset
     * @return Decoded URL using provided charset, if error, return original string
     */
    public static String decodeString(String encodedUrl, Charset charset) {
        try {
            encodedUrl = URLDecoder.decode(encodedUrl, charset.name());
        } catch (NullPointerException | IllegalArgumentException | UnsupportedEncodingException e) {
            String erroMessage = "Error decoding url ".concat(encodedUrl).concat(" charset ").concat(charset.name());
            log.error("Base64EncoderHelper decodeURL()  {} ", erroMessage, e);
        }
        return encodedUrl;
    }

}