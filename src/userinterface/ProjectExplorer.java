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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author edlund
 *
 */
public class ProjectExplorer {

	Tree treeWindow;
    TreeItem  lastItem = null;
    TreeEditor editor;
    
    final Color black = Display.getDefault().getSystemColor (SWT.COLOR_BLACK);
    
    
    TreeItem project = null;
    TreeItem automaton[] = null;
    

	
	public ProjectExplorer(Composite parent){
		treeWindow = new Tree(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        editor = new TreeEditor(treeWindow);
        
        treeWindow.addListener (SWT.Selection, new Listener () {
            public void handleEvent (Event event) {
                final TreeItem item = (TreeItem) event.item;
                if (item != null && item == lastItem) {
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
                   
                    Listener textListener = new Listener () {
                        public void handleEvent (final Event e) {
                            switch (e.type) {
                                case SWT.FocusOut:
                                    setName();
                                 
                                    composite.dispose ();
                                    
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
                                            setName();
                                            //FALL THROUGH
                                        case SWT.TRAVERSE_ESCAPE:
                                            composite.dispose ();
                                            e.doit = false;
                                    }
                                    break;
                            }
                        }
                        
                        private void setName(){
                            if(item.equals(project)){
                                Userinterface.getProjectPresentation().setProjectName(text.getText());
                            } else{
                                 //todo: Set the new name of the automaton                               
                            }
                            
                            updateProject();                      
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
                lastItem = item;
            }
        });
        
	}
	
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
            automaton = new TreeItem[automataNames.length];
        
            for(int i = 0; i < automataNames.length; i++){
                automaton[i] = new TreeItem(project, SWT.NONE);
                if(automataNames[i] != null){
                    automaton[i].setText(automataNames[i]);
                } else{
                    automaton[i].setText(ResourceManager.getString("automaton_untitled"));
                }
                
                automaton[i].setImage(ResourceManager.getHotImage(ResourceManager.FILE_NEW_AUTOMATON));
            }
        }
        project.setExpanded(true);
        
    }
    
}
