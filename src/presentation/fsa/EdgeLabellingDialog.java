package presentation.fsa;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import main.Hub;
import main.IDESWorkspace;
import model.Subscriber;
import model.fsa.FSAEvent;
import model.fsa.FSAEventsModel;
import model.fsa.FSATransition;
import model.fsa.ver1.Event;
import model.fsa.ver1.EventsModel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import util.EscapeDialog;
/**
 * Dialog window for assigning multiple events from the global events model
 * to transitions represented by an edge in the graph model. 
 * 
 * @author helen bretzke
 *
 */
public class EdgeLabellingDialog extends EscapeDialog implements Subscriber {
	
	private static EdgeLabellingDialog dialog;
	
	public static void initialize(JComponent parent, EventsModel eventsModel){
		Frame f = JOptionPane.getFrameForComponent(parent);
		dialog = new EdgeLabellingDialog(f, eventsModel);
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
		dialog.checkControllable.setSelected(dialog.cbCState);
		dialog.checkObservable.setSelected(dialog.cbOState);
        dialog.setEdge(e);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);        	
	}
	
	private EdgeLabellingDialog(){
		this(null, new EventsModel());		
	}		
	
	/**
	 * The listener for the user decides to add a new event.
	 */
	protected Action createListener = new AbstractAction()
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			if(!(actionEvent.getSource() instanceof JButton))
			{
				if("".equals(textField.getText()))
					buttonOK.doClick();
				else
					buttonCreate.doClick();
				return;
			}
			if("".equals(textField.getText()))
			{
				textField.requestFocus();
				return;
			}
			if(((JButton)actionEvent.getSource()).getText().equals(Hub.string("assignNew")))
			{
				newEvent = IDESWorkspace.instance().getActiveGraphModel().createEvent(textField.getText(), checkControllable.isSelected(), checkObservable.isSelected());
				updateOnlyAvailable();			
				listAvailableEvents.setSelectedValue(newEvent, true);
			}
			else
			{
				if(listAssignedEvents.existsElement(textField.getText()))
					return;
				listAvailableEvents.setSelectedIndex(listAvailableEvents.indexOfFirstElementWithPrefix(textField.getText()));
			}
			new AddButtonListener().actionPerformed(new ActionEvent(this,0,""));
			textField.setText("");
			textField.requestFocus();
		}
	};
	
	private EdgeLabellingDialog(Frame owner, EventsModel eventsModel){
		super(owner, "Assign events to edge", true);		
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	onEscapeEvent();
		    }
		});
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.eventsModel = eventsModel;
//		 NOT YET	eventsModel.attach(this);
		
		Box mainBox=Box.createVerticalBox();
		createBox=Box.createHorizontalBox();
		
		textField=new JTextField();
		textField.setMaximumSize(new Dimension(textField.getMaximumSize().width,
				textField.getPreferredSize().height));
		DocumentListener al=new DocumentListener()
		{
			public void changedUpdate(DocumentEvent e)
			{
				inserted=false;
				configStuff(textField.getText());
			}
			public void insertUpdate(DocumentEvent e)
			{
				inserted=true;
				configStuff(textField.getText());
			}
			public void removeUpdate(DocumentEvent e)
			{
				inserted=false;
				configStuff(textField.getText());
			}
			private void configStuff(String symbol)
			{
				if(!listAvailableEvents.existsElement(symbol) && !listAssignedEvents.existsElement(symbol)){
					createBox.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createLineBorder(
							(textField.getText().equals("")?UIManager.getColor("TextField.shadow"):
								UIManager.getColor("TextField.darkShadow")),1),
							Hub.string("enterAssignEvent")));
					buttonCreate.setText(Hub.string("assignNew"));
					checkControllable.setEnabled(true);
					checkControllable.setSelected(cbCState);
					checkObservable.setEnabled(true);
					checkObservable.setSelected(cbOState);
				}else{
					createBox.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createLineBorder(UIManager.getColor("TextField.shadow"),1),
							Hub.string("enterAssignEvent")));
					buttonCreate.setText(Hub.string("assign"));
					checkControllable.setEnabled(false);
					checkControllable.setSelected(cbCState);
					for (Iterator<FSAEvent> i=Hub.getWorkspace().getActiveModel().getEventIterator();i.hasNext();)
					{
						FSAEvent event=i.next();
						if(event.getSymbol().equals(symbol))
						{
							checkControllable.setSelected(event.isControllable());
							break;
						}
					}
					checkObservable.setEnabled(false);
					checkObservable.setSelected(cbOState);
					for (Iterator<FSAEvent> i=Hub.getWorkspace().getActiveModel().getEventIterator();i.hasNext();)
					{
						FSAEvent event=i.next();
						if(event.getSymbol().equals(symbol))
						{
							checkObservable.setSelected(event.isObservable());
							break;
						}
					}
				}
			}
		};
		textField.getDocument().addDocumentListener(al);
		textField.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),this);
		textField.getActionMap().put(this,createListener);
		textField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				if(ke.isActionKey())
					return;
				listAvailableEvents.clearSelection();
				listAssignedEvents.clearSelection();
				String symbol = textField.getText();
				if("".equals(symbol))
					return;
				// Select the first event in the lists for which symbol
				// is a prefix
				int i = listAvailableEvents.indexOfFirstElementWithPrefix(symbol);
				if(i > -1){
					listAvailableEvents.setSelectedIndex(i);
				}
				
				i = listAssignedEvents.indexOfFirstElementWithPrefix(symbol);
				if(i > -1){
					listAssignedEvents.setSelectedIndex(i);
				}
			}			
		});
		createBox.add(textField);
		
		checkControllable=new JCheckBox(Hub.string("controllable"));
		checkControllable.setSelected(true);
		checkControllable.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				cbCState=((JCheckBox)arg0.getSource()).isSelected();
				textField.requestFocus();
			}
			
		});
		createBox.add(checkControllable);
		checkObservable=new JCheckBox(Hub.string("observable"));
		checkObservable.setSelected(true);
		checkObservable.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {			
				cbOState=((JCheckBox)arg0.getSource()).isSelected();
				textField.requestFocus();
			}
			
		});		
		createBox.add(checkObservable);
		
		buttonCreate=new JButton(Hub.string("assignNew"));
		buttonCreate.setToolTipText(Hub.string("createEventTooltip"));
		buttonCreate.setPreferredSize(new Dimension(buttonCreate.getPreferredSize().width,
				textField.getPreferredSize().height));
		buttonCreate.addActionListener(createListener);
		createBox.add(buttonCreate);

		createBox.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(UIManager.getColor("TextField.shadow"),1),
				Hub.string("enterAssignEvent")));
		mainBox.add(createBox);
		mainBox.add(Box.createRigidArea(new Dimension(0,5)));
		
		Box listBox=Box.createHorizontalBox();

		listAvailableEvents = new MutableList(); //new FilteringJList();
		listAvailableEvents.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		//listAvailableEvents.installJTextField(textField);
		listAvailableEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting())
					return;

				ListSelectionModel lsm = (ListSelectionModel)e.getSource();				
				if (!lsm.isSelectionEmpty()) {
					listAssignedEvents.clearSelection();
					Object o = listAvailableEvents.getSelectedValue();
					if(o != null){
						selectedEvent = (Event)o;
						listAvailableEvents.setSelectedValue(o, true);	
						if(listAvailableEvents.hasFocus())
						{
							textField.setText(selectedEvent.getSymbol());
						}
						if(inserted && textField.getSelectedText()==null
								&&textField.getCaretPosition()==textField.getText().length())
						{
							String originalText=textField.getText();
							textField.setText(selectedEvent.getSymbol());
							textField.setSelectionEnd(textField.getText().length());
							textField.setSelectionStart(originalText.length());
						}
						
					}
				}
			}
			}
		);
		JScrollPane pane = new JScrollPane(listAvailableEvents);
		pane.setPreferredSize(new Dimension(200, 300));
		pane.setBorder(BorderFactory.createTitledBorder("Available"));

		listBox.add(pane);
		listBox.add(Box.createHorizontalGlue());
		
	    buttonAdd = new JButton(">>");
	    buttonAdd.setToolTipText("Assign events to edge");
	    AddButtonListener abl=new AddButtonListener();
	    buttonAdd.addActionListener(abl);
	    listAvailableEvents.addMouseListener(abl);
		buttonRemove = new JButton("<<");		
		buttonRemove.setToolTipText("Remove events from edge");
		RemoveButtonListener rbl=new RemoveButtonListener();
		buttonRemove.addActionListener(rbl);
		JPanel pCentre = new JPanel();
		BoxLayout boxLayout = new BoxLayout(pCentre, BoxLayout.Y_AXIS);
		pCentre.setLayout(boxLayout);
		pCentre.add(buttonAdd);
		pCentre.add(Box.createRigidArea(new Dimension(0,5)));
		pCentre.add(buttonRemove);		

		listBox.add(pCentre);
		listBox.add(Box.createHorizontalGlue());

		listAssignedEvents = new MutableList();
		// TODO Only one item can be selected: change to MULTIPLE_INTERVAL_SELECTION later
		listAssignedEvents.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		listAssignedEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();				
				if (!lsm.isSelectionEmpty()) {
					listAvailableEvents.clearSelection();
					Object o = listAssignedEvents.getSelectedValue();
					if(o != null){
						selectedEvent = (Event)o;
						listAssignedEvents.setSelectedValue(o, true);		
						if(listAssignedEvents.hasFocus())
						{
							textField.setText(selectedEvent.getSymbol());
						}
						
					}
				}
			}
			
		});
		listAssignedEvents.addMouseListener(rbl);
		pane = new JScrollPane(listAssignedEvents);
		pane.setPreferredSize(new Dimension(200, 300));
		pane.setBorder(BorderFactory.createTitledBorder("Assigned to Edge"));
		
		listBox.add(pane);				
		
		mainBox.add(listBox);
		mainBox.add(Box.createRigidArea(new Dimension(0,5)));
		
		ActionListener commitListener = new CommitListener();		
		buttonOK = new JButton("OK");
		buttonOK.addActionListener(commitListener);
		buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae){
					onEscapeEvent();
				}
			}
		);
		
		JPanel p = new JPanel(new FlowLayout());
		p.add(buttonOK);		
		p.add(buttonCancel);
		rootPane.setDefaultButton(buttonOK);
		
		mainBox.add(p);
		mainBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		getContentPane().add(mainBox);
		pack();
		
		buttonOK.setPreferredSize(new Dimension(
				Math.max(buttonOK.getWidth(),buttonCancel.getWidth()),buttonOK.getHeight()));
		buttonOK.invalidate();
		buttonCancel.setPreferredSize(new Dimension(
				Math.max(buttonOK.getWidth(),buttonCancel.getWidth()),buttonCancel.getHeight()));
		buttonCancel.invalidate();
	}

	public void updateOnlyAvailable() {
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
		updateOnlyAvailable();
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
	
	public void onEscapeEvent()
	{
        textField.requestFocus();
		dialog.setVisible(false);
	}
	
	//public void 
	
	// Data
	private Edge edge;
	private Event newEvent;		
	private Event selectedEvent;
	private boolean inserted=false;
	/**
	 * state of the controllable checkbox when creating new event
	 */
	private boolean cbCState=true;
	/**
	 * state of the observable checkbox when creating new event
	 */
	private boolean cbOState=true;
	
	//for bordercolor change
	private Box createBox;
	
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

		public boolean existsElement(String symbol) {
			DefaultListModel model = getContents();
			for(int i = 0; i < model.getSize(); i++){
				if(symbol.equals(model.getElementAt(i).toString())){
					return true;
				}
			}
			return false;
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
	private class AddButtonListener extends MouseAdapter implements ActionListener{

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
		
		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount()>1&&!listAvailableEvents.isSelectionEmpty())
				actionPerformed(new ActionEvent(listAvailableEvents,0,""));
		}
	}

	/**
	 * Responds to clicks on remove button.
	 * 
	 * @author Squirrel
	 *
	 */
	private class RemoveButtonListener extends MouseAdapter implements ActionListener{

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
		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount()>1&&!listAssignedEvents.isSelectionEmpty())
				actionPerformed(new ActionEvent(listAssignedEvents,0,""));
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
			IDESWorkspace.instance().getActiveGraphModel().replaceEventsOnEdge(events, edge);
			
			if(arg0.getSource().equals(buttonOK)){
		        textField.requestFocus();
				dialog.setVisible(false);
				if(dialog.getParent() != null){
					dialog.getParent().repaint();
				}
			}
		}
		
	}
	
//	private class DeleteListener implements ActionListener {
//
//		/* (non-Javadoc)
//		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
//		 */
//		public void actionPerformed(ActionEvent arg0) {
//			// TODO remove the currently selected event from the FSA 
//			// (must remove it from all transitions in the FSA). 
//			Hub.displayAlert("TODO Implement delete event.");			
//		}
//		
//	}
	
}
