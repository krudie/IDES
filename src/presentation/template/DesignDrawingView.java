package presentation.template;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;

import presentation.template.tools.DrawingTool;
import presentation.template.tools.MovementTool;

public class DesignDrawingView extends DesignView implements MouseListener, MouseMotionListener, KeyListener {

	protected DrawingTool currentTool;
	protected DrawingTool moveTool=new MovementTool();
	
	protected class DropHandler extends DropTargetAdapter
	{
		public void drop(DropTargetDropEvent dtde)
		{
			try
			{
			System.out.println((String)dtde.getTransferable().getTransferData(DataFlavor.stringFlavor)+
					" "+dtde.getLocation());
			}catch (Exception e){}
		}
	}
	
	public DesignDrawingView(TemplateGraph tg)
	{
		super(tg);
		scaleFactor = 1f;
		scaleToFit=false;
		moveTool.setContext(this);
		currentTool=moveTool;
		addMouseListener(this);
		addMouseMotionListener(this);
		setDropTarget(new DropTarget(this,new DropHandler()));
	}
	
	public JComponent getGUI()
	{
		JScrollPane sp = new JScrollPane(this, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		return sp;
	}
	
	 public void mouseClicked(MouseEvent e)
	 {
		 e=transformMouseCoords(e);
		 currentTool.handleMouseClicked(e);
	 }
	 
	 public void mouseEntered(MouseEvent e)
	 {
		 
	 }
	 
	 public void mouseExited(MouseEvent e)
	 {
		 
	 }
	 
	 public void mousePressed(MouseEvent e)
	 {
		 
	 }
	 
	 public void mouseReleased(MouseEvent e)
	 {
		 
	 }
	 
	 public void mouseDragged(MouseEvent e)
	 {
		 
	 }
	 
	 public void mouseMoved(MouseEvent e)
	 {
		 e=transformMouseCoords(e);
		 currentTool.handleMouseMoved(e);		 
	 }
	 
	 public void keyPressed(KeyEvent e)
	 {
		 
	 }

	 public void keyReleased(KeyEvent e)
	 {
		 
	 }

	 public void keyTyped(KeyEvent e)
	 {
		 
	 }
	
	protected MouseEvent transformMouseCoords(MouseEvent e)
	{
		Point2D.Float p=new Point2D.Float(e.getX(),e.getY());
		p=screenToLocal(p);
		return new MouseEvent(
				(Component)e.getSource(),
				e.getID(),
				e.getWhen(),
				e.getModifiersEx(),
				(int)p.x,
				(int)p.y,
				e.getClickCount(),
				e.isPopupTrigger(),
				e.getButton()
				);
	}
	
	public Point2D.Float screenToLocal(Point2D.Float p)
	{
		Point2D.Float r=(Point2D.Float)p.clone();
		r.x=r.x/scaleFactor;
		r.y=r.y/scaleFactor;
		return r;
	}
}
