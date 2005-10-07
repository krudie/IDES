/*
 * Created on Jun 22, 2004
 */
package userinterface.menu.listeners;
 
import ides2.SystemVariables;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import userinterface.MainWindow;
import userinterface.ResourceManager;
import userinterface.Userinterface;


/**
 * This class handles all events the fall under the "File" menu concept.
 * 
 * @author Michael Wood
 */
public class FileListener extends AbstractListener{
	
	
	private Shell shell;
        
    
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ListenersFile construction /////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * Construct the ListenersFile.
     * 
     * @param shell The main shell
     */
	public FileListener(Shell shell){
		this.shell = shell;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// adapters ///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	/**
	 * Find the appropriate Listener for this resource.
	 * 
	 * @param   resource_handle		The constant identification for a concept in the ResourceManager.
	 * @return	The appropriate Listener for this resource.
	 */
	public SelectionListener getListener(String resource_handle){
        if (resource_handle.equals(ResourceManager.FILE_NEW_PROJECT))  { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { newProject(e);   } }; }
        if (resource_handle.equals(ResourceManager.FILE_OPEN_PROJECT)) { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { openProject(e);        } }; }
        if (resource_handle.equals(ResourceManager.FILE_SAVE_PROJECT)) { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { saveProject(e);        } }; }
        if (resource_handle.equals(ResourceManager.FILE_EXIT))         { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { exit(e);        } }; }  
        if (resource_handle.equals(ResourceManager.FILE_NEW_AUTOMATON)){ return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { newAutomaton(e);   } }; }
		if (resource_handle.equals(ResourceManager.FILE_OPEN_AUTOMATON)){ return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { openAutomaton(e);        } }; }
		if (resource_handle.equals(ResourceManager.FILE_SAVE_AUTOMATON)){ return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { saveAutomaton(e);        } }; }
		System.out.println("Error: no match for resource_handle = " + resource_handle);
		return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { } };
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// listeners //////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
	
    /**
     * Create a new Automaton in the project
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void newAutomaton(org.eclipse.swt.events.SelectionEvent e){
        if(Userinterface.getProjectPresentation().isProjectOpen()){
            Userinterface.getProjectPresentation().addAutomaton(MainWindow.getProjectExplorer().getNewTitle());
            MainWindow.getProjectExplorer().updateProject();

        }
        
	}	
	
    /**
     * Open a gml file and load the graph.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void openAutomaton(org.eclipse.swt.events.SelectionEvent e){
       

	}	
	
    /**
     * Save the current data.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void saveAutomaton(org.eclipse.swt.events.SelectionEvent e) {
       
	} 
	
	

    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // listeners //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////         
    
    /**
     * Create a new Project
     * 
     * @param   e   The SelectionEvent that initiated this action.
     */
    public void newProject(org.eclipse.swt.events.SelectionEvent e){
        if(Userinterface.getProjectPresentation().hasUnsavedData()){
            MessageBox unsaved_changes = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL); 
            unsaved_changes.setText(ResourceManager.getString("file_sys.warning"));
            unsaved_changes.setMessage(ResourceManager.getString("file_sys.unsaved_changes"));
            int response = unsaved_changes.open();
            switch(response){
                case SWT.YES: 
                    // call the save listener
                    saveProject(e);
                    newProject(e);
                    return;
                case SWT.NO: 
                    break;
                case SWT.CANCEL:
                    // do nothing
                    return;
            }
        }
        
        MainWindow.getMenu().file_save_project.enable();
        MainWindow.getMenu().file_new_automaton.enable();
        MainWindow.getMenu().file_open_automaton.enable();
        Userinterface.getProjectPresentation().newProject(ResourceManager.getString("new_project_untitled"));
        MainWindow.getProjectExplorer().updateProject();
    }
    
    /**
     * Opens a project
     * 
     * @param   e   The SelectionEvent that initiated this action.
     */
    public void openProject(org.eclipse.swt.events.SelectionEvent e){
        
        if(Userinterface.getProjectPresentation().hasUnsavedData()){
            MessageBox unsaved_changes = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL); 
            unsaved_changes.setText(ResourceManager.getString("file_sys.warning"));
            unsaved_changes.setMessage(ResourceManager.getString("file_sys.unsaved_changes"));
            int response = unsaved_changes.open();
            switch(response){
                case SWT.YES: 
                    // call the save listener
                    saveProject(e);
                    openProject(e);
                    return;  
                case SWT.NO: 
                    break;
                case SWT.CANCEL:
                    // do nothing
                    return;
            }
        }
       
        FileDialog openDialog = new FileDialog(shell, SWT.OPEN); 
        openDialog.setText(ResourceManager.getToolTipText(ResourceManager.FILE_OPEN_PROJECT)); 
        openDialog.setFilterExtensions(new String[] {"*.xml", "*.*"});
        if (SystemVariables.last_used_path != null && SystemVariables.last_used_path.length() > 0){
            openDialog.setFilterPath(SystemVariables.last_used_path);
        }
        String openLocation = openDialog.open();
        if(openLocation != null){
            SystemVariables.last_used_path = openDialog.getFilterPath();
            String error = Userinterface.getProjectPresentation().openProject(new File(openLocation));

            if(!error.trim().equals("")){
                MainWindow.errorPopup(ResourceManager.getString("parsing_error"), error);
            }
            
            MainWindow.getMenu().file_save_project.enable();
            MainWindow.getMenu().file_new_automaton.enable();
            MainWindow.getMenu().file_open_automaton.enable();
            MainWindow.getProjectExplorer().updateProject();
        }
        

    }   
    
    /**
     * Save the project
     * 
     * @param   e   The SelectionEvent that initiated this action.
     */
    public void saveProject(org.eclipse.swt.events.SelectionEvent e) {
        String saveLocation = getSaveLocation(ResourceManager.getToolTipText(ResourceManager.FILE_SAVE_PROJECT), new String[] {"*.xml", "*.*"});
        if(saveLocation == null) return;
        Userinterface.getProjectPresentation().saveProject(saveLocation);
        MainWindow.getProjectExplorer().updateProject();
        Userinterface.getProjectPresentation().setUnsavedData(false);
    } 
    

    /**
     * Exit the system.
     * 
     * @param   e   The SelectionEvent that initiated this action.
     */
    public void exit(org.eclipse.swt.events.SelectionEvent e){
        if(Userinterface.getProjectPresentation().hasUnsavedData()){
            MessageBox unsaved_changes = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL); 
            unsaved_changes.setText(ResourceManager.getString("file_sys.warning"));
            unsaved_changes.setMessage(ResourceManager.getString("file_sys.unsaved_changes"));
            int response = unsaved_changes.open();
            switch(response){
                case SWT.YES: 
                    // call the save listener
                    saveProject(e);
                    exit(e);
                    return;  
                case SWT.NO: 
                    break;
                case SWT.CANCEL:
                    // do nothing
                    return;
            }
        }
        shell.dispose();       
    }
    
    
 
    /**
     * 
     * @param dialogTitle The title of the dialog box
     * @param filterExtensions The filters that the user can choose from in the save dialog
     * @return The file location to save to, null if the user cancels
     */
    private String getSaveLocation(String dialogTitle, String[] filterExtensions){
        
        
        FileDialog saveDialog = new FileDialog(shell, SWT.SAVE); 
        saveDialog.setText(dialogTitle); 
        saveDialog.setFilterExtensions(filterExtensions); 
        saveDialog.setFileName(Userinterface.getProjectPresentation().getProjectName());
        if (SystemVariables.last_used_path != null && SystemVariables.last_used_path.length() > 0){
            saveDialog.setFilterPath(SystemVariables.last_used_path);
        }
        String saveLocation = null;
        
        boolean userAccepts = false;
        
        while(!userAccepts){
            saveLocation = saveDialog.open();
            
            if(saveLocation == null){
                return null;
            }
            
            String[] automataNames = Userinterface.getProjectPresentation().getAutomataNames();
            File file;
            String fileNames = new String();
            
            for(int i = 0; i < automataNames.length;i++){
                file = new File(saveDialog.getFilterPath() + "/"+ automataNames[i] + ".xml");
                if(file.exists()){
                    fileNames += file.getAbsolutePath() + "\n";
                }
            }
            
            file = new File(saveLocation);
            
            if((file.exists()) || (fileNames.length() != 0)){
                
                fileNames = file.exists() ? fileNames + file.getAbsolutePath() + "\n" : fileNames;
                
                MessageBox confirmOverwrite = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
                confirmOverwrite.setText(dialogTitle);
                confirmOverwrite.setMessage(ResourceManager.getMessage("file_sys.confirm_overwrite",fileNames));
                int response = confirmOverwrite.open();
                switch(response){
                    case SWT.YES: 
                        // continue with the operation
                        userAccepts = true;
                        break;
                    case SWT.NO: 
                        // let them choose a different file
                        break;
                }
            } else {
                userAccepts = true;
            }
            
            
        }
        
        String projectName = Userinterface.getProjectPresentation().removeFileName(saveDialog.getFileName());
        Userinterface.getProjectPresentation().setProjectName(projectName);  
        return saveDialog.getFilterPath();
        
    }  
    

}