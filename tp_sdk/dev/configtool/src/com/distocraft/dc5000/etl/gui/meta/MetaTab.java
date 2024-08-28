package com.distocraft.dc5000.etl.gui.meta;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.etl.gui.MetaSelector;
import com.distocraft.dc5000.etl.gui.SwingWorker;
import com.distocraft.dc5000.etl.gui.SystemStructureNotificate;
import com.distocraft.dc5000.etl.gui.Tab;
import com.distocraft.dc5000.etl.gui.UI;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;

/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class MetaTab extends JPanel implements Tab, MouseListener {

	// private RockFactory rock;

	private RockFactory dwhrepRock;

	private JFrame frame;

	private SystemStructureNotificate ssn;

	private UI ui;

	// private JSplitPane split;

	private MetaSelector ms;

	private JTextField fileName;

	private JButton choose;

	private JButton insert;

	private JTextArea log;

	private File metaData = null;

	private static final String DEBUGKEY = "debug:";

	public MetaTab(JFrame frame, RockFactory rock, RockFactory dwhrepRock, SystemStructureNotificate ssn, UI ui) {
		super(new GridBagLayout());

		this.frame = frame;
		this.ssn = ssn;
		this.ui = ui;

		disconnected();
	}

	public void connected(RockFactory rock, RockFactory dwhrepRock, String connectionID) {
		// this.rock = rock;
		this.dwhrepRock = dwhrepRock;
		this.removeAll();

		JPanel left = new JPanel(new GridBagLayout());

		GridBagConstraints lc = new GridBagConstraints();
		lc.fill = GridBagConstraints.NONE;
		lc.anchor = GridBagConstraints.NORTHWEST;
		lc.weightx = 0;
		lc.weighty = 0;
		left.add(Box.createRigidArea(new Dimension(5, 5)), lc);

		lc.gridx = 1;
		lc.gridy = 1;
		left.add(new JLabel("Installed metadata"), lc);

		lc.gridy = 2;
		lc.gridx = 2;
		left.add(Box.createRigidArea(new Dimension(5, 5)), lc);

		lc.gridy = 3;
		lc.gridx = 0;
		lc.gridwidth = 3;
		lc.fill = GridBagConstraints.BOTH;
		lc.weightx = 1;
		lc.weighty = 1;

		ms = new MetaSelector(dwhrepRock, connectionID, null);
		ms.addMouseListener(this);
		left.add(ms, lc);

		JPanel right = new JPanel(new GridBagLayout());

		GridBagConstraints rc = new GridBagConstraints();
		rc.fill = GridBagConstraints.NONE;
		rc.anchor = GridBagConstraints.NORTHWEST;
		rc.weightx = 0;
		rc.weighty = 0;

		right.add(Box.createRigidArea(new Dimension(10, 10)), rc);

		rc.gridx = 1;
		rc.gridy = 1;
		rc.insets = new Insets(2, 2, 5, 2);
		right.add(new JLabel("Insert metadata to dwhrep"), rc);

		rc.gridy = 2;
		right.add(Box.createRigidArea(new Dimension(10, 10)));

		rc.gridy = 3;
		rc.weightx = 1;
		rc.fill = GridBagConstraints.BOTH;
		fileName = new JTextField();
		fileName.setEditable(false);
		right.add(fileName, rc);

		rc.gridx = 2;
		rc.weightx = 0;
		rc.fill = GridBagConstraints.NONE;
		choose = new JButton(ConfigTool.openDoc);
		choose.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				final JFileChooser fc = new JFileChooser();
				if (ConfigTool.fileDialogLastSelection != null)
					fc.setCurrentDirectory(ConfigTool.fileDialogLastSelection);
				fc.setDialogTitle("Select metadata file for insertion");
				fc.setFileFilter(new FileFilter() {

					public boolean accept(File f) {
						if (f.canRead() && (f.isDirectory() || f.getName().endsWith(".sql")))
							return true;
						else
							return false;
					}

					public String getDescription() {
						return "Metadata files (.sql)";
					}
				});

				if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					File sel = fc.getSelectedFile();
					if (sel != null) {
						ConfigTool.fileDialogLastSelection = sel.getParentFile();
						fileName.setText(sel.getPath());
						insert.setEnabled(true);
						metaData = sel;
					} else {
						fileName.setText("");
						insert.setEnabled(false);
						metaData = null;
					}

				}
			}
		});
		right.add(choose, rc);

		rc.gridx = 1;
		rc.gridy = 4;
		rc.fill = GridBagConstraints.NONE;
		right.add(Box.createRigidArea(new Dimension(10, 10)), rc);

		insert = new JButton("Insert", ConfigTool.dataStore);
		insert.setEnabled(false);
		insert.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				try {

					if (metaData == null) // no selection
						return;

					if (!metaData.exists() || !metaData.isFile() || !metaData.canRead()) {
						new ErrorDialog(frame, "Metadata Error", "Unable to read specified metadata file.", null);
						return;
					}

					InsertMetaDataWorker im = new InsertMetaDataWorker(metaData);
					im.start();

				} catch (Exception e) {
					new ErrorDialog(frame, "Failed", "Metadata installation failed", e);
				}

			}
		});
		rc.gridy = 5;
		right.add(insert, rc);

		rc.gridy = 6;
		right.add(Box.createRigidArea(new Dimension(10, 10)), rc);

		log = new JTextArea(10, 40);
		JScrollPane jsp = new JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		log.setText("--- Metadata log ---\nReady to insert. Select a metadata file.\n");
		rc.gridy = 7;
		rc.gridwidth = 2;
		rc.weightx = 1;
		rc.weighty = 1;
		rc.fill = GridBagConstraints.BOTH;
		right.add(jsp, rc);

		rc.gridy = 10;
		rc.gridx = 5;
		rc.weightx = 0;
		rc.weighty = 0;
		rc.gridwidth = 1;
		rc.fill = GridBagConstraints.NONE;
		right.add(Box.createRigidArea(new Dimension(10, 10)), rc);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		this.add(split, c);

		this.invalidate();
		this.validate();
		this.repaint();
	}

	public void disconnected() {
		this.removeAll();

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weighty = 0.5;

		this.add(Box.createRigidArea(new Dimension(20, 20)), c);

		c.weightx = 1;
		c.weighty = 0;
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(new JLabel("Disconnected"), c);

		c.gridy = 2;
		this.add(new JLabel("Select Connection-Connect to connect"), c);

		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weighty = 0.5;
		c.gridx = 2;
		c.gridy = 3;
		this.add(Box.createRigidArea(new Dimension(20, 20)), c);

		this.invalidate();
		this.validate();
		this.repaint();

	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		JTree comp = (JTree) e.getComponent();
		final TreePath tp = comp.getClosestPathForLocation(e.getX(), e.getY());

		DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) tp.getLastPathComponent();

		if (!(dmtn.getUserObject() instanceof Versioning))
			return;

		final Versioning vers = (Versioning) dmtn.getUserObject();

		if (e.getButton() == MouseEvent.BUTTON3) { // left click -> open popup

			JPopupMenu pop = new JPopupMenu("Metadata");
			JMenuItem rmd = new JMenuItem("Remove this Metadata", ConfigTool.delete);
			rmd.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent ae) {

					// Check if the selected metadata has a CUSTOM metadata related to it.
					String techpackName = vers.getTechpack_name();
					String customTechpackName = "CUSTOM_" + techpackName;

					Versioning whereVersioning = new Versioning(MetaTab.this.dwhrepRock);
					whereVersioning.setTechpack_name(customTechpackName);

					try {
						VersioningFactory versioningFactory = new VersioningFactory(MetaTab.this.dwhrepRock, whereVersioning);

						Vector customVersioning = versioningFactory.get();
						Iterator customVersioningIterator = customVersioning.iterator();

						int confirmResult = 0;
						if (customVersioningIterator.hasNext()) {
							// The CUSTOM_ metadata exists. Notify the user to delete it
							// first.

							confirmResult = JOptionPane.showConfirmDialog(MetaTab.this.frame,
									"This metadata has a related custom metadata named " + customTechpackName
											+ " and you should delete it before deleting this metadata.\n"
											+ "Are you sure you want to remove " + techpackName + " and all data related to it?",
									"Confirm delete", JOptionPane.YES_NO_OPTION);

						} else {
							// No custom metadata exists for this metadata. Ask confirmation
							// anyway.
							confirmResult = JOptionPane.showConfirmDialog(MetaTab.this.frame, "Are you sure you want to remove "
									+ techpackName + " and all data related to it?", "Confirm delete", JOptionPane.YES_NO_OPTION);

						}

						if (confirmResult == 0) {
							RemoveMetaDataWorker mdw = new RemoveMetaDataWorker(vers);
							mdw.start();
						}

					} catch (Exception e) {
						// Some error happened. Show dialog.
						JOptionPane.showConfirmDialog(MetaTab.this.frame, "Failed to load from Versioning table. Exception: "
								+ e.getStackTrace(), "Error", JOptionPane.OK_OPTION);
					}

				}
			});
			pop.add(rmd);

			JMenuItem rf = new JMenuItem("Refresh tree", ConfigTool.refresh);
			rf.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent ae) {
					if (ms != null)
						ms.refresh();
				}
			});
			pop.add(rf);

			pop.show(e.getComponent(), e.getX(), e.getY());

		}

	}

	public class RemoveMetaDataWorker extends SwingWorker {

		private Versioning vers = null;

		public RemoveMetaDataWorker(Versioning vers) {
			this.vers = vers;
			insert.setEnabled(false);
			choose.setEnabled(false);
			ui.startOperation("Removing " + vers.getTechpack_name() + " " + vers.getTechpack_version() + " ...");
		}

		public Object construct() {

			try {

				Runnable abouttoread = new Runnable() {

					public void run() {
						log.append("Removing " + vers.getTechpack_name() + " version " + vers.getTechpack_version() + "\n");
					}
				};
				SwingUtilities.invokeLater(abouttoread);

				Connection con = null;
				Statement stmt = null;
				boolean autocommit_org = false;

				try {

					con = dwhrepRock.getConnection();

					autocommit_org = con.getAutoCommit();
					con.setAutoCommit(false);

					stmt = con.createStatement();

					Runnable trying = new Runnable() {

						public void run() {
							log.append("Connection to dwhrep established.\nDeleting... This might take a while.\n");
						}
					};
					SwingUtilities.invokeLater(trying);

					String versionid = vers.getVersionid();

					StringBuffer sb = new StringBuffer();
					sb.append("delete from InterfaceMeasurement where DATAFORMATID like '" + versionid + "%';");
					sb.append("delete from DataItem where DATAFORMATID like '" + versionid + "%';");
					sb.append("delete from DefaultTags where DATAFORMATID like '" + versionid + "%';");
					sb.append("delete from DataFormat where VERSIONID='" + versionid + "';");
					sb.append("delete from ReferenceColumn where TYPEID like '" + versionid + "%';");
					sb.append("delete from ReferenceTable where TYPEID like '" + versionid + "%';");
					sb.append("delete from MeasurementKey where TYPEID like '" + versionid + "%';");
					sb.append("delete from MeasurementCounter where TYPEID like '" + versionid + "%';");
					sb.append("delete from MeasurementColumn where MTABLEID like '" + versionid + "%';");
					sb.append("delete from AggregationRule where VERSIONID='" + versionid + "';");
					sb.append("delete from Aggregation where VERSIONID='" + versionid + "';");
					sb.append("delete from MeasurementTable where MTABLEID like '" + versionid + "%';");
					sb.append("delete from MeasurementType where TYPEID like '" + versionid + "%';");
					sb.append("delete from MeasurementTypeClass where VERSIONID='" + versionid + "';");
					sb.append("delete from ExternalStatement where VERSIONID='" + versionid + "';");
					sb.append("delete from Transformation where TRANSFORMERID like '" + versionid + "%';");
					sb.append("delete from Transformer where TRANSFORMERID like '" + versionid + "%';");
					sb.append("delete from PromptOption where VERSIONID like '" + versionid + "%';");
					sb.append("delete from Prompt where VERSIONID like '" + versionid + "%';");
					sb.append("delete from PromptImplementor where VERSIONID like '" + versionid + "%';");
					sb.append("delete from Versioning where VERSIONID='" + versionid + "';");

					stmt.executeUpdate(sb.toString());

					stmt.close();

					con.commit();

					con.setAutoCommit(true);

					Runnable cmplete = new Runnable() {

						public void run() {
							log.append("Metadata successfully deleted.\n");
						}
					};
					SwingUtilities.invokeLater(cmplete);

				} catch (final Exception e) {
					new ErrorDialog(frame, "Metadata error", "Unable to delete metadata", e);

					Runnable xception = new Runnable() {

						public void run() {
							log.append("Metadata deletion failed. Rollbacking deletion.\n");
						}
					};
					SwingUtilities.invokeLater(xception);

					if (con != null)
						con.rollback();
				} finally {
					if (stmt != null) {
						try {
							stmt.close();
						} catch (Exception e) {
						}
					}
					if (con != null) {
						try {
							con.setAutoCommit(autocommit_org);
						} catch (Exception e) {
						}
					}
				}

			} catch (final Throwable t) {

				new ErrorDialog(frame, "Metadata error", "General failure", t);

				Runnable xception = new Runnable() {

					public void run() {
						log.append("Metadata deletion failed to general failure.\n");
					}
				};
				SwingUtilities.invokeLater(xception);
			}

			return null;

		}

		public void finished() {
			ms.refresh(null);
			ssn.metaDataChange();
			insert.setEnabled(true);
			choose.setEnabled(true);
			ui.endOperation();
		}

	};

	public class InsertMetaDataWorker extends SwingWorker {

		private File meta = null;

		long time = System.currentTimeMillis();

		public InsertMetaDataWorker(File meta) {
			this.meta = meta;

			insert.setEnabled(false);
			choose.setEnabled(false);
			ms.setEnabled(false);
			ui.startOperation("Inserting metadata...");
		}

		public Object construct() {

			try {

				Runnable abouttoread = new Runnable() {

					public void run() {
						log.append("Reading metadata file from " + meta.toString() + "\n");
					}
				};
				SwingUtilities.invokeLater(abouttoread);

				Connection con = null;
				Statement stmt = null;

				boolean autocommit_org = false;

				try {

					con = dwhrepRock.getConnection();
					autocommit_org = con.getAutoCommit();

					con.setAutoCommit(false);
					stmt = con.createStatement();

					Runnable trying = new Runnable() {

						public void run() {
							log.append("Connection to dwhrep established.\nInserting metadata... This might take a while.\n");
						}
					};
					SwingUtilities.invokeLater(trying);

					int rowCounter = 0;
					int executeCounter = 0;
					int exceptionCounter = 0;

					final StringBuffer sb = new StringBuffer();
					BufferedReader br = new BufferedReader(new FileReader(meta));
					try {
						String token;
						BufferedWriter bw = null;
						try {
							while ((token = br.readLine()) != null) {
								String sqlText = token.trim();
								rowCounter++;
								// remove empty lines and comments
								if ((sqlText.equals("")) || (sqlText.startsWith("--"))) {
									// Trick, if comment has value "-- debug:filename" then write
									// debug data into debug log file.
									// One metadata file can have multiple different log files.
									if (sqlText.length() > 1) { 
										sqlText = sqlText.substring(2).trim(); 
										if (sqlText.startsWith(DEBUGKEY)) {
											String debugFileName = sqlText.substring(DEBUGKEY.length()).trim();
											if (!debugFileName.equals("")) {
												if (bw != null) {
													bw.close();
												}
												bw = new BufferedWriter(new FileWriter(new File(debugFileName)));
											}
										}
									}
									continue;
								}
								sb.append(sqlText);
								sb.append("\n");
								if (sqlText.endsWith(";")) {
									try {
										stmt.executeUpdate(sb.toString());
										executeCounter++;
									} catch (final Exception e) {
										if (bw != null) {
											bw.write("Failed to execute row #" + rowCounter + ":\n" + sb.toString());
											bw.write(e.getMessage() + "\n\n");
										}
										exceptionCounter++;
									}
									sb.setLength(0);
								}
							}
						} finally {
							if (bw != null) {
								bw.close();
							}
						}
					} finally {
						br.close();
					}

					final StringBuffer resultText = new StringBuffer("");

					if (exceptionCounter != 0) {
						con.rollback();
						resultText.append("Metadata unsuccessfully inserted.\nThere were " + exceptionCounter
								+ " invalid sql statements in metadata batch");
					} else {
						con.commit();
						resultText.append("Metadata successfully inserted in " + ((System.currentTimeMillis() - time) / 1000)
								+ " seconds.\nThere were " + executeCounter + " sql statements in metadata batch\n");
					}

					Runnable cmplete = new Runnable() {

						public void run() {
							log.append(resultText.toString());
						}
					};
					SwingUtilities.invokeLater(cmplete);

				} catch (final Exception e) {
					new ErrorDialog(frame, "Metadata error", "Unable to insert metadata", e);

					Runnable xception = new Runnable() {

						public void run() {
							log.append("Metadata insertion failed.\n");
						}
					};
					SwingUtilities.invokeLater(xception);

					if (con != null)
						con.rollback();
				} finally {
					if (stmt != null) {
						try {
							stmt.close();
						} catch (Exception e) {
						}
					}
					if (con != null) {
						try {
							con.setAutoCommit(autocommit_org);
						} catch (Exception e) {
						}
					}
				}

			} catch (final Throwable t) {

				new ErrorDialog(frame, "Metadata error", "General failure", t);

				Runnable xception = new Runnable() {

					public void run() {
						log.append("Metadata insertion failed to general failure.\n");
					}
				};
				SwingUtilities.invokeLater(xception);
			}

			return null;
		}

		public void finished() {
			ms.setEnabled(true);
			ms.refresh(null);
			ssn.metaDataChange();
			ssn.techPackChange();
			insert.setEnabled(true);
			choose.setEnabled(true);
			ui.endOperation();
		}

	};

}
