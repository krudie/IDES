package ui.command;

import java.awt.Point;
import java.awt.geom.Point2D.Float;

import org.pietschy.command.ActionCommand;

import presentation.fsa.GraphElement;
import presentation.fsa.Node;
import ui.GraphDrawingView;

public class CreateCommand extends ActionCommand {

	private GraphDrawingView context;
	private int elementType;
	private Node source, target;
	private Point location;
	
	/**
	 * Types of elements to be created.
	 */
	public static final int UNKNOWN = -1;
	public static final int NODE = 0;
	public static final int EDGE = 1;
	public static final int NODE_AND_EDGE = 2;
	
	/**
	 * Default constructor.
	 */
	public CreateCommand(){		
		super("create.command");
		elementType = UNKNOWN;
	}
	
	public CreateCommand(GraphDrawingView context){
		super("create.command");
		setContext(context, UNKNOWN, null);
	}
	
	/**
	 * @param context
	 * @param elementType
	 * @param location
	 */
	public CreateCommand(GraphDrawingView context, int elementType, Point location){
		setContext(context, elementType, location);
	}	
	
	public void setContext(GraphDrawingView context,  int elementType, Point location){
		this.context = context;
		this.elementType = elementType;
		this.location = location;
	}
	
	public void setSourceNode(Node s){
		source = s;
	}
	
	public void setTargetNode(Node t){
		target = t;
	}
	
	@Override
	protected void handleExecute() {		
		// Only AFTER element has been created and added, add this event to the command history
		// NOTE: this must be a reversible command to be entered in the history
		switch(elementType){
		case NODE:
			context.getGraphModel().addNode(new Float(location.x, location.y));
			break;
		case NODE_AND_EDGE:
			context.getGraphModel().addEdgeAndNode(source, new Float(location.x, location.y));			
			break;
		case EDGE:
			context.getGraphModel().addEdge(source, target);
			break;
		default:
			// TODO set the tool in the *currently active* drawing view
			// set the current drawing tool to the CreationTool
			 context.setTool(GraphDrawingView.CREATE);
		}		
	}
}
