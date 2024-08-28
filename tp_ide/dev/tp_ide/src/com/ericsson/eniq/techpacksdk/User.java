package com.ericsson.eniq.techpacksdk;

import java.io.File;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.install.ant.ZipCrypter;
import com.distocraft.dc5000.repository.dwhrep.Useraccount;
import com.ericsson.eniq.techpacksdk.common.Utils;

public class User {

  public static final String ADMIN = "Admin";

  public static final String RND = "RnD";

  public static final String USER = "User";

  public static final String NONE = "None";

  public static final String DO_USERADMIN = "UserAdmin";

  public static final String DO_BASETP = "BaseTP";

  public static final String DO_PRODUCTTP = "ProductTP";

  public static final String DO_CUSTOMTP = "CustomTP";

  public static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

  final private Logger log = Logger.getLogger(User.class.getName());

  private final Useraccount ua;

  private final long lastLoginTime;

  private final String role;

  private BigInteger privateKeyMod = null;

  private BigInteger privateKeyExp = null;
  
  User(final Useraccount ua) {
    this.ua = ua;

    Timestamp ts = ua.getLastlogin();

    verifyPrivateKey();

    String r = ua.getRole();

    if (r.equals(ADMIN)) {
      role = ADMIN;
    } else if (r.equals(RND)) {
      if (privateKeyMod != null && privateKeyExp != null) {
        role = RND;
      } else {
        log.warning("No valid private key. RnD priviledges denied.");
        role = USER;
      }
    } else if (r.equals(USER)) {
      role = USER;
    } else {
      role = NONE;
    }

    if (ts != null) {
      log.info("User " + ua.getName() + " logged in as " + getRole() + ", last login " + ua.getLastlogin());
    } else {
      log.info("User " + ua.getName() + " logged in as " + getRole() + ", first login");
    }

    // Storing this because after consruction lastlogin is updated in
    // Useraccount
    lastLoginTime = getLoginTime();

  }

  public String getName() {
    return (ua == null) ? null : ua.getName();
  }

  public String getRole() {
    return role;
  }

  public long getLastLoginTime() {
    return lastLoginTime;
  }

  public long getLoginTime() {
    return (ua.getLastlogin() == null) ? -1 : ua.getLastlogin().getTime();
  }

  public boolean authorize(final String action) {

    if (role == ADMIN && action == DO_USERADMIN) {
      return true;
    } else if (role == RND && (action == DO_BASETP || action == DO_PRODUCTTP || action == DO_CUSTOMTP)) {
      return true;
    } else if (role == USER && action == DO_CUSTOMTP) {
      return true;
    } else {
      return false;
    }

  }

  public BigInteger getPrivateKeyMod() {
    return privateKeyMod;
  }
  
  public BigInteger getPrivateKeyExp() {
    return privateKeyExp;
  }

  private void verifyPrivateKey() {
    try {

      final Properties props = Utils.getProperties(TechPackIDE.CONPROPS_FILE);

      final String keyfilePrivate = props.getProperty("keyfile.private", "private.key");

      final File pKeyFile = new File(keyfilePrivate);
      if (!pKeyFile.exists() || !pKeyFile.isFile() || !pKeyFile.canRead()) {
    	  // Along with the TR Fix :: HN54220 (EANGUAN) :: To make the log message more clear
        log.fine("Private key file " + keyfilePrivate + " is not defined or cannot be read. Looking for file: " + pKeyFile.getCanonicalPath());
        return;
      }

      final Properties keys = Utils.getProperties(keyfilePrivate);

      final BigInteger keymodulate = new BigInteger(keys.getProperty("keymodulate"));
      final BigInteger keyexponent = new BigInteger(keys.getProperty("keyexponent"));

      final PrivateKey privKey = ZipCrypter.getPrivateKey(keymodulate, keyexponent);

      final PublicKey pubKey = ZipCrypter.getPublicKey(ZipCrypter.DEFAULT_KEY_MOD, ZipCrypter.DEFAULT_KEY_EXP);

      if (ZipCrypter.isValidKeyPair(pubKey, privKey)) {
        privateKeyMod = keymodulate;
        privateKeyExp = keyexponent;
      } else {
        log.info("Private key is invalid");
        return;
      }

    } catch (Exception e) {
      log.log(Level.INFO, "Private key check failed.", e);
    }

  }

}
