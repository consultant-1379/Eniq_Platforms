package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.Component;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.application.SingleFrameApplication;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import tableTree.TableTreeComponent;

import com.distocraft.dc5000.common.StaticProperties;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

public class TechpackSetWizardViewFactoryTest extends BaseUnitTestX {

  private final SingleFrameApplication application = null;

  private DataModelController mockDataModelController = null;

  private TableTreeComponent mockTableTreeComponent;

  private final JFrame frame = null;

  private final JFrame fFrame = null;

  private String setName = "DC_E_TEST";

  private final String setVersion = null;

  private final String versionid = null;

  private String setType;

  private final boolean editable = false;

  @Before
  public void setUp() throws Exception {
    StaticProperties.giveProperties(new Properties());
    recreateMockeryContext();
  }

  @Test
  public void checkThatCorrectComponentsHaveBeenAddedToTechpackSetWizardViewForStats() {
    createMockTableTreeComponent();
    createMockDataModelController();
    setType = "PM";

    final TechpackSetWizardView tpSetWizardView = TechpackSetWizardViewFactory.createTechpackSetWizardView(application,
        mockDataModelController, mockTableTreeComponent, frame, fFrame, setName, setVersion, versionid, setType,
        editable);

    assertThat(tpSetWizardView, is(TechpackSetWizardView.class));

    final Component[] components = tpSetWizardView.getComponents();
    assertEquals(components.length, 2);
    assertThat(components[0], is(JScrollPane.class));
    assertThat(components[1], is(JPanel.class));

    checkFirstComponentInfoIsCorrectForStats(components[0]);
    checkSecondComponentInfoIsCorrect(components[1]);
  }

  @Test
  public void checkThatCorrectComponentsHaveBeenAddedToTechpackSetWizardViewForEvents() {
    createMockTableTreeComponent();
    createMockDataModelController();
    setType = Constants.ENIQ_EVENT;

    final TechpackSetWizardView tpSetWizardView = TechpackSetWizardViewFactory.createTechpackSetWizardView(application,
        mockDataModelController, mockTableTreeComponent, frame, fFrame, setName, setVersion, versionid, setType,
        editable);

    assertThat(tpSetWizardView, is(EventsTechpackSetWizardView.class));

    final Component[] components = tpSetWizardView.getComponents();
    assertEquals(components.length, 2);
    assertThat(components[0], is(JScrollPane.class));
    assertThat(components[1], is(JPanel.class));

    checkFirstComponentInfoIsCorrectForEvents(components[0]);
    checkSecondComponentInfoIsCorrect(components[1]);
  }

  @Test
  public void checkThatCorrectComponentsHaveBeenAddedToTechpackSetWizardViewForSonvEvents() {
    createMockTableTreeComponent();
    createMockDataModelController();
    setType = Constants.ENIQ_EVENT;
    setName = "DC_E_TEST_SONV";
    final TechpackSetWizardView tpSetWizardView = TechpackSetWizardViewFactory.createTechpackSetWizardView(application,
        mockDataModelController, mockTableTreeComponent, frame, fFrame, setName, setVersion, versionid, setType,
        editable);

    assertThat(tpSetWizardView, is(SonvEventsTechpackSetWizardView.class));

    final Component[] components = tpSetWizardView.getComponents();
    assertEquals(components.length, 2);
    assertThat(components[0], is(JScrollPane.class));
    assertThat(components[1], is(JPanel.class));

    checkFirstComponentInfoIsCorrectForSonvEvents(components[0]);
    checkSecondComponentInfoIsCorrect(components[1]);
  }

  private void checkSecondComponentInfoIsCorrect(final Component component) {
    final Component[] components = ((JPanel) component).getComponents();
    assertEquals(components.length, 2);

    assertThat(components[0], is(JButton.class));
    assertThat(((JButton) components[0]).getName(), is("TechpackSetWizardViewCreate"));
    assertThat(components[1], is(JButton.class));
    assertThat(((JButton) components[1]).getName(), is("TechpackSetWizardViewDiscard"));
  }

  private void checkFirstComponentInfoIsCorrectForStats(final Component component) {
    final Component[] components = ((JScrollPane) component).getViewport().getComponents();
    assertEquals(components.length, 1);
    assertThat(components[0], is(JPanel.class));

    final Component[] jPanelComponents = ((JPanel) components[0]).getComponents();
    assertEquals(jPanelComponents.length, 14);

    assertThat(jPanelComponents[0], is(JLabel.class));
    assertThat(((JLabel) jPanelComponents[0]).getText(), is("Template Version"));

    assertThat(jPanelComponents[1], is(JComboBox.class));
    assertEquals(((JComboBox) jPanelComponents[1]).getItemCount(), 1);
    assertThat(((JComboBox) jPanelComponents[1]).getItemAt(0).toString(), is("5.2"));

    assertThat(jPanelComponents[2], is(JLabel.class));
    assertThat(((JLabel) jPanelComponents[2]).getText(), is("Generate"));

    assertThat(jPanelComponents[3], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[3]).getText(), is("DWH Sets"));

    assertThat(jPanelComponents[4], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[4]).getText(), is("Loader Sets"));

    assertThat(jPanelComponents[5], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[5]).getText(), is("Topology Loader Sets"));

    assertThat(jPanelComponents[6], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[6]).getText(), is("Aggregator Sets"));

    assertThat(jPanelComponents[7], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[7]).getText(), is("Directory Checker Sets"));

    assertThat(jPanelComponents[8], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[8]).getText(), is("Disk Manager Sets"));

    assertThat(jPanelComponents[9], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[9]).getText(), is("Schedulings"));

    assertThat(jPanelComponents[10], is(JLabel.class));
    assertThat(((JLabel) jPanelComponents[10]).getText(), is("Existing Sets"));

    assertThat(jPanelComponents[11], is(JComboBox.class));
    assertEquals(((JComboBox) jPanelComponents[11]).getItemCount(), 2);
    assertThat(((JComboBox) jPanelComponents[11]).getItemAt(0).toString(), is("Overwrite"));
    assertThat(((JComboBox) jPanelComponents[11]).getItemAt(1).toString(), is("Skip"));

    assertThat(jPanelComponents[12], is(JLabel.class));
    assertThat(((JLabel) jPanelComponents[12]).getText(), is("Measurements to Use"));

    assertThat(jPanelComponents[13], is(JPanel.class));
    assertEquals(((JPanel) jPanelComponents[13]).getComponentCount(), 1);
    assertThat(((JPanel) jPanelComponents[13]).getComponent(0), is(JScrollPane.class));
  }

  private void checkFirstComponentInfoIsCorrectForEvents(final Component component) {
    final Component[] components = ((JScrollPane) component).getViewport().getComponents();
    assertEquals(components.length, 1);
    assertThat(components[0], is(JPanel.class));

    final Component[] jPanelComponents = ((JPanel) components[0]).getComponents();
    assertEquals(jPanelComponents.length, 16);

    assertThat(jPanelComponents[0], is(JLabel.class));
    assertThat(((JLabel) jPanelComponents[0]).getText(), is("Template Version"));

    assertThat(jPanelComponents[1], is(JComboBox.class));
    assertEquals(((JComboBox) jPanelComponents[1]).getItemCount(), 1);
    assertThat(((JComboBox) jPanelComponents[1]).getItemAt(0).toString(), is("5.2"));

    assertThat(jPanelComponents[2], is(JLabel.class));
    assertThat(((JLabel) jPanelComponents[2]).getText(), is("Generate"));

    assertThat(jPanelComponents[3], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[3]).getText(), is("DWH Sets"));

    assertThat(jPanelComponents[4], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[4]).getText(), is("Loader Sets"));

    assertThat(jPanelComponents[5], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[5]).getText(), is("Topology Loader Sets"));

    assertThat(jPanelComponents[6], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[6]).getText(), is("Aggregator Sets"));

    assertThat(jPanelComponents[7], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[7]).getText(), is("Directory Checker Sets"));

    assertThat(jPanelComponents[8], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[8]).getText(), is("Disk Manager Sets"));

    assertThat(jPanelComponents[9], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[9]).getText(), is("Schedulings"));

    assertThat(jPanelComponents[10], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[10]).getText(), is("Count Sets"));

    assertThat(jPanelComponents[11], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[11]).getText(), is("Restore Sets"));

    assertThat(jPanelComponents[12], is(JLabel.class));
    assertThat(((JLabel) jPanelComponents[12]).getText(), is("Existing Sets"));

    assertThat(jPanelComponents[13], is(JComboBox.class));
    assertEquals(((JComboBox) jPanelComponents[13]).getItemCount(), 2);
    assertThat(((JComboBox) jPanelComponents[13]).getItemAt(0).toString(), is("Overwrite"));
    assertThat(((JComboBox) jPanelComponents[13]).getItemAt(1).toString(), is("Skip"));

    assertThat(jPanelComponents[14], is(JLabel.class));
    assertThat(((JLabel) jPanelComponents[14]).getText(), is("Measurements to Use"));

    assertThat(jPanelComponents[15], is(JPanel.class));
    assertEquals(((JPanel) jPanelComponents[15]).getComponentCount(), 1);
    assertThat(((JPanel) jPanelComponents[15]).getComponent(0), is(JScrollPane.class));
  }

  private void checkFirstComponentInfoIsCorrectForSonvEvents(final Component component) {
    final Component[] components = ((JScrollPane) component).getViewport().getComponents();
    assertEquals(components.length, 1);
    assertThat(components[0], is(JPanel.class));

    final Component[] jPanelComponents = ((JPanel) components[0]).getComponents();
    assertEquals(jPanelComponents.length, 17);

    assertThat(jPanelComponents[0], is(JLabel.class));
    assertThat(((JLabel) jPanelComponents[0]).getText(), is("Template Version"));

    assertThat(jPanelComponents[1], is(JComboBox.class));
    assertEquals(((JComboBox) jPanelComponents[1]).getItemCount(), 1);
    assertThat(((JComboBox) jPanelComponents[1]).getItemAt(0).toString(), is("5.2"));

    assertThat(jPanelComponents[2], is(JLabel.class));
    assertThat(((JLabel) jPanelComponents[2]).getText(), is("Generate"));

    assertThat(jPanelComponents[3], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[3]).getText(), is("DWH Sets"));

    assertThat(jPanelComponents[4], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[4]).getText(), is("Loader Sets"));

    assertThat(jPanelComponents[5], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[5]).getText(), is("Topology Loader Sets"));

    assertThat(jPanelComponents[6], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[6]).getText(), is("Aggregator Sets"));

    assertThat(jPanelComponents[7], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[7]).getText(), is("Directory Checker Sets"));

    assertThat(jPanelComponents[8], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[8]).getText(), is("Disk Manager Sets"));

    assertThat(jPanelComponents[9], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[9]).getText(), is("Schedulings"));

    assertThat(jPanelComponents[10], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[10]).getText(), is("Count Sets"));

    assertThat(jPanelComponents[11], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[11]).getText(), is("Restore Sets"));

    assertThat(jPanelComponents[12], is(JCheckBox.class));
    assertThat(((JCheckBox) jPanelComponents[12]).getText(), is("Backup Sets"));

    assertThat(jPanelComponents[13], is(JLabel.class));
    assertThat(((JLabel) jPanelComponents[13]).getText(), is("Existing Sets"));

    assertThat(jPanelComponents[14], is(JComboBox.class));
    assertEquals(((JComboBox) jPanelComponents[14]).getItemCount(), 2);
    assertThat(((JComboBox) jPanelComponents[14]).getItemAt(0).toString(), is("Overwrite"));
    assertThat(((JComboBox) jPanelComponents[14]).getItemAt(1).toString(), is("Skip"));

    assertThat(jPanelComponents[15], is(JLabel.class));
    assertThat(((JLabel) jPanelComponents[15]).getText(), is("Measurements to Use"));

    assertThat(jPanelComponents[16], is(JPanel.class));
    assertEquals(((JPanel) jPanelComponents[16]).getComponentCount(), 1);
    assertThat(((JPanel) jPanelComponents[16]).getComponent(0), is(JScrollPane.class));
  }

  private void createMockDataModelController() {
    mockDataModelController = context.mock(DataModelController.class);
    context.checking(new Expectations() {

      {
        allowing(mockDataModelController);
      }
    });
  }

  private void createMockTableTreeComponent() {
    mockTableTreeComponent = context.mock(TableTreeComponent.class);
    context.checking(new Expectations() {

      {
        allowing(mockTableTreeComponent);
      }
    });
  }
}
