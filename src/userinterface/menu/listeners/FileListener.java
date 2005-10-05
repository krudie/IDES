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
	
	
	Shell shell;
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
		if (resource_handle.equals(ResourceManager.FILE_NEW_AUTOMATON)){ return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { newAutomaton(e);   } }; }
		if (resource_handle.equals(ResourceManager.FILE_OPEN))         { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { open(e);        } }; }
		if (resource_handle.equals(ResourceManager.FILE_SAVE))         { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { save(e);        } }; }
		if (resource_handle.equals(ResourceManager.FILE_SAVEAS))       { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { saveAs(e);      } }; }
		if (resource_handle.equals(ResourceManager.FILE_EXIT))         { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { exit(e);        } }; }		
		System.out.println("Error: no match for resource_handle = " + resource_handle);
		return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { } };
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// listeners //////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
	/**
     * Create a new Project
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void newProject(org.eclipse.swt.events.SelectionEvent e){
        if(!checkSaved(e)) return;
        
        Userinterface.getProjectPresentation().newProject(ResourceManager.getString("new_project_untitled"));
        MainWindow.getProjectExplorer().updateProject();
    }
	
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
	public void open(org.eclipse.swt.events.SelectionEvent e){
		
        if(!checkSaved(e)) return;
       
        FileDialog openDialog = new FileDialog(shell, SWT.OPEN); 
		openDialog.setText(ResourceManager.getToolTipText(ResourceManager.FILE_OPEN)); 
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
            
            MainWindow.getProjectExplorer().updateProject();
		}
        

	}	
	
    /**
     * Save the current data.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void save(org.eclipse.swt.events.SelectionEvent e) {
		getSaveLocation(ResourceManager.getToolTipText(ResourceManager.FILE_SAVE), new String[] {"*.xml", "*.*"});
        
        Userinterface.getProjectPresentation().setUnsavedData(false);
	} 
	
    /**
     * Save the current data.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void saveAs(org.eclipse.swt.events.SelectionEvent e) {
		getSaveLocation(ResourceManager.getToolTipText(ResourceManager.FILE_SAVEAS), new String[] {"*.xml", "*.*"});
        Userinterface.getProjectPresentation().setUnsavedData(false);
	}

    /**
     * Exit the system.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void exit(org.eclipse.swt.events.SelectionEvent e){
        if(!checkSaved(e)) return;
        shell.dispose();
	}
	
	
	
	private String getSaveLocation(String dialogTitle, String[] filterExtensions){
		FileDialog saveDialog = new FileDialog(shell, SWT.SAVE); 
		saveDialog.setText(dialogTitle); 
		saveDialog.setFilterExtensions(filterExtensions); 
		String saveLocation = saveDialog.open();
		if (SystemVariables.last_used_path != null && SystemVariables.last_used_path.length() > 0){
			saveDialog.setFilterPath(SystemVariables.last_used_path);
		}
		if(saveLocation != null){
			SystemVariables.last_used_path = saveDialog.getFilterPath();
		}
		
		return saveLocation;
	}
    
    /**
     * Checks if there are unsaved stuff and asks the user to save it
     * @param e
     * @return a boolean indicating wheter or not to continue
     */
    
    private boolean checkSaved(org.eclipse.swt.events.SelectionEvent e){
        if(Userinterface.getProjectPresentation().hasUnsavedData()){
            MessageBox unsaved_changes = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL); 
            unsaved_changes.setText(ResourceManager.getString("file_sys.warning"));
            unsaved_changes.setMessage(ResourceManager.getString("file_sys.unsaved_changes"));
            int response = unsaved_changes.open();
            switch(response)
            {
                case SWT.YES: 
                    // call the save listener
                    save(e);
                    break;  
                case SWT.NO: 
                    break;
                case SWT.CANCEL:
                    // do nothing
                    return false;
            }
        }
           return true;
    }

}