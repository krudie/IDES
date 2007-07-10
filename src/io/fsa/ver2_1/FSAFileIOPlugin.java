/**
 * 
 */
package io.fsa.ver2_1;

import io.IOUtilities;
import io.ParsingToolbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

import main.Annotable;
import main.Hub;
import model.DESModel;
import model.fsa.FSAEvent;
import model.fsa.FSAModel;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import model.fsa.ver2_1.Event;
import model.fsa.ver2_1.State;
import model.fsa.ver2_1.Transition;
import pluggable.io.FileIOPlugin;
import pluggable.io.IOPluginManager;

import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author christiansilvano
 *
 */
public class FSAFileIOPlugin implements FileIOPlugin{    
	public Set<String> getMetaTags(String type)
	{
		if(type.equals("FSA"))
		{
			Set<String> returnSet = new HashSet<String>();
			returnSet.add("layout");
			return returnSet;	
		}
		return null;
	}
	
	public String getIOTypeDescriptor()
	{
		return "FSA";
	}
	
	//Singleton instance:
	private static FSAFileIOPlugin instance = null;
	private FSAFileIOPlugin()
	{
		this.initializeFileIO();
	}
	
	
	public static FSAFileIOPlugin getInstance()
	{
		if (instance == null)
		{
			instance = new FSAFileIOPlugin();
		}
		return instance;
	}
	
	/**
	 * Subscribes itself to the IOIE_PluginManager informing whether this object
	 * is a "metaSaver", "dataSaver", "metaLoader" or "dataLoader".
	 */
	public void initializeFileIO()
	{
		IOPluginManager.getInstance().registerDataLoader(this ,"FSA");
		IOPluginManager.getInstance().registerDataSaver(this, "FSA");
		IOPluginManager.getInstance().registerMetaSaver(this, "FSA", "layout");
		IOPluginManager.getInstance().registerMetaLoader(this, "FSA", "layout");
	}
	
	
	/**
	 * Saves its data in <code>file</code> according to a <code>model</code>.
	 * @param file the file to save the data in.
	 * @param model the model to be saved in the file.
	 * @param fileDirectory path to the file, so auxiliar files can be created.
	 */
	public boolean saveData(PrintStream stream, DESModel model, File fileDirectory)
	{
	       ListIterator<FSAState> si = ((FSAModel)model).getStateIterator();
	        while(si.hasNext()){
	            XMLExporter.stateToXML((State)si.next(), stream, XMLExporter.INDENT);            
	        }

	        ListIterator<FSAEvent> ei = ((FSAModel)model).getEventIterator();
	        while(ei.hasNext()){
	            XMLExporter.eventToXML((Event)ei.next(),stream, XMLExporter.INDENT);
	        }

	        ListIterator<FSATransition> ti = ((FSAModel)model).getTransitionIterator();
	        while(ti.hasNext()){
	            XMLExporter.transitionToXML((Transition)ti.next(),stream, XMLExporter.INDENT);
	        }
	       
	        //TODO make IOCoordinator call a "fireModelSaved", it should be a generic call for any
	        //kind of DES Model.
	       ((FSAModel)model).fireFSASaved();
	       
	       return true;
	}
	
	/**
	 * Loads data from the file.
	 * @param file
	 * @param fileDir
	 * @return
	 */
	public DESModel loadData(File f, File fileDir)
	{
		//this "plugin" does not use "fileDir"
	     FSAModel a = null;
	        if(!f.canRead())
	        {
	        	Hub.displayAlert(Hub.string("fileCantRead")+f.getPath());
	        	return (DESModel)a;
	        }
	        String errors="";
	        try
	        {
	        	BufferedReader head=new BufferedReader(new FileReader(f));
	        	head.readLine();
	        	String line=head.readLine();
	        	head.close();
	        	if(line.trim().startsWith("<automaton"))
	        	{
	            	AutomatonParser20 ap = new AutomatonParser20();
	                a = ap.parse(f);
	                errors=ap.getParsingErrors();
	        	}
	        	else
	        	{
	            	AutomatonParser ap = new AutomatonParser();
	                //
	            	a = ap.parse(f);
	                errors=ap.getParsingErrors();
	        	}
	        }catch(Exception e)
	        {
	        	a=null;
	        	errors+=e.getMessage();
	        }
	        if(!"".equals(errors))
	        {
	        	Hub.displayAlert(Hub.string("errorsParsingXMLFileL1")+f.getPath()+
	        			"\n"+Hub.string("errorsParsingXMLFileL2"));
	        }
	        return (DESModel)a;
	}
	
	/**
	 * Loads metadata from the file
	 * @param file
	 */
	public void loadMeta(File file)
	{
		
	}
	
	/**
	 * Save metaData to the file, according to model.
	 * @param file
	 * @param model
	 */
	public boolean saveMeta(PrintStream stream, DESModel model, String type, String tag)
	{
		//stream will be an OutputStream.
		//it will need to be converted to PrintStream(UTF-8).
		if(type.equals("FSA") & tag.equals("layout"))
		{
			ListIterator<FSAState> si = ((FSAModel)model).getStateIterator();
			ListIterator<FSATransition> ti = ((FSAModel)model).getTransitionIterator();
			stream.println("\t<font size=\""+(((FSAModel)model).getMeta()==null?12:((FSAModel)model).getMeta().getAttribute("size"))+"\"/>");
			si = ((FSAModel)model).getStateIterator();
			
			while(si.hasNext())
			{
				XMLExporter.stateLayoutToXML((State)si.next(), stream, XMLExporter.INDENT);            
			}

			ti = ((FSAModel)model).getTransitionIterator();
			while(ti.hasNext())
			{
				XMLExporter.transitionLayoutToXML((Transition)ti.next(),stream, XMLExporter.INDENT);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Unsubscribe itself from the IOIE_PluginManager
	 *
	 */
	public void unload()
	{
		
	}
	
	
private static class XMLExporter{
    private static final String INDENT =  "\t";

	   /**
     * prints a state in xml
     * @param s the state to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */ 
   private static void stateToXML(State s, PrintStream ps,String indent){
        if(s.isEmpty()) ps.println(indent + "<state" + " id=\"" + s.getId() + "\" />");
        else{
            ps.println(indent + "<state" + " id=\"" + s.getId() + "\">");
            subElementContainerToXML(s, ps, indent + INDENT);            
            ps.println(indent + "</state>");
        }
    }
    /**
     * prints an event in xml
     * @param e the event to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */ 
    private static void eventToXML(Event e, PrintStream ps, String indent){
        if(e.isEmpty()){
            ps.println(indent + "<event" + " id=\"" + e.getId() + "\" />");
        }
        else{
            ps.println(indent + "<event" + " id=\"" + e.getId() + "\">");
            subElementContainerToXML(e, ps, indent + INDENT);
            ps.println(indent + "</event>");
        }
    }
    /**
     * prints a transition in xml
     * @param t the transition to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */ 
    private static void transitionToXML(Transition t, PrintStream ps, String indent){
        if(t.isEmpty()){
            ps.println(indent + "<transition" + " id=\"" + t.getId() + "\"" + " source=\""
                    + t.getSource().getId() + "\"" + " target=\"" + t.getTarget().getId() + "\""
                    + ((t.getEvent() != null) ? " event=\"" + t.getEvent().getId() + "\"" : "") + " />");
        }
        else{
            ps.println(indent + "<transition" + " id=\"" + t.getId() + "\"" + " source=\""
                    + t.getSource().getId() + "\"" + " target=\"" + t.getTarget().getId() + "\""
                    
                    + ((t.getEvent() != null) ? " event=\"" + t.getEvent().getId() + "\"" : "") + ">");
            subElementContainerToXML(t, ps, indent + INDENT);
            ps.println(indent + "</transition>");
        }
    }

    /**
     * Prints this the subelementcontainer and all subelements of this objects to the
     * printsstream as XML.
     * @param sec the subelementcontainer ro export to xml
     * @param ps the printstream this object should be printet to.
     * @param indent the indentation this object should have.
     */
    private static void subElementContainerToXML(SubElementContainer sec, PrintStream ps, String indent){
        Enumeration<SubElement> see = sec.getSubElements();        
        if(see == null) return;
        while(see.hasMoreElements())
        {
        	SubElement se=see.nextElement();
        	if(!"graphic".equals(se.getName()))
        		subElementToXML(se,ps, indent);
        }
    }
    
    /**
     * prints a subelement in xml
     * @param se the subelement to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */    
    private static void subElementToXML(SubElement se, PrintStream ps, String indent){
        ps.print(indent + "<" + se.getName());
            
        Enumeration<String> av = se.getAttributeValues();
        Enumeration<String> an = se.getAttributeNames();
        
        while(an.hasMoreElements()){
            ps.print(" " + an.nextElement() + "=\"" + av.nextElement() + "\"");
        }

        if(se.isEmpty() && (se.getChars() == null || se.getChars().trim().equals(""))){
            ps.println(" />");
            return;
        }
        ps.print(">");
        
        if(!se.isEmpty()){
            ps.println();
            subElementContainerToXML(se, ps, indent + INDENT);
            if(se.getChars() != null && !se.getChars().trim().equals(""))
                ps.println(indent + INDENT +IOUtilities.encodeForXML(se.getChars()));
            ps.println(indent + "</" + se.getName() + ">");
            return;
        }
        if(se.getChars() != null && !se.getChars().trim().equals(""))
            ps.print(IOUtilities.encodeForXML(se.getChars()));
        ps.println("</" + se.getName() + ">");    
    }
    /**
     * prints a state in xml
     * @param s the state to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */ 
    private static void stateLayoutToXML(State s, PrintStream ps,String indent){
    	SubElement ge=s.getSubElement("graphic");
    	if(ge==null)
    		return;
        ps.println(indent + "<state" + " id=\"" + s.getId() + "\">");
        layoutContainerToXML(ge, ps, indent + INDENT);            
        ps.println(indent + "</state>");
    }
    
    /**
     * Prints this the subelementcontainer and all subelements of this objects to the
     * printsstream as XML.
     * @param sec the subelementcontainer ro export to xml
     * @param ps the printstream this object should be printet to.
     * @param indent the indentation this object should have.
     */
    private static void layoutContainerToXML(SubElementContainer sec, PrintStream ps, String indent){
        Enumeration<SubElement> see = sec.getSubElements();        
        if(see == null) return;
        while(see.hasMoreElements())
        {
        	SubElement se=see.nextElement();
       		subElementToXML(se,ps, indent);
        }
    }    
    
    /**
     * prints a transition in xml
     * @param t the transition to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */ 
   private static void transitionLayoutToXML(Transition t, PrintStream ps, String indent){
    	SubElement ge=t.getSubElement("graphic");
    	if(ge==null)
    		return;
        ps.println(indent + "<transition" + " id=\"" + t.getId() + "\">");
        layoutContainerToXML(ge, ps, indent + INDENT);
        ps.println(indent + "</transition>");
    }
    
}
}
