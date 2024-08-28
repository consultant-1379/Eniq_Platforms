/**
 * 
 */
package com.ericsson.eniq.licensing.cache;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import com.ericsson.eniq.licensing.cache.MappingDescriptor.MappingType;

/**
 * @author ecarbjo
 * 
 */
public class FeatureMapper {

  private final Vector<FeatureMapping> mappingTable = new Vector<FeatureMapping>();

  private static Logger log;

  /**
   * @param mappingFile
   */
  public FeatureMapper(final LicensingSettings settings) {
    log = Logger.getLogger("licensing.cache.FeatureMapper");
    reloadMapping(settings.getMappingFiles());
  }

  /**
   * Load new mapping from the file(s) given. Loading mappings will clear the
   * old mapping table, so all files that are to be loaded need to be included
   * in this call at once (since successive calls will all clear the old data)
   */
  public void reloadMapping(final String[] mappingFiles) {

    if (mappingFiles == null || mappingFiles.length == 0) {
      log.warning("Cannot reload mapping tables as no files were specified");
    } else {
      // clear old mapping table. Synchronize on mappingTable so that other
      // threads can't read
      // it while it is not completely updated.
      synchronized (mappingTable) {
        mappingTable.clear();

        // loop through all files and create mapping objects from the input.
        for (int i = 0; i < mappingFiles.length; i++) {
          BufferedReader br = null;
          try {
            br = new BufferedReader(new FileReader(mappingFiles[i]));
            String in;

            // add all lines from the read file as feature mappings.
            // FeatureMapping should make sure that only valid mappings are
            // added.
            while ((in = br.readLine()) != null) {
              in = in.trim();
              if(in.length() == 0){
                // Skip empty lines...
                continue;
              }
              final FeatureMapping fm = new FeatureMapping(in);
              if (mappingTable.contains(fm)) {
                log.finest("Mapping " + fm + " already exists in mapping table. Skipping.");
              } else {
                log.finest("Adding mapping " + fm + " to mapping table");
                mappingTable.add(fm);
              }
            }
          } catch (IOException e) {
            log.config("Could not read the feature mapping file " + mappingFiles[i]
                + ". Please check that this file exsits and is readable.");
          } finally {
            try {
              // close the reader.
              br.close();
            } catch (Exception e) {
              log.finer("Could not close reader for " + mappingFiles[i]);
            }
          }
        }
      }
    }

    log.info("Mapping data reloaded");
  }

  /**
   * This method returns a vector of strings that contain all matching mappings
   * for the given license.
   * 
   * @param license
   *          the license to map
   * @return Vector<String> of matching mappings.
   */
  public Vector<String> map(final MappingDescriptor md) {
    log.finest("enter map()");

    // check the mapping type to know what kind of result we should return.
    if (md.getType() == MappingType.DESCRIPTION) {
      log.finest("Trying to map " + md.getName() + " onto a description");
      // fetch the description from the mapping table and check the validity.
      // Either return null, or a vector with one element.
      final String desc = getDescription(md);
      if (desc == null) {
        return null;
      } else {
        final Vector<String> retVector = new Vector<String>();
        retVector.add(desc);
        return retVector;
      }
    } else if (md.getType() == MappingType.FAJ) {
      log.finest("Trying to map " + md.getName() + " onto a FAJ number");
      // fetch the faj number and check the validity. Either return null, or a
      // vector with a single element.
      final String faj = getFajNumber(md);
      if (faj == null) {
        return null;
      } else {
        final Vector<String> retVector = new Vector<String>();
        retVector.add(faj);
        return retVector;
      }
    } else if (md.getType() == MappingType.INTERFACE) {
      log.finest("Trying to map " + md.getName() + " onto one or more interfaces");
      // return the mapped interfaces. getInterfaces will return null if no
      // mappings were found for this feature name.
      return getInterfaces(md);
    } else if (md.getType() == MappingType.REPORTPACKAGE) {
      log.finest("Trying to map " + md.getName() + " onto one or more report packages");
      // return the mapped report packages. getReportPackages will return null
      // if no
      // mappings were found for this feature name.
      return getReportPackages(md);
    } else {
      // we received a request for an unknown mapping type.
      log.finer("Cannot map " + md.getName() + " because it requests an unknown mapping type " + md.getType());
      return null;
    }
  }

  /**
   * Returns a vector of interfaces for the given feature name(s)
   * 
   * @param md
   *          the descriptor containing the feature names that should be mapped
   * @return the interface names corresponding to the given feature names
   */
  protected Vector<String> getInterfaces(final MappingDescriptor md) {
    // create a new vector for the interfaces
    final Vector<String> ifaces = new Vector<String>();

    // get the cxc name.
    final String[] cxc = md.getFeatureNames();
    if (cxc == null || cxc.length < 1) {
      log.warning("Could not map " + md.getName() + " onto an interface because of too few tokens");
      return null;
    } else {
      // loop through the mapping table and add any found match to the interface
      // vector
      final Enumeration<FeatureMapping> elements = mappingTable.elements();
      while (elements.hasMoreElements()) {
        final FeatureMapping fm = elements.nextElement();
        for (String aCxc : cxc) {
          if (aCxc.equals(fm.getFeatureName()) && fm.getInterfaceName() != null) {
            if (aCxc.equals(fm.getFeatureName()) && fm.getInterfaceName() != null) {
              // make sure not to add interfaces multiple times.
              if (!ifaces.contains(fm.getInterfaceName())) {
                ifaces.add(fm.getInterfaceName());
              }
            }
          }
        }
      }
    }

    // return the found interfaces
    if (ifaces.size() > 0) {
      log.finer("Found " + ifaces.size() + " interface mappings for " + md.getName());
      return ifaces;
    } else {
      // we found no interfaces, return null instead of an empty vector
      return null;
    }
  }

  /**
   * Maps the given feature name onto a FAJ number if such a mapping exists
   * 
   * @param md
   *          the descriptor containing the feature names that should be mapped
   * @return the FAJ number for the given feature.
   */
  protected String getFajNumber(final MappingDescriptor md) {
    // get the feature name and check it for null.
    final String[] cxc = md.getFeatureNames();
    if (cxc == null || cxc.length > 1 || cxc.length < 1) {
      log.finest("Could not map " + md.getName() + " onto a FAJ number because of too few, or too many, tokens");
      return null;
    } else {
      // loop through the mapping table, and return the first mapping that is
      // found. (there should be only one)
      final Enumeration<FeatureMapping> elements = mappingTable.elements();
      while (elements.hasMoreElements()) {
        final FeatureMapping fm = elements.nextElement();
        if (cxc[0].equals(fm.getFeatureName()) && fm.getFajNumber() != null) {
          return fm.getFajNumber();
        }
      }
    }

    // we didn't find anything. Return null.
    return null;
  }

  /**
   * Fetches the feature description for the given feature name.
   * 
   * @param md
   *          the descriptor containing the feature names that should be mapped
   * @return the description for the given feature
   */
  protected String getDescription(final MappingDescriptor md) {
    // get and check the cxc number.
    final String[] cxc = md.getFeatureNames();
    if (cxc == null || cxc.length > 1 || cxc.length < 1) {
      log.warning("Could not map " + md.getName() + " onto a description because of too few, or too many tokens");
      return null;
    } else {
      // return the first matching mapping, since there should be only one
      // present in the mapping table.
      final Enumeration<FeatureMapping> elements = mappingTable.elements();
      while (elements.hasMoreElements()) {
        final FeatureMapping fm = elements.nextElement();
        if (cxc[0].equals(fm.getFeatureName()) && fm.getDescription() != null) {
          return fm.getDescription();
        }
      }
    }

    // we didn't find anything, return null.
    return null;
  }

  /**
   * Get the number of mappings that this mapper currently has.
   * 
   * @return the size of the mapping table.
   */
  public int getMappingSize() {
    if (mappingTable == null) {
      return 0;
    } else {
      return mappingTable.size();
    }
  }

  /**
   * Clear the mapping table. Used primarily for unit testing.
   */
  public void clear() {
    mappingTable.clear();
  }

  /**
   * Returns a vector of report package names for the given feature name(s)
   * 
   * @param md
   *          the descriptor containing the feature names that should be mapped
   * @return the report package names corresponding to the given feature names
   */
  protected Vector<String> getReportPackages(final MappingDescriptor md) {
    // create a new vector for the report packages.
    final Vector<String> reportPackages = new Vector<String>();

    // get the cxc name.
    final String[] cxc = md.getFeatureNames();
    if (cxc == null || cxc.length < 1) {
      log.warning("Could not map " + md.getName() + " onto an report package because of too few tokens");
      return null;
    } else {
      // loop through the mapping table and add any found match to the report
      // packages
      // vector
      final Enumeration<FeatureMapping> elements = mappingTable.elements();
      while (elements.hasMoreElements()) {
        final FeatureMapping fm = elements.nextElement();
        for (int i = 0; i < cxc.length; i++) {
          if (cxc[i].equals(fm.getFeatureName()) && fm.getReportPackage() != null) {

            if (!reportPackages.contains(fm.getReportPackage())) {
              reportPackages.add(fm.getReportPackage());
            }
          }
        }
      }
      // return the found report packages
      if (reportPackages.size() > 0) {
        log.finer("Found " + reportPackages.size() + " report package mappings for " + md.getName());
        return reportPackages;
      } else {
        // we found no report packages, return null instead of an empty vector
        return null;
      }

    }
  }
}
