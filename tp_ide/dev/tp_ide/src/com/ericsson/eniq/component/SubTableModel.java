package com.ericsson.eniq.component;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import ssc.rockfactory.RockDBObject;


public interface SubTableModel {

  RockDBObject createNew();
  int getRowCount();
  int getColumnCount();
  Object getValueAt(int ind, int jnd);
  void setColumnEditors(JTable editTable);
  void setColumnRenderers(JTable editTable);
  TableModel getTableModel();
  void startEditing();
  Vector<? extends Object> getData();
  void cancelEditing();
  void stopEditing();
  void insertDataAtRow(Object toBeInserted, int i);
  void insertDataLast(Object toBeInserted);
  void duplicateRow(int[] selectedRows, int times);
  void removeSelectedData(int[] selectedRows);
  JPopupMenu getPopUpMenu(TableComponentListener listener, Component component);
  Vector<String> validateData();
  
}
