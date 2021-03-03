package it.uniparthenope.fairwind.model.impl;

/**
 * Created by raffaelemontella on 18/05/2017.
 */

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import mjson.Json;


public class SSL {

    private final static String HEX = "0123456789ABCDEF";
    private final static String ENC = "US-ASCII";
    private final static int ITERATION = 1337;

    private static final String RANDOM_ALGORITHM = "PBEWithSHA256And256BitAES-CBC-BC";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM = "AES";

    private String session;

    private static final String header = "com.fairwindsystem.licensing.AESObfuscator-1|";
    private static final byte[] IV =
            { 16, 74, 71, -80, 32, 101, -47, 72, 117, -14, 0, -29, 70, 65, -12, 74 };

    private IvParameterSpec ips;

    public SSL(String session) {
        this.session=session;
        ips = new IvParameterSpec(IV);

        Security.addProvider(new BouncyCastleProvider());
    }

    public byte[] getCertificate() {
        return ips.getIV();
    }

    public String obfuscate(Json json, String key) throws GeneralSecurityException {
        String jsonAsString=json.toString();
        return encrypt(header + key + jsonAsString);
    }

    public Json unobfuscate(String obfuscated, String key) throws ValidationException {
        // Check for presence of header. This serves as a final integrity check, for cases
        // where the block size is correct during decryption.

        try {
            String result = decrypt(obfuscated);
            int headerIndex = result.indexOf(header + key);
            if (headerIndex != 0) {
                throw new ValidationException("Header not found (invalid data or key)" + ":" +
                        obfuscated);
            }
            String jsonAsString = result.substring(header.length() + key.length(), result.length());
            return Json.read(jsonAsString);
        } catch (GeneralSecurityException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }

    public String encrypt(String cleartext) throws GeneralSecurityException {
        byte[] rawKey = new byte[0];
        try {
            rawKey = getRawKey(session.toCharArray());
            byte[] result = encrypt(rawKey, cleartext.getBytes(ENC));
            return toHex(result);
        } catch (NoSuchAlgorithmException ex) {
            throw new GeneralSecurityException(ex);
        } catch (InvalidKeySpecException ex) {
            throw new GeneralSecurityException(ex);
        } catch (NoSuchPaddingException ex) {
            throw new GeneralSecurityException(ex);
        } catch (InvalidKeyException ex) {
            throw new GeneralSecurityException(ex);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new GeneralSecurityException(ex);
        } catch (IllegalBlockSizeException ex) {
            throw new GeneralSecurityException(ex);
        } catch (BadPaddingException ex) {
            throw new GeneralSecurityException(ex);
        } catch (UnsupportedEncodingException ex) {
            throw new GeneralSecurityException(ex);
        }
    }

    public String decrypt(String encrypted) throws GeneralSecurityException {
        byte[] rawKey = new byte[0];
        try {
            rawKey = getRawKey(session.toCharArray());
            byte[] enc = toByte(encrypted);
            byte[] result = decrypt(rawKey, enc);
            String decrypted=new String(result, ENC);
            return decrypted;
        } catch (NoSuchAlgorithmException ex) {
            throw new GeneralSecurityException(ex);
        } catch (InvalidKeySpecException ex) {
            throw new GeneralSecurityException(ex);
        } catch (NoSuchPaddingException ex) {
            throw new GeneralSecurityException(ex);
        } catch (InvalidKeyException ex) {
            throw new GeneralSecurityException(ex);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new GeneralSecurityException(ex);
        } catch (IllegalBlockSizeException ex) {
            throw new GeneralSecurityException(ex);
        } catch (BadPaddingException ex) {
            throw new GeneralSecurityException(ex);
        } catch (UnsupportedEncodingException ex) {
            throw new GeneralSecurityException(ex);
        }
    }

    private byte[] getRawKey(char[] seed) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec keySpec = new PBEKeySpec(seed, ips.getIV(), ITERATION);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(RANDOM_ALGORITHM);
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        return secretKey.getEncoded();
    }


    private byte[] encrypt(byte[] raw, byte[] clear) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, SECRET_KEY_ALGORITHM);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ips);
        byte[] encrypted = cipher.doFinal(clear);

        return encrypted;
    }

    private byte[] decrypt(byte[] raw, byte[] encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, SECRET_KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ips);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public String toHex(String txt) throws UnsupportedEncodingException {
        return toHex(txt.getBytes(ENC));
    }
    public String fromHex(String hex) throws UnsupportedEncodingException {
        return new String(toByte(hex), ENC);
    }

    public byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    public String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }

    public static void main(String[] args) throws Exception {
        SSL ssl=new SSL("session");
        String s=ssl.encrypt("Raffaele");
        System.out.println(s);
    }
}

