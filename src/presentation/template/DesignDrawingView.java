package presentation.template;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
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
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;

import model.template.TemplateChannel;
import model.template.TemplateModule;

import presentation.GraphicalLayout;
import presentation.template.tools.DrawingTool;
import presentation.template.tools.LinkTool;
import presentation.template.tools.MovementTool;
import presentation.template.tools.SelectionTool;

public class DesignDrawingView extends DesignView implements MouseListener, MouseMotionListener, KeyListener {

	public static final int MOVEMENT_TOOL=0;
	public static final int SELECTION_TOOL=1;
	public static final int LINK_TOOL=2;
	
	protected int currentTool;
	protected DrawingTool[] drawingTools=new DrawingTool[]
	                        {new MovementTool(),
	                        new SelectionTool(),
	                        new LinkTool()
	                        };
	
	protected Collection<GraphBlock> selection=new LinkedList<GraphBlock>();
	private Rectangle selectionArea=null;
	
	private GraphBlock linkStart=null;
	private Point linkEnd=null;
	
	protected class DropHandler extends DropTargetAdapter
	{
		public void drop(DropTargetDropEvent dtde)
		{
			try
			{
				String trans=(String)dtde.getTransferable().getTransferData(DataFlavor.stringFlavor);
				Point2D.Float loc=new Point2D.Float();
				loc.x=dtde.getLocation().x;
				loc.y=dtde.getLocation().y;
				if("".equals(trans))
					return;
				if(trans.charAt(0)=='M')
				{
					graph.createModule(trans.substring(1),loc);
				}
				else
				{
					graph.createChannel(trans.substring(1),loc);
				}
			}catch (Exception e){
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}
	
	public DesignDrawingView(TemplateGraph tg)
	{
		super(tg);
		scaleFactor = 1f;
		scaleToFit=false;
		for(int i=0;i<drawingTools.length;++i)
		{
			drawingTools[i].setContext(this);
		}
		setTool(SELECTION_TOOL);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		setDropTarget(new DropTarget(this,new DropHandler()));
	}
	
	public JComponent getGUI()
	{
		JScrollPane sp = new JScrollPane(this, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		return sp;
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		Graphics2D g2d=(Graphics2D)g;
		if(selectionArea!=null)
		{
			g2d.setStroke(GraphicalLayout.DASHED_STROKE);
			g2d.setColor(Color.DARK_GRAY);
			g2d.draw(selectionArea);
		}
		if(linkStart!=null&&linkEnd!=null)
		{
			g2d.setStroke(GraphicalLayout.WIDE_STROKE);
			g2d.setColor(Color.BLACK);
			g2d.drawLine((int)linkStart.getLocation().x,
					(int)linkStart.getLocation().y,
					linkEnd.x, linkEnd.y);
		}
	}
	
	public void setTool(int toolIdx)
	{
		currentTool=toolIdx;
		setCursor(drawingTools[currentTool].getCursor());
	}
	
	public DrawingTool getTool()
	{
		return drawingTools[currentTool];
	}
	
	 public void mouseClicked(MouseEvent e)
	 {
		 e=transformMouseCoords(e);
		 drawingTools[currentTool].handleMouseClicked(e);
	 }
	 
	 public void mouseEntered(MouseEvent e)
	 {
	 }
	 
	 public void mouseExited(MouseEvent e)
	 {
	 }
	 
	 public void mousePressed(MouseEvent e)
	 {
		 e=transformMouseCoords(e);
		 drawingTools[currentTool].handleMousePressed(e); 
	 }
	 
	 public void mouseReleased(MouseEvent e)
	 {
		 e=transformMouseCoords(e);
		 drawingTools[currentTool].handleMouseReleased(e);
	 }
	 
	 public void mouseDragged(MouseEvent e)
	 {
		 e=transformMouseCoords(e);
		 drawingTools[currentTool].handleMouseDragged(e); 
	 }
	 
	 public void mouseMoved(MouseEvent e)
	 {
		 e=transformMouseCoords(e);
		 drawingTools[currentTool].handleMouseMoved(e);		 
	 }
	 
	 public void keyPressed(KeyEvent e)
	 {
		 
	 }

	 public void keyReleased(KeyEvent e)
	 {
		 
	 }

	 public void keyTyped(KeyEvent e)
	 {
		 drawingTools[currentTool].handleKeyTyped(e);
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
	
	public Collection<GraphBlock> getSelection()
	{
		return selection;
	}
	
	public void clearSelection()
	{
		for(GraphBlock b:selection)
		{
			b.setSelected(false);
		}
		selection.clear();
	}
	
	public void addToSelection(GraphBlock b)
	{
		b.setSelected(true);
		selection.add(b);
	}
	
	public void setSelectionArea(Rectangle r)
	{
		if(r!=null&&r.width==0&&r.height==0)
		{
			r=null;
		}
		selectionArea=r;
	}

	public void link(GraphBlock b,Point p)
	{
		linkStart=b;
		linkEnd=p;
	}
	
	public Point2D.Float localToScreen(Point2D.Float p)
	{
		Point2D.Float r=(Point2D.Float)p.clone();
		r.x=r.x*scaleFactor;
		r.y=r.y*scaleFactor;
		return r;
	}
}
