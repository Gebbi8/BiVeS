/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.MathML;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;


/**
 * @author Martin Scharm
 *
 */
public class SBMLSpeciesReference
	extends SBMLSimpleSpeciesReference
	implements SBMLDiffReporter
{
	private Double stoichiometry;
	private MathML stoichiometryMath;
	private boolean constant;
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLSpeciesReference (DocumentNode documentNode,
		SBMLModel sbmlModel) throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		if (documentNode.getAttribute ("stoichiometry") != null)
		{
			try
			{
				stoichiometry = Double.parseDouble (documentNode.getAttribute ("stoichiometry"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("stoichiometry of species reference "+id+" of unexpected format: " + documentNode.getAttribute ("stoichiometry"));
			}
		}
		else // level <= 2
		{
			// is there stoichiometryMath?
			Vector<TreeNode> maths = documentNode.getChildrenWithTag ("stoichiometryMath");
			if (maths.size () > 1)
				throw new BivesSBMLParseException ("SpeciesReference has "+maths.size ()+" stoichiometryMath elements. (expected not more than one element)");
			if (maths.size () == 1)
			{
				maths = ((DocumentNode) maths.elementAt (0)).getChildrenWithTag ("math");
				if (maths.size () != 1)
					throw new BivesSBMLParseException ("stoichiometryMath in SpeciesReference has "+maths.size ()+" math elements. (expected exactly one element)");
				stoichiometryMath = new MathML ((DocumentNode) maths.elementAt (0));
			}
			else
				stoichiometry = 1.;
		}
		
		if (documentNode.getAttribute ("constant") != null)
		{
			try
			{
				constant = Boolean.parseBoolean (documentNode.getAttribute ("constant"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("constant of species reference "+id+" of unexpected format: " + documentNode.getAttribute ("constant"));
			}
		}
		else
			constant = false; // level <= 2
	}

	@Override
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLSpeciesReference a = (SBMLSpeciesReference) docA;
		SBMLSpeciesReference b = (SBMLSpeciesReference) docB;
		//if (a.getDocumentNode ().getModification () == 0)
		//	return stoichiometry + species.getNameAndId ();

		//System.out.println (a + " - " + b);
		//System.out.println (a.species + " - " + b.species);
		
		String retA = a.stoichiometry + a.species.getNameAndId ();
		String retB = b.stoichiometry + b.species.getNameAndId ();
		
		if (retA.equals (retB))
			return retA;
		else
			return "<span class='"+CLASS_DELETED+"'>" + retA + "</span> + <span class='"+CLASS_INSERTED +"'>" + retB + "</span>";
	}

	@Override
	public String reportInsert ()
	{
		return "<span class='"+CLASS_INSERTED+"'>" + stoichiometry + species.getNameAndId () + "</span>";
	}

	@Override
	public String reportDelete ()
	{
		return "<span class='"+CLASS_DELETED+"'>" + stoichiometry + species.getNameAndId () + "</span>";
	}
	
}