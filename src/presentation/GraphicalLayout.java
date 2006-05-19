package presentation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.Stroke;

public class GraphicalLayout {

	public static final Color DEFAULT_COLOR = Color.BLACK;
	public static final Color DEFAULT_HIGHLIGHT_COLOR = Color.RED;
	public static final Color DEFAULT_SELECTION_COLOR = Color.BLUE;
	
	public static final Stroke FINE_STROKE = new BasicStroke(1);
	public static final Stroke WIDE_STROKE = new BasicStroke(2);
	public static final Stroke DASHED_STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT,
																BasicStroke.JOIN_MITER,         
																50, new float[] {5, 2}, 0);	
	private Point2D.Float location;
	private String text;
	private Color color = DEFAULT_COLOR;
	private Color highlightColor = DEFAULT_HIGHLIGHT_COLOR;
	private Color selectionColor = DEFAULT_SELECTION_COLOR;	
	
	public GraphicalLayout(){
		this("");		
	}
	
	public GraphicalLayout(String text)	{
		this.text = text;
		location = new Point2D.Float();	
	}
	
	public GraphicalLayout(Point2D.Float location) {
		this(location, "");
	}

	public GraphicalLayout(Point2D.Float location, String text) {
		this.location = location;
		this.text = text;
	}

	public GraphicalLayout(Point2D.Float location, String text, Color color, Color highlightColor) {
		this.location = location;
		this.text = text;
		this.color = color;
		this.highlightColor = highlightColor;
	}

	public Point2D.Float getLocation(){
		return location;
	}

	public void setLocation(float x, float y) {
		location.setLocation(x,y);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getHighlightColor() {
		return highlightColor;
	}

	public void setHighlightColor(Color highlightColor) {
		this.highlightColor = highlightColor;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public Color getSelectionColor() {
		return selectionColor;
	}

	public void setSelectionColor(Color selectionColor) {
		this.selectionColor = selectionColor;
	}
	
	
}
