/**
 * 
 */
package tableTreeUtils;

import java.awt.Component;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JPopupMenu;

import com.ericsson.eniq.component.SubTableModel;
import com.ericsson.eniq.component.TableComponentListener;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.TTTableModel;


/**
 * @author eheijun
 *
 */
public abstract class TCTableModel extends TTTableModel implements Observer, SubTableModel {
  
  protected Vector<Object> copyOfTheData;

  public TCTableModel(final RockFactory RF, final Vector<TableInformation> tableInfos, final boolean editable) {
    super(RF, tableInfos, editable);
  }

  /* (non-Javadoc)
   * @see tableTree.TTTableModel#copyOf(java.lang.Object)
   */
  @Override
  public abstract Object copyOf(Object toBeCopied);

  /* (non-Javadoc)
   * @see tableTree.TTTableModel#createNew()
   */
  @Override
  public abstract RockDBObject createNew();

  /* (non-Javadoc)
   * @see tableTree.TTTableModel#deleteData(java.lang.Object)
   */
  @Override
  protected abstract void deleteData(Object rockObject) throws SQLException, RockException;

  /* (non-Javadoc)
   * @see tableTree.TTTableModel#getColumnFilterForTableType(int)
   */
  @Override
  public abstract String getColumnFilterForTableType(int column);

  /* (non-Javadoc)
   * @see tableTree.TTTableModel#getColumnValueAt(java.lang.Object, int)
   */
  @Override
  public abstract Object getColumnValueAt(Object dataObject, int columnIndex);

  /* (non-Javadoc)
   * @see tableTree.TTTableModel#getOriginalValueAt(int, int)
   */
  @Override
  public abstract Object getOriginalValueAt(int rowIndex, int columnIndex);

  /* (non-Javadoc)
   * @see tableTree.TTTableModel#getValueAt(int, int)
   */
  @Override
  public abstract Object getValueAt(int rowIndex, int columnIndex);

  /* (non-Javadoc)
   * @see tableTree.TTTableModel#isColumnFilteredForTableType(int)
   */
  @Override
  public abstract boolean isColumnFilteredForTableType(int column);

  /* (non-Javadoc)
   * @see tableTree.TTTableModel#saveData(java.lang.Object)
   */
  @Override
  protected abstract void saveData(Object rockObject) throws SQLException, RockException;

  /* (non-Javadoc)
   * @see tableTree.TTTableModel#setValueAt(java.lang.Object, int, int)
   */
  @Override
  public abstract void setValueAt(Object value, int row, int col);

  /* (non-Javadoc)
   * @see tableTree.TTTableModel#update(java.util.Observable, java.lang.Object)
   */
  @Override
  public abstract void update(Observable sourceObject, Object sourceArgument);

  /**
   * Create copyOfTheData  
   */
  public abstract void startEditing();
  
  /**
   * Get original data 
   */
  public abstract Vector<Object> getData();
  
  /**
   * Rollback changes  
   */
  public abstract void cancelEditing();
  
  /**
   * After edit functionality goes here
   */
  public abstract void stopEditing();
  
  /**
   * Popup menu implementation
   */
  public abstract JPopupMenu getPopUpMenu(final TableComponentListener listener, Component component);
  
}
