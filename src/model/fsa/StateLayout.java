package model.fsa;

import java.awt.Point;

import presentation.GraphicalLayout;

public class StateLayout implements GraphicalLayout {

	private int radius;
	private Point centre;
	private String name;
	private Point arrow; // the direction vector for arrow if the state is initial
	
	
	public StateLayout(Point centre, int radius, String name, Point arrow) {
		this(centre, radius, name);
		this.arrow = arrow;		
	}
	
	public StateLayout(Point centre, int radius, String name) {
		this.centre = centre;
		this.radius = radius;
		this.name = name;
		arrow = null;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public Point getLocation() {		
		return centre;
	}	

	public String getText() {		
		return name;
	}
	
	public Point getArrow(){		
		return arrow;
	}

}
