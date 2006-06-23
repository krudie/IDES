package io.fsa.ver1;

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
	
	// DEBUG
	public static final String DEFAULT_DIRECTORY = "C:/Documents and Settings/helen/My Documents/development/output/";
	
	public static FSAModel openAutomaton(File f) {		
	    AutomatonParser ap = new AutomatonParser();	    	
        Automaton automaton = ap.parse(f);
        // DEBUG
        System.out.println("Parent: " + f.getParent());
        SystemVariables.instance().setLast_used_path(f.getParent());
        automaton.setName(ParsingToolbox.removeFileType(f.getName()));
        return automaton;		
	}
		
	/**
     * Saves an automaton to a file
     * @param a the automaton to save
     * @param path the path to save it to
     */      
    public static void saveAutomaton(Automaton a){
    	System.out.println("Last used path: " + SystemVariables.instance().getLast_used_path());
        File file = new File(SystemVariables.instance().getLast_used_path(), a.getName() + ".xml");
        PrintStream ps = getPrintStream(file);
        if(ps == null) {
	        JFileChooser fc = new JFileChooser();
	        fc.setFileFilter(new ExtensionFileFilter("xml", "eXtensible Markup Language"));
        	int retVal = fc.showSaveDialog(null);
        	if(retVal == JFileChooser.APPROVE_OPTION){
        		file = fc.getSelectedFile();
        		ps = getPrintStream(file);
        		if(ps == null) return;
        	}
        }
        XMLexporter.automatonToXML(a, ps);        
    }  
    
	
	public static void saveAutomatonAs(Automaton a) {		
		File file;
		PrintStream ps;
		JFileChooser fc = new JFileChooser(SystemVariables.instance().getLast_used_path());
		fc.setFileFilter(new ExtensionFileFilter("xml", "eXtensible Markup Language"));
    	int retVal = fc.showSaveDialog(null);
    	if(retVal == JFileChooser.APPROVE_OPTION){    		
    		file = fc.getSelectedFile();  
    		
    		// FIXME this doesn't seem to work
    		file.renameTo(new File(file.getName() + ".xml"));
    		ps = getPrintStream(file);
    		if(ps == null) return;
    		int i = file.getName().lastIndexOf(".");    		
    		IDESWorkspace.instance().removeFSAModel(a.getName());
    		if(i > -1) {
    			a.setName(file.getName().substring(0, file.getName().lastIndexOf(".")));
    		}else{
    			a.setName(file.getName());
    		}
    		a.notifyAllSubscribers();   		
    		IDESWorkspace.instance().addFSAModel(a);    		
    		XMLexporter.automatonToXML(a, ps);
    		SystemVariables.instance().setLast_used_path(file.getAbsolutePath());
    	}    	
	}
	
	public static void exportSystemAsLatex(FSAModel model, File f){
		
	}
	
	public static void exportSystemAsEPS(FSAModel model, File f){
		
		
	}
	
	public static IDESWorkspace loadWorkspace(File f){
		return null;
	}
	
	
	/**
     * TODO give user opportunity to choose a different file if something screws up.
     * 
     * @see projectPresentation.ProjectPresentation#saveWorkspace(java.lang.String)
     */
    public static void saveWorkspace(String path){
        File file = new File(path, IDESWorkspace.instance().getName() + ".xml");
        PrintStream ps = getPrintStream(file);
        if(ps == null) return;
  // TODO  XMLexporter.workspaceToXML(IDESWorkspace.instance(), ps);
        Iterator<Automaton> ai = IDESWorkspace.instance().getAutomata();
        while(ai.hasNext()){
            Automaton a = ai.next();
            saveAutomaton(a);
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
	
	/**
     * Method for getting a printstream wrapped around a file
     * @param file the file that needs a printstream wrapped around it
     * @return The printstream pointing to a the file
     */
    private static PrintStream getPrintStream(File file){
        PrintStream ps = null;

	        if(!file.exists()){
	            try{
	                file.createNewFile();
	            }
	            catch(IOException ioe){
	                System.err.println("FileOperations: unable to create file, message: "
	                        + ioe.getMessage());
	                return null;
	            }
	        }
	        if(!file.isFile()){
	            System.err.println("FileOperations: " + file.getName() + " is not a file. ");
	            return null;
	        }
	        if(!file.canWrite()){
	            System.err.println("FileOperations: can not write to file: " + file.getName());
	            return null;
	        } 
   
	        try{
	            ps = new PrintStream(file);	            
	        }
	        catch(FileNotFoundException fnfe){	        	
	            System.err.println("FileOperations: file missing: " + fnfe.getMessage());
	            return null;
	        }
	            
        return ps;
    }

  
    
    private static class ClassFileFilter implements FilenameFilter{    	
		    public boolean accept(File dir, String name) {
		        return (name.endsWith(".class"));
		    }
    }
 }

