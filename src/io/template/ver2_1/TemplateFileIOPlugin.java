/**
 * 
 */
package io.template.ver2_1;

import model.fsa.FSAEvent;
import model.fsa.FSAModel;
import model.template.TemplateModel;
import model.template.TemplateModule;

import java.io.PrintStream;

import io.IOUtilities;
import io.ParsingToolbox;
//import io.fsa.ver2_1.XMLexporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;

import main.Annotable;
import main.Hub;
import model.DESModel;
import pluggable.io.FileIOPlugin;
import pluggable.io.IOPluginManager;
import presentation.template.GraphBlock;
import presentation.template.GraphLink;
import presentation.template.TemplateGraph;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author christiansilvano
 *
 */
public class TemplateFileIOPlugin implements FileIOPlugin{
	
	public Set<String> getMetaTags(String type)
	{
		if(type.equals("TemplateDesign"))
		{
			Set<String> returnSet = new HashSet<String>();
			returnSet.add("layout");
			return returnSet;	
		}
		return null;
	}
	
	public String getIOTypeDescriptor()
	{
		return "TemplateDesign";
	}
	
	public static final String LAST_PATH_SETTING_NAME="lastUsedPath";
	//Singleton instance:
	private static TemplateFileIOPlugin instance = null;
	private TemplateFileIOPlugin()
	{
		this.initializeFileIO();
	}
	
	
	public static TemplateFileIOPlugin getInstance()
	{
		if (instance == null)
		{
			instance = new TemplateFileIOPlugin();
		}
		return instance;
	}
	
	/**
	 * Subscribes itself to the IOIE_PluginManager informing whether this object
	 * is a "metaSaver", "dataSaver", "metaLoader" or "dataLoader".
	 */
	public void initializeFileIO()
	{
		//The FSA model is capable of saving metaData
		IOPluginManager.getInstance().registerDataLoader(this ,"TemplateDesign");
		IOPluginManager.getInstance().registerDataSaver(this, "TemplateDesign");
		IOPluginManager.getInstance().registerMetaSaver(this, "TemplateDesign", "layout");
		IOPluginManager.getInstance().registerMetaLoader(this, "TemplateDesign", "layout");
	}
	
	
	/**
	 * Saves its data in <code>file</code> according to a <code>model</code>.
	 * @param file the file to save the data in.
	 * @param model the model to be saved in the file.
	 * @param fileDirectory path to the file, so auxiliar files can be created.
	 */
	public boolean saveData(PrintStream stream, DESModel model, File fileDirectory)
	{
		//TODO make the code to save the templateDesign model.
		//The following commented code is the code written by Lenko which saves the
		//model having a given a TemplateGraph...
		//Make it save the code given a DESModel
//		TemplateGraph graph = null;
//		for(GraphBlock b:graph.getBlocks())
//        {
//        	if(b.getBlock() instanceof TemplateModule)
//        	{
//            	stream.println("\t<module fsa=\""+b.getBlock().getFSA().getName()+
//            			"\" id=\""+b.getBlock().getId()+"\">");
//            	for(FSAEvent event:b.getBlock().getInterfaceEvents())
//            	{
//            		stream.println("\t\t<event id=\""+event.getId()+"\"/>");
//            	}
//            	stream.println("\t</module>");
//        	}
//        	else //Template Channel
//        	{
//            	stream.println("\t<channel fsa=\""+b.getBlock().getFSA().getName()+
//            			"\" id=\""+b.getBlock().getId()+"\">");
//            	for(FSAEvent event:b.getBlock().getInterfaceEvents())
//            	{
//            		stream.println("\t\t<event id=\""+event.getId()+"\"/>");
//            	}
//            	stream.println("\t</channel>");        		
//        	}
//        }
//        for(GraphLink l:graph.getLinks())
//        {
//        	stream.println("\t<link lefttype=\""+(l.getLink().getBlockLeft() instanceof TemplateModule?"m":"c")+
//        			"\" leftblock=\""+l.getLink().getBlockLeft().getId()+
//        			"\" leftevent=\""+l.getLink().getEventLeft().getId()+
//        			"\" righttype=\""+(l.getLink().getBlockRight() instanceof TemplateModule?"m":"c")+
//        			"\" rightblock=\""+l.getLink().getBlockRight().getId()+
//        			"\" rightevent=\""+l.getLink().getEventRight().getId()+
//        			"\" id=\""+l.getLink().getId()+"\"/>");        	
//        }
//        Map<String,String> codeMap=graph.getModel().getPLCCodeMap();
//        for(String event:codeMap.keySet())
//        {
//        	stream.println("\t<routine event=\""+event+"\">");
//        	stream.println(codeMap.get(event).replaceAll("<event>","{event}"));
//        	stream.println("\t</routine>");
//        }
//		
		
		return false;
	}
	
	/**
	 * Loads data from the file.
	 * @param file
	 * @param fileDir
	 * @return
	 */
	public DESModel loadData(InputStream f, File fileDir)
	{	  
		//load file
		//go fsatoolset.wrap()
		//templatetoolset.wrap()
//		
//	      TemplateGraph graph = null;
//	      TemplateModel model = null;
//	        if(!f.canRead())
//	        {
//	        	Hub.displayAlert(Hub.string("fileCantRead")+f.getPath());
//	        	return (DESModel)model;
//	        }
//	        String errors="";
//	        try
//	        {	
//	        	model = TemplateParser.parse(f);
//	        }catch(Exception e)
//	        {
//	        	graph=null;
//	        	errors+=e.getMessage();
////	        	e.printStackTrace();
//	        }
//	        
//	        if(!"".equals(errors))
//	        {
//	        	Hub.displayAlert(Hub.string("errorsParsingXMLFileL1")+f.getPath()+
//	        			"\n"+Hub.string("errorsParsingXMLFileL2"));
//	        }
//	        if(graph!=null)
//	        {
//	        	graph.setAnnotation(Annotable.FILE,f);
//	        }
//	        Hub.persistentData.setProperty(LAST_PATH_SETTING_NAME,f.getParent());
//
//	        return (DESModel)model;
		return null;
	}
	
	/**
	 * Loads metadata from the file
	 * @param file
	 */
	public void loadMeta(InputStream stream, DESModel model)
	{
		
	}
	
	/**
	 * Save metaData to the file, according to model.
	 * @param file
	 * @param model
	 */
	public boolean saveMeta(PrintStream stream, DESModel model, String type, String tag)
	{
		return false;
	}
	
	/**
	 * Unsubscribe itself from the IOIE_PluginManager
	 *
	 */
	public void unload()
	{
		
	}
	
	public static class XMLExporter {

		public static void graph2XML(TemplateGraph graph, File dir)
		{
			if(!dir.exists())
			{
				dir.mkdir();
			}
			else
			{
				for(File f:dir.listFiles())
				{
					f.delete();
				}
			}
	        for(GraphBlock b:graph.getBlocks())
	        {
		        PrintStream ps = null;
				try
				{
					FSAModel fsa=b.getBlock().getFSA();
					if(fsa==null)
					{
						throw new RuntimeException();
					}
			        ps = IOUtilities.getPrintStream(
			        		new File(dir.getCanonicalPath()+File.separator+fsa.getName()+"."+IOUtilities.MODEL_FILE_EXT));
			        if(ps==null)
			        {
			        	throw new RuntimeException(Hub.string("checkInvalidChars"));
			        }
			        //TODO: MAKE THE XML, EXPORTER BE AN INNER CLASS, LIKE IT IS DONE IN THE FSAFileIOPlugin
//					XMLexporter.automatonToXML(fsa, ps);
					ps.close();
		        } catch(Exception e)
		        {
		        	try
		        	{
		        		ps.close();
		        	}catch(Exception ex){}
		        	Hub.displayAlert(Hub.string("cantSaveTemplate")+" "+e.getMessage());
		        }
	        }
	        PrintStream ps = null;
			try
			{
		        ps = IOUtilities.getPrintStream(
		        		new File(dir.getCanonicalPath()+File.separator+"TemplateDesign."+IOUtilities.MODEL_FILE_EXT));
		        if(ps==null)
		        {
		        	throw new RuntimeException(Hub.string("checkInvalidChars"));
		        }
				export(graph, ps);
				ps.close();
	        } catch(Exception e)
	        {
	        	try
	        	{
	        		ps.close();
	        	}catch(Exception ex){}
	        	Hub.displayAlert(Hub.string("cantSaveTemplate")+" "+e.getMessage());
	        }

		}
		
		public static void export(TemplateGraph graph,PrintStream ps)
		{
	        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	        ps.println("<model version=\"2.1\" type=\"TemplateDesign\">");
	        ps.println("<data>");
	        for(GraphBlock b:graph.getBlocks())
	        {
	        	if(b.getBlock() instanceof TemplateModule)
	        	{
	            	ps.println("\t<module fsa=\""+b.getBlock().getFSA().getName()+
	            			"\" id=\""+b.getBlock().getId()+"\">");
	            	for(FSAEvent event:b.getBlock().getInterfaceEvents())
	            	{
	            		ps.println("\t\t<event id=\""+event.getId()+"\"/>");
	            	}
	            	ps.println("\t</module>");
	        	}
	        	else //Template Channel
	        	{
	            	ps.println("\t<channel fsa=\""+b.getBlock().getFSA().getName()+
	            			"\" id=\""+b.getBlock().getId()+"\">");
	            	for(FSAEvent event:b.getBlock().getInterfaceEvents())
	            	{
	            		ps.println("\t\t<event id=\""+event.getId()+"\"/>");
	            	}
	            	ps.println("\t</channel>");        		
	        	}
	        }
	        for(GraphLink l:graph.getLinks())
	        {
	        	ps.println("\t<link lefttype=\""+(l.getLink().getBlockLeft() instanceof TemplateModule?"m":"c")+
	        			"\" leftblock=\""+l.getLink().getBlockLeft().getId()+
	        			"\" leftevent=\""+l.getLink().getEventLeft().getId()+
	        			"\" righttype=\""+(l.getLink().getBlockRight() instanceof TemplateModule?"m":"c")+
	        			"\" rightblock=\""+l.getLink().getBlockRight().getId()+
	        			"\" rightevent=\""+l.getLink().getEventRight().getId()+
	        			"\" id=\""+l.getLink().getId()+"\"/>");        	
	        }
	        Map<String,String> codeMap=graph.getModel().getPLCCodeMap();
	        for(String event:codeMap.keySet())
	        {
	        	ps.println("\t<routine event=\""+event+"\">");
	        	ps.println(codeMap.get(event).replaceAll("<event>","{event}"));
	        	ps.println("\t</routine>");
	        }
	        ps.println("</data>");
	        ps.println("<meta tag=\"layout\" version=\"2.1\">");
	        for(GraphBlock b:graph.getBlocks())
	        {
	        	if(b.getBlock() instanceof TemplateModule)
	        	{
	            	ps.println("\t<module x=\""+b.getLocation().x+
	            			"\" y=\""+b.getLocation().y+
	            			"\" name=\""+b.getName()+
	            			"\" id=\""+b.getBlock().getId()+"\"/>");
	        	}
	        	else //Template Channel
	        	{
	            	ps.println("\t<channel x=\""+b.getLocation().x+
	            			"\" y=\""+b.getLocation().y+
	            			"\" name=\""+b.getName()+
	            			"\" id=\""+b.getBlock().getId()+"\"/>");
	        	}
	        }
	        ps.println("</meta>");
	        ps.println("</model>");
		}
	}

}
