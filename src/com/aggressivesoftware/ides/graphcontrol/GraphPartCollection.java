package com.aggressivesoftware.ides.graphcontrol;


import java.util.Vector;

import org.eclipse.swt.SWT;

import com.aggressivesoftware.geometric.Box;
import com.aggressivesoftware.geometric.Point;
import com.aggressivesoftware.ides.GraphingPlatform;
import com.aggressivesoftware.ides.graphcontrol.graphparts.Edge;
import com.aggressivesoftware.ides.graphcontrol.graphparts.GraphObject;
import com.aggressivesoftware.ides.graphcontrol.graphparts.Node;

/**
 * This class keeps track of current groupings within a graph.  Items can be added or removed from groups
 * individually or on mass with the selection tools.  This is mostly used for copy/paste/delete/movement.
 * 
 * @author Michael Wood
 */
public class GraphPartCollection 
{
	/**
     * The platform in which this GraphPartCollection will exist.
     */
	private GraphingPlatform gp = null;

	/**
     * The parts list.
     */
	private Vector parts = null;

	/**
     * Records action in time period between mouse-down and mouse-up.
     * Hence if there is a double-click action, it can be informed of what was accomplished in the down event.
     * The up event clears this variable
     */
	public boolean new_last_grabbed_object = false;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// GraphPartCollection construction ///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * Construct the GraphPartCollection.
     * 
     * @param	gp	The platform in which this SelectionArea will exist.
     */
	public GraphPartCollection(GraphingPlatform gp)
	{	
		this.gp = gp;
		parts = new Vector();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// private actions on the collection //////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * If the given part is not in the collection then add it. Else remove it.
     * Adding and removal should update the objects "grouped" attribute
     * Only a doubleclicked item should have the "live text" attribute, hence we must remove it from any existing.
     * Note: double clicking can never be part of a ctrl grouping
     * 
     * @param	object	The GraphObject to be added or removed from the collection
     * @return	The last object added to the collection or null if empty.
     */
	private GraphObject updateTheCollection(GraphObject object)
	{
		if (parts.contains(object))
		{
			// remove the object and return the last inserted object or null
			object.removeAttribute(GraphObject.GROUPED);
			parts.remove(object);
			if (parts.size() > 0)
			{
				return (GraphObject)parts.lastElement();
			}
			else
			{
				return null;
			}
		}
		else
		{
			// add the object and return it
			object.addAttribute(GraphObject.GROUPED);
			parts.add(object);
			return object;
		}
	}
	
	/**
     * Remove the grouped attribute from all graphobjects and empty the collection
     */
	private void emptyTheCollection()
	{
		while (parts.size() > 0)
		{
			((GraphObject)parts.elementAt(0)).removeAttribute(GraphObject.GROUPED);
			parts.removeElementAt(0);
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// public actions on the collection ///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
    /**
     * If ctrl is pressed, then the grouped_parts should be updated,
     * Else the grouped_parts should be nulled,
     * Either way, the last_grabbed_object should be updated.
     * The live_text attribute is only received on a double_click.
     * 
     * @param	state_mask	The stateMask from the initiating MouseEvent.
     * @param	graph_part	The associated GraphObject.
     */
	public void updateGroup(int state_mask, GraphObject graph_part)
	{
		if ((state_mask & SWT.CTRL) == SWT.CTRL) { addToGrouping(graph_part); }
		else
		{
			abandonGroupHistory();
			parts = new Vector();
			updateTheCollection(graph_part);
			new_last_grabbed_object = true;
		}
		gp.mc.edit_copy.enable();
		gp.mc.edit_delete.enable();
	}

    /**
     * Add a graph object to the grouping.
     * 
     * @param	graph_part	The GraphObject to be added to the grouping.
     */
	public void addToGrouping(GraphObject graph_part)
	{
		if (parts != null) { updateTheCollection(graph_part); }
		else
		{
			parts = new Vector();
			updateTheCollection(graph_part);
			// note: [new_last_grabbed_object] doesn't care about the grouping actions.
			gp.mc.edit_copy.enable();
			gp.mc.edit_delete.enable();
		}		
	}

    /**
     * Remove the specified object from the collection without modifying it.
     * Update the last_grabbed_object to the object at the top of the list new list.
     * This is for removal of dead objects, so their attributes don't matter.
     * 
     * @param	object	The GraphObject to be removed from the collection
     */
	public void removeFromCollection(GraphObject object)
	{
		if (parts != null)
		{
			parts.remove(object);
			if (parts.size() == 0) 
			{
				gp.mc.edit_copy.disable();
				gp.mc.edit_delete.disable();
			}
		}
	}	
	
    /**
     * Forget any groupings.
     */
	public void abandonGroupHistory() 
	{
		if (parts != null) 
		{ 
			emptyTheCollection(); 
			parts = null;
		}
		gp.mc.edit_copy.disable();
		gp.mc.edit_delete.disable();
		gp.gc.group_area.setVisible(false);
	}		
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// queries on the collection //////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////				
	
	/**
     * Used to uniquely identify the edge or node that was last grabbed by the point tool.
     * Hence the top of the parts list.
     */
	public GraphObject lastGrabbedObject()
	{
		if (parts == null) { return null; }
		else { return (GraphObject)parts.lastElement(); }
	}
	
	/**
	 * Calculate a bounding area for this grouping
	 * Note: similar code is used in Node.newClone and EditBuffer.pasteCollection
	 *       this code is not reused in those places for computational efficiency.
	 */	
	public Box getBoundingArea()
	{
		int x1=0, y1=0, x2=0, y2=0;
		GraphObject part = null;
		Node n = null;
		if (parts != null) 
		{
			for (int i=0; i<parts.size(); i++)
			{ 
				part = (GraphObject)parts.elementAt(i); 
				if (part.isNode()) 
				{
					n = (Node)part;
					if (x1 == x2) 
					{ 
						// first node
						x1 = n.x()-n.r();
						y1 = n.y()-n.r();
						x2 = n.x()+n.r();
						y2 = n.y()+n.r();
					}
					else
					{
						// grow
						if (n.x()-n.r() < x1) { x1 = n.x()-n.r(); } 
						if (n.y()-n.r() < y1) { y1 = n.y()-n.r(); } 
						if (n.x()+n.r() > x2) { x2 = n.x()+n.r(); } 
						if (n.y()+n.r() > y2) { y2 = n.y()+n.r(); } 
					}
				}
			}
		}
		return new Box(x1-SelectionArea.PADDING,y1-SelectionArea.PADDING,x2+SelectionArea.PADDING,y2+SelectionArea.PADDING);
	}		
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// actions on the items in the collection /////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	/**
     * Delete all graphobjects and empty the collection
     */
	public void deleteGroup()
	{
		if (parts != null) 
		{ 
			while (parts.size() > 0)
			{
				((GraphObject)parts.elementAt(0)).delete();
				// this action of deleting a GraphObject, automatically removes it from this list.
			}
			parts = null; 
		}
		gp.gc.io.markUnsavedChanges();
		gp.gc.j2dcanvas.repaint();
		gp.mc.edit_copy.disable();
		gp.mc.edit_delete.disable();
	}
			
	/**
	 * Translate all variables.
	 * 
	 * @param	x	Translation in the x direction.
	 * @param	y	Translation in the y direction.
	 */	
	public void translateAll(int x, int y, boolean regardless)
	{
		if (parts != null) 
		{ 
			GraphObject part = null;
			for (int i=0; i<parts.size(); i++)
			{ 
				part = (GraphObject)parts.elementAt(i);
				if (part.isSafeGrouping() || regardless) { part.translateAll(x,y); }
			}
		}
	}	

	/**
	 * Snap all nodes to grid.
	 */	
	public void snapToGrid()
	{
		if (parts != null) 
		{ 
			GraphObject part = null;
			Node n = null;
			for (int i=0; i<parts.size(); i++)
			{ 
				part = (GraphObject)parts.elementAt(i);
				if (part.isNode()) 
				{
					n = (Node)part;
					n.initiateMovement(n.origin(),GraphObject.NULL,0);
					n.updateMovement(n.origin());
					n.terminateMovement(GraphObject.NULL);
				}
			}
			gp.gc.group_area.highlite(getBoundingArea());
		}
	}	
		
	/**
	 * Reset configuration of all selected edges.
	 */	
	public void resetConfiguration()
	{
		if (parts != null) 
		{ 
			GraphObject part = null;
			Edge e = null;
			for (int i=0; i<parts.size(); i++)
			{ 
				part = (GraphObject)parts.elementAt(i);
				if (part.isEdge())
				{
					e = (Edge)part;
					e.addAttribute(GraphObject.SIMPLE);
					e.autoConfigureCurve();
					e.accomodateLabel();
				}
			}
		}
	}	
	
	/**
	 * Call exclusive intiateMovement on all Nodes in the group, so that their edges outside the group can be updated
	 * 
	 * @param	mouse	The co-ordinates of the mouse initiating movement.
	 */	
	public void initiateNodeMovement(Point mouse)
	{
		if (parts != null) 
		{ 
			GraphObject part = null;
			Edge e = null;
			Node n = null;

			for (int i=0; i<parts.size(); i++)
			{ 
				part = (GraphObject)parts.elementAt(i); 
				if (part.isNode())
				{ 
					n = (Node)part;
					n.initiateMovement(mouse,GraphObject.HOT_SELECTED,true); 
					n.addAttribute(GraphObject.SAFE_GROUPING);
					// by definition, all nodes will be within the bounding area
				}
			}
			
			// also, mark all selected edges as SAFE_GROUPING if both of their nodes are grouped
			// this is necessary because a user can explicitly add edges to a grouping, that are outside the bounding box
			// and hence shouldn't be modified with the translate all action
			for (int i=0; i<parts.size(); i++)
			{ 
				part = (GraphObject)parts.elementAt(i); 
				if (part.isEdge())
				{ 
					e = (Edge)part; 
					if (e.n1.isGrouped() && e.n2.isGrouped()) { e.addAttribute(GraphObject.SAFE_GROUPING); }
					else { e.removeAttribute(GraphObject.SAFE_GROUPING); }
				}
			}
		}
	}		

	/**
	 * Call exclusive updateMovement on all Nodes in the group, so that their edges outside the group can be updated
	 * 
	 * @param	mouse	The current co-ordinates of the mouse.
	 */	
	public void updateNodeMovement(Point mouse)
	{
		if (parts != null) 
		{ 
			GraphObject part = null;
			for (int i=0; i<parts.size(); i++)
			{ 
				part = (GraphObject)parts.elementAt(i); 
				if (part.isNode())
				{ ((Node)part).updateMovement(mouse,true); }
			}
		}
	}			
	
	/**
	 * Call exclusive terminateMovement on all Nodes in the group, so that their edges outside the group can be updated
	 */	
	public void terminateNodeMovement()
	{
		if (parts != null) 
		{ 
			GraphObject part = null;
			for (int i=0; i<parts.size(); i++)
			{ 
				part = (GraphObject)parts.elementAt(i); 
				if (part.isNode())
				{ ((Node)part).terminateMovement(GraphObject.HOT_SELECTED,true); }
			}
		}
	}		

	/**
	 * Create a new list of cloned nodes.
	 */	
	public void cloneCollection(Vector node_list, Vector edge_list)
	{
		if (parts != null) 
		{ 
			GraphObject part = null;
			// clone the nodes
			for (int i=0; i<parts.size(); i++)
			{ 
				part = (GraphObject)parts.elementAt(i); 
				if (part.isNode()) { node_list.add(((Node)part).newClone()); }
				// this causes all origional nodes to have valid pointers to their newest clones
			}
			// clone the edges
			Edge e = null;
			for (int i=0; i<parts.size(); i++)
			{ 
				part = (GraphObject)parts.elementAt(i); 
				if (part.isEdge()) 
				{ 
					// note, edges without both nodes in the grouping will be weeded out by this process.
					e = ((Edge)part).newClone();
					if (e != null) { edge_list.add(e); }
				}
				// the edge can do this because it's associated nodes have valid pointers to their newest clones
			}
			// clean up
			for (int i=0; i<parts.size(); i++)
			{ 
				part = (GraphObject)parts.elementAt(i); 
				if (part.isNode()) { ((Node)part).last_clone = null; }
			}
		}
	}		
		
}
