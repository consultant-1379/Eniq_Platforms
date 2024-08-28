package com.ericsson.eniq.techpacksdk.view.measurement;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.distocraft.dc5000.repository.dwhrep.Versioning;

import tableTreeUtils.PairComponent;

//JUNIT TEST.

@RunWith(JMock.class)
public class MeasurementTypeParameterModelTest {
  
  private static Method setEnabelesAndDisables;
  private static Field customPlaceholders;
  private static Field productPlaceholders;
  private static Field compDeltaCalcSupport;
  
  // the jMock mockery.
  static Mockery context;
  private Mockery concreteContext1;
  private Mockery concreteContext2;
  private Mockery concreteContext3;

  
  private Sequence customPlaceholdersSequence;
  private MeasurementtypeExt measurementtypeExtMock;
  private Versioning versioningMock;
  private PairComponent customPlaceholdersMock;
  private PairComponent productPlaceholdersMock;
  private PairComponent compDeltaCalcSupportMock;
  private MeasurementTypeParameterModel measurementTypeParameterModel;
  
  @Before
  public void setUp() throws Exception {
    context = new JUnit4Mockery();
    concreteContext1 = new JUnit4Mockery() {{
      setImposteriser(ClassImposteriser.INSTANCE);
    }};
    concreteContext2 = new JUnit4Mockery() {{
      setImposteriser(ClassImposteriser.INSTANCE);
    }};
    concreteContext3 = new JUnit4Mockery() {{
      setImposteriser(ClassImposteriser.INSTANCE);
    }};

  }
  @Before
  public void setup(){
    customPlaceholdersSequence = context.sequence("customPlaceholdersSequence");
    
    measurementtypeExtMock = concreteContext1.mock(MeasurementtypeExt.class);
    versioningMock              = concreteContext1.mock(Versioning.class);
    customPlaceholdersMock   = concreteContext1.mock(PairComponent.class);
    productPlaceholdersMock  = concreteContext2.mock(PairComponent.class);
    compDeltaCalcSupportMock = concreteContext3.mock(PairComponent.class);    

    measurementTypeParameterModel =  new MeasurementTypeParameterModel(null, versioningMock, null, measurementtypeExtMock, null, null, null, null, false);
    try {
      customPlaceholders     = MeasurementTypeParameterModel.class.getDeclaredField("customPlaceholders");
      productPlaceholders    = MeasurementTypeParameterModel.class.getDeclaredField("productPlaceholders");
      compDeltaCalcSupport   = MeasurementTypeParameterModel.class.getDeclaredField("compDeltaCalcSupport");
      setEnabelesAndDisables = MeasurementTypeParameterModel.class.getDeclaredMethod("setEnabelesAndDisables", new Class[] {int.class});
      setEnabelesAndDisables.setAccessible(true);
      customPlaceholders.setAccessible(true);
      customPlaceholders.set(measurementTypeParameterModel, customPlaceholdersMock);
      productPlaceholders.setAccessible(true);
      productPlaceholders.set(measurementTypeParameterModel, productPlaceholdersMock);
      compDeltaCalcSupport.setAccessible(true);
      compDeltaCalcSupport.set(measurementTypeParameterModel, compDeltaCalcSupportMock);
    } catch (SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

}
  
  
  
  @Test
  public void testSetEnabelesAndDisablesRankTable(){
    
    
    try{
      concreteContext1.checking(new Expectations(){
      {
        this.oneOf(measurementtypeExtMock).setUseplaceholders(false);
        
        oneOf(customPlaceholdersMock).getComponent().setEnabled(false); 
        oneOf(customPlaceholdersMock).setVisible(false);
        oneOf(customPlaceholdersMock).setVisible(true); 
        oneOf(customPlaceholdersMock).getComponent().setEnabled(true);
        
        oneOf(measurementtypeExtMock).setUseplaceholders(true);
         
        allowing(versioningMock).getTechpack_type();
          will(returnValue("PM"));
      }
    });
      concreteContext2.checking(new Expectations(){
        {
          oneOf(productPlaceholdersMock).getComponent().setEnabled(false); 
          oneOf(productPlaceholdersMock).setVisible(false); 
          oneOf(productPlaceholdersMock).setVisible(true); 
          oneOf(productPlaceholdersMock).getComponent().setEnabled(true); 
        }
      });      
      concreteContext3.checking(new Expectations(){
        {
          this.oneOf(compDeltaCalcSupportMock).getComponent();
        }
      });      
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    
    try {
      int one = 1;
      setEnabelesAndDisables.invoke(measurementTypeParameterModel, new Object[] {one});
    } catch (SecurityException e) {
      e.printStackTrace();
      fail("SecurityException");
    }catch (IllegalArgumentException e) {
      e.printStackTrace();
      fail("IllegalArgumentException");
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      fail("IllegalAccessException");
    } catch (InvocationTargetException e) {
      e.printStackTrace();
      fail("InvocationTargetException");
    }
  }
  @Test
  public void testSetEnabelesAndDisablesRankTableCUSTOMTechPack(){
    
    
    try{
      concreteContext1.checking(new Expectations(){
      {
        this.oneOf(measurementtypeExtMock).setUseplaceholders(false);
        
        oneOf(customPlaceholdersMock).getComponent().setEnabled(false); 
        oneOf(customPlaceholdersMock).setVisible(false);
        oneOf(customPlaceholdersMock).setVisible(true); 
        never(customPlaceholdersMock).getComponent().setEnabled(true);
        
        oneOf(measurementtypeExtMock).setUseplaceholders(true);
         
        allowing(versioningMock).getTechpack_type();
          will(returnValue("CUSTOM"));
      }
    });
      concreteContext2.checking(new Expectations(){
        {
          oneOf(productPlaceholdersMock).getComponent().setEnabled(false); 
          oneOf(productPlaceholdersMock).setVisible(false); 
          oneOf(productPlaceholdersMock).setVisible(true); 
          never(productPlaceholdersMock).getComponent().setEnabled(true); 
        }
      });      
      concreteContext3.checking(new Expectations(){
        {
          this.oneOf(compDeltaCalcSupportMock).getComponent();
        }
      });      
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    
    try {
      int one = 1;
      setEnabelesAndDisables.invoke(measurementTypeParameterModel, new Object[] {one});
    } catch (SecurityException e) {
      e.printStackTrace();
      fail("SecurityException");
    }catch (IllegalArgumentException e) {
      e.printStackTrace();
      fail("IllegalArgumentException");
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      fail("IllegalAccessException");
    } catch (InvocationTargetException e) {
      e.printStackTrace();
      fail("InvocationTargetException");
    }
  }
  @Test
  public void testSetEnabelesAndDisablesCalcTable(){
    
    
    try{
      concreteContext1.checking(new Expectations(){
      {
        oneOf(measurementtypeExtMock).setUseplaceholders(false);       
        oneOf(customPlaceholdersMock).getComponent().setEnabled(false);        
        oneOf(customPlaceholdersMock).setVisible(false);
        never(customPlaceholdersMock).setVisible(true);
      }
    });
      concreteContext2.checking(new Expectations(){
        {
          oneOf(productPlaceholdersMock).getComponent().setEnabled(false);
          oneOf(productPlaceholdersMock).setVisible(false);
          never(productPlaceholdersMock).setVisible(true);
        }
      });      
      concreteContext3.checking(new Expectations(){
        {
          exactly(2).of(compDeltaCalcSupportMock).getComponent();
        }
      });      
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    
    try {
      int value = 3;
      setEnabelesAndDisables.invoke(measurementTypeParameterModel, new Object[] {value});
    } catch (SecurityException e) {
      e.printStackTrace();
      fail("SecurityException");
    }catch (IllegalArgumentException e) {
      e.printStackTrace();
      fail("IllegalArgumentException");
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      fail("IllegalAccessException");
    } catch (InvocationTargetException e) {
      e.printStackTrace();
      fail("InvocationTargetException");
    }
  }

}
