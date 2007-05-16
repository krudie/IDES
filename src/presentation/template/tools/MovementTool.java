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
	
	protected Point origin=new Point();
	
	public MovementTool()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		cursor = toolkit.createCustomCursor(toolkit.createImage(Hub.getResource("images/cursors/move.gif")), new Point(0,0), "MOVE");		
	}
	
	public void handleMouseDragged(MouseEvent m)
	{
		super.handleMouseDragged(m);
		int offX=m.getX()-origin.x;
		int offY=m.getY()-origin.y;
		origin=m.getPoint();
		for(GraphBlock b:context.getSelection())
		{
//			((TemplateGraph)context.getLayoutShell()).
			b.translate(offX, offY);
		}
		context.repaint();
	}
	
	public void handleMousePressed(MouseEvent m)	
	{
		super.handleMousePressed(m);
		origin=m.getPoint();
	}
	
	public void handleMouseReleased(MouseEvent m)
	{
		((TemplateGraph)context.getLayoutShell()).commitRelocate(context.getSelection());
		context.setTool(DesignDrawingView.SELECTION_TOOL);
	}
}
