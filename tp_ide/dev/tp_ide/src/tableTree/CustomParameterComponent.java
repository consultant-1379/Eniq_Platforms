package tableTree;

import java.awt.event.ActionListener;
import javax.swing.JComponent;

/**
 * An interface for components in the parameter panels, for which special
 * editors and renderers can be used. Implement this if you want to add
 * customized components in your TTParameterModel.
 * 
 * @author ejeahei enaland
 */
public interface CustomParameterComponent {

    /**
     * Retrieve the value of the component.
     * 
     * @return the value
     */
    public Object getValue();

    /**
     * Return the graphical component that represents this parameter.
     * 
     * @return the component
     */
    public JComponent getComponent();

    /**
     * Controls the setEnabled(boolean) methods of the custom components
     * 
     * @param isEnabled
     */
    public void setEnabled(boolean isEnabled);
    
    /**
     * Set the action command. This will be returned in the actionPerformed()
     * event to the listener.
     * 
     * @param command
     */
    public void setActionCommand(String command);

    /**
     * Set the action listener for the component. This action listener's
     * actionPerformed() method will be called when the value of the component
     * has changed.
     * 
     * @param listener
     */
    public void addActionListener(ActionListener listener);
}
