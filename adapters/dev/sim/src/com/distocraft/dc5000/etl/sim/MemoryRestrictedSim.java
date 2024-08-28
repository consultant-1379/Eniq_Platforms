package com.distocraft.dc5000.etl.sim;


/**
 * Created on 6th of May 2009
 * Interface for technology specific sim implementations having problems with memory.
 * @author unknown
 */
public interface MemoryRestrictedSim extends Sim {
  
  /**
   * Parses the source file specified by SourceFile object.
   * @param sf SourceFile for parsing.
   * @throws Exception thrown in case of failure.
   */
  int memoryConsumptionMB();
  void setMemoryConsumptionMB(int memoryConsumptionMB);
}
