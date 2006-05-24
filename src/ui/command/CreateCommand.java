package ui.command;

import java.awt.Point;
import java.awt.geom.Point2D.Float;

import org.pietschy.command.ActionCommand;

import presentation.fsa.GraphElement;
import ui.GraphDrawingView;

public class CreateCommand extends ActionCommand {

	private GraphDrawingView context;
	private int elementType;
	private Point location;
	
	/**
	 * Types of elements to be created.
	 */
	public static final int NODE = 0;
	public static final int EDGE = 1;
	public static final int NODE_AND_EDGE = 2;
	
	public CreateCommand(GraphDrawingView context, int elementType, Point location){
		this.context = context;
		this.elementType = elementType;
		this.location = location;
	}
	
	public void handleExecute() {
		// only after element has been created and added, add this event to the command history
		// NOTE: this must be a reversible command to be entered in the history
		switch(elementType){
		case NODE:
			context.getGraphModel().addNode(new Float(location.x, location.y));
			break;
		case NODE_AND_EDGE:
			// need a node and a point			
			break;
		case EDGE:
			// need a pair of nodes
			break;
		default:
				
		}		
	}
}
