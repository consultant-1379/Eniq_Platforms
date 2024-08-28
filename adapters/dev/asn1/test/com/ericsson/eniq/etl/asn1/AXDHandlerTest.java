package com.ericsson.eniq.etl.asn1;

import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.MeasurementFileImpl;
import com.distocraft.dc5000.etl.parser.ParseSession;
import com.distocraft.dc5000.etl.parser.ParserDebugger;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.distocraft.dc5000.etl.parser.Transformer;
import com.distocraft.dc5000.etl.parser.TransformerCache;
import com.distocraft.dc5000.repository.cache.DFormat;
import com.distocraft.dc5000.repository.cache.DItem;
import com.distocraft.dc5000.repository.cache.DataFormatCache;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import junit.framework.JUnit4TestAdapter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ssc.rockfactory.RockFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This class contains misc set of unit tests for the AXD parser.
 *
 * @author eheitur
 */
public class AXDHandlerTest {

  /**
   * Testing Object Identifier conversion from bytes to a dotted syntax OID
   * string.
   */
  @Test
  public void testDoOid() {
    AXDHandler ah = new AXDHandler();

    // Using OID hex: 2B 06 01 04 01 81 41 0E 01 02 02 01 02 01 03
    byte[] b = HexBin.decode("2B0601040181411303020201030103");

    try {
      String str = ah.doOid(b);
      assertEquals("1.3.6.1.4.1.193.19.3.2.2.1.3.1.3", str);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testDoOid() failed.");
    }

  }

  /**
   * Testing octet string to string conversion
   */
  @Test
  public void testDoString_with_OctectString() {
    AXDHandler ah = new AXDHandler();

    // Using IP Address hex: AC1FF802
    // byte[] b = HexBin.decode("AC1FF802");
    byte[] b = new byte[]{(byte) 172, (byte) 31, (byte) 248, (byte) 2};
    try {
      ASN1Rule rule = new ASN1Rule();
      rule.type = "OCTET STRING";
      String str = ah.doString(rule, b);
      assertEquals("AC1FF802", str);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testDoString_with_OctectString() failed.");
    }

  }


  /**
   * Testing date time id from time stamp
   */
  @Test
  public void testVendorTagConversion() {

    String measType = "abcdefghijklmnopqrstuvwxyz_abcdefghijklmnopqrstuvwxyz";
    String vendorTag = measType.substring(0, 50);
    assertEquals("abcdefghijklmnopqrstuvwxyz_abcdefghijklmnopqrstuvw", vendorTag);

    measType = "abcdefghijklmnopqrstuvwxyz_abcdefghijklmnopqrstu";
    vendorTag = measType;
    assertEquals("abcdefghijklmnopqrstuvwxyz_abcdefghijklmnopqrstu", vendorTag);

    measType = "1111111111_222222222_333333333_444444444_555555555_666666666";
    vendorTag = measType.substring(0, 50);
    assertEquals("1111111111_222222222_333333333_444444444_555555555", vendorTag);

    measType = "123456789     ";
    vendorTag = measType.substring(0, 10);
    System.out.println("'" + vendorTag + "'");
    assertEquals("123456789 ", vendorTag);

  }


  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(AXDHandlerTest.class);
  }
}
