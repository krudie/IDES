package presentation.fsa;

import java.awt.Color;
import java.awt.Point;

import presentation.GraphicalLayout;

public class StateLayout extends GraphicalLayout {
	
	private int radius;		
	private Point arrow; // the direction vector for arrow if the state is initial
	
	
	public StateLayout(Point centre, int radius, String name, Point arrow) {
		this(centre, radius, name);
		this.arrow = arrow;		
	}
	
	public StateLayout(Point centre, int radius, String name) {
		super(centre, name);
		this.radius = radius;		
		arrow = null;
	}
	
	public int getRadius() {
		return radius;
	}	
	
	public Point getArrow(){		
		return arrow;
	}	
}
