package com.ericsson.eniq.techpacksdk;

import static com.ericsson.eniq.techpacksdk.LimitedSizeTextField.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class LimitedTextGroup {

  List<JTextComponent> container = null;
  int mandCnt;
  private Color neutralBg;

  public LimitedTextGroup(int mandCnt) {
    this.mandCnt = mandCnt;
    container = new ArrayList<JTextComponent>();
  }

  public void add(JTextComponent tc) {
    neutralBg = tc.getBackground();
    container.add(tc);
    tc.getDocument().addDocumentListener(new OneRequiredWatch(this));
  }
  
  public void doValidation (){
    int filledCnt = 0;
    for (JTextComponent tc : container){
      if (tc.getText().length() > 0) {
        filledCnt++;
      }
    }
    
    boolean ok = (filledCnt == mandCnt);
    
    for (JTextComponent tc : container){
      Color bg = ok ? neutralBg : ERROR_BG;
      tc.setBackground(bg);
    }              
  }

  /**
   * Listener
   * 
   * @author etogust
   *
   */
  public class OneRequiredWatch implements DocumentListener {

    LimitedTextGroup ltg = null;

    public OneRequiredWatch(LimitedTextGroup ltg) {
      this.ltg= ltg;
    }

    public void changedUpdate(DocumentEvent e) {
      this.ltg.doValidation();
    }

    public void insertUpdate(DocumentEvent e) {
      changedUpdate(e);
    }

    public void removeUpdate(DocumentEvent e) {
      changedUpdate(e);
    }

  };

}
