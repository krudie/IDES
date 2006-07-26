/**
 * 
 */
package presentation.fsa;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;

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
	
	private static FSMGraph gm=null;
	private static Node n;

	protected Action commitListener = new AbstractAction()
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			if(gm!= null){
				gm.labelNode(n,area.getText());						
			}
			setVisible(false);
		}
	};

	private SingleLineNodeLabellingDialog()
	{
		super(Hub.getMainWindow(),Hub.string("nodeLabellingTitle"));
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	onEscapeEvent();
		    }
		});
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		Box mainBox=Box.createVerticalBox();
		mainBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		area=new JTextField(WIDTH);
		area.addFocusListener(new FocusListener()
		{
			public void focusLost(FocusEvent e)
			{
				commitListener.actionPerformed(new ActionEvent(this,0,""));	
			}
			public void focusGained(FocusEvent e)
			{
			}
		});
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
	
	public static void showAndLabel(FSMGraph gm, Node node)
	{
		SingleLineNodeLabellingDialog.gm=gm;
		n=node;
		Point p=new Point((int)node.getLayout().getLocation().x,
				(int)node.getLayout().getLocation().y);
		instance();
		me.pack();
		String label=node.getLabel().getLayout().getText();
		area.setText(label);
		GraphDrawingView gdv=((ui.MainWindow)Hub.getMainWindow()).getDrawingBoard();
		Point2D.Float r=gdv.localToScreen(new Point2D.Float(p.x,p.y));
		p.x=(int)r.x+Hub.getWorkspace().getDrawingBoardDisplacement().x;
		p.y=(int)r.y+Hub.getWorkspace().getDrawingBoardDisplacement().y;
		if(p.x+me.getWidth()>Toolkit.getDefaultToolkit().getScreenSize().getWidth())
			p.x=p.x-me.getWidth();
		if(p.y+me.getHeight()>Toolkit.getDefaultToolkit().getScreenSize().getHeight())
			p.y=p.y-me.getHeight();
		me.setLocation(p);
		me.setVisible(true);
	}
	
	public void onEscapeEvent()
	{
		setVisible(false);
	}
}
