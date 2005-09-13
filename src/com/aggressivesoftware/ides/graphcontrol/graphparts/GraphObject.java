package com.aggressivesoftware.ides.graphcontrol.graphparts;

import com.aggressivesoftware.geometric.Point;
import com.aggressivesoftware.ides.GraphingPlatform;
import com.aggressivesoftware.ides.graphcontrol.GraphModel;

/**
 * GraphObject is the parent class for both Nodes and Edges.
 * The primary reason for this abstraction is the common "attribute" handleing between the two.
 * It also provides some abstract signatures to standardize the interfaces between the two.
 * 
 * @author Michael Wood
 */
public abstract class GraphObject 
{
	/**
     * The attribute constants.  Information is stored bitwise.
     * 000000000 > null
     * 000000001 > start state
     * 000000010 > marked state
     * 000000100 > start arrow selected
     * 000001000 > trace object
     * 000010000 > grouped
     * 000100000 > hot selected
     * 001000000 > safe grouping
     * 010000000 > simple
     */
	public static final int NULL = 0,
							START_STATE = 1,
							MARKED_STATE = 2,
							START_ARROW_SELECTED = 4,
							TRACE_OBJECT = 8,
							GROUPED = 16,
							HOT_SELECTED = 32,
							SAFE_GROUPING = 64,
							SIMPLE = 128;
	
    /**
     * Determines the attributes of the GraphObject
     */
	private int attributes = 0;

	/**
     * Class names for various GraphObjects.
     */
	public static final String NODE_CLASS = "com.aggressivesoftware.ides.graphcontrol.graphparts.Node",
							   EDGE_CLASS = "com.aggressivesoftware.ides.graphcontrol.graphparts.Edge";
	
	/**
     * The platform in which this GraphObject exists.
     */
	protected GraphingPlatform gp = null;

	/**
     * The GraphModel of which this GraphObject is a part.
     */
	protected GraphModel gm = null;		
	
	/**
     * The configuration of the object when movement began.
     */
	protected Configuration origional_configuration = null;
	
	/**
     * The label for this GraphObject
     */
	public LatexLabel latex_label = null;

	/**
     * The label for this GraphObject
     */
	public GlyphLabel glyph_label = null;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// GraphObject construction ///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	/**
     * Construct the GraphObject.
     * 
     * @param	gp			The GraphingPlatform in which this GraphObject will exist.
     * @param	gm			The GraphModel in which this GraphObject will exist.
     * @param	attributes	The attributes of the GraphObject.
     */
	public GraphObject(GraphingPlatform gp, GraphModel gm, int attributes)
	{
		this.gp = gp;
		this.gm = gm;
		this.attributes = attributes;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Miscelaneous ///////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Force this GraphObject into the given GraphModel.
	 * 
	 * @param	gm	The GraphModel of which this GraphObject should be a part.
	 */
	public void confirm(GraphModel gm)
	{
		if (this.gm != gm) { this.gm = gm; }
	}
	
	/**
	 * Get the Label that is currently being used by this Node.
	 * 
	 * @return	An instance of the implementation of Label currently being used by this Node.
	 */
	protected Label selectedLabel()
	{
		if (gp.sv.use_latex_labels) { return latex_label; }
		else { return glyph_label; }
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// attribute methods //////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	/**
     * Add an attribute to the GraphObject.
     * 
     * @param	attribute	The attribute to be added.
     */
	public void addAttribute(int attribute)
	{
		attributes = attributes | attribute;
	}

    /**
     * Remove an attribute from the GraphObject.
     * 
     * @param	attribute	The attribute to be removed.
     */
	public void removeAttribute(int attribute)
	{
		attributes = attributes & ~attribute;		
	}

    /**
     * Print the attributes of this GraphObject.
     * Some attributes are excluded from this list.
     * 
     * @return	A String representation of the attributes of this GraphObject.
     */
	public String printAttributes()
	{
		return "" + (attributes & ~GROUPED);		
	}

    /**
     * Print the all attributes of this GraphObject.
     */
	public void debugAttributes()
	{
		System.out.println("" + attributes);		
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// attribute testing //////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	/**
     * Is this the "start" node?
     * 
     * @return	true if this GraphObject has the specified attribute.
     */
	public boolean isStartState()
	{
		if ((attributes & START_STATE) > 0) { return true; }
		else { return false; }
	}
	
	/**
     * Is this a "marked" node?
     * 
     * @return	true if this GraphObject has the specified attribute.
     */
	public boolean isMarkedState()
	{
		if ((attributes & MARKED_STATE) > 0) { return true; }
		else { return false; }
	}

	/**
     * Is the arrow which identifies this node as the "start" node currently being moved?
     * 
     * @return	true if this GraphObject has the specified attribute.
     */
	public boolean isStartArrowSelected()
	{
		if ((attributes & START_ARROW_SELECTED) > 0) { return true; }
		else { return false; }
	}	

	/**
     * Is this object currently highlited in a trace animation?
     * 
     * @return	true if this GraphObject has the specified attribute.
     */
	public boolean isTraceObject()
	{
		if ((attributes & TRACE_OBJECT) > 0) { return true; }
		else { return false; }
	}	
	
	/**
     * Test if this object included in a grouping.
     * Used for copy/paste/etc.
     * 
     * @return	true if this GraphObject has the specified attribute.
     */
	public boolean isGrouped()
	{
		if ((attributes & GROUPED) > 0) { return true; }
		else { return false; }
	}
	
	/**
     * Test if this object currently the focus of some kind of attention.
     * Used in moving.
     * 
     * @return	true if this GraphObject has the specified attribute.
     */
	public boolean isHotSelected()
	{
		if ((attributes & HOT_SELECTED) > 0) { return true; }
		else { return false; }
	}
	
	/**
     * Test if this edge should be drawn by the automatic algorithms. 
     * Compared to custom configurations.
     * 
     * @return	true if this GraphObject has the specified attribute.
     */
	public boolean isSimple()
	{
		if ((attributes & SIMPLE) > 0) { return true; }
		else { return false; }
	}

	/**
     * Test if this object part of an interconnected set of nodes and edges in a grouping.
     * As opposed to some random stray edge, marked as grouped, but outside of the bounding box.
     * 
     * @return	true if this GraphObject has the specified attribute.
     */
	public boolean isSafeGrouping()
	{
		if ((attributes & SAFE_GROUPING) > 0) { return true; }
		else { return false; }
	}	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// type testing ///////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	/**
     * Test if this GraphObject is a Node.
     * 
     * @return	true if it is a Node.
     */
	public boolean isNode() { return (this.getClass().getName() == NODE_CLASS); }

	/**
     * Test if this GraphObject is a Node.
     * 
     * @return	true if it is a Node.
     */
	public boolean isEdge() { return (this.getClass().getName() == EDGE_CLASS); }	
		
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// abstract methods ///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	/**
     * Remove this GraphObject from the GraphModel, and release all its resources.
     */
	public abstract void delete();
	
	/**
     * Save the state of this GraphObject so that later updates can be relative to the state
     * prior to movement.
     * 
     * @param	origin		The origin of movement.
     * @param	attribute	An attribute to add to this GraphObject.
     * @param	state_mask	The stateMask from the initiating MouseEvent.
     */
	public abstract void initiateMovement(Point origin, int attribute, int state_mask);

	/**
     * Modify this object to represent movement from the initial state, to the state diticated
     * by the current mouse position.
     * 
     * @param	mouse	The current mouse position.
     */
	public abstract void updateMovement(Point mouse);

	/**
     * Finalize the movement of this GraphObject.  Permanently change it's state to reflect
     * the change from the initial state, and destroy the initial state.
     * 
     * @param	attribute	An attribute to be removed from this GraphObject.
     */
	public abstract void terminateMovement(int attribute);
	
	/**
     * Adjust settings of this GraphObject to take into consideration recent changes to it's label.
     */
	public abstract void accomodateLabel();

	/**
     * Displace this object and everything it controlls by the given values.
     * 
     * @param	x			Displacement in the x direction.
     * @param	y			Displacement in the y direction.
     */
	public abstract void translateAll(int x, int y);
}