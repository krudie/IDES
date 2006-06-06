package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.Subscriber;
import model.fsa.FSAEventsModel;
import model.fsa.ver1.Event;
import model.fsa.ver1.EventsModel;
import presentation.fsa.Edge;

/**
 * Dialog window for assigning multiple events from the global events model
 * to transitions represented by an edge in the graph model. 
 * 
 * @author helen bretzke
 *
 */
public class EdgeLabellingDialog extends JDialog implements Subscriber {
	
	public EdgeLabellingDialog(){
		this(null, new EventsModel());
		
	}
	
	public EdgeLabellingDialog(Frame owner, EventsModel eventsModel){
		super(owner, "Edge Labelling Dialog", true);
		this.eventsModel = eventsModel;
		eventsModel.attach(this);
		
		// TODO add listeners
		textArea = new JTextArea("Enter event name here.");		
		textArea.setBorder(BorderFactory.createEtchedBorder());
				
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
		p.add(textArea, BorderLayout.WEST);
		p.add(buttonCreate, BorderLayout.CENTER);
		p.add(checkObservable, BorderLayout.EAST);
		p.add(checkControllable, BorderLayout.EAST);
	
		panel.add(p, BorderLayout.NORTH);
		
		p = new JPanel(); //new BorderLayout());
		
		// DEBUG
		String[] data = {"alpha", "beta", "gamma", "delta"};
		listGlobalEvents = new MutableList(data);		
		listGlobalEvents.setPreferredSize(new Dimension(150, 300));		
		listGlobalEvents.setSelectionModel(new ToggleSelectionModel());
		JScrollPane scrollPane = new JScrollPane(listGlobalEvents);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Available"));

		p.add(scrollPane); //, BorderLayout.WEST);		

		buttonAdd = new JButton(">>");
		buttonRemove = new JButton("<<");		
		JPanel pCentre = new JPanel();
		BoxLayout boxLayout = new BoxLayout(pCentre, BoxLayout.Y_AXIS);
		pCentre.setLayout(boxLayout);
		pCentre.add(buttonAdd);
		pCentre.add(buttonRemove);		

		p.add(pCentre); //, BorderLayout.CENTER);

		listSelectedEvents = new MutableList(data);
		listSelectedEvents.setPreferredSize(new Dimension(150, 300));
		listSelectedEvents.setSelectionModel(new ToggleSelectionModel());
		scrollPane = new JScrollPane(listSelectedEvents);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Selected"));
		
		p.add(scrollPane); //, BorderLayout.WEST);				
		
		panel.add(p, BorderLayout.CENTER);
		
		buttonOK = new JButton("OK");
		buttonCancel = new JButton("Cancel");
		p = new JPanel(new FlowLayout());
		p.add(buttonOK);
		p.add(buttonCancel);
		panel.add(p, BorderLayout.SOUTH);
		c.add(panel);
		pack();
	}
	
	public void update() {
		// TODO refresh mutable list models with data from eventsModel
		
	}

	public void setEdge(Edge edge){
		this.edge = edge;
	}
	
	// Data
//	private GraphModel graphModel;
//	private GraphDrawingView context;
	private Edge edge;
	private Event[] globalEvents;
	private Event[] selectedEvents;
	private Event newEvent;	
	private FSAEventsModel eventsModel; // the publisher to which i attach 

	// GUI controls
	private JTextArea textArea;
	private JCheckBox checkObservable, checkControllable;	
	private JList listGlobalEvents, listSelectedEvents;
	private JButton buttonCreate, buttonAdd, buttonRemove, buttonOK, buttonCancel;
	
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
	    	return (DefaultListModel)getModel();
	    }
	    
	    void addElement(Object o){
	    	getContents().addElement(o);
	    }
	    
	    void removeElement(Object o){
	    	getContents().removeElement(o);
	    }
	}   
	
}
