package presentation.fsa;

import java.awt.geom.Point2D;
import java.util.HashMap;

import presentation.GraphicalLayout;

public class NodeLayout extends GraphicalLayout {
	
	private float radius;		
	private Point2D.Float arrow = new Point2D.Float(1, 0); // the direction vector for arrow if the state is initial
	private CircleNode node;
	
	/**
     * The default radius of the circle which represents this Node, and the
     * fixed distance between outer and inner circles for marked Nodes.
     */    
	public static final float DEFAULT_RADIUS = 15, RDIF = 4;

	protected FSAGraph.UniformRadius uniformR=null;
	
	public NodeLayout(FSAGraph.UniformRadius u){
		this(u,new Point2D.Float(), DEFAULT_RADIUS, "");
	}
	
	public NodeLayout(FSAGraph.UniformRadius u,Point2D.Float centre){
		this(u,centre, DEFAULT_RADIUS, "");
	}
	
	public NodeLayout(FSAGraph.UniformRadius u,Point2D.Float centre, float radius, String name, Point2D.Float arrow) {
		this(u,centre, radius, name);
		this.arrow = arrow;		
	}
	
	public NodeLayout(FSAGraph.UniformRadius u,Point2D.Float centre, float radius, String name) {
		super(centre, name);
		this.radius = radius;		
		uniformR=u;
		uniformR.updateUniformRadius(this,radius);
	}

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
		if(node!=null)
			node.setDirty(d);
	}
	
	public void dispose()
	{
		if(uniformR!=null)
		{
			uniformR.remove(this);
			uniformR.updateUniformRadius();
		}
	}
	
	public void setUniformRadius(FSAGraph.UniformRadius ur)
	{
		if(uniformR!=null)
			uniformR.remove(uniformR.get(this));
		uniformR=ur;
		uniformR.updateUniformRadius(this,radius);
	}
}
