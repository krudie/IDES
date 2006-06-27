package presentation.fsa;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

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
public class EdgeLabellingDialog extends JDialog implements Subscriber {
	
	private static EdgeLabellingDialog dialog;
	
	public static void initialize(JComponent parent, EventsModel eventsModel){
		Frame f = JOptionPane.getFrameForComponent(parent);
		dialog = new EdgeLabellingDialog(f, eventsModel);
	}
	
	/**
	 *
	 * @param comp parent component
	 * @param e the edge to be labelled
	 */
	public static void showDialog(JComponent comp, Edge e){
		if (dialog == null) {
          initialize(comp, null);
        } 
        dialog.setEdge(e);
        dialog.setLocationRelativeTo(comp);
        dialog.setVisible(true);        		
	}
	
	private EdgeLabellingDialog(){
		this(null, new EventsModel());		
	}		
	
	private EdgeLabellingDialog(Frame owner, EventsModel eventsModel){
		super(owner, "Assign and Edit Events", true);		
		this.eventsModel = eventsModel;
//		 NOT YET	eventsModel.attach(this);
		
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(100, 20));
		buttonCreate = new JButton("Create");
		buttonCreate.setEnabled(false);
		buttonDelete = new JButton("Delete");
		buttonDelete.setEnabled(false);
		checkObservable = new JCheckBox("Observable");
		checkObservable.setEnabled(false);
		checkControllable = new JCheckBox("Controllable");
		checkControllable.setEnabled(false);
		
		Container c = getContentPane();
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JPanel p = new JPanel(); //new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder("Enter event symbol"));
		p.add(textField, BorderLayout.WEST);
		JPanel p2 = new JPanel(new FlowLayout());
		p2.add(buttonCreate);
		p2.add(buttonDelete);
		p.add(p2, BorderLayout.CENTER);
		p.add(checkObservable, BorderLayout.EAST);
		p.add(checkControllable, BorderLayout.EAST);
	
		panel.add(p, BorderLayout.NORTH);		
						
		
		p = new JPanel();
		listAvailableEvents = new FilteringJList();
		listAvailableEvents.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		listAvailableEvents.installJTextField(textField);
		listAvailableEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {			 
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				int index = listAvailableEvents.getSelectedIndex();
				if (lsm.isSelectionEmpty()) {
					checkObservable.setEnabled(false);
					checkControllable.setEnabled(false);
					buttonCreate.setEnabled(true);
					buttonDelete.setEnabled(false);
				}else{				
					Object o = listAvailableEvents.getSelectedValue();
					if(o != null){
						selectedObject = o;
						textField.setText(selectedObject.toString());
						// FIXME selected index is being cleared.
						// FilteringListener (document listener) is firing a ContentsChanged event
						// Try a different kind of listener.
						// The following doesn't fix it.
						listAvailableEvents.setSelectedIndex(index);
						////////////////////////////////////////////
						checkObservable.setEnabled(true);
						checkControllable.setEnabled(true);
						buttonCreate.setEnabled(false);
						buttonDelete.setEnabled(true);
					}
				}
			}
			}
		);
		listAvailableEvents.setPreferredSize(new Dimension(150, 300));	
		JScrollPane pane = new JScrollPane(listAvailableEvents);
		pane.setBorder(BorderFactory.createTitledBorder("Available"));
		add(pane, BorderLayout.CENTER);		
		p.add(pane); //, BorderLayout.WEST);		
		
	    buttonAdd = new JButton(">>");
	    buttonAdd.addActionListener(new AddButtonListener());
		buttonRemove = new JButton("<<");		
		buttonRemove.addActionListener(new RemoveButtonListener());
		JPanel pCentre = new JPanel();
		BoxLayout boxLayout = new BoxLayout(pCentre, BoxLayout.Y_AXIS);
		pCentre.setLayout(boxLayout);
		pCentre.add(buttonAdd);
		pCentre.add(buttonRemove);		

		p.add(pCentre); //, BorderLayout.CENTER);
		listSelectedEvents = new MutableList();
		// TODO Only one item can be selected: change to MULTIPLE_INTERVAL_SELECTION later
		listSelectedEvents.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		listSelectedEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		listSelectedEvents.setPreferredSize(new Dimension(150, 300));		
		pane = new JScrollPane(listSelectedEvents);
		pane.setBorder(BorderFactory.createTitledBorder("Selected"));
		
		p.add(pane); //, BorderLayout.WEST);				
		
		panel.add(p, BorderLayout.CENTER);
		
		ActionListener commitListener = new CommitListener();
		buttonApply = new JButton("Apply");
		buttonApply.addActionListener(commitListener);
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
		listSelectedEvents.removeAll();
		Iterator<FSATransition> trans = edge.getTransitions();
		while(trans.hasNext()){
			FSATransition t = trans.next();
			//selectedEvents.add(t.getEvent());
			listSelectedEvents.addElement(t.getEvent());
		}
		
		if(!listSelectedEvents.getContents().isEmpty()){
			// TODO Set the current event to the first one in the selected list
			listSelectedEvents.setSelectedIndex(0);
		}

		// Available events are those in the active FSA minus those already selected		
		listAvailableEvents.removeAll();
		Iterator<FSAEvent> events = IDESWorkspace.instance().getActiveModel().getEventIterator();
		while(events.hasNext()){
			FSAEvent e = events.next();
			if(!listSelectedEvents.getContents().contains(e)){
				//availableEvents.add(e);
				listAvailableEvents.addElement(e);
			}
		}
	}

	public void setEdge(Edge edge){
		this.edge = edge;
		if(edge != null){
			update();
		}else{
			setDummyData();
		}
	}
	
	
	
	// Data
	private Edge edge;
	private Event newEvent;
	private Event selectedEvent;
	
	// DEBUG
	private Object selectedObject;
	
	// LATER /////////////////////////////////////////////////////////////
	private FSAEventsModel eventsModel; // the publisher to which i attach
	//////////////////////////////////////////////////////////////////////

	// GUI controls
	private JTextField textField;
	private JCheckBox checkObservable, checkControllable;	
	private MutableList listSelectedEvents;
	private FilteringJList listAvailableEvents;
	private JButton buttonCreate, buttonDelete, buttonAdd, buttonRemove, buttonOK, buttonApply, buttonCancel;
		
		
	@SuppressWarnings("serial") 
	private class MutableList extends JList {
	    MutableList() {
	    	super(new DefaultListModel());
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
	    
	    void addElement(Object o){
	    	getContents().addElement(o);
	    }
	    
	    void removeElement(Object o){
	    	getContents().removeElement(o);
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
				listSelectedEvents.addElement(selected);
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
			Object selected = listSelectedEvents.getSelectedValue();
			if(selected != null){
				listSelectedEvents.removeElement(selected);
				listAvailableEvents.addElement(selected);
			}	
		}
	}

	private class CommitListener implements ActionListener{

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			// TODO Apply any changes to edge's events
			// Ask GraphModel class to do this
			
			if(arg0.getSource().equals(buttonOK)){
				dialog.setVisible(false);
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
	
//	private class SelectionListener implements ListSelectionListener {
//
//		/* (non-Javadoc)
//		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
//		 */
//		public void valueChanged(ListSelectionEvent e) {
//			// TODO 
//			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
//
//			int i = lsm.getMinSelectionIndex();
			
			
			
//			StringBuffer output = new StringBuffer();
//	        int firstIndex = e.getFirstIndex();
//	        int lastIndex = e.getLastIndex();
//	        boolean isAdjusting = e.getValueIsAdjusting();
//	        output.append("Event for indexes "
//	                      + firstIndex + " - " + lastIndex
//	                      + "; isAdjusting is " + isAdjusting
//	                      + "; selected indexes:");
//
//	        if (lsm.isSelectionEmpty()) {
//	            output.append(" <none>");
//	        } else {
//	            // Find out which indexes are selected.
//	            int minIndex = lsm.getMinSelectionIndex();
//	            int maxIndex = lsm.getMaxSelectionIndex();
//	            for (int i = minIndex; i <= maxIndex; i++) {
//	                if (lsm.isSelectedIndex(i)) {
//	                    output.append(" " + i);
//	                }
//	            }
//	        }
//	        output.append("\n");
//	        System.out.println(output.toString());
//		}	
//		
//	}
	
	private void setDummyData(){
//		 DEBUG ////////////////////////////////////////////
		String[] data = {"Alpha", "Beta", "Gamma", "Delta"};
		String elements[] = {
	             "Partridge in a pear tree",
	             "Turtle Doves",
	             "French Hens",
	             "Calling Birds",
	             "Golden Rings",
	             "Geese-a-laying",
	             "Swans-a-swimming",
	             "Maids-a-milking",
	             "Ladies dancing",
	             "Lords-a-leaping",
	             "Pipers piping",
	             "Drummers drumming",
	             "Dasher",
	             "Dancer",
	             "Prancer",
	             "Vixen",
	             "Comet",
	             "Cupid",
	             "Donner",
	             "Blitzen",
	             "Rudolf",
	             "Bakerloo",
	             "Center",
	             "Circle",
	             "District",
	             "East London",
	             "Hammersmith and City",
	             "Jubilee",
	             "Metropolitan",
	             "Northern",
	             "Piccadilly Royal",
	             "Victoria",
	             "Waterloo and City",
	             "Alpha",
	             "Beta",
	             "Gamma",
	             "Delta",
	             "Epsilon",
	             "Zeta",
	             "Eta",
	             "Theta",
	             "Iota",
	             "Kapa",
	             "Lamda",
	             "Mu",
	             "Nu",
	             "Xi",
	             "Omikron",
	             "Pi",
	             "Rho",
	             "Sigma",
	             "Tau",
	             "Upsilon",
	             "Phi",
	             "Chi",
	             "Psi",
	             "Omega"
	           };   
	   
	           for (String element: elements) {
	             listAvailableEvents.addElement(element);
	           }	           
	        
	   		listSelectedEvents = new MutableList(data);
	   		
		/////////////////////////////////////////////////////
	}
}
