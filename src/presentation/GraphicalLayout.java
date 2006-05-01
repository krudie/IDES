package presentation;

import java.awt.Color;
import java.awt.Point;

public abstract class GraphicalLayout {
		
	private Point location;
	private String text;
	private Color color = Color.BLACK;
	private Color highlightColor = Color.RED;
	
	public GraphicalLayout(){
		this(null);		
	}
	
	public GraphicalLayout(Point location) {
		this.location = location;
		text = "";
	}

	public GraphicalLayout(Point location, String text) {
		this.location = location;
		this.text = text;
	}

	public GraphicalLayout(Point location, String text, Color color, Color highlightColor) {
		this.location = location;
		this.text = text;
		this.color = color;
		this.highlightColor = highlightColor;
	}

	public Point getLocation(){
		return location;
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

	public void setLocation(Point location) {
		this.location = location;
	}
	
	
}
