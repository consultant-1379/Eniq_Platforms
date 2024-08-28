package com.ericsson.eniq.licensing.cache;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;



public class FeatureMappingTest {

  @Test
  public void checkThatFeatureDescriptionInfoIsCorrect() throws IOException {
    String featureName = "CXC4010583";
    String featureDescription = "Ericsson Network IQ Starter";
    String fajNumber = "FAJ 121 1137";

    FeatureMapping featureMapping = new FeatureMapping(featureName + "::" + featureDescription + "::" + fajNumber);

    assertEquals(featureName, featureMapping.getFeatureName());
    assertEquals(featureDescription, featureMapping.getDescription());
    assertEquals(fajNumber, featureMapping.getFajNumber());

    assertThat(featureMapping.getInterfaceName(), is((String) null));
    assertThat(featureMapping.getReportPackage(), is((String) null));
  }

  @Test
  public void checkThatFeatureTechPackInfoIsCorrect() throws IOException {
    String featureName = "CXC4010589";
    String interfaceName = "INTF_DIM_E_CN_SITEDST";

    FeatureMapping featureMapping = new FeatureMapping(featureName + "::" + interfaceName);

    assertEquals(featureName, featureMapping.getFeatureName());
    assertEquals(interfaceName, featureMapping.getInterfaceName());

    assertThat(featureMapping.getDescription(), is((String) null));
    assertThat(featureMapping.getFajNumber(), is((String) null));
    assertThat(featureMapping.getReportPackage(), is((String) null));
  }

  @Test
  public void checkThatFeatureReportPackageInfoIsCorrect() throws IOException {
    String featureName = "CXC4010777";
    String reportPackageName = "BO_E_ERBS";

    FeatureMapping featureMapping = new FeatureMapping(featureName + "::" + reportPackageName);

    assertEquals(featureName, featureMapping.getFeatureName());
    assertEquals(reportPackageName, featureMapping.getReportPackage());

    assertThat(featureMapping.getInterfaceName(), is((String) null));
    assertThat(featureMapping.getDescription(), is((String) null));
    assertThat(featureMapping.getFajNumber(), is((String) null));
  }

  @Test
  public void checkThatStringWithInCorrectFormatWillThrowException() {
    FeatureMapping featureMapping = null;
    try {
      featureMapping = new FeatureMapping("test1234");
      fail("Should not have got here....");
    } catch (Exception e) {
      assertThat(featureMapping, is((FeatureMapping) null));
      assertEquals("Could not parse the string test1234", e.getMessage());
      assertTrue(e instanceof IOException);
    }
  }

  @Test
  public void checkThatFeatureDescriptionInfoIsCorrectWhenFAJContainsABackSlashCharacter() throws IOException {
    String featureName = "CXC4010923";
    String featureDescription = "Ericsson SGSN-MME 2G Event Tech Pack";
    String fajNumber = "FAJ 121 1533/2G";

    FeatureMapping featureMapping = new FeatureMapping(featureName + "::" + featureDescription + "::" + fajNumber);

    assertEquals(featureName, featureMapping.getFeatureName());
    assertEquals(featureDescription, featureMapping.getDescription());
    assertEquals(fajNumber, featureMapping.getFajNumber());

    assertThat(featureMapping.getInterfaceName(), is((String) null));
    assertThat(featureMapping.getReportPackage(), is((String) null));
  }
}
