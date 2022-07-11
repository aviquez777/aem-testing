package com.edc.edcweb.core.helpers;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = SlingHttpServletRequest.class)
public class EncryptUtils {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    private static final Logger log = LoggerFactory.getLogger(EncryptUtils.class);

    @Inject
    @Optional
    private String toEncrypt;

    @Inject
    @Optional
    private String toDecrypt;

    private String encrypted;

    private String decrypted;

    private static SecretKey secretKey = null;
    private static IvParameterSpec ivParameterSpec = null;

    @PostConstruct
    protected void init() {
        initialize();
        if (StringUtils.isNotBlank(toEncrypt)) {
            encrypted = encryptString(toEncrypt);
        }
        if (StringUtils.isNotBlank(toDecrypt)) {
            decrypted = decryptString(toDecrypt);
        }
    }

    public String getEncrypted() {
        return encrypted;
    }

    public String getDecrypted() {
        return decrypted;
    }

    public static String encryptString(String toEncrypt) {
        initialize();
        String encryptedStr = null;
        try {
            encryptedStr = encrypt(toEncrypt);
            ;
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
            log.error("EncryptUtils Error encryptString {}", toEncrypt, e);
        }
        return encryptedStr;
    }

    public static String decryptString(String toDecrypt) {
        initialize();
        String decryptedStr = null;
        try {
            decryptedStr = decrypt(toDecrypt);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
            log.error("EncryptUtils Error decryptString {}", toDecrypt, e);
        }
        return decryptedStr;
    }

    private static void initialize() {
        if (ivParameterSpec == null) {
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            ivParameterSpec = new IvParameterSpec(iv);
        }
        if (secretKey == null) {
            KeyGenerator keyGenerator;
            try {
                keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(256);
                secretKey = keyGenerator.generateKey();
            } catch (NoSuchAlgorithmException e) {
                log.error("Secret Key error", e);
            }
        }
    }

    private static String encrypt(String toEncrypt) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] cipherText = cipher.doFinal(toEncrypt.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String toDecreypt) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(toDecreypt)));
    }
}
