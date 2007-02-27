package ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;

import observer.Subscriber;
import observer.WorkspaceMessage;
import observer.WorkspaceSubscriber;

import main.Hub;
import model.fsa.FSAEvent;
import model.fsa.FSAMessage;
import model.fsa.FSAModel;
import model.fsa.FSAPublisher;
import model.fsa.FSAPublisherAdaptor;
import model.fsa.FSASubscriber;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.Event;

/**
 * TODO Comment
 * 
 * @author Lenko Grigorov
 */
public class EventView extends JPanel implements WorkspaceSubscriber, FSASubscriber, ActionListener {

	protected class EventTableModel extends AbstractTableModel
	{
		private FSAModel a=null;
		private Vector<FSAEvent> events=null;
		private Vector<Boolean> controllable=null;
		private Vector<Boolean> observable=null;
		private final String[] columnNames = {
			Hub.string("eventNameHeading"),
			Hub.string("controllableHeading"),
			Hub.string("observableHeading")
			};
		
		public EventTableModel()
		{
			events=new Vector<FSAEvent>();
			controllable=new Vector<Boolean>();
			observable=new Vector<Boolean>();
		}
		
		public EventTableModel(FSAModel a)
		{
			events=new Vector<FSAEvent>();
			controllable=new Vector<Boolean>();
			observable=new Vector<Boolean>();
			this.a=a;
			for(Iterator<FSAEvent> i=a.getEventIterator();i.hasNext();)
			{
				events.add(i.next());
			}
			Collections.sort(events,new Comparator<FSAEvent>(){
				public int compare(FSAEvent event1, FSAEvent event2)
				{
					return event1.getSymbol().compareTo(event2.getSymbol());
				}
			});
			for(int i=0;i<events.size();++i)
			{
				if(events.elementAt(i).isControllable())
					controllable.add(new Boolean(true));
				else
					controllable.add(new Boolean(false));
				if(events.elementAt(i).isObservable())
					observable.add(new Boolean(true));
				else
					observable.add(new Boolean(false));
			}
		}
		
	    public String getColumnName(int col) {
	        return columnNames[col].toString();
	    }
	    
	    public int getRowCount() { return events.size(); }
	    public int getColumnCount() { return columnNames.length; }
	    public Object getValueAt(int row, int col) {
	    	if(col==0)
	    	{
	    		return events.elementAt(row).getSymbol();
	    	}
	    	else if(col==1)
	    	{
	    		return controllable.elementAt(row);
	    	}
	    	else
	    	{
	    		return observable.elementAt(row);	    		
	    	}
	    }

	    public boolean isCellEditable(int row, int col) { return true; }
	    
	    /**
	     * Sets symbol, observable or controllable properties of the event
	     * at the given row to the given value.
	     */
	    public void setValueAt(Object value, int row, int col) {
	    	long eventId=0;
	    	if(col==0)
	    	{
	    		if("".equals((String)value))
	    		{
	    			Toolkit.getDefaultToolkit().beep();
	    			return;
	    		}
	    		eventId=events.elementAt(row).getId();
	    		FSAEvent existingEvent=closestEvent((String)value);
	    		if(((String)value).equals(existingEvent.getSymbol()))
	    		{
	    			Toolkit.getDefaultToolkit().beep();
	    			return;
	    		}
	    		
	    		events.elementAt(row).setSymbol((String)value);

	    	}
	    	else if(col==1)
	    	{
	    		
	    		events.elementAt(row).setControllable(((Boolean)value).booleanValue());
	    		controllable.removeElementAt(row);
	    		controllable.insertElementAt((Boolean)value, row);
	    	}
	    	else
	    	{
	    		
	    		events.elementAt(row).setObservable(((Boolean)value).booleanValue());
	    		observable.removeElementAt(row);
	    		observable.insertElementAt((Boolean)value, row);	    		
	    	}
	    		    		
	    	((FSAPublisher)a).fireFSAEventSetChanged(new FSAMessage(FSAMessage.MODIFY,
	    			FSAMessage.EVENT, events.elementAt(row).getId(), (FSAPublisherAdaptor)a));			
	    	
	    	if(col==0)
	    	{
				Collections.sort(events,new Comparator<FSAEvent>(){
					public int compare(FSAEvent event1, FSAEvent event2)
					{
						return event1.getSymbol().compareTo(event2.getSymbol());
					}
				});
				fireTableDataChanged();
				int newIdx=events.indexOf(a.getEvent(eventId));
				table.setRowSelectionInterval(newIdx,newIdx);
				table.scrollRectToVisible(table.getCellRect(newIdx,0,false));
	    	}
	    	else
	    		fireTableCellUpdated(row, col);
	    }
	    
        public Class getColumnClass(int c) {
        	if(c==0)
        	{
        		return String.class;
        	}
        	else
        	{
        		return Boolean.class;
        	}
        }
        
        public FSAEvent closestEvent(String symbol)
        {
        	FSAEvent retVal=null;
        	if(!events.isEmpty())
        		retVal=events.firstElement();
        	for(FSAEvent event:events)
        	{
        		if(event.getSymbol().compareTo(symbol)>0)
        			break;
        		retVal=event;
        	}
        	return retVal;
        }
        
        public FSAEvent getEventAt(int idx)
        {
        	return events.elementAt(idx);
        }
	}
	
	/**
	 * The listener for the user pressing the <code>Delete</code> key.
	 */
	protected Action deleteListener = new AbstractAction()
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			int[] rows=table.getSelectedRows();
			if(rows.length>0)
			{
				int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
						Hub.string("confirmDeleteEvents"),Hub.string("deleteEventsTitle"),
						JOptionPane.YES_NO_CANCEL_OPTION);
				if(choice!=JOptionPane.YES_OPTION)
					return;
			}
			FSAEvent[] delEvents=new FSAEvent[rows.length];
			for(int i=0;i<rows.length;++i)
			{
				delEvents[i]=((EventTableModel)table.getModel()).getEventAt(rows[i]);
			}
			// FIXME Issue a command that goes to automaton.
			FSAModel a=Hub.getWorkspace().getActiveModel();
			for(int i=0;i<delEvents.length;++i)
			{
				//Hub.getWorkspace().getActiveGraphModel().removeEvent((Event)delEvents[i]);
				a.remove((Event)delEvents[i]);
			}
			refreshEventTable();
			eventNameField.requestFocus();
		}
	};

	/**
	 * The listener for the user decides to add a new event.
	 */
	protected Action createListener = new AbstractAction()
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			if(!(actionEvent.getSource() instanceof JButton))
			{
				createButton.doClick();
				return;
			}
			FSAModel a=Hub.getWorkspace().getActiveModel();
			if(a==null||"".equals(eventNameField.getText()))
				return;
			
			// FIXME issue a command to the Automaton and let messaging notify the graph model. 
			Event event=Hub.getWorkspace().getActiveGraphModel().createAndAddEvent(eventNameField.getText(), controllableCBox.isSelected(), observableCBox.isSelected());
//			Event event=new Event(Hub.getWorkspace().getActiveGraphModel().getFreeEventId());
//			event.setSymbol(eventNameField.getText());
//			event.setControllable(controllableCBox.isSelected());
//			event.setObservable(observableCBox.isSelected());
//			a.add(event);
//			a.notifyAllSubscribers();
			
			//update();
			int rows=table.getModel().getRowCount();
			for(int i=0;i<rows;++i)
			{
				if(((String)table.getModel().getValueAt(i,0)).equals(event.getSymbol()))
				{
					table.setRowSelectionInterval(i,i);
					table.scrollRectToVisible(table.getCellRect(i,0,false));
					break;
				}
			}
			eventNameField.requestFocus();
		}
	};
	
	protected JTable table;
	protected JTextField eventNameField;
	protected JCheckBox controllableCBox;
	protected JCheckBox observableCBox;
	protected JButton createButton;
	protected JButton deleteButton;
	private FSAModel lastModel=null;
	
	public EventView()
	{
		super();
		Box mainBox=Box.createVerticalBox();
		Box createBox=Box.createHorizontalBox();
		
		eventNameField=new JTextField();
		eventNameField.setMaximumSize(new Dimension(eventNameField.getMaximumSize().width,
				eventNameField.getPreferredSize().height));
		DocumentListener al=new DocumentListener()
		{
			public void changedUpdate(DocumentEvent e)
			{
				configStuff(eventNameField.getText());
			}
			public void insertUpdate(DocumentEvent e)
			{
				configStuff(eventNameField.getText());
			}
			public void removeUpdate(DocumentEvent e)
			{
				configStuff(eventNameField.getText());
			}
			private void configStuff(String s)
			{
				FSAEvent event=((EventTableModel)table.getModel()).closestEvent(s);
				if(event!=null&&event.getSymbol().equals(s))
				{
					createButton.setEnabled(false);
					controllableCBox.setEnabled(false);
					//controllableCBox.setSelected(event.isControllable());
					observableCBox.setEnabled(false);
					//observableCBox.setSelected(event.isObservable());
				}
				else if ("".equals(s))
				{
					createButton.setEnabled(false);
					controllableCBox.setEnabled(true);
					observableCBox.setEnabled(true);
				}
				else
				{
					createButton.setEnabled(true);
					controllableCBox.setEnabled(true);
					observableCBox.setEnabled(true);
				}
				int rows=table.getModel().getRowCount();
				for(int i=0;i<rows;++i)
				{
					if(((String)table.getModel().getValueAt(i,0)).equals(event.getSymbol()))
					{
						table.setRowSelectionInterval(i,i);
						table.scrollRectToVisible(table.getCellRect(i,0,false));
						break;
					}
				}
			}
		};
		eventNameField.getDocument().addDocumentListener(al);
		eventNameField.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),this);
		eventNameField.getActionMap().put(this,createListener);
		createBox.add(eventNameField);
		
		controllableCBox=new JCheckBox(Hub.string("controllable"));
		controllableCBox.setSelected(true);
		createBox.add(controllableCBox);
		observableCBox=new JCheckBox(Hub.string("observable"));
		observableCBox.setSelected(true);
		createBox.add(observableCBox);
		
		createButton=new JButton(Hub.string("add"));
		createButton.setPreferredSize(new Dimension(createButton.getPreferredSize().width,
				eventNameField.getPreferredSize().height));
		createButton.addActionListener(createListener);
		createBox.add(createButton);
		createBox.setBorder(BorderFactory.createTitledBorder(Hub.string("addNewEvent")));//.createEmptyBorder(5,5,5,5));
		//Box borderPane=Box.createHorizontalBox();
		//borderPane.setBorder(BorderFactory.createLineBorder(this.getForeground()));
		//borderPane.add(createBox);
		mainBox.add(createBox);
		
		mainBox.add(Box.createRigidArea(new Dimension(0,5)));

		table=new JTable(new EventTableModel());
		table.setPreferredScrollableViewportSize(new Dimension(
				table.getPreferredScrollableViewportSize().width,
				200));
		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0),this);
		table.getActionMap().put(this,deleteListener);
		mainBox.add(new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		
		mainBox.add(Box.createRigidArea(new Dimension(0,5)));

		Box deleteBox=Box.createHorizontalBox();
		deleteBox.add(Box.createHorizontalGlue());
		//deleteBox.add(new JLabel(Hub.string("deleteSelectedEvents")));
		deleteButton=new JButton(Hub.string("delete"));
		deleteButton.addActionListener(deleteListener);
		deleteBox.add(deleteButton);
		deleteBox.setBorder(BorderFactory.createTitledBorder(Hub.string("deleteSelectedEvents")));//.createEmptyBorder(5,5,5,5));
		mainBox.add(deleteBox);

		add(mainBox);
		refreshEventTable();
		Hub.getWorkspace().addSubscriber(this);
	}
	
	private void refreshEventTable()
	{
		FSAModel model=Hub.getWorkspace().getActiveModel();
		eventNameField.setText("");
		
		// CLM: these controls should be disabled whenever eventNameField
		// is empty
		createButton.setEnabled(false);
		if(model==null)
		{
			table.setModel(new EventTableModel());
			deleteButton.setEnabled(false);
			eventNameField.setEnabled(false);
			table.setEnabled(false);
			if(lastModel!=null)
			{
				lastModel.removeSubscriber(this);
				lastModel=null;
			}
		}
		else
		{
			table.setModel(new EventTableModel(model));
			//CLM: these should be enabled iff eventNameField is nonempty
			//createButton.setEnabled(true);
			//controllableCBox.setEnabled(true);
			//observableCBox.setEnabled(true);
			
			if (table.getRowCount() == 0)
				deleteButton.setEnabled(false);
			else
				deleteButton.setEnabled(true);
			eventNameField.setEnabled(true);
			table.setEnabled(true);
			if(!model.equals(lastModel))
			{
				if(lastModel!=null)
					lastModel.removeSubscriber(this);
				lastModel=model;
				lastModel.addSubscriber(this);
			}
		}	
	}
//	
//	/**
//	 * TODO remove this method when no longer extends Publisher
//	 */
//	public void update()
//	{
//		refreshEventTable();
//	}
//	
	public void actionPerformed(ActionEvent e){}

	/* (non-Javadoc)
	 * @see observer.WorkspaceSubscriber#modelCollectionChanged(observer.WorkspaceMessage)
	 */
	public void modelCollectionChanged(WorkspaceMessage message) {
		// ??? Can I ignore the notification if the model was modified (e.g. renamed)?
		refreshEventTable();		
	}

	/* (non-Javadoc)
	 * @see observer.WorkspaceSubscriber#repaintRequired(observer.WorkspaceMessage)
	 */
	public void repaintRequired(WorkspaceMessage message) {}
	
	/**
	 * Makes the Event tab visible or invisible
	 * 
	 * @param b flag indicating visibility status
	 */
	public void setVisible(boolean b)
	{
		super.setVisible(b);
		eventNameField.requestFocus();
	}

	/* (non-Javadoc)
	 * @see observer.WorkspaceSubscriber#modelSwitched(observer.WorkspaceMessage)
	 */
	public void modelSwitched(WorkspaceMessage message) {
		refreshEventTable();		
	}

	/* (non-Javadoc)
	 * @see observer.FSMSubscriber#fsmStructureChanged(observer.FSMMessage)
	 */
	public void fsaStructureChanged(FSAMessage message) {}

	/* (non-Javadoc)
	 * @see observer.FSMSubscriber#fsmEventSetChanged(observer.FSMMessage)
	 */
	public void fsaEventSetChanged(FSAMessage message) {
		refreshEventTable();		
	}

	/* (non-Javadoc)
	 * @see observer.FSASubscriber#fsaSaved()
	 */
	public void fsaSaved() {
		// TODO Auto-generated method stub
		
	}
}
