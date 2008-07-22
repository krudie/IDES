package presentation.template.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import main.Hub;
import presentation.template.DesignDrawingView;
import presentation.template.GraphBlock;
import presentation.template.GraphLink;

public class SelectionTool extends DrawingTool
{

	protected Point origin = new Point();

	private boolean dragSelection = false;

	// TODO these variables are bad design. need to rework the delete stuff
	private GraphLink delLink = null;

	private GraphBlock delBlock = null;

	private boolean promptOn = false;

	public SelectionTool()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		cursor = toolkit.createCustomCursor(toolkit.createImage(Hub
				.getResource("images/cursors/modify_.gif")),
				new Point(0, 0),
				"SELECT");
	}

	@Override
	public void handleMouseDragged(MouseEvent m)
	{
		if (m.getButton() != MouseEvent.BUTTON1)
		{
			return;
		}
		super.handleMouseDragged(m);
		updateDragDisplay(new Rectangle(Math.min(origin.x, m.getX()), Math
				.min(origin.y, m.getY()), Math.abs(m.getX() - origin.x), Math
				.abs(m.getY() - origin.y)));
		context.repaint();
	}

	@Override
	public void handleMousePressed(MouseEvent m)
	{
		if (m.getButton() != MouseEvent.BUTTON1)
		{
			return;
		}
		super.handleMousePressed(m);
		origin = m.getPoint();
		context.setSelectionArea(null);
		if (!intersectsSelection(m.getPoint()))
		{
			context.clearSelection();
			dragSelection = false;
			// for (GraphBlock b : ((TemplateGraph)context.getLayoutShell())
			// .getBlocks())
			// {
			// if (b.bounds().contains(m.getPoint()))
			// {
			// updateDragDisplay(new Rectangle(m.getPoint()));
			// dragSelection = true;
			// break;
			// }
			// }
		}
		else
		{
			dragSelection = true;
		}
		if (dragSelection)
		{
			context.setTool(DesignDrawingView.MOVEMENT_TOOL);
			context.getTool().handleMousePressed(m);
		}
		context.repaint();
	}

	@Override
	public void handleMouseReleased(MouseEvent m)
	{
		if (m.getButton() != MouseEvent.BUTTON1)
		{
			return;
		}
		updateDragDisplay(new Rectangle(Math.min(origin.x, m.getX()), Math
				.min(origin.y, m.getY()), Math.abs(m.getX() - origin.x), Math
				.abs(m.getY() - origin.y)));
		context.setSelectionArea(null);
		context.repaint();
	}

	@Override
	public void handleMouseMoved(MouseEvent m)
	{
		if (promptOn)
		{
			return;
		}
		context.setTool(DesignDrawingView.LINK_TOOL);
		context.getTool().handleMouseMoved(m);
	}

	@Override
	public void handleMouseClicked(MouseEvent m)
	{
		if (m.getButton() == MouseEvent.BUTTON3)
		{
			Point2D.Float mp = context.localToScreen(new Point2D.Float(
					m.getX(),
					m.getY()));
			Point p = new Point((int)mp.x, (int)mp.y);
			GraphBlock selectedB = context.getBlockAt(m.getPoint());
			GraphLink selectedL = context.getLinkAt(m.getPoint());
			if (selectedB != null)
			{
				promptOn = true;
				context.clearSelection();
				delBlock = selectedB;
				delBlock.setHighlighted(true);
				JPopupMenu delMenu = new JPopupMenu();
				JMenuItem delItem = new JMenuItem(Hub.string("delete"));
				delItem.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (delBlock != null)
						{
							// ((TemplateGraph)context.getLayoutShell())
							// .remove(delBlock);
							delBlock = null;
							promptOn = false;
						}
					}
				});
				delMenu.addPopupMenuListener(new PopupMenuListener()
				{
					public void popupMenuCanceled(PopupMenuEvent e)
					{
						if (delBlock != null)
						{
							delBlock.setHighlighted(false);
						}
						delBlock = null;
						if (delLink != null)
						{
							delLink.setHighlighted(false);
						}
						delLink = null;
						promptOn = false;
					}

					public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
					{
						if (delBlock != null)
						{
							delBlock.setHighlighted(false);
						}
						if (delLink != null)
						{
							delLink.setHighlighted(false);
						}
					}

					public void popupMenuWillBecomeVisible(PopupMenuEvent e)
					{
					}
				});
				delMenu.add(delItem);
				JMenuItem openItem = new JMenuItem(Hub.string("open"));
				openItem.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (delBlock != null)
						{
							Hub.getWorkspace().addModel(delBlock
									.getBlock().getFSA());
							Hub.getWorkspace().setActiveModel(delBlock
									.getBlock().getFSA().getName());
							delBlock = null;
							promptOn = false;
						}
					}
				});
				delMenu.add(openItem);
				delMenu.show(context, p.x, p.y);
			}
			else if (selectedL != null)
			{
				promptOn = true;
				context.clearSelection();
				delLink = selectedL;
				delLink.setHighlighted(true);
				JPopupMenu delMenu = new JPopupMenu();
				JMenuItem delItem = new JMenuItem(Hub.string("delete"));
				delItem.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (delLink != null)
						{
							// ((TemplateGraph)context.getLayoutShell())
							// .remove(delLink);
							delLink = null;
							promptOn = false;
						}
					}
				});
				delMenu.addPopupMenuListener(new PopupMenuListener()
				{
					public void popupMenuCanceled(PopupMenuEvent e)
					{
						if (delBlock != null)
						{
							delBlock.setHighlighted(false);
						}
						delBlock = null;
						if (delLink != null)
						{
							delLink.setHighlighted(false);
						}
						delLink = null;
						promptOn = false;
					}

					public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
					{
						if (delBlock != null)
						{
							delBlock.setHighlighted(false);
						}
						if (delLink != null)
						{
							delLink.setHighlighted(false);
						}
					}

					public void popupMenuWillBecomeVisible(PopupMenuEvent e)
					{
					}
				});
				delMenu.add(delItem);
				delMenu.show(context, p.x, p.y);
			}
			context.repaint();
		}
		else if (m.getButton() == MouseEvent.BUTTON1 && m.getClickCount() == 2)
		{
			GraphBlock selected = context.getBlockAt(m.getPoint());
			if (selected != null)
			{
				promptOn = true;
				// BlockLabellingDialog.showAndLabel(context,
				// (TemplateGraph)context.getLayoutShell(),
				// selected,
				// this);
			}
		}
	}

	public void promptOff()
	{
		promptOn = false;
	}

	protected boolean intersectsSelection(Point p)
	{
		for (GraphBlock b : context.getSelection())
		{
			if (b.bounds().contains(p))
			{
				return true;
			}
		}
		return false;
	}

	protected void updateDragDisplay(Rectangle selectionArea)
	{
		context.clearSelection();
		// for (GraphBlock b : ((TemplateGraph)context.getLayoutShell())
		// .getBlocks())
		// {
		// // second condition is for cases when area has dimensions 0
		// if (selectionArea.intersects(b.bounds())
		// || b.bounds().contains(selectionArea.getLocation()))
		// {
		// context.addToSelection(b);
		// }
		// }
		context.setSelectionArea(selectionArea);
	}
}
