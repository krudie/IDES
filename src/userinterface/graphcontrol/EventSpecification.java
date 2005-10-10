/*
 * Created on Sep 28, 2004
 */
package userinterface.graphcontrol;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;



/**
 * This class handles the creation and management of everything inside the Event
 * Specification tab. Specifically it handles the managment of all Event
 * data, their names, descriptions, controllability, etc. In the GraphModel,
 * individual Edges maintin pointers to various entries in this class.
 * 
 * @author Kristian Edlund
 */
public class EventSpecification {

    /**
     * 
     */
    private TableColumn[] tableColumn = null;
    
    private String[] columnName = new String[]{"Name", "Description", "Controllable", "Observable"};
    
    private Table table;
    
    
    /**
     * Constructor for the TransitionData object
     * @param gp A link to the graphing platform
     * @param parent The parent, which the panel should be atteched to
     */
    public EventSpecification(Composite parent){
   
        table = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        
        
        GridData gd_table = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
        table.setLayoutData(gd_table);
        
        tableColumn = new TableColumn[columnName.length+1];
        for(int i= 0; i<columnName.length; i++){
            tableColumn[i] = new TableColumn(table,SWT.CENTER);
            tableColumn[i].setText(columnName[i]);
            tableColumn[i].setResizable(true);
            tableColumn[i].pack();
            
        }
        tableColumn[tableColumn.length-1] = new TableColumn(table, SWT.NONE);
        tableColumn[tableColumn.length-1].pack();
        
       
        createNewRow();
    }
    
    public void createNewRow(){
        TableItem empty = new TableItem(table, SWT.NONE);
        
        //The symbol row editor
        
        TableEditor editor = new TableEditor (table);
        Text text = new Text (table, SWT.BORDER);
        editor.grabHorizontal = true;
        editor.setEditor(text, empty, 0);
        editor = new TableEditor (table);
        
        
        //The description editor
        editor = new TableEditor (table);
        text = new Text (table, SWT.BORDER);
        editor.grabHorizontal = true;
        editor.setEditor(text, empty, 1);
        editor = new TableEditor (table);
        
        //Controlability
        Button button = new Button (table, SWT.CHECK );
        button.pack ();
        editor.minimumWidth = button.getSize ().x;
        editor.horizontalAlignment = SWT.CENTER;
        editor.setEditor (button, empty, 2);
        editor = new TableEditor (table);
        
        //observability
        button = new Button (table, SWT.CHECK);
        button.pack ();
        editor.minimumWidth = button.getSize ().x;
        editor.horizontalAlignment = SWT.CENTER;
        editor.setEditor(button, empty, 3);
    }
    
    
}