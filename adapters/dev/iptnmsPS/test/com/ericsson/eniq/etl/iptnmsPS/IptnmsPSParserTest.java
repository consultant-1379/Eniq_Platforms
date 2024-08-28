package com.ericsson.eniq.etl.iptnmsPS;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.distocraft.dc5000.etl.parser.MeasurementFileImpl;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.ericsson.eniq.etl.iptnmsPS.IptnmsPSParser;


/**
 * @author eneacon
 * 
 */
public class IptnmsPSParserTest {

	IptnmsPSParser objectUnderTest;

	private static Field mainParserObject;
	private static Field setType;
	private static Field setName;
	private static Field techPack;
	private static Field status;
	private static Field workerName;

	private static Field measFile;

	private static Field readVendorIDFrom;
	private static Field objectMask;
	private static Field objectClass;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	public static void init() {
		try {

			/* Reflecting fields and methods from the tested class */
			mainParserObject = IptnmsPSParser.class
					.getDeclaredField("mainParserObject");
			techPack = IptnmsPSParser.class.getDeclaredField("techPack");
			setType = IptnmsPSParser.class.getDeclaredField("setType");
			setName = IptnmsPSParser.class.getDeclaredField("setName");
			status = IptnmsPSParser.class.getDeclaredField("status");
			workerName = IptnmsPSParser.class.getDeclaredField("workerName");
			readVendorIDFrom = IptnmsPSParser.class
					.getDeclaredField("readVendorIDFrom");
			objectMask = IptnmsPSParser.class.getDeclaredField("objectMask");

			objectClass = IptnmsPSParser.class.getDeclaredField("objectClass");

			measFile = IptnmsPSParser.class.getDeclaredField("measFile");

			mainParserObject.setAccessible(true);
			techPack.setAccessible(true);
			setType.setAccessible(true);
			setName.setAccessible(true);
			status.setAccessible(true);
			workerName.setAccessible(true);
			readVendorIDFrom.setAccessible(true);
			objectMask.setAccessible(true);

			objectClass.setAccessible(true);

			measFile.setAccessible(true);

		} catch (Exception e) {
			e.printStackTrace();
			fail("");
		}

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStatus() {
		IptnmsPSParser ap = new IptnmsPSParser();

		assertEquals(0, ap.status());
	}

	@Test
	public void testInit2() {
		IptnmsPSParser ap = new IptnmsPSParser();

		try {
			ap.init(null, "tp", "st", "sn", null);
			fail("shouldn't execute this line, nullpointerException expected");

		} catch (Exception e) {
			// test passed
		}
	}

}