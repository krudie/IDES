/**
 * 
 */
package presentation.template;

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

import main.Hub;
import presentation.template.tools.SelectionTool;
import util.EscapeDialog;

/**
 * @author Lenko Grigorov
 */
public class BlockLabellingDialog extends EscapeDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1303197177886897161L;

	protected static final int WIDTH = 15;

	protected static final int HEIGHT = 4;

	private static BlockLabellingDialog me = null;

	protected static TemplateGraph graph;

	protected static GraphBlock block;

	protected Action commitListener = new AbstractAction()
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5388694428183259036L;

		public void actionPerformed(ActionEvent actionEvent)
		{
			if (graph != null)
			{
				graph.label(block, area.getText());
			}
			graph = null;
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

	private class Notifier extends WindowAdapter
	{
		private SelectionTool tool;

		public void setTool(SelectionTool t)
		{
			tool = t;
		}

		@Override
		public void windowDeactivated(WindowEvent e)
		{
			tool.promptOff();
		}
	}

	private Notifier notifier = new Notifier();

	private BlockLabellingDialog()
	{
		super(Hub.getMainWindow(), Hub.string("blockLabellingTitle"));
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				onEscapeEvent();
			}
		});
		addWindowListener(notifier);
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

	public static BlockLabellingDialog instance()
	{
		if (me == null)
		{
			me = new BlockLabellingDialog();
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

	public static void showAndLabel(DesignDrawingView context,
			TemplateGraph gm, GraphBlock block, SelectionTool tool)
	{
		graph = gm;
		BlockLabellingDialog.block = block;
		Point p = new Point((int)block.getLocation().x, (int)block
				.getLocation().y);
		instance();
		me.pack();
		me.notifier.setTool(tool);
		String label = block.getName();
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
		Point2D.Float r = context.localToScreen(new Point2D.Float(p.x, p.y));
		p.x = (int)r.x + context.getLocationOnScreen().x;
		p.y = (int)r.y + context.getLocationOnScreen().y;
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
