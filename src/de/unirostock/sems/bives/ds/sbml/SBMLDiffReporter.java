package de.unirostock.sems.bives.ds.sbml;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;


public interface SBMLDiffReporter
{
	public static final String CLASS_DELETED = "deleted";
	public static final String CLASS_INSERTED = "inserted";
	public static final String CLASS_ATTRIBUTE = "attr";
	
	/*public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB);
	public String reportInsert ();
	public String reportDelete ();*/
	
	public MarkupElement reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB, MarkupDocument markupDocument);
	public MarkupElement reportInsert (MarkupDocument markupDocument);
	public MarkupElement reportDelete (MarkupDocument markupDocument);
	
}
