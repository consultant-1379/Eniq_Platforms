/**
 * 
 */
package com.ericsson.catalina.users;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

import com.ericsson.catalina.users.AsciiCrypter;


/**
 * @author eheijun
 *
 */
public class AsciiCrypterTest {

  private static final String CRYPT_STAMP = "{AsciiCrypter}";

  /**
   * Test method for {@link org.apache.catalina.users.Password#encrypt(java.lang.String)}.
   * @throws Exception 
   */
  @Test
  public void testEncryptAndDecrypt() throws Exception {
    String originalPassword = "admin";
    String encryptedPassword = AsciiCrypter.getInstance().encrypt(originalPassword);
    assertTrue(encryptedPassword.startsWith(CRYPT_STAMP));
    String decryptedPassword = AsciiCrypter.getInstance().decrypt(encryptedPassword);
    assertTrue(originalPassword.equals(decryptedPassword));
  }

  /**
   * Test method for {@link org.apache.catalina.users.Password#decrypt(java.lang.String)}.
   * @throws Exception 
   */
  @Test
  public void testDecryptAndEncrypt() throws Exception {
    String encryptedPassword = CRYPT_STAMP + "BbiKN+dVg/U=";
    String decryptedPassword = AsciiCrypter.getInstance().decrypt(encryptedPassword);
    assertFalse(encryptedPassword.equals(decryptedPassword));
    String originalPassword = AsciiCrypter.getInstance().encrypt(decryptedPassword);
    assertTrue(encryptedPassword.equals(originalPassword));
  }

  /**
   * Test method for {@link org.apache.catalina.users.Password#decrypt(java.lang.String)}.
   * @throws NoSuchPaddingException 
   * @throws NoSuchAlgorithmException 
   * @throws BadPaddingException 
   * @throws IllegalBlockSizeException 
   * @throws UnsupportedEncodingException 
   * @throws InvalidKeyException 
   */
  @Test(expected=NullPointerException.class)
  public void testEncryptFail() throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
    String originalPassword = null;
    AsciiCrypter.getInstance().encrypt(originalPassword);
  }

  /**
   * Test method for {@link org.apache.catalina.users.Password#decrypt(java.lang.String)}.
   * @throws Exception 
   */
  @Test(expected=NullPointerException.class)
  public void testDecryptFail() throws Exception {
    String encryptedPassword = null;
    AsciiCrypter.getInstance().decrypt(encryptedPassword);
  }

}
