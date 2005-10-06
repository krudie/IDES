/**
 * 
 */
package userinterface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author edlund
 *
 */
public class ProjectExplorer {

	Tree treeWindow;
        
    final Color black = Display.getDefault().getSystemColor (SWT.COLOR_BLACK);
    ProjectExplorerPopup pep;
    
    TreeItem project = null;
    TreeItem automata[] = null;
    
    

	
	public ProjectExplorer(Composite parent, Shell shell){
		treeWindow = new Tree(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);     
        
        pep = new ProjectExplorerPopup(shell, this);
                
        ProjectListener projectListener = new ProjectListener();
        treeWindow.addListener(SWT.MouseDown, projectListener);  
        treeWindow.addListener(SWT.KeyDown, projectListener);
	}
	
    /**
     * Updates the window of the projectexplorer
     */
    public void updateProject(){
        treeWindow.removeAll();
         
        //gets the project
        if(Userinterface.getProjectPresentation().getProjectName() ==null){
            return;
        }
        project = new TreeItem(treeWindow, SWT.NONE);
        project.setText(Userinterface.getProjectPresentation().getProjectName());
        project.setImage(ResourceManager.getHotImage(ResourceManager.FILE_NEW_PROJECT));
 
        
        String[] automataNames = Userinterface.getProjectPresentation().getAutomataNames();
        
        if(automataNames != null){
            automata = new TreeItem[automataNames.length];
        
            for(int i = 0; i < automataNames.length; i++){
                automata[i] = new TreeItem(project, SWT.NONE);
                automata[i].setText(automataNames[i]);              
                automata[i].setImage(ResourceManager.getHotImage(ResourceManager.FILE_NEW_AUTOMATON));
            }
        }
        project.setExpanded(true);
        
    }
    
    /**
     * Finds the first availble name for a new automaton
     * @return the name of the new automaton
     */
    public String getNewTitle(){
        
        boolean free = false;
        int i = 1;
        String title = null;
        while(!free){
            free = true;
            title = ResourceManager.getString("new_automaton_untitled") + i;
            
            if(project.getText().equals(title)){              
                free = false;
                i++;
                continue;
            }
            for(int j = 0; j < automata.length; j++){
                if(automata[j].getText().equals(title)){
                    free = false;
                    break;
                }
            }
            i++;
        }
        
        return title;
    }
    
    /**
     * Brings up a renaming box for the automatons
     * @param item the treeItem to be renamed
     */
    public void rename(TreeItem selected){
        final Color black = Display.getDefault().getSystemColor (SWT.COLOR_BLACK);
        final TreeItem item = selected;
        final TreeEditor editor = new TreeEditor (treeWindow);

        if (item != null) {
            boolean isCarbon = SWT.getPlatform ().equals ("carbon");
            final Composite composite = new Composite (treeWindow, SWT.NONE);
            if (!isCarbon) composite.setBackground (black);
            final Text text = new Text (composite, SWT.NONE);
            final int inset = isCarbon ? 0 : 1;
            composite.addListener (SWT.Resize, new Listener () {
                public void handleEvent (Event e) {
                    Rectangle rect = composite.getClientArea ();
                    text.setBounds (rect.x + inset, rect.y + inset, rect.width - inset * 2, rect.height - inset * 2);
                }
            });
            
            //makes the listener for the popup box
            Listener textListener = new Listener () {
                boolean looseFocus = false;
                                
                public void handleEvent (final Event e) {
                                        
                    switch (e.type) {
                    case SWT.FocusOut:
                        if(!looseFocus){
                            if(!setName()){
                                break;
                            }
                            composite.dispose ();
                        }
                        break;
                    case SWT.Verify:
                        String newText = text.getText ();
                        String leftText = newText.substring (0, e.start);
                        String rightText = newText.substring (e.end, newText.length ());
                        GC gc = new GC (text);
                        Point size = gc.textExtent (leftText + e.text + rightText);
                        gc.dispose ();
                        size = text.computeSize (size.x, SWT.DEFAULT);
                        editor.horizontalAlignment = SWT.LEFT;
                        Rectangle itemRect = item.getBounds (), rect = treeWindow.getClientArea ();
                        editor.minimumWidth = Math.max (size.x, itemRect.width) + inset * 2;
                        int left = itemRect.x, right = rect.x + rect.width;
                        editor.minimumWidth = Math.min (editor.minimumWidth, right - left);
                        editor.minimumHeight = size.y + inset * 2;
                        editor.layout ();
                        break;
                            
                        case SWT.Traverse:
                            switch (e.detail) {
                            case SWT.TRAVERSE_RETURN:
                                    if(!setName()){
                                        break;
                                    }
                                //  FALL THROUGH
                                case SWT.TRAVERSE_ESCAPE:
                                    composite.dispose ();
                                    e.doit = false;
                            }
                            break;

                    }
                    
                }
                    /**
                     * Sets the name if the name does not break the given rules
                     * @return wheter the name was legal or not
                     */
                    private boolean setName(){
                        //Testing if the name is empty
                        if(text.getText().trim().equals("")){
                            looseFocus = true;
                            MainWindow.errorPopup(ResourceManager.getString("naming_error.title"), ResourceManager.getString("naming_error.empty"));
                            looseFocus = false;
                            return false; 
                        }
                        
                        //testing if the name is already used
                        if(!item.equals(project) && project.getText().equals(text.getText())){
                            looseFocus = true;
                            MainWindow.errorPopup(ResourceManager.getString("naming_error.title"), ResourceManager.getString("naming_error.used"));
                            looseFocus = false;
                            return false; 
                        }
                        
                        for(int i = 0; i<automata.length; i++){
                            if((!item.equals(automata[i])) && (automata[i].getText().equals(text.getText()))){
                                looseFocus = true;
                                MainWindow.errorPopup(ResourceManager.getString("naming_error.title"), ResourceManager.getString("naming_error.used"));
                                looseFocus = false;
                                return false;
                            }
                        }
                        
                                                    
                        if(item.equals(project)){
                            Userinterface.getProjectPresentation().setProjectName(text.getText());
                        } else{
                             Userinterface.getProjectPresentation().setAutomatonName(item.getText(),text.getText());                               
                        }
                        updateProject();
                        return true;
                    }
            };
            text.addListener (SWT.FocusOut, textListener);
            text.addListener (SWT.Traverse, textListener);
            text.addListener (SWT.Verify, textListener);
            editor.setEditor (composite, item);
            text.setText (item.getText ());
            text.selectAll ();
            text.setFocus ();
        }
    }
    
 
    public void delete(){
        for(int i = 0; i <treeWindow.getSelection().length; i++){
            Userinterface.getProjectPresentation().deleteAutomatonByName(treeWindow.getSelection()[i].getText());
        }
        updateProject();
    }
  
    /**
     * Private Listener implemenetation for the ProjectExplorer
     * @author edlund
     *
     */
    private class ProjectListener implements Listener{
        
        /**
         * Method that is used by the interface
         */
        public void handleEvent(Event event){
            TreeItem item ; 
            
            switch(event.type){
            case SWT.MouseDown:
            
                Point point = new Point (event.x, event.y);
                item = treeWindow.getItem(point);    
                if(event.button == 3 && item != null && !item.equals(project)){
                    pep.getAutomatonMenu(item).setVisible(true);
                } else if(event.button == 3 && item != null && item.equals(project)){
                    pep.getProjectMenu(item).setVisible(true);
                }
                break;
                
            case SWT.KeyDown:
                switch(event.keyCode){
                case SWT.F2:
                    rename(treeWindow.getSelection()[0]);
                    break;
                    
                case SWT.DEL:
                    delete();
                }
                break;
            }      
        } 
    }
        
    
    
    
    
   
}
