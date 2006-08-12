/**
 * 
 */
package presentation.fsa;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import model.fsa.FSAState;


/**
 * @author helen bretzke
 *
 */
public abstract class Node extends GraphElement {
	//	 the state to be represented
	FSAState state;
	GraphLabel label;

	public abstract String createExportString(Rectangle selectionBox, int exportType);

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

	
		
}
