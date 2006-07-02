/**
 * 
 */
package presentation.fsa;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import main.Hub;

import util.EscapeDialog;
import util.ProperSingleton;

/**
 *
 * @author Lenko Grigorov
 */
public class SingleLineNodeLabellingDialog extends EscapeDialog {

	protected static final int WIDTH=15;
	protected static final int HEIGHT=4;
	
	private static SingleLineNodeLabellingDialog me=null;

	protected Action commitListener = new AbstractAction()
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			retString=area.getText();
			setVisible(false);
		}
	};

	private static String retString="";
	
	private SingleLineNodeLabellingDialog()
	{
		super(Hub.getMainWindow(),Hub.string("nodeLabellingTitle"),true);
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	onEscapeEvent();
		    }
		});
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		Box mainBox=Box.createVerticalBox();
		mainBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		area=new JTextField(WIDTH);
//		Object actionKey=area.getInputMap(JComponent.WHEN_FOCUSED).get(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0));
		area.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),this);
//		area.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,KeyEvent.CTRL_DOWN_MASK),actionKey);
		area.getActionMap().put(this,commitListener);
//		JScrollPane sPane=new JScrollPane(area);
		mainBox.add(area);
		mainBox.add(Box.createRigidArea(new Dimension(0,5)));
		//Box labelBox=Box.createHorizontalBox();
		//labelBox.add(new JLabel(Hub.string("ctrlEnter4NewLine")));
		//labelBox.add(Box.createHorizontalGlue());
		//mainBox.add(labelBox);
		getContentPane().add(mainBox);
		pack();
	}

	public static SingleLineNodeLabellingDialog instance()
	{
	    if (me == null)
	        me = new SingleLineNodeLabellingDialog();
	    return me;
	}

	public Object clone()
	{
	    throw new RuntimeException("Cloning of "+this.getClass().toString()+" not supported."); 
	}

	protected static JTextField area;
	
	public static String showAndGetLabel(String label, Point p)
	{
		instance();
		me.pack();
		retString=label;
		area.setText(label);
		if(p.x+me.getWidth()>Toolkit.getDefaultToolkit().getScreenSize().getWidth())
			p.x=p.x-me.getWidth();
		if(p.y+me.getHeight()>Toolkit.getDefaultToolkit().getScreenSize().getHeight())
			p.y=p.y-me.getHeight();
		me.setLocation(p);
		me.setVisible(true);
		return retString;
	}
	
	public void onEscapeEvent()
	{
		setVisible(false);
	}
}
