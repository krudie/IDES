package presentation.template.tools;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Collection;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import presentation.template.DesignDrawingView;
import presentation.template.GraphBlock;
import presentation.template.TemplateGraph;

import main.Hub;
import model.fsa.FSAEvent;
import model.fsa.FSAEventSet;

public class LinkTool extends DrawingTool implements ActionListener {

	private class PopupDelayer implements Runnable
	{
		private Point p;
		private boolean cancelled=false;
		public PopupDelayer(MouseEvent e)
		{
			p=e.getPoint();
		}
		public void run()
		{
			synchronized(this)
			{
				try
				{
					this.wait(500);
				}catch(InterruptedException e){}
				if(!cancelled&&popup!=null)
				{
					popup.show(context,p.x,p.y);
					context.repaint();
				}
			}
		}
		public void cancel()
		{
			synchronized(this)
			{
				cancelled=true;
				this.notifyAll();
			}
		}
		public Point getOrigin()
		{
			return p;
		}
	}
	private PopupDelayer menuThread=null;
	
	private JPopupMenu popup;
	private GraphBlock leftBlock;
	private FSAEvent leftEvent;
	private boolean isLinking=false;
	
	public LinkTool()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		cursor = toolkit.createCustomCursor(toolkit.createImage(Hub.getResource("images/cursors/modify_.gif")), new Point(0,0), "MANAGE_LINKS");		
	}
	
	public void handleMouseMoved(MouseEvent m)
	{
		super.handleMouseMoved(m);
		if(menuThread!=null)
		{
			menuThread.cancel();
			menuThread=null;
		}
		GraphBlock selected=context.getBlockAt(m.getPoint());
//		Collection<GraphBlock> blocks=((TemplateGraph)context.getLayoutShell()).getBlocks();
//		for(GraphBlock b:blocks)
//		{
//			if(b.intersects(m.getPoint()))
//			{
//				selected=b;
//				break;
//			}
//		}
		if(selected!=null)
		{
			FSAEventSet events=selected.getBlock().getInterfaceEvents();
			popup=new JPopupMenu();
			if(events.isEmpty())
			{
				JMenuItem item=new JMenuItem(Hub.string("noIfaceEvents"));
				item.setEnabled(false);
				popup.add(item);
			}
			else
			{
				for(FSAEvent e:events)
				{
					JMenuItem item=new JMenuItem(e.getSymbol());
					item.addActionListener(this);
					popup.add(item);
				}
			}
			menuThread=new PopupDelayer(m);
			new Thread(menuThread).start();
		}
		else
		{
			if(popup!=null)
			{
				popup.setVisible(false);
				popup=null;
			}
		}
		if(isLinking)
		{
			context.link(leftBlock, m.getPoint());
			context.repaint();
		}
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if(menuThread==null)
		{
			return;
		}
		GraphBlock block=context.getBlockAt(
						new Point2D.Float(menuThread.getOrigin().x,
								menuThread.getOrigin().y));
		if(block==null)
		{
			return;
		}
		FSAEvent event=null;
		for(FSAEvent e:block.getBlock().getFSA().getEventSet())
		{
			if(ae.getActionCommand().equals(e.getSymbol()))
			{
				event=e;
				break;
			}
		}
		if(!isLinking)
		{
			isLinking=true;
			leftBlock=block;
			leftEvent=event;
		}
		else
		{
			cancelLink();
			((TemplateGraph)context.getLayoutShell()).createLink(
					leftBlock, leftEvent, block, event);
		}
	}
	
	public void handleMouseClicked(MouseEvent m)
	{
		if(menuThread!=null)
		{
			menuThread.cancel();
		}
		cancelLink();
		context.setTool(DesignDrawingView.SELECTION_TOOL);
		context.getTool().handleMouseClicked(m);
	}
	
	public void handleMouseDragged(MouseEvent m)
	{
		if(menuThread!=null)
		{
			menuThread.cancel();
		}
		cancelLink();
		context.setTool(DesignDrawingView.SELECTION_TOOL);
		context.getTool().handleMouseDragged(m);
	}
	
	
	public void handleMousePressed(MouseEvent m)	
	{
		if(menuThread!=null)
		{
			menuThread.cancel();
		}
		cancelLink();
		context.setTool(DesignDrawingView.SELECTION_TOOL);
		context.getTool().handleMousePressed(m);	
	}
	
	public void handleMouseReleased(MouseEvent m)
	{
		if(menuThread!=null)
		{
			menuThread.cancel();
		}
		cancelLink();
		context.setTool(DesignDrawingView.SELECTION_TOOL);
		context.getTool().handleMouseReleased(m);
	}
	
	public void handleRightClick(MouseEvent m){
		if(menuThread!=null)
		{
			menuThread.cancel();
		}
		cancelLink();
		context.setTool(DesignDrawingView.SELECTION_TOOL);
		context.getTool().handleRightClick(m);
	}
	
	protected void cancelLink()
	{
		isLinking=false;
		context.link(null, null);
		context.repaint();
	}
	
	public void handleKeyTyped(KeyEvent ke){
		super.handleKeyTyped(ke);
		if(ke.getKeyChar()==KeyEvent.VK_ESCAPE)
		{
			cancelLink();
		}
	}
}
