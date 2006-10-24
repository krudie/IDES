package presentation.fsa;

import java.awt.geom.Point2D;

import presentation.GraphicalLayout;

/**
 * Graphical layout data required to display a circular node.  
 * 
 * @author Helen Bretzke
 */
public class CircleNodeLayout extends GraphicalLayout {
	
	/** the radius of the circle */
	private float radius;		
	
	/** the direction vector for arrow if the state is initial */
	private Point2D.Float arrow = new Point2D.Float(1, 0);
	
	/** the node that is laid out */
	private CircleNode node;
	
	/**
     * The default radius of the circle which represents this Node.
     */    
	public static final float DEFAULT_RADIUS = 15;
	/**
	 * The fixed margin between outer and inner circles for marked Nodes.
	 */
	public static final float RADIUS_MARGIN = 4;

	/**
	 * Keeps track of the maximum radius over all nodes in the graph. 
	 */
	protected FSAGraph.UniformRadius uniformR=null;
	
	
	public CircleNodeLayout(FSAGraph.UniformRadius u){
		this(u,new Point2D.Float(), DEFAULT_RADIUS, "");
	}
	
	public CircleNodeLayout(FSAGraph.UniformRadius u,Point2D.Float centre){
		this(u,centre, DEFAULT_RADIUS, "");
	}
	
	public CircleNodeLayout(FSAGraph.UniformRadius u,Point2D.Float centre, float radius, String name, Point2D.Float arrow) {
		this(u,centre, radius, name);
		this.arrow = arrow;		
	}
	
	public CircleNodeLayout(FSAGraph.UniformRadius u,Point2D.Float centre, float radius, String name) {
		super(centre, name);
		this.radius = radius;		
		uniformR=u;
		uniformR.updateUniformRadius(this,radius);
	}

	public CircleNodeLayout(){
		this(new Point2D.Float(), DEFAULT_RADIUS, "");
	}
	
	public CircleNodeLayout(Point2D.Float centre){
		this(centre, DEFAULT_RADIUS, "");
	}
	
	public CircleNodeLayout(Point2D.Float centre, float radius, String name, Point2D.Float arrow) {
		this(centre, radius, name);
		this.arrow = arrow;		
	}
	
	public CircleNodeLayout(Point2D.Float centre, float radius, String name) {
		super(centre, name);
		this.radius = radius;		
	}

	public float getRadius() {
		if(uniformR!=null&&GraphDrawingView.isUniformNodes())
			return uniformR.getRadius();
		return radius;
	}	
	
	public Point2D.Float getArrow(){		
		return arrow;
	}

	public void setArrow(Point2D.Float arrow) {
		this.arrow = arrow;
		setDirty(true);
	}
		
	public void setRadius(float radius) {
		this.radius = radius;
		setDirty(true);
		if(uniformR!=null)
			uniformR.updateUniformRadius(this,radius);
	}

	/**
	 * @param node
	 */
	public void setNode(CircleNode node) {
		this.node = node;		
	}	
	
	public void setDirty(boolean d){
		// KLUGE all accesss to NodeLayout should go through the Node interface.
		super.setDirty(d);
		if( node != null ) {
			node.setNeedsRefresh(d);
		}
	}
	
	public void dispose()
	{
		if(uniformR!=null) {
			uniformR.remove(this);
			uniformR.updateUniformRadius();
		}
	}
	
	public void setUniformRadius(FSAGraph.UniformRadius ur)
	{
		if(uniformR != null) {
			uniformR.remove(uniformR.get(this));
		}
		uniformR = ur;
		uniformR.updateUniformRadius(this,radius);
	}
}
