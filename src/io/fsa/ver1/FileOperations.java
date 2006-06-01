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

import org.pietschy.command.ActionCommand;
import org.pietschy.command.CommandManager;
import org.pietschy.command.LoadException;

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
	
	public static final String DEFAULT_DIRECTORY = "C:/Documents and Settings/helen/My Documents/development/output/";
	
	public static FSAModel openSystem(File f) {	
	    AutomatonParser ap = new AutomatonParser();	    	
        Automaton automaton = ap.parse(f);	        
        automaton.setName(ParsingToolbox.removeFileType(f.getName()));
        return automaton;		
	}
	
	public static void saveSystem(FSAModel model) {
		// TODO call saveSystemAs with default directory from SystemVariables
				
	}
	
	public static void saveSystemAs(FSAModel model, File f) {
		// TODO implement
		
		// merge metadata with automaton data structure and use legacy code to write the xml
		
		// open file output stream
		
		// write file
		
		// handle runtime errors
		
	}
	
	public static void exportSystemAsLatex(FSAModel model, File f){
		
	}
	
	public static void exportSystemAsEPS(FSAModel model, File f){
		
		
	}
	
	public static IDESWorkspace loadWorkspace(File f){
		return null;
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
            System.out.println("FileOperations: file missing: " + fnfe.getMessage());
            return null;
        }
        return ps;
    }

  
    /**
     * @see projectPresentation.ProjectPresentation#saveProject(java.lang.String)
     */
    public static void saveProject(String path){
        File file = new File(path, IDESWorkspace.instance().getName() + ".xml");
        PrintStream ps = getPrintStream(file);
        if(ps == null) return;
  // TODO  XMLexporter.workspaceToXML(IDESWorkspace.instance(), ps);
        Iterator<Automaton> ai = IDESWorkspace.instance().getAutomata();
        while(ai.hasNext()){
            Automaton a = ai.next();
            saveAutomaton(a, path);
        }
    }

    /**
     * Saves an automaton to a file
     * @param a the automaton to save
     * @param path the path to save it to
     */      
    public static void saveAutomaton(Automaton a, String path){
        File file = new File(path, a.getName() + ".xml");
        PrintStream ps = getPrintStream(file);
        if(ps == null) return;
        XMLexporter.automatonToXML(a, ps);        
    }  
    
    private static class ClassFileFilter implements FilenameFilter{    	
		    public boolean accept(File dir, String name) {
		        return (name.endsWith(".class"));
		    }
    }
 }

