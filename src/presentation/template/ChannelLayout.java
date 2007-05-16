package presentation.template;

import java.awt.Color;
import java.awt.geom.Point2D;

public class ChannelLayout extends BlockLayout {

	public ChannelLayout(){
		super();		
	}
	
	/** 
	 * Creates a graphical layout instance with the given 
	 * string as label text.
	 * 
	 * @param text the text to display on the label
	 */	
	public ChannelLayout(String text)	{
		super(text);
	}
	
	/** 
	 * Creates a graphical layout instance at the given location
	 * with empty string as text.
	 * 	
	 * @param location
	 */
	public ChannelLayout(Point2D.Float location) {
		super(location);
	}

	/**
 	 * Creates a graphical layout instance at the given location
 	 * and the given string as label text.
 	 * 
	 * @param location
	 * @param text
	 */
	public ChannelLayout(Point2D.Float location, String text) {
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
	public ChannelLayout(Point2D.Float location, String text, Color color, Color highlightColor) {
		super(location,text,color,highlightColor);
	}
	
	public void recomputeBounds()
	{
	}
}
