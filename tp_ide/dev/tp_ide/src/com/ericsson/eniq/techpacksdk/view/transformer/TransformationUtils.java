package com.ericsson.eniq.techpacksdk.view.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.distocraft.dc5000.repository.dwhrep.Transformation;

/**
 * Utility class for handling and comparing transformations. 
 * @author ECIACAH
 *
 */
public class TransformationUtils {

  private StringBuffer currentTrans = new StringBuffer();

  private StringBuffer otherTrans = new StringBuffer();

  /**
   * Search transformer transformations for individual transformation.
   * 
   * @param transformations
   *          The list of transformations to search.
   * @param tWeAreLookingFor
   *          The transformation we are looking for.
   * @return count The number of occurrences.
   */
  public int findNumOccurrences(List<Transformation> transformations, Transformation tWeAreLookingFor) {
    int count = 0;
    if (transformations != null && tWeAreLookingFor != null) {
      for (Transformation t1 : transformations) {
        if (checkIsTransformationEqual(t1, tWeAreLookingFor)) {
          count++;
        }
      }
    }
    return count;
  }

  /**
   * Search transformer transformations for individual transformation.
   * 
   * @param transformations
   *          The list of transformations to search.
   * @param tWeAreLookingFor
   *          The transformation we are looking for.
   * @return count The number of occurrences.
   */
  public int findNumOccurrencesWithObjects(List<Object> transformations, Transformation tWeAreLookingFor) {
    int count = 0;
    if (transformations != null || tWeAreLookingFor != null) {
      for (Object t1 : transformations) {
        if (checkIsTransformationEqual((Transformation) t1, tWeAreLookingFor)) {
          count++;
        }
      }
    }
    return count;
  }

  /**
   * Checks if two transformation objects are equal.
   * 
   * @param t1
   * @param t2
   * @return True if the transformations are equal based on config, source,
   *         target and type columns.
   */
  public boolean checkIsTransformationEqual(Transformation t1, Transformation t2) {
    if (t1 == t2)
      return true;
    if (t2 == null)
      return false;
    if (t1.getClass() != t2.getClass())
      return false;
    if (t1.getConfig() == null) {
      if (t2.getConfig() != null)
        return false;
    } else if (compareStringsWithoutCR(t1.getConfig(), t2.getConfig()) == false) {
      return false;
    }
    if (t1.getSource() == null) {
      if (t2.getSource() != null)
        return false;
    } else if (compareStringsWithoutCR(t1.getSource(), t2.getSource()) == false) {
      return false;
    }
    if (t1.getTarget() == null) {
      if (t2.getTarget() != null)
        return false;
    } else if (compareStringsWithoutCR(t1.getTarget(), t2.getTarget()) == false) {
      return false;
    }
    if (t1.getType() == null) {
      if (t2.getType() != null)
        return false;
    } else if (compareStringsWithoutCR(t1.getType(), t2.getType()) == false) {
      return false;
    }
    return true;
  }

  /**
   * Compare two strings without carriage returns and tab characters.
   * 
   * @param t1
   *          String value from first transformation.
   * @param t2
   *          String value from other transformation.
   * @return True if the strings are equal without extra characters.
   */
  private boolean compareStringsWithoutCR(final String t1, final String t2) {
    if (t1 == null || t2 == null) {
      return false;
    }

    // Blank the StringBuffers:
    currentTrans.delete(0, currentTrans.length());
    otherTrans.delete(0, otherTrans.length());

    // Strip out carriage returns and tabs. Some strings could have \n added if
    // the user presses return in the text field but they should be used in the
    // comparison.
    currentTrans.append(t1);
    currentTrans.replace(0, t1.length(), currentTrans.toString().replaceAll("\\n", ""));
    currentTrans.replace(0, t1.length(), currentTrans.toString().replaceAll("\\t", ""));

    otherTrans.append(t2);
    otherTrans.replace(0, t2.length(), otherTrans.toString().replaceAll("\\n", ""));
    otherTrans.replace(0, t2.length(), otherTrans.toString().replaceAll("\\t", ""));
    if (!currentTrans.toString().equals(otherTrans.toString())) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Searches for a transformation in a list of transformations.
   * 
   * @param searchT
   *          The transformation to search for.
   * @param transformations
   *          List of transformations.
   * @return Position of the transformation in the list. -1 if not found.
   */
  public int searchForTransformation(final Transformation searchT, final List<Transformation> transformations,
      final boolean compareTransformerID) {
    int position = -1;

    for (Transformation transformation : transformations) {
      position++;

      boolean doCheck = true;
      if (compareTransformerID) {
        doCheck = searchT.getTransformerid().equalsIgnoreCase(transformation.getTransformerid());
      }

      if (doCheck && checkIsTransformationEqual(searchT, transformation)) {
        return position;
      }
    }
    return -1;
  }

  /**
   * Looks up a transformation in a map. Returns the list of transformer id
   * strings associated with the transformation.
   * 
   * @param transformation
   *          The transformation we are looking for.
   * @param mappings
   *          The HashMap to search.
   * @return The list of transformer ids as strings that are mapped to the
   *         transformation.
   */
  public ArrayList<String> lookUpTransformationInMap(Transformation transformation,
      HashMap<Transformation, ArrayList<String>> mappings) {
    // Get the transformations from the map (the keys):
    List<Transformation> transformations = new ArrayList<Transformation>(mappings.keySet());
    
    // Get the list of transformer ids:
    ArrayList<String> tformers = null;
    int position = searchForTransformation(transformation, transformations, false);
    if (position >= 0) {
      tformers = mappings.get(transformations.get(position));
    }
    return tformers;
  }

}
