package presentation.template;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import presentation.template.tools.DrawingTool;
import presentation.template.tools.MovementTool;

public class DesignDrawingView extends DesignView implements MouseListener, MouseMotionListener, KeyListener {

	protected DrawingTool currentTool;
	protected DrawingTool moveTool=new MovementTool();
	
	public DesignDrawingView(TemplateGraph tg)
	{
		super(tg);
		moveTool.setContext(this);
		currentTool=moveTool;
		addMouseListener(this);
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
}
