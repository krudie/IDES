/*
 * Created on Sep 28, 2004
 */
package userinterface.graphcontrol;

import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import userinterface.ResourceManager;



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
    
    private Button newRow = null,
                   deleteRow = null;
    
    private boolean changed;
    
    public final static int NAME = 0,
                            DESCRIPTION = 1,
                            CONTROLLABLE = 2,
                            OBSERVABLE = 3;
                            
                 
    
    
    
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
        
        
        newRow = new Button(parent, SWT.PUSH);
        newRow.setText(ResourceManager.getString("eventSpec_newRow"));
        newRow.addListener (SWT.Selection, new Listener(){
            public void handleEvent(Event arg0){
                createNewEvent();
            }
        });
        
        
        deleteRow = new Button(parent, SWT.PUSH);
        deleteRow.setText(ResourceManager.getString("eventSpec_deleteRow"));
        deleteRow.addListener (SWT.Selection, new Listener(){
            public void handleEvent(Event arg0){
                deleteRow();
            }
        });
        
        
        final TableEditor editor = new TableEditor (table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        table.addListener (SWT.MouseDown, new Listener () {
            public void handleEvent (Event event) {
                Rectangle clientArea = table.getClientArea ();
                Point pt = new Point (event.x, event.y);
                int index = table.getTopIndex ();
                while (index < table.getItemCount ()) {
                    boolean visible = false;
                    final TableItem item = table.getItem (index);
                    for (int i=0; i<table.getColumnCount (); i++) {
                        Rectangle rect = item.getBounds (i);
                        if (rect.contains (pt)) {
                            final int column = i;
                            if(column > 1){
                                item.setText(column, Boolean.toString(item.getText(column).equals("false")));
                                return;
                            }
                            final Text text = new Text (table, SWT.NONE);
                            
                            Listener textListener = new Listener () {
                                public void handleEvent (final Event e) {
                                    switch (e.type) {
                                        case SWT.FocusOut:
                                            item.setText (column, text.getText ());
                                            changed = true;
                                            text.dispose ();
                                            break;
                                        case SWT.Traverse:
                                            switch (e.detail) {
                                                case SWT.TRAVERSE_RETURN:
                                                    item.setText (column, text.getText ());
                                                    changed = true;
                                                    //FALL THROUGH
                                                case SWT.TRAVERSE_ESCAPE:
                                                    text.dispose ();
                                                    e.doit = false;
                                            }
                                            break;
                                    }
                                }
                            };
                            text.addListener (SWT.FocusOut, textListener);
                            text.addListener (SWT.Traverse, textListener);
                            editor.setEditor (text, item, i);
                            text.setText (item.getText (i));
                            text.selectAll ();
                            text.setFocus ();
                            return;
                        }
                        if (!visible && rect.intersects (clientArea)) {
                            visible = true;
                        }
                    }
                    if (!visible) return;
                    index++;
                } 
        
            }
        });
    }
    
    public int createNewEvent(){
        TableItem ti = new TableItem(table, SWT.NONE);      
        table.setSelection(table.getItemCount()-1);
        
        for(int i = 2; i < columnName.length;i++){
            ti.setText(i, "false");
        }      
        
        changed = true;
                
        return table.getItemCount()-1; 
    }
    
    public int createNewEvent(String name, String description, boolean controllable, boolean observable){
        int retVal = createNewEvent();
        setName(retVal, name);
        setDescription(retVal, description);
        setControllable(retVal, controllable);
        setObservable(retVal, observable);
        return retVal;
    }
    
    public void deleteRow(){
        if(table.getSelectionIndex() != -1){
            int selected = table.getSelectionIndex();
            table.remove(table.getSelectionIndices());
            
            table.setSelection((selected < table.getItemCount()) ? selected : table.getItemCount()-1);
            table.redraw();
            changed = true;
        }
    }
    
   
    public TableItem[] getEventLabels(){
        
        LinkedList<TableItem> retVal = new LinkedList<TableItem>();
        
        for(int i = 0; i < table.getItemCount(); i++){
            if(!table.getItem(i).getText(EventSpecification.NAME).trim().equals("")){
                retVal.add(table.getItem(i));
            }
        }
        return retVal.toArray(new TableItem[retVal.size()]);
       
    }
    
    public boolean isChanged(){
        return changed;
    }
    
    public void setChanged(boolean state){
        changed = state;
    }
    
    
    public String getName(int id){        
        try{
            return table.getItem(id).getText(NAME);
        } catch(Exception e){
            return null;
        }
    }
    
    public void setName(int id, String name){        
        try{
            table.getItem(id).setText(NAME, name);
        } catch(Exception e){}
    }
    
    public String getDescription(int id){       
       try{
           return table.getItem(id).getText(DESCRIPTION);
       } catch(Exception e){
           return null;
       }
    }
    
    public void setDescription(int id, String description){        
        try{    
            table.getItem(id).setText(DESCRIPTION, description);
        } catch(Exception e){}
    }  
    
    public boolean getObservable(int id){
        try{
            return table.getItem(id).getText(OBSERVABLE).equals("true");
        } catch(Exception e){
            return false;
        }
    }
    
    public void setObservable(int id, boolean observable){
        try{
            table.getItem(id).setText(OBSERVABLE, Boolean.toString(observable));
        } catch(Exception e){}
    }
    
    public boolean getControllable(int id){
        try{
            return table.getItem(id).getText(CONTROLLABLE).equals("true");
        } catch(Exception e){
            return false;
        }
    }
    
    public void setControllable(int id, boolean controllable){
        try{
            table.getItem(id).setText(CONTROLLABLE, Boolean.toString(controllable));
        } catch(Exception e){}
    }
    
    public int getId(String name){
        for(int i = 0; i< table.getItemCount(); i++){
            if(table.getItem(i).getText(NAME).equals(name)) return i;
        }
        return -1;
    }
    
    public int getEventCount(){
        return table.getItemCount();
    }
    
    
}