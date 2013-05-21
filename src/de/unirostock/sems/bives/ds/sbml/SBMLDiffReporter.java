package de.unirostock.sems.bives.ds.sbml;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;


public interface SBMLDiffReporter
{
	public static final String CLASS_DELETED = "deleted";
	public static final String CLASS_INSERTED = "inserted";
	public static final String CLASS_ATTRIBUTE = "attr";
	
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB);
	public String reportInsert ();
	public String reportDelete ();
	
}
