/**
 * 
 */
package ui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import observer.FSMMessage;
import observer.FSMSubscriber;
import observer.Subscriber;
import observer.WorkspaceMessage;
import observer.WorkspaceSubscriber;

import presentation.fsa.FSMGraph;

import main.Hub;

/**
 *
 * @author Lenko Grigorov
 */
public class StatusBar extends JPanel implements FSMSubscriber, WorkspaceSubscriber {

	JLabel numbersLabel=new JLabel();
	model.fsa.ver1.Automaton a=null;
	
	public StatusBar()
	{
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		Hub.getWorkspace().addSubscriber(this);
		add(Box.createRigidArea(new Dimension(5,0)));
		add(numbersLabel);
		add(Box.createHorizontalGlue());
		//add(labelBox);
	}

	private void refreshActiveModel()
	{
		if(a!=null)
			a.removeSubscriber(this);

		if(Hub.getWorkspace().getActiveModel()!=null)
		{
			a=(model.fsa.ver1.Automaton)Hub.getWorkspace().getActiveModel();
			a.addSubscriber(this);
			refreshStatusLabel();
		}			
	}
		
	
	private void refreshStatusLabel()
	{
		if(a != null)
		{
			numbersLabel.setText(
					Hub.string("nOfStates")+a.getStateCount()+", "+
					Hub.string("nOfTransitions")+a.getTransitionCount());
		}else{
			numbersLabel.setText(Hub.string("noModelOpen"));
		}
	}
	
	/* (non-Javadoc)
	 * @see observer.WorkspaceSubscriber#modelCollectionChanged(observer.WorkspaceMessage)
	 */
	public void modelCollectionChanged(WorkspaceMessage message) {
		refreshActiveModel();
	}

	/* (non-Javadoc)
	 * @see observer.WorkspaceSubscriber#repaintRequired(observer.WorkspaceMessage)
	 */
	public void repaintRequired(WorkspaceMessage message) {}

	/* (non-Javadoc)
	 * @see observer.WorkspaceSubscriber#modelSwitched(observer.WorkspaceMessage)
	 */
	public void modelSwitched(WorkspaceMessage message) {
		refreshActiveModel();
	}

//	/**
//	 * TODO remove this method after this class no longer implements generic subscriber. 
//	 */
//	public void update() {
//		refreshActiveModel();
//	}

	/* (non-Javadoc)
	 * @see observer.FSMSubscriber#fsmStructureChanged(observer.FSMMessage)
	 */
	public void fsmStructureChanged(FSMMessage message) {
		// if states or transitions added or removed
		if( (message.getElementType() == FSMMessage.STATE || 
				message.getElementType() == FSMMessage.TRANSITION) &&
			(message.getEventType() == FSMMessage.ADD || 
				message.getEventType() == FSMMessage.REMOVE) )
		{
			refreshStatusLabel();
		}
	}

	/* (non-Javadoc)
	 * @see observer.FSMSubscriber#fsmEventSetChanged(observer.FSMMessage)
	 */
	public void fsmEventSetChanged(FSMMessage message) {}
}
