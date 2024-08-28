package com.distocraft.dc5000.etl.gui.iface;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.DataformatFactory;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;
import com.distocraft.dc5000.repository.dwhrep.InterfacemeasurementFactory;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.TransformerFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;

public class DataFormatWindow extends JDialog {

  private JFrame parent;

  private RockFactory dwhrepRock;

  private Datainterface di;

  private Interfacemeasurement im;

  private JTextField tagID = null;

  private TPComboBox techPack = null;

  private JComboBox dataFormat = null;

  private JComboBox transformer = null;

  private JComboBox status = null;

  private JTextArea comment = null;

  private boolean commit = false;

  DataFormatWindow(JFrame parent, RockFactory dwhrepRock, Datainterface di, Interfacemeasurement im) {
    super(parent, true);

    this.parent = parent;
    this.dwhrepRock = dwhrepRock;
    this.di = di;
    this.im = im;

    try {

      Dataformat df = null;
      boolean newOne = true;

      if (im == null) {
        setTitle("New Measurement");
        newOne = true;
      } else {
        setTitle("Measurement " + im.getTagid());
        Dataformat adf = new Dataformat(dwhrepRock);
        adf.setDataformatid(im.getDataformatid());
        DataformatFactory adff = new DataformatFactory(dwhrepRock, adf);
        df = adff.getElementAt(0);
        newOne = false;
      }

      final Container con = getContentPane();
      con.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.NORTHWEST;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      c.weighty = 0;
      c.insets = new Insets(2, 4, 2, 2);

      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      c.gridy = 1;
      c.gridx = 1;
      con.add(new JLabel("TagID"), c);

      c.gridx = 2;
      if (im == null) {
        tagID = new JTextField(15);
        con.add(tagID, c);
      } else {
        tagID = new JTextField(15);
        tagID.setText(im.getTagid());
        con.add(new JLabel(im.getTagid()), c);
      }

      c.gridx = 1;
      c.gridy = 2;
      con.add(new JLabel("TechPack"), c);

      c.gridx = 2;
      techPack = new TPComboBox(dwhrepRock);
      if (im != null && df != null) {

        Versioning aver = new Versioning(dwhrepRock);
        aver.setVersionid(df.getVersionid());
        VersioningFactory averf = new VersioningFactory(dwhrepRock, aver);
        Versioning ov = averf.getElementAt(0);
        //techPack.setSelectedItem(ov);
        techPack.setSelectedVersionID(ov.getVersionid());
      }

      techPack.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent ae) {

          try {
            Versioning vers = (Versioning) techPack.getSelectedItem();

            Dataformat adf = new Dataformat(DataFormatWindow.this.dwhrepRock);
            adf.setVersionid(vers.getVersionid());
            DataformatFactory adff = new DataformatFactory(DataFormatWindow.this.dwhrepRock, adf);
            List formats = adff.get();

            Transformer tr = new Transformer(DataFormatWindow.this.dwhrepRock);
            tr.setVersionid(vers.getVersionid());
            TransformerFactory trf = new TransformerFactory(DataFormatWindow.this.dwhrepRock, tr);
            List transformers = trf.get();

            DefaultComboBoxModel dfcbm1 = new DefaultComboBoxModel((Dataformat[]) formats
                .toArray(new Dataformat[formats.size()]));
            dataFormat.setModel(dfcbm1);

            DefaultComboBoxModel dfcbm2 = new DefaultComboBoxModel((Transformer[]) transformers
                .toArray(new Transformer[transformers.size()]));
            transformer.setModel(dfcbm2);

            DataFormatWindow.this.invalidate();
            DataFormatWindow.this.validate();
            DataFormatWindow.this.repaint();

            DataFormatWindow.this.pack();

          } catch (Exception e) {

          }
        }

      });

      con.add(techPack, c);

      c.gridx = 1;
      c.gridy = 3;
      con.add(new JLabel("DataFormat"), c);

      c.gridx = 2;
      DefaultComboBoxModel dfcbm1 = null;
      int selix = -1;
      if (im != null && df != null) {

        Versioning aver = new Versioning(dwhrepRock);
        aver.setVersionid(df.getVersionid());
        VersioningFactory averf = new VersioningFactory(dwhrepRock, aver);

        Dataformat adf = new Dataformat(dwhrepRock);
        adf.setVersionid(averf.getElementAt(0).getVersionid());
        DataformatFactory adff = new DataformatFactory(dwhrepRock, adf);
        List formats = adff.get();

        Iterator it = formats.iterator();
        for (int i = 0; it.hasNext(); i++) {
          Dataformat tdf = (Dataformat) it.next();

          if (tdf.getDataformatid().equals(df.getDataformatid())) {
            selix = i;
            break;
          }
        }

        dfcbm1 = new DefaultComboBoxModel((Dataformat[]) formats.toArray(new Dataformat[formats.size()]));

      } else {
        dfcbm1 = new DefaultComboBoxModel(new Dataformat[0]);
      }

      dataFormat = new JComboBox(dfcbm1);
      if (selix >= 0)
        dataFormat.setSelectedIndex(selix);
      dataFormat.setRenderer(new DataFormatRenderer());
      con.add(dataFormat, c);

      c.gridx = 1;
      c.gridy = 4;
      con.add(new JLabel("Transfomer"), c);

      c.gridx = 2;
      DefaultComboBoxModel dfcbm2 = null;
      int selixx = -1;
      if (df != null) {

        Versioning aver = new Versioning(dwhrepRock);
        aver.setVersionid(df.getVersionid());
        VersioningFactory averf = new VersioningFactory(dwhrepRock, aver);

        Transformer tr = new Transformer(dwhrepRock);
        tr.setVersionid(averf.getElementAt(0).getVersionid());
        TransformerFactory trf = new TransformerFactory(dwhrepRock, tr);
        List transformers = trf.get();

        Iterator it = transformers.iterator();
        for (int i = 0; it.hasNext(); i++) {
          Transformer ttr = (Transformer) it.next();

          Interfacemeasurement imm = new Interfacemeasurement(dwhrepRock);
          imm.setDataformatid(df.getDataformatid());
          InterfacemeasurementFactory immf = new InterfacemeasurementFactory(dwhrepRock, imm);

          Interfacemeasurement imf = immf.getElementAt(0);

          if (ttr.getTransformerid().equals(imf.getTransformerid())) {
            selixx = i;
            break;
          }
        }

        dfcbm2 = new DefaultComboBoxModel((Transformer[]) transformers.toArray(new Transformer[transformers.size()]));

      } else {

        dfcbm2 = new DefaultComboBoxModel(new Transformer[0]);
      }

      transformer = new JComboBox(dfcbm2);
      if (selixx >= 0)
        transformer.setSelectedIndex(selixx);
      transformer.setRenderer(new TransformerRenderer());
      con.add(transformer, c);

      c.gridx = 1;
      c.gridy = 5;
      con.add(new JLabel("Status"), c);

      c.gridx = 2;
      Integer[] stats = { new Integer(0), new Integer(1) };
      status = new JComboBox(stats);
      status.setRenderer(new StatusRenderer());
      if (im != null && im.getStatus().intValue() == 1)
        status.setSelectedIndex(1);
      else
        status.setSelectedIndex(0);
      con.add(status, c);

      c.gridx = 1;
      c.gridy = 6;
      con.add(new JLabel("Description"), c);

      c.gridx = 2;
      comment = new JTextArea(4, 25);
      comment.setLineWrap(true);
      comment.setWrapStyleWord(true);
      if (im != null && im.getDescription() != null)
        comment.setText(im.getDescription());
      con.add(comment, c);

      c.gridx = 1;
      c.gridy = 7;
      JButton discard = new JButton("Discard", ConfigTool.delete);
      discard.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent ae) {
          commit = false;
          setVisible(false);
        }
      });
      con.add(discard, c);

      c.gridx = 2;
      JButton save = new JButton("Save", ConfigTool.check);
      save.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent ae) {

          String error = "";

          if (tagID != null && tagID.getText().trim().length() <= 0)
            error += "TagID must be defined\n";

          if (dataFormat.getSelectedItem() == null)
            error += "DataFormat must be selected\n";

          if (error.length() > 0) {
            JOptionPane.showMessageDialog(DataFormatWindow.this, error, "Invalid configuration",
                JOptionPane.ERROR_MESSAGE);
            return;
          }

          ConfigTool.reloadConfig();
          commit = true;
          setVisible(false);
        }
      });
      con.add(save, c);

      c.gridx = 3;
      c.gridy = 8;
      c.insets = new Insets(0, 0, 0, 0);
      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      pack();
      setVisible(true);

      if (!commit)
        return;

      boolean newDataFormat = false;
      
      if (im == null) {
        im = new Interfacemeasurement(dwhrepRock);
        newDataFormat = true;
      }

      im.setInterfacename(di.getInterfacename());
      if (tagID != null) {
        im.setTagid(tagID.getText().trim());
      }

      im.setStatus(new Long(status.getSelectedIndex()));
      im.setModiftime(new Timestamp(new Date().getTime()));
      im.setDescription(comment.getText());
      im.setDataformatid(((Dataformat) dataFormat.getSelectedItem()).getDataformatid());

      if (transformer.getSelectedItem() != null) {
        im.setTransformerid(((Transformer) transformer.getSelectedItem()).getTransformerid());
      } else
        im.setTransformerid(null);

      if (newOne) {
        im.insertDB();
      } else {
        im.updateDB();
      }

      Interfacemeasurement aim = new Interfacemeasurement(dwhrepRock);
      aim.setInterfacename(di.getInterfacename());
      InterfacemeasurementFactory aimf = new InterfacemeasurementFactory(dwhrepRock, aim);
      Vector ims = aimf.get();

      if (ims == null)
        ims = new Vector();

      ims.add(im);

    } catch (Exception e) {
      ErrorDialog ed = new ErrorDialog(parent, "Error", "Storing to DB failed", e);
    }

  }

  public boolean committed() {
    return commit;
  }

  public class StatusRenderer extends JLabel implements ListCellRenderer {

    private Border noFocusBorder;

    /**
     * Constructs a default renderer object for an item in a list.
     */
    public StatusRenderer() {
      super();

      noFocusBorder = new EmptyBorder(1, 1, 1, 1);

      setOpaque(true);
      setBorder(noFocusBorder);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {

      Integer stat = (Integer) value;

      setComponentOrientation(list.getComponentOrientation());
      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      } else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }

      if (stat.intValue() == 1)
        setText("enabled");
      else
        setText("disabled");

      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

      return this;
    }

    public void validate() {
    }

    public void revalidate() {
    }

    public void repaint(long tm, int x, int y, int width, int height) {
    }

    public void repaint(Rectangle r) {
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
      if (propertyName == "text")
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
    }

    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
    }

    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
    }

    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
    }

    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
    }

    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
    }

    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }

  };

  public class TransformerRenderer extends JLabel implements ListCellRenderer {

    private Border noFocusBorder;

    /**
     * Constructs a default renderer object for an item in a list.
     */
    public TransformerRenderer() {
      super();

      noFocusBorder = new EmptyBorder(1, 1, 1, 1);

      setOpaque(true);
      setBorder(noFocusBorder);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {

      Transformer format = (Transformer) value;

      setComponentOrientation(list.getComponentOrientation());
      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      } else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }

      if (format != null)
        setText(format.getTransformerid());
      else
        setText("Not available");

      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

      return this;
    }

    public void validate() {
    }

    public void revalidate() {
    }

    public void repaint(long tm, int x, int y, int width, int height) {
    }

    public void repaint(Rectangle r) {
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
      if (propertyName == "text")
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
    }

    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
    }

    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
    }

    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
    }

    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
    }

    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
    }

    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }

  };

  public class DataFormatRenderer extends JLabel implements ListCellRenderer {

    private Border noFocusBorder;

    /**
     * Constructs a default renderer object for an item in a list.
     */
    public DataFormatRenderer() {
      super();

      noFocusBorder = new EmptyBorder(1, 1, 1, 1);

      setOpaque(true);
      setBorder(noFocusBorder);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {

      Dataformat format = (Dataformat) value;

      setComponentOrientation(list.getComponentOrientation());
      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      } else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }

      if (format != null)
        setText(format.getDataformatid());
      else
        setText("Not available");

      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

      return this;
    }

    public void validate() {
    }

    public void revalidate() {
    }

    public void repaint(long tm, int x, int y, int width, int height) {
    }

    public void repaint(Rectangle r) {
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
      if (propertyName == "text")
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
    }

    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
    }

    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
    }

    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
    }

    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
    }

    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
    }

    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }

  };

}
