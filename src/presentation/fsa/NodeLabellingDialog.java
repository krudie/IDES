/**
 * 
 */
package presentation.fsa;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
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
import javax.swing.KeyStroke;

import main.Hub;
import presentation.fsa.actions.GraphActions;
import util.EscapeDialog;

/**
 * @author Lenko Grigorov
 */
public class NodeLabellingDialog extends EscapeDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8319535490150212814L;

	protected static final int WIDTH = 15;

	protected static final int HEIGHT = 4;

	private static NodeLabellingDialog me = null;

	private static FSAGraph gm = null;

	private static CircleNode n;

	protected Action commitListener = new AbstractAction()
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8347292673808107793L;

		public void actionPerformed(ActionEvent actionEvent)
		{
			if (gm != null && !area.getText().equals(n.getLabel().getText()))
			{
				new GraphActions.LabelAction(gm, n, area.getText()).execute();
			}
			setVisible(false);
		}
	};

	// the main job will be handled by commitListener on focusLost
	protected Action enterListener = new AbstractAction()
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8316924939213463160L;

		public void actionPerformed(ActionEvent actionEvent)
		{
			setVisible(false);
		}
	};

	protected static FocusListener commitOnFocusLost = new FocusListener()
	{
		public void focusLost(FocusEvent e)
		{
			me.commitListener.actionPerformed(new ActionEvent(this, 0, ""));
		}

		public void focusGained(FocusEvent e)
		{
		}
	};

	private NodeLabellingDialog()
	{
		super(Hub.getMainWindow(), Hub.string("nodeLabellingTitle"));
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				onEscapeEvent();
			}
		});
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		Box mainBox = Box.createVerticalBox();
		mainBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		area = new JTextArea(HEIGHT, WIDTH);
		Object actionKey = area
				.getInputMap(JComponent.WHEN_FOCUSED).get(KeyStroke
						.getKeyStroke(KeyEvent.VK_ENTER, 0));
		area.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, 0),
				this);
		area.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK),
				actionKey);
		area.getActionMap().put(this, enterListener);
		JScrollPane sPane = new JScrollPane(area);
		mainBox.add(sPane);
		mainBox.add(Box.createRigidArea(new Dimension(0, 5)));
		Box labelBox = Box.createHorizontalBox();
		labelBox.add(new JLabel(Hub.string("ctrlEnter4NewLine")));
		labelBox.add(Box.createHorizontalGlue());
		mainBox.add(labelBox);
		getContentPane().add(mainBox);
		pack();
	}

	public static NodeLabellingDialog instance()
	{
		if (me == null)
		{
			me = new NodeLabellingDialog();
		}
		return me;
	}

	@Override
	public Object clone()
	{
		throw new RuntimeException("Cloning of " + this.getClass().toString()
				+ " not supported.");
	}

	protected static JTextArea area;

	public static void showAndLabel(GraphDrawingView gdv, FSAGraph gm,
			CircleNode node)
	{
		gdv.startUIInteraction();
		NodeLabellingDialog.gm = gm;
		n = node;
		Point p = new Point((int)node.getLayout().getLocation().x, (int)node
				.getLayout().getLocation().y);
		instance();
		me.pack();
		String label = node.getLabel().getText();
		boolean hasOurListener = false;
		for (int i = 0; i < area.getFocusListeners().length; ++i)
		{
			if (area.getFocusListeners()[i] == commitOnFocusLost)
			{
				hasOurListener = true;
			}
		}
		if (!hasOurListener)
		{
			area.addFocusListener(commitOnFocusLost);
		}
		area.setText(label);
		area.selectAll();
		if (gdv == null)
		{
			return;
		}
		Point2D.Float r = gdv.localToScreen(new Point2D.Float(p.x, p.y));
		p.x = (int)r.x + gdv.getLocationOnScreen().x;
		p.y = (int)r.y + gdv.getLocationOnScreen().y;
		if (p.x + me.getWidth() > Toolkit
				.getDefaultToolkit().getScreenSize().getWidth())
		{
			p.x = p.x - me.getWidth();
		}
		if (p.y + me.getHeight() > Toolkit
				.getDefaultToolkit().getScreenSize().getHeight())
		{
			p.y = p.y - me.getHeight();
		}
		me.setLocation(p);
		me.setVisible(true);
		area.requestFocus();
	}

	@Override
	public void onEscapeEvent()
	{
		area.removeFocusListener(commitOnFocusLost);
		setVisible(false);
	}
}
