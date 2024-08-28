package com.ericsson.catalina.users;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.math.BigInteger;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


final public class AsciiCrypter {

  private final static String CRYPT_STAMP_LABEL = "AsciiCrypter";
  
  public static final String CRYPT_STAMP = "{" + CRYPT_STAMP_LABEL + "}";
  
  private final static BigInteger BIKEY = new BigInteger("8842165122180440050");

  private final static String ALGO = "DES";

  private final transient Cipher ecipher;

  private final transient Cipher dcipher;

  private static AsciiCrypter crypter = null;

  private AsciiCrypter() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
    final SecretKey keySpec = new SecretKeySpec(BIKEY.toByteArray(), ALGO);
    ecipher = Cipher.getInstance(ALGO);
    dcipher = Cipher.getInstance(ALGO);
    ecipher.init(Cipher.ENCRYPT_MODE, keySpec);
    dcipher.init(Cipher.DECRYPT_MODE, keySpec);
  }

  /**
   * 
   * Gets the instance of this singleton class
   * 
   * @return
   * 
   * @throws Exception
   */

  public static AsciiCrypter getInstance() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
    synchronized (AsciiCrypter.class) {
      if (crypter == null) {
        crypter = new AsciiCrypter();
      }
    }
    return crypter;
  }

  public String encrypt(final String str) throws UnsupportedEncodingException, IllegalBlockSizeException,
      BadPaddingException {
    // Encode the string into bytes using utf-8
    final byte[] utf8 = str.getBytes("UTF8");
    // Encrypt
    final byte[] enc = ecipher.doFinal(utf8);
    // Encode bytes to base64 to get a string
    final String retVal = CRYPT_STAMP + new sun.misc.BASE64Encoder().encode(enc);
    return retVal;
  }

  public String decrypt(final String str) throws IOException, IllegalBlockSizeException, BadPaddingException {
    String retVal = null;
    // Decode base64 to get bytes
    final byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str.replaceFirst("\\{" + CRYPT_STAMP_LABEL + "\\}", ""));
    // Decrypt
    final byte[] utf8 = dcipher.doFinal(dec);
    // Decode using utf-8
    retVal = new String(utf8, "UTF8");
    return retVal;
  }

}
