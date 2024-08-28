package com.ericsson.eniq.techpacksdk.view.actionViews;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JPanel;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.component.ExceptionHandler;

public class DiskManagerActionViewTest extends BaseUnitTestX {

	DiskManagerActionView objUnderTest;

	private Meta_transfer_actions mockMetaTransferActions = null;

	// list of properties in string form with file age values set as numbers
	private final static String EXPECTED_ACTION_CONTENT = "#\n#Thu Nov 08 14:52:12 GMT 2012\ndiskManager.dir.fileAgeDay=65\ndiskManager.dir.timeMask=\ndiskManager.dir.fileAgeMinutes=0\ndiskManager.dir.fileAgeHour=0\ndiskManager.dir.outDir=\ndiskManager.dir.fileMask=\ndiskManager.dir.archiveMode=4\ndiskManager.dir.dateFormatOutput=\ndiskManager.dir.fileAgeMode=0\ndiskManager.dir.deleteEmptyDirectories=false\ndiskManager.dir.inDir=/eniq/log/servicesaudit/\ndiskManager.dir.dateFormatInput=\ndiskManager.dir.archivePrefix=\ndiskManager.dir.directoryDepth=5\n";

	// list of properties in string form with file age values set as static.properties keys
	private final static String EXPECTED_ACTION_CONTENT_WITH_PROPS = "#\n#Thu Nov 08 14:52:12 GMT 2012\ndiskManager.dir.fileAgeDay=serviceaudit.fileAgeDay:65\ndiskManager.dir.timeMask=\ndiskManager.dir.fileAgeMinutes=serviceaudit.fileAgeMinutes:0\ndiskManager.dir.fileAgeHour=serviceaudit.fileAgeHour:0\ndiskManager.dir.outDir=\ndiskManager.dir.fileMask=\ndiskManager.dir.archiveMode=4\ndiskManager.dir.dateFormatOutput=\ndiskManager.dir.fileAgeMode=0\ndiskManager.dir.deleteEmptyDirectories=false\ndiskManager.dir.inDir=/eniq/log/servicesaudit/\ndiskManager.dir.dateFormatInput=\ndiskManager.dir.archivePrefix=\ndiskManager.dir.directoryDepth=5\n";
	
	// list of properties in string form with file age values set as wrongly formatted static.properties keys
	private final static String EXPECTED_ACTION_CONTENT_WITH_INVALID_PROPS = "#\n#Thu Nov 08 14:52:12 GMT 2012\ndiskManager.dir.fileAgeDay=serviceaudit.fileAgeDay\ndiskManager.dir.timeMask=\ndiskManager.dir.fileAgeMinutes=serviceaudit.fileAgeMinutes:xyz\ndiskManager.dir.fileAgeHour=serviceaudit.fileAgeHour::0\ndiskManager.dir.outDir=\ndiskManager.dir.fileMask=\ndiskManager.dir.archiveMode=4\ndiskManager.dir.dateFormatOutput=\ndiskManager.dir.fileAgeMode=0\ndiskManager.dir.deleteEmptyDirectories=false\ndiskManager.dir.inDir=/eniq/log/servicesaudit/\ndiskManager.dir.dateFormatInput=\ndiskManager.dir.archivePrefix=\ndiskManager.dir.directoryDepth=5\n";

	private final static String EXPECTED_WHERE_CLAUSE = "";

	private final JPanel parent = new JPanel();

	@Before
	public void setUp() {
		recreateMockeryContext();
		mockMetaTransferActions = createMockMetaTransferActions("mockMetaTransferActionsName",  EXPECTED_ACTION_CONTENT);
		objUnderTest = new DiskManagerActionView(parent, mockMetaTransferActions);
	}

	@Test
	public void checkThatGetTypeReturnsCorrectString() {
		assertEquals("Diskmanager", objUnderTest.getType());
	}

	@Test
	public void checkThatValidateReturnsCorrectString() throws IOException {
		objUnderTest = new DiskManagerActionView(parent, null);
		assertEquals("", objUnderTest.validate());
	}

	@Test
	public void checkThatGetContentReturnsCorrectString() throws Exception {
		
		// convert expected and returned strings BACK to properties list to 
	    // simplify comparison (order of properties within string is not 
		// guaranteed
		Properties expectedProps = stringToProps(EXPECTED_ACTION_CONTENT);
		Properties returnedProps = stringToProps(objUnderTest.getContent());
		
		assertEquals(expectedProps, returnedProps);
	}

	@Test
	public void checkThatGetContentReturnsCorrectStringForStaticPropKeys() throws Exception {
		
		recreateMockeryContext();
		mockMetaTransferActions = createMockMetaTransferActions("mockMetaTransferActionsName",  EXPECTED_ACTION_CONTENT_WITH_PROPS);
		objUnderTest = new DiskManagerActionView(parent, mockMetaTransferActions);

		// convert expected and returned strings BACK to properties list to 
	    // simplify comparison (order of properties within string is not 
		// guaranteed
		Properties expectedProps = stringToProps(EXPECTED_ACTION_CONTENT_WITH_PROPS);
		Properties returnedProps = stringToProps(objUnderTest.getContent());
		
		assertEquals(expectedProps, returnedProps);
	}

	/**
	 * Convert string s to a properties object
	 * @param s String of properties
	 * @return Properties object created from s
	 */
	Properties stringToProps(String s) {
		Properties props = new Properties();

		if (s != null && s.length() > 0) {

			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
				props.load(bais);
				bais.close();
			} catch (Exception e) {
				ExceptionHandler.instance().handle(e);
				e.printStackTrace();
			}
		}
		
		return props;
	}

	@Test
	public void checkValidationReturnsCorrectValue() throws Exception {
		String validationErrors = objUnderTest.validate();
		assertEquals("", validationErrors);
	}
	
	
	@Test
	public void checkValidationForStaticPropKeysReturnsCorrectValue() throws Exception {
		
		recreateMockeryContext();
		mockMetaTransferActions = createMockMetaTransferActions("mockMetaTransferActionsName",  EXPECTED_ACTION_CONTENT_WITH_PROPS);
		objUnderTest = new DiskManagerActionView(parent, mockMetaTransferActions);

		String validationErrors = objUnderTest.validate();
		assertEquals("", validationErrors);
	}

	@Test
	public void checkValidationForBadStaticPropKeysReturnsCorrectValue() throws Exception {
		
		recreateMockeryContext();
		mockMetaTransferActions = createMockMetaTransferActions("mockMetaTransferActionsName",  EXPECTED_ACTION_CONTENT_WITH_INVALID_PROPS);
		objUnderTest = new DiskManagerActionView(parent, mockMetaTransferActions);

		String expectedErrors = DiskManagerActionView.INVALID_AGE_DAYS_FIELD_ERROR +
								DiskManagerActionView.INVALID_AGE_HOURS_FIELD_ERROR +
								DiskManagerActionView.INVALID_AGE_MINS_FIELD_ERROR;
		String validationErrors = objUnderTest.validate();
		assertEquals(expectedErrors, validationErrors);
	}

	@Test
	public void checkThatGetWhereReturnsCorrectString() throws Exception {
		assertTrue(objUnderTest.getWhere().equals(""));
	}

	@Test
	public void checkThatIsChangedReturnsTrue() {
		assertEquals(true, objUnderTest.isChanged());
	}

	@Test
	public void isValidAgeFieldReturnsTrueForValidContent() {
		assertTrue(objUnderTest.isValidAgeField("0"));
		assertTrue(objUnderTest.isValidAgeField("65"));
		assertTrue(objUnderTest.isValidAgeField("serviceAudit.fileAgeDay:65"));
		assertTrue(objUnderTest.isValidAgeField("service_audit.file_age_day:0"));
		assertTrue(objUnderTest.isValidAgeField("service_audit.fileAge.day:123"));
		assertTrue(objUnderTest.isValidAgeField("service_audit.fileAge999.day:123"));
		assertTrue(objUnderTest.isValidAgeField("service_audit.fileAge123:123"));
	}

	@Test
	public void isValidAgeFieldReturnsFalseForInvalidContent() {
		assertFalse(objUnderTest.isValidAgeField(""));
		assertFalse(objUnderTest.isValidAgeField(null));
		assertFalse(objUnderTest.isValidAgeField("service_audit.fileAge.day"));
		assertFalse(objUnderTest.isValidAgeField("999service_audit.fileAge.day"));
		assertFalse(objUnderTest.isValidAgeField("service_audit.fileAge."));
		assertFalse(objUnderTest.isValidAgeField(".service_audit.fileAge"));
		assertFalse(objUnderTest.isValidAgeField("_service_audit.fileAge"));
		assertFalse(objUnderTest.isValidAgeField("service_audit.fileAge_"));
	}
	
	private Meta_transfer_actions createMockMetaTransferActions(final String name, final String expectedContent) {
		
		mockMetaTransferActions = context.mock(Meta_transfer_actions.class, name);
		
		context.checking(new Expectations() {

			{
				allowing(mockMetaTransferActions).getAction_contents();
				will(returnValue(expectedContent));
			}
		});
		
		return mockMetaTransferActions;
	}


}
