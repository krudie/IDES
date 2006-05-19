package presentation.fsa;

import java.awt.geom.Point2D;

import presentation.GraphicalLayout;

public class NodeLayout extends GraphicalLayout {
	
	private float radius;		
	private Point2D.Float arrow; // the direction vector for arrow if the state is initial
	
	/**
     * The default radius of the circle which represents this Node, and the
     * fixed distance between outer and inner circles for marked Nodes.
     */    
	public static final float DEFAULT_RADIUS = 15, RDIF = 4;;
		
	public NodeLayout(){
		this(new Point2D.Float(), DEFAULT_RADIUS, "");
	}
	
	public NodeLayout(Point2D.Float centre){
		this(centre, DEFAULT_RADIUS, "");
	}
	
	public NodeLayout(Point2D.Float centre, float radius, String name, Point2D.Float arrow) {
		this(centre, radius, name);
		this.arrow = arrow;		
	}
	
	public NodeLayout(Point2D.Float centre, float radius, String name) {
		super(centre, name);
		this.radius = radius;		
		arrow = new Point2D.Float();
	}
	
	public float getRadius() {
		return radius;
	}	
	
	public Point2D.Float getArrow(){		
		return arrow;
	}	
}
