/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import java.util.Vector;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class HierarchyNetworkVariable
{
	private int id;
	private String labelA, labelB;
	private DocumentNode docA, docB;
	private HierarchyNetwork hn;
	private boolean singleDoc;
	private HierarchyNetworkComponent componentA, componentB;
	private Vector<HierarchyNetworkVariable> connectionsA, connectionsB;

	public HierarchyNetworkVariable (HierarchyNetwork hn, String labelA, String labelB, DocumentNode docA, DocumentNode docB, HierarchyNetworkComponent componentA, HierarchyNetworkComponent componentB)
	{
		this.hn = hn;
		this.id = hn.getNextVariableID ();
		this.labelA = labelA;
		this.labelB = labelB;
		this.docA = docA;
		this.docB = docB;
		this.componentA = componentA;
		this.componentB = componentB;
		singleDoc = false;
	}
	
	public void addConnectionA (HierarchyNetworkVariable var)
	{
		connectionsA.add (var);
	}
	
	public void addConnectionB (HierarchyNetworkVariable var)
	{
		connectionsB.add (var);
	}
	
	public void setComponentA (HierarchyNetworkComponent component)
	{
		this.componentA = component;
	}
	
	public void setComponentB (HierarchyNetworkComponent component)
	{
		this.componentB = component;
	}
	
	public HierarchyNetworkComponent getComponent ()
	{
		if (componentA == null)
			return componentB;
		
		if (componentB == null || componentA == componentB)
			return componentA;
		return null;
	}
	
	public void setDocA (DocumentNode docA)
	{
		this.docA = docA;
	}
	
	public void setLabelA (String labelA)
	{
		this.labelA = labelA;
	}
	
	public void setDocB (DocumentNode docB)
	{
		this.docB = docB;
	}
	
	public void setLabelB (String labelB)
	{
		this.labelB = labelB;
	}
	
	public DocumentNode getA ()
	{
		return docA;
	}
	
	public DocumentNode getB ()
	{
		return docB;
	}
	
	public String getId ()
	{
		return "s" + id;
	}
	
	public String getLabel ()
	{
		if (labelA == null)
			return labelB;
		if (labelB == null)
			return labelA;
		if (labelA.equals (labelB))
			return labelA;
		return labelA + " -> " + labelB;
	}
	
	public int getModification ()
	{
		if (singleDoc)
			return CRN.UNMODIFIED;
		
		if (labelA == null)
			return CRN.INSERT;
		if (labelB == null)
			return CRN.DELETE;
		if (docA.hasModification (TreeNode.MODIFIED|TreeNode.SUB_MODIFIED) || docB.hasModification (TreeNode.MODIFIED|TreeNode.SUB_MODIFIED) || componentA != componentB)
			return CRN.MODIFIED;
		return CRN.UNMODIFIED;
	}

	public void setSingleDocument ()
	{
		singleDoc = true;
	}
	
}
