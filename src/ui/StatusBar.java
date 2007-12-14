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

import main.Hub;
import main.WorkspaceMessage;
import main.WorkspaceSubscriber;
import model.DESModelMessage;
import model.DESModelSubscriber;
import model.fsa.FSAMessage;
import model.fsa.FSAModel;
import model.fsa.FSASubscriber;

/**
 * @author Lenko Grigorov
 */
public class StatusBar extends JPanel implements FSASubscriber,
		WorkspaceSubscriber, DESModelSubscriber
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8036410231866758994L;

	/**
	 * label to display the name of the current model with state and transition
	 * counts
	 */
	JLabel statsLabel = new JLabel();

	/** the currently active model */
	FSAModel a = null;

	public StatusBar()
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		Hub.getWorkspace().addSubscriber(this);
		add(Box.createRigidArea(new Dimension(5, 0)));
		add(statsLabel);
		add(Box.createHorizontalGlue());
		// add(labelBox);
	}

	private void refreshActiveModel()
	{
		if (a != null)
		{
			a.removeSubscriber((FSASubscriber)this);
			a.removeSubscriber((DESModelSubscriber)this);
		}

		if (Hub.getWorkspace().getActiveModel() != null
				&& Hub.getWorkspace().getActiveModel() instanceof FSAModel)
		{
			a = (FSAModel)Hub.getWorkspace().getActiveModel();
			a.addSubscriber((FSASubscriber)this);
			a.addSubscriber((DESModelSubscriber)this);
		}

		refreshStatusLabel();
	}

	private void refreshStatusLabel()
	{
		if (Hub.getWorkspace().getActiveModel() == null)
		{
			statsLabel.setText(Hub.string("noModelOpen"));
			return;
		}
		if (a != null)
		{
			String name;
			name = a.getName();
			statsLabel.setText(name + ":  " + +a.getStateCount() + " states,  "
					+ +a.getTransitionCount() + " transitions");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see observer.WorkspaceSubscriber#modelCollectionChanged(observer.WorkspaceMessage)
	 */
	public void modelCollectionChanged(WorkspaceMessage message)
	{
		// refreshActiveModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see observer.WorkspaceSubscriber#repaintRequired(observer.WorkspaceMessage)
	 */
	public void repaintRequired(WorkspaceMessage message)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see observer.WorkspaceSubscriber#modelSwitched(observer.WorkspaceMessage)
	 */
	public void modelSwitched(WorkspaceMessage message)
	{
		refreshActiveModel();
	}

	// /**
	// * TODO remove this method after this class no longer implements generic
	// subscriber.
	// */
	// public void update() {
	// refreshActiveModel();
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see observer.FSMSubscriber#fsmStructureChanged(observer.FSMMessage)
	 */
	public void fsaStructureChanged(FSAMessage message)
	{
		// if states or transitions added or removed
		if ((message.getElementType() == FSAMessage.STATE || message
				.getElementType() == FSAMessage.TRANSITION)
				&& (message.getEventType() == FSAMessage.ADD || message
						.getEventType() == FSAMessage.REMOVE))
		{

			refreshStatusLabel();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see observer.FSMSubscriber#fsmEventSetChanged(observer.FSMMessage)
	 */
	public void fsaEventSetChanged(FSAMessage message)
	{
	}

	public void saveStatusChanged(DESModelMessage message)
	{
	}

	public void modelNameChanged(DESModelMessage msg)
	{
		refreshStatusLabel();
	}
}
