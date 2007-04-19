package presentation.template.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Collection;

import main.Hub;
import model.template.TemplateModule;
import model.template.ver2_1.Module;

import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.ToolPopup;
import presentation.template.DesignDrawingView;
import presentation.template.GraphBlock;
import presentation.template.TemplateGraph;

public class MovementTool extends DrawingTool {
	
	
// Dragging flag -- set to true when user presses mouse button
// and cleared to false when user releases mouse button.
	protected boolean dragging = false;
	
	public MovementTool()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		cursor = toolkit.createCustomCursor(toolkit.createImage(Hub.getResource("images/cursors/modify_.gif")), new Point(0,0), "MOVE");		
	}
	
	public void handleMouseClicked(MouseEvent m)
	{
		super.handleMouseClicked(m);
		((TemplateGraph)context.getLayoutShell()).createModule(new Point2D.Float(m.getX(),m.getY()));
	}
	
	public void handleMouseMoved(MouseEvent m)
	{
		super.handleMouseMoved(m);
		Collection<GraphBlock> blocks=((TemplateGraph)context.getLayoutShell()).getBlocks();
		for(GraphBlock b:blocks)
		{
			if(b.intersects(m.getPoint()))
			{
				b.setHighlighted(true);
			}
			else
			{
				b.setHighlighted(false);
			}
		}
		context.repaint();
	}
}
