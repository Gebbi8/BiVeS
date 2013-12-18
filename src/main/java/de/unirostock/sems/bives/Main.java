/**
 * 
 */
package de.unirostock.sems.bives;

import java.io.File;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.GraphProducer;
import de.unirostock.sems.bives.algorithm.cellml.CellMLGraphProducer;
import de.unirostock.sems.bives.algorithm.sbml.SBMLGraphProducer;
import de.unirostock.sems.bives.api.CellMLDiff;
import de.unirostock.sems.bives.api.CellMLSingle;
import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.api.RegularDiff;
import de.unirostock.sems.bives.api.SBMLDiff;
import de.unirostock.sems.bives.api.SBMLSingle;
import de.unirostock.sems.bives.api.Single;
import de.unirostock.sems.bives.ds.cellml.CellMLDocument;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.tools.DocumentClassifier;
import de.unirostock.sems.bives.tools.Tools;
import de.unirostock.sems.bives.tools.XmlTools;
//import de.unirostock.sems.bives.algorithm.sbmldeprecated.SBMLDiffInterpreter;

//TODO: detect document type
//TODO: graph producer

/**
 * @author Martin Scharm
 *
 */
public class Main
{
	public static final int WANT_DIFF = 1;
	public static final int WANT_DOCUMENTTYPE = 2;
	public static final int WANT_META = 4;
	public static final int WANT_REPORT_MD = 8;
	public static final int WANT_REPORT_HTML = 16;
	public static final int WANT_CRN_GRAPHML = 32;
	public static final int WANT_CRN_DOT = 64;
	public static final int WANT_COMP_HIERARCHY_GRAPHML = 128;
	public static final int WANT_COMP_HIERARCHY_DOT = 256;
	public static final int WANT_REPORT_RST = 512;
	public static final int WANT_COMP_HIERARCHY_JSON = 1024;
	public static final int WANT_CRN_JSON = 2048;
	public static final int WANT_SBML = 4096;
	public static final int WANT_CELLML = 8192;
	public static final int WANT_REGULAR = 16384;

	// single
	public static final int WANT_SINGLE_CRN_GRAPHML = 32;
	public static final int WANT_SINGLE_CRN_DOT = 64;
	public static final int WANT_SINGLE_COMP_HIERARCHY_GRAPHML = 128;
	public static final int WANT_SINGLE_COMP_HIERARCHY_DOT = 256;
	public static final int WANT_SINGLE_COMP_HIERARCHY_JSON = 1024;
	public static final int WANT_SINGLE_CRN_JSON = 2048;
	
	
	public static final String REQ_FILES = "files";
	public static final String REQ_WANT = "get";
	public static final String REQ_WANT_META = "meta";
	public static final String REQ_WANT_DOCUMENTTYPE = "documentType";
	public static final String REQ_WANT_DIFF = "xmlDiff";
	public static final String REQ_WANT_REPORT_MD = "reportMd";
	public static final String REQ_WANT_REPORT_RST = "reportRST";
	public static final String REQ_WANT_REPORT_HTML = "reportHtml";
	public static final String REQ_WANT_CRN_GRAPHML = "crnGraphml";
	public static final String REQ_WANT_CRN_DOT = "crnDot";
	public static final String REQ_WANT_CRN_JSON = "crnJson";
	public static final String REQ_WANT_COMP_HIERARCHY_GRAPHML = "compHierarchyGraphml";
	public static final String REQ_WANT_COMP_HIERARCHY_DOT = "compHierarchyDot";
	public static final String REQ_WANT_COMP_HIERARCHY_JSON = "compHierarchyJson";

	public static final String REQ_WANT_SINGLE_CRN_GRAPHML = "singleCrnGraphml";
	public static final String REQ_WANT_SINGLE_CRN_DOT = "singleCrnDot";
	public static final String REQ_WANT_SINGLE_CRN_JSON = "singleCrnJson";
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML = "singleCompHierarchyGraphml";
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_DOT = "singleCompHierarchyDot";
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_JSON = "singleCompHierarchyJson";
	
	private class Option 
	{
		public String description;
		public int value;
		public Option (int value, String description)
		{
			this.description = description;
			this.value = value;
		}
	}

	private HashMap<String, Option> options;
	private HashMap<String, Option> addOptions;
	
	private void fillOptions ()
	{
		options = new HashMap<String, Option> ();
		//options.put ("--meta", new Option (WANT_META, "get meta information about documents"));
		//options.put ("--documentType", new Option (WANT_DOCUMENTTYPE, ""));
		options.put ("--xmlDiff", new Option (WANT_DIFF, "get the diff encoded in XML format"));
		options.put ("--reportMd", new Option (WANT_REPORT_MD, "get the report of changes encoded in MarkDown"));
		options.put ("--reportRST", new Option (WANT_REPORT_RST, "get the report of changes encoded in ReStructuredText"));
		options.put ("--reportHtml", new Option (WANT_REPORT_HTML, "get the report of changes encoded in HTML"));
		options.put ("--crnGraphml", new Option (WANT_CRN_GRAPHML, "get the highlighted chemical reaction network encoded in GraphML"));
		options.put ("--crnDot", new Option (WANT_CRN_DOT, "get the highlighted chemical reaction network encoded in DOT language"));
		options.put ("--crnJson", new Option (WANT_CRN_JSON, "get the highlighted chemical reaction network encoded in JSON"));
		options.put ("--compHierarchyGraphml", new Option (WANT_COMP_HIERARCHY_GRAPHML, "get the hierarchy of components in a CellML document encoded in GraphML"));
		options.put ("--compHierarchyDot", new Option (WANT_COMP_HIERARCHY_DOT, "get the hierarchy of components in a CellML document encoded in DOT language"));
		options.put ("--compHierarchyJson", new Option (WANT_COMP_HIERARCHY_JSON, "get the hierarchy of components in a CellML document encoded in JSON"));
		options.put ("--SBML", new Option (WANT_SBML, "force SBML comparison"));
		options.put ("--CellML", new Option (WANT_CELLML, "force CellML comparison"));
		options.put ("--regular", new Option (WANT_REGULAR, "force regular XML comparison"));
		
		addOptions = new HashMap<String, Option> ();
		addOptions.put ("--documentType", new Option (WANT_DOCUMENTTYPE, "get the documentType of an XML file"));
		addOptions.put ("--meta", new Option (WANT_META, "get some meta information about an XML file"));
		addOptions.put ("--singleCrnJson", new Option (WANT_SINGLE_CRN_JSON, "get the chemical reaction network of a single file encoded in JSON"));
		addOptions.put ("--singleCrnGraphml", new Option (WANT_SINGLE_CRN_GRAPHML, "get the chemical reaction network of a single file encoded in GraphML"));
		addOptions.put ("--singleCrnDot", new Option (WANT_SINGLE_CRN_DOT, "get the chemical reaction network of a single file encoded in DOT language"));
		addOptions.put ("--singleCompHierarchyJson", new Option (WANT_SINGLE_COMP_HIERARCHY_JSON, "get the hierarchy of components in a single CellML document encoded in JSON"));
		addOptions.put ("--singleCompHierarchyGraphml", new Option (WANT_SINGLE_COMP_HIERARCHY_GRAPHML, "get the hierarchy of components in a single CellML document encoded in GraphML"));
		addOptions.put ("--singleCompHierarchyDot", new Option (WANT_SINGLE_COMP_HIERARCHY_DOT, "get the hierarchy of components in a single CellML document encoded in DOT language"));
	}
	
	
	
	public void usage (String msg)
	{
		if (msg != null && msg.length () > 0)
		{
			System.err.println (msg);
			System.out.println ();
		}
		
		System.out.println ("ARGUMENTS:");
		System.out.println ("\t[option] FILE1 [FILE2]  compute the differences between 2 XML files");
		System.out.println ();
		System.out.println ("FILE1 and FILE2 define XML files to compare");
		System.out.println ();
		System.out.println ("OPTIONS:");
		SortedSet<String> keys = new TreeSet<String>(options.keySet());
		int longest = 0;
		for (String key : keys)
		{
			if (key.length () > longest)
				longest = key.length ();
		}
		SortedSet<String> addKeys = new TreeSet<String>(addOptions.keySet());
		for (String key : addKeys)
		{
			if (key.length () > longest)
				longest = key.length ();
		}
		
		longest += 2;
		System.out.println ("\tCOMMON OPTIONS");
		System.out.println ("\t[none]"+Tools.repeat (" ", longest - "[none]".length ()) +"expect XML files and print patch");
		System.out.println ("\t--help"+Tools.repeat (" ", longest - "--help".length ()) +"print this help");
		System.out.println ("\t--debug"+Tools.repeat (" ", longest - "--debug".length ()) +"enable verbose mode");
		System.out.println ("\t--debugg"+Tools.repeat (" ", longest - "--debugg".length ()) +"enable even more verbose mode");

		System.out.println ();
		System.out.println ("\tMAPPING OPTIONS");
		
		for (String key : keys)
			System.out.println ("\t"+key + Tools.repeat (" ", longest - key.length ()) + options.get (key).description);
		System.out.println ();

		System.out.println ("\tENCODING OPTIONS");
		System.out.println ("\tby default we will just dump the result to the terminal. Thus, it's only usefull if you call for one single output.");
		System.out.println ("\t--json"+Tools.repeat (" ", longest - "--json".length ()) +"encode results in JSON");
		System.out.println ("\t--xml"+Tools.repeat (" ", longest - "--xml".length ()) +"encode results in XML");
		System.out.println ();

		System.out.println ("\tADDITIONAL OPTIONS for single files");
		for (String key : addKeys)
			System.out.println ("\t"+key + Tools.repeat (" ", longest - key.length ()) + addOptions.get (key).description);
		System.out.println ();

		System.exit (2);
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main (String[] args) throws Exception
	{
		LOGGER.setLogToStdErr (false);
		LOGGER.setLogToStdOut (false);
		LOGGER.setLevel (LOGGER.ERROR);
		
		//args = new String [] {"test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--reportHtml", "--xml", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--reportRST", "--crnGraphml", "--json", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--reportRST", "--crnGraphml", "--json", "--CellML", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--reportRST", "--crnGraphml", "--json", "--regular", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--debugg", "--reportRST", "--crnGraphml", "--json", "--SBML", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--debugg", "--reportRST", "--crnGraphml", "--json", "--regular", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--meta", "test/BSA-ptinst-2012-11-11"};
		//args = new String [] {"--documentType", "test/BSA-ptinst-2012-11-11"};
		//args = new String [] {"--documentType", "test/BSA-ptinst-2012-11-11", "test/BSA-ptinst-2012-11-11"};
		//args = new String [] {"--debugg", "--reportHtml", "test/potato (3).xml", "test/potato (3).xml"};
		//args = new String [] {"--help"};
		args = new String [] {"--singleCompHierarchyJson", "test/bhalla_iyengar_1999_j_v1.cellml"};
		
		new Main ().run (args); 
	}
	
	private Main ()
	{
		fillOptions ();
	}
	
	@SuppressWarnings("unchecked")
	private void run (String[] args) throws Exception
	{
		
		Diff diff = null;
    File file1 = null, file2 = null;
    int output = 0;
  	int want = 0;
  	DocumentClassifier classifier = null;
    HashMap<String, String> toReturn = new HashMap<String, String> ();
    

    
    for (int i = 0; i < args.length; i++)
    {
    	Option o = options.get (args[i]);
    	if (o != null)
    	{
    		want |= o.value;
    		continue;
    	}
    	o = addOptions.get (args[i]);
    	if (o != null)
    	{
    		want |= o.value;
    		continue;
    	}
    	
    	if (args[i].equals ("--debug"))
    	{
    		LOGGER.setLogToStdErr (true);
    		LOGGER.setLevel (LOGGER.INFO | LOGGER.WARN | LOGGER.ERROR);
    		continue;
    	}
    	if (args[i].equals ("--debugg"))
    	{
    		LOGGER.setLogToStdErr (true);
    		LOGGER.setLevel (LOGGER.DEBUG | LOGGER.INFO | LOGGER.WARN | LOGGER.ERROR);
    		continue;
    	}
    	if (args[i].equals ("--xml"))
    	{
    		output = 1;
    		continue;
    	}
    	if (args[i].equals ("--json"))
    	{
    		output = 2;
    		continue;
    	}
    	/*if (args[i].equals ("--meta"))
    	{
    		want = -1;
    		continue;
    	}
    	if (args[i].equals ("--documentType"))
    	{
    		want = -2;
    		continue;
    	}*/
    	if (args[i].equals ("--help"))
    	{
    		usage ("");
    	}
    	if (file1 == null)
    		file1 = new File (args[i]);
    	else if (file2 == null)
    		file2 = new File (args[i]);
    	else
    		usage ("do not understand " + args[i] + " (found files " + file1 + " and " + file2 + ")");
    		
    }
    

    if (file1 == null)
    	usage ("no file provided");
    if (!file1.exists ())
    	usage ("cannot find " + file1.getAbsolutePath ());
    if (!file1.canRead ())
    	usage ("cannot read " + file1.getAbsolutePath ());
    
    
    if (file2 == null)
    {
    	// single mode
    	if ((WANT_META & want) > 0)
    	{
    		// meta
    		classifier = new DocumentClassifier ();
    		int type = classifier.classify (file1);

  			String ret = "";
  			
    		if ((type & DocumentClassifier.SBML) > 0)
    		{
    			SBMLDocument doc = classifier.getSbmlDocument ();
    			ret += "sbmlVersion:" + doc.getVersion () + ";sbmlLevel:" + doc.getLevel () + ";modelId:" + doc.getModel ().getID () + ";modelName:" + doc.getModel ().getName () + ";";
    		}
    		if ((type & DocumentClassifier.CELLML) > 0)
    		{
    			CellMLDocument doc = classifier.getCellMlDocument ();
    			ret += "containsImports:" + doc.containsImports () + ";modelName:" + doc.getModel ().getName () + ";";
    		}
				if ((type & DocumentClassifier.XML) > 0)
				{
					TreeDocument doc = classifier.getXmlDocument ();
					ret += "nodestats:" + doc.getNodeStats () + ";";
				}
				toReturn.put (REQ_WANT_META, ret);
    	}
    	if ((WANT_DOCUMENTTYPE & want) > 0)
    	{
    		// doc type
    		classifier = new DocumentClassifier ();
    		int type = classifier.classify (file1);
				
				toReturn.put (REQ_WANT_DOCUMENTTYPE, DocumentClassifier.humanReadable (type));
    	}
    	
    	if ((WANT_SINGLE_COMP_HIERARCHY_DOT|WANT_SINGLE_COMP_HIERARCHY_JSON|WANT_SINGLE_COMP_HIERARCHY_GRAPHML|WANT_SINGLE_CRN_JSON|WANT_SINGLE_CRN_GRAPHML|WANT_SINGLE_CRN_DOT & want) > 0)
    	{
    		Single single = null;
	    	classifier = new DocumentClassifier ();
	    	int type = classifier.classify (file1);
	    	
	    	if ((type & DocumentClassifier.SBML) != 0)
	    	{
	    		single = new SBMLSingle (file1);
	    	}
	    	else if ((type & DocumentClassifier.CELLML) != 0)
	    	{
	    		single = new CellMLSingle (file1);
	    	}
	    	if (single == null)
	    		usage ("cannot produce the requested output for the provided file.");
    		if ((want & WANT_SINGLE_CRN_JSON) > 0)
    			toReturn.put (REQ_WANT_SINGLE_CRN_JSON, result (single.getCRNJsonGraph ()));
    		if ((want & WANT_SINGLE_CRN_GRAPHML) > 0)
    			toReturn.put (REQ_WANT_SINGLE_CRN_GRAPHML, result (single.getCRNGraphML ()));
    		if ((want & WANT_SINGLE_CRN_DOT) > 0)
    			toReturn.put (REQ_WANT_SINGLE_CRN_DOT, result (single.getCRNDotGraph ()));
    		if ((want & WANT_SINGLE_COMP_HIERARCHY_JSON) > 0)
    			toReturn.put (REQ_WANT_SINGLE_COMP_HIERARCHY_JSON, result (single.getHierarchyJsonGraph ()));
    		if ((want & WANT_SINGLE_COMP_HIERARCHY_GRAPHML) > 0)
    			toReturn.put (REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML, result (single.getHierarchyGraphML ()));
    		if ((want & WANT_SINGLE_COMP_HIERARCHY_DOT) > 0)
    			toReturn.put (REQ_WANT_SINGLE_COMP_HIERARCHY_DOT, result (single.getHierarchyDotGraph ()));
    	}
    		
    }
    else
    {
    	// compare mode
	    if (!file2.exists ())
	    	usage ("cannot find " + file2.getAbsolutePath ());
	    if (!file2.canRead ())
	    	usage ("cannot read " + file2.getAbsolutePath ());
	    
	    if (want == 0)
	    	want = WANT_DIFF;
	    
	    // decide which kind of mapper to use
	    if ((WANT_CELLML & want) > 0)
	    	diff = new CellMLDiff (file1, file2);
	    else if ((WANT_SBML & want) > 0)
	    	diff = new SBMLDiff (file1, file2);
	    else if ((WANT_REGULAR & want) > 0)
	    	diff = new RegularDiff (file1, file2);
	    else
	    {
	    	classifier = new DocumentClassifier ();
	    	int type1 = classifier.classify (file1);
	    	int type2 = classifier.classify (file2);
	    	int type = type1 & type2;
	    	if ((type & DocumentClassifier.SBML) != 0)
	    	{
	    		diff = new SBMLDiff (file1, file2);
	    	}
	    	else if ((type & DocumentClassifier.CELLML) != 0)
	    	{
	    		diff = new CellMLDiff (file1, file2);
	    	}
	    	else if ((type & DocumentClassifier.XML) != 0)
	    	{
	    		diff = new RegularDiff (file1, file2);
	    	}
	    	else
	    		usage ("cannot compare these files (["+DocumentClassifier.humanReadable (type1) + "] ["+DocumentClassifier.humanReadable (type2)+"])");
	    }
	    
	    if (diff == null)
	  		usage ("cannot compare these files");
	
	  	//System.out.println (want);
	    
	    // create mapping
	    diff.mapTrees ();
	    
	    
	    // compute results
			if ((want & WANT_DIFF) > 0)
				toReturn.put (REQ_WANT_DIFF, result (diff.getDiff ()));
			
			if ((want & WANT_CRN_GRAPHML) > 0)
				toReturn.put (REQ_WANT_CRN_GRAPHML, result (diff.getCRNGraphML ()));
			
			if ((want & WANT_CRN_DOT) > 0)
				toReturn.put (REQ_WANT_CRN_DOT, result (diff.getCRNDotGraph ()));
			
			if ((want & WANT_CRN_JSON) > 0)
				toReturn.put (REQ_WANT_CRN_JSON, result (diff.getCRNJsonGraph ()));
			
			if ((want & WANT_COMP_HIERARCHY_DOT) > 0)
				toReturn.put (REQ_WANT_COMP_HIERARCHY_DOT, result (diff.getHierarchyDotGraph ()));
			
			if ((want & WANT_COMP_HIERARCHY_JSON) > 0)
				toReturn.put (REQ_WANT_COMP_HIERARCHY_JSON, result (diff.getHierarchyJsonGraph ()));
			
			if ((want & WANT_COMP_HIERARCHY_GRAPHML) > 0)
				toReturn.put (REQ_WANT_COMP_HIERARCHY_GRAPHML, result (diff.getHierarchyGraphML ()));
			
			if ((want & WANT_REPORT_HTML) > 0)
				toReturn.put (REQ_WANT_REPORT_HTML, result (diff.getHTMLReport ()));
			
			if ((want & WANT_REPORT_MD) > 0)
				toReturn.put (REQ_WANT_REPORT_MD, result (diff.getMarkDownReport ()));
			
			if ((want & WANT_REPORT_RST) > 0)
				toReturn.put (REQ_WANT_REPORT_RST, result (diff.getReStructuredTextReport ()));
    }

		if (toReturn.size () < 1)
		{
			usage ("invalid call. no output produced.");
		}
    
    if (output == 0)
    {
    	for (String ret : toReturn.keySet ())
    		System.out.println (toReturn.get (ret));
    }
    else if (output == 1)
    {
    	//xml
    	DocumentBuilderFactory factory =
      DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
    	Document document = builder.newDocument();
    	Element root =  (Element) document.createElement("bivesResult"); 
    	document.appendChild(root);

    	for (String ret : toReturn.keySet ())
    	{
    		Element el = (Element) document.createElement(ret);
    		el.appendChild (document.createTextNode(toReturn.get (ret)));
    		root.appendChild(el);
    	}
    	
    	System.out.println (XmlTools.prettyPrintDocument (document));
    }
    else
    {
    	// json
    	JSONObject json = new JSONObject ();
    	for (String ret : toReturn.keySet ())
    		json.put (ret, toReturn.get (ret));
    	System.out.println (json);
    }
	}
	
	public static String result (String s)
	{
		if (s == null)
			return "";
		return s;
	}
	
}
