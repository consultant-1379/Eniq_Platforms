package com.ericsson.eniq.techpacksdk.unittest.fest;

import static java.awt.event.KeyEvent.VK_F2;
import static org.fest.swing.core.MouseButton.LEFT_BUTTON;

import java.awt.Point;

import javax.swing.JTable;
import javax.swing.text.JTextComponent;

import org.fest.swing.annotation.RunsInEDT;
import org.fest.swing.core.Robot;
import org.fest.swing.driver.AbstractJTableCellWriter;
import org.fest.swing.driver.JTextComponentDriver;
import org.fest.swing.exception.ActionFailedException;
import org.fest.swing.fixture.JButtonFixture;

import tableTreeUtils.DescriptionComponent;



/**
 * Understands an implementation of <code>{@link JTTTableCellWriter}</code> that
 * knows how to use <code>{@link DescriptionComponent}</code>s as cell editors.
 * 
 * Use the enterValue method to set the value directly to the text field of the
 * DescriptionComponent, or start the cell editing with startCellEditing method
 * to get the "edit button" clicked and the edit frame opened.
 * 
 * @author eheitur
 */
public class TTTableDescComponentEditorCellWriter extends AbstractJTableCellWriter {

  protected final JTextComponentDriver driver;

  public TTTableDescComponentEditorCellWriter(Robot robot) {
    super(robot);
    driver = new JTextComponentDriver(robot);
  }

  /** {@inheritDoc} */
  @RunsInEDT
  public void enterValue(JTable table, int row, int column, String value) {
    // TODO: Debug
    System.out.println(this.getClass() + " enterValue(): row=" + row + ", col=" + column + ", value=" + value + ".");

    DescriptionComponent editor = doStartCellEditing(table, row, column);

    System.out.println(this.getClass() + " enterValue(): editor showing = " + editor.isShowing());
    System.out.println(this.getClass() + " enterValue(): editor visible = " + editor.isVisible());

    JTextComponent textField = editor.getTextField();
    driver.replaceText(textField, value);
    stopCellEditing(table, row, column);
  }

  /** {@inheritDoc} */
  @RunsInEDT
  public void startCellEditing(JTable table, int row, int column) {
    System.out.println(this.getClass() + " startCellEditing(): row=" + row + ", col=" + column + ".");

    // doStartCellEditing(table, row, column);
    DescriptionComponent editor = doStartCellEditing(table, row, column);

    System.out.println(this.getClass() + " startCellEditing(): editor showing = " + editor.isShowing());
    System.out.println(this.getClass() + " startCellEditing(): editor visible = " + editor.isVisible());
    System.out.println(this.getClass() + " startCellEditing(): editor button showing = "
        + editor.getEditButton().isShowing());
    System.out.println(this.getClass() + " startCellEditing(): editor button visible = "
        + editor.getEditButton().isVisible());

    System.out.println(this.getClass() + " startCellEditing(): simulating edit button click.");
    JButtonFixture bf = new JButtonFixture(robot, editor.getEditButton());
    bf.click();

  }

  @RunsInEDT
  private DescriptionComponent doStartCellEditing(JTable table, int row, int column) {
    Point cellLocation = cellLocation(table, row, column, location);
    DescriptionComponent descComp = null;
    try {
      descComp = activateEditorWithF2Key(table, row, column, cellLocation);
    } catch (ActionFailedException e) {
      descComp = activateEditorWithDoubleClick(table, row, column, cellLocation);
    }
    cellEditor(cellEditor(table, row, column));
    return descComp;
  }

  @RunsInEDT
  private DescriptionComponent activateEditorWithF2Key(JTable table, int row, int column, Point cellLocation) {
    robot.click(table, cellLocation);
    robot.pressAndReleaseKeys(VK_F2);
    return waitForEditorActivation(table, row, column);
  }

  @RunsInEDT
  private DescriptionComponent activateEditorWithDoubleClick(JTable table, int row, int column, Point cellLocation) {
    robot.click(table, cellLocation, LEFT_BUTTON, 2);
    return waitForEditorActivation(table, row, column);
  }

  @RunsInEDT
  private DescriptionComponent waitForEditorActivation(JTable table, int row, int column) {
    return waitForEditorActivation(table, row, column, DescriptionComponent.class);
  }
}
