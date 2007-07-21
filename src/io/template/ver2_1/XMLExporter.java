package io.template.ver2_1;

import io.IOUtilities;
//import io.fsa.ver2_1.XMLexporter;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;

import main.Hub;
import model.fsa.FSAEvent;
import model.fsa.FSAModel;
import model.template.TemplateModule;

import presentation.template.GraphBlock;
import presentation.template.GraphLink;
import presentation.template.Template;
import presentation.template.TemplateGraph;

/**
 * @deprecated Do not use htis class, it is being eliminated from IDES, the xmlExporters must be implemented
 * by ioplugins.
 * @author christiansilvano
 *
 */
public class XMLExporter {

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
//				XMLexporter.automatonToXML(fsa, ps);
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
