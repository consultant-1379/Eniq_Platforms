package com.ericsson.eniq.techpacksdk.unittest.fest;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

import org.fest.swing.cell.JTableCellWriter;
import org.fest.swing.core.Robot;
import org.fest.swing.driver.AbstractJTableCellWriter;
import org.fest.swing.driver.JTableCheckBoxEditorCellWriter;
import org.fest.swing.driver.JTableComboBoxEditorCellWriter;
import org.fest.swing.driver.JTableTextComponentEditorCellWriter;
import org.fest.swing.exception.ActionFailedException;

import com.ericsson.eniq.component.TableComponent;

import tableTreeUtils.DescriptionComponent;

public class TTTableCellWriter extends AbstractJTableCellWriter {

  private final JTableCheckBoxEditorCellWriter checkBoxWriter;

  private final JTableComboBoxEditorCellWriter comboBoxWriter;

  private final JTableTextComponentEditorCellWriter textComponentWriter;

  private final TTTableDescComponentEditorCellWriter descComponentWriter;
  
  private final TTTableTableComponentEditorCellWriter tableComponentWriter;
  
  private final TTTableJPanelEditorCellWriter jpanelComponentWriter;


  public TTTableCellWriter(Robot robot) {
    super(robot);
    textComponentWriter = new JTableTextComponentEditorCellWriter(robot);
    checkBoxWriter = new JTableCheckBoxEditorCellWriter(robot);
    comboBoxWriter = new JTableComboBoxEditorCellWriter(robot);
    descComponentWriter = new TTTableDescComponentEditorCellWriter(robot);
    tableComponentWriter = new TTTableTableComponentEditorCellWriter(robot);
    jpanelComponentWriter = new TTTableJPanelEditorCellWriter(robot);

  }

  /**
   * Enters the given value at the given cell of the <code>JTable</code>. This
   * method only supports the following GUI components as cell editors:
   * <ul>
   * <li><code>{@link DescriptionComponent}</code>: any value will be entered in
   * the cell.</li>
   * <li><code>{@link TableComponent}</code>: any value will be entered in
   * the cell.</li>
   * <li><code>{@link JComboBox}</code>: this writer will select the element
   * which <code>String</code> representation matches the given value.</li>
   * <li><code>{@link JTextComponent}</code>: any value will be entered in the
   * cell.</li>
   * </ul>
   * 
   * @param table
   *          the target <code>JTable</code>.
   * @param row
   *          the row index of the cell.
   * @param column
   *          the column index of the cell.
   * @param value
   *          the value to enter.
   * @throws ActionFailedException
   *           if this writer is unable to handle the underlying cell editor.
   */
  public void enterValue(JTable table, int row, int column, String value) {
    cellWriterFor(table, row, column).enterValue(table, row, column, value);
  }

  /**
   * Starts editing the given cell of the <code>{@link JTable}</code>. This
   * method only supports the following GUI components as cell editors:
   * <ul>
   * <li><code>{@link DescriptionComponent}</code></li>
   * <li><code>{@link TableComponent}</code></li>
   * <li><code>{@link JCheckBox}</code></li>
   * <li><code>{@link JComboBox}</code></li>
   * <li><code>{@link JTextComponent}</code></li>
   * </ul>
   * 
   * @param row
   *          the row index of the cell.
   * @param column
   *          the column index of the cell.
   * @throws ActionFailedException
   *           if this writer is unable to handle the underlying cell editor.
   * @see JTableCellWriter#startCellEditing(JTable, int, int)
   */
  public void startCellEditing(JTable table, int row, int column) {
    cellWriterFor(table, row, column).startCellEditing(table, row, column);
  }

  /**
   * Stops editing the given cell of the <code>{@link JTable}</code>. This
   * method only supports the following GUI components as cell editors:
   * <ul>
   * <li><code>{@link DescriptionComponent}</code></li>
   * <li><code>{@link TableComponent}</code></li>
   * <li><code>{@link JCheckBox}</code></li>
   * <li><code>{@link JComboBox}</code></li>
   * <li><code>{@link JTextComponent}</code></li>
   * </ul>
   * 
   * @param row
   *          the row index of the cell.
   * @param column
   *          the column index of the cell.
   * @throws ActionFailedException
   *           if this writer is unable to handle the underlying cell editor.
   * @see JTableCellWriter#stopCellEditing(JTable, int, int)
   */
  public void stopCellEditing(JTable table, int row, int column) {
    cellWriterFor(table, row, column).stopCellEditing(table, row, column);
  }

  /**
   * Cancels editing the given cell of the <code>{@link JTable}</code>. This
   * method only supports the following GUI components as cell editors:
   * <ul>
   * <li><code>{@link DescriptionComponent}</code></li>
   * <li><code>{@link TableComponent}</code></li>
   * <li><code>{@link JCheckBox}</code></li>
   * <li><code>{@link JComboBox}</code></li>
   * <li><code>{@link JTextComponent}</code></li>
   * </ul>
   * 
   * @param row
   *          the row index of the cell.
   * @param column
   *          the column index of the cell.
   * @throws ActionFailedException
   *           if this writer is unable to handle the underlying cell editor.
   * @see JTableCellWriter#cancelCellEditing(JTable, int, int)
   */
  public void cancelCellEditing(JTable table, int row, int column) {
    cellWriterFor(table, row, column).cancelCellEditing(table, row, column);
  }

  private JTableCellWriter cellWriterFor(JTable table, int row, int column) {
    Component editor = editorForCell(table, row, column);
    if (editor instanceof JCheckBox)
      return checkBoxWriter;
    if (editor instanceof JComboBox)
      return comboBoxWriter;
    if (editor instanceof JTextComponent)
      return textComponentWriter;
    if (editor instanceof DescriptionComponent)
      return descComponentWriter;
    if (editor instanceof TableComponent)
      return tableComponentWriter;
    if (editor instanceof JPanel)
        return jpanelComponentWriter;
    throw cannotFindOrActivateEditor(row, column);
  }
}
