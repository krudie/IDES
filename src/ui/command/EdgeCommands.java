package ui.command;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoableEdit;
import main.Hub;
import main.Workspace;
import org.pietschy.command.ActionCommand;
import org.pietschy.command.undo.UndoableActionCommand;
import presentation.fsa.BezierEdge;
import presentation.fsa.BezierLayout;
import presentation.fsa.CircleNode;
import presentation.fsa.Edge;
import presentation.fsa.EdgeLabellingDialog;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.InitialArrow;
import presentation.fsa.ReflexiveEdge;
import presentation.fsa.ReflexiveLayout;
import presentation.fsa.SelectionGroup;
import presentation.Geometry;
import presentation.GraphicalLayout;

/**
 * This class holds static commands that can take actions over edges.
 * All the actions that can be undone, follows the "Swing way" and the "Command"
 * Design Pattern.
 * When the user wants to execute an action (e.g.: by clicking a button),
 * an <code>AbstractAction</code> encapsulates an UndoableAction that knows how to undo/redo 
 * the action. The abstract action also reports a manager about an undoable action everytime
 * such an action is taken.
 * 
 * So everytime an action that can be undone is executed, two steps follows the user's request:
 * 1- One <code>AbstractAction</code> executes an <action>UndoableAction</action> that can 
 * redo\/undo the the desired action.
 * 2- A code inside the AbstractAction, notifies the UndoManager in the CommandManager about 
 * a performed undoable action.
 * 
 * One of the reasons for making an UndoableAction be called by an 
 * AbstractAction (instead of making the action be an AbstractAction AND an UndoableAction) is 
 * the fact that by doing this, one AbstractAction could encapsulate several UndoableActions
 * generating a "composite" UndoableAction. So according to the chosen design, every UndoableAction
 * should, in fact, be atomic, so that big undoable actions can be made by composing smaller 
 * UndobleActions.
 * 
 * It is simpler (in my opinion (Christian)), to have always the job done by two simple classes (one to
 * instanciate an UndoableAction and update to the CommandManager about the action, and other 
 * being UndoableAction itself rather than having one classe inheriting AbstractAction and extending UndoableCommand.
 * Having everything in just one class would make this class be too big, more difficult to write
 * and also, less usable. 
 *
 * @author Christian Silvano
 *
 */
public class EdgeCommands {
	
	
	//TODO make this action undoable according to what was done for the other actions, e.g.: SymmetrizeEdge()
	//In order to do that, the AbstractAction bellow should instantiate an UndoableAction sending all the references
	//needed, so the UndoableAction can undo/redo the action everytime it is requested by the command manager.
	//CreateEvent should also, after instanciating the UndoableAction, send a reference of this action to the CommandManager
	//using the method undoSupport->postEdit(UndoableAction) inside the CommandManager.
	public static class CreateEventCommand extends AbstractAction {
		private static final ImageIcon icon = new ImageIcon();
		private static final String name = "Label with events";
		
		private GraphDrawingView view;
		private Edge edge;
		
		public CreateEventCommand(GraphDrawingView view,Edge edge){
			super(name,icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/machine_alpha.gif")));
			this.edge = edge;
			this.view = view;
		}
		
		public void actionPerformed(ActionEvent evt) {
			if(edge != null & view != null)
			{
				EdgeLabellingDialog.showDialog(view, edge);
			}
		}
		
	}
	
	//TODO make this action undoable according to what was done for the other actions, e.g.: SymmetrizeEdge()
	//In order to do that, the AbstractAction bellow should instantiate an UndoableAction sending all the references
	//needed, so the UndoableAction can undo/redo the action everytime it is requested by the command manager.
	//CreateEvent should also, after instanciating the UndoableAction, send a reference of this action to the CommandManager
	//using the method undoSupport->postEdit(UndoableAction) inside the CommandManager.
	public static class RemoveEventCommand extends ActionCommand {
		
		public RemoveEventCommand(){
			super("event.remove.command");
		}
		
		@Override
		protected void handleExecute() {
			// TODO Auto-generated method stub
			System.out.println("Remove an event from local alphabet (leave it in the global alphabet).");
		}
		
	}
	
	public static class PruneEventsCommand extends ActionCommand {
		
		public PruneEventsCommand(){
			super("event.prune.command");
		}
		
		@Override
		protected void handleExecute() {
			// TODO Auto-generated method stub
			System.out.println("Remove all events from global alphabet that don't exist in any local alphabet in the workspace.");
		}
		
	}
	
	/**
	 * This method is executed every-time one of the Edge's control points is edited.
	 * @author christiansilvano
	 *
	 */
	public static class ModifyEdgeAction extends AbstractAction {
		
		private Edge edge;
		private GraphicalLayout previousLayout;
		private GraphDrawingView view;
		public ModifyEdgeAction(){
			super("modify.edge.command");
		}
		
		/**
		 * Class constructor.
		 * @param view a reference to the GraphDrawingView displaying the layouts of interest
		 * @param edge a reference to the edge of interest
		 * @param previousLayout a copy of the layout before the modification
		 */
		public ModifyEdgeAction(GraphDrawingView view, Edge edge, GraphicalLayout previousLayout) {
			setEdge(edge);
			setPreviousLayout(previousLayout);
			this.view = view;
		}
		
		//Sets the edge of interest
		public void setEdge(Edge edge){
			this.edge = edge;
		}
		
		//Sets the backup for the Layout of edge 
		public void setPreviousLayout(GraphicalLayout layout){
			this.previousLayout = layout;
		}
		
		//Some classes that uses this Action, calls the execute() method. That is legacy from
		//when IDES used the GUICommands library.
		public void execute()
		{
			actionPerformed(null);
		}
		
		/**Instantiate the UndoableAction of interest, sending references to the Edge, the GraphDrawingView
		 ** and the backup for the layout of the edge.
		 ** Reports the UndoableAction to the CommandManager.
		 **/
		public void actionPerformed(ActionEvent evt) {
			UndoableModifyEdge action = new UndoableModifyEdge(view, edge,previousLayout);
			//perform action
			action.redo();
			// notify the listeners
			CommandManager_new.getInstance().undoSupport.postEdit(action);	
		}		
	}
	/**
	 * This class is an UndoableAction responsible for swaping layouts of an edge, the layouts
	 * means the state of the edge before and after an edition.
	 * This class makes an edition over the layout of an edge, be actually undoable.
	 * 
	 * @see javax.swing.undo.AbstractUndoableEdit 
	 * @author christiansilvano
	 */
	private static class UndoableModifyEdge extends AbstractUndoableEdit {
		Edge edge;
		GraphDrawingView view; 
		GraphicalLayout previousLayout, editedLayout;
		public UndoableModifyEdge(GraphDrawingView view, Edge edge, GraphicalLayout previousLayout) {
			this.view = view;
			this.edge = edge;
			this.previousLayout = previousLayout;
			if(edge.getSourceNode() == null & edge.getTargetNode() != null)
			{
				//This is an initial edge!
				editedLayout = new GraphicalLayout(((InitialArrow)edge).getDirection());
			}else{
				Point2D.Float[] bezierControls = new Point2D.Float[4];
				BezierLayout layout = (BezierLayout)edge.getLayout();
				bezierControls[0] = (Point2D.Float)((BezierLayout)layout).getCurve().getP1();
				bezierControls[1] = (Point2D.Float)((BezierLayout)layout).getCurve().getCtrlP1();
				bezierControls[2] = (Point2D.Float)((BezierLayout)layout).getCurve().getCtrlP2();
				bezierControls[3] = (Point2D.Float)((BezierLayout)layout).getCurve().getP2();
				editedLayout = new BezierLayout(bezierControls);
				//Backup the layout:
				if(!edge.getSourceNode().equals(edge.getTargetNode()))
				{
					//This is a regular edge, neither a reflexive edge nor an initial edge.
					((BezierLayout)editedLayout).setEdge((BezierEdge)edge);
				}else if(edge.getSourceNode().equals(edge.getTargetNode()))
				{
					//This is a reflexive edge!
					editedLayout = new ReflexiveLayout(edge.getSourceNode(), (ReflexiveEdge)edge, (BezierLayout)editedLayout);
					((ReflexiveLayout)editedLayout).setEdge((ReflexiveEdge)edge);
				}
			}
		}
		
		//Swaps the layout of the edge.
		public void undo() throws CannotRedoException {
			edge.setLayout(previousLayout);
			edge.getLayout().setDirty(true);
			view.repaint();
		}
		
		//Swaps the layout of the edge.
		public void redo() throws CannotRedoException {
			edge.setLayout(editedLayout);
			edge.getLayout().setDirty(true);
			view.repaint();
		}
		
		public boolean canUndo() {
			return true;
		}
		
		public boolean canRedo() {
			return true;
		}
		
		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			return Hub.string("modifyEdge");
		}
		
	}
	
	
	/**
	 * If this edge is not straight,  make it have a symmetrical appearance.
	 * Make the two vectors - from P1 to CTRL1 and from P2 to CTRL2, be of 
	 * the same length and have the same angle. So the edge will look it has 
	 * a symmetrical curve. 
	 * There are two cases:
	 * The 2 control points are on the same side of the curve (a curve with the 
	 * form of a bow); and the 2 control points are on different sides of the 
	 * edge (a curve like a wave). In one of the cases, theangles of the vectors
	 * should be A=B, in the other A=-B.
	 *
	 *The command is undoable, so the following AbstractAction encapsulates 
	 *an UndoableEdit. The SymmetrizeEdgeAction also reports UndoableEdit objects
	 *to the CommandManager. 
	 */
	public static class SymmetrizeEdgeAction extends AbstractAction
	{
		//Icon and label are here so the SymmetrizeAction can be simply added to buttons
		//if desired. The buttons should inherit the label and Icon, making a nice visual
		//for the user.
		private static final ImageIcon icon = new ImageIcon();
		private static final String name = Hub.string("symmetrize");
		
		//Class attribute, a copy of the layout for the edge, so Undoable objects can be constructed,
		//and use this layout information to undo/redo the symmetrization
		private BezierLayout layout;
		
		//The view of the Graph of interest.
		private GraphDrawingView view;
		//A reference to the Edge of interest.
		private Edge edge;
		
		//Default constructor. It gets the references for the edge and graph of interest.
		public SymmetrizeEdgeAction(GraphDrawingView view,Edge edge){
			super(name,icon);
			this.edge = edge;
			this.view = view;
		}
		
		//Performs the action.
		//The same as any other action that can be undone in IDES. Instantiates a new UndoableAction passing
		//any needed data so it can undo/redo itself makes the UndoableAction execute a redo and finally
		//tells the CommandManager about a new UndoableAction.
		public void actionPerformed(ActionEvent evt){
			BezierLayout l = (BezierLayout)edge.getLayout();
			UndoableSymmetrizeEdge action = new UndoableSymmetrizeEdge(view,l);
			//Perform the operation
			action.redo();
			// notify the listeners
			CommandManager_new.getInstance().undoSupport.postEdit(action);
		}
	}
	
	/**
	 * Undoable Action related to the command Symmetrize Edge.
	 * Swaps the layout of the edge, making it go either to the "original" layout, or to the modified on
	 * as the CommandManager queries undo or redo.
	 * @author christiansilvano
	 *
	 */
	private static class UndoableSymmetrizeEdge extends AbstractUndoableEdit{
		//A backup of the "original" layout of the edge. 
		BezierLayout backupLayout;
		//The Graph of interest
		GraphDrawingView view;
		//The edge of interest
		BezierEdge edge;
		
		/**
		 * Default constructor
		 * @param view the Graph of interest
		 * @param layout a reference for the "original" layout of the edge
		 */
		public UndoableSymmetrizeEdge(GraphDrawingView view, BezierLayout layout)
		{
			edge = layout.getEdge();
			
			//Backing up the original edge, so the action can be undone as many times as it is 
			//necessary.
			Point2D.Float[] bezierControls = new Point2D.Float[4];
			bezierControls[0] = (Point2D.Float)((BezierLayout)layout).getCurve().getP1();
			bezierControls[1] = (Point2D.Float)((BezierLayout)layout).getCurve().getCtrlP1();
			bezierControls[2] = (Point2D.Float)((BezierLayout)layout).getCurve().getCtrlP2();
			bezierControls[3] = (Point2D.Float)((BezierLayout)layout).getCurve().getP2();
			
			//Making sure backupLayout is an exact copy of the Edge's original layout.
			backupLayout = new BezierLayout(bezierControls);
			backupLayout.setEdge(edge);
			this.view = view;
		}
		
		
		// Restores the original layout by applying the backup of the original layout
		// to the edge
		public void undo() throws CannotRedoException{
			edge.setLayout(backupLayout);
			backupLayout.setDirty(true);
			view.repaint();
		}
		
		//Symmetrize the edge.
		// If this edge is not straight,  make it have a symmetrical appearance.
		// Make the two vectors - from P1 to CTRL1 and from P2 to CTRL2, be of 
		// the same length and have the same angle. So the edge will look it has 
		// a symmetrical curve. 
		// There are two cases:
		// The 2 control points are on the same side of the curve (a curve with the 
		// form of a bow); and the 2 control points are on different sides of the 
		// edge (a curve like a wave). In one of the cases, theangles of the vectors
		// should be A=B, in the other A=-B.
		public void redo() throws CannotRedoException{
			//Performs the symmetrization over the original curve.
			Point2D.Float[] points=new Point2D.Float[4];
			points[0]=Geometry.translate(backupLayout.getCurve().getP1(),-backupLayout.getCurve().getX1(),-backupLayout.getCurve().getY1());
			points[1]=Geometry.translate(backupLayout.getCurve().getCtrlP1(),-backupLayout.getCurve().getX1(),-backupLayout.getCurve().getY1());
			points[2]=Geometry.translate(backupLayout.getCurve().getCtrlP2(),-backupLayout.getCurve().getX1(),-backupLayout.getCurve().getY1());
			points[3]=Geometry.translate(backupLayout.getCurve().getP2(),-backupLayout.getCurve().getX1(),-backupLayout.getCurve().getY1());
			
			float edgeAngle=(float)Math.atan(Geometry.slope(backupLayout.getCurve().getP1(),backupLayout.getCurve().getP2()));
			points[0]=Geometry.rotate(points[0],-edgeAngle);
			points[1]=Geometry.rotate(points[1],-edgeAngle);
			points[2]=Geometry.rotate(points[2],-edgeAngle);
			points[3]=Geometry.rotate(points[3],-edgeAngle);
			
			double quadrantFix1=(points[0].x-points[1].x>0)?Math.PI:0;
			double quadrantFix2=(points[2].x-points[3].x>0)?Math.PI:0;
			
			float a1=(float)Math.atan(Geometry.slope(points[0],points[1]));
			float a2=(float)Math.atan(Geometry.slope(points[3],points[2]));
			float angle=(float)(Math.abs(a1)+Math.abs(a2))/2F;
			float distance=(float)(points[0].distance(points[1])+points[2].distance(points[3]))/2F;
			
			points[1]=Geometry.rotate(new Point2D.Float(distance,0),(angle*Math.signum(a1)+quadrantFix1));
			points[2]=Geometry.rotate(new Point2D.Float(distance,0),(angle*Math.signum(a2)+quadrantFix2+Math.PI));
			points[2].x+=points[3].x;
			points[2].y+=points[3].y;
			
			a1=(float)Math.atan(Geometry.slope(points[0],points[1]));
			a2=(float)Math.atan(Geometry.slope(points[3],points[2]));
			
			points[1]=Geometry.rotate(points[1],edgeAngle);
			points[2]=Geometry.rotate(points[2],edgeAngle);
			points[0]=new Point2D.Float((float)backupLayout.getCurve().getX1(), (float)backupLayout.getCurve().getY1());
			points[1]=Geometry.translate(points[1],backupLayout.getCurve().getX1(),backupLayout.getCurve().getY1());
			points[2]=Geometry.translate(points[2],backupLayout.getCurve().getX1(),backupLayout.getCurve().getY1());
			points[3]=new Point2D.Float((float)backupLayout.getCurve().getX2(), (float)backupLayout.getCurve().getY2());
			
			//Apply a new layout to the edge
			edge.setLayout(new BezierLayout(points));
			//Make the new layout have a reference to its edge.
			((BezierLayout)edge.getLayout()).setEdge(edge);
			//Refresh the graph
			view.repaint();
		}
		
		public boolean canUndo()
		{
			return true;
		}
		
		public boolean canRedo()
		{
			return true;
		}
		
		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName()
		{
			return Hub.string("symmetrizeEdge");
		}
		
	}
	
}

