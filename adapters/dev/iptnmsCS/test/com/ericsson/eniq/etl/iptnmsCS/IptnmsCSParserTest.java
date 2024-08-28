package com.ericsson.eniq.etl.iptnmsCS;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author eneacon
 * 
 */
public class IptnmsCSParserTest {

	IptnmsCSParser objectUnderTest;

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
			mainParserObject = IptnmsCSParser.class
					.getDeclaredField("mainParserObject");
			techPack = IptnmsCSParser.class.getDeclaredField("techPack");
			setType = IptnmsCSParser.class.getDeclaredField("setType");
			setName = IptnmsCSParser.class.getDeclaredField("setName");
			status = IptnmsCSParser.class.getDeclaredField("status");
			workerName = IptnmsCSParser.class.getDeclaredField("workerName");
			readVendorIDFrom = IptnmsCSParser.class
					.getDeclaredField("readVendorIDFrom");
			objectMask = IptnmsCSParser.class.getDeclaredField("objectMask");

			objectClass = IptnmsCSParser.class.getDeclaredField("objectClass");

			measFile = IptnmsCSParser.class.getDeclaredField("measFile");

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
		IptnmsCSParser ap = new IptnmsCSParser();

		assertEquals(0, ap.status());
	}

	@Test
	public void testInit2() {
		IptnmsCSParser ap = new IptnmsCSParser();

		try {
			ap.init(null, "tp", "st", "sn", null);
			fail("shouldn't execute this line, nullpointerException expected");

		} catch (Exception e) {
			// test passed
		}
	}

}