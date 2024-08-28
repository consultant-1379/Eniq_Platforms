package com.ericsson.eniq.techpacksdk.view.busyhourtree.nonmigrated;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyHourData;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyhourHandlingDataModel;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackTab;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

public class NonMigratedManageBusyHourView extends JPanel {

	private final SingleFrameApplication application;

	private final GeneralTechPackTab parentPanel;

	private class BusyhourTableListener implements TableModelListener {
		public void tableChanged(TableModelEvent e) {
			getParentAction("disableTabs").actionPerformed(null);
		}
	}

	private javax.swing.Action getParentAction(final String actionName) {
		if (application != null) {
			return application.getContext().getActionMap(parentPanel).get(actionName);
		}
		return null;
	}


	public NonMigratedManageBusyHourView(final SingleFrameApplication application,
																			 final DataModelController dataModelController, final Versioning versioning,
																			 final GeneralTechPackTab parentPanel) {
		super(new GridBagLayout());
		this.application = application;
		this.parentPanel = parentPanel;
		BusyhourHandlingDataModel busyHourDataModel = dataModelController.getBusyhourHandlingDataModel();
		busyHourDataModel.setCurrentVersioning(versioning);
		busyHourDataModel.refresh();
		final Vector<BusyHourData> data = new Vector<BusyHourData>(busyHourDataModel.getBusyHourData());
		final BusyHourTableModel busyHourTableModel = new BusyHourTableModel(application, dataModelController, data);
		final JTable datatable = new JTable(busyHourTableModel);
		datatable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		datatable.getModel().addTableModelListener(new BusyhourTableListener());
		datatable.setRowHeight(22);
		final JScrollPane scrollPane = new JScrollPane(datatable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		busyHourTableModel.setColumnWidths(datatable);
		busyHourTableModel.setColumnEditors(datatable);
		busyHourTableModel.setColumnRenderers(datatable);
		final ErrorMessageComponent errorMessageComponent = new ErrorMessageComponent(application);
		errorMessageComponent.setValue(null);

		final JButton close = new JButton(getParentAction("closeDialog"));
		close.setEnabled(true);
		close.setName("ReferenceViewClose");

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		buttonPanel.add(errorMessageComponent);
		buttonPanel.add(close);

		final GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;

		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		this.add(scrollPane, c);

		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 1;
		this.add(buttonPanel, c);
	}
}
