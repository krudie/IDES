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

import main.IDESWorkspace;
import model.Subscriber;
import model.fsa.FSAEvent;
import model.fsa.FSAEventsModel;
import model.fsa.FSATransition;
import model.fsa.ver1.Event;
import model.fsa.ver1.EventsModel;
import javax.swing.JOptionPane;

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
        } else {
        	dialog.setEdge(e);
            dialog.setLocationRelativeTo(comp);
            dialog.setVisible(true);
        }		
	}
	
	public EdgeLabellingDialog(){
		this(null, new EventsModel());		
	}		
	
	public EdgeLabellingDialog(Frame owner, EventsModel eventsModel){
		super(owner, "Assign Events to Edge", true);		
		this.eventsModel = eventsModel;
//		 NOT YET	eventsModel.attach(this);
//		selectedEvents = new ArrayList<FSAEvent>();
//		availableEvents = new ArrayList<FSAEvent>();	
		
		text = new JTextField();
		text.setPreferredSize(new Dimension(200, 20));
		buttonCreate = new JButton("Create");
		buttonCreate.setEnabled(false);
		checkObservable = new JCheckBox("Observable");
		checkObservable.setEnabled(false);
		checkControllable = new JCheckBox("Controllable");
		checkControllable.setEnabled(false);
		
		Container c = getContentPane();
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JPanel p = new JPanel(); //new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder("Enter event symbol"));
		p.add(text, BorderLayout.WEST);
		p.add(buttonCreate, BorderLayout.CENTER);
		p.add(checkObservable, BorderLayout.EAST);
		p.add(checkControllable, BorderLayout.EAST);
	
		panel.add(p, BorderLayout.NORTH);		
						
		
		p = new JPanel(); //new BorderLayout());
		listAvailableEvents = new FilteringJList();
		JScrollPane pane = new JScrollPane(listAvailableEvents);
		add(pane, BorderLayout.CENTER);		
		listAvailableEvents.installJTextField(text);		
		listAvailableEvents.setPreferredSize(new Dimension(150, 300));		
		listAvailableEvents.setSelectionModel(new ToggleSelectionModel());
		pane.setBorder(BorderFactory.createTitledBorder("Available"));
		p.add(pane); //, BorderLayout.WEST);		

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
		/////////////////////////////////////////////////////
		
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

		// DEBUG ////////////////////////////////////////////
		listSelectedEvents = new MutableList(data);
		// DEBUG ////////////////////////////////////////////
		
		listSelectedEvents.setPreferredSize(new Dimension(150, 300));
		listSelectedEvents.setSelectionModel(new ToggleSelectionModel());
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
		//selectedEvents.clear();
		listSelectedEvents.removeAll();
		Iterator<FSATransition> trans = edge.getTransitions();
		while(trans.hasNext()){
			FSATransition t = trans.next();
			//selectedEvents.add(t.getEvent());
			listSelectedEvents.addElement(t.getEvent());
		}
		
		if(!listSelectedEvents.getContents().isEmpty()){
			// TODO Set the current event to the first one in the selected list
			
		}
		
		refreshAvailableEvents();
	}

	private void refreshAvailableEvents(){
//		 Available events are those in the active FSA minus those already selected
		//availableEvents.clear();
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
		update();
	}
	
	// Data
	private Edge edge;
//	private ArrayList<FSAEvent> availableEvents;
//	private ArrayList<FSAEvent> selectedEvents;
	private Event newEvent;
	
	// LATER /////////////////////////////////////////////////////////////
	private FSAEventsModel eventsModel; // the publisher to which i attach
	//////////////////////////////////////////////////////////////////////

	// GUI controls
	private JTextField text;
	private JCheckBox checkObservable, checkControllable;	
	private MutableList listSelectedEvents;
	private FilteringJList listAvailableEvents;
	private JButton buttonCreate, buttonAdd, buttonRemove, buttonOK, buttonApply, buttonCancel;
	
	// private classes	
	private class ToggleSelectionModel extends DefaultListSelectionModel
	{
	    boolean gestureStarted = false;
	    
	    public void setSelectionInterval(int index0, int index1) {
		if (isSelectedIndex(index0) && !gestureStarted) {
		    super.removeSelectionInterval(index0, index1);
		}
		else {
		    super.setSelectionInterval(index0, index1);
		}
		gestureStarted = true;
	    }

	    public void setValueIsAdjusting(boolean isAdjusting) {
		if (isAdjusting == false) {
		    gestureStarted = false;
		}
	    }
	}
		
	@SuppressWarnings("serial") 
	private class MutableList extends JList {
	    MutableList() {
	    	super(new DefaultListModel());
	    }
	    
	    MutableList(Object[] data){
	    	super(data);
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
	
	
}
