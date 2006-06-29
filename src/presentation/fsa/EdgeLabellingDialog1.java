package presentation.fsa;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import main.Hub;
import main.IDESWorkspace;
import model.Subscriber;
import model.fsa.FSAEvent;
import model.fsa.FSAEventsModel;
import model.fsa.FSATransition;
import model.fsa.ver1.Event;
import model.fsa.ver1.EventsModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ui.FilteringJList;
/**
 * Dialog window for assigning multiple events from the global events model
 * to transitions represented by an edge in the graph model. 
 * 
 * @author helen bretzke
 *
 */
public class EdgeLabellingDialog1 extends JDialog implements Subscriber {
	
	private static EdgeLabellingDialog1 dialog;
	
	public static void initialize(JComponent parent, EventsModel eventsModel){
		Frame f = JOptionPane.getFrameForComponent(parent);
		dialog = new EdgeLabellingDialog1(f, eventsModel);
	}
	
	/**
	 *
	 * @param view parent component
	 * @param e the edge to be labelled
	 */
	public static void showDialog(JComponent view, Edge e){
		if (dialog == null) {
          initialize(view, null);
        } 
        dialog.setEdge(e);
        dialog.setLocationRelativeTo(view);  
        dialog.setVisible(true);        	
	}
	
	private EdgeLabellingDialog1(){
		this(null, new EventsModel());		
	}		
	
	private EdgeLabellingDialog1(Frame owner, EventsModel eventsModel){
		super(owner, "Assign and Edit Events", true);		
		this.eventsModel = eventsModel;
//		 NOT YET	eventsModel.attach(this);
		
		textField = new JTextField(20);
		textField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				String symbol = textField.getText();
				
				// Select the first event in the lists for which symbol
				// is a prefix
				int i = listAvailableEvents.indexOfFirstElementWithPrefix(symbol);
				if(i > -1){
					listAvailableEvents.setSelectedIndex(i);
				}else{
					listAvailableEvents.clearSelection();
				}
				
				i = listAssignedEvents.indexOfFirstElementWithPrefix(symbol);
				if(i > -1){
					listAssignedEvents.setSelectedIndex(i);
				}else{
					listAssignedEvents.clearSelection();
				}
				if(listAvailableEvents.getSelectedIndex() == -1 && listAssignedEvents.getSelectedIndex() == -1){
					buttonCreate.setEnabled(true);
					checkControllable.setEnabled(true);
					checkControllable.setSelected(false); // not working
					checkObservable.setEnabled(true);
					checkControllable.setSelected(false); // not working
				}else{
					buttonCreate.setEnabled(false);
					checkControllable.setEnabled(false);
					checkObservable.setEnabled(false);
				}
			}			
		});
		
		
		buttonCreate = new JButton(Hub.string("create"));
		buttonCreate.setToolTipText(Hub.string("createEventTooltip"));
		buttonCreate.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				// get the symbol from the text field
				String symbol = textField.getText();
				if( ! symbol.equals("") ){
					// ask the graph model to make a new event
					newEvent = IDESWorkspace.instance().getActiveGraphModel().createEvent(symbol, checkControllable.isSelected(), checkObservable.isSelected());
					update();
					listAvailableEvents.setSelectedValue(newEvent, true);
					buttonCreate.setEnabled(false);
				}
			}
			
		});
		buttonCreate.setEnabled(false);		
		
		checkObservable = new JCheckBox(Hub.string("observable"));
		checkObservable.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {			
				IDESWorkspace.instance().getActiveGraphModel().setObservable(selectedEvent, checkObservable.isSelected());
			}
			
		});		
		checkObservable.setEnabled(false);
		
		checkControllable = new JCheckBox(Hub.string("controllable"));
		checkControllable.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				IDESWorkspace.instance().getActiveGraphModel().setControllable(selectedEvent, checkControllable.isSelected());				
			}
			
		});
		checkControllable.setEnabled(false);
		
		Container c = getContentPane();
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JPanel p = new JPanel(); //new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder("Enter event symbol"));
		p.add(textField, BorderLayout.WEST);
		JPanel p2 = new JPanel(new FlowLayout());		
		p2.add(buttonCreate);		
		p.add(p2, BorderLayout.CENTER);
		p.add(checkObservable, BorderLayout.EAST);
		p.add(checkControllable, BorderLayout.EAST);
	
		panel.add(p, BorderLayout.NORTH);						
		
		p = new JPanel();
		listAvailableEvents = new MutableList(); //new FilteringJList();
		listAvailableEvents.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		//listAvailableEvents.installJTextField(textField);
		listAvailableEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {			 
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();				
				if (lsm.isSelectionEmpty()) {
					
					// TODO see if contents of textField matches any of the events in the assigned list
					// NOTE wouldn't need to do this if we used a sorted list instead of a filtered one...
					checkObservable.setEnabled(false);
					checkControllable.setEnabled(false);
					buttonCreate.setEnabled(true);
					
				}else{				
					Object o = listAvailableEvents.getSelectedValue();
					if(o != null){
						selectedEvent = (Event)o;
						textField.setText(selectedEvent.getSymbol());						
						listAvailableEvents.setSelectedValue(o, true);		
						checkObservable.setEnabled(true);
						checkObservable.setSelected(selectedEvent.isObservable());
						checkControllable.setEnabled(true);
						checkControllable.setSelected(selectedEvent.isControllable());
						buttonCreate.setEnabled(false);
						
					}
				}
			}
			}
		);
		listAvailableEvents.setPreferredSize(new Dimension(150, 300));	
		JScrollPane pane = new JScrollPane(listAvailableEvents);
		pane.setBorder(BorderFactory.createTitledBorder("Available"));
		add(pane, BorderLayout.CENTER);		
		p.add(pane);		
		
	    buttonAdd = new JButton(">>");
	    buttonAdd.setToolTipText("Assign events to edge");
	    buttonAdd.addActionListener(new AddButtonListener());
		buttonRemove = new JButton("<<");		
		buttonRemove.setToolTipText("Remove events from edge");
		buttonRemove.addActionListener(new RemoveButtonListener());
		JPanel pCentre = new JPanel();
		BoxLayout boxLayout = new BoxLayout(pCentre, BoxLayout.Y_AXIS);
		pCentre.setLayout(boxLayout);
		pCentre.add(buttonAdd);
		pCentre.add(buttonRemove);		

		p.add(pCentre);
		listAssignedEvents = new MutableList();
		// TODO Only one item can be selected: change to MULTIPLE_INTERVAL_SELECTION later
		listAssignedEvents.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		listAssignedEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();				
				if (lsm.isSelectionEmpty()) {
					checkObservable.setEnabled(false);
					checkControllable.setEnabled(false);
					buttonCreate.setEnabled(true);
					
				}else{				
					Object o = listAssignedEvents.getSelectedValue();
					if(o != null){
						selectedEvent = (Event)o;
						textField.setText(selectedEvent.getSymbol());						
						listAssignedEvents.setSelectedValue(o, true);		
						checkObservable.setEnabled(true);
						checkObservable.setSelected(selectedEvent.isObservable());
						checkControllable.setEnabled(true);
						checkControllable.setSelected(selectedEvent.isControllable());
						buttonCreate.setEnabled(false);
						
					}
				}
			}
			
		});
		listAssignedEvents.setPreferredSize(new Dimension(150, 300));		
		pane = new JScrollPane(listAssignedEvents);
		pane.setBorder(BorderFactory.createTitledBorder("Assigned to Edge"));
		
		p.add(pane);				
		
		panel.add(p, BorderLayout.CENTER);
		
		ActionListener commitListener = new CommitListener();		
		buttonOK = new JButton("OK");
		buttonOK.addActionListener(commitListener);
		buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae){
					dialog.setVisible(false);
				}
			}
		);
		
		p = new JPanel(new FlowLayout());
		p.add(buttonOK);		
		p.add(buttonCancel);
		panel.add(p, BorderLayout.SOUTH);
		c.add(panel);
		pack();
	}
	
	public void update() {
		// TODO refresh mutable list models with data from eventsModel
		// For now just refresh list models with local event sets from the active FSA
		
		// Selected events are those assigned to transitions on the edge
		listAssignedEvents.removeAll();
		Iterator<FSATransition> trans = edge.getTransitions();
		while(trans.hasNext()){
			FSATransition t = trans.next();
			if(t.getEvent() != null){
				listAssignedEvents.addElement(t.getEvent());
			}
		}
		
		// Available events are those in the active FSA minus those already selected		
		listAvailableEvents.removeAll();
		Iterator<FSAEvent> events = IDESWorkspace.instance().getActiveModel().getEventIterator();

		while(events.hasNext()){
			FSAEvent e = events.next();
			if(!listAssignedEvents.getContents().contains(e)){				
				listAvailableEvents.insertElement((Comparable)e);
			}
		}		
		textField.setText("");
	}

	public void setEdge(Edge edge){
		this.edge = edge;
		if(edge != null){
			update();
		}else{
			textField.setText("");
			// TODO clear out lists
		}
	}	
	
	
	// Data
	private Edge edge;
	private Event newEvent;		
	private Event selectedEvent;
	
	
	// LATER /////////////////////////////////////////////////////////////
	private FSAEventsModel eventsModel; // the publisher to which i attach
	//////////////////////////////////////////////////////////////////////

	// GUI controls
	private JTextField textField;
	private JCheckBox checkObservable, checkControllable;	
	private MutableList listAssignedEvents;
	private MutableList listAvailableEvents;
	private JButton buttonCreate, buttonAdd, buttonRemove, buttonOK, buttonCancel;
		
		
	@SuppressWarnings("serial") 
	private class MutableList extends JList {
	    MutableList() {
	    	super(new DefaultListModel());
	    }
	    
	    /**
		 * @param symbol
		 * @return index of the first element found with prefix of string representation matching symbol, -1 if no such element
		 */
		public int indexOfFirstElementWithPrefix(String prefix) {
			DefaultListModel model = getContents();
			for(int i = 0; i < model.getSize(); i++){
				if(model.getElementAt(i).toString().startsWith(prefix)){
					return i;
				}
			}
			return -1;
		}

		MutableList(Object[] elements){
	    	this();
	    	DefaultListModel model = getContents();
	    	for(Object element: elements){
	    		model.addElement(element);
	    	}
	    }
	    
	    DefaultListModel getContents() {
	    	@SuppressWarnings("unused") ListModel m = getModel();
	    	return (DefaultListModel)getModel();
	    }
	    
	    void insertElement(Comparable o){
	    	int i = findInsertionPoint(o);
	    	getContents().insertElementAt(o, i);
	    }
	    
	    void addElement(Object o){
	    	getContents().addElement(o);
	    }
	    
	    void removeElement(Object o){
	    	getContents().removeElement(o);
	    }
	    
	    public void removeAll(){
	    	getContents().removeAllElements();
	    }
	    /**
	     * Internal helper method to find the insertion point for a new 
	     * entry in a sorted (ascending) model.
	     */
	    private int findInsertionPoint(Comparable entry) {
	        int insertionPoint = getContents().getSize();
	        for(int i=0; i<insertionPoint; i++){
	        	int c = ((Comparable)getContents().elementAt(i)).compareTo(entry);
	        	if(c >= 0){
	        		return i;
	        	}
	        }	        
	        return insertionPoint;
	    }
	    
	}   
	
	/**
	 * Responds to clicks on add button.
	 * 
	 * @author Squirrel
	 *
	 */
	private class AddButtonListener implements ActionListener{

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {		 
			// get elements selected from available events list
			Object selected = listAvailableEvents.getSelectedValue();
			if(selected != null){
				listAssignedEvents.addElement((Comparable)selected);
				listAvailableEvents.removeElement(selected);
			}	
		}		
	}

	/**
	 * Responds to clicks on remove button.
	 * 
	 * @author Squirrel
	 *
	 */
	private class RemoveButtonListener implements ActionListener{

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			Object selected = listAssignedEvents.getSelectedValue();
			if(selected != null){
				listAssignedEvents.removeElement(selected);
				listAvailableEvents.insertElement((Comparable)selected);
			}	
		}
	}

	private class CommitListener implements ActionListener{

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			// Apply any changes to edge's events
			Event[] events = new Event[listAssignedEvents.getContents().size()];
			Object[] contents = listAssignedEvents.getContents().toArray();
			for(int i = 0; i < contents.length; i++){
				events[i] = (Event)contents[i];
			}				
			IDESWorkspace.instance().getActiveGraphModel().assignEvents(events, edge);
			
			if(arg0.getSource().equals(buttonOK)){
				dialog.setVisible(false);
				if(dialog.getParent() != null){
					dialog.getParent().repaint();
				}
			}
		}
		
	}
	
	private class DeleteListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			// TODO remove the currently selected event from the FSA 
			// (must remove it from all transitions in the FSA). 
			Hub.displayAlert("TODO Implement delete event.");			
		}
		
	}
	
}
