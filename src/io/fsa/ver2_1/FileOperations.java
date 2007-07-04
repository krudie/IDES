package io.fsa.ver2_1;

import io.IOUtilities;
import io.ParsingToolbox;
import io.WorkspaceParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import main.Annotable;
import main.Hub;
import main.WorkspaceDescriptor;
import model.fsa.FSAModel;
import model.fsa.ver2_1.Automaton;

import org.pietschy.command.CommandManager;
import org.pietschy.command.LoadException;
import org.pietschy.command.file.ExtensionFileFilter;

/**
 * Contains operations for reading and writing automaton and workspace files.
 * 
 * @author Helen Bretzke
 * @author Lenko Grigorov
 */
public class FileOperations {
	
	public static final String LAST_PATH_SETTING_NAME="lastUsedPath";
	
	public static FSAModel openAutomaton(File f) {
        FSAModel a = null;
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
        	a.setAnnotation(Annotable.FILE,f);
        }
        Hub.persistentData.setProperty(LAST_PATH_SETTING_NAME,f.getParent());
        return a;
	}
		
	/**
     * Saves the given automaton to <code>file</code> and
     * if successful, stores the path to the location and returns true. 
     * 
     * @param a the automaton to save
     * @param file the file to save it to
     * @return if file was saved
     */      
    public static boolean saveAutomaton(FSAModel a, File file){    	
        PrintStream ps = IOUtilities.getPrintStream(file);
        if(ps == null){
        	
        	return saveAutomatonAs(a);
        
        }else{
        	// write the automaton to file
        	XMLexporter.automatonToXML(a, ps);
        	
        	String newName=ParsingToolbox.removeFileType(file.getName());
        	if(!newName.equals(a.getName())
        			&&Hub.getWorkspace().getModel(newName)!=null)
        		Hub.getWorkspace().removeModel(newName);
        	
        	a.setName(newName);
        	a.setAnnotation(Annotable.FILE,file);
        	a.fireFSASaved(); 
        	
            Hub.persistentData.setProperty(LAST_PATH_SETTING_NAME,file.getParent());
            
            return true;
        }
    }  
    
	/**
	 * Presents a file dialog and saves the given automaton to a file selected 
	 * by the user. If successful, stores the path to the location and returns true. 
	 * 
	 * @param a automaton to save
	 * @return if file was saved
	 */
	public static boolean saveAutomatonAs(FSAModel a) {
		JFileChooser fc;
		
		if((File)a.getAnnotation(Annotable.FILE)!=null){
			fc=new JFileChooser(((File)a.getAnnotation(Annotable.FILE)).getParent());
		}else{
			fc=new JFileChooser(Hub.persistentData.getProperty(LAST_PATH_SETTING_NAME));
		}
		
		fc.setDialogTitle(Hub.string("saveModelTitle"));
		fc.setFileFilter(new ExtensionFileFilter(IOUtilities.MODEL_FILE_EXT, 
				Hub.string("modelFileDescription")));
		
		if((File)a.getAnnotation(Annotable.FILE)!=null){
			fc.setSelectedFile((File)a.getAnnotation(Annotable.FILE));
		}else{
			fc.setSelectedFile(new File(a.getName()));
		}
		
		int retVal;
		boolean fcDone=true;
		File file=null;
		do
		{
			retVal = fc.showSaveDialog(Hub.getMainWindow());
			if(retVal != JFileChooser.APPROVE_OPTION)
				break;
			file=fc.getSelectedFile();
    		if(!file.getName().toLowerCase().endsWith("."+IOUtilities.MODEL_FILE_EXT))
    			file=new File(file.getPath()+"."+IOUtilities.MODEL_FILE_EXT);
			if(file.exists())
			{
				int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
					Hub.string("fileExistAsk1")+file.getPath()+Hub.string("fileExistAsk2"),
					Hub.string("saveModelTitle"),
					JOptionPane.YES_NO_CANCEL_OPTION);
				fcDone=choice!=JOptionPane.NO_OPTION;
				if(choice!=JOptionPane.YES_OPTION)
					retVal=JFileChooser.CANCEL_OPTION;
			}
		} while(!fcDone);
    	
		if(retVal == JFileChooser.APPROVE_OPTION){
    		return saveAutomaton(a,file);
		}
    	return false;
	}
	
	public static void exportSystemAsLatex(FSAModel model, File f){
		
	}
	
	public static void exportSystemAsEPS(FSAModel model, File f){
		
		
	}
	
	/**
	 * Opens the workspace described in the given configuration file. 
	 * 
	 * @param file the file containing the workspace description
	 * @return a workspace descriptor object if file is valid, null otherwise
	 */
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
	 * Saves the workspace described by <code>wd</code>. If the file name is invalid, calls
	 * {@link #saveWorkspaceAs(WorkspaceDescriptor)} to get a new file name.
	 * 
	 * @param wd the description of the workspace
	 * @param file the file where the workspace will be written
	 * @return true if file was saved
	 */
    public static boolean saveWorkspace(WorkspaceDescriptor wd, File file){
        PrintStream ps = IOUtilities.getPrintStream(file);
        if(ps == null)
        	return saveWorkspaceAs(wd);
        else
        {
        	XMLexporter.workspaceToXML(wd, ps);
        	Hub.getWorkspace().setFile(file);
            Hub.persistentData.setProperty(LAST_PATH_SETTING_NAME,file.getParent());
            return true;
        }
    }

    /**
     * Asks the user for a file name and then calls {@link #saveWorkspace(WorkspaceDescriptor, File)}.
     * 
     * @param wd the description of the workspace
     * @return true if file was saved
     */
    public static boolean saveWorkspaceAs(WorkspaceDescriptor wd){
    	JFileChooser fc;
    	
    	if(wd.getFile()!=null)
			fc=new JFileChooser(wd.getFile().getParent());
		else
			fc=new JFileChooser(Hub.persistentData.getProperty(LAST_PATH_SETTING_NAME));
    	
		fc.setDialogTitle(Hub.string("saveWorkspaceTitle"));
		fc.setFileFilter(new ExtensionFileFilter(IOUtilities.WORKSPACE_FILE_EXT, Hub.string("workspaceFileDescription")));
		
		if(wd.getFile()!=null)
			fc.setSelectedFile(wd.getFile());
		else
			fc.setSelectedFile(new File(Hub.string("newModelName")));
		
		int retVal;
		boolean fcDone=true;
		File file=null;
		do
		{
			retVal = fc.showSaveDialog(Hub.getMainWindow());
			if(retVal != JFileChooser.APPROVE_OPTION)
				break;
    		file=fc.getSelectedFile();
    		if(!file.getName().toLowerCase().endsWith("."+IOUtilities.WORKSPACE_FILE_EXT))
    			file=new File(file.getPath()+"."+IOUtilities.WORKSPACE_FILE_EXT);
    		
			if(file.exists())
			{
				int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
					Hub.string("fileExistAsk1")+file.getPath()+Hub.string("fileExistAsk2"),
					Hub.string("saveWorkspaceTitle"),
					JOptionPane.YES_NO_CANCEL_OPTION);
				fcDone=choice!=JOptionPane.NO_OPTION;
				if(choice!=JOptionPane.YES_OPTION)
					retVal=JFileChooser.CANCEL_OPTION;
			}
		} while(!fcDone);
		
    	if(retVal == JFileChooser.APPROVE_OPTION)
    		return saveWorkspace(wd,file);
    	
    	return false;
    }

	/**
	 * Loads the commands definitions from an xml file with the given name 
	 * and initializes the command manager (i.e. the singleton class that 
	 * manages and locates ActionCommand and CommandGroup instances.)
	 * 
	 * TODO move this to the io or ui.command package; it doesn't do anything with FSAs. 
	 *  
	 * @param commandFileName the absolute path to the command configuration file.
	 */
	public static void loadCommandManager(String commandFileName){		
		try {
		   CommandManager.defaultInstance().load(Hub.getResource(commandFileName));
		} catch (LoadException e){
			throw new RuntimeException(e);
		}		
	}	    
    
 }

