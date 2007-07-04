/**
 * 
 */
package ie.fsa.ver2_1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import main.Hub;
import model.ModelManager;
import model.fsa.FSAModel;

import org.pietschy.command.file.ExtensionFileFilter;

import pluggable.io.IOPluginManager;
import pluggable.io.IOCoordinator;
import pluggable.io.ImportExportPlugin;
import io.IOUtilities;

/**
 * @author christiansilvano
 *
 */
public class GrailPlugin implements ImportExportPlugin{
	
	
	private String description = IOUtilities.GRAIL_DESCRIPTOR;
	private String ext = IOUtilities.FM_FILE_EXT;
	
	public String getExportExtension(){
		return ext;
	}
	//Singleton instance:
	private static GrailPlugin instance = null;
	private GrailPlugin()
	{
		this.initializeImportExport();
	}
	
	
	public static GrailPlugin getInstance()
	{
		if (instance == null)
		{
			instance = new GrailPlugin();
		}
		return instance;
	}
	
	
	/**
	 * Registers itself to the IOPluginManager
	 *
	 */
	public void initializeImportExport()
	{
		IOPluginManager.getInstance().registerExport(this, IOUtilities.GRAIL_DESCRIPTOR, IOUtilities.FSA_DESCRIPTOR);
		IOPluginManager.getInstance().registerImport(this, IOUtilities.GRAIL_DESCRIPTOR, IOUtilities.FSA_DESCRIPTOR);
	}
	
	/**
	 * Unregisters itself from the IOPluginManager
	 *
	 */
	public void unload()
	{
	}
	
	
	/**
	 * Exports a file to a different format
	 * @param src - the source file
	 * @param dst - the destination
	 */
	public void exportFile(File src, File dst)
	{    	
    	//Loading the model from the file:
    	FSAModel a=(FSAModel)IOCoordinator.getInstance().load(src);

    	//Container for the grail model:
    	String fileContents = "";
    	
    	//Translating the model to the grail format:
    	for(Iterator<model.fsa.FSAState> i=a.getStateIterator();i.hasNext();)
    	{
    		model.fsa.FSAState s=i.next();
    		if(s.isInitial())
    		{
    			fileContents+="(START) |- "+s.getId()+"\n";
    		}
    		if(s.isMarked())
    		{
    			fileContents+=""+s.getId()+" -| (FINAL)\n";
    		}
    		for(Iterator<model.fsa.FSATransition> j=s.getSourceTransitionsListIterator();j.hasNext();)
    		{
    			model.fsa.FSATransition t=j.next();
    			fileContents+=""+s.getId()+" "+(t.getEvent()==null?"NULL":t.getEvent().getSymbol())+" "+t.getTarget().getId()+"\n";
    		}
    	}
    	
		FileWriter latexWriter = null;
				
		if (fileContents == null)
		{
			return;
		}
		
		try
		{
			latexWriter = new FileWriter(dst);
			latexWriter.write(fileContents);
			latexWriter.close();
		}
		catch (IOException fileException)
		{
			Hub.displayAlert(Hub.string("problemLatexExport")+dst.getPath());
		}

	}
	
	
	/**
	 * Import a file from a different format to the IDES file system
	 * @param importFile - the source file
	 * @return
	 */
	public void importFile(File src, File dst)
	{
    	java.io.BufferedReader in=null;
    	try
    	{
    		in=new java.io.BufferedReader(new java.io.FileReader(src));
    		FSAModel a=ModelManager.createModel(FSAModel.class,src.getName());
    		long tCount=0;
    		long eCount=0;
    		java.util.Hashtable<String,Long> events=new java.util.Hashtable<String, Long>();
    		String line;
    		while((line=in.readLine())!=null)
    		{
    			String[] parts=line.split(" ");
    			if(parts[0].startsWith("("))
    			{
    				long sId=Long.parseLong(parts[2]);
    				model.fsa.ver2_1.State s=(model.fsa.ver2_1.State)a.getState(sId);
    				if(s==null)
    				{
    					s=new model.fsa.ver2_1.State(sId);
    					a.add(s);
    				}
    				s.setInitial(true);
    			}
    			else if(parts[2].startsWith("("))
    			{	    				
    				long sId=Long.parseLong(parts[0]);
    				model.fsa.ver2_1.State s=(model.fsa.ver2_1.State)a.getState(sId);
    				if(s==null)
    				{
    					s=new model.fsa.ver2_1.State(sId);
    					a.add(s);
    				}
    				s.setMarked(true);
    			}
    			else
    			{
    				long sId1=Long.parseLong(parts[0]);
    				model.fsa.ver2_1.State s1=(model.fsa.ver2_1.State)a.getState(sId1);
    				if(s1==null)
    				{
    					s1=new model.fsa.ver2_1.State(sId1);
    					a.add(s1);
    				}
    				long sId2=Long.parseLong(parts[2]);
    				model.fsa.ver2_1.State s2=(model.fsa.ver2_1.State)a.getState(sId2);
    				if(s2==null)
    				{
    					s2=new model.fsa.ver2_1.State(sId2);
    					a.add(s2);
    				}
    				model.fsa.ver2_1.Event e=null;
    				Long eId=events.get(parts[1]);
    				if(eId==null)
    				{
    					e=new model.fsa.ver2_1.Event(eCount);
    					e.setSymbol(parts[1]);
    					e.setObservable(true);
    					e.setControllable(true);
    					eCount++;
    					a.add(e);
    					events.put(parts[1], new Long(e.getId()));
    				}
    				else
    					e=(model.fsa.ver2_1.Event)a.getEvent(eId.longValue());
    				model.fsa.ver2_1.Transition t=new model.fsa.ver2_1.Transition(tCount,s1,s2,e);
    				a.add(t);
    				tCount++;
    			}
    		}
    		//Create an automatic layout given the imported method
    		presentation.fsa.FSAGraph g=new presentation.fsa.FSAGraph(a);
			//Save the model to the selected destination
    		IOCoordinator.getInstance().save(a, dst);
    		//Add the new layout to the workspace
    		Hub.getWorkspace().addModel(a);
			Hub.getWorkspace().setActiveModel(a.getName());
			//Hub.getWorkspace().fireRepaintRequired();

    	}catch(java.io.IOException e)
    	{
    		Hub.displayAlert(Hub.string("cantParseImport")+src);
    	}
    	catch(RuntimeException e)
    	{
    		Hub.displayAlert(Hub.string("cantParseImport")+src);
    	}
    	finally
    	{
    		try
    		{
    			if(in!=null)
    				in.close();
    		}catch(java.io.IOException e){}
    	}
	}
	
	/**
	 * Return a human readable description of the plugin
	 */
	public String getDescription()
	{
		return description;
	}
	
}
