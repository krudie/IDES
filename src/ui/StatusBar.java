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

import presentation.fsa.GraphModel;

import main.Hub;
import model.Subscriber;

/**
 *
 * @author Lenko Grigorov
 */
public class StatusBar extends JPanel implements Subscriber {

	JLabel numbersLabel=new JLabel();
	model.fsa.ver1.Automaton a=null;
	
	public StatusBar()
	{
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		Hub.getWorkspace().attach(this);
		add(Box.createRigidArea(new Dimension(5,0)));
		add(numbersLabel);
		add(Box.createHorizontalGlue());
		//add(labelBox);
	}

	public void update() {
		if(a!=null)
			a.detach(this);
		if(Hub.getWorkspace().getActiveModel()!=null)
		{
			a=(model.fsa.ver1.Automaton)Hub.getWorkspace().getActiveModel();
			a.attach(this);
			numbersLabel.setText(
					Hub.string("nOfStates")+a.getStateCount()+", "+
					Hub.string("nOfTransitions")+a.getTransitionCount());
		}
		else
			numbersLabel.setText(Hub.string("noModelOpen"));
	}

}
