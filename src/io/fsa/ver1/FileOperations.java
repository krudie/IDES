package io.fsa.ver1;

import io.IOUtilities;
import io.ParsingToolbox;
import io.WorkspaceParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.CommandManager;
import org.pietschy.command.LoadException;
import org.pietschy.command.file.ExtensionFileFilter;

import main.Hub;
import main.IDESWorkspace;
import main.SystemVariables;
import main.WorkspaceDescriptor;
import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;

/**
 * TODO 
 * extract interface to package io.fsa
 * implement most methods
 * 
 * @author helen bretzke
 *
 */
public class FileOperations {
	
	public static final String LAST_PATH_SETTING_NAME="lastUsedPath";
	
	public static FSAModel openAutomaton(File f) {
        Automaton a = null;
        if(!f.canRead())
        {
        	Hub.displayAlert(Hub.string("fileCantRead")+f.getPath());
        	return a;
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
        if(a!=null)
        {
        	a.setName(ParsingToolbox.removeFileType(f.getName()));
        	a.setFile(f);
        }
        Hub.persistentData.setProperty(LAST_PATH_SETTING_NAME,f.getParent());
        return a;
	}
		
	/**
     * Saves an automaton to a file
     * @param a the automaton to save
     * @param path the path to save it to
     */      
    public static void saveAutomaton(Automaton a, File file){    	
        PrintStream ps = IOUtilities.getPrintStream(file);
        if(ps == null)
        	saveAutomatonAs(a);
        else
        {
        	XMLexporter.automatonToXML(a, ps);
        	a.setName(ParsingToolbox.removeFileType(file.getName()));
        	a.setFile(file);
            Hub.persistentData.setProperty(LAST_PATH_SETTING_NAME,file.getParent());
            a.notifyAllSubscribers();
        }
    }  
    
	
	public static void saveAutomatonAs(Automaton a) {		
		JFileChooser fc = new JFileChooser(Hub.persistentData.getProperty(LAST_PATH_SETTING_NAME));
		fc.setDialogTitle(Hub.string("saveModelTitle"));
		fc.setFileFilter(new ExtensionFileFilter(IOUtilities.MODEL_FILE_EXT, Hub.string("modelFileDescription")));
    	int retVal = fc.showSaveDialog(Hub.getMainWindow());
    	if(retVal == JFileChooser.APPROVE_OPTION){
    		File file=fc.getSelectedFile();
    		if(!file.getName().toLowerCase().endsWith("."+IOUtilities.MODEL_FILE_EXT))
    			file=new File(file.getPath()+"."+IOUtilities.MODEL_FILE_EXT);
    		saveAutomaton(a,file);
    	}
	}
	
	public static void exportSystemAsLatex(FSAModel model, File f){
		
	}
	
	public static void exportSystemAsEPS(FSAModel model, File f){
		
		
	}
	
	public static WorkspaceDescriptor openWorkspace(File file){
        WorkspaceDescriptor wd = null;
        if(!file.canRead())
        {
        	Hub.displayAlert(Hub.string("fileCantRead")+file.getPath());
        	return wd;
        }
        WorkspaceParser wdp = new WorkspaceParser();	    	
        wd = wdp.parse(file);
        if(!"".equals(wdp.getParsingErrors()))
        {
        	Hub.displayAlert(Hub.string("errorsParsingXMLFileL1")+file.getPath()+
        			"\n"+Hub.string("errorsParsingXMLFileL2"));
        }
        Hub.persistentData.setProperty(LAST_PATH_SETTING_NAME,file.getParent());    		
        return wd;
	}
	

	/**
	 * Saves the workspace. If the file name is invalid, calls
	 * {@link #saveWorkspaceAs(WorkspaceDescriptor)} to get a new file name.
	 * @param wd the description of the workspace
	 * @param file the file where the workspace will be written
	 */
    public static void saveWorkspace(WorkspaceDescriptor wd, File file){
        PrintStream ps = IOUtilities.getPrintStream(file);
        if(ps == null)
        	saveWorkspaceAs(wd);
        else
        {
        	XMLexporter.workspaceToXML(wd, ps);
        	Hub.getWorkspace().setFile(file);
            Hub.persistentData.setProperty(LAST_PATH_SETTING_NAME,file.getParent());
        }
    }

    /**
     * Ask the user for a file name and then call {@link #saveWorkspace(WorkspaceDescriptor, File)}.
     * @param wd the description of the workspace
     */
    public static void saveWorkspaceAs(WorkspaceDescriptor wd){
		JFileChooser fc = new JFileChooser(Hub.persistentData.getProperty(LAST_PATH_SETTING_NAME));
		fc.setDialogTitle(Hub.string("saveWorkspaceTitle"));
		fc.setFileFilter(new ExtensionFileFilter(IOUtilities.WORKSPACE_FILE_EXT, Hub.string("workspaceFileDescription")));
    	int retVal = fc.showSaveDialog(Hub.getMainWindow());
    	if(retVal == JFileChooser.APPROVE_OPTION){
    		File file=fc.getSelectedFile();
    		if(!file.getName().toLowerCase().endsWith("."+IOUtilities.WORKSPACE_FILE_EXT))
    			file=new File(file.getPath()+"."+IOUtilities.WORKSPACE_FILE_EXT);
    		saveWorkspace(wd,file);
    	}
    }

	/**
	 * TODO move this to the io or ui.command package; it doesn't do anything with FSAs. 
	 * 
	 * Loads the command configuration files and initializes the CommandManager 
	 * (i.e. the singleton class that manages and locates ActionCommand and CommandGroup instances.)
	 * 
	 * @param commandFileName the absolute path to the command configuration file.
	 */
	public static void loadCommandManager(String commandFileName){
//		load the xml command definition and initialize the manager.
		File myCommandFile = new File(commandFileName);
		try {
		   CommandManager.defaultInstance().load(myCommandFile);
		} catch (LoadException e){
		   // oops
		   e.printStackTrace();
		   System.exit(1);	
		}		
	}
	
	/**
	 * Loads all ActionCommand subclasses in the package with the given name
	 * and exports them so they can be seen by menu and toolbar groups. 
	 * 
	 * Precondition: the given package name contains only classes that extend ActionCommand
	 * 
	 * @param packageName
	 */
	public static void loadAndExportCommands(String commandsFileName){	
		//ClassLoader loader = ClassLoader.getSystemClassLoader();
		BufferedReader in;
		String s;
	    
		try {	
			in = new BufferedReader(new FileReader(SystemVariables.instance().getSystem_path() + commandsFileName));
			s = in.readLine();
		    while (s != null) {
					ActionCommand cmd = (ActionCommand)Class.forName(s).newInstance();
					cmd.export();
					s = in.readLine();
		    }
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	


  
    
    private static class ClassFileFilter implements FilenameFilter{    	
		    public boolean accept(File dir, String name) {
		        return (name.endsWith(".class"));
		    }
    }
 }

