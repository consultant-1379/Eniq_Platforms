/**
 * 
 */
package com.ericsson.eniq.techpacksdk.unittest.utils;

import static org.fest.assertions.Assertions.assertThat;

import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;

import com.ericsson.eniq.techpacksdk.unittest.fest.TTTableCellWriter;

import tableTree.TableTreeComponent;
import tableTreeUtils.PairComponent;
import tableTreeUtils.TableContainer;

/**
 * @author efaigha
 * 
 */
public abstract class TransformersUtils {

  /**
   * Finds textfield-fixture with title from given panelFixture
   * 
   * PRE-CONDITION: Param-node must be selected/visible
   * 
   * @param parameterFixture
   * @return JTextComponentFixture object
   */
  public static JTextComponentFixture findJTextComponentWithTitleAndJPanelFixture(final String textComponentTitle,
      JPanelFixture parameterFixture) {
    JTextComponentFixture textFix = parameterFixture.textBox(new GenericTypeMatcher<JTextComponent>(JTextComponent.class) {
    
      @Override
      protected boolean isMatching(JTextComponent c) {
        PairComponent pc = (PairComponent) c.getParent();
        if (textComponentTitle != null && textComponentTitle.equals(pc.getTitle())) {
          return true;
        } else {
          return false;
        }
      }
    });
    return textFix;
  }
  
  /**
   * Finds parameter panel under the given TableTreeComponent.
   * 
   * @param transformersTree
   * @param String TableTreeConponent Path
   * @return
   */
  
  public static JPanelFixture selectParameters(JTreeFixture transformersTree, String transformerNode,String measurementType) {
	  transformersTree.target.setRootVisible(true);
	  //transformersTree.target.collapseRow(0);
	    transformersTree.selectPath(transformersTree.target.getModel().getRoot().toString()
	    				+transformersTree.separator()+transformerNode.toLowerCase());   
	    int[] rowsNow = transformersTree.component().getSelectionRows();
	    assertThat(rowsNow).isNotNull().as("Selection in the Transformers tree is empty!");
	    transformersTree.toggleRow(rowsNow[0]);
	    transformersTree.toggleRow(rowsNow[0]+1);
	    transformersTree.component().setSelectionRow(rowsNow[0]+1);
	    transformersTree.component().startEditingAtPath(transformersTree.component().getSelectionPath());

	    DefaultMutableTreeNode comp = (DefaultMutableTreeNode) transformersTree.target.getLastSelectedPathComponent();
	    JTreeFixture ttcFix = new JTreeFixture(TechPackIdeStarter.getMyRobot(), (TableTreeComponent) comp.getUserObject());
	    assertThat(ttcFix).isNotNull().as("The TTC for node ALL is not found.");
	    
	  
	    ttcFix.selectPath(measurementType);
	    ttcFix.toggleRow(ttcFix.target.getSelectionRows()[0]);
	    int parameterRow = ttcFix.target.getSelectionRows()[0]+1;	  
	    ttcFix.toggleRow(parameterRow);	    
	    Pause.pause(1000);
	    ttcFix.selectPath(measurementType+"/"+"Parameters");
	    ttcFix.selectRow(ttcFix.target.getSelectionRows()[0]+1);
	    Pause.pause(2000);
	    
	    return new JPanelFixture(TechPackIdeStarter.getMyRobot(), CommonUtils.findParameterPanel());
	  }
  
  /**
   * Finds Transformation table under the given TableTreeComponent.
   * 
   * @param transformersTree
   * @param String TableTreeConponent Path
   * @return
   */
  
  public static JTableFixture selectTransformations(JTreeFixture transformersTree, String transformerNode,String measType) {
	  transformersTree.target.setRootVisible(true);
	   //transformersTree.target.collapseRow(0);
	    transformersTree.selectPath(transformersTree.target.getModel().getRoot().toString()
	    				+transformersTree.separator()+transformerNode.toLowerCase());   
	    int[] rowsNow = transformersTree.component().getSelectionRows();
	    assertThat(rowsNow).isNotNull().as("Selection in the Transformers tree is empty!");
	    //transformersTree.toggleRow(rowsNow[0]);
	    //transformersTree.toggleRow(rowsNow[0]+1);
	    transformersTree.component().setSelectionRow(rowsNow[0]+1);
	    transformersTree.component().startEditingAtPath(transformersTree.component().getSelectionPath());

	    DefaultMutableTreeNode comp = (DefaultMutableTreeNode) transformersTree.target.getLastSelectedPathComponent();
	    JTreeFixture ttcFix = new JTreeFixture(TechPackIdeStarter.getMyRobot(), (TableTreeComponent) comp.getUserObject());
	    assertThat(ttcFix).isNotNull().as("The TTC for node ALL is not found.");
	    
	  
	    ttcFix.selectPath(measType);
	    int allRow = ttcFix.target.getSelectionRows()[0];
	    ttcFix.toggleRow(allRow);
	    int transformationRow = ttcFix.target.getSelectionRows()[0]+2;	  
	    ttcFix.toggleRow(transformationRow);	    
	    Pause.pause(1000);
	    ttcFix.selectRow(transformationRow);
	    //ttcFix.selectPath(ttcPath+"/"+"Common Transformations");
	    ttcFix.selectRow(ttcFix.target.getSelectionRows()[0]+1);
	    Pause.pause(2000);
	    TableContainer table = CommonUtils.findTablePanel();	   
	   
	    JTableFixture tableFix = new JTableFixture(TechPackIdeStarter.getMyRobot(), table.getTable());
	    tableFix.cellWriter(new TTTableCellWriter(TechPackIdeStarter.getMyRobot()));
	    
	return tableFix;
	  
  }


}
