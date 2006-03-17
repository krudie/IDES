package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import javax.swing.JPanel;

/**
 * The area in which users draw graphs and in which 
 * stored graphs are displayed. 
 * 
 * @author helen
 *
 */
public class DrawingBoard extends JPanel implements ActionListener, KeyListener, MouseListener {

	/**
	 * ??? What does this mean? 
	 */
	private static final long serialVersionUID = 1L;
	
	public DrawingBoard() {
		addMouseListener(this);
	}
	
	
	public void paint(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		
		Ellipse2D.Double circle =
	        new Ellipse2D.Double(x, y, radius*2, radius*2);
		g2d.draw(circle);
		
	    GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
	    
	    path.moveTo(20.0f,50.0f);  // first point
	    path.lineTo(0.0f,125.0f);  // straight line
	    path.quadTo(100.0f,100.0f,225.0f,125.0f);  // quadratic curve
	    path.curveTo(260.0f,100.0f,130.0f,50.0f,225.0f,0.0f);  // cubic (bezier) curve (ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2) starts at current point
	    path.closePath();
	    
	    AffineTransform at = new AffineTransform();
	    at.setToRotation(-Math.PI/8.0);
	    g2d.transform(at);
	    at.setToTranslation(0.0f,150.0f);
	    g2d.transform(at);
	    g2d.setColor(Color.blue);
	    g2d.setStroke(new BasicStroke(3));
	                  
	    // g2d.fill(path);
	    g2d.draw(path);	
	}


	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		x = arg0.getX();
		y = arg0.getY();
		this.paint(this.getGraphics());
	}


	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	// Current state information
	private int x, y;
	
	// Graph configuration data
	private int radius = 5;
}
