package presentation.template;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import presentation.GraphicalLayout;

public class BlockLayout extends GraphicalLayout {
	protected float width;
	protected float height;
	
	public BlockLayout(){
		super();		
	}
	
	/** 
	 * Creates a graphical layout instance with the given 
	 * string as label text.
	 * 
	 * @param text the text to display on the label
	 */	
	public BlockLayout(String text)	{
		super(text);
	}
	
	/** 
	 * Creates a graphical layout instance at the given location
	 * with empty string as text.
	 * 	
	 * @param location
	 */
	public BlockLayout(Point2D.Float location) {
		super(location);
	}

	/**
 	 * Creates a graphical layout instance at the given location
 	 * and the given string as label text.
 	 * 
	 * @param location
	 * @param text
	 */
	public BlockLayout(Point2D.Float location, String text) {
		super(location,text);
	}

	/**
	 * Creates a graphical layout instance at the given location,
 	 * with the given label text, colour and highlight colour. 
	 * 
	 * @param location
	 * @param text
	 * @param color
	 * @param highlightColor
	 */
	public BlockLayout(Point2D.Float location, String text, Color color, Color highlightColor) {
		super(location,text,color,highlightColor);
	}
	
	public float getWidth()
	{
		return width;
	}
	
	public float getHeight()
	{
		return height;
	}
	
	public Rectangle getBounds()
	{
		return new Rectangle((int)(getLocation().x-width/2),
				(int)(getLocation().y-height/2),(int)width,(int)height);
	}
}
