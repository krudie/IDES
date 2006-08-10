/**
 * 
 */
package presentation.fsa;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D.Float;

/**
 * @author helen
 *
 */
public class InitialArrow extends Edge {

	private ArrowHead arrowHead = new ArrowHead();
	private GeneralPath shaft = new GeneralPath();
	
	// What should the layout and handler look like for this class?
	
	/**
	 * Initial node pointing to the given target node with null source node. 
	 * 
	 * @param target
	 */
	public InitialArrow(Node target) {
		super(null, target);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#createExportString(java.awt.Rectangle, int)
	 */
	@Override
	public String createExportString(Rectangle selectionBox, int exportType) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#intersectionWithBoundary(presentation.fsa.Node)
	 */
	@Override
	public Float intersectionWithBoundary(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#computeEdge(presentation.fsa.Node, presentation.fsa.Node)
	 */
	@Override
	public void computeEdge() {
				
	}

}
