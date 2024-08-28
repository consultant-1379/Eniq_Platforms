package com.ericsson.eniq.techpacksdk;



@SuppressWarnings("serial")
public class LimitedSizePropertyTextArea extends LimitedSizeTextArea {

  public LimitedSizePropertyTextArea(final int limit, final boolean required, final int rows, final int cols) {
    super(limit, required, rows, cols);    
  }
  
  /**
   * 
   */
  public String getText (){
    String text = super.getText();
    String t = text.replaceAll("\\n", " \\\\"+ "\n");
    return t;
  }
}
