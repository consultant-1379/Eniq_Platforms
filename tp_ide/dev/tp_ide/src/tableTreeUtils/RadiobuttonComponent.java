package tableTreeUtils;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import tableTree.CustomParameterComponent;

/**
 * Helper class for displaying radio button group
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class RadiobuttonComponent extends JPanel implements ActionListener, CustomParameterComponent {

  /**
   * Action listener for when this is used in parameter panels
   */
  ActionListener myListener = null;

  /**
   * Action command for when the action listener is triggered
   */
  private String actionCommand;

  /**
   * Stores currently selected radiobutton value
   */
  private String value;

  /**
   * The radio button group
   */
  private ButtonGroup bg = null;

  /**
   * Stores the radio button names in the button group
   */
  private String[] buttonNames = null;

  /**
   * Creates radiobuttons and buttongroup for them
   * 
   * @param buttons
   *          array of names for radio buttons
   * @param selected
   *          selected item index (use -1 for no selection)
   */
  public RadiobuttonComponent(final String[] buttons, final int selected) {
    super(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    this.buttonNames = buttons;
    this.bg = new ButtonGroup();
    for (int ind = 0; ind < this.buttonNames.length; ind++) {
      final JRadioButton rb = new JRadioButton(this.buttonNames[ind]);
      rb.setActionCommand(this.buttonNames[ind]);
      rb.addActionListener(this);
      bg.add(rb);
      this.add(rb);
    }
    setSelectedButton(selected);

  }

  /**
   * Sets the selected radio button based on the given index. (use -1 for no
   * selection).
   * 
   * @param index
   *          the index of the selected button
   */
  public void setSelectedButton(final int index) {
    final Enumeration<AbstractButton> buttons = this.bg.getElements();
    int i = 0;
    while (buttons.hasMoreElements()) {
      final AbstractButton btn = buttons.nextElement();
      if (index == i) {
        this.bg.setSelected(btn.getModel(), true);
        value = buttonNames[i];
      } else {
        this.bg.setSelected(btn.getModel(), false);
      }
      i++;
    }

    // Fire an event so that the listener knows the value was updated
    if (myListener != null) {
      myListener.actionPerformed(new ActionEvent(this, 0, actionCommand));
    }
  }

  /**
   * Sets the selected radio button based on the given button name. (use "" for
   * no selection).
   * 
   * @param index
   *          the name of the selected button
   */
  public void setSelectedButton(final String buttonName) {
    final Enumeration<AbstractButton> buttons = this.bg.getElements();
    int i = 0;
    while (buttons.hasMoreElements()) {
      final AbstractButton btn = buttons.nextElement();
      if (buttonName != null && buttonName.equals(buttonNames[i])) {
        this.bg.setSelected(btn.getModel(), true);
        value = buttonNames[i];
      } else {
        this.bg.setSelected(btn.getModel(), false);
      }
      i++;
    }

    // Fire an event so that the listener knows the value was updated
    if (myListener != null) {
      myListener.actionPerformed(new ActionEvent(this, 0, actionCommand));
    }
  }

  /**
   * This is the listener callback for radiobuttons.
   */
  public void actionPerformed(final ActionEvent e) {
    value = e.getActionCommand();

    // Fire an event so that the listener knows the value was updated
    if (myListener != null) {
      myListener.actionPerformed(new ActionEvent(this, 0, actionCommand));
    }
  }

  /**
   * Return the component's current value
   */
  public String getValue() {
    return value;
  }

  /**
   * Set the component's current value
   * 
   * @param value
   */
  // public void setValue(String value) {
  // this.value = value;
  // }

  /**
   * Return itself
   */
  public JComponent getComponent() {
    return this;
  }

  /**
   * Use this method for enabling/disabling the component depending on the
   * tree's isEditable value
   */
  public void setEnabled(final boolean isEnabled) {
    final int nrOfComponents = this.getComponentCount();
    for (int i = 0; i < nrOfComponents; i++) {
      this.getComponent(i).setEnabled(isEnabled);
    }
  }

  /**
   * Set the action command for this component
   */
  public void setActionCommand(final String command) {
    actionCommand = command;
  }

  /**
   * Set the action listener for this instance
   */
  public void addActionListener(final ActionListener listener) {
    myListener = listener;
  }

}
