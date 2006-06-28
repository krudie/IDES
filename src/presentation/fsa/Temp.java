/**
 * 
 */
package presentation.fsa;

/**
 * @author lenko
 *
 */
public class Temp {

	/**
	 * 
	 */
	public Temp() {
		super();
		// TODO Auto-generated constructor stub
	}
//	package presentation.fsa;
//
//	import java.awt.BorderLayout;
//	import java.awt.Component;
//	import java.awt.Container;
//	import java.awt.Dimension;
//	import java.awt.FlowLayout;
//	import java.awt.Frame;
//	import java.awt.event.ActionEvent;
//	import java.awt.event.ActionListener;
//	import java.awt.event.KeyEvent;
//	import java.util.ArrayList;
//	import java.util.Hashtable;
//	import java.util.Iterator;
//
//	import javax.swing.BorderFactory;
//	import javax.swing.Box;
//	import javax.swing.BoxLayout;
//	import javax.swing.DefaultListModel;
//	import javax.swing.DefaultListSelectionModel;
//	import javax.swing.JButton;
//	import javax.swing.JCheckBox;
//	import javax.swing.JComponent;
//	import javax.swing.JDialog;
//	import javax.swing.JList;
//	import javax.swing.JPanel;
//	import javax.swing.JScrollPane;
//	import javax.swing.JTextArea;
//	import javax.swing.JTextField;
//	import javax.swing.KeyStroke;
//	import javax.swing.ListModel;
//	import javax.swing.ListSelectionModel;
//
//	import main.Hub;
//	import main.IDESWorkspace;
//	import model.Subscriber;
//	import model.fsa.FSAEvent;
//	import model.fsa.FSAEventsModel;
//	import model.fsa.FSAModel;
//	import model.fsa.FSATransition;
//	import model.fsa.ver1.Event;
//	import model.fsa.ver1.EventsModel;
//	import javax.swing.JOptionPane;
//	import javax.swing.event.DocumentEvent;
//	import javax.swing.event.DocumentListener;
//	import javax.swing.event.ListSelectionEvent;
//	import javax.swing.event.ListSelectionListener;
//
//	import ui.FilteringJList;
//	import util.EscapeDialog;
//	/**
//	 * Dialog window for assigning multiple events from the global events model
//	 * to transitions represented by an edge in the graph model. 
//	 * 
//	 * @author helen bretzke
//	 *
//	 */
//	public class EdgeLabellingDialog extends EscapeDialog implements Subscriber {
//		
//		private static EdgeLabellingDialog dialog;
//		private static Hashtable<String,FSAEvent> events=new Hashtable<String,FSAEvent>();
//		
//		public static void initialize(JComponent parent, FSAModel a){
//			Frame f = JOptionPane.getFrameForComponent(parent);
//			dialog = new EdgeLabellingDialog(f, null);
//			events.clear();
//			for(Iterator<FSAEvent> i=Hub.getWorkspace().getActiveModel().getEventIterator();i.hasNext();)
//			{
//				FSAEvent event=i.next();
//				events.put(event.getSymbol(),event);
//			}
//		}
//
////		public static void initialize(JComponent parent, EventsModel eventsModel){
////			Frame f = JOptionPane.getFrameForComponent(parent);
////			dialog = new EdgeLabellingDialog(f, eventsModel);
////		}
//		
//		/**
//		 *
//		 * @param view parent component
//		 * @param e the edge to be labelled
//		 */
//		public static void showDialog(JComponent view, Edge e){
//			if (dialog == null) {
//	          initialize(view, null);
//	        } 
//	        dialog.setEdge(e);
//	        dialog.setLocationRelativeTo(view);  
//	        dialog.setVisible(true);        	
//		}
//		
//		private EdgeLabellingDialog(){
//			//this(null, new EventsModel());		
//		}		
//		
//		private EdgeLabellingDialog(Frame owner, FSAModel a){//EventsModel eventsModel){
//			super(owner, "Assign events to transition", true);		
//			this.eventsModel = eventsModel;
////			 NOT YET	eventsModel.attach(this);
//			
//			
//			Box mainBox=Box.createVerticalBox();
//			Box createBox=Box.createHorizontalBox();
//			
//			eventNameField=new JTextField();
//			eventNameField.setMaximumSize(new Dimension(eventNameField.getMaximumSize().width,
//					eventNameField.getPreferredSize().height));
//			DocumentListener al=new DocumentListener()
//			{
//				public void changedUpdate(DocumentEvent e)
//				{
//					configStuff(eventNameField.getText());
//				}
//				public void insertUpdate(DocumentEvent e)
//				{
//					configStuff(eventNameField.getText());
//				}
//				public void removeUpdate(DocumentEvent e)
//				{
//					configStuff(eventNameField.getText());
//				}
//				private void configStuff(String s)
//				{
//					//FSAEvent event=((EventTableModel)table.getModel()).closestEvent(s);
//					if(events.containsKey(s))
//					{
//						createButton.setEnabled(false);
//						checkControllable.setEnabled(false);
//						//controllableCBox.setSelected(event.isControllable());
//						checkObservable.setEnabled(false);
//						//observableCBox.setSelected(event.isObservable());
//					}
//					else
//					{
//						createButton.setEnabled(true);
//						checkControllable.setEnabled(true);
//						checkObservable.setEnabled(true);
//					}
//				}
//			};
//			eventNameField.getDocument().addDocumentListener(al);
//			eventNameField.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),this);
//			//eventNameField.getActionMap().put(this,createListener);
//			createBox.add(eventNameField);
//			
//			checkControllable=new JCheckBox(Hub.string("controllable"));
//			checkControllable.setSelected(true);
//			createBox.add(checkControllable);
//			checkObservable=new JCheckBox(Hub.string("observable"));
//			checkObservable.setSelected(true);
//			createBox.add(checkObservable);
//			
//			createButton=new JButton(Hub.string("add"));
//			createButton.setPreferredSize(new Dimension(createButton.getPreferredSize().width,
//					eventNameField.getPreferredSize().height));
//			//createButton.addActionListener(createListener);
//			createBox.add(createButton);
//			createBox.setBorder(BorderFactory.createTitledBorder(Hub.string("addNewEvent")));
//			mainBox.add(createBox);
//			mainBox.add(Box.createRigidArea(new Dimension(0,5)));
//			
////			textField = new JTextField(20);
////			textField.addActionListener(new ActionListener(){
//	//
////				public void actionPerformed(ActionEvent arg0) {
////					String symbol = textField.getText();
////					//if(symbol doesn't exist in the current FSA){
////						///	enable the create button
////				}
////				
////			});
////			
////			buttonClear = new JButton("Clear");
////			buttonClear.setToolTipText("Clear text and show all available events");
////			buttonClear.addActionListener(new ActionListener(){
//	//
////				public void actionPerformed(ActionEvent arg0) {
////					textField.setText("");
////				}
////				
////			});
////			buttonCreate = new JButton("Create");
////			buttonCreate.setToolTipText("Create a new event");
////			buttonCreate.addActionListener(new ActionListener(){
//	//
////				public void actionPerformed(ActionEvent arg0) {
////					// get the symbol from the text field
////					String symbol = textField.getText();
////					if( ! symbol.equals("") ){
////						// ask the graph model to make a new event
////						// IDESWorkspace.instance().getActiveGraphModel().createEvent(symbol);
////					}
////				}
////				
////			});
////			buttonCreate.setEnabled(false);
////			buttonDelete = new JButton("Delete");
////			buttonDelete.setToolTipText("Delete the selected event");
////			buttonDelete.setEnabled(false);
////			buttonDelete.addActionListener(new ActionListener(){
//	//
////				public void actionPerformed(ActionEvent arg0) {
////					Hub.displayAlert("EdgeLabellingDialog: Please implement delete event");
////				}
////				
////			});
////			
////			checkObservable = new JCheckBox("Observable");
////			checkObservable.addActionListener(new ActionListener(){
//	//
////				public void actionPerformed(ActionEvent arg0) {			
////					IDESWorkspace.instance().getActiveGraphModel().setObservable(selectedEvent, checkObservable.isSelected());
////				}
////				
////			});		
////			checkObservable.setEnabled(false);
////			
////			checkControllable = new JCheckBox("Controllable");
////			checkControllable.addActionListener(new ActionListener(){
//	//
////				public void actionPerformed(ActionEvent arg0) {
////					IDESWorkspace.instance().getActiveGraphModel().setControllable(selectedEvent, checkControllable.isSelected());				
////				}
////				
////			});
////			checkControllable.setEnabled(false);
//			
//			Container c = getContentPane();
//			
//			Box listBox=Box.createHorizontalBox();
//			
////			JPanel panel = new JPanel();
////			panel.setLayout(new BorderLayout());
//			
////			JPanel p = new JPanel(); //new BorderLayout());
////			p.setBorder(BorderFactory.createTitledBorder("Enter event symbol"));
////			p.add(textField, BorderLayout.WEST);
////			JPanel p2 = new JPanel(new FlowLayout());
////			p2.add(buttonClear);
////			p2.add(buttonCreate);
////			p2.add(buttonDelete);
////			p.add(p2, BorderLayout.CENTER);
////			p.add(checkObservable, BorderLayout.EAST);
////			p.add(checkControllable, BorderLayout.EAST);
////		
////			panel.add(p, BorderLayout.NORTH);						
//			
//			//p = new JPanel();
//			listAvailableEvents = new FilteringJList();
//			listAvailableEvents.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
//			listAvailableEvents.installJTextField(eventNameField);
//			listAvailableEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//				public void valueChanged(ListSelectionEvent e) {			 
//					ListSelectionModel lsm = (ListSelectionModel)e.getSource();				
//					if (lsm.isSelectionEmpty()) {
//						
//						// TODO see if contents of textField matches any of the events in the assigned list
//						// NOTE wouldn't need to do this if we used a sorted list instead of a filtered one...
//						checkObservable.setEnabled(false);
//						checkControllable.setEnabled(false);
//						createButton.setEnabled(true);
//						buttonDelete.setEnabled(false);
//					}else{				
//						Object o = listAvailableEvents.getSelectedValue();
//						if(o != null){
//							selectedEvent = (Event)o;
//							eventNameField.setText(selectedEvent.getSymbol());						
//							listAvailableEvents.setSelectedValue(o, true);		
//							checkObservable.setEnabled(true);
//							checkObservable.setSelected(selectedEvent.isObservable());
//							checkControllable.setEnabled(true);
//							checkControllable.setSelected(selectedEvent.isControllable());
//							createButton.setEnabled(false);
//							buttonDelete.setEnabled(true);
//						}
//					}
//				}
//				}
//			);
//			listAvailableEvents.setPreferredSize(new Dimension(150, 300));	
//			JScrollPane pane = new JScrollPane(listAvailableEvents);
//			pane.setBorder(BorderFactory.createTitledBorder("Available"));
//			listBox.add(pane);
//			mainBox.add(Box.createRigidArea(new Dimension(0,5)));
////			add(pane, BorderLayout.CENTER);		
////			p.add(pane);		
//			
//		    buttonAdd = new JButton(">>");
//		    buttonAdd.setToolTipText("Assign events to edge");
//		    buttonAdd.addActionListener(new AddButtonListener());
//			buttonRemove = new JButton("<<");		
//			buttonRemove.setToolTipText("Remove events from edge");
//			buttonRemove.addActionListener(new RemoveButtonListener());
//			JPanel pCentre = new JPanel();
//			BoxLayout boxLayout = new BoxLayout(pCentre, BoxLayout.Y_AXIS);
//			pCentre.setLayout(boxLayout);
//			pCentre.add(buttonAdd);
//			pCentre.add(buttonRemove);
//			listBox.add(pCentre);
//
//			//p.add(pCentre);
//			listAssignedEvents = new MutableList();
//			// TODO Only one item can be selected: change to MULTIPLE_INTERVAL_SELECTION later
//			listAssignedEvents.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
//			listAssignedEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
//
//				public void valueChanged(ListSelectionEvent e) {
//					ListSelectionModel lsm = (ListSelectionModel)e.getSource();				
//					if (lsm.isSelectionEmpty()) {
//						checkObservable.setEnabled(false);
//						checkControllable.setEnabled(false);
//						createButton.setEnabled(true);
//						buttonDelete.setEnabled(false);
//					}else{				
//						Object o = listAssignedEvents.getSelectedValue();
//						if(o != null){
//							selectedEvent = (Event)o;
//							eventNameField.setText(selectedEvent.getSymbol());						
//							listAssignedEvents.setSelectedValue(o, true);		
//							checkObservable.setEnabled(true);
//							checkObservable.setSelected(selectedEvent.isObservable());
//							checkControllable.setEnabled(true);
//							checkControllable.setSelected(selectedEvent.isControllable());
//							createButton.setEnabled(false);
//							buttonDelete.setEnabled(true);
//						}
//					}
//				}
//				
//			});
//			listAssignedEvents.setPreferredSize(new Dimension(150, 300));		
//			pane = new JScrollPane(listAssignedEvents);
//			pane.setBorder(BorderFactory.createTitledBorder("Assigned to Edge"));
//			listBox.add(pane);
//			
//			//p.add(pane);
//			
//			mainBox.add(listBox);
//			//panel.add(p, BorderLayout.CENTER);
//			
//			ActionListener commitListener = new CommitListener();
//			buttonApply = new JButton("Apply");
//			buttonApply.addActionListener(commitListener);
//			buttonOK = new JButton("OK");
//			buttonOK.addActionListener(commitListener);
//			buttonCancel = new JButton("Cancel");
//			buttonCancel.addActionListener(new ActionListener(){
//					public void actionPerformed(ActionEvent ae){
//						dialog.setVisible(false);
//					}
//				}
//			);
//			
//			JPanel buttonBox=new JPanel();
//
//			//Box buttonBox=Box.createHorizontalBox();
//			//buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
//			//p = new JPanel(new FlowLayout());
//			buttonBox.add(buttonOK);
//			//buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
//			buttonBox.add(buttonApply);
//			//buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
//			buttonBox.add(buttonCancel);
//			
//			//panel.add(p, BorderLayout.SOUTH);
//			mainBox.add(buttonBox);
//			mainBox.add(Box.createRigidArea(new Dimension(0,5)));
//			c.add(mainBox);
//			pack();
//			
//			buttonOK.setPreferredSize(new Dimension(
//					Math.max(buttonOK.getWidth(),buttonCancel.getWidth()),buttonOK.getHeight()));
//			buttonOK.invalidate();
//			buttonCancel.setPreferredSize(new Dimension(
//					Math.max(buttonOK.getWidth(),buttonCancel.getWidth()),buttonCancel.getHeight()));
//			buttonCancel.invalidate();
//
//			//invalidate();
//		}
//		
//		public void update() {
//			// TODO refresh mutable list models with data from eventsModel
//			// For now just refresh list models with local event sets from the active FSA
//			
//			// Selected events are those assigned to transitions on the edge
//			listAssignedEvents.removeAll();
//			Iterator<FSATransition> trans = edge.getTransitions();
//			while(trans.hasNext()){
//				FSATransition t = trans.next();
//				if(t.getEvent() != null){
//					listAssignedEvents.addElement(t.getEvent());
//				}
//			}
//			
//			// Available events are those in the active FSA minus those already selected		
//			listAvailableEvents.removeAll();
//			Iterator<FSAEvent> events = IDESWorkspace.instance().getActiveModel().getEventIterator();
//			while(events.hasNext()){
//				FSAEvent e = events.next();
//				if(!listAssignedEvents.getContents().contains(e)){				
//					listAvailableEvents.addElement(e);
//				}
//			}
//			
//			eventNameField.setText("");
//		}
//
//		public void setEdge(Edge edge){
//			this.edge = edge;
//			if(edge != null){
//				update();
//			}else{
//				eventNameField.setText("");
//				// TODO clear out lists
//			}
//		}	
//		
//		
//		// Data
//		private Edge edge;
//		private Event newEvent;
//			
//		private Event selectedEvent;
//		
//		
//		// LATER /////////////////////////////////////////////////////////////
//		private FSAEventsModel eventsModel; // the publisher to which i attach
//		//////////////////////////////////////////////////////////////////////
//
//		// GUI controls
//		private JTextField eventNameField;
//		private JCheckBox checkObservable, checkControllable;	
//		private MutableList listAssignedEvents;
//		private FilteringJList listAvailableEvents;
//		private JButton buttonClear, createButton, buttonDelete, buttonAdd, buttonRemove, buttonOK, buttonApply, buttonCancel;
//			
//			
//		@SuppressWarnings("serial") 
//		private class MutableList extends JList {
//			
//		    MutableList() {
//		    	super(new DefaultListModel());
//		    }
//
////		    MutableList(FSAModel a){
////		    	this();
////		    	DefaultListModel model = getContents();
////		    	for(Iterator<FSAEvent> i=a.getEventIterator();i.hasNext();)
////		    	{
////		    		events.
////		    	}
////		    	for(Object element: elements){
////		    		model.addElement(element);
////		    	}
////		    }
//
//		    MutableList(Object[] elements){
//		    	this();
//		    	DefaultListModel model = getContents();
//		    	for(Object element: elements){
//		    		model.addElement(element);
//		    	}
//		    }
//		    
//		    DefaultListModel getContents() {
//		    	@SuppressWarnings("unused") ListModel m = getModel();
//		    	return (DefaultListModel)getModel();
//		    }
//		    
//		    void addElement(Object o){
//		    	getContents().addElement(o);
//		    }
//		    
//		    void removeElement(Object o){
//		    	getContents().removeElement(o);
//		    }
//		    
//		    public void removeAll(){
//		    	getContents().removeAllElements();
//		    }
//		}   
//		
//		/**
//		 * Responds to clicks on add button.
//		 * 
//		 * @author Squirrel
//		 *
//		 */
//		private class AddButtonListener implements ActionListener{
//
//			/* (non-Javadoc)
//			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
//			 */
//			public void actionPerformed(ActionEvent arg0) {		 
//				// get elements selected from available events list
//				Object selected = listAvailableEvents.getSelectedValue();
//				if(selected != null){
//					listAssignedEvents.addElement(selected);
//					listAvailableEvents.removeElement(selected);
//				}	
//			}		
//		}
//
//		/**
//		 * Responds to clicks on remove button.
//		 * 
//		 * @author Squirrel
//		 *
//		 */
//		private class RemoveButtonListener implements ActionListener{
//
//			/* (non-Javadoc)
//			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
//			 */
//			public void actionPerformed(ActionEvent arg0) {
//				Object selected = listAssignedEvents.getSelectedValue();
//				if(selected != null){
//					listAssignedEvents.removeElement(selected);
//					listAvailableEvents.addElement(selected);
//				}	
//			}
//		}
//
//		private class CommitListener implements ActionListener{
//
//			/* (non-Javadoc)
//			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
//			 */
//			public void actionPerformed(ActionEvent arg0) {
//				// Apply any changes to edge's events
//				Event[] events = new Event[listAssignedEvents.getContents().size()];
//				Object[] contents = listAssignedEvents.getContents().toArray();
//				for(int i = 0; i < contents.length; i++){
//					events[i] = (Event)contents[i];
//				}				
//				IDESWorkspace.instance().getActiveGraphModel().assignEvents(events, edge);
//				
//				if(arg0.getSource().equals(buttonOK)){
//					dialog.setVisible(false);
//					if(dialog.getParent() != null){
//						dialog.getParent().repaint();
//					}
//				}
//			}
//			
//		}
//		
//		private class DeleteListener implements ActionListener {
//
//			/* (non-Javadoc)
//			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
//			 */
//			public void actionPerformed(ActionEvent arg0) {
//				// TODO remove the currently selected event from the FSA 
//				// (must remove it from all transitions in the FSA). 
//				Hub.displayAlert("TODO Implement delete event.");			
//			}
//			
//		}
//		
//		
//		private void setDummyData(){
////			 DEBUG ////////////////////////////////////////////
//			String[] data = {"Alpha", "Beta", "Gamma", "Delta"};
//			String elements[] = {
//		             "Partridge in a pear tree",
//		             "Turtle Doves",
//		             "French Hens",
//		             "Calling Birds",
//		             "Golden Rings",
//		             "Geese-a-laying",
//		             "Swans-a-swimming",
//		             "Maids-a-milking",
//		             "Ladies dancing",
//		             "Lords-a-leaping",
//		             "Pipers piping",
//		             "Drummers drumming",
//		             "Dasher",
//		             "Dancer",
//		             "Prancer",
//		             "Vixen",
//		             "Comet",
//		             "Cupid",
//		             "Donner",
//		             "Blitzen",
//		             "Rudolf",
//		             "Bakerloo",
//		             "Center",
//		             "Circle",
//		             "District",
//		             "East London",
//		             "Hammersmith and City",
//		             "Jubilee",
//		             "Metropolitan",
//		             "Northern",
//		             "Piccadilly Royal",
//		             "Victoria",
//		             "Waterloo and City",
//		             "Alpha",
//		             "Beta",
//		             "Gamma",
//		             "Delta",
//		             "Epsilon",
//		             "Zeta",
//		             "Eta",
//		             "Theta",
//		             "Iota",
//		             "Kapa",
//		             "Lamda",
//		             "Mu",
//		             "Nu",
//		             "Xi",
//		             "Omikron",
//		             "Pi",
//		             "Rho",
//		             "Sigma",
//		             "Tau",
//		             "Upsilon",
//		             "Phi",
//		             "Chi",
//		             "Psi",
//		             "Omega"
//		           };   
//		   
//		           for (String element: elements) {
//		             listAvailableEvents.addElement(element);
//		           }	           
//		        
//		   		listAssignedEvents = new MutableList(data);
//		   		
//			/////////////////////////////////////////////////////
//		}
//	}

}
