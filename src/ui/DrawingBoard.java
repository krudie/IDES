package ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

import presentation.Glyph;
import presentation.GlyphLabel;
import ui.listeners.DrawingBoardListener;

/**
 * The area in which users draw graphs and in which 
 * stored graphs are displayed. 
 * 
 * @author helen
 *
 */
public class DrawingBoard extends JPanel {

	/**
	 * ??? What is this variable? 
	 */
	private static final long serialVersionUID = 1L;
	
	public DrawingBoard() {
		addMouseListener(new DrawingBoardListener());
	}
	
	
	public void paint(Graphics g){
		Graphics2D g2d = (Graphics2D)g;

		// Warning: scales distance from origin as well as size of nodes
		// we want to scale everything from the centre of the user's view.
		// Solution: translate origin before scaling and beware of op precendence.
		g2d.translate(-(this.getWidth()/2), -(this.getHeight()/2));
		g2d.scale(2.0f, 2.0f);

		// One day this will grow up to be a node.
		Ellipse2D.Double circle =
	        new Ellipse2D.Double(p.getX(), p.getY(), radius*3, radius*3);
		g2d.draw(circle);

		// NOTE This type extends Component and could be inserted in the panel
		// if we used an absolute layout manager.
		// Then we could use geComponentAt(Point) to get the selected glyph in
		// one call (use the container as the presentation model's DS).
		// Then would have to make all glyphs extend Component (heavy).
		Glyph glyph = new GlyphLabel("(" + (int)p.getX() + "," + (int)p.getY() + ")", p);
		glyph.draw(g2d);
		
		/*	        
	    AffineTransform at = new AffineTransform();
	    at.setToRotation(-Math.PI/8.0);
	    g2d.transform(at);
	    at.setToTranslation(0.0f,150.0f);
	    g2d.transform(at);
	    g2d.setColor(Color.blue);
	    g2d.setStroke(new BasicStroke(3));
	                  
	    // g2d.fill(path);
	    g2d.draw(path);
	    */
		
	}

	/**
	 * TODO
	 * 
	 * switch on interaction mode
	 * get Glyph(s)/Components that intersect with the point
	 * update the presentation model and abstract model (see Observer pattern)
	 * update the view (paint)
	 * 		
	 * @param p
	 */
	public void setPoint(Point p){
		
		this.p = p;
		paint(this.getGraphics());
	}
	
	// TODO move to UIStateModel
	// Current state information
	private Point p = new Point();
	
	// Graph configuration data
	private int radius = 10;
}
