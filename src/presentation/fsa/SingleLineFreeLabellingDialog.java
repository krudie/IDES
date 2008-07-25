/**
 * 
 */
package presentation.fsa;

import ides.api.core.Hub;
import ides.api.utilities.EscapeDialog;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;


/**
 * A dialog window for entering a single line of text and creating a free label
 * (i.e. not associated with any graph element) on the drawing canvas. Commits
 * text and creates label when user types enter or when dialog loses focus.
 * 
 * @author Helen Bretzke
 */
public class SingleLineFreeLabellingDialog extends EscapeDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 928588520692787101L;

	protected static final int WIDTH = 15;

	protected static final int HEIGHT = 4;

	private static SingleLineFreeLabellingDialog me = null;

	private static FSAGraph gm = null;

	protected Action commitListener = new AbstractAction()
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8040999636847105815L;

		public void actionPerformed(ActionEvent actionEvent)
		{
			// TODO these changes should be sent through undoable commands
			if (gm != null && !area.getText().equals(""))
			{
				if (freeLabel == null)
				{
					// add a free label
					gm.addFreeLabel(area.getText(), new Point2D.Float(me
							.getLocation().x, me.getLocation().y
							- me.getHeight()));
				}
				else
				{
					// change the text on an existing one.
					gm.setLabelText(freeLabel, area.getText());
				}
				area.setText("");
			}
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

	private SingleLineFreeLabellingDialog()
	{
		super(Hub.getMainWindow(), Hub.string("freeLabellingTitle"));
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
		area = new JTextField(WIDTH);
		// Object
		// actionKey=area.getInputMap(JComponent.WHEN_FOCUSED).get(KeyStroke.
		// getKeyStroke(KeyEvent.VK_ENTER,0));
		area.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, 0),
				this);
		// area.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(
		// KeyEvent.VK_ENTER,KeyEvent.CTRL_DOWN_MASK),actionKey);
		area.getActionMap().put(this, commitListener);
		// JScrollPane sPane=new JScrollPane(area);
		mainBox.add(area);
		mainBox.add(Box.createRigidArea(new Dimension(0, 5)));
		// Box labelBox=Box.createHorizontalBox();
		// labelBox.add(new JLabel(Hub.string("ctrlEnter4NewLine")));
		// labelBox.add(Box.createHorizontalGlue());
		// mainBox.add(labelBox);
		getContentPane().add(mainBox);
		pack();
	}

	public static SingleLineFreeLabellingDialog instance()
	{
		if (me == null)
		{
			me = new SingleLineFreeLabellingDialog();
		}
		return me;
	}

	@Override
	public Object clone()
	{
		throw new RuntimeException("Cloning of " + this.getClass().toString()
				+ " not supported.");
	}

	protected static JTextField area;

	protected static GraphLabel freeLabel;

	public static void showAndLabel(GraphDrawingView gdv, FSAGraph gm,
			GraphLabel label)
	{
		SingleLineFreeLabellingDialog.gm = gm;
		freeLabel = label;
		Point p = new Point((int)label.getLayout().getLocation().x, (int)label
				.getLayout().getLocation().y);
		instance();
		me.pack();
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
		area.setText(label.getText());
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

	/**
	 * TODO Create a label where none exists so show the dialog at the given
	 * point and place the new label at this location.
	 */
	public static void showAndLabel(GraphDrawingView gdv, FSAGraph gm,
			Point2D.Float p)
	{
		SingleLineFreeLabellingDialog.gm = gm;
		freeLabel = null;
		instance();
		me.pack();
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
		area.setText("");
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
		me.setLocation((int)p.x, (int)p.y);
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
