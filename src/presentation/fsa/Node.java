/**
 * 
 */
package presentation.fsa;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import model.fsa.FSAState;


/**
 * The graphical representation of a state in a finite state automaton.
 * 
 * @author helen bretzke
 *
 */
public abstract class Node extends GraphElement {
	//	 the state to be represented
	FSAState state;
	GraphLabel label;
	InitialArrow initialArrow;

	/**
	 * @return the shape that visually represents this Node in a graphical display
	 */
	public abstract Shape getShape();
	
	/**
	 * This method is responsible for creating a string that contains
	 * an appropriate (depending on the type) representation of this
	 * node.
	 *  
	 * @param selectionBox The area being selected or considered
	 * @param exportType The export format
	 * @see GraphExporter#INT_EXPORT_TYPE_EPS 
	 * @see GraphExporter#INT_EXPORT_TYPE_PSTRICKS
	 * 
	 * @return String The string representation
	 * 
	 * @author Sarah-Jane Whittaker
	 */
	public abstract String createExportString(Rectangle selectionBox, int exportType);
		
	/**
	 * @return an iterator of all adjacent edges
	 */
	public abstract Iterator<Edge> adjacentEdges();
	
	/**
	 * @return bounding rectangle for union of this Node with all of its children.
	 */
	public abstract Rectangle2D adjacentBounds();
	
	public Long getId() {
		return state.getId();
	}

	public FSAState getState() {
		return state;
	}

	/**
	 * Gets the label of the node.
	 * @return the label of the node
	 */
	public GraphLabel getLabel() {
		return label;
	}

	/**
	 * Sets the location of this Node to p
	 * and adjusts the location of all adjacent edges. 
	 * @param p the new location 
	 */
	public void setLocation(Point2D p) {
		super.setLocation(p);
		recomputeEdges();		
	}

	/**
	 * Compute edge layout for each edge adjacent to this Node.
	 */
	protected void recomputeEdges() {
		Iterator<Edge> adjacent = adjacentEdges();
		while(adjacent.hasNext()){
			Edge e = adjacent.next();
				e.computeEdge();			
		}
	}

	/**
	 * @return the initial arrow for this node
	 */
	protected InitialArrow getInitialArrow() {
		return initialArrow;
	}

	/**
	 * Sets my intial arrow to the given instance and
	 * adds it as one of my child elements.  If already have an initial arrow
	 * in my set of children, removes it.
	 * @param initialArrow
	 */
	protected void setInitialArrow(InitialArrow initialArrow) {
		if(this.initialArrow != null)  
		{
			remove(this.initialArrow);
		}
		this.insert(initialArrow);
		this.initialArrow = initialArrow;
	}		
}		

